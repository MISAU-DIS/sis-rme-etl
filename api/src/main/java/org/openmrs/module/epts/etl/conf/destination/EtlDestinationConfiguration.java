package org.openmrs.module.epts.etl.conf.destination;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.datasource.PreparedQuery;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.ConditionalEtlElement;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDstConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlItemConfigurationComponent;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.ActionOnLoad;
import org.openmrs.module.epts.etl.conf.types.FieldMappingResolutionStrategy;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.transformer.DefaultRecordTransformer;
import org.openmrs.module.epts.etl.etl.processor.transformer.EtlRecordTransformer;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.InvalidDataSourceOnFieldDefifitionException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectLoaderHelper;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlObjectConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Connector-neutral aggregate for one ETL destination. It is a complete logical
 * transformation target and may optionally infer one physical adapter.
 */
public class EtlDestinationConfiguration extends AbstractEtlDataConfiguration
		implements EtlTransformTarget, EtlDataSource, EtlDstConf, EtlItemConfigurationComponent, ConditionalEtlElement {

	private ConnectorResourceConfiguration resource;
	private JdbcTableDestinationConfiguration tableDataDestination;
	private JsonDestinationConfiguration jsonDataDestination;
	private CsvDestinationConfiguration csvDataDestination;
	private List<Field> fields;
	private PrimaryKey primaryKey;

	private List<FieldsMapping> joinFields;
	private List<List<FieldsMapping>> generatedJoinFields;
	private List<FieldsMapping> mapping;
	private List<FieldsMapping> allMapping;
	private List<String> excludedFields;
	private List<String> ignorableFields;
	private List<String> preferredDataSource;
	private ActionOnEtlIssue unmappedFieldBehavior;
	private String transformer;
	private Boolean includeAllFieldsFromDataSource;
	private String applyCondition;
	private ActionOnEtlIssue onMultipleDataSourceForSameMapping;
	private ActionOnEtlIssue onMultipleDataSourceWithSameName;
	private Boolean useAsDataSource;
	private Boolean ignoreNoDstIssue;
	private FieldMappingResolutionStrategy mappingResolutionStrategy;
	private ActionOnEtlIssue onMissingRequiredSrcObject;
	private Boolean allowDuplicateDestinationMappings;
	private ActionOnLoad identifiedRecordAction;
	private ActionOnEtlIssue incompleteMappingBehavior;
	private Boolean disabled;
	private PreparedQuery defaultPreparedQuery;
	private Boolean loadedDataSourceInfo;
	private Boolean fullLoaded;
	private Boolean mustLoadChildrenInfo;
	private Boolean fieldsMappingAlreadyGenerated;

	@JsonIgnore
	private transient Class<? extends EtlDatabaseObject> etlRecordClass;

	@JsonIgnore
	private transient DatabaseObjectLoaderHelper loadHelper;

	@JsonIgnore
	private transient EtlRecordTransformer transformerInstance;

	@JsonIgnore
	private transient List<EtlDataSource> allAvailableDataSource;

	@JsonIgnore
	private transient List<EtlDataSource> allPreferredDataSource;

	@JsonIgnore
	private transient List<EtlDataSource> allNotPreferredDataSource;

	@JsonIgnore
	private transient DestinationDataConfiguration destinationDataConfiguration;

	@JsonIgnore
	private transient ConnectorResource resolvedResource;

	@JsonIgnore
	private transient EtlItemConfiguration parentItem;

	public EtlDestinationConfiguration() {
		this.onMultipleDataSourceForSameMapping = ActionOnEtlIssue.ABORT_PROCESS;
		this.onMultipleDataSourceWithSameName = ActionOnEtlIssue.ABORT_PROCESS;
		this.useAsDataSource = Boolean.FALSE;
	}

	public static List<EtlDestinationConfiguration> copyAll(List<EtlDestinationConfiguration> destinations) {
		if (destinations == null) {
			return null;
		}
		List<EtlDestinationConfiguration> copies = new ArrayList<>(destinations.size());
		for (EtlDestinationConfiguration destination : destinations) {
			try {
				String json = new ObjectMapperProvider().getContext(EtlDestinationConfiguration.class)
						.writeValueAsString(destination);
				copies.add(new ObjectMapperProvider().getContext(EtlDestinationConfiguration.class)
						.readValue(json, EtlDestinationConfiguration.class));
			} catch (java.io.IOException exception) {
				throw new EtlConfException("Could not copy destination configuration", exception);
			}
		}
		return copies;
	}

	@JsonIgnore
	public String getConnector() {
		return resource == null ? null : resource.getConnector();
	}

	@JsonProperty("connector")
	private void rejectRootConnector(String ignored) {
		throw new EtlConfException("destination.connector is not supported; use destination.resource.connector");
	}

	public ConnectorResourceConfiguration getResource() {
		return resource;
	}

	public void setResource(ConnectorResourceConfiguration resource) {
		this.resource = resource;
	}

	public JdbcTableDestinationConfiguration getTableDataDestination() {
		return tableDataDestination;
	}

	@JsonIgnore
	public JdbcTableDestinationConfiguration requireJdbcTableDestination() {
		if (tableDataDestination == null) {
			throw new ForbiddenOperationException("Destination '" + getName() + "' is not JDBC-backed");
		}
		return tableDataDestination;
	}

	public void setTableDataDestination(JdbcTableDestinationConfiguration tableDataDestination) {
		this.tableDataDestination = tableDataDestination;
	}

	public JsonDestinationConfiguration getJsonDataDestination() {
		return jsonDataDestination;
	}

	public void setJsonDataDestination(JsonDestinationConfiguration jsonDataDestination) {
		this.jsonDataDestination = jsonDataDestination;
	}

	public CsvDestinationConfiguration getCsvDataDestination() {
		return csvDataDestination;
	}

	public void setCsvDataDestination(CsvDestinationConfiguration csvDataDestination) {
		this.csvDataDestination = csvDataDestination;
	}

	@Override
	@JsonIgnore
	public String getName() {
		return resource == null ? null : resource.getName();
	}

	@JsonProperty("name")
	private void rejectRootName(String ignored) {
		throw new EtlConfException("destination.name is not supported; use destination.resource.name");
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Field> List<T> getFields() {
		return (List<T>) fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	@Override
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<FieldsMapping> getJoinFields() {
		return joinFields;
	}

	public void setJoinFields(List<FieldsMapping> joinFields) {
		this.joinFields = joinFields;
	}

	public List<List<FieldsMapping>> getGeneratedJoinFields() {
		return generatedJoinFields;
	}

	public void setGeneratedJoinFields(List<List<FieldsMapping>> generatedJoinFields) {
		this.generatedJoinFields = generatedJoinFields;
	}

	public List<FieldsMapping> getMapping() {
		return mapping;
	}

	@Override
	public void setMapping(List<FieldsMapping> mapping) {
		this.mapping = mapping;
	}

	@Override
	public List<FieldsMapping> getAllMapping() {
		return allMapping;
	}

	@Override
	public void setAllMapping(List<FieldsMapping> allMapping) {
		this.allMapping = allMapping;
	}

	public List<String> getExcludedFields() {
		return excludedFields;
	}

	public void setExcludedFields(List<String> excludedFields) {
		this.excludedFields = excludedFields;
	}

	public List<String> getIgnorableFields() {
		return ignorableFields;
	}

	public void setIgnorableFields(List<String> ignorableFields) {
		this.ignorableFields = ignorableFields;
	}

	public List<String> getPreferredDataSource() {
		return preferredDataSource;
	}

	public void setPreferredDataSource(List<String> preferredDataSource) {
		this.preferredDataSource = preferredDataSource;
	}

	public ActionOnEtlIssue getUnmappedFieldBehavior() {
		return unmappedFieldBehavior;
	}

	public void setUnmappedFieldBehavior(ActionOnEtlIssue unmappedFieldBehavior) {

		if (unmappedFieldBehavior != null && !unmappedFieldBehavior.allowedOnMissingFieldsMapping()) {
			throw new EtlConfException("Value not allowed unmappedFieldBehavior [" + unmappedFieldBehavior + "]");
		}

		this.unmappedFieldBehavior = unmappedFieldBehavior;
	}

	@Override
	public ActionOnEtlIssue unmappedFieldBehavior() {
		return getUnmappedFieldBehavior();
	}

	public String getTransformer() {
		return transformer;
	}

	public void setTransformer(String transformer) {
		this.transformer = transformer;
	}

	@JsonIgnore
	public EtlRecordTransformer getTransformerInstance() {
		return transformerInstance;
	}

	@JsonIgnore
	public void setTransformerInstance(EtlRecordTransformer transformerInstance) {
		this.transformerInstance = transformerInstance;
	}

	public Boolean hasTransformer() {
		return utilities.stringHasValue(transformer);
	}

	public Boolean useDefaultTransformer() {
		return transformerInstance instanceof DefaultRecordTransformer;
	}

	public Boolean getIncludeAllFieldsFromDataSource() {
		return isTrue(includeAllFieldsFromDataSource);
	}

	public void setIncludeAllFieldsFromDataSource(Boolean includeAllFieldsFromDataSource) {
		this.includeAllFieldsFromDataSource = includeAllFieldsFromDataSource;
	}

	public Boolean includeAllFieldsFromDataSource() {
		return getIncludeAllFieldsFromDataSource();
	}

	public String getApplyCondition() {
		return applyCondition;
	}

	public void setApplyCondition(String applyCondition) {
		this.applyCondition = applyCondition;
	}

	public void setSrcObjectCondition(String applyCondition) {
		setApplyCondition(applyCondition);
	}

	@Override
	public String applyCondition() {
		return applyCondition;
	}

	@Override
	public String getCondition() {
		return applyCondition();
	}

	public ActionOnEtlIssue getOnMultipleDataSourceForSameMapping() {
		return onMultipleDataSourceForSameMapping;
	}

	public void setOnMultipleDataSourceForSameMapping(ActionOnEtlIssue behavior) {
		this.onMultipleDataSourceForSameMapping = behavior;
	}

	@Override
	public ActionOnEtlIssue onMultipleDataSourceForSameMapping() {
		return onMultipleDataSourceForSameMapping;
	}

	public ActionOnEtlIssue getOnMultipleDataSourceWithSameName() {
		return onMultipleDataSourceWithSameName;
	}

	public void setOnMultipleDataSourceWithSameName(ActionOnEtlIssue behavior) {
		this.onMultipleDataSourceWithSameName = behavior;
	}

	public ActionOnEtlIssue onMultipleDataSourceWithSameName() {
		return onMultipleDataSourceWithSameName;
	}

	public Boolean getUseAsDataSource() {
		return useAsDataSource;
	}

	public void setUseAsDataSource(Boolean useAsDataSource) {
		this.useAsDataSource = useAsDataSource;
	}

	@Override
	public Boolean useAsDataSource() {
		return isTrue(useAsDataSource);
	}

	public Boolean getIgnoreNoDstIssue() {
		return ignoreNoDstIssue;
	}

	public void setIgnoreNoDstIssue(Boolean ignoreNoDstIssue) {
		this.ignoreNoDstIssue = ignoreNoDstIssue;
	}

	public Boolean ignoreNoDstIssue() {
		return isTrue(ignoreNoDstIssue);
	}

	public FieldMappingResolutionStrategy getMappingResolutionStrategy() {
		return mappingResolutionStrategy;
	}

	public void setMappingResolutionStrategy(FieldMappingResolutionStrategy mappingResolutionStrategy) {
		this.mappingResolutionStrategy = mappingResolutionStrategy;
	}

	@Override
	public FieldMappingResolutionStrategy mappingResolutionStrategy() {
		return mappingResolutionStrategy;
	}

	public ActionOnEtlIssue getOnMissingRequiredSrcObject() {
		return onMissingRequiredSrcObject;
	}

	public void setOnMissingRequiredSrcObject(ActionOnEtlIssue onMissingRequiredSrcObject) {
		this.onMissingRequiredSrcObject = onMissingRequiredSrcObject;
	}

	public ActionOnEtlIssue onMissingRequiredSrcObject() {
		return onMissingRequiredSrcObject != null ? onMissingRequiredSrcObject
				: getRelatedEtlConf().getGeneralBehaviourOnEtlException();
	}

	public Boolean getAllowDuplicateDestinationMappings() {
		return allowDuplicateDestinationMappings;
	}

	public void setAllowDuplicateDestinationMappings(Boolean allowDuplicateDestinationMappings) {
		this.allowDuplicateDestinationMappings = allowDuplicateDestinationMappings;
	}

	@Override
	public Boolean allowDuplicateDestinationMappings() {
		return isTrue(allowDuplicateDestinationMappings);
	}

	public ActionOnLoad getIdentifiedRecordAction() {
		return identifiedRecordAction;
	}

	public void setIdentifiedRecordAction(ActionOnLoad identifiedRecordAction) {
		this.identifiedRecordAction = identifiedRecordAction;
	}

	public boolean executeUpdateOnIdentifiedRecordAction() {
		return identifiedRecordAction == ActionOnLoad.UPDATE;
	}

	public boolean executePatchUpdateOnIdentifiedRecordAction() {
		return identifiedRecordAction == ActionOnLoad.PATCH_UPDATE;
	}

	public boolean hasActionWhenRecordIdIsSet() {
		return identifiedRecordAction != null;
	}

	@Override
	public ActionOnEtlIssue getIncompleteMappingBehavior() {
		return incompleteMappingBehavior;
	}

	public void setIncompleteMappingBehavior(ActionOnEtlIssue incompleteMappingBehavior) {
		this.incompleteMappingBehavior = incompleteMappingBehavior;
	}

	@JsonIgnore
	public List<EtlDataSource> getAllAvailableDataSource() {
		return allAvailableDataSource;
	}

	@JsonIgnore
	public void setAllAvailableDataSource(List<EtlDataSource> allAvailableDataSource) {
		this.allAvailableDataSource = allAvailableDataSource;
	}

	@JsonIgnore
	public List<EtlDataSource> getAllPreferredDataSource() {
		return allPreferredDataSource;
	}

	@JsonIgnore
	public void setAllPreferredDataSource(List<EtlDataSource> allPreferredDataSource) {
		this.allPreferredDataSource = allPreferredDataSource;
	}

	@JsonIgnore
	public List<EtlDataSource> getAllNotPreferredDataSource() {
		return allNotPreferredDataSource;
	}

	@JsonIgnore
	public void setAllNotPreferredDataSource(List<EtlDataSource> allNotPreferredDataSource) {
		this.allNotPreferredDataSource = allNotPreferredDataSource;
	}

	@Override
	public List<EtlDataSource> getAllPrefferredDataSource() {
		return allPreferredDataSource;
	}

	@Override
	public void setAllPrefferredDataSource(List<EtlDataSource> dataSources) {
		this.allPreferredDataSource = dataSources;
	}

	@Override
	public List<EtlDataSource> getAllNotPrefferredDataSource() {
		return allNotPreferredDataSource;
	}

	@Override
	public void setAllNotPrefferredDataSource(List<EtlDataSource> dataSources) {
		this.allNotPreferredDataSource = dataSources;
	}

	@Override
	public List<EtlDataSource> getAllAvaliableDataSource() {
		return allAvailableDataSource;
	}

	@Override
	public void setAllAvaliableDataSource(List<EtlDataSource> dataSources) {
		this.allAvailableDataSource = dataSources;
	}

	@JsonIgnore
	public DestinationDataConfiguration getDestinationDataConfiguration() {

		if (destinationDataConfiguration == null) {
			throw new IllegalStateException("This destination does not define a physical adapter");
		}
		return destinationDataConfiguration;
	}

	@JsonIgnore
	public boolean hasDestinationDataConfiguration() {
		return destinationDataConfiguration != null;
	}

	@Override
	@JsonIgnore
	public List<UniqueKeyInfo> getUniqueKeys() {
		return destinationDataConfiguration == null ? java.util.Collections.emptyList()
				: destinationDataConfiguration.getUniqueKeys();
	}

	@Override
	@JsonIgnore
	public List<ParentTable> getParentRefInfo() {
		return destinationDataConfiguration == null ? java.util.Collections.emptyList()
				: destinationDataConfiguration.getParentRefInfo();
	}

	@Override
	@JsonIgnore
	public List<ChildTable> getChildRefInfo() {
		return destinationDataConfiguration == null ? java.util.Collections.emptyList()
				: destinationDataConfiguration.getChildRefInfo();
	}

	@Override
	@JsonIgnore
	public String getSharePkWith() {
		return destinationDataConfiguration == null ? null : destinationDataConfiguration.getSharePkWith();
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean isDisabled() {
		return isTrue(disabled);
	}

	public Boolean isDoNotUseSrcConfAsDataSource() {
		return getPrimaryDataSource() != null && getPrimaryDataSource().doNotUseAsDatasource();
	}

	public Boolean useSrcConfAsDataSource() {
		return !isDoNotUseSrcConfAsDataSource();
	}

	@Override
	public PrimaryDataSourceConfiguration getPrimaryDataSource() {
		return parentItem == null ? null : parentItem.getPrimaryDataSource();
	}

	@Override
	public void loadDataSourceInfo(Connection conn) throws DBException {

		if (isLoadedDataSourceInfo()) {
			return;
		}

		if (parentItem == null) {
			throw new EtlConfException("Destination must be attached to an ETL item before loading data sources");
		}

		addAllToAvaliableDataSource(parentItem.collectAllAvaliableDataSources(conn));

		if (useAsDataSource()) {
			addToAvaliableDataSource(this);
		}

		determinePreferredDataSources();

		setLoadedDataSourceInfo(Boolean.TRUE);
	}

	@Override
	public Boolean isLoadedDataSourceInfo() {
		return isTrue(loadedDataSourceInfo);
	}

	public Boolean getLoadedDataSourceInfo() {
		return loadedDataSourceInfo;
	}

	public void setLoadedDataSourceInfo(Boolean loadedDataSourceInfo) {
		this.loadedDataSourceInfo = loadedDataSourceInfo;
	}

	@Override
	public Boolean isFullLoaded() {
		EtlObjectConfiguration physical = physicalObjectConfiguration();
		return physical != null ? physical.isFullLoaded() : isTrue(fullLoaded);
	}

	@Override
	public void fullLoad() throws DBException {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		if (physical != null) {
			physical.fullLoad();
			synchronizePhysicalFields(physical);
			synchronizePhysicalPrimaryKey(physical);
		}

		loadTransformationElements(null);
		fullLoaded = Boolean.TRUE;
	}

	@Override
	public void fullLoad(Connection conn) throws DBException {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		if (physical != null) {
			physical.fullLoad(conn);
			synchronizePhysicalFields(physical);
			synchronizePhysicalPrimaryKey(physical);
		}

		loadTransformationElements(conn);

		fullLoaded = Boolean.TRUE;
	}

	@Override
	public Boolean isAutoIncrementId() {
		return physicalObjectConfiguration() instanceof TableConfiguration
				? ((TableConfiguration) physicalObjectConfiguration()).isAutoIncrementId()
				: Boolean.FALSE;
	}

	@Override
	public EtlDatabaseObject getTargetDefaultObject(Connection srcConn, Connection dstConn) throws DBException {
		return physicalObjectConfiguration() instanceof EtlTransformTarget
				? ((EtlTransformTarget) physicalObjectConfiguration()).getTargetDefaultObject(srcConn, dstConn)
				: null;
	}

	@Override
	public PreparedQuery getDefaultPreparedQuery() {
		return defaultPreparedQuery;
	}

	@Override
	public void setDefaultPreparedQuery(PreparedQuery defaultPreparedQuery) {
		this.defaultPreparedQuery = defaultPreparedQuery;
	}

	@Override
	public String getQuery() {
		return physicalObjectConfiguration() instanceof EtlDataSource
				? ((EtlDataSource) physicalObjectConfiguration()).getQuery()
				: null;
	}

	@Override
	public void setParentConf(EtlDataConfiguration parent) {

		if (!(parent instanceof EtlItemConfiguration)) {
			throw new EtlConfException("A destination must belong to an EtlItemConfiguration");
		}
		this.parentItem = (EtlItemConfiguration) parent;
	}

	@Override
	public EtlItemConfiguration getParentEtlItemConf() {
		return parentItem;
	}

	@Override
	public void tryToLoadSchemaInfo(EtlDatabaseObject schemaInfoSrc, Connection conn) throws DBException {

		if (physicalObjectConfiguration() instanceof EtlDataSource) {
			((EtlDataSource) physicalObjectConfiguration()).tryToLoadSchemaInfo(schemaInfoSrc, conn);
		} else {
			fullLoaded = Boolean.TRUE;
		}
	}

	private EtlObjectConfiguration physicalObjectConfiguration() {
		return destinationDataConfiguration instanceof EtlObjectConfiguration
				? (EtlObjectConfiguration) destinationDataConfiguration
				: null;
	}

	private void synchronizePhysicalFields(EtlObjectConfiguration physical) {

		if (fields == null && physical != null && physical.getFields() != null) {
			this.fields = new ArrayList<>(physical.getFields());
		}
	}

	private void synchronizePhysicalPrimaryKey(EtlObjectConfiguration physical) {

		if (primaryKey == null && physical != null && physical.getPrimaryKey() instanceof PrimaryKey) {
			this.primaryKey = (PrimaryKey) physical.getPrimaryKey();
		}
	}

	private void loadTransformationElements(Connection conn) throws DBException {

		if (!hasUnmappedFieldBehavior()) {
			setUnmappedFieldBehavior(ActionOnEtlIssue.ABORT_PROCESS);
		}

		if (mappingResolutionStrategy == null) {
			setMappingResolutionStrategy(FieldMappingResolutionStrategy.MANUAL_THEN_AUTO);
		}

		if (hasIncompleteMappingBehavior()) {
			getIncompleteMappingBehavior().validateOnIncompleteMapping("incompleteMappingBehavior");
		}

		loadDataSourceInfo(conn);
		loadTransformer();
		loadConfiguredJoinFields();
		generateAllFieldsMapping(conn);
	}

	private void loadConfiguredJoinFields() {

		if (!utilities.listHasElement(joinFields) || utilities.listHasElement(generatedJoinFields)) {
			return;
		}
		generatedJoinFields = new ArrayList<>();
		generatedJoinFields.add(new ArrayList<>(joinFields));
	}

	@SuppressWarnings("unchecked")
	private void loadTransformer() {

		if (!hasTransformer() || DefaultRecordTransformer.class.getCanonicalName().equals(transformer)) {
			setTransformer(DefaultRecordTransformer.class.getCanonicalName());
			setTransformerInstance(DefaultRecordTransformer.getInstance());
			return;
		}

		try {
			ClassLoader loader = EtlRecordTransformer.class.getClassLoader();
			Class<? extends EtlRecordTransformer> transformerClass = (Class<? extends EtlRecordTransformer>) loader
					.loadClass(transformer);
			setTransformerInstance(transformerClass.getDeclaredConstructor().newInstance());
		} catch (ReflectiveOperationException exception) {
			throw new ForbiddenOperationException(
					"Error loading transformer class [" + transformer + "]: " + exception.getLocalizedMessage());
		}
	}

	private void determinePreferredDataSources() {
		List<EtlDataSource> available = getAllAvaliableDataSource();

		if (available == null) {
			available = new ArrayList<>();
			setAllAvaliableDataSource(available);
		}

		if (utilities.listHasNoElement(preferredDataSource)) {
			preferredDataSource = new ArrayList<>();

			for (EtlDataSource dataSource : available) {

				if (dataSource instanceof PrimaryDataSourceConfiguration
						&& ((PrimaryDataSourceConfiguration) dataSource).doNotUseAsDatasource()) {
					continue;
				}

				if (getName() != null && getName().equals(dataSource.getName())) {
					preferredDataSource.add(dataSource.getAlias());
					break;
				}
			}

			PrimaryDataSourceConfiguration primary = getPrimaryDataSource();

			if (primary != null && useSrcConfAsDataSource() && !preferredDataSource.contains(primary.getAlias())) {
				preferredDataSource.add(primary.getAlias());
			}
		}

		for (String configuredName : preferredDataSource) {
			EtlDataSource preferred = findUniqueDataSource(configuredName, available);

			if (preferred == null) {
				throw new InvalidDataSourceOnFieldDefifitionException(getName(), configuredName);
			}
			addToPrefferedDataSource(preferred);
		}

		for (EtlDataSource dataSource : available) {

			if (getAllPrefferredDataSource() != null && getAllPrefferredDataSource().contains(dataSource)) {
				continue;
			}
			addToNotPrefferedDataSource(dataSource);
		}
	}

	private EtlDataSource findUniqueDataSource(String nameOrAlias, List<EtlDataSource> available) {
		EtlDataSource result = null;

		for (EtlDataSource dataSource : available) {

			if (nameOrAlias.equals(dataSource.getAlias()) || nameOrAlias.equals(dataSource.getName())) {

				if (result != null && !onMultipleDataSourceWithSameName().useLast()) {
					throw new ForbiddenOperationException("The preferred datasource '" + nameOrAlias
							+ "' occurs more than once; configure its alias instead");
				}
				result = dataSource;
			}
		}
		return result;
	}

	public synchronized void generateAllFieldsMapping(Connection conn) throws DBException {

		if (isTrue(fieldsMappingAlreadyGenerated) || !useDefaultTransformer()) {
			return;
		}

		setAllMapping(new ArrayList<>());

		if (mappingResolutionStrategy().allowManual() && utilities.listHasElement(mapping)) {

			for (FieldsMapping fieldMapping : mapping) {
				prepareMapping(fieldMapping);
				fieldMapping.setManuallyConfigured(Boolean.TRUE);
				addMapping(fieldMapping);
			}
		}

		if ((mappingResolutionStrategy().allowAuto() || mappingResolutionStrategy().allowDefault())
				&& utilities.listHasElement(fields)) {

			for (Field field : fields) {

				if (isIgnorableField(field) || findMappingByDstField(field.getName()) != null) {
					continue;
				}

				FieldsMapping generated = FieldsMapping.fastCreate(this, field.getName(), field.getName(), true, conn);

				prepareMapping(generated);

				addMapping(generated);
			}
		}

		fieldsMappingAlreadyGenerated = Boolean.TRUE;
	}

	private void prepareMapping(FieldsMapping fieldMapping) throws DBException {
		fieldMapping.setTransformationTargetObject(this);

		if (!utilities.stringHasValue(fieldMapping.getDstField())) {
			throw new EtlConfException(
					"A mapping on destination '" + getName() + "' does not define dstField: " + fieldMapping);
		}

		fieldMapping.tryToLoadDataSourceInfoFromSrcField(this);
		fieldMapping.tryToLoadTransformer(this);

		if (fieldMapping.hasTransformer() && fieldMapping.isSetToNullValue()) {
			fieldMapping.setSrcValue(null);
		}

		if (fieldMapping.hasDataSourceName()) {
			EtlDataSource dataSource = findDataSource(fieldMapping.getDataSourceName());

			if (dataSource == null) {
				throw new InvalidDataSourceOnFieldDefifitionException(fieldMapping.getName(),
						fieldMapping.getDataSourceName());
			}
			fieldMapping.setDataSource(dataSource);
			fieldMapping.setDataSourceName(dataSource.getAlias());
		} else {
			tryToLoadDataSourceToFieldMapping(fieldMapping);
		}
	}

	private boolean isIgnorableField(Field field) {

		if (field == null || !utilities.listHasElement(ignorableFields)) {
			return false;
		}

		for (String ignorableField : ignorableFields) {

			if (utilities.equalsFieldsName(ignorableField, field.getName())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public TableConfiguration findFullConfiguredConfInAllRelatedTable(String fullTableName,
			List<Integer> alreadyCheckedObjects) {
		EtlObjectConfiguration physical = physicalObjectConfiguration();
		return physical == null ? null
				: physical.findFullConfiguredConfInAllRelatedTable(fullTableName, alreadyCheckedObjects);
	}

	@Override
	public String generateClassName() {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		if (physical != null) {
			return physical.generateClassName();
		}

		String logicalName = getName();

		if (!utilities.stringHasValue(logicalName)) {
			throw new EtlConfException("Cannot generate a record class name for an unnamed destination");
		}

		StringBuilder className = new StringBuilder();

		for (String part : logicalName.split("[^A-Za-z0-9]+")) {

			if (!part.isEmpty()) {
				className.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
			}
		}

		return className.append("VO").toString();
	}

	@Override
	public String getObjectName() {
		return getName();
	}

	@Override
	public Boolean hasPK() {
		return getPrimaryKey() != null;
	}

	@Override
	public Boolean hasPK(Connection conn) throws DBException {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		return physical == null ? hasPK() : physical.hasPK(conn);
	}

	@Override
	public Boolean isMetadata() {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		return physical == null ? Boolean.FALSE : physical.isMetadata();
	}

	@Override
	public DBConnectionInfo getRelatedConnInfo() {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		return physical == null ? null : physical.getRelatedConnInfo();
	}

	@Override
	public void setEtlRecordClass(Class<? extends EtlDatabaseObject> etlRecordClass) {
		this.etlRecordClass = etlRecordClass;
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		if (physical != null) {
			physical.setEtlRecordClass(etlRecordClass);
		}
	}

	@Override
	public Class<? extends EtlDatabaseObject> getEtlRecordClass() {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		return etlRecordClass != null ? etlRecordClass : physical == null ? null : physical.getEtlRecordClass();
	}

	@Override
	public Boolean isDestinationInstallationType() {
		return Boolean.TRUE;
	}

	@Override
	public void generateRelatedPojoClass(DBConnectionInfo connInfo, Boolean fullClass) {
		EtlObjectConfiguration physical = requirePhysicalObjectConfiguration("generateRelatedPojoClass");

		physical.generateRelatedPojoClass(connInfo, fullClass);

		this.etlRecordClass = physical.getEtlRecordClass();
	}

	@Override
	public DatabaseObjectLoaderHelper getLoadHealper() {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		if (physical != null) {
			return physical.getLoadHealper();
		}

		if (loadHelper == null) {
			loadHelper = new DatabaseObjectLoaderHelper(this);
		}
		return loadHelper;
	}

	@Override
	public String getAlias() {
		if (resource != null && utilities.stringHasValue(resource.getAlias())) {
			return resource.getAlias();
		}
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		return physical == null ? getName() : physical.getAlias();
	}

	@Override
	public String generateSelectFromQuery() {
		return requirePhysicalObjectConfiguration("generateSelectFromQuery").generateSelectFromQuery();
	}

	@Override
	public List<String> getExcludedFieldsFromObjectDesc() {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		return physical == null ? java.util.Collections.emptyList() : physical.getExcludedFieldsFromObjectDesc();
	}

	@Override
	public Boolean isMustLoadChildrenInfo() {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		return physical == null ? isTrue(mustLoadChildrenInfo) : physical.isMustLoadChildrenInfo();
	}

	public void setMustLoadChildrenInfo(Boolean mustLoadChildrenInfo) {
		this.mustLoadChildrenInfo = mustLoadChildrenInfo;
	}

	private EtlObjectConfiguration requirePhysicalObjectConfiguration(String operation) {
		EtlObjectConfiguration physical = physicalObjectConfiguration();

		if (physical == null) {
			throw new ForbiddenOperationException("Destination '" + getName() + "' does not yet provide the physical "
					+ "capability required by " + operation);
		}

		return physical;
	}

	public boolean supports(org.openmrs.module.epts.etl.connector.ConnectorWriteMode mode) {
		return destinationDataConfiguration != null && destinationDataConfiguration.supports(mode);
	}

	/** Copies only logical transformation rules from a legacy destination. */
	public void copyTransformationRulesFrom(DstConf legacy) {

		if (legacy == null) {
			return;
		}

		setJoinFields(copy(legacy.getJoinFields()));
		setGeneratedJoinFields(copyNested(legacy.getGeneratedJoinFields()));
		setMapping(copy(legacy.getMapping()));
		setAllMapping(copy(legacy.getAllMapping()));
		setExcludedFields(copy(legacy.getExcludedFields()));
		setIgnorableFields(copy(legacy.getIgnorableFields()));
		setPreferredDataSource(copy(legacy.getPreferredDataSource()));
		setUnmappedFieldBehavior(legacy.getUnmappedFieldBehavior());
		setTransformer(legacy.getTransformer());
		setTransformerInstance(legacy.getTransformerInstance());
		setIncludeAllFieldsFromDataSource(legacy.isIncludeAllFieldsFromDataSource());
		setApplyCondition(legacy.getApplyCondition());
		setOnMultipleDataSourceForSameMapping(legacy.getOnMultipleDataSourceForSameMapping());
		setOnMultipleDataSourceWithSameName(legacy.getOnMultipleDataSourceWithSameName());
		setUseAsDataSource(legacy.getUseAsDataSource());
		setIgnoreNoDstIssue(legacy.getIgnoreNoDstIssue());
		setMappingResolutionStrategy(legacy.getMappingResolutionStrategy());
		setOnMissingRequiredSrcObject(legacy.getOnMissingRequiredSrcObject());
		setAllowDuplicateDestinationMappings(legacy.getAllowDuplicateDestinationMappings());
		setIdentifiedRecordAction(legacy.getIdentifiedRecordAction());
		setIncompleteMappingBehavior(legacy.getIncompleteMappingBehavior());
		setDisabled(legacy.isDisabled());
	}

	private <T> List<T> copy(List<T> values) {
		return values == null ? null : new ArrayList<>(values);
	}

	private List<List<FieldsMapping>> copyNested(List<List<FieldsMapping>> values) {

		if (values == null) {
			return null;
		}

		List<List<FieldsMapping>> copied = new ArrayList<>();

		for (List<FieldsMapping> value : values) {
			copied.add(copy(value));
		}

		return copied;
	}

	@Override
	@JsonIgnore
	public ConnectorResource getResolvedResource() {
		return resolvedResource;
	}

	@JsonIgnore
	public void setResolvedResource(ConnectorResource resolvedResource) {
		this.resolvedResource = resolvedResource;
		if (resolvedResource != null) {
			inferAndConfigureAdapter(resolvedResource);
			if (destinationDataConfiguration != tableDataDestination) {
				validateRecordFields(destinationDataConfiguration);
			}
		}
	}

	@JsonIgnore
	public EtlItemConfiguration getParentItem() {
		return parentItem;
	}

	public void initialize(EtlItemConfiguration parentItem) {

		if (isInitialized()) {
			return;
		}

		this.parentItem = parentItem;

		applyIncludes();

		resolveDestinationDataConfiguration();

		markAsInitialized();
	}

	private void resolveDestinationDataConfiguration() {
		inferFileAdapter(resource);
		List<DestinationDataConfiguration> configured = new ArrayList<>();
		List<String> names = new ArrayList<>();

		add(configured, names, "tableDataDestination", tableDataDestination);
		add(configured, names, "jsonDataDestination", jsonDataDestination);
		add(configured, names, "csvDataDestination", csvDataDestination);

		if (configured.size() > 1) {
			throw new EtlConfException("destination may define at most one of "
					+ "[tableDataDestination, jsonDataDestination, csvDataDestination]; configured: " + names);
		}

		DestinationDataConfiguration resolved = configured.isEmpty() ? null : configured.get(0);

		validateDestinationDataConfiguration(resolved);

		this.destinationDataConfiguration = resolved;

		if (this.destinationDataConfiguration != null) {
			this.destinationDataConfiguration.setParentDestination(this);
		}
		configureJdbcAdapter();

		if (primaryKey != null && tableDataDestination != null) {
			tableDataDestination.setPrimaryKey(primaryKey);
		}

		if (fields == null && physicalObjectConfiguration() != null) {
			synchronizePhysicalFields(physicalObjectConfiguration());
		}
	}

	private void inferAndConfigureAdapter(ConnectorResource resolved) {
		ConnectorType connectorType = resolved.getEndpoint().getType();
		if (destinationDataConfiguration == null) {
			if (connectorType == ConnectorType.JDBC) {
				tableDataDestination = new JdbcTableDestinationConfiguration(resolved.getName());
				destinationDataConfiguration = tableDataDestination;
			} else if (connectorType == ConnectorType.FILE) {
				inferFileAdapter(resource);
				destinationDataConfiguration = jsonDataDestination != null ? jsonDataDestination : csvDataDestination;
				if (destinationDataConfiguration == null) {
					throw new EtlConfException("Cannot infer the destination format for file resource '"
							+ resolved.getName() + "'; use a .json/.csv name, resource.properties.format, "
							+ "or configure the adapter explicitly");
				}
			}
		}

		if (destinationDataConfiguration == null) {
			throw new EtlConfException("Cannot infer a destination adapter for connector type " + connectorType
					+ " and resource '" + resolved.getName() + "'; configure an adapter explicitly or provide "
					+ "resource.properties.format");
		}

		if (destinationDataConfiguration != null) {
			validateAdapterCompatibility(connectorType);
			destinationDataConfiguration.setParentDestination(this);
			configureJdbcAdapter();
		}
	}

	private void inferFileAdapter(ConnectorResourceConfiguration configuredResource) {
		if (configuredResource == null || tableDataDestination != null || jsonDataDestination != null
				|| csvDataDestination != null) {
			return;
		}
		Object configuredFormat = configuredResource.getProperties().get("format");
		String format = configuredFormat == null ? null : String.valueOf(configuredFormat).trim().toLowerCase(Locale.ROOT);
		String resourceName = configuredResource.getName();
		String normalizedName = resourceName == null ? "" : resourceName.toLowerCase(Locale.ROOT);
		if ("json".equals(format) || "jsonl".equals(format) || normalizedName.endsWith(".json")
				|| normalizedName.endsWith(".jsonl")) {
			jsonDataDestination = new JsonDestinationConfiguration();
		} else if ("csv".equals(format) || normalizedName.endsWith(".csv")) {
			csvDataDestination = new CsvDestinationConfiguration();
		}
	}

	private void validateAdapterCompatibility(ConnectorType connectorType) {
		if (connectorType == ConnectorType.JDBC && destinationDataConfiguration != tableDataDestination) {
			throw new EtlConfException("A JDBC connector requires tableDataDestination");
		}
		if (connectorType == ConnectorType.FILE && destinationDataConfiguration == tableDataDestination) {
			throw new EtlConfException("A FILE connector cannot use tableDataDestination");
		}
	}

	private void configureJdbcAdapter() {
		if (tableDataDestination == null) {
			return;
		}
		if (parentItem != null) {
			tableDataDestination.setParentConf(parentItem);
		}
		if (primaryKey != null) {
			tableDataDestination.setPrimaryKey(primaryKey);
		}
		if (resolvedResource != null) {
			tableDataDestination.setResolvedResource(resolvedResource);
			if (resource != null && utilities.stringHasValue(resource.getAlias())) {
				tableDataDestination.setTableAlias(resource.getAlias());
			}
			if (resolvedResource.getEndpoint().getType() == ConnectorType.JDBC) {
				tableDataDestination.setRelatedConnInfo(
						resolvedResource.getEndpoint().unwrap(DBConnectionInfo.class));
			}
		}
	}

	private void validateDestinationDataConfiguration(DestinationDataConfiguration resolved) {

		if (resolved == tableDataDestination && tableDataDestination != null
				&& tableDataDestination.getResource() != null) {
			throw new EtlConfException(
					"destination.tableDataDestination.resource is not allowed; configure destination.resource instead");
		}

		if (resolved != null && resolved != tableDataDestination && resource == null) {
			throw new EtlConfException("destination.resource is required for non-JDBC destinations");
		}

		if (resource != null && !utilities.stringHasValue(resource.getName())) {
			throw new EtlConfException("destination.resource.name is required");
		}

		if ((resolved != null && resolved != tableDataDestination)
				|| (resolved == null && resource == null)) {
			validateRecordFields(resolved);
		}
	}

	private void validateRecordFields(DestinationDataConfiguration resolved) {

		if (fields == null || fields.isEmpty()) {
			throw new EtlConfException("destination.fields is required for " + variantName(resolved));
		}

		Set<String> names = new HashSet<>();

		for (Field field : fields) {

			if (field == null || !utilities.stringHasValue(field.getName())) {
				throw new EtlConfException("Every destination field must have a name");
			}

			if (!names.add(field.getName().trim().toLowerCase(Locale.ROOT))) {
				throw new EtlConfException("Duplicated destination field '" + field.getName() + "'");
			}
		}
	}

	private String variantName(DestinationDataConfiguration configuration) {

		if (configuration == null) {
			return "an adapter-free destination";
		}
		return configuration == jsonDataDestination ? "jsonDataDestination" : "csvDataDestination";
	}

	private void add(List<DestinationDataConfiguration> configured, List<String> names, String name,
			DestinationDataConfiguration configuration) {

		if (configuration != null) {
			configured.add(configuration);
			names.add(name);
		}
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return parentItem == null ? null : parentItem.getRelatedEtlConf();
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return parentItem;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
		Field.tryToReplacePlaceholders(joinFields, schemaInfoSrc);
		Field.tryToReplacePlaceholders(mapping, schemaInfoSrc);

		this.ignorableFields = utilities.tryToReplacePlaceholdersAll(this.ignorableFields, schemaInfoSrc);
		this.excludedFields = utilities.tryToReplacePlaceholdersAll(this.excludedFields, schemaInfoSrc);
	}

	@Override
	public String toString() {
		return "EtlDestinationConfiguration[resource=" + resource + ", variant="
				+ (destinationDataConfiguration == null ? "unresolved"
						: destinationDataConfiguration.getClass().getSimpleName())
				+ "]";
	}
}
