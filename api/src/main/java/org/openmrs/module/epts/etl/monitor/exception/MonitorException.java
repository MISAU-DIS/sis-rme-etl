package org.openmrs.module.epts.etl.monitor.exception;


public class MonitorException extends RuntimeException {


    private static final long serialVersionUID = 1L;



    public MonitorException(String message){

        super(message);

    }



    public MonitorException(String message, Throwable cause){

        super(message,cause);

    }

}