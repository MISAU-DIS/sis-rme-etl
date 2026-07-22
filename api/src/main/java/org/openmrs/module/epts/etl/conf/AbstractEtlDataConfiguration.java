package org.openmrs.module.epts.etl.conf;

import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.transformer.FieldTransformerType;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public abstract class AbstractEtlDataConfiguration extends AbstractBaseConfiguration implements EtlDataConfiguration {

	private EtlTemplateInfo template;

	private List<EtlFragmentInclude> include;

	private List<String> dynamicElements;

	private Boolean initialized;

	public Boolean isInitialized() {
		return isTrue(initialized);
	}

	public Boolean getInitialized() {
		return isTrue(initialized);
	}

	public void setInitialized(Boolean initialized) {
		this.initialized = initialized;
	}

	public void markAsInitialized() {
		setInitialized(true);
	}

	@Override
	public List<EtlFragmentInclude> getInclude() {
		return this.include;
	}

	public void setInclude(List<EtlFragmentInclude> include) {
		this.include = include;
	}

	@Override
	public EtlTemplateInfo getTemplate() {
		return template;
	}

	@Override
	public void setTemplate(EtlTemplateInfo template) {
		this.template = template;
	}

	@Override
	public ActionOnEtlIssue getGeneralBehaviourOnEtlException() {
		return getRelatedEtlConf().getGeneralBehaviourOnEtlException();
	}

	public List<String> getDynamicElements() {
		return dynamicElements;
	}

	public void setDynamicElements(List<String> dynamicElements) {
		this.dynamicElements = dynamicElements;
	}

	public static boolean isTransformerExpression(EtlConfiguration etlConfiguration, String value) throws FieldAvaliableInMultipleDataSources, DBException {
		if (value == null)
			return false;

		String transformer = value.contains("(") ? value.split("\\(")[0] : "";

		transformer = transformer.trim().strip();

		if (!utilities.stringHasValue(transformer))
			return false;

		FieldsMapping map = FieldsMapping.fastCreate(FastEtlTransformingTarget.fastCreate(etlConfiguration, null, null),
				"tmp", null);

		map.setTransformer(transformer);

		FieldTransformerType type = null;

		try {
			type = FieldTransformerType.resolveType(map);
		} catch (Exception e) {
		}

		return type != null;
	}
}
