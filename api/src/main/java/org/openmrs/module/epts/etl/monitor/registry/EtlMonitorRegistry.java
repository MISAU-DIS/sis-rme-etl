package org.openmrs.module.epts.etl.monitor.registry;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.monitor.EtlMonitor;



public class EtlMonitorRegistry {
// Responsável por saber o que está a correr neste momento
    // Aqui fica o armazenamento em memória.

    private static final EtlMonitorRegistry INSTANCE =
            new EtlMonitorRegistry();



    private Map<String,EtlMonitor> monitors =
            new ConcurrentHashMap<>();



    private EtlMonitorRegistry(){}



    public static EtlMonitorRegistry getInstance(){

        return INSTANCE;

    }

    public int size(){

        return monitors.size();

    }

    public boolean exists(String id){

        return monitors.containsKey(id);

    }



    public void register(EtlMonitor monitor){

        monitors.put(
                monitor.getOperationId(),
                monitor
        );

    }



    public void remove(String id){

        monitors.remove(id);

    }



    public EtlMonitor find(String id){

        return monitors.get(id);

    }



    public List<EtlMonitor> findAll(){

        return new ArrayList<>(
                monitors.values()
        );

    }


}
