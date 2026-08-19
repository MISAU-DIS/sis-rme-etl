package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.DefaultEtlValidator;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlTemplateInfo;
import org.openmrs.module.epts.etl.conf.Extension;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.EmptyTransformedValueException;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.InvalidDataSourceOnFieldDefifitionException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public abstract class AbstractEtlFieldTransformer extends AbstractEtlDataConfiguration implements EtlFieldTransformer {
	private static final Pattern TRANSFORMER_EXPRESSION_PATTERN = Pattern
			.compile("^[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(.*\\)$");

	protected List<Object> parameters;

	protected EtlTransformTarget relatedEtlTransformTarget;

	protected TransformableField field;

	protected Connection overrideConnection;

	protected FieldsMapping input;

	protected String inputExpression;

	protected ActionOnEtlIssue onNullTransformedvalue;

	public AbstractEtlFieldTransformer(List<Object> parameters, EtlTransformTarget relatedEtlTargedConf,
			TransformableField field) {

		this.parameters = parameters;
		this.relatedEtlTransformTarget = relatedEtlTargedConf;
		this.field = field;

		if (relatedEtlTargedConf == null)
			throw new EtlConfException("The target conf withing " + this + " is null");

		if (relatedEtlTargedConf.getRelatedEtlConf() == null)
			throw new EtlConfException("The RelatedEtlConf conf withing the target of " + this + " is null");

	}

	public AbstractEtlFieldTransformer(List<Object> parameters, EtlTransformTarget relatedEtlTargedConf,
			TransformableField field, Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		this(parameters, relatedEtlTargedConf, field);

		loadParameters(conn);
	}

	protected void loadParameters(Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		if (utilities.listHasElement(this.parameters)) {

			for (Object fieldData : this.parameters) {
				String[] mapping = fieldData.toString().split(":", 2);

				if (mapping.length != 2) {
					throw new EtlExceptionImpl(
							"Wrong format for conditional parameters within the tranformer " + getTransformerDsc()
									+ "\n" + "Each object param must be specified as paramName:paramValue");
				}

				String paramName = mapping[0];
				String paramValue = mapping[1];

				if (!utilities.stringHasValue(paramValue)) {
					throw new EtlExceptionImpl("The paramValue for parameter " + paramName
							+ " has no value on transformer:  " + getTransformerDsc());
				}

				if (paramName.equals("input")) {
					if (isTransformerExpression(paramValue)) {
						this.input = FieldsMapping.fastCreateWithTransformer(this.getRelatedEtlTransformTarget(),
								field.getDstField(), paramValue, conn);
					} else {
						this.input = FieldsMapping.fastCreate(this.getRelatedEtlTransformTarget(), paramValue,
								paramValue, conn);
					}

					this.inputExpression = paramValue;
				}
			}
		}

	}

	public void traceTransformationInitialization(TransformableField field) {
		logTrace("Starting transformation of field {} within {}", field.toString(), this.toString());
	}

	public void traceTransformationFinalization(TransformableField field) {
		logTrace("Finished transformation of field {} within {}", field.toString(), this.toString());
	}

	public ActionOnEtlIssue getOnNullTransformedvalue() {
		return onNullTransformedvalue;
	}

	public void setOnNullTransformedvalue(ActionOnEtlIssue onNullTransformedvalue) {
		this.onNullTransformedvalue = onNullTransformedvalue;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public static String buildCacheKey(EtlTransformTarget dstConf, TransformableField field, List<Object> parameters) {
		String params = parameters != null && !parameters.isEmpty()
				? ("|" + parameters.stream().map(Object::toString).collect(Collectors.joining("|")))
				: null;

		return (dstConf != null ? dstConf.toString() : "No EtlTransformTarget") + "|" + field.toString() + params;
	}

	public boolean hasInput() {
		return this.input != null;
	}

	public String getInputExpression() {
		return inputExpression;
	}

	public void setInputExpression(String inputExpression) {
		this.inputExpression = inputExpression;
	}

	public FieldsMapping getInput() {
		return input;
	}

	public void setInput(FieldsMapping input) {
		this.input = input;
	}

	public EtlTransformTarget getRelatedEtlTransformTarget() {
		return relatedEtlTransformTarget;
	}

	public String getTransformerDsc() {
		return field.getTransformer();
	}

	@Override
	public Connection getOverrideConnection() {
		return overrideConnection;
	}

	@Override
	public void setOverrideConnection(Connection overrideConnection) {
		this.overrideConnection = overrideConnection;
	}

	protected void logTrace(String msg) {
		this.relatedEtlTransformTarget.getRelatedEtlConf().trace(msg);
	}

	protected void logTrace(String msg, Object... arguments) {
		this.relatedEtlTransformTarget.getRelatedEtlConf().trace(msg, arguments);
	}

	@Override
	public String toString() {
		return this.getTransformerDsc();
	}

	public static boolean isTransformerExpression(String value) {

		if (value == null || value.isBlank()) {
			return false;
		}

		return TRANSFORMER_EXPRESSION_PATTERN.matcher(value.trim()).matches();
	}

	protected Object[] resolveDstValues(EtlDatabaseObject srcObject, List<FieldTransformingInfo> params,
			SrcConf srcConf, TableConfiguration dstConf, Connection srcConn, Connection dstConn) throws DBException {

		Object[] resolvedParams = new Object[params.size()];

		EtlDatabaseObject auxObject = dstConf.createRecordInstance();

		for (int i = 0; i < params.size(); i++) {
			FieldTransformingInfo paramValueInfo = params.get(i);
			Object transformedValue = paramValueInfo.getTransformedValue();

			if (paramValueInfo.skipRelationshipResolution()) {
				resolvedParams[i] = transformedValue;
				continue;
			}

			TransformableField srcField = paramValueInfo.getSrcField();

			ParentTable refInfo = dstConf.findParentRefInfoByField(srcField.getDstField());

			if (refInfo == null) {
				refInfo = dstConf.findParentRefInfoByField(srcField.getSrcField());
			}

			if (refInfo == null) {
				refInfo = dstConf.findParentRefInfoByField(srcField.getName());
			}

			if (refInfo == null) {
				resolvedParams[i] = transformedValue;
				continue;
			}

			auxObject.setFieldValue(refInfo.getChildColumnOnSimpleMapping(), transformedValue);

			EtlDatabaseObject parentInSrc = auxObject.retrieveParentInSrcUsingDstParentInfo(refInfo, srcConf, srcConn);
			EtlDatabaseObject parentInDst = null;

			refInfo.fullLoad(dstConn);

			if (parentInSrc != null) {
				parentInDst = auxObject.retrieveParentInDestination(refInfo, parentInSrc, dstConn);
			}

			if (parentInDst == null) {
				srcObject.loadObjectIdData();
				throw new EtlTransformationException(
						"The " + refInfo.getTableName() + "(" + transformedValue + ") of " + dstConf.getTableName()
								+ "(" + srcObject.getObjectId().asSimpleNumericValue() + ") cannot be found on dst db",
						srcObject, srcConf.getRelatedEtlConf().getDefaultInconsistencyBehavior());
			}

			resolvedParams[i] = parentInDst.getObjectId().asSimpleNumericValue();
		}

		return resolvedParams;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.relatedEtlTransformTarget != null ? this.relatedEtlTransformTarget.getRelatedEtlConf() : null;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.relatedEtlTransformTarget;
	}

	@Override
	public List<DefaultEtlValidator> getValidators() {
		return null;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}

	@Override
	public ActionOnEtlIssue getGeneralBehaviourOnEtlException() {
		return this.getRelatedEtlTransformTarget().getRelatedEtlConf().getDefaultInconsistencyBehavior();
	}

	@Override
	public EtlTemplateInfo getTemplate() {
		return null;
	}

	@Override
	public void setTemplate(EtlTemplateInfo template) {
	}

	@Override
	public List<Extension> getExtension() {
		return null;
	}

	@Override
	public void setExtension(List<Extension> extension) {
	}

	protected FieldTransformingInfo handleValueNotFound(EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, TransformableField field) throws EtlTransformationException {

		String objectName = transformedRecord != null && transformedRecord.getRelatedConfiguration() != null
				? transformedRecord.getRelatedConfiguration().getObjectName()
				: null;

		String fieldName = objectName != null ? objectName + "(" + field.getDstField() + ")"
				: "'" + field.getDstField() + "'";

		String srcField = field.getDataSourceName() != null ? field.getDataSourceName() + "." : "";

		srcField += field.hasSrcField() ? field.getSrcField().split("@")[0] : "";

		String srcMessage = "the available source objects or previous destination records";

		if (!srcField.isEmpty())
			srcMessage = srcField;

		String msg = "The field " + fieldName + " could not be resolved from " + srcMessage
				+ ". The transformation would produce a null value, but this field is not configured to accept null values. "
				+ "Configure an explicit mapping, allow null values, or ensure that a previous destination record is available as a data source.";

		if (field.nullValueBehavior().markRecordAsFailed()) {
			throw new EmptyTransformedValueException(msg, srcObject, ActionOnEtlIssue.LOG);
		}

		if (field.nullValueBehavior().abort()) {
			throw new EmptyTransformedValueException(msg, srcObject, ActionOnEtlIssue.ABORT_PROCESS);
		}

		if (field.nullValueBehavior().ignore() || field.nullValueBehavior().setToNull()) {
			return new FieldTransformingInfo(field, null, null);
		}

		throw new EmptyTransformedValueException(msg, srcObject, ActionOnEtlIssue.LOG);
	}

}
