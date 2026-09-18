package org.openmrs.module.epts.etl.monitor.collector;

import org.openmrs.module.epts.etl.monitor.model.TaskMonitorInfo;
import org.openmrs.module.epts.etl.processor.TaskProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects monitoring information from active ETL task processors.
 *
 * <p>
 * This class is responsible only for reading the current state of
 * {@link TaskProcessor} instances and converting that information into
 * {@link TaskMonitorInfo} objects.
 * </p>
 *
 * <p>
 * It does not execute, start, stop, or modify task processors.
 * </p>
 */
public class TaskProcessorCollector {

    /**
     * Collects monitoring information from the supplied task processors.
     *
     * @param engineId identifier of the engine associated with the processors
     * @param processors active task processors
     * @return list containing the monitoring information of each valid processor
     */
    public List<TaskMonitorInfo> collect(
            String engineId,
            List<? extends TaskProcessor<?>> processors) {

        List<TaskMonitorInfo> result =
                new ArrayList<TaskMonitorInfo>();

        // Nothing to collect when there are no active processors.
        if (processors == null || processors.isEmpty()) {
            return result;
        }

        for (TaskProcessor<?> processor : processors) {

            // Ignore null entries to avoid breaking the entire collection.
            if (processor == null) {
                continue;
            }

            TaskMonitorInfo info =
                    new TaskMonitorInfo();

            /*
             * Engine identification.
             */
            info.setEngineId(
                    engineId
            );

            /*
             * Processor identification.
             */
            info.setProcessorId(
                    processor.getProcessorId()
            );

            /*
             * Task identification.
             *
             * TaskProcessor currently does not expose a separate
             * task identifier, therefore the processor identifier
             * is used as the task identifier.
             */
            info.setTaskId(
                    processor.getProcessorId()
            );

            /*
             * Current processor status.
             */
            info.setStatus(
                    processor.getOperationStatus()
            );

            /*
             * Processing limits.
             *
             * The limits are optional, therefore they must be checked
             * before accessing their values.
             */
            if (processor.getLimits() != null) {

                info.setMinRecord(
                        processor.getLimits().getMinRecordId()
                );

                info.setMaxRecord(
                        processor.getLimits().getMaxRecordId()
                );
            }

            result.add(info);
        }

        return result;
    }
}