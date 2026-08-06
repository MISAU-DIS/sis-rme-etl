package org.openmrs.module.epts.etl.monitor.collector;


import java.util.Date;

import org.openmrs.module.epts.etl.engine.EtlProgressMeter;
import org.openmrs.module.epts.etl.monitor.model.EtlMonitorSnapshot;



public class ProgressCollector {


    public EtlMonitorSnapshot collect(
            String operationId,
            EtlProgressMeter meter){


        if(meter == null){
            return null;
        }


        EtlMonitorSnapshot snapshot =
                new EtlMonitorSnapshot();



        snapshot.setOperationId(operationId);


        snapshot.setStatus(
                meter.getStatus()
        );


        snapshot.setTotal(
                meter.getTotal()
        );


        snapshot.setProcessed(
                meter.getProcessed()
        );


        snapshot.setRemaining(
                meter.getRemain()
        );


        snapshot.setProgress(
                meter.getProgress()
        );


        snapshot.setCapturedAt(
                new Date()
        );



        return snapshot;

    }

}