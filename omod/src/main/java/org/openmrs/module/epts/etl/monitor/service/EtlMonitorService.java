package org.openmrs.module.epts.etl.monitor.service;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.controller.ProcessStarter;
import org.openmrs.module.epts.etl.engine.EtlProgressMeter;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.OperationProgressInfo;
import org.openmrs.module.epts.etl.model.TableOperationProgressInfo;
import org.openmrs.module.epts.etl.monitor.dto.MonitorInfoDTO;
import org.openmrs.module.epts.etl.monitor.dto.TaskMonitorInfoDTO;
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
			monitor.setOperationIdentifier(null);
			monitor.setProgress(0.0);
			monitor.setProcessed(0L);
			monitor.setTotal(0L);
			monitor.setTasks(new ArrayList<TaskMonitorInfoDTO>());

			if (processService.isEnabled()) {
				monitor.setStatus("NOT_INITIALIZED");
				monitor.setMessage("ETL is not initialized.");
			} else {
				monitor.setStatus("DISABLED");
				monitor.setMessage("ETL is disabled.");
			}

			return monitor;
		}

		List<ProcessController> processes = this.getStarter().getCurrentControllers();

		int runningProcess = 0;
		int stoppedProcess = 0;
		int finishedProcess = 0;

		for (ProcessController p : processes) {
			runningProcess += p.isRunning() ? 1 : 0;
			stoppedProcess += p.isStopped() ? 1 : 0;
			finishedProcess += p.isFinished() ? 1 : 0;

			List<OperationController<? extends EtlDatabaseObject>> operationController = p.getOperationsControllers();

			for (OperationController<? extends EtlDatabaseObject> operation : operationController) {
				TableOperationProgressInfo item = operation.getCurrentActivititieMonitor()
						.getTableOperationProgressInfo();

				TaskMonitorInfoDTO task = createTaskInfo(operation.getProgressInfo(), item);

				monitor.getTasks().add(task);
			}
		}

		if (runningProcess > 0 && stoppedProcess == 0) {
			monitor.setStatus("RUNNING");
			monitor.setMessage("All Processes are running.");

		} else if (stoppedProcess > 0 && runningProcess == 0) {
			monitor.setStatus("STOPPED");
			monitor.setMessage("All Processes are stopped.");

		} else if (finishedProcess > 0 && runningProcess == 0) {
			monitor.setStatus("FINISHED");
			monitor.setMessage("All process are finished.");
		} else if (runningProcess > 0 && stoppedProcess > 0) {
			monitor.setStatus("RUNNING");
			monitor.setMessage("Processes Partial running.");
		} else {
			monitor.setStatus("INITIALIZED");
			monitor.setMessage("ETL is initialized.");
		}

		setControllerStatus(monitor);

		return monitor;
	}

	private ProcessStarter getStarter() {
		return this.processService.getProcessStarter();
	}

	private TaskMonitorInfoDTO createTaskInfo(OperationProgressInfo operation, TableOperationProgressInfo item) {
		EtlProgressMeter meter = item.getProgressMeter();

		TaskMonitorInfoDTO task = new TaskMonitorInfoDTO();

		String taskId;

		try {
			taskId = item.getOperationId();
		} catch (Exception e) {
			taskId = operation.getOperationName();
		}

		task.setTaskId(taskId);

		task.setStatus(meter.getStatus() != null ? meter.getStatus().name() : "NOT_INITIALIZED");

		String message = meter.getStatusMsg();

		if (message == null || message.trim().isEmpty()) {
			message = meter.getStatus() != null ? meter.getStatus().getDsc() : "";
		}

		if (meter.isStatusError()) {
			task.setStatus("ERROR");
		}

		task.setMessage(message);

		long total = meter.getTotal();
		long processed = meter.getProcessed();

		task.setTotal(total);
		task.setProcessed(processed);

		if (total > 0) {
			task.setProgress(roundProgress(((double) processed / (double) total) * 100.0));
		} else {
			task.setProgress(0.0);
		}

		return task;
	}

	private void setControllerStatus(MonitorInfoDTO monitor) {
	}

	private double roundProgress(double progress) {
		return Math.round(progress * 100.0) / 100.0;
	}
}
