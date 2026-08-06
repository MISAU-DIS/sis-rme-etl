package org.openmrs.module.epts.etl.monitor;


import java.util.List;

import org.openmrs.module.epts.etl.monitor.registry.EtlMonitorRegistry;
import org.openmrs.module.epts.etl.monitor.model.EtlMonitorSnapshot;


public class EtlMonitorService {


    private EtlMonitorRegistry registry;



    public EtlMonitorService() {

        this.registry = EtlMonitorRegistry.getInstance();

    }



    public List<EtlMonitor> findAll(){

        return registry.findAll();

    }



    public EtlMonitor findById(String operationId){

        return registry.find(operationId);

    }



    public EtlMonitorSnapshot getSnapshot(String operationId){

        EtlMonitor monitor =
                registry.find(operationId);


        if(monitor == null)
            return null;


        return monitor.getSnapshot();

    }



}