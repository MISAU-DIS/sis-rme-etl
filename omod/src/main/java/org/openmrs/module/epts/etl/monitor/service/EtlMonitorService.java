package org.openmrs.module.epts.etl.monitor.service;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.engine.EtlProgressMeter;
import org.openmrs.module.epts.etl.model.OperationProgressInfo;
import org.openmrs.module.epts.etl.model.ProcessProgressInfo;
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

    public MonitorInfoDTO getMonitor() {

        MonitorInfoDTO monitor = new MonitorInfoDTO();

        ProcessController controller =
                processService.getCurrentController();

        if (controller == null) {
            monitor.setOperationId(null);
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

        monitor.setOperationId(controller.getControllerId());

        ProcessProgressInfo processProgressInfo =
                controller.getProgressInfo();

        if (processProgressInfo == null) {
            setControllerStatus(monitor, controller);

            monitor.setProgress(0.0);
            monitor.setProcessed(0L);
            monitor.setTotal(0L);
            monitor.setTasks(new ArrayList<TaskMonitorInfoDTO>());

            return monitor;
        }

        List<OperationProgressInfo> operations =
                processProgressInfo.getOperationsProgressInfo();

        long total = 0L;
        long processed = 0L;

        List<TaskMonitorInfoDTO> tasks =
                new ArrayList<TaskMonitorInfoDTO>();

        if (operations != null) {

            for (OperationProgressInfo operation : operations) {

                if (operation == null) {
                    continue;
                }

                List<TableOperationProgressInfo> items =
                        operation.getItemsProgressInfo();

                if (items == null) {
                    continue;
                }

                for (TableOperationProgressInfo item : items) {

                    if (item == null) {
                        continue;
                    }

                    EtlProgressMeter meter =
                            item.getProgressMeter();

                    if (meter == null) {
                        continue;
                    }

                    total += meter.getTotal();
                    processed += meter.getProcessed();

                    TaskMonitorInfoDTO task =
                            createTaskInfo(operation, item, meter);

                    tasks.add(task);
                }
            }
        }

        monitor.setTotal(total);
        monitor.setProcessed(processed);

        if (total > 0) {
            double progress =
                    ((double) processed / (double) total) * 100.0;

            monitor.setProgress(roundProgress(progress));
        } else {
            monitor.setProgress(0.0);
        }

        monitor.setTasks(tasks);

        setControllerStatus(monitor, controller);

        return monitor;
    }

    private TaskMonitorInfoDTO createTaskInfo(
            OperationProgressInfo operation,
            TableOperationProgressInfo item,
            EtlProgressMeter meter) {

        TaskMonitorInfoDTO task =
                new TaskMonitorInfoDTO();

        String taskId;

        try {
            taskId = item.getOperationId();
        } catch (Exception e) {
            taskId = operation.getOperationName();
        }

        task.setTaskId(taskId);

        task.setStatus(
                meter.getStatus() != null
                        ? meter.getStatus().name()
                        : "NOT_INITIALIZED"
        );

        String message = meter.getStatusMsg();

        if (message == null || message.trim().isEmpty()) {
            message = meter.getStatus() != null
                    ? meter.getStatus().getDsc()
                    : "";
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
            task.setProgress(
                    roundProgress(
                            ((double) processed / (double) total) * 100.0
                    )
            );
        } else {
            task.setProgress(0.0);
        }

        return task;
    }

    private void setControllerStatus(
            MonitorInfoDTO monitor,
            ProcessController controller) {

        if (controller.isRunning()) {
            monitor.setStatus("RUNNING");
            monitor.setMessage("ETL is running.");

        } else if (controller.isStopped()) {
            monitor.setStatus("STOPPED");
            monitor.setMessage("ETL is stopped.");

        } else if (controller.isFinished()) {
            monitor.setStatus("FINISHED");
            monitor.setMessage("ETL process is finished.");

        } else {
            monitor.setStatus("INITIALIZED");
            monitor.setMessage("ETL is initialized.");
        }
    }

    private double roundProgress(double progress) {
        return Math.round(progress * 100.0) / 100.0;
    }

    public ProcessController getCurrentController() {
        return processService.getCurrentController();
    }
}