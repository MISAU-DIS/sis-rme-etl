package org.openmrs.module.epts.etl.monitor.dto;


public class TaskDTO {


    private String taskId;


    private String processorId;


    private String status;


    private Long minRecord;


    private Long maxRecord;



    public String getTaskId() {
        return taskId;
    }


    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }



    public String getProcessorId() {
        return processorId;
    }


    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }



    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }



    public Long getMinRecord() {
        return minRecord;
    }


    public void setMinRecord(Long minRecord) {
        this.minRecord = minRecord;
    }



    public Long getMaxRecord() {
        return maxRecord;
    }


    public void setMaxRecord(Long maxRecord) {
        this.maxRecord = maxRecord;
    }

}