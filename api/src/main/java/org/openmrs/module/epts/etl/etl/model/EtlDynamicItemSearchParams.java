package org.openmrs.module.epts.etl.etl.model;

import org.openmrs.module.epts.etl.conf.datasource.EtlItemSrcConf;

public class EtlDynamicItemSearchParams extends EtlDatabaseObjectSearchParams {

	public EtlDynamicItemSearchParams(EtlItemSrcConf relatedItem) {
		super(relatedItem, null);
	}
}
