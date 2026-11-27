package org.openmrs.module.epts.etl.openmrs.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import org.openmrs.module.epts.etl.Main;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;

public class EnvironmentPropertyNamingTest {

    @TempDir
    public Path temporaryFolder;

    private final String originalEnvFile = System.getProperty("etl.env.file");

    @AfterEach
    public void restoreEnvFile() {
        if (originalEnvFile == null) {
            System.clearProperty("etl.env.file");
        } else {
            System.setProperty("etl.env.file", originalEnvFile);
        }
    }

    @Test
    public void shouldResolveCanonicalEnvironmentPlaceholders() throws Exception {
        useProperties("etlRootDir=/opt/etl\nhivDestinationLocationId=212\nextraCondition=voided = 0\n");
        String resolved = EtlDataConfiguration.resolvePlaceholders(
                "${etlRootDir}|${hivDestinationLocationId}|${extraCondition}",
                null, Collections.emptyMap(), false);
        assertEquals("/opt/etl|212|voided = 0", resolved);
    }

    @Test
    public void shouldLoadCanonicalOpenMrsOptionsWithoutRenamingDatabaseFields() throws Exception {
        useProperties("openmrsBaseUrl=http://localhost:8080/openmrs\nopenmrsEndpoint=searchindexupdate\n"
                + "openmrsUsername=test-user\nopenmrsPassword=test-password\n"
                + "connectTimeoutSeconds=15\nrequestTimeoutSeconds=30\n");
        OpenMrsIndexConfiguration configuration = OpenMrsIndexConfiguration.load(true);
        assertEquals("http://localhost:8080/openmrs/ws/rest/v1/", configuration.getBaseUrl());
        assertEquals("searchindexupdate", configuration.getEndpoint());
        assertEquals("test-user", configuration.getUsername());
        assertEquals("test-password", configuration.getPassword());
        assertEquals(15, configuration.getConnectTimeoutSeconds());
        assertEquals(30, configuration.getRequestTimeoutSeconds());
        assertEquals("resource_type", configuration.getResourceTypeField());
        assertEquals("record_uuid", configuration.getUuidField());
        assertEquals("operation_type", configuration.getOperationField());
    }

    @Test
    public void shouldUseCanonicalOpenMrsLifecyclePropertyNames() {
		assertEquals("eptsetl.", EtlDataConfiguration.ETL_GLOBAL_PROPERTY_PREFIX);
        assertEquals("eptsetl.startupFile", Main.STARTUP_FILE_GLOBAL_PROPERTY);
        assertEquals("eptsetl.openmrsReadinessTimeoutSeconds",
                Main.STARTUP_READINESS_TIMEOUT_SECONDS_GLOBAL_PROPERTY);
        assertEquals("eptsetl.openmrsReadinessPollSeconds",
                Main.STARTUP_READINESS_POLL_SECONDS_GLOBAL_PROPERTY);
		assertEquals("eptsetl.openmrsStartupStabilizationSeconds",
				Main.STARTUP_STABILIZATION_SECONDS_GLOBAL_PROPERTY);
		assertEquals("eptsetl.openmrsBaseUrl", Main.OPENMRS_BASE_URL_GLOBAL_PROPERTY);
    }

    private void useProperties(String contents) throws Exception {
        File file = Files.createTempFile(temporaryFolder, "env", ".properties").toFile();
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        System.setProperty("etl.env.file", file.getAbsolutePath());
    }
}
