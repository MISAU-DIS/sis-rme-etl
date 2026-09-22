package org.openmrs.module.epts.etl.config;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Service;

/**
 * Centralizes access to ETL Global Properties configured in OpenMRS.
 *
 * <p>
 * This service is responsible only for reading configuration.
 * It does not start, stop or restart the ETL.
 * </p>
 *
 * <p>
 * The OpenMRS AdministrationService is intentionally obtained lazily.
 * This is important because this bean may be created before the OpenMRS
 * ServiceContext is fully initialized.
 * </p>
 */
@Service
public class EtlGlobalPropertyService {

    private static final String PREFIX = "epts.etl.";

    /**
     * Gets the OpenMRS AdministrationService lazily.
     *
     * <p>
     * Do not call OpenMRS services from the constructor of this class.
     * </p>
     *
     * @return OpenMRS AdministrationService
     */
    private AdministrationService getAdministrationService() {
        return Context.getAdministrationService();
    }

    // =========================================================
    // GENERIC
    // =========================================================

    /**
     * Gets an ETL global property.
     *
     * @param property property name without the "epts.etl." prefix
     * @return configured value or null when not configured
     */
    public String get(String property) {
        return getAdministrationService().getGlobalProperty(
                PREFIX + property
        );
    }

    /**
     * Gets an ETL global property with a default value.
     *
     * @param property     property name without the "epts.etl." prefix
     * @param defaultValue value returned when the property is not configured
     * @return configured value or default value
     */
    public String get(String property, String defaultValue) {
        String value = get(property);

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value.trim();
    }

    /**
     * Gets a boolean ETL global property.
     *
     * @param property     property name without the "epts.etl." prefix
     * @param defaultValue default value
     * @return configured value or default value
     */
    public boolean getBoolean(String property, boolean defaultValue) {
        String value = get(property);

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value.trim());
    }

    /**
     * Gets an integer ETL global property.
     *
     * @param property     property name without the "epts.etl." prefix
     * @param defaultValue default value
     * @return configured value or default value when parsing fails
     */
    public int getInt(String property, int defaultValue) {
        String value = get(property);

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Gets a long ETL global property.
     *
     * @param property     property name without the "epts.etl." prefix
     * @param defaultValue default value
     * @return configured value or default value when parsing fails
     */
    public long getLong(String property, long defaultValue) {
        String value = get(property);

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // =========================================================
    // ETL PROCESSING
    // =========================================================

    /**
     * Indicates whether ETL startup is enabled.
     */
    public boolean isEnabled() {
        return getBoolean("enabled", false);
    }

    /**
     * Gets the ETL root directory.
     */
    public String getRootDir() {
        return get(
                "root.dir",
                "/opt/openmrs/etl"
        );
    }

    /**
     * Gets the ETL configuration directory.
     */
    public String getConfDir() {
        return get(
                "conf.dir",
                "/opt/openmrs/etl/conf"
        );
    }

    /**
     * Gets the ETL startup configuration file.
     *
     * <p>
     * When this property is not configured, the process service may
     * fall back to {@code conf.dir/etl.json}.
     * </p>
     */
    public String getStartupFile() {
        return get("startup.file");
    }

    public boolean isDebugMode() {
        return getBoolean(
                "run.in.debug.mode",
                false
        );
    }

    public boolean isAutoCommit() {
        return getBoolean(
                "auto.commit",
                false
        );
    }

    public String getParallelProcessingStrategy() {
        return get(
                "parallel.processing.strategy",
                "SINGLE_THREAD"
        );
    }

    public int getProcessingBatch() {
        return getInt(
                "processing.batch",
                2000
        );
    }

    public boolean isDemographicsDisabled() {
        return getBoolean(
                "disable.demographics",
                false
        );
    }

    public int getMaxSupportedProcessors() {
        return getInt(
                "max.supported.processors",
                1
        );
    }

    public String getMode() {
        return get(
                "mode",
                "db_synchronization"
        );
    }

    // =========================================================
    // LOGGING
    // =========================================================

    public String getLogLevel() {
        return get(
                "log.level",
                "WARN"
        );
    }

    public String getConfigurationLogLevel() {
        return get(
                "log.level.configuration",
                "WARN"
        );
    }

    public String getControllerLogLevel() {
        return get(
                "log.level.controller",
                "WARN"
        );
    }

    public String getEngineLogLevel() {
        return get(
                "log.level.engine",
                "WARN"
        );
    }

    public String getProcessorLogLevel() {
        return get(
                "log.level.processor",
                "WARN"
        );
    }

    public String getDaoLogLevel() {
        return get(
                "log.level.dao",
                "WARN"
        );
    }

    public String getConnectionLogLevel() {
        return get(
                "log.level.connection",
                "WARN"
        );
    }

    // =========================================================
    // LOCATIONS / PROGRAMS
    // =========================================================

    public String getLocationName() {
        return get("location.name");
    }

    public String getHivDestinationLocationId() {
        return get("hiv.destination.location.id");
    }

    public String getSmiDestinationLocationId() {
        return get("smi.destination.location.id");
    }

    public String getSyncDestinationLocationId() {
        return get("sync.destination.location.id");
    }

    public String getDstTarvServiceId() {
        return get("dst.tarv.service.id");
    }

    public String getDstCcrServiceId() {
        return get("dst.ccr.service.id");
    }

    public int getTarvProgramId() {
        return getInt(
                "tarv.program.id",
                2
        );
    }

    public int getCcrProgramId() {
        return getInt(
                "ccr.program.id",
                6
        );
    }

    public int getPrepProgramId() {
        return getInt(
                "prep.program.id",
                25
        );
    }

    // =========================================================
    // SOURCE DATABASE
    // =========================================================

    public String getSourceDatabase() {
        return get("src.db");
    }

    public String getSourceUsername() {
        return get("src.username");
    }

    public String getSourcePassword() {
        return get("src.password");
    }

    public String getSourceConnectionUri() {
        return get("src.connection.uri");
    }

    public String getSourceLocationList() {
        return get("src.location.list");
    }

    // =========================================================
    // DESTINATION DATABASE
    // =========================================================

    public String getDestinationDatabase() {
        return get("dst.db");
    }

    public String getDestinationUsername() {
        return get("dst.username");
    }

    public String getDestinationPassword() {
        return get("dst.password");
    }

    public String getDestinationConnectionUri() {
        return get("dst.connection.uri");
    }

    public String getDatabaseDriver() {
        return get(
                "database.driver",
                "com.mysql.cj.jdbc.Driver"
        );
    }

    public String getConnectionIsolationLevel() {
        return get(
                "connection.isolation.level",
                "TRANSACTION_REPEATABLE_READ"
        );
    }

    // =========================================================
    // EXTRACTION CONDITIONS
    // =========================================================

    public boolean isExtraConditionEnabled() {
        return getBoolean(
                "extra.condition",
                true
        );
    }

    public String getSyncFragments() {
        return get(
                "sync.fragments",
                "sesp-sync-fragments/*.json"
        );
    }

    public boolean isIncludedPatientExtraConditionEnabled() {
        return getBoolean(
                "included.patient.extra.condition",
                true
        );
    }

    public boolean isIncludedPersonExtraConditionEnabled() {
        return getBoolean(
                "included.person.extra.condition",
                true
        );
    }

    public boolean isIncludedPersonBExtraConditionEnabled() {
        return getBoolean(
                "included.person.b.extra.condition",
                true
        );
    }

    // =========================================================
    // INDEX REBUILD
    // =========================================================

    /**
     * Gets the interval used to determine when the application
     * should perform an index rebuild.
     *
     * <p>The value is expressed in milliseconds.</p>
     *
     * <p>Default: 1,800,000 ms = 30 minutes.</p>
     *
     * @return rebuild interval in milliseconds
     */
    public long getRebuildIndexRepeatInterval() {
        return getLong(
                "rebuild.index.repeat.interval",
                1800000L
        );
    }

    // =========================================================
    // ERROR HANDLING
    // =========================================================

    public String getDefaultExceptionBehavior() {
        return get(
                "default.exception.behavior",
                "LOG"
        );
    }

    public String getDefaultInconsistencyBehavior() {
        return get(
                "default.inconsistency.behavior",
                "MARK_RECORD_AS_FAILED"
        );
    }

    public String getStageRecordIssueBehavior() {
        return get(
                "stage.record.issue.behavior",
                "IGNORE"
        );
    }
}