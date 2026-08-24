package org.openmrs.module.epts.etl.conf;

import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.transformer.FieldTransformerType;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public abstract class AbstractBaseConfiguration implements BaseConfiguration {

	private static final EtlLogger LOG = EtlLogger.getLogger(AbstractBaseConfiguration.class);

	private Object comments;

	private List<Extension> extension;

	private List<DefaultEtlValidator> validators;

	public AbstractBaseConfiguration() {
	}

	public List<Extension> getExtension() {
		return extension;
	}

	public void setExtension(List<Extension> extension) {
		this.extension = extension;
	}

	public Object getComments() {
		return comments;
	}

	public void setComments(Object comments) {
		this.comments = comments;
	}

	public List<DefaultEtlValidator> getValidators() {
		return validators;
	}

	public void setValidators(List<DefaultEtlValidator> validators) {
		this.validators = validators;
	}

	public boolean hasValidator() {
		return utilities.listHasElement(this.validators);
	}

	public static Boolean isTrue(Boolean b) {
		return b != null && b;
	}

	public static Boolean isFalse(Boolean b) {
		return !isTrue(b);
	}

	public static Boolean false_() {
		return Boolean.FALSE;
	}

	public static Boolean true_() {
		return Boolean.TRUE;
	}

	public static boolean isTransformerExpression(EtlConfiguration etlConfiguration, String value)
			throws FieldAvaliableInMultipleDataSources, DBException {
		String transformer = value.contains("(") ? value.split("\\(")[0] : "";

		transformer = transformer.trim().strip();

		if (!utilities.stringHasValue(transformer))
			return false;

		FastEtlTransformingTarget target = FastEtlTransformingTarget.fastCreate(etlConfiguration, null, null);

		FieldsMapping map = FieldsMapping.fastCreate(target, "tmp", null);

		map.setTransformer(transformer);

		FieldTransformerType type = null;

		try {
			type = FieldTransformerType.resolveType(map);
		} catch (Exception e) {
		}

		return type != null;
	}

	public void debug(String msg) {
		LOG.debug(msg);
	}

	public void debug(String msg, Object... arguments) {
		LOG.debug(msg, arguments);
	}

	public void trace(String msg) {
		LOG.trace(msg);
	}

	public void trace(String msg, Object... arguments) {
		LOG.trace(msg, arguments);
	}

	public void info(String msg) {
		LOG.info(msg);
	}

	public void info(String msg, Object... arguments) {
		LOG.info(msg, arguments);
	}

	public void warn(String msg) {
		LOG.warn(msg);
	}

	public void warn(String msg, Object... arguments) {
		LOG.warn(msg, arguments);
	}

	public void err(String msg, Exception e) {
		LOG.err(msg, e);
	}

	public void err(String msg, Throwable throwable, Object... arguments) {
		LOG.err(msg, throwable, arguments);
	}

	public void err(String msg, Object... arguments) {
		LOG.err(msg, arguments);
	}

}
