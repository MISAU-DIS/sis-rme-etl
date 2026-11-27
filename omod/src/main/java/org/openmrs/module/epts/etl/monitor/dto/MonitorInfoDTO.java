package org.openmrs.module.epts.etl.monitor.dto;

import java.util.List;
import java.util.ArrayList;

public class MonitorInfoDTO {

    private String status;
    private String message;
    private List<OperationMonitorInfoDTO> operations = new ArrayList<>();

    public MonitorInfoDTO() {
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

    public List<OperationMonitorInfoDTO> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationMonitorInfoDTO> operations) {
        this.operations = operations;
    }
}
