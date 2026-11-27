package org.openmrs.module.epts.etl.service;

import java.io.IOException;
import java.util.List;

import org.openmrs.module.epts.etl.Main;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.controller.ProcessStarter;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.springframework.stereotype.Service;

@Service
public class EtlProcessService {

	private volatile ProcessStarter processStarter;

	/**
	 * Gets the ProcessStarter instance that is actually managed by the OpenMRS ETL
	 * lifecycle.
	 */
	public synchronized void initialize() {

		if (this.processStarter != null) {
			return;
		}

		ProcessStarter starter = Main.getOpenMrsProcessStarter();

		if (starter != null) {
			this.processStarter = starter;
		}
	}

	public boolean isEnabled() {
		return Main.isOpenMrsEtlEnabled();
	}

	public boolean isInitialized() {

		initialize();

		return processStarter != null;
	}

	public ProcessStarter getProcessStarter() {
		initialize();

		return processStarter;
	}

	public List<ProcessController> getCurrentControllers() {

		ProcessStarter starter = getProcessStarter();

		if (starter == null) {
			return null;
		}

		return starter.getCurrentControllers();
	}

	/**
	 * Starts the ETL using the ProcessStarter instance already created by
	 * Main/OpenMRS.
	 *
	 * This method does NOT create another ProcessStarter.
	 * 
	 * @throws DBException
	 * @throws IOException
	 */
	public synchronized void start() throws DBException, IOException {
		ProcessStarter starter = getProcessStarter();
		if (starter == null) {
			Main.startFromOpenMRS();
		} else if (starter.isFinalized() && Main.restartFromOpenMRS()) {
			this.processStarter = Main.getOpenMrsProcessStarter();
		}
	}

	/**
	 * Requests the current ETL controller to stop.
	 */
	public synchronized void stop() {
		ProcessStarter starter = getProcessStarter();
		if (starter != null) {
			starter.requestStop();
		}
	}
}
