package org.openmrs.module.epts.etl.conf.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;

public interface EtlItemConfigurationComponent extends EtlDataConfiguration {
	
	EtlItemConfiguration getParentEtlItemConf();
	
	@Override
	default Map<String, Object> retrieveAllAvailableTemplateParameters() {
		
		Map<String, Object> allParameters = new HashMap<>();
		
		Map<String, Object> parentParameters = this.getParentEtlItemConf().retrieveAllAvailableTemplateParameters();
		
		if (parentParameters != null && !parentParameters.isEmpty()) {
			allParameters.putAll(parentParameters);
		}
		
		Map<String, Object> ownParameters = EtlDataConfiguration.super.retrieveAllAvailableTemplateParameters();
		
		if (ownParameters != null && !ownParameters.isEmpty()) {
			allParameters.putAll(ownParameters);
		}
		
		return allParameters;
	}
}
