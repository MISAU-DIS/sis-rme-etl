
package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.EtlChildItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlCounter;
import org.openmrs.module.epts.etl.conf.EtlField;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlExpansionDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlItemConfigurationComponent;
import org.openmrs.module.epts.etl.conf.interfaces.EtlSrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.JoinableEntity;
import org.openmrs.module.epts.etl.conf.interfaces.MainJoiningEntity;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.ConditionClauseScope;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.conf.types.JoinType;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.etl.model.EtlDatabaseObjectSearchParams;
import org.openmrs.module.epts.etl.etl.model.EtlLoadStatus;
import org.openmrs.module.epts.etl.etl.model.stage.EtlStageAreaObject;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.MissingJoiningElementsException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.db.conn.SQLUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SrcConf extends AbstractTableConfiguration
		implements MainJoiningEntity, JoinableEntity, EtlSrcConf, EtlItemConfigurationComponent {

	private static final String[] TRACKING_FIELDS = { "processing_status", "processing_date", "processing_error",
			"retry_count" };

	private List<AuxExtractTable> auxExtractTable;

	private List<TableDataSourceConfig> extraTableDataSource;

	private List<QueryDataSourceConfig> extraQueryDataSource;

	private List<TransformableDataSource> extraObjectDataSource;

	private List<JsonDataSource> extraJsonDataSource;

	private EtlDstType dstType;

	private List<FieldsMapping> joinFields;

	private ConditionClauseScope joinExtraConditionScope;

	/**
	 * The fields involved in ETL process for this srcConf. Note that when the
	 * dstConf is not configured on related {@link EtlItemConfiguration}, then this
	 * fields will automatically mapped to target table
	 */
	private List<EtlField> etlFields;

	private String joinExtraCondition;

	private Boolean limitToOneResult;

	private Boolean doNotUseAsDatasource;

	/**
	 * Indicates whether source records should track their ETL processing state.
	 *
	 * <p>
	 * When enabled, the ETL engine updates processing metadata directly on the
	 * source record after each processing attempt. This is useful for queue-based
	 * or event-based ETL processes where failed records must remain available for
	 * retry while still exposing their latest processing status.
	 * </p>
	 *
	 * <p>
	 * A state-tracked source table must expose the following fields:
	 * </p>
	 *
	 * <ul>
	 * <li>{@code processing_status}</li>
	 * <li>{@code processing_date}</li>
	 * <li>{@code processing_error}</li>
	 * <li>{@code retry_count}</li>
	 * </ul>
	 *
	 * <p>
	 * If any of these fields are missing, the ETL configuration should be rejected
	 * during validation.
	 * </p>
	 */
	private Boolean trackProcessingState;

	private ExpansionQueryDataSource expansionQueryDataSource;
	private ExpansionTableDataSource expansionTableDataSource;

	private ActionOnEtlIssue missingRequiredObjectBehavior;

	public SrcConf() {
		this.joinExtraConditionScope = ConditionClauseScope.JOIN_CLAUSE;
		this.limitToOneResult = false;
	}

	public ActionOnEtlIssue getMissingRequiredObjectBehavior() {
		return missingRequiredObjectBehavior;
	}

	public void setMissingRequiredObjectBehavior(ActionOnEtlIssue missingRequiredObjectBehavior) {
		this.missingRequiredObjectBehavior = missingRequiredObjectBehavior;
	}

	public ActionOnEtlIssue missingRequiredObjectBehavior() {
		return this.missingRequiredObjectBehavior;
	}

	public Boolean getTrackProcessingState() {
		return trackProcessingState;
	}

	public Boolean trackProcessingState() {
		return isTrue(this.getTrackProcessingState());
	}

	public void setTrackProcessingState(Boolean trackProcessingState) {
		this.trackProcessingState = trackProcessingState;
	}

	public Boolean getLimitToOneResult() {
		return limitToOneResult;
	}

	public void setLimitToOneResult(Boolean limitToOneResult) {
		this.limitToOneResult = limitToOneResult;
	}

	public Boolean limitToOneResult() {
		return isTrue(this.getLimitToOneResult());
	}

	public Boolean getDoNotUseAsDatasource() {
		return doNotUseAsDatasource;
	}

	public void setDoNotUseAsDatasource(Boolean doNotUseAsDatasource) {
		this.doNotUseAsDatasource = doNotUseAsDatasource;
	}

	public ExpansionQueryDataSource getExpansionQueryDataSource() {
		return expansionQueryDataSource;
	}

	public void setExpansionQueryDataSource(ExpansionQueryDataSource expansionQueryDataSource) {
		this.expansionQueryDataSource = expansionQueryDataSource;
	}

	public ExpansionTableDataSource getExpansionTableDataSource() {
		return expansionTableDataSource;
	}

	public void setExpansionTableDataSource(ExpansionTableDataSource expansionTableDataSource) {
		this.expansionTableDataSource = expansionTableDataSource;
	}

	public EtlExpansionDataSource getExpansionDataSource() {
		return this.getExpansionQueryDataSource() != null ? getExpansionQueryDataSource()
				: getExpansionTableDataSource();
	}

	public boolean hasExpansionDs() {
		return this.getExpansionDataSource() != null;
	}

	public void init(EtlItemConfiguration relatedItemConf, Connection srcConn, Connection dstConn) throws DBException {
		if (isInitialized()) {
			return;
		}

		this.applyIncludes();

		this.setParentConf(relatedItemConf);

		super.init(this.getParentConf(), getParentConf().getRelatedEtlSchemaObject(), srcConn, dstConn);

		if (!this.hasDstType()) {
			// We start with the first operation dst type. Eventual this should be changed
			// if the nested operation has different dstType
			this.setDstType(getRelatedEtlConf().getOperations().get(0).getDstType());
		}

		if (this.hasExpansionDs()) {
			this.getExpansionDataSource().setParentConf(this);
		}

		if (this.hasAlias()) {
			this.setUsingManualDefinedAlias(true);
			getRelatedEtlConf().tryToAddToBusyTableAliasName(this.getTableAlias());
		} else {
			tryToGenerateTableAlias(getRelatedEtlConf());
		}

		if (getExpansionQueryDataSource() != null && getExpansionTableDataSource() != null) {
			throw new EtlConfException(
					"Invalid srcConf configuration. Only one expansion data source may be configured. "
							+ "Choose either 'expansionQueryDataSource' or 'expansionTableDataSource', "
							+ "but not both.");
		}

		initAllAvaliableExtraDataSource(srcConn, dstConn);

		setInitialized(true);
	}

	@Override
	public Boolean doNotUseAsDatasource() {
		return isTrue(doNotUseAsDatasource);
	}

	public List<JsonDataSource> getExtraJsonDataSource() {
		return extraJsonDataSource;
	}

	public void setExtraJsonDataSource(List<JsonDataSource> extraJsonDataSOurce) {
		this.extraJsonDataSource = extraJsonDataSOurce;
	}

	public List<TransformableDataSource> getExtraObjectDataSource() {
		return extraObjectDataSource;
	}

	public void setExtraObjectDataSource(List<TransformableDataSource> extraObjectDataSource) {
		this.extraObjectDataSource = extraObjectDataSource;
	}

	public List<EtlField> getEtlFields() {
		return etlFields;
	}

	public void setEtlFields(List<EtlField> etlFields) {
		this.etlFields = etlFields;
	}

	public EtlDstType getDstType() {
		return dstType;
	}

	public void setDstType(EtlDstType dstType) {
		this.dstType = dstType;
	}

	public List<AuxExtractTable> getAuxExtractTable() {
		return auxExtractTable;
	}

	public void setAuxExtractTable(List<AuxExtractTable> auxExtractTable) {
		this.auxExtractTable = (List<AuxExtractTable>) auxExtractTable;
	}

	@Override
	public List<? extends JoinableEntity> getJoiningTable() {
		return this.getAuxExtractTable();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setJoiningTable(List<? extends JoinableEntity> joiningTable) {
		setAuxExtractTable((List<AuxExtractTable>) joiningTable);
	}

	public List<QueryDataSourceConfig> getExtraQueryDataSource() {
		return extraQueryDataSource;
	}

	public void setExtraQueryDataSource(List<QueryDataSourceConfig> extraQueryDataSource) {
		this.extraQueryDataSource = extraQueryDataSource;
	}

	public List<TableDataSourceConfig> getExtraTableDataSource() {
		return extraTableDataSource;
	}

	public void setExtraTableDataSource(List<TableDataSourceConfig> extraTableDataSource) {
		this.extraTableDataSource = extraTableDataSource;
	}

	@Override
	public String getName() {
		return getTableName();
	}

	@Override
	public Boolean isGeneric() {
		return false;
	}

	public Boolean hasDstType() {
		return getDstType() != null;
	}

	@Override
	public void fullLoad(Connection conn) throws DBException {
		init(getParentConf(), conn, conn);

		if (!hasManualMapPrimaryKeyOnField()) {
			setManualMapPrimaryKeyOnField(getRelatedEtlConf().getManualMapPrimaryKeyOnField());
		}

		super.fullLoad(conn);
	}

	@Override
	public void loadParents(Connection conn) throws DBException {
		super.loadParents(conn);

		if (useSharedPKKey()) {

			ParentTable shrd = this.getSharedKeyRefInfo(conn);

			// Parse the shared parent to data source
			ParentAsSrcDataSource sharedAsSrcConf = ParentAsSrcDataSource.generateFromSrcConfSharedPkParent(this, shrd,
					conn);

			utilities.updateOnArray(this.getParentRefInfo(), shrd, sharedAsSrcConf);
		}
	}

	@Override
	public void loadOwnElements(EtlDatabaseObject schemaInfo, Connection conn) throws DBException {

		super.loadOwnElements(schemaInfo, conn);

		if (hasJoinExtraCondition()) {
			if (!SQLUtilities.isValidSelectSqlQuery("select * from where " + this.getJoinExtraCondition(), null)) {
				throw new EtlConfException("Invalid joinExtraCondition \n" + this.getJoinExtraCondition());
			}
		}

		this.tryToLoadParentRefInfo(conn);

		this.tryToLoadAuxExtraJoinTable(schemaInfo, conn);

		this.tryToLoadExtraDatasource(schemaInfo, conn);

		this.loadEtlFields();

		this.setFullLoaded(true);

		if (isJoinable()) {
			this.loadJoinElements(schemaInfo, conn);
		}

		if (this.getMissingRequiredObjectBehavior() == null) {
			this.setMissingRequiredObjectBehavior(this.getRelatedEtlConf().getDefaultMissingRequiredObjectBehavior());
		}

		if (trackProcessingState() && !isTrackabled()) {
			throw new EtlConfException(
					"Table " + this + "is configured to 'trackProcessingState' but miss one or more of traking field "
							+ TRACKING_FIELDS);
		}
	}

	@Override
	public void loadJoinElements(EtlDatabaseObject schemaInfo, Connection conn) throws DBException {
		try {
			JoinableEntity.super.loadJoinElements(schemaInfo, conn);
		} catch (MissingJoiningElementsException e) {
			if (this.hasParentItemConf() && this.getPrimaryKey().equals(this.getParentSrcConf().getPrimaryKey())) {
				this.setJoinFields(new ArrayList<>());

				String srcField = this.getParentSrcConf().getTableAlias() + "."
						+ this.getPrimaryKey().asSimpleKey().getName();

				this.getJoinFields().add(FieldsMapping.fastCreate(srcField, conn));
			}
		}
	}

	public SrcConf getParentSrcConf() {
		if (hasParentItemConf()) {
			return ((EtlChildItemConfiguration) this.getParentConf()).getParentItemConf().getSrcConf();
		}

		return null;
	}

	public Boolean hasParentItemConf() {
		return this.getParentConf() instanceof EtlChildItemConfiguration;
	}

	private void loadEtlFields() {
		if (hasExtraDataSource() || hasAuxExtractTable()) {
			this.setEtlFields(new ArrayList<>());

			this.loadOwnFieldsToEtlFields(this.getEtlFields(), false);

			for (EtlDataSource ds : this.getAvaliableExtraDataSource()) {
				ds.loadOwnFieldsToEtlFields(this.getEtlFields(), false);
			}
		} else {
			this.setEtlFields(new ArrayList<>());

			// Preserve the original names if there is only the main ds
			this.loadOwnFieldsToEtlFields(this.getEtlFields(), true);
		}
	}

	/**
	 * @param conn
	 * @param srcConn
	 * @throws DBException
	 */
	private void tryToLoadExtraDatasource(EtlDatabaseObject schemaInfo, Connection conn) throws DBException {
		OpenConnection srcConn = this.getRelatedConnInfo().openConnection(this);

		try {

			if (this.hasExpansionDs()) {
				this.getExpansionDataSource().fullLoad(srcConn);
			}

			if (hasExtraTableDataSourceConfig()) {
				for (TableDataSourceConfig t : this.getExtraTableDataSource()) {

					TableConfiguration fullLoadedTab = findFullConfiguredConfInAllRelatedTable(t.getFullTableName(),
							new ArrayList<>());

					t.tryToGenerateTableAlias(getRelatedEtlConf());

					if (fullLoadedTab != null) {
						t.clone(fullLoadedTab, this, null, conn);
					} else {
						t.fullLoad(srcConn);
					}

					t.setRelatedSrcConf(this);

					if (t.useSharedPKKey()) {
						t.getSharedKeyRefInfo(conn).tryToGenerateTableAlias(getRelatedEtlConf());

						fullLoadedTab = findFullConfiguredConfInAllRelatedTable(
								t.getSharedKeyRefInfo(conn).getFullTableName(), new ArrayList<>());

						if (fullLoadedTab != null) {
							t.getSharedKeyRefInfo(conn).clone(fullLoadedTab, this, null, conn);
						} else {
							t.getSharedKeyRefInfo(conn).fullLoad();
						}
					}
				}
			}

			if (hasExtraQueryDataSourceConfig()) {
				for (QueryDataSourceConfig query : this.getExtraQueryDataSource()) {
					query.setRelatedSrcConf(this);
					query.fullLoad(srcConn);
				}
			}

			if (hasExtraObjectDataSourceConfig()) {
				for (TransformableDataSource ds : this.getExtraObjectDataSource()) {
					ds.setRelatedSrcConf(this);

					if (ds.hasObjectFields()) {
						for (DataSourceField f : ds.getObjectFields()) {
							f.setName(SQLUtilities.tryToReplaceParamsInQuery(f.getName().toString(), schemaInfo));

							if (f.hasValue() && schemaInfo != null) {
								f.setValue(SQLUtilities.tryToReplaceParamsInQuery(f.getValue().toString(), schemaInfo));
							}
						}
					}

					ds.fullLoad(srcConn);
				}
			}

			if (hasExtraJsonSourceConfig()) {
				for (JsonDataSource jDs : this.getExtraJsonDataSource()) {
					jDs.setRelatedSrcConf(this);
					jDs.fullLoad(srcConn);
				}
			}

		} finally {
			srcConn.finalizeConnection(this);
		}
	}

	/**
	 * @param conn
	 * @throws DBException
	 */
	private void tryToLoadParentRefInfo(Connection conn) throws DBException {
		if (this.hasParentRefInfo()) {
			for (ParentTable ref : this.getParentRefInfo()) {
				TableConfiguration fullLoadedTab = findFullConfiguredConfInAllRelatedTable(ref.getFullTableName(),
						new ArrayList<>());

				ref.tryToGenerateTableAlias(getRelatedEtlConf());

				if (fullLoadedTab != null) {
					ref.clone(fullLoadedTab, this, null, conn);
				} else {
					ref.fullLoad();
				}

				if (ref.useSharedPKKey()) {
					fullLoadedTab = findFullConfiguredConfInAllRelatedTable(
							ref.getSharedKeyRefInfo(conn).getFullTableName(), new ArrayList<>());

					if (!ref.getSharedKeyRefInfo(conn).hasAlias()) {
						ref.getSharedKeyRefInfo(conn).tryToGenerateTableAlias(getRelatedEtlConf());
					}
					if (fullLoadedTab != null) {
						ref.getSharedKeyRefInfo(conn).clone(fullLoadedTab, this, null, conn);
					} else {
						ref.getSharedKeyRefInfo(conn).fullLoad();
					}
				}

			}
		}
	}

	public static SrcConf fastCreate(AbstractTableConfiguration tableConfig, EtlItemConfiguration itemConf,
			Connection conn) throws DBException {
		SrcConf src = new SrcConf();

		src.copyFromOther(tableConfig, null, itemConf, conn);

		return src;
	}

	@Override
	public void setParentConf(EtlDataConfiguration parent) {
		/*
		 * if (!(parent instanceof EtlItemConfiguration)) throw new
		 * ForbiddenOperationException("Only 'EtlItemConfiguration' is allowed to be a parent of an SrcConf"
		 * );
		 */
		super.setParentConf(parent);
	}

	@Override
	public DBConnectionInfo getRelatedConnInfo() {
		return getSrcConnInfo();
	}

	@JsonIgnore
	public List<EtlAdditionalDataSource> getAvaliableExtraDataSource() {
		if (!isInitialized())
			throw new EtlExceptionImpl("The SrcConf is not yet initialized!! ");

		List<EtlAdditionalDataSource> ds = new ArrayList<>();

		if (useSharedPKKey() && isFullLoaded()) {
			ds.add((EtlAdditionalDataSource) getSharedKeyRefInfo(null));
		}

		// We intentional give the jsonDatasource high priority because usual them do
		// not depends on other datasources
		if (hasExtraJsonSourceConfig()) {

			for (JsonDataSource d : this.getExtraJsonDataSource()) {
				ds.add(d.getOutputDataSource());

				if (d.getOutputDataSource().hasParents()) {
					ds.addAll(d.getOutputDataSource().getParentOutputDataSource());
				}
			}
		}

		if (hasExtraTableDataSourceConfig()) {
			ds.addAll(this.getExtraTableDataSource());
		}

		if (hasExtraQueryDataSourceConfig()) {
			ds.addAll(this.getExtraQueryDataSource());
		}

		if (hasExtraObjectDataSourceConfig()) {
			ds.addAll(this.getExtraObjectDataSource());
		}

		return ds;
	}

	private void initAdditionalDataSource(EtlAdditionalDataSource t, Connection srcConn, Connection dstConn)
			throws DBException {
		t.init(this, getParentConf().getRelatedEtlSchemaObject(), srcConn, dstConn);

		if (t instanceof AbstractTableConfiguration) {
			TableConfiguration tAsTabConf = (TableConfiguration) t;

			if (tAsTabConf.hasAlias()) {
				tAsTabConf.setUsingManualDefinedAlias(true);
				getRelatedEtlConf().tryToAddToBusyTableAliasName(tAsTabConf.getTableAlias());
			} else {
				tAsTabConf.tryToGenerateTableAlias(getRelatedEtlConf());
			}

			getRelatedEtlConf().addConfiguredTable((AbstractTableConfiguration) t);
		}
	}

	private void initAdditionalAllDataSource(List<EtlAdditionalDataSource> ds, Connection srcConn, Connection dstConn)
			throws DBException {

		if (ds == null)
			return;

		for (EtlAdditionalDataSource t : ds) {
			initAdditionalDataSource(t, srcConn, dstConn);
		}
	}

	private void initAllAvaliableExtraDataSource(Connection srcConn, Connection dstConn) throws DBException {
		if (useSharedPKKey() && isFullLoaded()) {
			initAdditionalDataSource((EtlAdditionalDataSource) getSharedKeyRefInfo(null), srcConn, dstConn);
		}

		initAdditionalAllDataSource(utilities.parseList(this.getExtraTableDataSource(), EtlAdditionalDataSource.class),
				srcConn, dstConn);

		initAdditionalAllDataSource(utilities.parseList(this.getExtraQueryDataSource(), EtlAdditionalDataSource.class),
				srcConn, dstConn);

		initAdditionalAllDataSource(utilities.parseList(this.getExtraObjectDataSource(), EtlAdditionalDataSource.class),
				srcConn, dstConn);

		initAdditionalAllDataSource(utilities.parseList(this.getExtraJsonDataSource(), EtlAdditionalDataSource.class),
				srcConn, dstConn);
	}

	/**
	 * Generate all avaliable fields on this srcConf, this fields will include all
	 * field from {@link #getFields()} and the fields from all
	 * {@link #extraTableDataSource} Note that the duplicated fields will only be
	 * included once
	 * 
	 * @return
	 */
	public List<Field> generateAllAvaliableFields() {
		List<Field> fields = new ArrayList<>();

		for (Field f : this.getFields()) {
			fields.add(f);
		}

		if (hasExtraTableDataSourceConfig()) {

			for (EtlAdditionalDataSource ds : this.getExtraTableDataSource()) {
				for (Field f : ds.getFields()) {

					if (!fields.contains(f)) {
						fields.add(f);
					}
				}
			}
		}

		return fields;
	}

	public Boolean hasExtraTableDataSourceConfig() {
		return utilities.listHasElement(this.getExtraTableDataSource());
	}

	public Boolean hasExtraQueryDataSourceConfig() {
		return utilities.listHasElement(this.getExtraQueryDataSource());

	}

	public Boolean hasExtraJsonSourceConfig() {
		return utilities.listHasElement(this.getExtraJsonDataSource());

	}

	public Boolean hasExtraObjectDataSourceConfig() {
		return utilities.listHasElement(this.getExtraObjectDataSource());
	}

	public Boolean hasRequiredExtraDataSource() {
		if (hasExtraDataSource()) {
			return false;
		} else {
			for (EtlAdditionalDataSource ds : getAvaliableExtraDataSource()) {
				if (ds.isRequired()) {
					return true;
				}
			}
		}

		return false;
	}

	public Boolean hasEtlFields() {
		return utilities.listHasElement(this.getEtlFields());
	}

	public EtlField getEtlField(String fieldName, List<EtlDataSource> preferredDataSource, Boolean deepCheck) {
		if (utilities.listHasElement(preferredDataSource)) {
			for (EtlDataSource ds : preferredDataSource) {
				EtlField f = getEtlField(fieldName, deepCheck, ds);

				if (f != null) {
					return f;
				}
			}
		} else {
			return getEtlField(fieldName, deepCheck, null);
		}

		return null;
	}

	private EtlField getEtlField(String fieldName, Boolean deepCheck, EtlDataSource preferredDataSource) {

		EtlField found = null;

		if (this.hasEtlFields()) {
			for (EtlField f : this.getEtlFields()) {

				if (f.getName().equals(fieldName)) {
					found = f;
				}

				if (f.hasSrcField()) {
					if (f.getSrcField().getName().equals(fieldName)) {
						found = f;
					}
				}
			}
		}

		if (found != null && (preferredDataSource == null || found.checkIfUsesSameDataSouce(preferredDataSource))) {
			return found;
		}

		if (deepCheck) {

			List<EtlDataSource> allDs = new ArrayList<>();

			allDs.add(this);

			if (this.hasExtraDataSource()) {
				allDs.addAll(this.getAvaliableExtraDataSource());
			}

			if (this.hasExtraDataSource()) {
				for (EtlDataSource ds : allDs) {
					EtlField f = this.getEtlField(EtlField.fastCreate(fieldName, ds, false).getName(), false,
							preferredDataSource);

					if (f != null)
						found = f;

					if (found != null
							&& (preferredDataSource == null || found.checkIfUsesSameDataSouce(preferredDataSource))) {
						return found;
					}
				}
			}
		}

		return null;
	}

	public Boolean hasExtraDataSource() {
		return utilities.listHasElement(getAvaliableExtraDataSource());
	}

	public Boolean isComplex() {
		return hasRequiredExtraDataSource();
	}

	public void copyFromOther(TableConfiguration toClone, EtlDatabaseObject schemaInfoSrc,
			EtlItemConfiguration relatedItemConf, Connection conn) throws DBException {

		super.clone(toClone, relatedItemConf, schemaInfoSrc, conn);

		if (toClone instanceof SrcConf) {
			SrcConf toCloneFrom = (SrcConf) toClone;
			if (utilities.listHasElement(toCloneFrom.getAuxExtractTable())) {
				this.setAuxExtractTable(
						AuxExtractTable.cloneAll(toCloneFrom.getAuxExtractTable(), this, schemaInfoSrc, conn));

				for (AuxExtractTable aux : this.getAuxExtractTable()) {

					if (!aux.isUsingManualDefinedAlias()) {
						aux.setJoinExtraCondition(aux.getJoinExtraCondition().replaceAll(aux.getTableName() + "\\.",
								aux.getTableAlias() + "\\."));
					}

					if (schemaInfoSrc != null) {
						aux.setJoinExtraCondition(
								SQLUtilities.tryToReplaceParamsInQuery(aux.getJoinExtraCondition(), schemaInfoSrc));
					}

					if (!aux.hasJoinFields()) {
						throw new ForbiddenOperationException("No join fields were difined between "
								+ aux.getJoiningEntity().getTableName() + " And " + this.getTableName());
					} else {

						if (schemaInfoSrc != null) {
							for (FieldsMapping joiningField : aux.getJoinFields()) {
								joiningField.setSrcField(SQLUtilities
										.tryToReplaceParamsInQuery(joiningField.getSrcField(), schemaInfoSrc));
								joiningField.setDstField(SQLUtilities
										.tryToReplaceParamsInQuery(joiningField.getDstField(), schemaInfoSrc));
							}
						}
					}
				}
			}

			if (toCloneFrom.hasExtraTableDataSourceConfig()) {
				this.setExtraTableDataSource(TableDataSourceConfig.cloneAll(toCloneFrom.getExtraTableDataSource(), this,
						schemaInfoSrc, conn));
			}

			if (toCloneFrom.hasExtraQueryDataSourceConfig()) {
				this.setExtraQueryDataSource(
						QueryDataSourceConfig.cloneAll(toCloneFrom.getExtraQueryDataSource(), this, conn));
			}

			if (toCloneFrom.hasExtraObjectDataSourceConfig()) {
				this.setExtraObjectDataSource(
						TransformableDataSource.cloneAll(toCloneFrom.getExtraObjectDataSource(), this, conn));

				if (hasExtraObjectDataSourceConfig()) {
					for (TransformableDataSource query : this.getExtraObjectDataSource()) {
						query.setRelatedSrcConf(this);

						if (query.hasObjectFields() && schemaInfoSrc != null) {
							for (DataSourceField f : query.getObjectFields()) {
								try {
									f.setName(SQLUtilities.tryToReplaceParamsInQuery(f.getName().toString(),
											schemaInfoSrc));
								} catch (ForbiddenOperationException e) {
								}

								if (f.hasValue()) {
									try {
										f.setValue(SQLUtilities.tryToReplaceParamsInQuery(f.getValue().toString(),
												schemaInfoSrc));
									} catch (ForbiddenOperationException e) {
									}
								}
							}
						}

					}
				}

			}

			this.setDstType(toCloneFrom.getDstType());
		}
	}

	@Override
	public void tryToReplacePlaceholdersOnOwnElements(EtlDatabaseObject schemaInfoSrc) {
		if (hasAuxExtractTable()) {
			AuxExtractTable.tryToReplacePlaceholders(this.getAuxExtractTable(), schemaInfoSrc);
		}

		if (hasExtraTableDataSourceConfig()) {
			TableDataSourceConfig.tryToReplacePlaceholders(this.getExtraTableDataSource(), schemaInfoSrc);
		}

		if (hasExtraQueryDataSourceConfig()) {
			QueryDataSourceConfig.tryToReplacePlaceholders(this.getExtraQueryDataSource(), schemaInfoSrc);
		}

		if (hasExtraObjectDataSourceConfig()) {
			TransformableDataSource.tryToReplacePlaceholders(this.getExtraObjectDataSource(), schemaInfoSrc);
		}
	}

	@Override
	public EtlItemConfiguration getParentConf() {
		return (EtlItemConfiguration) super.getParentConf();
	}

	@Override
	public Boolean isJoinable() {
		return hasParentItemConf();
	}

	@Override
	public JoinableEntity parseToJoinable() throws ForbiddenOperationException {
		return this;
	}

	@Override
	public List<FieldsMapping> getJoinFields() {
		return this.joinFields;
	}

	@Override
	public TableConfiguration getJoiningEntity() {
		if (!isJoinable()) {
			throw new ForbiddenOperationException("Only a srcConf with a child EtlItemConf can have a joining entity");
		}

		return ((EtlChildItemConfiguration) this.getParentConf()).getParentItemConf().getSrcConf();
	}

	@Override
	public String getJoinExtraCondition() {
		return this.joinExtraCondition;
	}

	@Override
	public JoinType getJoinType() {
		return JoinType.INNER;
	}

	@Override
	public ConditionClauseScope getJoinExtraConditionScope() {
		return this.joinExtraConditionScope;
	}

	@Override
	public void setJoinExtraConditionScope(ConditionClauseScope joinExtraConditionScope) {
		this.joinExtraConditionScope = joinExtraConditionScope;
	}

	@Override
	public void setJoinFields(List<FieldsMapping> joinFields) {
		this.joinFields = joinFields;
	}

	@Override
	public void setJoinType(JoinType joinType) {
	}

	@Override
	public void setJoinExtraCondition(String joinExtraCondition) {
		this.joinExtraCondition = joinExtraCondition;
	}

	@Override
	public void setMainExtractTable(MainJoiningEntity mainJoiningTable) {
	}

	@Override
	public Boolean isMainJoiningEntity() {
		return !isJoinable();
	}

	@Override
	public MainJoiningEntity parseToJoining() throws ForbiddenOperationException {
		return this;
	}

	@Override
	public MainJoiningEntity getMainExtractTable() {
		if (!isJoinable()) {
			throw new ForbiddenOperationException(
					"Only a srcConf with a child EtlItemConf can have a mainExtractTable");
		}

		return getParentConf().getSrcConf();
	}

	@Override
	public String getQuery() {
		String condition = super.generateConditionsFields(null, this.joinFields, this.joinExtraCondition);

		return this.generateSelectFromQuery() + " WHERE " + condition;
	}

	public EtlDataSource findDataSourceOnAllAvaliabeDatasources(String dsName) throws DatabaseResourceDoesNotExists {
		if (dsName.equals(this.getName()) || dsName.equals(this.getTableAlias())) {
			return this;
		}

		if (this.hasAuxExtractTable()) {
			for (JoinableEntity auxExtractTable : this.getJoiningTable()) {
				if (auxExtractTable.doNotUseAsDatasource()) {
					continue;
				}

				if (dsName.equals(auxExtractTable.getName()) || dsName.equals(auxExtractTable.getTableAlias())) {
					return auxExtractTable;
				}

				if (auxExtractTable.isMainJoiningEntity() && auxExtractTable.parseToJoining().hasAuxExtractTable()) {
					for (JoinableEntity innerAuxExtractTable : auxExtractTable.parseToJoining().getJoiningTable()) {
						if (innerAuxExtractTable.doNotUseAsDatasource()) {
							continue;
						}

						if (dsName.equals(innerAuxExtractTable.getName())
								|| dsName.equals(innerAuxExtractTable.getTableAlias())) {
							return innerAuxExtractTable;
						}

					}
				}
			}
		}

		List<EtlAdditionalDataSource> avaliableDataSources = getAvaliableExtraDataSource();

		if (utilities.listHasElement(avaliableDataSources)) {
			for (EtlAdditionalDataSource ds : avaliableDataSources) {
				if (ds.getAlias().equals(dsName)) {
					return ds;
				}
			}
		}

		throw new DatabaseResourceDoesNotExists(dsName);
	}

	@Override
	public List<EtlDatabaseObject> searchRecords(Engine<? extends EtlDatabaseObject> engine,
			EtlDatabaseObject parentSrcObject, List<EtlDatabaseObject> auxDataSourceObjects, Connection srcConn)
			throws DBException {

		EtlDatabaseObjectSearchParams searchParams = new EtlDatabaseObjectSearchParams(this, null);

		return searchParams.search(null, parentSrcObject, auxDataSourceObjects, srcConn, srcConn);
	}

	public void ensureEtlStageTableExists(EtlCounter counter, Connection srcConn, Connection dstConn)
			throws DBException {
		this.fullLoad(srcConn);

		this.createRelatedSrcStageAreaTable(srcConn);

		this.createRelatedStageAreaSrcUniqueKeysTable(srcConn);

		if (this.getRelatedEtlConf().mustCreateArquiveTables()) {
			this.createRelatedProcessedRecordStageAreaTable(srcConn);
		}
	}

	public EtlItemConfiguration getParentItemConf() {
		if (this.hasParentItemConf()) {
			return ((EtlChildItemConfiguration) this.getParentConf()).getParentItemConf();
		}

		return null;
	}

	@Override
	public EtlItemConfiguration getParentEtlItemConf() {
		return this.getParentConf();
	}

	@Override
	public Map<String, Object> retrieveAllAvailableTemplateParameters() {
		Map<String, Object> joined = new HashMap<>();

		Map<String, Object> p2 = getParentConf().retrieveAllAvailableTemplateParameters();

		if (p2 != null) {
			joined.putAll(p2);
		}

		Map<String, Object> p = EtlItemConfigurationComponent.super.retrieveAllAvailableTemplateParameters();

		if (p != null) {
			joined.putAll(p);
		}

		return joined;
	}

	public boolean isTrackabled() {
		return this.containsAllFields(Field.parseFromNames(TRACKING_FIELDS));
	}

	public void fillTrackingFields(EtlDatabaseObject toTrack, EtlStageAreaObject processedRec, Connection srcConn) {

		if (!isTrackabled())
			throw new ForbiddenOperationException("The table " + this + " is not trackable!");

		if (!processedRec.getType().isProcessedRecord()) {
			throw new ForbiddenOperationException("The EtlStageAreaObject processedRec must be of 'PROCESSED_RECORD'");
		}

		for (String trackingName : TRACKING_FIELDS) {
			if (trackingName.equals("retry_count")) {
				toTrack.setFieldValue(trackingName, ((int) processedRec.getFieldValue(trackingName)) + 1);
			} else {
				toTrack.setFieldValue(trackingName, processedRec.getFieldValue(trackingName));
			}
		}
	}

	public void changeObjectStatus(EtlDatabaseObject obj, EtlLoadStatus status, Connection srcConn) {
		if (!isTrackabled())
			throw new ForbiddenOperationException("The table " + this + " is not trackable!");

		obj.setFieldValue("processing_status", status.toString());
	}
}
