
package org.openmrs.module.epts.etl.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.monitor.dto.MonitorInfoDTO;
import org.openmrs.module.epts.etl.monitor.service.EtlMonitorService;
import org.openmrs.module.epts.etl.service.EtlProcessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Web controller responsible for the ETL monitor page
 * and the monitor control operations.
 * <p>
 * Browser
 * ↓ HTTP
 * MonitorController
 * ↓
 * EtlMonitorService / EtlProcessService
 * ↓
 * ProcessController
 */
@Controller(MonitorController.CONTROLLER_NAME)
public class MonitorController {

    public static final String CONTROLLER_NAME =
            "epts.etl.monitorController";

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MonitorController.class);

    private final EtlMonitorService monitorService;

    private final EtlProcessService processService;

    private final ObjectMapper objectMapper;

    @Autowired
    public MonitorController(
            EtlMonitorService monitorService,
            EtlProcessService processService) {

        this.monitorService = monitorService;
        this.processService = processService;
        this.objectMapper = new ObjectMapper();
    }

    // =========================================================
    // MONITOR PAGE
    // =========================================================

    /**
     * Displays the ETL monitor page.
     * <p>
     * URL:
     * GET /module/epts/etl/monitorData.form
     */
    @RequestMapping(
            value = "/module/epts/etl/monitorData.form",
            method = RequestMethod.GET
    )
    public ModelAndView monitor() {

        return new ModelAndView(
                "module/etl/monitorData"
        );
    }

    // =========================================================
    // MONITOR DATA
    // =========================================================

    /**
     * Returns the current ETL monitor information as JSON.
     * <p>
     * URL:
     * GET /module/epts/etl/monitorData.json
     */
    @RequestMapping(
            value = "/module/epts/etl/monitorData.json",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public String monitorData() {

        try {

            LOGGER.debug(
                    "[ETL-MONITOR] Getting ETL monitor"
            );

            MonitorInfoDTO response =
                    monitorService.getMonitor();

            if (response == null) {

                LOGGER.warn(
                        "[ETL-MONITOR] No monitor available"
                );

                return createErrorResponse(
                        "No ETL monitor available"
                );
            }

            return objectMapper.writeValueAsString(
                    response
            );

        } catch (Exception e) {

            LOGGER.error(
                    "[ETL-MONITOR] Error getting ETL monitor",
                    e
            );

            return createErrorResponse(
                    "Unable to get ETL monitor: "
                            + (
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : e.getClass().getSimpleName()
                    )
            );
        }
    }

    // =========================================================
    // START
    // =========================================================

    /**
     * Starts the ETL asynchronously.
     * <p>
     * URL:
     * POST /module/epts/etl/start.form
     */
    @RequestMapping(
            value = "/module/epts/etl/start.form",
            method = RequestMethod.POST,
            produces = "application/json"
    )
    @ResponseBody
    public String start() {

        try {

            LOGGER.info(
                    "[ETL-START] Starting ETL..."
            );

            ProcessController controller =
                    processService.start();

            String operationId =
                    controller != null
                            ? controller.getControllerId()
                            : null;

            LOGGER.info(
                    "[ETL-START] ETL started: {}",
                    operationId
            );

            return createOperationResponse(
                    "STARTED",
                    "ETL iniciado com sucesso",
                    operationId
            );

        } catch (Exception e) {

            LOGGER.error(
                    "[ETL-START] Error starting ETL",
                    e
            );

            return createOperationErrorResponse(
                    "Não foi possível iniciar o ETL: "
                            + (
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : e.getClass().getSimpleName()
                    )
            );
        }
    }

    // =========================================================
    // RESTART
    // =========================================================

    /**
     * Restarts the ETL.
     * <p>
     * URL:
     * POST /module/epts/etl/restart.form
     */
    @RequestMapping(
            value = "/module/epts/etl/restart.form",
            method = RequestMethod.POST,
            produces = "application/json"
    )
    @ResponseBody
    public String restart() {

        try {

            LOGGER.info(
                    "[ETL-RESTART] Restarting ETL..."
            );

            ProcessController controller =
                    processService.restart();

            String operationId =
                    controller != null
                            ? controller.getControllerId()
                            : null;

            LOGGER.info(
                    "[ETL-RESTART] ETL restarted: {}",
                    operationId
            );

            return createOperationResponse(
                    "RESTARTED",
                    "ETL reiniciado com sucesso",
                    operationId
            );

        } catch (Exception e) {

            LOGGER.error(
                    "[ETL-RESTART] Error restarting ETL",
                    e
            );

            return createOperationErrorResponse(
                    "Não foi possível reiniciar o ETL: "
                            + (
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : e.getClass().getSimpleName()
                    )
            );
        }
    }

    // =========================================================
    // RESPONSES
    // =========================================================

    /**
     * Creates an error response for the monitor endpoint.
     */
    private String createErrorResponse(
            String message) {

        try {

            MonitorInfoDTO response =
                    new MonitorInfoDTO();

            response.setStatus("ERROR");
            response.setMessage(message);
            response.setOperationId(null);
            response.setProgress(0.0);
            response.setProcessed(0L);
            response.setTotal(0L);

            return objectMapper.writeValueAsString(
                    response
            );

        } catch (Exception e) {

            LOGGER.error(
                    "[ETL-MONITOR] Error serializing error response",
                    e
            );

            return "{\"status\":\"ERROR\","
                    + "\"message\":\"Unable to serialize response\"}";
        }
    }

    /**
     * Creates a successful response for an ETL operation.
     */
    private String createOperationResponse(
            String status,
            String message,
            String operationId) {

        try {

            Map<String, Object> response =
                    new HashMap<String, Object>();

            response.put("status", status);
            response.put("success", true);
            response.put("message", message);
            response.put("operationId", operationId);

            return objectMapper.writeValueAsString(
                    response
            );

        } catch (Exception e) {

            LOGGER.error(
                    "[ETL] Error serializing operation response",
                    e
            );

            return "{\"success\":false,"
                    + "\"message\":\"Unable to serialize response\"}";
        }
    }

    /**
     * Creates an error response for an ETL operation.
     */
    private String createOperationErrorResponse(
            String message) {

        try {

            Map<String, Object> response =
                    new HashMap<String, Object>();

            response.put("status", "ERROR");
            response.put("success", false);
            response.put("message", message);
            response.put("operationId", null);

            return objectMapper.writeValueAsString(
                    response
            );

        } catch (Exception e) {

            LOGGER.error(
                    "[ETL] Error serializing operation error response",
                    e
            );

            return "{\"success\":false,"
                    + "\"message\":\"Unable to serialize response\"}";
        }
    }
}