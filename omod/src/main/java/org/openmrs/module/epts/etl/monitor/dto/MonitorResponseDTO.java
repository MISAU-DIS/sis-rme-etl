package org.openmrs.module.epts.etl.monitor.dto;


import java.util.List;


public class MonitorResponseDTO {
    // Resposta principal.

    private String operationId;


    private String status;


    private String message;


    private List<EngineMonitorDTO> engines;



    public String getOperationId() {
        return operationId;
    }


    public void setOperationId(String operationId) {
        this.operationId = operationId;
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



    public List<EngineMonitorDTO> getEngines() {
        return engines;
    }


    public void setEngines(List<EngineMonitorDTO> engines) {
        this.engines = engines;
    }

}