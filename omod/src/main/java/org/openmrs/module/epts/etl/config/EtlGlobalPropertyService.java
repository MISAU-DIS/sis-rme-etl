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

    private static final String PREFIX = "eptsetl.";

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
     * @param property property name without the "eptsetl." prefix
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
     * @param property     property name without the "eptsetl." prefix
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
     * @param property     property name without the "eptsetl." prefix
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
     * @param property     property name without the "eptsetl." prefix
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
     * @param property     property name without the "eptsetl." prefix
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
                "etlRootDir",
                "/opt/openmrs/etl"
        );
    }

    /**
     * Gets the ETL configuration directory.
     */
    public String getConfDir() {
        return get(
                "etlConfDir",
                "/opt/openmrs/etl/conf"
        );
    }

    /**
     * Gets the ETL startup configuration file.
     *
     * <p>
     * When this property is not configured, the process service may
     * fall back to {@code etlConfDir/etl.json}.
     * </p>
     */
    public String getStartupFile() {
        return get("startupFile");
    }

    public boolean isDebugMode() {
        return getBoolean(
                "runInDebugMode",
                false
        );
    }

    public boolean isAutoCommit() {
        return getBoolean(
                "autoCommit",
                false
        );
    }

    public String getParallelProcessingStrategy() {
        return get(
                "parallelProcessingStrategy",
                "SINGLE_THREAD"
        );
    }

    public int getProcessingBatch() {
        return getInt(
                "processingBatch",
                2000
        );
    }

    public boolean isDemographicsDisabled() {
        return getBoolean(
                "disableDemographics",
                false
        );
    }

    public int getMaxSupportedProcessors() {
        return getInt(
                "maxSupportedProcessors",
                1
        );
    }


    // =========================================================
    // LOGGING
    // =========================================================

    public String getLogLevel() {
        return get(
                "logLevel",
                "WARN"
        );
    }

    public String getConfigurationLogLevel() {
        return get(
                "logLevelConfiguration",
                "WARN"
        );
    }

    public String getControllerLogLevel() {
        return get(
                "logLevelController",
                "WARN"
        );
    }

    public String getEngineLogLevel() {
        return get(
                "logLevelEngine",
                "WARN"
        );
    }

    public String getProcessorLogLevel() {
        return get(
                "logLevelProcessor",
                "WARN"
        );
    }

    public String getDaoLogLevel() {
        return get(
                "logLevelDao",
                "WARN"
        );
    }

    public String getConnectionLogLevel() {
        return get(
                "logLevelConnection",
                "WARN"
        );
    }

    // =========================================================
    // LOCATIONS / PROGRAMS
    // =========================================================

    public String getLocationName() {
        return get("locationName");
    }

    public String getHivDestinationLocationId() {
        return get("hivDestinationLocationId");
    }

    public String getSmiDestinationLocationId() {
        return get("smiDestinationLocationId");
    }

    public String getSyncDestinationLocationId() {
        return get("syncDestinationLocationId");
    }

    public String getDstTarvServiceId() {
        return get("dstTarvServiceId");
    }

    public String getDstCcrServiceId() {
        return get("dstCcrServiceId");
    }

    public int getTarvProgramId() {
        return getInt(
                "tarvProgramId",
                2
        );
    }

    public int getCcrProgramId() {
        return getInt(
                "ccrProgramId",
                6
        );
    }

    public int getPrepProgramId() {
        return getInt(
                "prepProgramId",
                25
        );
    }

    // =========================================================
    // SOURCE DATABASE
    // =========================================================

    private String connectionProperty(String flow, String suffix) {
        if (!"sync".equals(flow) && !"migr".equals(flow)) {
            throw new IllegalArgumentException("Expected sync or migr flow");
        }
        return flow + suffix;
    }

    public String getSourceDatabase(String flow) {
        return get(connectionProperty(flow, "SrcDb"));
    }

    public String getSourceUsername(String flow) {
        return get(connectionProperty(flow, "SrcUserName"));
    }

    public String getSourcePassword(String flow) {
        return get(connectionProperty(flow, "SrcUserPassword"));
    }

    public String getSourceConnectionUri(String flow) {
        return get(connectionProperty(flow, "SrcConnectionURI"));
    }

    public String getSourceLocationList() {
        return get("srcLocationList");
    }

    // =========================================================
    // DESTINATION DATABASE
    // =========================================================

    public String getDestinationDatabase(String flow) {
        return get(connectionProperty(flow, "DstDb"));
    }

    public String getDestinationUsername(String flow) {
        return get(connectionProperty(flow, "DstUserName"));
    }

    public String getDestinationPassword(String flow) {
        return get(connectionProperty(flow, "DstUserPassword"));
    }

    public String getDestinationConnectionUri(String flow) {
        return get(connectionProperty(flow, "DstConnectionURI"));
    }

    public String getDatabaseDriver() {
        return get(
                "driveClassName",
                "com.mysql.cj.jdbc.Driver"
        );
    }

    public String getConnectionIsolationLevel() {
        return get(
                "connectionIsolationLevel",
                "TRANSACTION_REPEATABLE_READ"
        );
    }

    // =========================================================
    // EXTRACTION CONDITIONS
    // =========================================================

    public boolean isExtraConditionEnabled() {
        return getBoolean(
                "extraCondition",
                true
        );
    }

    public String getSyncFragments() {
        return get(
                "syncFragments",
                "sync/sesp-sync-fragments/*.json"
        );
    }

    public boolean isIncludedPatientExtraConditionEnabled() {
        return getBoolean(
                "includedPatientExtraCondition",
                true
        );
    }

    public boolean isIncludedPersonExtraConditionEnabled() {
        return getBoolean(
                "includedPersonExtraCondition",
                true
        );
    }

    public boolean isIncludedPersonBExtraConditionEnabled() {
        return getBoolean(
                "includedPersonBExtraCondition",
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
                "rebuildIndexRepeatInterval",
                1800000L
        );
    }

    // =========================================================
    // ERROR HANDLING
    // =========================================================

    public String getDefaultExceptionBehavior() {
        return get(
                "defaultExceptionBehavior",
                "LOG"
        );
    }

    public String getDefaultInconsistencyBehavior() {
        return get(
                "defaultInconsistencyBehavior",
                "MARK_RECORD_AS_FAILED"
        );
    }

    public String getStageRecordIssueBehavior() {
        return get(
                "stageRecordIssueBehavior",
                "IGNORE"
        );
    }
}