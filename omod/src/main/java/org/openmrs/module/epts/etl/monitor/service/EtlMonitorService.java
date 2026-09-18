package org.openmrs.module.epts.etl.monitor.service;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.monitor.EtlMonitor;
import org.openmrs.module.epts.etl.monitor.dto.MonitorInfoDTO;
import org.openmrs.module.epts.etl.monitor.dto.TaskMonitorInfoDTO;
import org.openmrs.module.epts.etl.monitor.model.EtlMonitorSnapshot;
import org.openmrs.module.epts.etl.monitor.model.TaskMonitorInfo;
import org.openmrs.module.epts.etl.service.EtlProcessService;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Facade responsible for exposing ETL monitoring information
 * to the web layer.
 * <p>
 * It does not execute ETL operations.
 * It only reads the current ProcessController and its monitor.
 * <p>
 * Web
 * ↓
 * EtlMonitorService
 * ↓
 * EtlProcessService
 * ↓
 * ProcessController
 * ↓
 * EtlMonitor
 */
@Service
public class EtlMonitorService {

    private final EtlProcessService processService;

    @Autowired
    public EtlMonitorService(EtlProcessService processService) {
        this.processService = processService;
    }

    // =========================================================
    // CURRENT MONITOR
    // =========================================================

    /**
     * Returns the monitor information for the current ETL process.
     */
    public MonitorInfoDTO getMonitor() throws DBException {

        ProcessController controller =
                processService.getCurrentController();

        MonitorInfoDTO monitor =
                new MonitorInfoDTO();

        // -----------------------------------------------------
        // ETL NOT INITIALIZED
        // -----------------------------------------------------

        if (controller == null) {


            monitor.setOperationId(null);

            if (processService.isEnabled()) {

                monitor.setStatus("NOT_INITIALIZED");
                monitor.setMessage(
                        "ETL is not initialized."
                );

            } else {

                monitor.setStatus("DISABLED");
                monitor.setMessage(
                        "ETL is disabled."
                );
            }

            monitor.setProgress(0.0);
            monitor.setProcessed(0L);
            monitor.setTotal(0L);
            monitor.setTasks(null);

            return monitor;


        }

        // -----------------------------------------------------
        // IDENTIFICATION
        // -----------------------------------------------------

        monitor.setOperationId(
                controller.getControllerId()
        );

        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        if (controller.isRunning()) {


            monitor.setStatus("RUNNING");
            monitor.setMessage(
                    "ETL is running."
            );


        } else if (controller.isStopped()) {


            monitor.setStatus("STOPPED");
            monitor.setMessage(
                    "ETL is stopped."
            );


        } else if (controller.isFinished()) {


            monitor.setStatus("FINISHED");
            monitor.setMessage(
                    "ETL process is finished."
            );


        } else {


            monitor.setStatus("INITIALIZED");
            monitor.setMessage(
                    "ETL is initialized."
            );


        }

        // -----------------------------------------------------
        // REAL ETL MONITOR
        // -----------------------------------------------------

        EtlMonitor etlMonitor =
                controller.getMonitor();

        if (etlMonitor != null) {


            EtlMonitorSnapshot snapshot =
                    etlMonitor.getSnapshot();

            if (snapshot != null) {

                monitor.setProgress(
                        snapshot.getProgress()
                );

                monitor.setProcessed(
                        (long) snapshot.getProcessed()
                );

                monitor.setTotal(
                        (long) snapshot.getTotal()
                );

            } else {

                monitor.setProgress(0.0);
                monitor.setProcessed(0L);
                monitor.setTotal(0L);
            }

            // -------------------------------------------------
            // TASKS
            // -------------------------------------------------

            monitor.setTasks(
                    convertTasks(etlMonitor.getTasks())
            );


        } else {


            monitor.setProgress(0.0);
            monitor.setProcessed(0L);
            monitor.setTotal(0L);
            monitor.setTasks(null);


        }

        return monitor;
    }

    // =========================================================
    // TASK CONVERSION
    // =========================================================

    /**
     * Converts the internal monitoring task model into the DTO
     * exposed by the web layer.
     */
    private List<TaskMonitorInfoDTO> convertTasks(
            List<TaskMonitorInfo> tasks) {

        if (tasks == null || tasks.isEmpty()) {
            return null;
        }

        List<TaskMonitorInfoDTO> result =
                new ArrayList<TaskMonitorInfoDTO>();

        for (TaskMonitorInfo task : tasks) {


            if (task == null) {
                continue;
            }

            TaskMonitorInfoDTO dto =
                    new TaskMonitorInfoDTO();

            dto.setTaskId(task.getTaskId());

            // -------------------------------------------------
            // STATUS
            // -------------------------------------------------

            if (task.getStatus() != null) {
                dto.setStatus(
                        task.getStatus().name()
                );
            }

            // -------------------------------------------------
            // PROCESSED
            // -------------------------------------------------

            dto.setProcessed(
                    (long) task.getProcessed()
            );

            // -------------------------------------------------
            // TOTAL
            // -------------------------------------------------

            long total = calculateTaskTotal(task);

            dto.setTotal(total);

            // -------------------------------------------------
            // PROGRESS
            // -------------------------------------------------

            dto.setProgress(
                    calculateTaskProgress(
                            task.getProcessed(),
                            total
                    )
            );

            // -------------------------------------------------
            // MESSAGE
            // -------------------------------------------------

            if (task.getErrors() > 0) {

                dto.setMessage(
                        task.getErrors()
                                + " error(s) detected."
                );

            } else if (task.getStatus() != null) {

                dto.setMessage(
                        task.getStatus().name()
                );

            } else {

                dto.setMessage(null);
            }

            result.add(dto);


        }

        return result;
    }

    /**
     * Calculates the total number of records represented by a task.
     * <p>
     * The task model exposes the record range through minRecord
     * and maxRecord.
     */
    private long calculateTaskTotal(TaskMonitorInfo task) {

        long minRecord = task.getMinRecord();
        long maxRecord = task.getMaxRecord();

        if (maxRecord < minRecord) {
            return 0L;
        }

        return maxRecord - minRecord + 1L;
    }

    /**
     * Calculates task progress as a percentage.
     */
    private double calculateTaskProgress(
            long processed,
            long total) {

        if (total <= 0L) {
            return 0.0;
        }

        double progress =
                ((double) processed / (double) total) * 100.0;

        if (progress < 0.0) {
            return 0.0;
        }

        if (progress > 100.0) {
            return 100.0;
        }

        return progress;
    }

    // =========================================================
    // CURRENT CONTROLLER
    // =========================================================

    /**
     * Returns the current ETL controller.
     */
    public ProcessController getCurrentController()
            throws DBException {

        return processService.getCurrentController();
    }
}
