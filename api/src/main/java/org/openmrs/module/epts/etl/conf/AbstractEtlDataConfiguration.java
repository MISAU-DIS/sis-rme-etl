package org.openmrs.module.epts.etl.conf;

import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;

public abstract class AbstractEtlDataConfiguration extends AbstractBaseConfiguration implements EtlDataConfiguration {
	
	private EtlConfiguration relatedEtlConf;
	
	private EtlTemplateInfo template;
	
	private List<EtlFragmentInclude> include;
	
	private List<String> dynamicElements;
	
	private Boolean initialized;
	
	public EtlConfiguration getRelatedEtlConf() {
		return relatedEtlConf;
	}
	
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
	
	public void setRelatedEtlConfig(EtlConfiguration relatedEtlConf) {
		this.relatedEtlConf = relatedEtlConf;
	}
	
	@Override
	public ActionOnEtlIssue getGeneralBehaviourOnEtlException() {
		return relatedEtlConf.getGeneralBehaviourOnEtlException();
	}
	
	public List<String> getDynamicElements() {
		return dynamicElements;
	}
	
	public void setDynamicElements(List<String> dynamicElements) {
		this.dynamicElements = dynamicElements;
	}
	
}
