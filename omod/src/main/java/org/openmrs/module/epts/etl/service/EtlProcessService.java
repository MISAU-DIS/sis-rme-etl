package org.openmrs.module.epts.etl.service;

import org.openmrs.module.epts.etl.Main;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.config.EtlGlobalPropertyService;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.controller.ProcessStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EtlProcessService {

    private final EtlGlobalPropertyService globalPropertyService;

    private volatile ProcessStarter processStarter;

    @Autowired
    public EtlProcessService(
            EtlGlobalPropertyService globalPropertyService) {

        this.globalPropertyService = globalPropertyService;
    }

    /**
     * Gets the ProcessStarter instance that is actually managed
     * by the OpenMRS ETL lifecycle.
     */
    public synchronized void initialize() {
        if (this.processStarter != null) {
            return;
        }

        ProcessStarter starter = Main.getOpenMrsProcessStarter();

        if (starter != null) {
            this.processStarter = starter;
        }
    }

    public boolean isEnabled() {
        return globalPropertyService.isEnabled();
    }

    public boolean isInitialized() {

        initialize();

        return processStarter != null
                && processStarter.isInitialized();
    }

    public ProcessStarter getProcessStarter() {

        initialize();

        return processStarter;
    }

    public ProcessController getCurrentController() {

        ProcessStarter starter =
                getProcessStarter();

        if (starter == null) {
            return null;
        }

        return starter.getCurrentController();
    }

    /**
     * Starts the ETL using the ProcessStarter instance
     * already created by Main/OpenMRS.
     *
     * This method does NOT create another ProcessStarter.
     */
    public synchronized ProcessController start() {
        ProcessStarter starter = getProcessStarter();

        if (starter == null) {
            throw new IllegalStateException(
                    "EPTS ETL ProcessStarter is not available. The ETL has not been started by OpenMRS."
            );
        }

        ProcessController controller = starter.getCurrentController();

        if (controller != null && controller.isRunning()) {
            return controller;
        }

        /*
         * Primeiro arranque do ProcessStarter.
         */
        if (!starter.isInitialized()) {
            Thread etlThread = new Thread(starter, "epts-etl-manual-start");
            etlThread.setDaemon(true);
            etlThread.start();
            return null;
        }

        /*
         * O lifecycle anterior terminou.
         * Criamos um novo ProcessStarter usando a mesma configuração ETL.
         */
        if (controller != null && (controller.isStopped() || controller.isFinished())) {

            EtlConfiguration etlConfig = starter.getEtlConfig();

            ProcessStarter newStarter = new ProcessStarter(etlConfig);

            this.processStarter = newStarter;

            Thread etlThread = new Thread(
                    newStarter,
                    "epts-etl-manual-restart"
            );

            etlThread.setDaemon(true);
            etlThread.start();

            return null;
        }

        return controller;
    }

    /**
     * Requests the current ETL controller to stop.
     */
    public synchronized void stop() {

        ProcessController controller =
                getCurrentController();

        if (controller == null) {
            return;
        }

        if (!controller.isStopping()
                && !controller.isStopped()
                && !controller.isFinished()) {

            controller.requestStop();
        }
    }
}