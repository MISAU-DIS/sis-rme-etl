package org.openmrs.module.epts.etl.monitor.model;

import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;

public class TaskMonitorInfo {

    private String taskId;

    private String engineId;

    private EtlOperationStatus status;

    private long minRecord;

    private long maxRecord;

    private int processed;

    private int errors;

    private String processorId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getEngineId() {
        return engineId;
    }

    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    public EtlOperationStatus getStatus() {
        return status;
    }

    public void setStatus(EtlOperationStatus status) {
        this.status = status;
    }

    public long getMinRecord() {
        return minRecord;
    }

    public void setMinRecord(long minRecord) {
        this.minRecord = minRecord;
    }

    public long getMaxRecord() {
        return maxRecord;
    }

    public void setMaxRecord(long maxRecord) {
        this.maxRecord = maxRecord;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }
}