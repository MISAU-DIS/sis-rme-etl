package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.PreparedQueryInfo;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.SQLUtilities;

/**
 * Transformer responsible for retrieving the UUID of an existing destination
 * record or generating a new UUID when no matching record is found.
 *
 * <p>
 * This transformer searches the destination table using a configured lookup
 * condition. If a record matching the condition exists, the UUID of that record
 * is returned. If no matching record is found, a new UUID is generated and
 * returned.
 * </p>
 *
 * <p>
 * This transformer is useful when multiple records may need to reference or
 * reuse the same logical destination entity, even when that entity has not yet
 * been created at the time of transformation.
 * </p>
 *
 * <p>
 * Transformer syntax:
 * </p>
 *
 * <pre>
 * UUID_ON_DEMAND_TRANSFORMER(
 *     table:tableName,
 *     lookup_condition:condition
 * )
 * </pre>
 *
 * <p>
 * Parameters:
 * </p>
 * <ul>
 * <li><b>table:tableName</b> – Destination table where an existing record
 * should be searched.</li>
 * <li><b>lookup_condition:condition</b> – Condition used to search for an
 * existing destination record. If a record matching this condition is found,
 * its UUID is returned.</li>
 * </ul>
 *
 * <p>
 * Behavior:
 * </p>
 * <ol>
 * <li>Evaluate the lookup condition against the destination table.</li>
 * <li>If a matching record exists, return its UUID.</li>
 * <li>If no matching record exists, generate and return a new UUID.</li>
 * </ol>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * UUID_ON_DEMAND_TRANSFORMER(
 *     table:encounter,
 *     lookup_condition:encounter_type=${aliases_prefix}_encounter_dst_ds.encounter_type
 *         and patient_id=${patient_id}
 *         and encounter_datetime=${obs_datetime}
 * )
 * </pre>
 *
 * <p>
 * In this example, the transformer searches for an existing encounter for the
 * given patient and encounter date. If one exists, its UUID is reused.
 * Otherwise, a new UUID is generated.
 * </p>
 */
public class UuidOnDemanTransformer extends AbstractEtlFieldTransformer {
	protected final Object lock = new Object();

	protected static final Map<String, UuidOnDemanTransformer> INSTANCES = new ConcurrentHashMap<>();

	private String tableName;
	private String tableAlias;

	private String onDemandCheckCondition;

	private TableConfiguration tableConf;

	private SrcConf defaultSrcConf;

	private Map<String, Object> dynamicElements;

	public UuidOnDemanTransformer(List<Object> parameters, DstConf relatedEtlTransformTarget, TransformableField field,
			Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {

		super(parameters, relatedEtlTransformTarget, field);

		init(conn);
	}

	public void init(Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {

		if (parameters == null || parameters.size() < 2) {
			throw new ForbiddenOperationException("Error initializing transformer: " + this
					+ "\nA UuidOnDemanTransformer needs at least 2 parameters.\n"
					+ "UuidOnDemanTransformer(table:table_name,lookup_condition:sql_condition)");
		}

		this.dynamicElements = new HashMap<>();

		for (Object fieldData : this.getParameters()) {
			String[] mapping = fieldData.toString().split(":", 2);

			if (mapping.length != 2) {
				throw new EtlExceptionImpl(
						"Wrong format for Param (" + fieldData + ") within the " + getTransformerDsc() + "\n"
								+ "Each object param must be specified as filedName:srcFieldOrValue");
			} else {
				String dstField = mapping[0];
				String srcFieldOrValue = mapping[1];

				if (dstField.equals("table_name")) {
					if (!utilities.stringHasValue(srcFieldOrValue)) {
						throw new ForbiddenOperationException("The table_name field has no value");
					}

					this.tableName = srcFieldOrValue;
				} else if (dstField.equals("table_alias")) {
					if (!utilities.stringHasValue(srcFieldOrValue)) {
						throw new ForbiddenOperationException("The table_alias field has no value");
					}

					this.tableAlias = srcFieldOrValue;
				} else if (dstField.equals("lookup_condition")) {
					if (!utilities.stringHasValue(srcFieldOrValue)) {
						throw new ForbiddenOperationException("The lookup_condition has no value");
					}

					this.onDemandCheckCondition = srcFieldOrValue;
				}

				else {
					if (!utilities.stringHasValue(srcFieldOrValue) || srcFieldOrValue.toLowerCase().equals("null")) {
						srcFieldOrValue = null;
					}

					this.dynamicElements.put(dstField, srcFieldOrValue);
				}
			}
		}

		if (!utilities.stringHasValue(this.onDemandCheckCondition) || !utilities.stringHasValue(this.tableName)) {
			throw new ForbiddenOperationException(
					"At least 'table' and 'lookup_condition' parameters must be specified");
		}

		if (this.tableAlias != null) {
			this.dynamicElements.put("table_alias", this.tableAlias);
		}

		Map<String, Object> params = relatedEtlTransformTarget.retrieveAllAvailableTemplateParameters();

		if (params != null && !params.isEmpty()) {
			for (Entry<String, Object> p : params.entrySet()) {
				this.dynamicElements.put(p.getKey(), p.getValue());
			}
		}

		this.tryToLoadDumpScriptContentToFieldAndValidate("onDemandCheckCondition", this.dynamicElements, conn);
	}

	@Override
	public DstConf getRelatedEtlTransformTarget() {
		return (DstConf) super.getRelatedEtlTransformTarget();
	}

	public static UuidOnDemanTransformer getInstance(List<Object> parameters,
			EtlTransformTarget relatedEtlTransformTarget, TransformableField field, Connection conn) {

		String key = buildCacheKey(relatedEtlTransformTarget, field, parameters);

		return INSTANCES.computeIfAbsent(key, k -> {
			try {
				return new UuidOnDemanTransformer(parameters, (DstConf) relatedEtlTransformTarget, field, conn);
			} catch (DBException e) {
				throw new EtlExceptionImpl(e);
			}
		});
	}

	public String getOnDemandCheckCondition() {
		return onDemandCheckCondition;
	}

	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			Connection srcConn, Connection dstConn) throws DBException, EtlTransformationException {

		try {

			stepIntoBreakpoint(getRelatedEtlConf(),
					transformedRecord.getRelatedConfiguration().getObjectName().equals("orders"));

			String uuid = this.retrieveExistingOnDemandUuid(processor, srcObject, additionalSrcObjects, srcConn,
					dstConn);

			if (uuid == null) {
				uuid = UUID.randomUUID().toString();
			}

			return new FieldTransformingInfo(field, uuid, null);
		} catch (Exception e) {
			throw new EtlConfException("Error happened while processing transformer " + this, e);
		}

	}

	public TableConfiguration getTableConf() {
		return tableConf;
	}

	public String getTable() {
		return tableName;
	}

	public SrcConf getDefaultSrcConf() {
		return defaultSrcConf;
	}

	void ensureTableInitialized(Connection srcConn, Connection dstConn) throws DBException {

		if (this.getTableConf() == null) {
			synchronized (lock) {
				if (this.getTableConf() == null) {

					AbstractTableConfiguration defaultTable = new GenericTableConfiguration(this.getTable());

					try {
						defaultTable.tryToLoadSchemaInfo(null, srcConn);
					} catch (DatabaseResourceDoesNotExists e) {
						if (this.getRelatedEtlTransformTarget().getRelatedEtlConf().hasDefaultEtlTable()) {
							defaultTable = new GenericTableConfiguration(
									this.getRelatedEtlTransformTarget().getRelatedEtlConf().getDefaultSourceTable());
						} else
							throw new EtlConfException(
									"The transformer failed while trying to create defaultSourceTable for ["
											+ this.getTable()
											+ "] and no defaultEtlTable was configred with the EtlConfiguration. Error when loading transformer: "
											+ this);
					}

					this.defaultSrcConf = SrcConf.fastCreate(defaultTable,
							this.getRelatedEtlTransformTarget().getParentConf(), srcConn);

					this.defaultSrcConf.fullLoad(srcConn);

					TableConfiguration conf = new GenericTableConfiguration(this.getTable(),
							this.getRelatedEtlTransformTarget());

					if (this.tableAlias != null) {
						conf.setTableAlias(this.tableAlias);
					}

					conf.fullLoad(dstConn);

					if (!conf.containsField("uuid")) {
						throw new EtlConfException("The table '" + this.getTable() + "' does not have uuid.");
					}

					this.tableConf = conf;
				}
			}
		}
	}

	private String retrieveExistingOnDemandUuid(EtlProcessor processor, EtlDatabaseObject srcObject,
			List<EtlDatabaseObject> srcObjects, Connection srcConn, Connection dstConn)
			throws DBException, ForbiddenOperationException {

		this.ensureTableInitialized(srcConn, dstConn);

		List<EtlDatabaseObject> allObjs = srcObjects != null ? srcObjects
				: (srcObject != null ? utilities.parseToList(srcObject) : null);

		PreparedQueryInfo p = SQLUtilities.prepareQueryReplacingDataSourceElementsWithParams(
				this.getOnDemandCheckCondition(), utilities.parseToList(this.getTableConf().getAlias()), allObjs,
				getRelatedEtlConf(), dstConn);

		EtlDatabaseObject obj;

		try {
			obj = getTableConf().find(p.getQuery(), resolveDstValues(srcObject, p.getParameters(), srcConn, dstConn),
					dstConn);
		} catch (DBException e) {
			throw e;
		}

		return obj != null ? obj.getFieldValue("uuid").toString() : null;
	}

	private Object[] resolveDstValues(EtlDatabaseObject srcObject, List<FieldTransformingInfo> params,
			Connection srcConn, Connection dstConn) throws DBException {

		ensureTableInitialized(srcConn, dstConn);

		Object[] resolvedParams = new Object[params.size()];

		EtlDatabaseObject auxObject = this.getTableConf().createRecordInstance();

		for (int i = 0; i < params.size(); i++) {
			FieldTransformingInfo paramValueInfo = params.get(i);

			if (!paramValueInfo.skipRelationshipResolution()) {
				ParentTable refInfo = this.getTableConf()
						.findParentRefInfoByField(paramValueInfo.getSrcField().getName());

				if (refInfo != null) {
					auxObject.setFieldValue(refInfo.getChildColumnOnSimpleMapping(),
							paramValueInfo.getTransformedValue());

					EtlDatabaseObject parentInSrc = auxObject.retrieveParentInSrcUsingDstParentInfo(refInfo,
							this.getDefaultSrcConf(), srcConn);

					EtlDatabaseObject parentInDst = null;

					refInfo.fullLoad(dstConn);

					if (parentInSrc != null) {
						parentInDst = auxObject.retrieveParentInDestination(refInfo, parentInSrc, dstConn);
					}

					if (parentInDst == null) {
						srcObject.loadObjectIdData();

						throw new EtlTransformationException("The " + refInfo.getTableName() + "("
								+ paramValueInfo.getTransformedValue() + ") of " + this.getTableConf().getTableName()
								+ "(" + srcObject.getObjectId().asSimpleNumericValue() + ") cannot be found on src db",
								srcObject, ActionOnEtlIssue.ABORT_PROCESS);
					}

					resolvedParams[i] = parentInDst.getObjectId().asSimpleNumericValue();
				} else {
					resolvedParams[i] = paramValueInfo.getTransformedValue();
				}
			} else {
				resolvedParams[i] = paramValueInfo.getTransformedValue();
			}
		}

		return resolvedParams;
	}

	@Override
	public void init(Connection srcConn, Connection dstConn) throws DBException {

	}
}
