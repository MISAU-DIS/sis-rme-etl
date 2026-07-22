package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.FastEtlTransformingTarget;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.AutoIncrementHandlingType;
import org.openmrs.module.epts.etl.conf.types.FieldMappingResolutionStrategy;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class OnDemandInfo extends AbstractEtlDataConfiguration {

	public static CommonUtilities utilities = CommonUtilities.getInstance();

	private String parentTableName;

	private String parentSourceField;

	private EtlItemConfiguration onDemandCreateParentItemConf;

	private EtlItemConfiguration existingParentItemConf;

	private FieldsMapping parentSourceIdMapping;

	private List<String> rawParameterDefinitions;

	private List<FieldsMapping> onDemandParentFieldMappings;

	private List<FieldsMapping> overrideFields;

	private String overrideFieldsStr;

	private String onDemandCheckCondition;

	private String templateName;

	private Map<String, Object> templateParams;

	private DstConf relatedEtlTransformTarget;

	private TransformableField field;

	private List<Object> originalParameters;

	private ActionOnEtlIssue unmappedFieldBehavior;

	private FieldMappingResolutionStrategy mappingResolutionStrategy;

	private AutoIncrementHandlingType autoIncrementHandlingType;

	public static OnDemandInfo create(List<Object> parameters, DstConf relatedEtlTransformTarget,
			TransformableField field, Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {

		OnDemandInfo o = new OnDemandInfo();

		o.init(parameters, relatedEtlTransformTarget, field, conn);

		return o;
	}

	public void init(List<Object> parameters, DstConf relatedEtlTransformTarget, TransformableField field,
			Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {

		this.relatedEtlTransformTarget = relatedEtlTransformTarget;
		this.field = field;
		this.originalParameters = parameters;

		if (parameters == null || parameters.size() < 1) {
			throw new ForbiddenOperationException("A ParentOnDemandLoadTransformer needs at least 1 parameters.\n"
					+ "ParentOnDemandLoadTransformer(parentTableName)");
		}

		this.templateParams = new HashMap<>();

		this.parentTableName = parameters.get(0).toString();

		this.rawParameterDefinitions = parameters.size() > 1
				? parameters.subList(1, parameters.size()).stream().map(Object::toString).toList()
				: null;

		if (utilities.listHasElement(this.rawParameterDefinitions)) {
			String prev = null;

			for (String fieldData : this.rawParameterDefinitions) {
				String[] mapping = fieldData.split(":", 2);

				if (mapping.length != 2) {
					if (isOverrideField(prev)) {
						this.overrideFieldsStr += "," + mapping[0];
					} else
						throw new EtlExceptionImpl(
								"Wrong format for newObjectData(" + fieldData + ") within the " + getTransformerDsc()
										+ "\n" + "Each object param must be specified as filedName:srcFieldOrValue");
				} else {
					String dstField = mapping[0];
					String srcFieldOrValue = mapping[1];

					if (dstField.equals("parent_field_in_datasource_object")) {
						if (!utilities.stringHasValue(srcFieldOrValue)) {
							throw new ForbiddenOperationException("The parent_field_in_datasource_object has no value");
						}

						this.parentSourceField = srcFieldOrValue;

						this.parentSourceIdMapping = utilities.stringHasValue(this.parentSourceField)
								? new FieldsMapping(relatedEtlTransformTarget, this.parentSourceField,
										relatedEtlTransformTarget.getSrcConf().getTableAlias(), field.getDstField(),
										conn)
								: null;

					} else if (dstField.equals("on_demand_check_condition")) {
						if (!utilities.stringHasValue(srcFieldOrValue)) {
							throw new ForbiddenOperationException("The on_demand_check_condition has no value");
						}

						this.onDemandCheckCondition = srcFieldOrValue;

						this.tryToLoadDumpScriptContentToFieldAndValidate("onDemandCheckCondition",
								relatedEtlTransformTarget.retrieveAllAvailableTemplateParameters(), conn);
					} else if (dstField.equals("template")) {
						if (!utilities.stringHasValue(srcFieldOrValue)) {
							throw new ForbiddenOperationException("The template has no value");
						}

						this.templateName = srcFieldOrValue;
					} else if (dstField.equals("unmapped_field_behavior")) {
						if (!utilities.stringHasValue(srcFieldOrValue)) {
							throw new ForbiddenOperationException("The unmapped_field_behavior has no value");
						}

						this.unmappedFieldBehavior = ActionOnEtlIssue.valueOf(srcFieldOrValue.toUpperCase());
					} else if (dstField.equals("mapping_resolution_strategy")) {
						if (!utilities.stringHasValue(srcFieldOrValue)) {
							throw new ForbiddenOperationException("The mapping_resolution_strategy has no value");
						}

						this.mappingResolutionStrategy = FieldMappingResolutionStrategy
								.valueOf(srcFieldOrValue.toUpperCase());
					} else if (dstField.startsWith("template_param_")) {

						String paramName = dstField.substring("template_param_".length());

						if (!utilities.stringHasValue(paramName)) {
							throw new ForbiddenOperationException("Invalid template_param key: " + dstField);
						}

						templateParams.put(paramName, srcFieldOrValue);
					} else if (dstField.equals("override_fields")) {
						this.overrideFieldsStr = srcFieldOrValue;
					} else if (dstField.equals("auto_increment_handling_type")) {
						this.autoIncrementHandlingType = AutoIncrementHandlingType
								.valueOf(srcFieldOrValue.toUpperCase());
					} else {
						if (!utilities.stringHasValue(srcFieldOrValue)
								|| srcFieldOrValue.toLowerCase().equals("null")) {
							srcFieldOrValue = null;
						}

						FieldsMapping fm;

						if (isTransformerExpression(relatedEtlTransformTarget.getRelatedEtlConf(), srcFieldOrValue)) {
							fm = FieldsMapping.fastCreate(relatedEtlTransformTarget, dstField, dstField, false, conn);
							fm.setTransformer(srcFieldOrValue);
							fm.tryToLoadTransformer(relatedEtlTransformTarget, conn);

						} else {
							fm = fastCreateFieldMap(srcFieldOrValue, dstField, relatedEtlTransformTarget, conn);
						}

						if (onDemandParentFieldMappings == null) {
							onDemandParentFieldMappings = new ArrayList<>();
						}

						this.onDemandParentFieldMappings.add(fm);

					}
				}

				prev = fieldData;
			}
		}

		if (!utilities.stringHasValue(this.onDemandCheckCondition)
				&& !utilities.stringHasValue(this.parentSourceField)) {
			throw new ForbiddenOperationException(
					"At least on_demand_check_condition or parent_field_in_datasource_object must be specified");
		}

		if (!this.templateParams.isEmpty() && !utilities.stringHasValue(this.templateName)) {
			throw new ForbiddenOperationException(
					"Template parameters specified but no templated was defined with transformer: \n"
							+ getTransformerDsc());
		}
	}

	public AutoIncrementHandlingType getAutoIncrementHandlingType() {
		return autoIncrementHandlingType;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.relatedEtlTransformTarget.getRelatedEtlConf();
	}

	public ActionOnEtlIssue unmappedFieldBehavior() {
		return unmappedFieldBehavior;
	}

	private boolean isOverrideField(String fieldData) {
		if (fieldData == null)
			return false;

		String[] map = fieldData.split(":");

		return "override_fields".equals(map[0]);
	}

	private FieldsMapping fastCreateFieldMap(String parentFieldName, String dstField, DstConf transformTarget,
			Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		FieldsMapping fieldMap = FieldsMapping.fastCreate(transformTarget, parentFieldName, dstField, conn);

		if (!fieldMap.hasDataSourceName() && !fieldMap.isMapToNullValue()) {

			if (utilities.isNumeric(parentFieldName)) {
				fieldMap.setSrcValue(parentFieldName);
			} else {
				throw new EtlExceptionImpl("The value '" + parentFieldName + "' on " + getTransformerDsc()
						+ " must be either a valid field datasource or number");
			}
		}

		return fieldMap;
	}

	public boolean usesTemplate() {
		return this.templateName != null;
	}

	public boolean sourceParentMayExists() {
		return this.parentSourceIdMapping != null;
	}

	public String getTransformerDsc() {
		return field.getTransformer();
	}

	public String getParentTableName() {
		return parentTableName;
	}

	public void setParentTableName(String parentTableName) {
		this.parentTableName = parentTableName;
	}

	public String getParentSourceField() {
		return parentSourceField;
	}

	public void setParentSourceField(String parentSourceField) {
		this.parentSourceField = parentSourceField;
	}

	public EtlItemConfiguration getOnDemandCreateParentItemConf() {
		return onDemandCreateParentItemConf;
	}

	public void setOnDemandCreateParentItemConf(EtlItemConfiguration onDemandCreateParentItemConf) {
		this.onDemandCreateParentItemConf = onDemandCreateParentItemConf;
	}

	public EtlItemConfiguration getExistingParentItemConf() {
		return existingParentItemConf;
	}

	public void setExistingParentItemConf(EtlItemConfiguration existingParentItemConf) {
		this.existingParentItemConf = existingParentItemConf;
	}

	public FieldsMapping getParentSourceIdMapping() {
		return parentSourceIdMapping;
	}

	public void setParentSourceIdMapping(FieldsMapping parentSourceIdMapping) {
		this.parentSourceIdMapping = parentSourceIdMapping;
	}

	public List<String> getRawParameterDefinitions() {
		return rawParameterDefinitions;
	}

	public void setRawParameterDefinitions(List<String> rawParameterDefinitions) {
		this.rawParameterDefinitions = rawParameterDefinitions;
	}

	public List<FieldsMapping> getOnDemandParentFieldMappings() {
		return onDemandParentFieldMappings;
	}

	public void setOnDemandParentFieldMappings(List<FieldsMapping> onDemandParentFieldMappings) {
		this.onDemandParentFieldMappings = onDemandParentFieldMappings;
	}

	public List<FieldsMapping> getOverrideFields() {
		return overrideFields;
	}

	public void setOverrideFields(List<FieldsMapping> overrideFields) {
		this.overrideFields = overrideFields;
	}

	public String getOverrideFieldsStr() {
		return overrideFieldsStr;
	}

	public void setOverrideFieldsStr(String overrideFieldsStr) {
		this.overrideFieldsStr = overrideFieldsStr;
	}

	public String getOnDemandCheckCondition() {
		return onDemandCheckCondition;
	}

	public void setOnDemandCheckCondition(String onDemandCheckCondition) {
		this.onDemandCheckCondition = onDemandCheckCondition;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Map<String, Object> getTemplateParams() {
		return templateParams;
	}

	public void setTemplateParams(Map<String, Object> templateParams) {
		this.templateParams = templateParams;
	}

	public DstConf getRelatedEtlTransformTarget() {
		return relatedEtlTransformTarget;
	}

	public void setRelatedEtlTransformTarget(DstConf relatedEtlTransformTarget) {
		this.relatedEtlTransformTarget = relatedEtlTransformTarget;
	}

	public TransformableField getField() {
		return field;
	}

	public void setField(TransformableField field) {
		this.field = field;
	}

	public List<Object> getOriginalParameters() {
		return originalParameters;
	}

	public void setOriginalParameters(List<Object> originalParameters) {
		this.originalParameters = originalParameters;
	}

	public FieldMappingResolutionStrategy getMappingResolutionStrategy() {
		return mappingResolutionStrategy;
	}

	public void setMappingResolutionStrategy(FieldMappingResolutionStrategy mappingResolutionStrategy) {
		this.mappingResolutionStrategy = mappingResolutionStrategy;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return null;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}
}
