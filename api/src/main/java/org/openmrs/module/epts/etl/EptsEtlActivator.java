package org.openmrs.module.epts.etl;

import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.epts.etl.utilities.EtlLogger;

public class EptsEtlActivator extends BaseModuleActivator {

	private static final EtlLogger LOG =
			EtlLogger.getLogger(EptsEtlActivator.class);

	@Override
	public void willStart() {
		LOG.info("Starting Epts Synchronize Module");
	}

	@Override
	public void started() {
		LOG.info("Epts Synchronize Module started");

		try {
			/*
			 * OpenMRS is now starting the ETL through the
			 * single official startup entry point.
			 *
			 * Main is responsible for reading:
			 *
			 *   epts.etl.enabled
			 *   epts.etl.startup.file
			 */
			Main.startFromOpenMRS();

		} catch (Exception e) {
			/*
			 * Do not prevent OpenMRS from continuing its startup
			 * just because the ETL could not be started.
			 */
			LOG.err("Failed to start ETL from OpenMRS", e);
		}
	}

	@Override
	public void willRefreshContext() {
		LOG.info("Refreshing Epts Synchronize Module");
	}

	@Override
	public void contextRefreshed() {
		LOG.info("Epts Synchronize Module refreshed");
	}

	@Override
	public void willStop() {
		LOG.info("Stopping Epts Synchronize Module");
	}

	@Override
	public void stopped() {
		LOG.info("Epts Synchronize Module stopped");
	}
}