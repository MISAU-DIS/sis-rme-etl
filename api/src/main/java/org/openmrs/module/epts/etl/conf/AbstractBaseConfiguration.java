package org.openmrs.module.epts.etl.conf;

import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.transformer.FieldTransformerType;

public abstract class AbstractBaseConfiguration implements BaseConfiguration {

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

	public static boolean isTransformerExpression(String value) {
		String transformer = value.contains("(") ? value.split("\\(")[0] : "";

		transformer = transformer.trim().strip();

		if (!utilities.stringHasValue(transformer))
			return false;

		FieldsMapping map = FieldsMapping.fastCreate("tmp", null);

		map.setTransformer(transformer);

		FieldTransformerType type = null;

		try {
			type = FieldTransformerType.resolveType(map);
		} catch (Exception e) {
		}

		return type != null;
	}

}
