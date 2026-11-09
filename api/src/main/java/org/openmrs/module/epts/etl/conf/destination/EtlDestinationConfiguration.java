package org.openmrs.module.epts.etl.conf.destination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.ConditionalEtlElement;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.ActionOnLoad;
import org.openmrs.module.epts.etl.conf.types.FieldMappingResolutionStrategy;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.etl.processor.transformer.DefaultRecordTransformer;
import org.openmrs.module.epts.etl.etl.processor.transformer.EtlRecordTransformer;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Connector-neutral aggregate for one ETL destination. Its physical destination
 * model is inferred from exactly one configured variant.
 */
public class EtlDestinationConfiguration extends AbstractEtlDataConfiguration implements ConditionalEtlElement {

	private String connector;
	private ConnectorResourceConfiguration resource;
	private JdbcTableDestinationConfiguration tableDataDestination;
	private JsonDestinationConfiguration jsonDataDestination;
	private CsvDestinationConfiguration csvDataDestination;

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

	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
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

	public void setMapping(List<FieldsMapping> mapping) {
		this.mapping = mapping;
	}

	public List<FieldsMapping> getAllMapping() {
		return allMapping;
	}

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

	@JsonIgnore
	public DestinationDataConfiguration getDestinationDataConfiguration() {
		if (destinationDataConfiguration == null) {
			throw new IllegalStateException(
					"Destination data configuration has not been resolved; initialize destination first");
		}
		return destinationDataConfiguration;
	}

	@JsonIgnore
	public String getName() {
		return getDestinationDataConfiguration().getName();
	}

	@JsonIgnore
	public List<? extends Field> getFields() {
		return getDestinationDataConfiguration().getFields();
	}

	@JsonIgnore
	public UniqueKeyInfo getPrimaryKey() {
		return getDestinationDataConfiguration().getPrimaryKey();
	}

	@JsonIgnore
	public List<UniqueKeyInfo> getUniqueKeys() {
		return getDestinationDataConfiguration().getUniqueKeys();
	}

	@JsonIgnore
	public List<ParentTable> getParentRefInfo() {
		return getDestinationDataConfiguration().getParentRefInfo();
	}

	@JsonIgnore
	public List<ChildTable> getChildRefInfo() {
		return getDestinationDataConfiguration().getChildRefInfo();
	}

	@JsonIgnore
	public String getSharePkWith() {
		return getDestinationDataConfiguration().getSharePkWith();
	}

	public boolean supports(org.openmrs.module.epts.etl.connector.ConnectorWriteMode mode) {
		return getDestinationDataConfiguration().supports(mode);
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

	@JsonIgnore
	public ConnectorResource getResolvedResource() {
		return resolvedResource;
	}

	@JsonIgnore
	public void setResolvedResource(ConnectorResource resolvedResource) {
		this.resolvedResource = resolvedResource;
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
		List<DestinationDataConfiguration> configured = new ArrayList<>();
		List<String> names = new ArrayList<>();
		add(configured, names, "tableDataDestination", tableDataDestination);
		add(configured, names, "jsonDataDestination", jsonDataDestination);
		add(configured, names, "csvDataDestination", csvDataDestination);

		if (configured.size() != 1) {
			throw new EtlConfException("destination must define exactly one of "
					+ "[tableDataDestination, jsonDataDestination, csvDataDestination]; configured: " + names);
		}

		DestinationDataConfiguration resolved = configured.get(0);
		validateDestinationDataConfiguration(resolved);
		this.destinationDataConfiguration = resolved;
		this.destinationDataConfiguration.setParentDestination(this);
	}

	private void validateDestinationDataConfiguration(DestinationDataConfiguration resolved) {
		if (resolved == tableDataDestination && tableDataDestination.getResource() != null) {
			throw new EtlConfException(
					"destination.tableDataDestination.resource is not allowed; configure destination.resource instead");
		}

		if (resource != null && utilities.stringHasValue(resource.getConnector())) {
			if (utilities.stringHasValue(connector) && !connector.equals(resource.getConnector())) {
				throw new EtlConfException("destination.connector differs from destination.resource.connector");
			}
			connector = resource.getConnector();
		}

		if (resolved != tableDataDestination && resource == null) {
			throw new EtlConfException("destination.resource is required for non-JDBC destinations");
		}

		if (!utilities.stringHasValue(resolved.getName())) {
			throw new EtlConfException("The logical destination name is required");
		}

		if (resource != null && !utilities.stringHasValue(resource.getName())) {
			throw new EtlConfException("destination.resource.name is required");
		}

		if (resolved != tableDataDestination) {
			validateRecordFields(resolved);
		}
	}

	private void validateRecordFields(DestinationDataConfiguration resolved) {
		if (resolved.getFields() == null || resolved.getFields().isEmpty()) {
			throw new EtlConfException("destination." + variantName(resolved) + ".fields is required");
		}

		Set<String> names = new HashSet<>();
		for (Field field : resolved.getFields()) {
			if (field == null || !utilities.stringHasValue(field.getName())) {
				throw new EtlConfException(
						"Every destination." + variantName(resolved) + " field must have a name");
			}
			if (!names.add(field.getName().trim().toLowerCase(Locale.ROOT))) {
				throw new EtlConfException("Duplicated destination field '" + field.getName() + "' in destination."
						+ variantName(resolved));
			}
		}
	}

	private String variantName(DestinationDataConfiguration configuration) {
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
		FieldsMapping.tryToReplacePlaceholders(joinFields, schemaInfoSrc);
		FieldsMapping.tryToReplacePlaceholders(mapping, schemaInfoSrc);
		ignorableFields = utilities.tryToReplacePlaceholdersAll(ignorableFields, schemaInfoSrc);
		excludedFields = utilities.tryToReplacePlaceholdersAll(excludedFields, schemaInfoSrc);
	}

	@Override
	public String toString() {
		return "EtlDestinationConfiguration[connector=" + connector + ", resource=" + resource + ", variant="
				+ (destinationDataConfiguration == null ? "unresolved"
						: destinationDataConfiguration.getClass().getSimpleName())
				+ "]";
	}
}
