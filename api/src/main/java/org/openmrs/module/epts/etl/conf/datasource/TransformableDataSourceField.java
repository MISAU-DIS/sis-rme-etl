package org.openmrs.module.epts.etl.conf.datasource;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.ConditionalEtlElement;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.RelationshipResolutionStrategy;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.transformer.EtlFieldTransformer;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.Field;

public class TransformableDataSourceField extends DataSourceField implements TransformableField, ConditionalEtlElement {

	private static final long serialVersionUID = -7824136202167355998L;

	private String transformer;

	private EtlFieldTransformer transformerInstance;

	private Boolean dataTypeLoaded;

	private Object defaultValue;

	private Object overrideTriggerValue;

	private String srcField;

	private FieldsMapping auxFieldMapping;

	private RelationshipResolutionStrategy relationshipResolutionStrategy;

	private ActionOnEtlIssue nullValueBehavior;

	private String applyCondition;

	public TransformableDataSourceField() {
		this.nullValueBehavior = ActionOnEtlIssue.IGNORE;
		this.relationshipResolutionStrategy = RelationshipResolutionStrategy.RESOLVE;
	}

	public static TransformableDataSourceField fastCreate(String name, Object value) {
		TransformableDataSourceField ds = new TransformableDataSourceField();

		ds.setValue(value);
		ds.setName(name);

		return ds;
	}

	@Override
	public String getCondition() {
		return this.applyCondition;
	}

	public String getApplyCondition() {
		return applyCondition;
	}

	public void setApplyCondition(String applyCondition) {
		this.applyCondition = applyCondition;
	}

	@Override
	public EtlTransformTarget getTransformationTargetObject() {
		return this.getParent();
	}

	public RelationshipResolutionStrategy getRelationshipResolutionStrategy() {
		return relationshipResolutionStrategy;
	}

	public void setRelationshipResolutionStrategy(RelationshipResolutionStrategy relationshipResolutionStrategy) {
		this.relationshipResolutionStrategy = relationshipResolutionStrategy;
	}

	@Override
	public TransformableDataSource getParent() {
		return (TransformableDataSource) super.getParent();
	}

	@Override
	public void setParent(EtlDataSource parent) {
		if (parent != null && !(parent instanceof TransformableDataSource))
			throw new EtlConfException("The parent of an ObjectDataSourceField must be a ObjectDataSource");

		super.setParent(parent);
	}

	@Override
	public Object getOverrideTriggerValue() {
		return this.overrideTriggerValue;
	}

	@Override
	public void setOverrideTriggerValue(Object overrideTriggerValue) {
		this.overrideTriggerValue = overrideTriggerValue;
	}

	public void setNullValueBehavior(ActionOnEtlIssue nullValueBehavior) {
		this.nullValueBehavior = nullValueBehavior;
	}

	@Override
	public ActionOnEtlIssue nullValueBehavior() {
		return this.nullValueBehavior;
	}

	@Override
	public String getDataSourceName() {
		return this.getParent().getName();
	}

	@Override
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String getTransformer() {
		return transformer;
	}

	public void setTransformer(String transformer) {
		this.transformer = transformer;
	}

	public EtlFieldTransformer getTransformerInstance() {
		return transformerInstance;
	}

	public void setTransformerInstance(EtlFieldTransformer transformerInstance) {
		this.transformerInstance = transformerInstance;
	}

	public Boolean isDataTypeLoaded() {
		return dataTypeLoaded != null && dataTypeLoaded;
	}

	public void setDataTypeLoaded(Boolean dataTypeLoaded) {
		this.dataTypeLoaded = dataTypeLoaded;
	}

	public FieldsMapping getAuxFieldMapping() {
		return auxFieldMapping;
	}

	public void setAuxFieldMapping(FieldsMapping auxFieldMapping) {
		this.auxFieldMapping = auxFieldMapping;
	}

	public Boolean hasAuxFieldMapping() {
		return this.auxFieldMapping != null;
	}

	@Override
	public void copyFrom(Field f) {
		super.copyFrom(f);

		if (f instanceof TransformableDataSourceField) {
			TransformableDataSourceField fDs = (TransformableDataSourceField) f;
			this.setTransformer(fDs.getTransformer());
			this.setTransformerInstance(fDs.getTransformerInstance());
			this.setExtension(fDs.getExtension());
			this.setDataTypeLoaded(fDs.isDataTypeLoaded());
			this.setSrcField(fDs.getSrcField());
			this.setAuxFieldMapping(fDs.getAuxFieldMapping());
			this.setNullValueBehavior(fDs.nullValueBehavior());
			this.setRelationshipResolutionStrategy(fDs.getRelationshipResolutionStrategy());
		}
	}

	@Override
	public Object getValueToTransform() {
		return this.getValue();
	}

	@Override
	public Boolean hasSrcField() {
		return Boolean.FALSE;
	}

	@Override
	public String getDstField() {
		return this.getName();
	}

	@Override
	public String getSrcField() {
		return this.srcField;
	}

	public void setSrcField(String srcField) {
		this.srcField = srcField;
	}

	public static List<TransformableDataSourceField> cloneAll_(List<TransformableDataSourceField> toCloneFrom,
			TransformableDataSource toCloneTo) {
		if (toCloneFrom == null)
			return null;

		List<TransformableDataSourceField> clonedItems = new ArrayList<>(toCloneFrom.size());

		for (TransformableDataSourceField dsF : toCloneFrom) {
			TransformableDataSourceField clonedItem = new TransformableDataSourceField();

			clonedItem.copyFrom(dsF);
			clonedItem.setParent(toCloneTo);

			clonedItems.add(clonedItem);
		}

		return clonedItems;
	}

	@Override
	public RelationshipResolutionStrategy relationshipResolutionStrategy() {
		return this.relationshipResolutionStrategy;
	}

	@Override
	public TransformableDataSource getDataSource() {
		return this.getParent();
	}

	@Override
	public void init(EtlTransformTarget target) {
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.getParent().getRelatedEtlConf();
	}
}
