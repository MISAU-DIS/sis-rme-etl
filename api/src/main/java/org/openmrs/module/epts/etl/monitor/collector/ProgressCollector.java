package org.openmrs.module.epts.etl.monitor.collector;

import org.openmrs.module.epts.etl.engine.EtlProgressMeter;
import org.openmrs.module.epts.etl.monitor.model.EtlMonitorSnapshot;

import java.util.Date;

/**
 * Collects the current progress information of an ETL operation.
 *
 * <p>
 * This class is responsible only for converting the state exposed by
 * {@link EtlProgressMeter} into an {@link EtlMonitorSnapshot}.
 * </p>
 *
 * <p>
 * It does not execute the ETL process and does not store the snapshot.
 * The resulting snapshot is returned to the caller, which decides what
 * to do with it.
 * </p>
 */
public class ProgressCollector {

    /**
     * Creates a monitoring snapshot from the current state of an
     * {@link EtlProgressMeter}.
     *
     * @param operationId identifier of the ETL operation being monitored
     * @param meter current progress meter of the operation
     * @return a snapshot containing the current progress information,
     *         or {@code null} when the meter is not available
     */
    public EtlMonitorSnapshot collect(
            String operationId,
            EtlProgressMeter meter) {

        // There is nothing to collect when the progress meter is unavailable.
        if (meter == null) {
            return null;
        }

        EtlMonitorSnapshot snapshot = new EtlMonitorSnapshot();

        // Identify the operation represented by this snapshot.
        snapshot.setOperationId(operationId);

        // Current ETL execution status.
        snapshot.setStatus(
                meter.getStatus()
        );

        // Total number of records expected to be processed.
        snapshot.setTotal(
                meter.getTotal()
        );

        // Number of records already processed.
        snapshot.setProcessed(
                meter.getProcessed()
        );

        // Number of records still remaining.
        snapshot.setRemaining(
                meter.getRemain()
        );

        // Progress percentage/value calculated by the progress meter.
        snapshot.setProgress(
                meter.getProgress()
        );

        // Timestamp indicating when this snapshot was collected.
        snapshot.setCapturedAt(
                new Date()
        );

        return snapshot;
    }
}