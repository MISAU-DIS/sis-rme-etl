package org.openmrs.module.epts.etl.monitor.collector;


import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.TaskProcessor;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.monitor.EtlMonitor;
import org.openmrs.module.epts.etl.monitor.context.EtlExecutionContext;
import org.openmrs.module.epts.etl.monitor.model.EtlMonitorSnapshot;
import org.openmrs.module.epts.etl.monitor.model.TaskMonitorInfo;
import org.openmrs.module.epts.etl.monitor.registry.EtlMonitorRegistry;



public class MonitorCollector {


    private final ProgressCollector progressCollector;

    private final TaskProcessorCollector taskProcessorCollector;



    public MonitorCollector(){

        this.progressCollector =
                new ProgressCollector();


        this.taskProcessorCollector =
                new TaskProcessorCollector();

    }



    /**
     * Collects monitoring information from an ETL execution context.
     *
     * The context contains all active engines associated
     * with an operation.
     *
     * @param context current ETL execution context
     */
    public void collect(EtlExecutionContext context){


        if(context == null){
            return;
        }


        String operationId =
                context.getOperationId();



        System.out.println(
                "MONITOR COLLECT OPERATION: "
                        + operationId
        );



        EtlMonitorRegistry registry =
                EtlMonitorRegistry.getInstance();



        EtlMonitor monitor =
                registry.find(operationId);



        if(monitor == null){

            monitor =
                    new EtlMonitor(operationId);


            registry.register(monitor);

        }



        List<Engine<? extends EtlDatabaseObject>> engines =
                context.getEngines();



        if(engines == null || engines.isEmpty()){
            return;
        }



        for(Engine<? extends EtlDatabaseObject> engine : engines){


            collectEngineState(
                    monitor,
                    engine
            );

        }

    }





    /**
     * Collect information from a single engine.
     */
    private void collectEngineState(
            EtlMonitor monitor,
            Engine<? extends EtlDatabaseObject> engine){



        if(engine == null){
            return;
        }



        String engineId =
                engine.getEngineId();



        System.out.println(
                "MONITOR COLLECT ENGINE: "
                        + engineId
        );



        /*
         * Estado geral do ETL
         */
        EtlMonitorSnapshot snapshot =
                progressCollector.collect(
                        engineId,
                        engine.getProgressMeter()
                );



        if(snapshot != null){

            monitor.updateSnapshot(snapshot);

        }




        /*
         * Estado dos processors activos
         */
        List<? extends TaskProcessor<?>> processors =
                engine.getCurrentTaskProcessor();



        if(processors == null){

            processors =
                    new ArrayList<>();

        }



        if(!processors.isEmpty()){


            List<TaskMonitorInfo> tasks =
                    taskProcessorCollector.collect(
                            engineId,
                            processors
                    );


            monitor.updateTasks(tasks);

        }

    }

}