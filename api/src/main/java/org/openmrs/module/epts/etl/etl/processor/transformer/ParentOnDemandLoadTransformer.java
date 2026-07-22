package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlChildItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlTemplateInfo;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.PreparedQueryInfo;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.model.EtlLoadHelper;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.inconsistenceresolver.model.InconsistenceInfo;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.model.pojo.generic.Oid;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.InconsistentStateException;
import org.openmrs.module.epts.etl.utilities.db.conn.SQLUtilities;

/**
 * Transformer responsible for resolving, reusing, or creating a parent record
 * on demand in the destination table and returning its primary key as the
 * transformed value.
 * <p>
 * This transformer is used when the current record being transformed depends on
 * a parent record that may or may not already exist in the destination
 * database. The transformer first attempts to resolve an existing parent from
 * the source data, then tries to locate an already created on-demand parent in
 * the destination using a configurable condition, and finally creates the
 * parent record on demand when necessary.
 * </p>
 * <p>
 * Transformer syntax:
 * </p>
 * 
 * <pre>
 * ParentOnDemandLoadTransformer(
 *      parentTable,
 *      parent_field_in_datasource_object:srcField,
 *      on_demand_check_condition:sqlCondition,
 *      template:templateName,
 *      template_param_paramName:srcFieldOrValue,
 *      dstField1:srcFieldOrValue1,
 *      dstField2:srcFieldOrValue2,
 *      ...
 *      override_fields:field1,field2,...
 * )
 * </pre>
 * <p>
 * Parameters:
 * </p>
 * <ul>
 * <li><b>parentTable</b> – Name of the parent table in the destination database
 * whose record must exist before the child record is saved.</li>
 * <li><b>parent_field_in_datasource_object:srcField</b> – Defines the field
 * from the available source data objects that should be used to resolve the
 * parent record from the source database. This parameter replaces the old
 * positional <code>parentFieldOnDataSourceObject</code> argument.</li>
 * <li><b>on_demand_check_condition:sqlCondition</b> – Optional condition used
 * to search for an already existing parent record previously created on demand
 * in the destination database. If a record matching this condition is found, it
 * will be reused as the parent instead of creating a new one.</li>
 * <li><b>template:templateName</b> – Optional template name used to initialize
 * the {@link org.openmrs.module.epts.etl.conf.EtlItemConfiguration} responsible
 * for creating or loading the parent on demand. Templates allow reuse of
 * predefined ETL configurations.</li>
 * <li><b>template_param_paramName:srcFieldOrValue</b> – Optional parameters
 * used to dynamically inject values into the specified template. These
 * parameters follow a flat prefix-based approach and are resolved at runtime
 * before the template is applied.
 * <p>
 * Supported forms:
 * </p>
 * <ul>
 * <li><b>template_param_x:srcField</b> – copy value from a source field</li>
 * <li><b>template_param_x:constantValue</b> – assign constant value</li>
 * <li><b>template_param_x:@parameter</b> – assign dynamic ETL parameter</li>
 * <li><b>template_param_x:null</b> – explicitly assign <code>null</code></li>
 * <li><b>template_param_x:</b> – implicitly assign <code>null</code></li>
 * </ul>
 * </li>
 * <li><b>dstField:srcFieldOrValue</b> – Optional additional field mappings used
 * when creating the parent record in the destination database. Each parameter
 * defines how a field in the parent record should be populated.
 * <p>
 * Supported forms:
 * </p>
 * <ul>
 * <li><b>dstField:srcField</b> – copy the value from a field available in the
 * source data</li>
 * <li><b>dstField:constantValue</b> – assign a constant value</li>
 * <li><b>dstField:@parameter</b> – assign a dynamic ETL parameter value</li>
 * <li><b>dstField:null</b> – explicitly set the destination field to
 * <code>null</code></li>
 * <li><b>dstField:</b> – omit the value to implicitly assign
 * <code>null</code></li>
 * </ul>
 * </li>
 * <li><b>override_fields:field1,field2,...</b> – Defines which fields should be
 * recomputed and updated when an existing parent record is reused. By default,
 * existing parent records are reused as-is. When this parameter is defined,
 * only the specified fields are recalculated and overwritten using the same
 * transformation logic defined for parent creation.</li>
 * </ul>
 * <p>
 * Behavior:
 * </p>
 * <ol>
 * <li>Attempt to resolve the parent from the source database using
 * <code>parent_field_in_datasource_object</code>.</li>
 * <li>If the source parent exists, attempt to locate the corresponding record
 * in the destination.</li>
 * <li>If no destination parent is found and
 * <code>on_demand_check_condition</code> is defined, search for an already
 * existing parent previously created on demand.</li>
 * <li>If no existing parent is found, initialize the ETL item configuration
 * (optionally using a template and injected template parameters) and create or
 * migrate the parent on demand.</li>
 * <li>If an existing parent is reused and <code>override_fields</code> is
 * defined, recompute and update only the specified fields.</li>
 * <li>Return the parent primary key as the transformed value.</li>
 * </ol>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * ParentOnDemandLoadTransformer(
 *     visit,
 *     parent_field_in_datasource_object:visit_id,
 *     on_demand_check_condition:patient_id=patient_id and date_started=encounter_datetime,
 *     template:visit_on_demand_template,
 *     template_param_patient_id:@patient_id,
 *     template_param_visit_date:@encounter_datetime,
 *     visit_type_id:42,
 *     date_started:encounter_datetime,
 *     location_id:@migration_location_id,
 *     date_stopped:null,
 *     indication_concept_id:
 * )
 * </pre>
 * <p>
 * In this example:
 * </p>
 * <ul>
 * <li>The parent record belongs to the <b>visit</b> table.</li>
 * <li>The source parent is resolved using the field <b>visit_id</b>.</li>
 * <li>If no mapped destination parent is found, the transformer checks for an
 * existing visit matching the provided condition.</li>
 * <li>The ETL configuration is initialized using the template
 * <b>visit_on_demand_template</b>.</li>
 * <li>The template parameters <b>patient_id</b> and <b>visit_date</b> are
 * dynamically injected.</li>
 * <li>The field <b>visit_type_id</b> receives the constant value
 * <b>42</b>.</li>
 * <li>The field <b>date_started</b> is populated from
 * <b>encounter_datetime</b>.</li>
 * <li>The field <b>location_id</b> is populated from the ETL parameter
 * <b>@migration_location_id</b>.</li>
 * <li>The field <b>date_stopped</b> is explicitly set to
 * <code>null</code>.</li>
 * <li>The field <b>indication_concept_id</b> is implicitly set to
 * <code>null</code>.</li>
 * </ul>
 * <p>
 * This transformer is intended for high-performance ETL scenarios where parent
 * records must be resolved or created dynamically during child record
 * transformation.
 * </p>
 */
public class ParentOnDemandLoadTransformer extends AbstractEtlFieldTransformer {

	protected final Object lock = new Object();

	protected final String[] listFields = { "override_fields" };

	protected static final Map<String, ParentOnDemandLoadTransformer> INSTANCES = new ConcurrentHashMap<>();

	private OnDemandInfo onDemandInfo;

	public ParentOnDemandLoadTransformer(OnDemandInfo onDemandInfo, Connection conn)
			throws FieldAvaliableInMultipleDataSources, DBException {

		super(onDemandInfo.getOriginalParameters(), onDemandInfo.getRelatedEtlTransformTarget(),
				onDemandInfo.getField());

		this.onDemandInfo = onDemandInfo;
	}

	public void setOnDemandInfo(OnDemandInfo onDemandInfo) {
		this.onDemandInfo = onDemandInfo;
	}

	public OnDemandInfo getOnDemandInfo() {
		return onDemandInfo;
	}

	@Override
	public DstConf getRelatedEtlTransformTarget() {
		return (DstConf) super.getRelatedEtlTransformTarget();
	}

	public static ParentOnDemandLoadTransformer getInstance(List<Object> parameters,
			EtlTransformTarget relatedEtlTransformTarget, TransformableField field, Connection conn) {

		String key = buildCacheKey(relatedEtlTransformTarget, field, parameters);

		return INSTANCES.computeIfAbsent(key, k -> {
			try {
				return new ParentOnDemandLoadTransformer(
						OnDemandInfo.create(parameters, (DstConf) relatedEtlTransformTarget, field, conn), conn);
			} catch (DBException e) {
				throw new EtlExceptionImpl(e);
			}
		});
	}

	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			Connection srcConn, Connection dstConn) throws DBException, EtlTransformationException {

		traceTransformationInitialization(field);

		try {
			EtlDatabaseObject dstParent = null;
			EtlDatabaseObject srcParent = null;

			if (existingSrcParentIsApplicable()) {

				try {
					srcParent = this.resolveSrcParent(processor, srcObject, transformedRecord, additionalSrcObjects,
							srcConn, dstConn);

					dstParent = this.resolveParent(processor, srcParent, srcObject, transformedRecord,
							additionalSrcObjects, srcConn, dstConn);
				} catch (InconsistentStateException e) {

					ParentTable parentInfo = ((TableConfiguration) srcObject.getRelatedConfiguration())
							.findParentRefInfoByParentTable(getParentTableName());

					parentInfo.setTableName(getParentTableName());

					InconsistenceInfo inconsistence = InconsistenceInfo.generate(srcObject, parentInfo,
							processor.getRelatedEtlConfiguration().getOriginAppLocationCode());

					srcObject.setFieldValue(this.getParentSourceField(), null);

					inconsistence.save((TableConfiguration) this.relatedEtlTransformTarget, srcConn);
				}
			}

			if (dstParent == null) {
				dstParent = this.retrieveExistingOnDemandParent(processor, srcObject, additionalSrcObjects, srcConn,
						dstConn);

				if (dstParent == null) {
					if (usesTemplate()) {
						SrcConf srcConf = null;

						if (srcParent == null) {
							srcConf = loadSrcConfForNonExistingSrcParentIfNeeded(srcConn, dstConn);
						} else {
							srcConf = loadSrcConfForExistingSrcParentIfNeeded(srcConn, dstConn);
						}

						if (!srcConf.doNotUseAsDatasource()) {
							List<EtlDatabaseObject> recs;
							logTrace("Searching recs on demand for: " + this);

							recs = srcConf.searchRecords(null, srcObject, additionalSrcObjects, srcConn);

							logTrace("OnDemand Searching recs finalized: " + this);

							if (recs.isEmpty()) {
								throw new EtlExceptionImpl("No src record was returned with " + this);
							}

							srcParent = recs.get(0);
						} else {
							System.err.println();
						}
					}

					dstParent = this.createParent(processor, srcParent, srcObject, transformedRecord,
							additionalSrcObjects, field, srcConn, dstConn);

				}

			}

			if (dstParent == null) {

				if (field.nullValueBehavior().abortProcess()) {
					throw new EtlTransformationException(
							"Error on transforming the parentDstRecord on " + getTransformerDsc(), srcObject,
							ActionOnEtlIssue.ABORT_PROCESS);
				}
			}

			return new FieldTransformingInfo(field, dstParent != null ? dstParent.getObjectId().asSimpleValue() : null,
					dstParent != null ? (EtlDataSource) dstParent.getRelatedConfiguration() : null);
		} finally {
			traceTransformationFinalization(field);
		}

	}

	EtlDatabaseObject resolveSrcParent(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, Connection srcConn,
			Connection dstConn) throws EtlTransformationException, DBException {

		EtlDatabaseObject srcParent = null;

		if (this.getParentSourceIdMapping() != null) {

			FieldTransformingInfo fieldInfo = null;

			fieldInfo = this.getParentSourceIdMapping().getTransformerInstance().transform(processor, srcObject,
					transformedRecord, additionalSrcObjects, getParentSourceIdMapping(), srcConn, dstConn);

			if (fieldInfo != null && fieldInfo.getTransformedValue() != null) {
				SrcConf srcConf = loadSrcConfForExistingSrcParentIfNeeded(srcConn, dstConn);

				srcParent = DatabaseObjectDAO.getByOid(srcConf,
						Oid.fastCreate(srcConf, fieldInfo.getTransformedValue()), srcConn);

				if (srcParent == null) {
					throw new InconsistentStateException("The related srcValue (" + fieldInfo.getTransformedValue()
							+ ") does not represent a valid Src Object within " + getTransformerDsc());
				}
			}
		}

		return srcParent;
	}

	EtlDatabaseObject resolveParent(EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, Connection srcConn,
			Connection dstConn) throws EtlTransformationException, DBException {

		if (srcParent != null) {
			ensureEtlTransformTargetForExistingSrcParentInitialized(srcConn, dstConn);

			EtlDatabaseObjectConfiguration bkp = srcParent.getRelatedConfiguration();

			srcParent.setRelatedConfiguration(getEtlTransformTargetForExistingSrcParent(srcConn, dstConn));

			EtlDatabaseObject dstObject = DatabaseObjectDAO.getByUniqueKeys(srcParent, dstConn);

			srcParent.setRelatedConfiguration(bkp);

			return dstObject;

		} else {
			return null;
		}
	}

	EtlDatabaseObject createParent(EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			Connection srcConn, Connection dstConn) throws DBException {

		processor.logDebug("Performing on-demand creation of "
				+ this.loadSrcConfForNonExistingSrcParentIfNeeded(srcConn, dstConn).getTableName() + " For "
				+ srcObject.getRelatedConfiguration().getObjectName());

		DstConf etlTransformTarget = null;

		TransformationType transformationType = TransformationType.PRINCIPAL;

		if (srcParent != null && sourceParentMayExists()) {
			ensureEtlTransformTargetForExistingSrcParentInitialized(srcConn, dstConn);

			etlTransformTarget = getEtlTransformTargetForExistingSrcParent(srcConn, dstConn);
		} else {
			ensureEtlTransformTargetForNonExistingSrcParentInitialized(false, srcConn, dstConn);

			etlTransformTarget = getEtlTransformTargetForNonExistingSrcParent(srcConn, dstConn);

			if (srcParent == null) {
				srcParent = srcObject.createACopy();
				srcParent.setAuxLoadObject(null);
			}

			transformationType = TransformationType.ON_DEMAND;
		}

		srcParent.setAuxLoadObject(!srcParent.hasAuxLoadObject() ? new ArrayList<>() : srcParent.getAuxLoadObject());
		srcParent.getAuxLoadObject().addAll(additionalSrcObjects);

		List<EtlDatabaseObject> migratedRecs = null;

		try {
			EtlLoadHelper loadHelper = EtlLoadHelper.fastLoadRecord(processor, srcParent, (DstConf) etlTransformTarget,
					transformationType, srcConn, dstConn);

			migratedRecs = loadHelper.getAllSuccedTransformedObjects((DstConf) etlTransformTarget);

		} catch (DBException e) {
			if (e.isDuplicatePrimaryOrUniqueKeyException()) {
				EtlDatabaseObject dstParent = resolveParent(processor, srcParent, srcObject, transformedRecord,
						additionalSrcObjects, srcConn, dstConn);

				if (dstParent != null) {
					migratedRecs = utilities.parseToList(dstParent);
				} else
					throw e;

			} else
				throw e;
		} catch (ForbiddenOperationException e) {
			e.printStackTrace();

			throw e;
		}

		if (utilities.listHasElement(migratedRecs)) {
			return migratedRecs.get(0);
		}

		return null;

	}

	private EtlDatabaseObject retrieveExistingOnDemandParent(EtlProcessor processor, EtlDatabaseObject srcObject,
			List<EtlDatabaseObject> srcObjects, Connection srcConn, Connection dstConn)
			throws DBException, ForbiddenOperationException {

		if (!utilities.stringHasValue(this.getOnDemandCheckCondition())) {
			return null;
		}

		this.ensureEtlTransformTargetForNonExistingSrcParentInitialized(true, srcConn, dstConn);

		DstConf dstConf = this.getEtlTransformTargetForNonExistingSrcParent(srcConn, dstConn);

		List<EtlDatabaseObject> allObjs = srcObjects != null ? srcObjects
				: (srcObject != null ? utilities.parseToList(srcObject) : null);

		String onDemand = this.getOnDemandCheckCondition();

		logTrace(onDemand);

		PreparedQueryInfo p = SQLUtilities.prepareQueryReplacingDataSourceElementsWithParams(
				this.getOnDemandCheckCondition(), null, allObjs, getRelatedEtlConf(), dstConn);

		return dstConf.find(p.getPreparedQuery(), resolveDstValues(srcObject, p.getParameters(), srcConn, dstConn),
				dstConn);
	}

	private Object[] resolveDstValues(EtlDatabaseObject srcObject, List<FieldTransformingInfo> params,
			Connection srcConn, Connection dstConn) throws DBException {

		this.ensureEtlTransformTargetForNonExistingSrcParentInitialized(false, srcConn, dstConn);

		SrcConf srcConf = loadSrcConfForNonExistingSrcParentIfNeeded(srcConn, dstConn);
		DstConf dstConf = this.getEtlTransformTargetForNonExistingSrcParent(srcConn, dstConn);

		Object[] resolvedParams = new Object[params.size()];

		EtlDatabaseObject auxObject = dstConf.createRecordInstance();

		for (int i = 0; i < params.size(); i++) {
			FieldTransformingInfo paramValueInfo = params.get(i);

			if (!paramValueInfo.isLoadedWithDstValue()) {
				ParentTable refInfo = dstConf.findParentRefInfoByField(paramValueInfo.getSrcField().getName());

				if (refInfo != null) {
					auxObject.setFieldValue(refInfo.getChildColumnOnSimpleMapping(),
							paramValueInfo.getTransformedValue());

					EtlDatabaseObject parentInSrc = auxObject.retrieveParentInSrcUsingDstParentInfo(refInfo, srcConf,
							srcConn);

					EtlDatabaseObject parentInDst = null;

					if (parentInSrc != null) {
						parentInDst = auxObject.retrieveParentInDestination(refInfo, parentInSrc, dstConn);
					}

					if (parentInDst == null) {
						throw new EtlTransformationException("The " + refInfo.getTableName() + "("
								+ paramValueInfo.getTransformedValue() + ") of " + dstConf.getTableName() + "("
								+ srcObject.getObjectId().asSimpleNumericValue() + ") cannot be found on src db",
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

	DstConf getEtlTransformTargetForExistingSrcParent(Connection srcConn, Connection dstConn) throws DBException {
		return this.getExistingParentItemConf().getDstConf().get(0);
	}

	protected DstConf getEtlTransformTargetForNonExistingSrcParent(Connection srcConn, Connection dstConn)
			throws DBException {
		return this.getOnDemandCreateParentItemConf().getDstConf().get(0);
	}

	SrcConf loadSrcConfForNonExistingSrcParentIfNeeded(Connection srcConn, Connection dstConn) throws DBException {
		ensureEtlItemConfForNonExistingSrcParentInitialized(srcConn, dstConn);

		return this.getOnDemandCreateParentItemConf().getSrcConf();
	}

	SrcConf loadSrcConfForExistingSrcParentIfNeeded(Connection srcConn, Connection dstConn) throws DBException {
		ensureEtlItemConfForExistingSrcParentInitialized(srcConn, dstConn);

		return this.getExistingParentItemConf().getSrcConf();
	}

	protected void ensureEtlTransformTargetForNonExistingSrcParentInitialized(boolean skipFullLoad, Connection srcConn,
			Connection dstConn) throws DBException {

		synchronized (lock) {
			this.ensureEtlItemConfForNonExistingSrcParentInitialized(srcConn, dstConn);

			DstConf etlTransformTarget = this.getEtlTransformTargetForNonExistingSrcParent(srcConn, dstConn);

			if (skipFullLoad && !etlTransformTarget.isFullLoaded()) {
				etlTransformTarget.tryToGenerateTableAlias(etlTransformTarget.getRelatedEtlConf());

				if (!usesTemplate()) {
					etlTransformTarget.setMapping(this.getOnDemandParentFieldMappings());
				}

				etlTransformTarget
						.addAllToAvaliableDataSource(this.relatedEtlTransformTarget.getAllAvaliableDataSource());
				etlTransformTarget
						.addAllToPreferredDataSource(this.relatedEtlTransformTarget.getAllPrefferredDataSource());
			} else if (!etlTransformTarget.isFullLoaded()) {
				if (!etlTransformTarget.isFullLoaded()) {

					if (!usesTemplate()) {
						etlTransformTarget.getSrcConf().setDoNotUseAsDatasource(true);
						etlTransformTarget.setMapping(this.getOnDemandParentFieldMappings());
					}

					etlTransformTarget
							.addAllToAvaliableDataSource(this.relatedEtlTransformTarget.getAllAvaliableDataSource());
					etlTransformTarget
							.addAllToPreferredDataSource(this.relatedEtlTransformTarget.getAllPrefferredDataSource());

					etlTransformTarget.fullLoad(dstConn);

				}
			}
		}
	}

	public String getParentTableName() {
		return onDemandInfo.getParentTableName();
	}

	public String getParentSourceField() {
		return onDemandInfo.getParentSourceField();
	}

	public boolean usesTemplate() {
		return onDemandInfo.usesTemplate();
	}

	public FieldsMapping getParentSourceIdMapping() {
		return onDemandInfo.getParentSourceIdMapping();
	}

	public boolean sourceParentMayExists() {
		return this.onDemandInfo.sourceParentMayExists();
	}

	public String getOnDemandCheckCondition() {
		return this.onDemandInfo.getOnDemandCheckCondition();
	}

	public List<FieldsMapping> getOnDemandParentFieldMappings() {
		return this.onDemandInfo.getOnDemandParentFieldMappings();
	}

	public String getOverrideFieldsStr() {
		return onDemandInfo.getOverrideFieldsStr();
	}

	public List<FieldsMapping> getOverrideFields() {
		return onDemandInfo.getOverrideFields();
	}

	public String getTemplateName() {
		return onDemandInfo.getTemplateName();
	}

	public Map<String, Object> getTemplateParams() {
		return onDemandInfo.getTemplateParams();
	}

	void ensureOverrideFieldsInitialized(Connection srcConn, Connection dstConn) throws DBException {

		if (!existingSrcParentIsApplicable()) {
			throw new ForbiddenOperationException("The override fields are not applicable for this transformer!");
		}

		if (utilities.stringHasValue(this.onDemandInfo.getOverrideFieldsStr())) {

			synchronized (lock) {
				ensureEtlItemConfForNonExistingSrcParentInitialized(srcConn, dstConn);

				List<FieldsMapping> onDemandFields = getOnDemandParentFieldMappings() != null
						? getOnDemandParentFieldMappings()
						: getEtlTransformTargetForNonExistingSrcParent(srcConn, dstConn).getMapping();

				String[] toOverride = this.getOverrideFieldsStr().split(",");

				for (String to : toOverride) {
					FieldsMapping f = new FieldsMapping();
					f.setDstField(to);

					int i = onDemandFields.indexOf(f);

					if (i < 0) {
						throw new EtlExceptionImpl("The field to override '" + f.getDstField()
								+ "' is not listed on onDemandFields on transformer \n" + this);
					}

					if (this.getOverrideFieldsStr() == null)
						this.onDemandInfo.setOverrideFields(new ArrayList<>());

					this.getOverrideFields().add(onDemandFields.get(i));

				}
			}

		}
	}

	void ensureEtlTransformTargetForExistingSrcParentInitialized(Connection srcConf, Connection dstConn)
			throws DBException {
		if (!sourceParentMayExists()) {
			throw new EtlExceptionImpl(
					"Error On " + this + "\nExisting SrcParent not applicable as no parentSourceIdMapping is defined!");
		}

		ensureEtlItemConfForExistingSrcParentInitialized(srcConf, dstConn);

		DstConf etlTransformTarget = getEtlTransformTargetForExistingSrcParent(srcConf, dstConn);

		if (!etlTransformTarget.isFullLoaded()) {
			synchronized (lock) {
				if (!etlTransformTarget.isFullLoaded()) {

					List<EtlDataSource> avaliableDataSource = null;
					List<EtlDataSource> preferredDataSource = null;

					if (this.getRelatedEtlTransformTarget().useSharedPKKey() && etlTransformTarget.getTableName()
							.equals(this.getRelatedEtlTransformTarget().getSharePkWith())) {

						preferredDataSource = new ArrayList<>();
						avaliableDataSource = new ArrayList<>();

						for (EtlDataSource p : this.relatedEtlTransformTarget.getAllAvaliableDataSource()) {
							if (p != this.relatedEtlTransformTarget.getSrcConf().getSharedKeyRefInfo(dstConn)) {
								avaliableDataSource.add(p);
							}
						}

						for (EtlDataSource p : this.relatedEtlTransformTarget.getAllPrefferredDataSource()) {
							if (p != this.relatedEtlTransformTarget.getSrcConf().getSharedKeyRefInfo(dstConn)) {
								preferredDataSource.add(p);
							}
						}

					} else {
						avaliableDataSource = this.relatedEtlTransformTarget.getAllAvaliableDataSource();
						preferredDataSource = this.relatedEtlTransformTarget.getAllPrefferredDataSource();

					}

					ensureOverrideFieldsInitialized(srcConf, dstConn);

					etlTransformTarget.setMapping(this.getOverrideFields());

					etlTransformTarget.addAllToAvaliableDataSource(avaliableDataSource);

					etlTransformTarget.addToPrefferedDataSource(etlTransformTarget.getSrcConf());
					etlTransformTarget.addAllToPreferredDataSource(preferredDataSource);

					etlTransformTarget.fullLoad(dstConn);
				}
			}
		}
	}

	protected void ensureEtlItemConfForNonExistingSrcParentInitialized(Connection srcConn, Connection dstConn)
			throws DBException {

		if (this.getOnDemandCreateParentItemConf() == null) {
			synchronized (lock) {
				if (getOnDemandCreateParentItemConf() == null) {

					EtlItemConfiguration conf = this.generateEtlItemConf(srcConn, dstConn);

					this.onDemandInfo.setOnDemandCreateParentItemConf(conf);
				}
			}
		}
	}

	private EtlItemConfiguration generateEtlItemConf(Connection srcConn, Connection dstConn) throws DBException {
		boolean useMainEtlTable = false;

		AbstractTableConfiguration parentConf = new GenericTableConfiguration(this.getParentTableName());

		try {
			parentConf.tryToLoadSchemaInfo(null, srcConn);
		} catch (DatabaseResourceDoesNotExists e) {

			if (this.getRelatedEtlTransformTarget().getRelatedEtlConf().hasDefaultEtlTable()) {

				parentConf = new GenericTableConfiguration(
						this.getRelatedEtlTransformTarget().getRelatedEtlConf().getDefaultSourceTable());

				useMainEtlTable = true;
			} else
				throw new EtlConfException("The defined parentTableName [" + this.getParentTableName()
						+ "] cannot be found on the srcDb and no defaultEtlTable was configred with the EtlConfiguration. Error when loading transformer: "
						+ this);
		}

		EtlChildItemConfiguration conf = EtlChildItemConfiguration.fastCreate(parentConf, srcConn);
		conf.setRelatedEtlConf(this.getRelatedEtlTransformTarget().getRelatedEtlConf());

		EtlTemplateInfo template = usesTemplate() ? new EtlTemplateInfo(this.getTemplateName()) : null;

		if (template != null) {
			template.setParameters(this.getTemplateParams());
			conf.setSrcConf(null);
			conf.setTemplate(template);

			conf.getRelatedEtlConf().trace("Template within transformer loaded!");
		} else if (useMainEtlTable) {
			DstConf EtlTransformTarget = new DstConf(getParentTableName());

			conf.setDstConf(utilities.parseToList(EtlTransformTarget));
		}

		conf.setParentItemConf(this.getRelatedEtlTransformTarget().getParentConf());
		conf.setRelatedParentDstConfName(this.getRelatedEtlTransformTarget().getTableAlias());

		conf.setDoNotFullLoadDstConf(true);
		try {
			conf.init(relatedEtlTransformTarget.getRelatedEtlConf(), false, srcConn, dstConn);
		} catch (DatabaseResourceDoesNotExists e) {
			throw e;
		}

		conf.fullLoad(relatedEtlTransformTarget.getRelatedEtlConf().getOperations().get(0));

		for (DstConf dstC : conf.getDstConf()) {
			dstC.setUnmappedFieldBehavior(this.onDemandInfo.unmappedFieldBehavior());
			dstC.setMappingResolutionStrategy(this.onDemandInfo.getMappingResolutionStrategy());
			dstC.setOnMultipleDataSourceWithSameName(ActionOnEtlIssue.USE_LAST);
			dstC.setAutoIncrementHandlingType(onDemandInfo.getAutoIncrementHandlingType());
		}

		return conf;
	}

	boolean existingSrcParentIsApplicable() {
		return this.getParentSourceIdMapping() != null;
	}

	void ensureEtlItemConfForExistingSrcParentInitialized(Connection srcConn, Connection dstConn) throws DBException {
		if (this.getParentSourceIdMapping() == null) {
			throw new EtlExceptionImpl("Existing SrcParent not applicable as no parentSourceIdMapping is defined!");
		}

		if (this.getExistingParentItemConf() == null) {
			synchronized (lock) {
				if (getExistingParentItemConf() == null) {

					EtlItemConfiguration conf = generateEtlItemConf(srcConn, dstConn);

					this.onDemandInfo.setExistingParentItemConf(conf);
				}
			}
		}
	}

	@Override
	public void init(Connection srcConn, Connection dstConn) throws DBException {
		if (sourceParentMayExists()) {
			ensureEtlTransformTargetForExistingSrcParentInitialized(srcConn, dstConn);
		}

		ensureEtlTransformTargetForNonExistingSrcParentInitialized(true, srcConn, dstConn);
	}

	public EtlItemConfiguration getExistingParentItemConf() {
		return onDemandInfo.getExistingParentItemConf();
	}

	public EtlItemConfiguration getOnDemandCreateParentItemConf() {
		return onDemandInfo.getOnDemandCreateParentItemConf();
	}
}
