package org.openmrs.module.epts.etl.monitor.model;


import java.util.Date;

import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;



public class EtlMonitorSnapshot {
    // Representa o estado do progress meter.


    private String operationId;


    private EtlOperationStatus status;


    private int total;


    private int processed;


    private int remaining;


    private double progress;


    private Date capturedAt;

    private Date startTime;

    private Date finishTime;

    private String processingTime;

    private String totalTime;

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


    public Date getStartTime() {
        return startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public String getProcessingTime() {
        return processingTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public String getEstimatedRemainingTime() {
        return estimatedRemainingTime;
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


    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public void setEstimatedRemainingTime(String estimatedRemainingTime) {
        this.estimatedRemainingTime = estimatedRemainingTime;
    }
}