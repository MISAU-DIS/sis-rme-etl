package org.openmrs.module.epts.etl;

import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.epts.etl.utilities.EtlLogger;

public class EptsEtlActivator extends BaseModuleActivator {

	private static final EtlLogger LOG =
			EtlLogger.getLogger(EptsEtlActivator.class);

	@Override
	public void willStart() {
		LOG.info("Starting EptsEtl Module");
	}

	@Override
	public void started() {
		LOG.info("EptsEtl Module started");

		try {
			/*
			 * OpenMRS is now starting the ETL through the
			 * single official startup entry point.
			 *
			 * Main is responsible for reading:
			 *
				 *   eptsetl.enabled
				 *   eptsetl.startupFile
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
		LOG.info("Refreshing EptsEtl Module");
	}

	@Override
	public void contextRefreshed() {
		LOG.info("EptsEtl Module refreshed");
	}

	@Override
	public void willStop() {
		LOG.info("Stopping EptsEtl Module");
	}

	@Override
	public void stopped() {
		LOG.info("EptsEtl Module stopped");
	}
}
