package org.openmrs.module.epts.etl.monitor.context;


import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;


public class EtlExecutionContextRegistry {


    private static final Map<String,EtlExecutionContext> contexts =
            new ConcurrentHashMap<>();



    public static void register(
            EtlExecutionContext context){

        contexts.put(
                context.getOperationId(),
                context
        );

    }



    public static EtlExecutionContext find(
            String operationId){

        return contexts.get(operationId);

    }



    public static void remove(
            String operationId){

        contexts.remove(operationId);

    }

}