package org.openmrs.module.epts.etl.monitor.dto;

import java.util.List;

public class MonitorInfoDTO {

    private String operationIdentifier;
    private String status;
    private String message;
    private Double progress;
    private Long processed;
    private Long total;
    private List<TaskMonitorInfoDTO> tasks;

    public MonitorInfoDTO() {
    }

    public String getOperationIdentifier() {
        return operationIdentifier;
    }

    public void setOperationIdentifier(String operationIdentifier) {
        this.operationIdentifier = operationIdentifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Long getProcessed() {
        return processed;
    }

    public void setProcessed(Long processed) {
        this.processed = processed;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<TaskMonitorInfoDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskMonitorInfoDTO> tasks) {
        this.tasks = tasks;
    }
}
