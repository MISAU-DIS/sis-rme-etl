
package org.openmrs.module.epts.etl.service;

import org.openmrs.module.epts.etl.Main;
import org.openmrs.module.epts.etl.config.EtlGlobalPropertyService;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.controller.ProcessStarter;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides access to the ETL process running inside OpenMRS.
 *
 * <p>
 * The actual ETL process is created and started by
 * {@link Main#startFromOpenMRS()}.
 * </p>
 *
 * <p>
 * This service does not create a second {@link ProcessStarter}.
 * Instead, it obtains the exact {@link ProcessStarter} instance created
 * by the API startup process through {@link Main#getOpenMrsProcessStarter()}.
 * </p>
 *
 * <p>
 * This is important because the API and OMOD run inside the same JVM.
 * Creating another ProcessStarter here would create a second independent
 * ETL lifecycle and could result in duplicate executions.
 * </p>
 */
@Service
public class EtlProcessService {

    private final EtlGlobalPropertyService globalPropertyService;

    /**
     * Reference to the ProcessStarter created by Main during OpenMRS startup.
     *
     * <p>
     * This reference is only a convenience/cache. The source of truth is
     * {@link Main#getOpenMrsProcessStarter()}.
     * </p>
     */
    private volatile ProcessStarter processStarter;

    @Autowired
    public EtlProcessService(
            EtlGlobalPropertyService globalPropertyService) {

        this.globalPropertyService = globalPropertyService;
    }

    // =========================================================
    // INITIALIZATION
    // =========================================================

    /**
     * Obtains the ProcessStarter created by the OpenMRS ETL startup.
     *
     * <p>
     * This method intentionally does not load {@code etl.json} and does not
     * create a new ProcessStarter.
     * </p>
     *
     * @throws DBException kept for compatibility with the existing service API
     */
    public void initialize()
            throws DBException {

        /*
         * If the local reference already exists, there is nothing to do.
         */
        if (processStarter != null) {
            return;
        }

        /*
         * Obtain the actual ProcessStarter created by Main.
         */
        ProcessStarter starter =
                Main.getOpenMrsProcessStarter();

        /*
         * The OMOD may be initialized before the ETL startup has completed.
         * In that case, simply leave the reference null.
         *
         * The next call to initialize() will try again.
         */
        if (starter == null) {
            return;
        }

        this.processStarter = starter;
    }

    // =========================================================
    // START ON MODULE STARTUP
    // =========================================================

    /**
     * Starts the ETL automatically when automatic startup is enabled.
     *
     * <p>
     * Normally the ETL is already started by
     * {@link Main#startFromOpenMRS()} during module startup.
     * Therefore this method primarily verifies and reuses the existing
     * ProcessStarter rather than creating another one.
     * </p>
     *
     * @throws DBException                 if ETL database initialization fails
     * @throws ForbiddenOperationException if ETL startup is not allowed
     */
    public void startIfEnabled()
            throws DBException,
            ForbiddenOperationException {

        /*
         * Check the OpenMRS Global Property.
         */
        if (!globalPropertyService.isEnabled()) {
            return;
        }

        /*
         * Obtain the ProcessStarter created by Main.
         */
        initialize();

        if (processStarter == null) {
            throw new IllegalStateException(
                    "EPTS ETL is enabled, but the OpenMRS "
                            + "ProcessStarter has not been initialized.");
        }

        ProcessController controller =
                processStarter.getCurrentController();

        /*
         * The starter should normally already have created its controller.
         */
        if (controller == null) {
            throw new IllegalStateException(
                    "EPTS ETL ProcessStarter exists, but "
                            + "its ProcessController is null.");
        }

        /*
         * Do not start another execution when the ETL is already running.
         */
        if (controller.isRunning()) {
            return;
        }

        /*
         * A finished controller cannot be reused.
         *
         * ProcessStarter.startProcess() knows how to create a new controller
         * when necessary, so we do not create one here.
         */
        if (controller.isFinished()
                || controller.isStopped()) {

            processStarter.startProcess();
            return;
        }

        /*
         * Start the existing controller asynchronously.
         */
        processStarter.startProcess();
    }

    // =========================================================
    // START
    // =========================================================

    /**
     * Starts the ETL process asynchronously.
     *
     * <p>
     * The actual execution is delegated to the ProcessStarter created
     * by Main. No new ETL process is created here.
     * </p>
     *
     * @return the ProcessController used by the ETL
     * @throws ForbiddenOperationException if startup is not allowed
     * @throws DBException                 if ETL database initialization fails
     */
    public synchronized ProcessController start()
            throws ForbiddenOperationException,
            DBException {

        ensureInitialized();

        if (processStarter == null) {
            throw new IllegalStateException(
                    "EPTS ETL ProcessStarter is not available. "
                            + "The ETL may not have been started by OpenMRS.");
        }

        return processStarter.startProcess();
    }

    // =========================================================
    // RESTART
    // =========================================================

    /**
     * Restarts the current ETL process asynchronously.
     *
     * <p>
     * The existing ProcessStarter is reused. Its
     * {@link ProcessStarter#restartProcess()} method is responsible for
     * stopping the current controller, creating a new controller and
     * starting the new execution.
     * </p>
     *
     * @return the new ProcessController
     * @throws ForbiddenOperationException if restart is not allowed
     * @throws DBException                 if ETL database initialization fails
     */
    public synchronized ProcessController restart()
            throws ForbiddenOperationException,
            DBException {

        ensureInitialized();

        if (processStarter == null) {
            throw new IllegalStateException(
                    "EPTS ETL ProcessStarter is not available. "
                            + "The ETL may not have been started by OpenMRS.");
        }

        return processStarter.restartProcess();
    }

    // =========================================================
    // STATUS
    // =========================================================

    /**
     * Returns whether automatic ETL execution is enabled.
     *
     * @return true when epts.etl.enabled is enabled
     */
    public boolean isEnabled() {

        return globalPropertyService.isEnabled();
    }

    /**
     * Indicates whether the OpenMRS ETL ProcessStarter is available.
     *
     * @return true when a ProcessStarter has been obtained
     */
    public boolean isInitialized() throws DBException {

        /*
         * Refresh the reference in case Main created the starter after
         * this service was instantiated.
         */
        if (processStarter == null) {
            initialize();
        }

        return processStarter != null;
    }

    // =========================================================
    // CURRENT PROCESS
    // =========================================================

    /**
     * Returns the ProcessController currently managed by the real ETL.
     *
     * @return current ProcessController, or null if the ETL has not been
     * initialized
     */
    public ProcessController getCurrentController() throws DBException {

        /*
         * Refresh the local reference when necessary.
         */
        if (processStarter == null) {
            initialize();
        }

        ProcessStarter starter =
                this.processStarter;

        if (starter == null) {
            return null;
        }

        return starter.getCurrentController();
    }

    /**
     * Returns the controller that should be used by the monitoring layer.
     *
     * @return current ProcessController
     */
    public ProcessController getMonitorController() throws DBException {

        return getCurrentController();
    }

    /**
     * Returns the actual ProcessStarter created by Main.
     *
     * @return real OpenMRS ProcessStarter, or null if not initialized
     */
    public ProcessStarter getProcessStarter() throws DBException {

        if (processStarter == null) {
            initialize();
        }

        return processStarter;
    }

    // =========================================================
    // INTERNAL
    // =========================================================

    /**
     * Ensures that the service has obtained the ProcessStarter created
     * by Main.
     *
     * @throws DBException kept for compatibility with the service API
     */
    private void ensureInitialized()
            throws DBException {

        if (processStarter == null) {
            initialize();
        }
    }
}