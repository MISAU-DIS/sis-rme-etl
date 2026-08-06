package org.openmrs.module.epts.etl.monitor.context;

import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.monitor.EtlMonitor;

import java.util.ArrayList;
import java.util.List;


public class EtlExecutionContext {


    private final String operationId;


    private final List<Engine<? extends EtlDatabaseObject>> engines;


    private EtlMonitor monitor;



    public EtlExecutionContext(String operationId){

        this.operationId = operationId;
        this.engines = new ArrayList<>();

    }



    public String getOperationId(){

        return operationId;

    }



    public void addEngine(
            Engine<? extends EtlDatabaseObject> engine){

        engines.add(engine);

    }



    public List<Engine<? extends EtlDatabaseObject>> getEngines(){

        return engines;

    }



    public EtlMonitor getMonitor(){

        return monitor;

    }



    public void setMonitor(EtlMonitor monitor){

        this.monitor = monitor;

    }

}