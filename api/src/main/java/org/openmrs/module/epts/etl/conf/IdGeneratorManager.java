package org.openmrs.module.epts.etl.conf;

import java.util.concurrent.atomic.AtomicLong;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/**
 * Generates IDs for a single destination configuration.
 */
public class IdGeneratorManager extends AbstractEtlDataConfiguration {

	private final AtomicLong lastGeneratedId;

	private final DstConf dstConf;

	IdGeneratorManager(DstConf dstConf, long lastGeneratedId) {
		this.dstConf = dstConf;
		this.lastGeneratedId = new AtomicLong(lastGeneratedId);
	}

	public DstConf getDstConf() {
		return dstConf;
	}

	public long retrieveNextIdForRecord() throws ForbiddenOperationException {
		return lastGeneratedId.incrementAndGet();
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.dstConf != null ? this.dstConf.getRelatedEtlConf() : null;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.dstConf;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {

	}

}
