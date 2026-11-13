package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.destination.DestinationDataConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDstConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlItemConfigurationComponent;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.ActionOnLoad;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.conf.types.FieldMappingResolutionStrategy;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorWriteMode;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.transformer.DefaultRecordTransformer;
import org.openmrs.module.epts.etl.etl.processor.transformer.EtlRecordTransformer;
import org.openmrs.module.epts.etl.etl.processor.transformer.ParentOnDemandLoadTransformer;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.FieldNotAvaliableInAnyDataSource;
import org.openmrs.module.epts.etl.exceptions.FieldsMappingException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.IncompleteFieldsMappingException;
import org.openmrs.module.epts.etl.exceptions.InvalidDataSourceOnFieldDefifitionException;
import org.openmrs.module.epts.etl.exceptions.RequireJdbcException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JdbcTableDestinationConfiguration extends AbstractTableConfiguration implements EtlDataSource, EtlDstConf,
		EtlTransformTarget, EtlItemConfigurationComponent, DestinationDataConfiguration {

	private Boolean automaticalyGenerated;

	private EtlDstType dstType;

	private Boolean inMemoryTable;

	@JsonIgnore
	private EtlDestinationConfiguration parentDestination;

	public JdbcTableDestinationConfiguration() {
		this.setEtlSide(EtlSide.DST);
	}

	public JdbcTableDestinationConfiguration(String tableName) {
		this();

		setTableName(tableName);
	}

	private EtlDestinationConfiguration logicalDestination() {
		if (parentDestination == null) {
			throw new EtlConfException("JDBC table destination '" + getTableName()
					+ "' is not attached to an EtlDestinationConfiguration");
		}
		return parentDestination;
	}

	public ActionOnEtlIssue getIncompleteMappingBehavior() {
		return logicalDestination().getIncompleteMappingBehavior();
	}

	public void setIncompleteMappingBehavior(ActionOnEtlIssue incompleteMappingBehavior) {
		logicalDestination().setIncompleteMappingBehavior(incompleteMappingBehavior);
	}

	public Boolean getAllowDuplicateDestinationMappings() {
		return logicalDestination().getAllowDuplicateDestinationMappings();
	}

	public void setAllowDuplicateDestinationMappings(Boolean allowDuplicateDestinationMappings) {
		logicalDestination().setAllowDuplicateDestinationMappings(allowDuplicateDestinationMappings);
	}

	@Override
	public Boolean allowDuplicateDestinationMappings() {
		return logicalDestination().allowDuplicateDestinationMappings();
	}

	public ActionOnEtlIssue getOnMissingRequiredSrcObject() {
		return logicalDestination().getOnMissingRequiredSrcObject();
	}

	public void setOnMissingRequiredSrcObject(ActionOnEtlIssue onMissingRequiredSrcObject) {
		logicalDestination().setOnMissingRequiredSrcObject(onMissingRequiredSrcObject);
	}

	public ActionOnEtlIssue onMissingRequiredSrcObject() {

		return logicalDestination().onMissingRequiredSrcObject();
	}

	@Override
	public EtlDatabaseObject getTargetDefaultObject(Connection srcConn, Connection dstConn) throws DBException {
		return logicalDestination().getTargetDefaultObject(srcConn, dstConn);
	}

	public ActionOnEtlIssue getUnmappedFieldBehavior() {
		return logicalDestination().getUnmappedFieldBehavior();
	}

	public void setUnmappedFieldBehavior(ActionOnEtlIssue onMissingMapping) {

		logicalDestination().setUnmappedFieldBehavior(onMissingMapping);
	}

	@Override
	public ActionOnEtlIssue unmappedFieldBehavior() {
		return getUnmappedFieldBehavior();
	}

	@Override
	public String getCondition() {
		return this.applyCondition();
	}

	public void setIgnoreNoDstIssue(Boolean ignoreNoDstIssue) {
		logicalDestination().setIgnoreNoDstIssue(ignoreNoDstIssue);
	}

	public Boolean getIgnoreNoDstIssue() {
		return logicalDestination().getIgnoreNoDstIssue();
	}

	public Boolean ignoreNoDstIssue() {
		return logicalDestination().ignoreNoDstIssue();
	}

	public Boolean getUseAsDataSource() {
		return logicalDestination().getUseAsDataSource();
	}

	public void setUseAsDataSource(Boolean useAsDataSource) {
		logicalDestination().setUseAsDataSource(useAsDataSource);
	}

	@Override
	public Boolean useAsDataSource() {
		return logicalDestination().useAsDataSource();
	}

	public ActionOnEtlIssue onMultipleDataSourceWithSameName() {
		return logicalDestination().onMultipleDataSourceWithSameName();
	}

	public ActionOnEtlIssue getOnMultipleDataSourceWithSameName() {
		return logicalDestination().getOnMultipleDataSourceWithSameName();
	}

	public void setOnMultipleDataSourceWithSameName(ActionOnEtlIssue onMultipleDataSourceWithSameName) {
		logicalDestination().setOnMultipleDataSourceWithSameName(onMultipleDataSourceWithSameName);
	}

	public ActionOnEtlIssue getOnMultipleDataSourceForSameMapping() {
		return logicalDestination().getOnMultipleDataSourceForSameMapping();
	}

	public void setOnMultipleDataSourceForSameMapping(ActionOnEtlIssue onMultipleDataSourceForSameMapping) {
		logicalDestination().setOnMultipleDataSourceForSameMapping(onMultipleDataSourceForSameMapping);
	}

	public ActionOnEtlIssue onMultipleDataSourceForSameMapping() {
		return logicalDestination().onMultipleDataSourceForSameMapping();
	}

	/**
	 * Returns all destination configurations that:
	 * <ul>
	 * <li>belong to the same parent ETL item configuration;</li>
	 * <li>appear before this destination configuration;</li>
	 * <li>are configured to be used as transformation data sources.</li>
	 * </ul>
	 *
	 * <p>
	 * The returned list preserves the declaration order in the ETL configuration,
	 * allowing subsequent destination mappings to reference records produced by
	 * previously executed destination configurations.
	 *
	 * @param conn the database connection (currently unused)
	 * @return all preceding destination configurations that can be used as data
	 *         sources
	 */
	@Override
	public List<EtlTransformTarget> retrievePreviousDataSourceTargets() {
		return logicalDestination().retrievePreviousDataSourceTargets();
	}

	public Boolean isLoadedDataSourceInfo() {
		return logicalDestination().isLoadedDataSourceInfo();
	}

	public Boolean getLoadedDataSourceInfo() {
		return logicalDestination().getLoadedDataSourceInfo();
	}

	public void setLoadedDataSourceInfo(Boolean loadedDataSourceInfo) {
		logicalDestination().setLoadedDataSourceInfo(loadedDataSourceInfo);
	}

	public Boolean isDoNotUseSrcConfAsDataSource() {
		return this.getPrimaryDataSource().doNotUseAsDatasource();
	}

	public Boolean hasParentDstConf() {
		return this.getPrimaryDataSource().getParentConf() instanceof EtlChildItemConfiguration;
	}

	public Boolean hasChildDstConf() {
		return this.getParentConf().hasChildItemConf();
	}

	public String applyCondition() {
		return logicalDestination().applyCondition();
	}

	public String getApplyCondition() {
		return logicalDestination().getApplyCondition();
	}

	public void setSrcObjectCondition(String applyCondition) {
		logicalDestination().setApplyCondition(applyCondition);
	}

	public Boolean isIncludeAllFieldsFromDataSource() {
		return logicalDestination().getIncludeAllFieldsFromDataSource();
	}

	public void setIncludeAllFieldsFromDataSource(Boolean includeAllFieldsFromDataSource) {
		logicalDestination().setIncludeAllFieldsFromDataSource(includeAllFieldsFromDataSource);
	}

	public Boolean includeAllFieldsFromDataSource() {
		return isIncludeAllFieldsFromDataSource();
	}

	public Boolean isInMemoryTable() {
		return isTrue(inMemoryTable);
	}

	public void setInMemoryTable(Boolean inMemoryTable) {
		this.inMemoryTable = inMemoryTable;
	}

	public EtlRecordTransformer getTransformerInstance() {
		return logicalDestination().getTransformerInstance();
	}

	public void setTransformerInstance(EtlRecordTransformer transformerInstance) {
		logicalDestination().setTransformerInstance(transformerInstance);
	}

	public String getTransformer() {
		return logicalDestination().getTransformer();
	}

	public void setTransformer(String transformer) {
		logicalDestination().setTransformer(transformer);
	}

	public Boolean hasTransformer() {
		return getTransformer() != null;
	}

	public EtlDstType getDstType() {
		return dstType;
	}

	public void setDstType(EtlDstType dstType) {
		this.dstType = dstType;
	}

	public List<List<FieldsMapping>> getGeneratedJoinFields() {
		return logicalDestination().getGeneratedJoinFields();
	}

	public void setGeneratedJoinFields(List<List<FieldsMapping>> generatedJoinFields) {
		logicalDestination().setGeneratedJoinFields(generatedJoinFields);
	}

	public Boolean isAutomaticalyGenerated() {
		return isTrue(automaticalyGenerated);
	}

	public void setAutomaticalyGenerated(Boolean automaticalyGenerated) {
		this.automaticalyGenerated = automaticalyGenerated;
	}

	public List<String> getPreferredDataSource() {
		return logicalDestination().getPreferredDataSource();
	}

	public void setPreferredDataSource(List<String> preferredDataSource) {
		logicalDestination().setPreferredDataSource(preferredDataSource);
	}

	public List<FieldsMapping> getJoinFields() {
		return logicalDestination().getJoinFields();
	}

	public void setJoinFields(List<FieldsMapping> joinFields) {
		logicalDestination().setJoinFields(joinFields);
	}

	@Override
	public Boolean isGeneric() {
		return Boolean.FALSE;
	}

	public List<FieldsMapping> getMapping() {
		return logicalDestination().getMapping();
	}

	public void setMapping(List<FieldsMapping> manualFieldsMapping) {
		logicalDestination().setMapping(manualFieldsMapping);
	}

	public List<FieldsMapping> getAllMapping() {
		return logicalDestination().getAllMapping();
	}

	public void setAllMapping(List<FieldsMapping> allMapping) {
		logicalDestination().setAllMapping(allMapping);
	}

	public List<String> getExcludedFields() {
		return logicalDestination().getExcludedFields();
	}

	public void setExcludedFields(List<String> excludedFields) {
		logicalDestination().setExcludedFields(excludedFields);
	}

	@Override
	public List<String> getIgnorableFields() {
		return logicalDestination().getIgnorableFields();
	}

	@Override
	public void setIgnorableFields(List<String> ignorableFields) {
		logicalDestination().setIgnorableFields(ignorableFields);
	}

	@Override
	public List<String> getPatchFields() {
		return logicalDestination().getPatchFields();
	}

	@Override
	public void setPatchFields(List<String> patchFields) {
		logicalDestination().setPatchFields(patchFields);
	}

	public Boolean getDisabled() {
		return logicalDestination().getDisabled();
	}

	@Override
	public void setDisabled(Boolean disabled) {
		logicalDestination().setDisabled(disabled);
	}

	@Override
	public Boolean isDisabled() {
		return logicalDestination().isDisabled();
	}

	public Boolean useDefaultTransformer() {
		return getTransformerInstance() instanceof DefaultRecordTransformer;
	}

	public void setFieldsMappingAlredyGenerated(Boolean fieldsMappingAlredyGenerated) {
		logicalDestination().setFieldsMappingAlreadyGenerated(fieldsMappingAlredyGenerated);
	}

	public Boolean getFieldsMappingAlredyGenerated() {
		return logicalDestination().getFieldsMappingAlreadyGenerated();
	}

	private boolean fieldsMappingAlredyGenerated() {
		return !this.useDefaultTransformer() || isTrue(this.getFieldsMappingAlredyGenerated());
	}

	public FieldMappingResolutionStrategy getMappingResolutionStrategy() {
		return logicalDestination().getMappingResolutionStrategy();
	}

	public void setMappingResolutionStrategy(FieldMappingResolutionStrategy mappingResolutionStrategy) {
		logicalDestination().setMappingResolutionStrategy(mappingResolutionStrategy);
	}

	public DBConnectionInfo getRelatedConnInfo() {
		ConnectorResource resource = getResolvedResource();

		if (resource == null || resource.getEndpoint() == null) {
			throw new RequireJdbcException(
					"The connector resource is not initialized for destination '" + getObjectName() + "'");
		}

		if (resource.getEndpoint().getType() != ConnectorType.JDBC) {
			throw new RequireJdbcException("Destination '" + getObjectName() + "' is not JDBC-backed");
		}
		return resource.getEndpoint().unwrap(DBConnectionInfo.class);
	}

	@Override
	public synchronized void fullLoad() throws DBException {
		OpenConnection conn = this.getRelatedConnInfo().openConnection(this);

		try {
			this.fullLoad(conn);
		} finally {
			finalizeConnection(conn, this);
		}
	}

	private List<EtlDataSource> avaliableDataSources() {
		List<EtlDataSource> dataSource = new ArrayList<>();

		dataSource.add(this.getPrimaryDataSource());

		if (this.getParentConf().getSource().hasAdditionalDataSource()) {
			dataSource.addAll(this.getParentConf().getSource().getAvailableAdditionalDataSources());
		}

		return dataSource;
	}

	@Override
	public synchronized void fullLoad(Connection conn) throws DBException {

		if (!this.isInMemoryTable()) {

			if (!hasManualMapPrimaryKeyOnField()) {
				setManualMapPrimaryKeyOnField(getRelatedEtlConf().getManualMapPrimaryKeyOnField());
			}

			super.fullLoad(conn);
		} else {

			try {
				this.setFieldsLoaded(true);

				loadDataSourceInfo(conn);

				if (this.hasMapping()) {
					this.setFields(
							FieldsMapping.parseAllToField(this.getMapping(), this, avaliableDataSources(), conn));

					if (this.includeAllFieldsFromDataSource()) {

						for (EtlField field : this.getPrimaryDataSource().getEtlFields()) {

							if (!this.containsField(field.getName())) {
								this.getFields().add(field);
								this.addMapping(FieldsMapping.converteFromEtlField(field, this, conn));
							}
						}
					}
				} else {
					this.setFields(EtlField.convertToSimpleFiled(this.getPrimaryDataSource().getEtlFields()));
				}

				if (this.getPrimaryDataSource().hasPK()) {

					if (this.containsAllFields(utilities
							.parseList(this.getPrimaryDataSource().getPrimaryKey().getFields(), Field.class))) {
						this.setPrimaryKey(
								(PrimaryKey) ((PrimaryKey) this.getPrimaryDataSource().getPrimaryKey()).clone());
					}
				}

				this.setPrimaryKeyInfoLoaded(true);

				if (!this.getPrimaryDataSource().getUniqueKeys().isEmpty()) {

					for (UniqueKeyInfo uk : this.getPrimaryDataSource().getUniqueKeys()) {

						if (this.containsAllFields(utilities.parseList(uk.getFields(), Field.class))) {

							if (this.hasUniqueKeys() && !this.getUniqueKeys().contains(uk)) {
								this.addUniqueKey(uk);
							}
						}
					}
				}

				this.setUniqueKeyInfoLoaded(true);

				this.setFullLoaded(true);
			} catch (CloneNotSupportedException e) {
				throw new EtlExceptionImpl(e);
			}

		}

	}

	/**
	 * Find a parent which with same name in src
	 * 
	 * @param dstParent the parent to find the correspondent one in the src
	 * @return the related parent in src
	 * @throws ForbiddenOperationException if the @param dstParent is not a parent
	 *                                     of this table or if it is not in the src
	 */
	public ParentTable findCorrespondentSrcParentConf(ParentTable dstParent) throws ForbiddenOperationException {
		List<ParentTable> parents = findAllRefToParent(dstParent.getTableName());

		if (!utilities.listHasElement(parents)) {
			throw new ForbiddenOperationException(
					"The table " + dstParent.getTableName() + " is not parent of " + this.getTableName());
		}

		parents = getPrimaryDataSource().requireJdbc().findAllRefToParent(dstParent.getTableName());

		if (!utilities.listHasElement(parents)) {
			throw new ForbiddenOperationException("The table " + dstParent.getTableName() + " is not parent of "
					+ this.getTableName() + " in the src");
		}

		return parents.get(0);
	}

	@Override
	public void loadOwnElements(EtlDatabaseObject schemaInfo, Connection conn) throws DBException {
		super.loadOwnElements(schemaInfo, conn);
	}

	@Override
	public List<EtlDataSource> getAllPrefferredDataSource() {
		return logicalDestination().getAllPrefferredDataSource();
	}

	@Override
	public List<EtlDataSource> getAllNotPrefferredDataSource() {
		return logicalDestination().getAllNotPrefferredDataSource();
	}

	public Boolean useSrcConfAsDataSource() {
		return !this.isDoNotUseSrcConfAsDataSource();
	}

	@Override
	public void loadDataSourceInfo(Connection conn) throws DBException {
		logicalDestination().loadDataSourceInfo(conn);
	}

	public List<EtlDataSource> getAllAvaliableDataSource() {
		return logicalDestination().getAllAvaliableDataSource();
	}

	public PrimaryDataSourceConfiguration getPrimaryDataSource() {
		return this.getParentConf().getPrimaryDataSource();
	}

	@Override
	public void setParentConf(EtlDataConfiguration parent) {
		super.setParentConf((EtlItemConfiguration) parent);
	}

	@Override
	public EtlItemConfiguration getParentConf() {
		return (EtlItemConfiguration) super.getParentConf();
	}

	/**
	 * Generates SQL join condition between this destination table and its src table
	 * using the {@link #joinField}
	 * 
	 * @param sourceTableAlias      alias name for source table
	 * @param destinationTableAlias alias name for destination table
	 * @return the generated join condition based on {@link #joinField}
	 */
	@JsonIgnore
	public String generateJoinConditionWithSrc() {
		String fullCondition = "";

		String ownAlias = getTableAlias();
		String relatedTableAlias = getPrimaryDataSource().requireJdbc().getTableAlias();

		for (int outerConter = 0; outerConter < this.getGeneratedJoinFields().size(); outerConter++) {
			List<FieldsMapping> joindFields = this.getGeneratedJoinFields().get(outerConter);

			String currJoinCondition = "";

			for (int innerCounter = 0; innerCounter < joindFields.size(); innerCounter++) {

				if (innerCounter > 0)
					currJoinCondition += " AND ";

				// Force the alias to be from the sharedPk table
				if (useSharedPKKey() && !containsField(joindFields.get(innerCounter).getDstField())) {
					ownAlias = getSharedKeyRefInfo(null).getAlias();
				}

				if (getPrimaryDataSource().requireJdbc().useSharedPKKey()
						&& !getPrimaryDataSource().containsField(joindFields.get(innerCounter).getSrcField())) {
					relatedTableAlias = getPrimaryDataSource().requireJdbc().getSharedKeyRefInfo(null).getAlias();
				}

				currJoinCondition += ownAlias + "." + joindFields.get(innerCounter).getDstField() + " = "
						+ relatedTableAlias + "." + joindFields.get(innerCounter).getSrcField();
			}

			if (outerConter > 0) {
				fullCondition = "(" + fullCondition + ") OR (" + currJoinCondition + ")";
			} else {
				fullCondition = "(" + currJoinCondition + ")";
			}

		}

		if (!utilities.stringHasValue(fullCondition) && this.isMetadata()) {
			fullCondition = getTableAlias() + "." + getPrimaryKey().retrieveSimpleKeyColumnName() + " = "
					+ getPrimaryDataSource().requireJdbc().getTableAlias() + "."
					+ getPrimaryKey().retrieveSimpleKeyColumnName();
		}

		return fullCondition;
	}

	public Boolean hasJoinFields() {
		return utilities.listHasElement(this.getGeneratedJoinFields());
	}

	public static List<JdbcTableDestinationConfiguration> generateDefaultDstConf(final EtlItemConfiguration itemConf)
			throws DBException {

		synchronized (itemConf) {

			OpenConnection dstConn;

			if (!itemConf.hasDestinations()) {

				dstConn = itemConf.getRelatedEtlConf().openDstConn(itemConf);

				try {
					return createDefaultDstConf(itemConf, dstConn.getSchema(),
							itemConf.getRelatedEtlConf().getDstConnInfo());
				} catch (SQLException e) {
					throw new DBException(e);
				} finally {
					dstConn.finalizeConnection(itemConf);
				}
			} else {
				return null;
			}
		}
	}

	public static List<JdbcTableDestinationConfiguration> generateDefaultDstConf(final EtlItemConfiguration itemConf,
			DBConnectionInfo connectionInfo) {

		synchronized (itemConf) {
			return itemConf.hasDestinations() ? null
					: createDefaultDstConf(itemConf, connectionInfo.getSchema(), connectionInfo);
		}
	}

	private static List<JdbcTableDestinationConfiguration> createDefaultDstConf(EtlItemConfiguration itemConf,
			String schema, DBConnectionInfo connectionInfo) {
		var sourceTable = itemConf.getPrimaryDataSource().requireJdbc();
		JdbcTableDestinationConfiguration map = new JdbcTableDestinationConfiguration();
		map.setTableName(sourceTable.getTableName());
		map.setSchema(schema);
		map.setDstType(sourceTable.getDstType());
		map.setObservationDateFields(sourceTable.getObservationDateFields());
		map.setRemoveForbidden(sourceTable.isRemoveForbidden());
		map.setOnConflict(sourceTable.onConflict());
		map.setWinningRecordFieldsInfo(sourceTable.getWinningRecordFieldsInfo());
		map.setManualMapPrimaryKeyOnField(sourceTable.getManualMapPrimaryKeyOnField());
		map.setAutomaticalyGenerated(true);
		map.setParentConf(itemConf);
		return utilities.parseToList(map);
	}

	public Boolean hasMapping() {
		return utilities.listHasElement(this.getMapping());
	}

	public static List<JdbcTableDestinationConfiguration> cloneAll(
			List<JdbcTableDestinationConfiguration> allToCloneFrom, EtlItemConfiguration relatedItemConf,
			EtlDatabaseObject schemaInfoSrc, Connection conn) throws DBException {

		List<JdbcTableDestinationConfiguration> allCloned = null;

		if (utilities.listHasElement(allToCloneFrom)) {
			allCloned = new ArrayList<>(allToCloneFrom.size());

			for (JdbcTableDestinationConfiguration aux : allToCloneFrom) {
				JdbcTableDestinationConfiguration cloned = new JdbcTableDestinationConfiguration();
				cloned.setIgnoreMissingParameters(true);
				cloned.clone(aux, relatedItemConf, schemaInfoSrc, conn);

				allCloned.add(cloned);
			}
		}

		return allCloned;
	}

	public void clone(JdbcTableDestinationConfiguration toCloneFrom, EtlItemConfiguration relatedItemConf,
			EtlDatabaseObject schemaInfoSrc, Connection conn) throws DBException {

		super.clone(toCloneFrom, relatedItemConf, schemaInfoSrc, conn);
		this.setDstType(toCloneFrom.getDstType());
	}

	@Override
	public void tryToReplacePlaceholdersOnOwnElements(EtlDatabaseObject schemaInfoSrc) {
		// Transformation placeholders belong to EtlDestinationConfiguration.
	}

	@Override
	public String getName() {
		return this.getTableAlias() != null ? this.getTableAlias() : this.getTableName();
	}

	@Override
	public String getQuery() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		boolean se = super.equals(obj);

		if (obj instanceof JdbcTableDestinationConfiguration && se) {
			JdbcTableDestinationConfiguration thisDstConf = (JdbcTableDestinationConfiguration) this;
			JdbcTableDestinationConfiguration otherDstConf = (JdbcTableDestinationConfiguration) obj;

			if (thisDstConf.hasAlias()) {
				return thisDstConf.getTableAlias().equals(otherDstConf.getTableAlias());
			} else if (otherDstConf.hasAlias()) {
				return otherDstConf.getTableAlias().equals(thisDstConf.getTableAlias());
			}

			if (thisDstConf.hasCondition()) {
				return thisDstConf.applyCondition().equals(otherDstConf.applyCondition());
			}

			if (otherDstConf.hasCondition()) {
				return otherDstConf.applyCondition().equals(thisDstConf.applyCondition());
			}
		}

		return se;
	}

	public void init(EtlItemConfiguration relatedItemConf, Connection srcConn, Connection dstConn) throws DBException {
		if (isInitialized())
			return;

		synchronized (LOCK) {
			if (isInitialized())
				return;

			if (!isInMemoryTable()) {
				super.init(relatedItemConf, relatedItemConf.getRelatedEtlSchemaObject(), srcConn, dstConn);

				if (this.hasAlias()) {
					this.setUsingManualDefinedAlias(true);

					this.getRelatedEtlConf().tryToAddToBusyTableAliasName(this.getTableAlias());
				}

				getRelatedEtlConf().addConfiguredTable(this);
				setInitialized(true);
			}
		}
	}

	public void ensureEtlStageTableExists(EtlCounter counter, Connection srcConn,
			DestinationConnectorConnections destinationConnections) throws DBException {
		Connection dstConn = destinationConnections.getIfJdbc(this);
		this.fullLoad(dstConn);
		this.createRelatedDstSyncStage(srcConn);
		this.createRelatedSyncStageAreaDstUniqueKeysTable(srcConn);

		if (hasMapping()) {

			for (FieldsMapping map : this.getMapping()) {

				if (!map.hasTransformer()) {
					continue;
				}
				map.tryToLoadTransformer(logicalDestination());
				map.getTransformerInstance().init();
				map.getTransformerInstance().determineTransformerType();

				if (map.getTransformerType().isParentOnDemand()
						|| map.getTransformerType().isParentOnDemandWithDefaults()) {
					ParentOnDemandLoadTransformer onDemand = (ParentOnDemandLoadTransformer) map
							.getTransformerInstance();

					if (onDemand.getExistingParentItemConf() != null) {
						onDemand.getExistingParentItemConf().ensureEtlStageTableExists(counter,
								getParentConf().getRelatedEtlConf().getDefaultOperation(), srcConn);
					}

					EtlItemConfiguration onDemandEtlConf = onDemand.getOnDemandCreateParentItemConf();
					EtlOperationConfig defaultOperation = getParentConf().getRelatedEtlConf().getDefaultOperation();
					onDemandEtlConf.ensureEtlStageTableExists(counter, defaultOperation, srcConn);
				}
			}
		}
	}

	@Override
	public void setAllNotPrefferredDataSource(List<EtlDataSource> ds) {
		logicalDestination().setAllNotPrefferredDataSource(ds);
	}

	@Override
	public void setAllAvaliableDataSource(List<EtlDataSource> ds) {
		logicalDestination().setAllAvaliableDataSource(ds);
	}

	@Override
	public void setAllPrefferredDataSource(List<EtlDataSource> ds) {
		logicalDestination().setAllPrefferredDataSource(ds);
	}

	@Override
	public EtlItemConfiguration getParentEtlItemConf() {
		return this.getParentConf();
	}

	@Override
	public Map<String, Object> retrieveAllAvailableTemplateParameters() {
		return EtlItemConfigurationComponent.super.retrieveAllAvailableTemplateParameters();
	}

	@Override
	public void tryToLoadSchemaInfo(EtlDatabaseObject schemaInfoSrc, Connection conn)
			throws DBException, ForbiddenOperationException, DatabaseResourceDoesNotExists {

		if (!isInMemoryTable()) {
			super.tryToLoadSchemaInfo(schemaInfoSrc, conn);
		}
	}

	@Override
	public FieldMappingResolutionStrategy mappingResolutionStrategy() {
		return logicalDestination().mappingResolutionStrategy();
	}

	@Override
	public String toString() {
		String str = super.toString();

		str += hasCondition() ? ", [applyCondition: " + this.getCondition() + "]" : "";

		return str;
	}

	public boolean executeUpdateOnIdentifiedRecordAction() {
		return logicalDestination().executeUpdateOnIdentifiedRecordAction();
	}

	public boolean executePatchUpdateOnIdentifiedRecordAction() {
		return logicalDestination().executePatchUpdateOnIdentifiedRecordAction();
	}

	public ActionOnLoad getIdentifiedRecordAction() {
		return logicalDestination().getIdentifiedRecordAction();
	}

	public void setIdentifiedRecordAction(ActionOnLoad identifiedRecordAction) {
		logicalDestination().setIdentifiedRecordAction(identifiedRecordAction);
	}

	public EtlDataSource findParentDataSource(ParentTable ref) {
		return logicalDestination().findParentDataSource(ref);
	}

	public boolean hasActionWhenRecordIdIsSet() {
		return this.getIdentifiedRecordAction() != null;
	}

	@Override
	public EtlDestinationConfiguration getParentDestination() {
		return this.parentDestination;
	}

	@Override
	public void setParentDestination(EtlDestinationConfiguration parentDestination) {
		this.parentDestination = parentDestination;
	}

	@Override
	public Set<ConnectorWriteMode> getSupportedWriteModes() {
		return EnumSet.allOf(ConnectorWriteMode.class);
	}
}
