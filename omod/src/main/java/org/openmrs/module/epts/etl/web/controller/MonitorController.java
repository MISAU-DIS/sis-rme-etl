package org.openmrs.module.epts.etl.web.controller;


import java.io.IOException;

import org.openmrs.module.epts.etl.api.OperationClient;
import org.openmrs.module.epts.etl.monitor.dto.MonitorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller responsible for displaying ETL execution monitoring.
 *
 * The OMOD does not manage ETL execution state.
 * It only consumes monitoring information exposed by the API.
 */
@Controller(MonitorController.CONTROLLER_NAME)
public class MonitorController {


    public static final String CONTROLLER_NAME =
            "epts.etl.monitorController";


    private static final Logger LOGGER =
            LoggerFactory.getLogger(MonitorController.class);



    @Autowired
    private OperationClient operationClient;



    /**
     * Opens monitor page.
     */
    @RequestMapping(
            value = "/module/epts/etl/monitor",
            method = RequestMethod.GET
    )
    public String monitor(ModelMap model) {

        LOGGER.info(
                "Opening ETL monitor page"
        );

        return "syncMonitor";
    }




    /**
     * Returns current monitoring information.
     *
     * This endpoint is called periodically by monitor.jsp
     * using AJAX.
     */
    @RequestMapping(
            value = "/module/epts/etl/monitorData",
            method = RequestMethod.GET
    )
    @ResponseBody
    public MonitorResponseDTO monitorData() {


        LOGGER.debug(
                "Retrieving ETL monitor data"
        );


        try {


            return operationClient.getMonitor();



        } catch (Exception e) {


            LOGGER.error(
                    "Error retrieving ETL monitor information",
                    e
            );


            MonitorResponseDTO response =
                    new MonitorResponseDTO();


            response.setStatus(
                    "ERROR"
            );


            response.setMessage(
                    e.getMessage()
            );


            return response;

        }

    }


}