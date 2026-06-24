package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSource;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSourceField;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlValidator;
import org.openmrs.module.epts.etl.conf.interfaces.ValidationRule;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.EtlDBConnectionType;
import org.openmrs.module.epts.etl.conf.types.ValidationPhase;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.etl.processor.transformer.FieldTransformingInfo;
import org.openmrs.module.epts.etl.exceptions.EmptyTransformedValueException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class DefaultEtlValidator extends AbstractEtlDataConfiguration implements EtlValidator {

	public static final CommonUtilities utilities = CommonUtilities.getInstance();

	private static Object lock = new Object();

	private DefaultValidationRule rule;

	private ActionOnEtlIssue behavior;

	private String message;

	private String name;

	private ValidationPhase phase;

	private TransformableDataSourceField value;

	private boolean initialized;

	private EtlDBConnectionType connectionToUse;

	private EtlDataConfiguration relatedEtlDataConf;

	public DefaultEtlValidator() {
		phase = ValidationPhase.AFTER_LOAD;
		behavior = ActionOnEtlIssue.ABORT_PROCESS;
		connectionToUse = EtlDBConnectionType.srcConnInfo;
	}

	@Override
	public EtlDBConnectionType getConnectionToUse() {
		return this.connectionToUse;
	}

	public void setConnectionToUse(EtlDBConnectionType connectionToUse) {
		this.connectionToUse = connectionToUse;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.relatedEtlDataConf.getRelatedEtlConf();
	}

	@Override
	public void init(EtlDataConfiguration relatedEtlConfig, Connection conn) throws DBException {
		this.relatedEtlDataConf = relatedEtlConfig;

		if (initialized)
			return;

		synchronized (lock) {
			EtlConfiguration etlConf = relatedEtlConfig instanceof EtlConfiguration
					? (EtlConfiguration) relatedEtlConfig
					: relatedEtlConfig.getRelatedEtlConf();

			getValue().tryToLoadTransformer(FastEtlTransformingTarget.fastCreate(etlConf,
					GenericDatabaseObject.createDefaultObjectListUsingConfParams(etlConf), conn), conn);

			if (getValue().getParent() == null) {

				SrcConf srcConf = new SrcConf();

				srcConf.setParentConf(etlConf.getDefaultEtlItemConf());

				TransformableDataSource objConf = new TransformableDataSource();
				objConf.setParentConf(srcConf);

				srcConf.setTableName(EtlConfiguration.ETL_RECORD_ERROR_TABLE_NAME);

				getValue().setParent(objConf);
			}

			initialized = true;
		}
	}

	@Override
	public void validate(EtlProcessor processor, EtlDatabaseObject srcObject, EtlDatabaseObject transformedRecord,
			List<EtlDatabaseObject> additionalSrcObjects, Connection srcConn, Connection dstConn)
			throws EtlExceptionImpl, DBException {

		if (!initialized)
			throw new EtlExceptionImpl("The Validator is not initialized!");

		if (connectionToUse.isDst()) {
			getValue().getTransformerInstance().setOverrideConnection(dstConn);
		}

		boolean valid;
		try {
			FieldTransformingInfo value = getValue().transform(processor, srcObject, transformedRecord,
					additionalSrcObjects, srcConn, dstConn);

			valid = this.getRule().evaluate(value.getTransformedValue());
		} catch (EmptyTransformedValueException e) {
			valid = false;
		}

		if (!valid) {
			handleFailure(transformedRecord);
		}
	}

	private void handleFailure(EtlDatabaseObject transformedRecord) {
		EtlExceptionImpl e = new EtlExceptionImpl(buildMessage());

		switch (behavior) {
		case MARK_RECORD_AS_FAILED:
			transformedRecord.getEtlInfo().setExceptionOnEtl(e);
			break;

		case ABORT_PROCESS:
			throw e;

		default:
			throw new EtlExceptionImpl("Unsupported validation fail behavior: " + behavior);
		}
	}

	private String buildMessage() {
		return "Validator: " + getName() + " failed"
				+ (utilities.stringHasValue(this.getMessage()) ? " due:" + this.getMessage() + "!" : "!");
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ValidationPhase getPhase() {
		return this.phase;
	}

	@Override
	public ActionOnEtlIssue getBehavior() {
		return this.behavior;
	}

	@Override
	public TransformableDataSourceField getValue() {
		return this.value;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public ValidationRule getRule() {
		return rule;
	}

	public void setRule(DefaultValidationRule rule) {
		this.rule = rule;
	}

	public void setBehavior(ActionOnEtlIssue behavior) {
		this.behavior = behavior;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhase(ValidationPhase phase) {
		this.phase = phase;
	}

	public void setValue(TransformableDataSourceField value) {
		this.value = value;
	}

	public static void tryToValidate(EtlDataConfiguration conf, Connection srcConn, Connection dstConn)
			throws EtlExceptionImpl, DBException {
		if (conf.hasValidator()) {
			for (EtlValidator validator : conf.getValidators()) {
				validator.init(conf, srcConn);

				validator.validate(null, null, null, null, srcConn, dstConn);
			}
		}

	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.relatedEtlDataConf;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {

	}
}
