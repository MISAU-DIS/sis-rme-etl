package org.openmrs.module.epts.etl.monitor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.module.epts.etl.monitor.model.EtlMonitorSnapshot;
import org.openmrs.module.epts.etl.monitor.model.TaskMonitorInfo;


public class EtlMonitor {


    private final String operationId;


    private volatile EtlMonitorSnapshot snapshot;


    private volatile List<TaskMonitorInfo> tasks =
            Collections.emptyList();

    private Date startTime;



    public EtlMonitor(String operationId) {
        this.operationId = operationId;
        this.startTime = new Date();
    }



    public String getOperationId() {
        return operationId;
    }



    public EtlMonitorSnapshot getSnapshot() {
        return snapshot;
    }



    public void updateSnapshot(EtlMonitorSnapshot snapshot) {

        this.snapshot = snapshot;

    }



    public List<TaskMonitorInfo> getTasks() {
        return tasks;
    }



    public synchronized void updateTasks(
            List<TaskMonitorInfo> tasks){

        this.tasks =
                tasks == null
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(tasks);

    }


}