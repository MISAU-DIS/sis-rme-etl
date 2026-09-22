package org.openmrs.module.epts.etl.monitor.service;

import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.monitor.dto.MonitorInfoDTO;
import org.openmrs.module.epts.etl.service.EtlProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Facade responsible for exposing ETL monitoring information
 * to the web layer.
 *
 * It only reads the current ProcessController state.
 * It does not execute ETL operations.
 */
@Service
public class EtlMonitorService {

    private final EtlProcessService processService;

    @Autowired
    public EtlMonitorService(EtlProcessService processService) {
        this.processService = processService;
    }

    /**
     * Returns monitoring information for the current ETL process.
     */
    public MonitorInfoDTO getMonitor() {

        ProcessController controller =
                processService.getCurrentController();

        MonitorInfoDTO monitor =
                new MonitorInfoDTO();

        // =====================================================
        // ETL NOT INITIALIZED
        // =====================================================

        if (controller == null) {

            monitor.setOperationId(null);
            monitor.setProgress(0.0);
            monitor.setProcessed(0L);
            monitor.setTotal(0L);
            monitor.setTasks(null);

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

            return monitor;
        }

        // =====================================================
        // OPERATION IDENTIFICATION
        // =====================================================

        monitor.setOperationId(
                controller.getControllerId()
        );

        // =====================================================
        // STATUS
        // =====================================================

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

        // =====================================================
        // PROGRESS
        // =====================================================
        //
        // The current ProcessController does not expose
        // EtlMonitor/EtlMonitorSnapshot anymore.
        //
        // Until we identify the existing core progress API,
        // keep these values at their safe default.
        //

        monitor.setProgress(0.0);
        monitor.setProcessed(0L);
        monitor.setTotal(0L);
        monitor.setTasks(null);

        return monitor;
    }

    /**
     * Returns the current ETL controller.
     */
    public ProcessController getCurrentController() {

        return processService.getCurrentController();
    }
}