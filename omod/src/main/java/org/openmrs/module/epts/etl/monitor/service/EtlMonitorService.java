package org.openmrs.module.epts.etl.monitor.service;

import java.util.List;

import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.controller.ProcessStarter;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.EtlProgressMeter;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.TableOperationProgressInfo;
import org.openmrs.module.epts.etl.monitor.dto.MonitorInfoDTO;
import org.openmrs.module.epts.etl.monitor.dto.OperationMonitorInfoDTO;
import org.openmrs.module.epts.etl.service.EtlProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EtlMonitorService {

	private final EtlProcessService processService;

	@Autowired
	public EtlMonitorService(EtlProcessService processService) {
		this.processService = processService;
	}

	public MonitorInfoDTO retrieveMonitor() {
		MonitorInfoDTO monitor = new MonitorInfoDTO();

		if (this.processService.getProcessStarter() == null) {

			if (processService.isEnabled()) {
				monitor.setStatus("NOT_INITIALIZED");
				monitor.setMessage("ETL is not initialized.");
			} else {
				monitor.setStatus("DISABLED");
				monitor.setMessage("ETL is disabled.");
			}

			return monitor;
		}

		ProcessStarter starter = this.getStarter();
		List<ProcessController> processes = starter.getCurrentControllers();

		int runningProcess = 0;
		int stoppedProcess = 0;
		int finishedProcess = 0;

		for (ProcessController p : processes) {
			runningProcess += p.isRunning() ? 1 : 0;
			stoppedProcess += p.isStopped() ? 1 : 0;
			finishedProcess += p.isFinished() ? 1 : 0;

			List<OperationController<? extends EtlDatabaseObject>> runningOperations = p.getOperationsControllers();

			if (runningOperations == null) {
				continue;
			}

			for (OperationController<? extends EtlDatabaseObject> operation : runningOperations) {
				monitor.getOperations().add(createOperationInfo(p, operation));
			}
		}

		int processCount = processes.size();

		if (processCount == 0) {

			if (!starter.isStarted()) {
				monitor.setStatus("INITIALIZED");
				monitor.setMessage("ETL is initialized.");
			} else if (starter.isStopRequested()) {
				monitor.setStatus("STOPPED");
				monitor.setMessage("All processes are stopped.");
			} else {
				monitor.setStatus("FINISHED");
				monitor.setMessage("All processes are finished.");
			}
		} else if (runningProcess == processCount) {
			monitor.setStatus("RUNNING");
			monitor.setMessage("All Processes are running.");

		} else if (stoppedProcess == processCount) {
			monitor.setStatus("STOPPED");
			monitor.setMessage("All Processes are stopped.");

		} else if (finishedProcess == processCount) {
			monitor.setStatus("FINISHED");
			monitor.setMessage("All process are finished.");
		} else if (runningProcess > 0) {
			monitor.setStatus("RUNNING");
			monitor.setMessage("Processes are partially running.");
		} else if (stoppedProcess > 0) {
			monitor.setStatus("STOPPED");
			monitor.setMessage("Processes are partially stopped.");
		} else {
			monitor.setStatus("INITIALIZED");
			monitor.setMessage("ETL is initialized.");
		}

		return monitor;
	}

	private ProcessStarter getStarter() {
		return this.processService.getProcessStarter();
	}

	private OperationMonitorInfoDTO createOperationInfo(ProcessController process,
			OperationController<? extends EtlDatabaseObject> operation) {

		OperationMonitorInfoDTO info = new OperationMonitorInfoDTO();

		info.setProcessId(process.getControllerId());
		info.setOperationIdentifier(operation.getOperationIdentifier());
		info.setStatus(
				operation.getOperationStatus() != null ? operation.getOperationStatus().name() : "NOT_INITIALIZED");
		info.setMessage(operation.getOperationStatus() != null ? operation.getOperationStatus().getDsc() : "");
		info.setProgress(0.0);
		info.setProcessed(0L);
		info.setTotal(0L);

		Engine<?> engine = operation.getCurrentActivititieMonitor();

		if (engine == null) {
			return info;
		}

		info.setEngineId(engine.getEngineId());

		TableOperationProgressInfo item = engine.getTableOperationProgressInfo();

		if (item == null || item.getProgressMeter() == null) {
			return info;
		}

		EtlProgressMeter meter = item.getProgressMeter();

		info.setStatus(meter.getStatus() != null ? meter.getStatus().name() : info.getStatus());

		String message = meter.getLastException() != null ? meter.getLastException().getMessage()
				: meter.getStatusMsg();

		if (message == null || message.trim().isEmpty()) {
			message = meter.getStatus() != null ? meter.getStatus().getDsc() : "";
		}

		if (meter.isStatusError()) {
			info.setStatus("ERROR");
		}

		info.setMessage(message);

		long total = meter.getTotal();
		long processed = meter.getProcessed();

		info.setTotal(total);
		info.setProcessed(processed);

		if (total > 0) {
			info.setProgress(roundProgress(((double) processed / (double) total) * 100.0));
		}

		return info;
	}

	private double roundProgress(double progress) {
		return Math.round(progress * 100.0) / 100.0;
	}
}
