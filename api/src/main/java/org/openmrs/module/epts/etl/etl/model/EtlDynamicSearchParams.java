package org.openmrs.module.epts.etl.etl.model;

import org.openmrs.module.epts.etl.conf.datasource.EtlConfigurationSrcConf;

public class EtlDynamicSearchParams extends EtlDatabaseObjectSearchParams {

	public EtlDynamicSearchParams(EtlConfigurationSrcConf relatedSrcConf) {
		super(relatedSrcConf, null);

	}
}
