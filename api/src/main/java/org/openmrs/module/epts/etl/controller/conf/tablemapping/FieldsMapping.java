package org.openmrs.module.epts.etl.controller.conf.tablemapping;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlField;
import org.openmrs.module.epts.etl.conf.Extension;
import org.openmrs.module.epts.etl.conf.FastEtlTransformingTarget;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSource;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSourceField;
import org.openmrs.module.epts.etl.conf.interfaces.ConditionalEtlElement;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.RelationshipResolutionStrategy;
import org.openmrs.module.epts.etl.etl.processor.transformer.AbstractEtlFieldTransformer;
import org.openmrs.module.epts.etl.etl.processor.transformer.DefaultFieldTransformer;
import org.openmrs.module.epts.etl.etl.processor.transformer.EtlFieldTransformer;
import org.openmrs.module.epts.etl.etl.processor.transformer.SimpleValueTransformer;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.FieldNotAvaliableInAnyDataSource;
import org.openmrs.module.epts.etl.exceptions.FieldsMappingException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.InvalidDataSourceOnFieldDefifitionException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.AttDefinedElements;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.SQLUtilities;

/**
 * This class is used to map fields between any source table and destination
 * table
 * 
 * @author jpboane
 */
public class FieldsMapping extends Field implements TransformableField, ConditionalEtlElement {

	private static final long serialVersionUID = -2713197928272643006L;

	static CommonUtilities utilities = CommonUtilities.getInstance();

	private Object srcValue;

	private Object dstValue;

	private String srcField;

	private String dataSourceName;

	private String dstField;

	private Boolean mapToNullValue;

	private String transformer;

	private EtlFieldTransformer transformerInstance;

	private Extension extension;

	private Boolean dataTypeLoaded;

	private List<String> possibleSrc;

	private Object defaultValue;

	private Object overrideTriggerValue;

	private String originalSrcFieldDefinition;

	private RelationshipResolutionStrategy relationshipResolutionStrategy;

	private ActionOnEtlIssue nullValueBehavior;

	private EtlDataSource dataSource;

	private EtlTransformTarget transformTargetObject;

	private Boolean manuallyConfigured;

	private Boolean srcValueSwitchedToSrcField;

	private String applyCondition;

	public FieldsMapping() {
		this.nullValueBehavior = ActionOnEtlIssue.ABORT_PROCESS;
		this.relationshipResolutionStrategy = RelationshipResolutionStrategy.RESOLVE;

		this.possibleSrc = new ArrayList<>(5);

		this.manuallyConfigured = false;
	}

	private FieldsMapping(EtlTransformTarget target, String srcFieldFullName, String providedDataSourceName,
			String dstField, String providedTransformer, Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		this();

		boolean tryToLoadDataSourceAndTransformer = false;

		if (providedTransformer != null || providedDataSourceName != null || target.hasDataSource()) {
			tryToLoadDataSourceAndTransformer = true;
		}

		this.setOriginalSrcFieldDefinition(srcFieldFullName);
		this.setTransformer(providedTransformer);
		this.setSrcField(srcFieldFullName);
		this.setDstField(dstField);

		if (!hasDstField() && hasSrcField()) {
			String[] srcFieldParts = srcFieldFullName.toString().split("\\.");

			this.setDstField(srcFieldParts[srcFieldParts.length - 1]);
		}

		init(target);

		if (tryToLoadDataSourceAndTransformer) {
			tryToLoadDataSourceAndTransformer(conn);
		}

	}

	private void tryToLoadDataSourceAndTransformer(Connection conn)
			throws FieldAvaliableInMultipleDataSources, InvalidDataSourceOnFieldDefifitionException, DBException {

		EtlTransformTarget target = this.getTransformationTargetObject();

		if (hasDataSourceName()) {
			EtlDataSource ds = target.findDataSource(this.getDataSourceName());

			if (ds != null) {
				this.setDataSourceName(ds.getAlias());
			} else {

				if (target instanceof FastEtlTransformingTarget && !target.hasDataSource()) {
					this.setDataSourceName(this.getDataSourceName());
				} else {
					throw new InvalidDataSourceOnFieldDefifitionException(this.getOriginalSrcFieldDefinition(),
							this.getDataSourceName());
				}
			}
		} else {
			try {
				target.tryToLoadDataSourceToFieldMapping(this, conn);
			} catch (FieldNotAvaliableInAnyDataSource e) {
				this.setSrcValue(this.getSrcField());
				this.setSrcField(null);
			}

		}

		this.tryToLoadTransformer(target, conn);
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.getTransformationTargetObject() != null ? this.getTransformationTargetObject().getRelatedEtlConf()
				: null;
	}

	public String getApplyCondition() {
		return applyCondition;
	}

	public void setApplyCondition(String applyCondition) {
		this.applyCondition = applyCondition;
	}

	@Override
	public String getCondition() {
		return getApplyCondition();
	}

	public Boolean getManuallyConfigured() {
		return manuallyConfigured;
	}

	public void setManuallyConfigured(Boolean manuallyConfigured) {
		this.manuallyConfigured = manuallyConfigured;
	}

	public Boolean isManuallyConfigured() {
		return isTrue(getManuallyConfigured());
	}

	public ActionOnEtlIssue getNullValueBehavior() {
		return nullValueBehavior;
	}

	public void setNullValueBehavior(ActionOnEtlIssue nullValueBehavior) {
		this.nullValueBehavior = nullValueBehavior;
	}

	@Override
	public EtlTransformTarget getTransformationTargetObject() {
		return transformTargetObject;
	}

	public void setTransformationTargetObject(EtlTransformTarget targetObject) {
		this.transformTargetObject = targetObject;
	}

	@Override
	public ActionOnEtlIssue nullValueBehavior() {
		return this.nullValueBehavior;
	}

	public RelationshipResolutionStrategy getRelationshipResolutionStrategy() {
		return relationshipResolutionStrategy;
	}

	public void setRelationshipResolutionStrategy(RelationshipResolutionStrategy relationshipResolutionStrategy) {
		this.relationshipResolutionStrategy = relationshipResolutionStrategy;
	}

	@Override
	public RelationshipResolutionStrategy relationshipResolutionStrategy() {
		return this.relationshipResolutionStrategy;
	}

	public String getOriginalSrcFieldDefinition() {
		return originalSrcFieldDefinition;
	}

	public void setOriginalSrcFieldDefinition(String originalSrcFieldDefinition) {
		this.originalSrcFieldDefinition = originalSrcFieldDefinition;
	}

	@Override
	public Object getOverrideTriggerValue() {
		return this.overrideTriggerValue;
	}

	@Override
	public void setOverrideTriggerValue(Object overrideTriggerValue) {
		this.overrideTriggerValue = overrideTriggerValue;
	}

	@Override
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public List<String> getPossibleSrc() {
		return possibleSrc;
	}

	public void setPossibleSrc(List<String> possibleSrc) {
		this.possibleSrc = possibleSrc;
	}

	public Boolean isDataTypeLoaded() {
		return dataTypeLoaded != null && dataTypeLoaded;
	}

	public void setDataTypeLoaded(Boolean dataTypeLoaded) {
		this.dataTypeLoaded = dataTypeLoaded;
	}

	public static FieldsMapping createSimpleFieldsMapping(EtlTransformTarget target, String fieldName, Object value,
			Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {

		FieldsMapping map = FieldsMapping.fastCreate(target, fieldName, null, conn);

		map.setSrcField(null);
		map.setSrcValue(value);
		map.setTransformer(SimpleValueTransformer.class.getCanonicalName());
		map.tryToLoadTransformer(target, conn);

		return map;
	}

	public static FieldsMapping fastCreate(EtlTransformTarget transformTarget, String srcField, Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {
		String transformer = null;
		String dstField = null;
		String dataSourceName = null;

		return new FieldsMapping(transformTarget, srcField, dataSourceName, dstField, transformer, conn);
	}

	public static FieldsMapping fastCreate(EtlTransformTarget transformTarget, String srcField, String destField,
			Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		String providedDataSourceName = null;

		return new FieldsMapping(transformTarget, srcField, providedDataSourceName, destField, null, conn);
	}

	public static FieldsMapping fastCreateWithTransformer(EtlTransformTarget transformTarget, String dstField,
			String transformer, Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		return new FieldsMapping(transformTarget, null, null, dstField, transformer, conn);
	}

	public static FieldsMapping fastCreate(EtlTransformTarget transformTarget, String srcField,
			String providedDataSourceName, String destField, Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		return new FieldsMapping(transformTarget, srcField, providedDataSourceName, destField, null, conn);
	}

	public static FieldsMapping fastCreate(EtlTransformTarget transformTarget, TransformableDataSourceField dsF,
			Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		FieldsMapping f = null;

		if (dsF instanceof TransformableDataSourceField) {

			if (((TransformableDataSourceField) dsF).hasTransformer()) {
				if (dsF.getParent() instanceof EtlTransformTarget) {

					EtlTransformTarget target = (EtlTransformTarget) dsF.getParent();
					String dstField = ((TransformableDataSourceField) dsF).getDstField();
					String transformer = ((TransformableDataSourceField) dsF).getTransformer();
					String srcField = null;
					String dataSourceName = null;

					f = new FieldsMapping(target, srcField, dataSourceName, dstField, transformer, conn);
				} else {
					throw new ForbiddenOperationException("Only a targed parent is accepted!!");
				}
			} else {
				EtlTransformTarget target = (EtlTransformTarget) dsF.getParent();
				String srcField = dsF.getValue().toString();
				String dstField = dsF.getName();
				String transformer = null;
				String dataSourceName = null;

				f = new FieldsMapping(target, srcField, dataSourceName, dstField, transformer, conn);
			}
		}

		if (dsF.getValue() != null && dsF.getValue().toString().startsWith("@")) {

			String fn = dsF.getValue().toString().substring(1);

			Object paramValue = dsF.getParent().getRelatedEtlConf().getParamValue(fn);

			if (paramValue == null) {
				f.setSrcField(fn);

				EtlDataSource ds;

				if (dsF.getParent() instanceof TransformableDataSource) {
					ds = dsF.getParent().getSrcConf();
				} else if (dsF.getParent() instanceof EtlAdditionalDataSource) {
					ds = ((EtlAdditionalDataSource) dsF.getParent()).getRelatedSrcConf();
				} else {
					throw new EtlExceptionImpl("Unsupported datasource type " + dsF.getParent());
				}

				f.setDataSourceName(ds.getAlias());
			} else {
				f.setSrcValue(paramValue);
				f.setSrcField(null);
			}
		}

		return f;

	}

	public void fullLoad(EtlTransformTarget target, Connection conn)
			throws FieldAvaliableInMultipleDataSources, DBException {

		this.copyFrom(fastCreate(target, this.srcField, this.dstField, conn));

	}

	public EtlFieldTransformer getTransformerInstance() {
		if (!isInitialized()) {
			throw new ForbiddenOperationException("The FieldsMapping is not Initialized: " + this);
		}

		return this.transformerInstance;
	}

	public Boolean useDefaultTransformer() {
		return getTransformerInstance() instanceof DefaultFieldTransformer;
	}

	public void setTransformerInstance(EtlFieldTransformer transformerInstance) {
		this.transformerInstance = transformerInstance;
	}

	public String getTransformer() {
		return transformer;
	}

	public void setTransformer(String transformer) {
		this.transformer = transformer;
	}

	public Object getSrcValue() {
		return srcValue;
	}

	public void setSrcValue(Object srcValue) {
		this.srcValue = srcValue;
	}

	public Object getDstValue() {
		return dstValue;
	}

	public void setDstValue(Object dstValue) {
		this.dstValue = dstValue;
	}

	public String getDstField() {
		return dstField;
	}

	public void setDstField(String destField) {
		if (utilities.containsSpace(destField)) {
			throw new ForbiddenOperationException("Space found on field " + destField);
		}
		this.dstField = destField;
	}

	public String getSrcFieldAsClassField() {
		return AttDefinedElements.convertTableAttNameToClassAttName(this.srcField);
	}

	public String getDstFieldAsClassField() {
		return AttDefinedElements.convertTableAttNameToClassAttName(this.dstField);
	}

	public String getSrcField() {
		return srcField;
	}

	public void setSrcField(String srcField) {
		/*
		 * if (utilities.containsSpace(srcField) && !isTransformerExpression(srcField))
		 * { throw new ForbiddenOperationException("Space found on field " + srcField);
		 * }
		 */

		this.srcField = srcField;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;

		if (this.getPossibleSrc().size() == 0) {
			this.getPossibleSrc().add(dataSourceName);
		} else if (this.getPossibleSrc().size() == 1) {
			this.getPossibleSrc().set(0, dataSourceName);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FieldsMapping))
			return false;

		FieldsMapping fm = (FieldsMapping) obj;

		return this.dstField.equals(fm.dstField);
	}

	@Override
	public String toString() {
		String str = srcField != null ? ("srcField: " + srcField) : "";

		String separator = !str.isEmpty() && srcValue != null ? ", " : "";

		str += separator + (srcValue != null ? ("srcValue: " + srcValue) : "");

		separator = !str.isEmpty() && dstField != null ? ", " : "";

		str += separator + (dstField != null ? ("dstField: " + dstField) : "");

		separator = !str.isEmpty() && dataSourceName != null ? ", " : "";

		str += separator + (dataSourceName != null ? ("dataSourceName: " + dataSourceName) : "");

		return "[" + str + "]";
	}

	public void setMapToNullValue(Boolean b) {
		mapToNullValue = b;
	}

	public Boolean isMapToNullValue() {
		return mapToNullValue != null && mapToNullValue;
	}

	/**
	 * Parse this mapping to {@link Field}. The parsed field represent the
	 * {@link #dstField}
	 * 
	 * @param srcField the related src field
	 * @return the parsed field
	 */
	public Field parseToField(Field srcField) {
		Field f = (Field) srcField.cloneMe();

		f.setName(this.getDstField());
		f.setDataType(srcField.getDataType());

		return f;
	}

	public static List<Field> parseAllToField(List<FieldsMapping> toParse, EtlTransformTarget target,
			List<EtlDataSource> dataSource, Connection conn) {
		List<Field> parsed = new ArrayList<>(toParse.size());

		for (FieldsMapping fm : toParse) {

			Field dstField = null;

			if (fm.hasSrcField()) {
				Field srcField = fm.findSrcFieldInDataSource(dataSource);

				dstField = fm.parseToField(srcField);
			} else {
				// If there is no srcField then there must be a transformer

				fm.loadType(target, null, conn);

				dstField = Field.fastCreateField(fm.getDstField());

				if (!fm.isDataTypeLoaded()) {
					throw new ForbiddenOperationException(
							"The dataType for dstField " + fm.getDstField() + " was not loaded");
				}

				dstField.setDataType(fm.getDataType());
			}

			parsed.add(dstField);

		}

		return parsed;
	}

	public Boolean hasSrcField() {
		return utilities.stringHasValue(this.getSrcField());
	}

	public Boolean hasSrcValue() {
		return this.getSrcValue() != null;
	}

	public Boolean hasDstField() {
		return utilities.stringHasValue(this.getDstField());
	}

	public Boolean hasDstValue() {
		return this.getDstValue() != null;
	}

	private Field findSrcFieldInDataSource(List<EtlDataSource> dataSource) {
		return findContainingDataSource(dataSource).getField(this.getSrcField());
	}

	private EtlDataSource findContainingDataSource(List<EtlDataSource> dataSource) {

		for (EtlDataSource ds : dataSource) {

			if (ds.getName().equals(this.getDataSourceName()) || ds.getAlias().equals(this.getDataSourceName())) {

				if (ds.getField(this.getSrcField()) == null) {
					throw new ForbiddenOperationException("No field '" + this.getSrcField() + "' found on datasource "
							+ this.getDataSourceName() + "!");
				}

				return ds;
			}
		}

		throw new ForbiddenOperationException(
				"The field '" + this.getSrcField() + "' was not found in any datasource ");

	}

	public static FieldsMapping converteFromEtlField(EtlField field, EtlTransformTarget target, Connection conn) {

		if (field.getSrcDataSource() == null) {
			throw new ForbiddenOperationException("The EtlField " + field.getName() + " has no datasource!");
		}

		FieldsMapping fm = new FieldsMapping();

		fm.setSrcField(field.getSrcField().getName());
		fm.setDataSourceName(field.getSrcDataSource().getName());
		fm.setDstField(field.getName());
		fm.tryToLoadTransformer(target, conn);

		return fm;
	}

	public Boolean getSrcValueSwitchedToSrcField() {
		return srcValueSwitchedToSrcField;
	}

	public Boolean srcValueSwitchedToSrcField() {
		return isTrue(srcValueSwitchedToSrcField);
	}

	public void setSrcValueSwitchedToSrcField(Boolean srcValueSwitchedToSrcField) {
		this.srcValueSwitchedToSrcField = srcValueSwitchedToSrcField;
	}

	@Override
	public void init(EtlTransformTarget target) {
		this.setTransformationTargetObject(target);

		if (!this.hasSrcField() && this.hasSrcValue()) {
			// Try to switch from srcValue to srcField.
			// We force switch if there is no srcField but srcValue follow the pattern
			// srcField
			String[] srcFieldParts = this.getSrcValue().toString().split("\\.");

			if (srcFieldParts.length == 2) {
				if (SQLUtilities.isValidQueryColumnDefinition(this.getSrcValue().toString())) {
					this.tryToReloadSrcFieldAndDataSourceName(this.getSrcValue().toString());
					this.setSrcValue(null);
					this.setSrcValueSwitchedToSrcField(true);
				}
			}
		} else if (this.hasSrcField() && !this.hasSrcValue()) {
			String[] srcFieldParts = this.getSrcField().toString().split("\\.");

			this.tryToReloadSrcFieldAndDataSourceName(this.getSrcField());

			if (srcFieldParts.length == 1) {
				if (this.getSrcField().startsWith("@") || utilities.isNumeric(this.getSrcField())) {
					this.setSrcValue(this.getSrcField());
					this.setSrcField(null);
				} else if (this.getSrcField().equals("null")) {
					this.setMapToNullValue(true);
					this.setSrcField(null);
				}
			}
		}

		boolean srcFieldIsTransformer = hasSrcField()
				&& AbstractEtlFieldTransformer.isTransformerExpression(this.getSrcField());

		boolean srcValueIsTransformer = hasSrcValue() && this.getSrcValue() != null
				&& AbstractEtlFieldTransformer.isTransformerExpression(this.getSrcValue().toString());

		if (srcFieldIsTransformer && srcValueIsTransformer) {
			throw new EtlExceptionImpl("Only one of srcField or srcValue can define a transformer expression.");
		} else if (srcFieldIsTransformer) {
			initTransformerFromField(this.getSrcField());
			this.setSrcField(null);
		} else if (srcValueIsTransformer) {
			initTransformerFromField(this.getSrcValue().toString());

			this.setSrcValue(null);
		}

		if (!hasDstField()) {
			throw new FieldsMappingException("No dstField was defined withn the FieldsMapping");
		}

		this.markAsInitialized();
	}

	private void tryToReloadSrcFieldAndDataSourceName(String fullSrcFieldName) throws EtlExceptionImpl {
		String[] srcFieldParts = fullSrcFieldName.split("\\.");

		if (srcFieldParts.length == 2) {
			if (!this.hasDataSourceName()) {
				this.setDataSourceName(srcFieldParts[0]);
			} else {
				String onFieldDataSourceName = srcFieldParts[0];

				if (!onFieldDataSourceName.equals(this.getDataSourceName())) {
					throw new EtlExceptionImpl("Mismatch datasource definition for " + srcField
							+ ". On Field datasource definition '" + onFieldDataSourceName
							+ "' difer to defined datasource attribute'" + this.getDataSourceName() + "'");
				}
			}
		}

		this.setSrcField(srcFieldParts[srcFieldParts.length - 1]);
	}

	public void initTransformerFromField(String transformer) {
		if (hasTransformer()) {
			throw new FieldsMappingException("The dstField: " + this.getDstField()
					+ " is already using a transformer. You cannot use a dynamic transformer from srcField or srcValue!");
		}

		this.setTransformer(transformer);
	}

	public void tryToLoadDataSourceInfoFromSrcField(EtlTransformTarget dstConf) {
		if (!isInitialized()) {
			this.init(dstConf);
		}

		if (this.hasSrcField()) {
			String[] srcFieldParts = this.getSrcField().split("\\.");

			if (srcFieldParts.length > 2) {
				throw new ForbiddenOperationException("Malformed srcField " + this.getSrcField());
			} else if (srcFieldParts.length == 2) {
				if (!this.hasDataSourceName()) {
					this.setDataSourceName(srcFieldParts[0]);
				}

				this.setSrcField(srcFieldParts[1]);
			}
		}

	}

	public Boolean hasDataSourceName() {
		return utilities.stringHasValue(this.getDataSourceName());
	}

	@Override
	public Object getValueToTransform() {
		return this.getSrcValue();
	}

	@Override
	public String getName() {
		return hasSrcField() ? this.getSrcField() : this.getDstField();
	}

	public static List<FieldsMapping> cloneAll(List<FieldsMapping> toClone, Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {
		if (toClone == null)
			return null;

		List<FieldsMapping> cloned = new ArrayList<>(toClone.size());

		for (FieldsMapping f : toClone) {
			FieldsMapping clonedF = new FieldsMapping(f.getTransformationTargetObject(), f.getSrcField(),
					f.getDataSourceName(), f.getDstField(), f.getTransformer(), conn);

			clonedF.setSrcValue(f.getSrcValue());
			clonedF.setDstValue(f.getDstValue());

			cloned.add(clonedF);
		}

		return cloned;
	}

	@Override
	public void copyFrom(Field toCotoCOpyFrompyFrom) {
		super.copyFrom(toCotoCOpyFrompyFrom);

		if (toCotoCOpyFrompyFrom instanceof FieldsMapping) {
			FieldsMapping toCopyFormAsFieldsMapping = (FieldsMapping) toCotoCOpyFrompyFrom;

			this.srcValue = toCopyFormAsFieldsMapping.srcValue;
			this.dstValue = toCopyFormAsFieldsMapping.dstValue;
			this.srcField = toCopyFormAsFieldsMapping.srcField;
			this.dataSourceName = toCopyFormAsFieldsMapping.dataSourceName;
			this.dstField = toCopyFormAsFieldsMapping.dstField;
			this.mapToNullValue = toCopyFormAsFieldsMapping.mapToNullValue;
			this.transformer = toCopyFormAsFieldsMapping.transformer;
			this.transformerInstance = toCopyFormAsFieldsMapping.transformerInstance;
			this.extension = toCopyFormAsFieldsMapping.extension;
			this.dataTypeLoaded = toCopyFormAsFieldsMapping.dataTypeLoaded;
			this.possibleSrc = toCopyFormAsFieldsMapping.possibleSrc;
			this.defaultValue = toCopyFormAsFieldsMapping.defaultValue;
			this.overrideTriggerValue = toCopyFormAsFieldsMapping.overrideTriggerValue;
			this.nullValueBehavior = toCopyFormAsFieldsMapping.nullValueBehavior;
			this.relationshipResolutionStrategy = toCopyFormAsFieldsMapping.relationshipResolutionStrategy;
		}
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
		super.tryToReplacePlaceholders(schemaInfoSrc);

		setSrcValue(utilities.tryToReplacePlaceholders(getSrcValue(), schemaInfoSrc));
		setDstValue(utilities.tryToReplacePlaceholders(getDstValue(), schemaInfoSrc));
		setSrcField(utilities.tryToReplacePlaceholders(getSrcField(), schemaInfoSrc));
		setDataSourceName(utilities.tryToReplacePlaceholders(getDataSourceName(), schemaInfoSrc));
		setDstField(utilities.tryToReplacePlaceholders(getDstField(), schemaInfoSrc));
		setDataType(utilities.tryToReplacePlaceholders(getDataType(), schemaInfoSrc));
	}

	@Override
	public EtlDataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(EtlDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Boolean isSetToNullValue() {
		return hasSrcValue() && srcValue.toString().toLowerCase().equals("null");
	}

	public boolean overridable() {
		return !isManuallyConfigured();
	}

	public String getFullSrcField() {
		if (filedPathernIncludeDataSource()) {
			return this.srcField;
		} else {
			return this.getDataSourceName() != null ? this.getDataSourceName() + "." + this.getSrcField()
					: this.getSrcField();
		}
	}

	private boolean filedPathernIncludeDataSource() {
		return this.getSrcField().split("\\.").length > 1;
	}
}
