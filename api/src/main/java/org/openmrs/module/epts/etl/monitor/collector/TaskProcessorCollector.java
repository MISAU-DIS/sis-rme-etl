package org.openmrs.module.epts.etl.monitor.collector;


import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.engine.TaskProcessor;
import org.openmrs.module.epts.etl.monitor.model.TaskMonitorInfo;



public class TaskProcessorCollector {


    /**
     * Collect monitoring information from active task processors.
     *
     * @param engineId related engine identifier
     * @param processors active processors
     * @return list of task monitoring information
     */
    public List<TaskMonitorInfo> collect(
            String engineId,
            List<? extends TaskProcessor<?>> processors){


        List<TaskMonitorInfo> result =
                new ArrayList<>();


        if(processors == null || processors.isEmpty()){

            return result;

        }



        for(TaskProcessor<?> processor : processors){


            if(processor == null){

                continue;

            }


            TaskMonitorInfo info =
                    new TaskMonitorInfo();



            /*
             * Engine identification
             */
            info.setEngineId(
                    engineId
            );



            /*
             * Processor identification
             */
            info.setProcessorId(
                    processor.getProcessorId()
            );



            /*
             * Task identification
             *
             * Currently processor id is used
             * because TaskProcessor does not expose
             * another task identifier.
             */
            info.setTaskId(
                    processor.getProcessorId()
            );



            /*
             * Processor status
             */
            info.setStatus(
                    processor.getOperationStatus()
            );



            /*
             * Processing limits
             */
            if(processor.getLimits() != null){


                info.setMinRecord(
                        processor.getLimits().getMinRecordId()
                );


                info.setMaxRecord(
                        processor.getLimits().getMaxRecordId()
                );

            }



            result.add(info);

        }


        return result;

    }

}