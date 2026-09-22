package org.openmrs.module.epts.etl.monitor.model;

import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;

import java.util.Date;

/**
 * Represents a snapshot of the runtime state of an ETL operation.
 *
 * <p>
 * This class is a data model only. It does not calculate progress
 * and it does not control the ETL execution.
 * </p>
 *
 * <p>
 * The snapshot is updated by the monitoring layer using the real
 * {@code ProcessController}, {@code OperationController} and
 * {@code Engine} instances.
 * </p>
 */
public class EtlMonitorSnapshot {

    /**
     * Identifier of the ETL process/operation being monitored.
     */
    private String operationId;

    /**
     * Current status of the ETL operation.
     */
    private EtlOperationStatus status;

    /**
     * Total number of records expected to be processed.
     */
    private int total;

    /**
     * Number of records already processed.
     */
    private int processed;

    /**
     * Number of records still remaining.
     */
    private int remaining;

    /**
     * Current progress percentage/value.
     */
    private double progress;

    /**
     * Date and time when this snapshot was captured.
     */
    private Date capturedAt;

    /**
     * Date and time when the ETL operation started.
     */
    private Date startTime;

    /**
     * Date and time when the ETL operation finished.
     */
    private Date finishTime;

    /**
     * Time spent actively processing records.
     *
     * <p>
     * Kept as a String because the monitor/UI can decide how
     * the duration should be displayed.
     * </p>
     */
    private String processingTime;

    /**
     * Total elapsed time of the ETL operation.
     */
    private String totalTime;

    /**
     * Estimated time remaining for the ETL operation.
     */
    private String estimatedRemainingTime;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public EtlOperationStatus getStatus() {
        return status;
    }

    public void setStatus(EtlOperationStatus status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Date getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Date capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getEstimatedRemainingTime() {
        return estimatedRemainingTime;
    }

    public void setEstimatedRemainingTime(
            String estimatedRemainingTime) {

        this.estimatedRemainingTime = estimatedRemainingTime;
    }
}