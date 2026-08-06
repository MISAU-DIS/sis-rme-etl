package org.openmrs.module.epts.etl.monitor.dto;


import java.util.List;


public class EngineMonitorDTO {
    // Representa cada engine.

    private String engineId;


    private Integer progress;


    private String status;


    private List<TaskDTO> tasks;



    public String getEngineId() {
        return engineId;
    }


    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }



    public Integer getProgress() {
        return progress;
    }


    public void setProgress(Integer progress) {
        this.progress = progress;
    }



    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }



    public List<TaskDTO> getTasks() {
        return tasks;
    }


    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

}