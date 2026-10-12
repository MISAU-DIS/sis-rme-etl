package org.openmrs.module.epts.etl.openmrs.conf;

import java.util.Properties;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Runtime configuration for the OpenMRS index-update integration. */
public final class OpenMrsIndexConfiguration extends AbstractEtlDataConfiguration {

	protected static final String PATH = "/ws/rest/v1/";

	private final String baseUrl;
	private final String endpoint;
	private final String username;
	private final String password;
	private final String bearerToken;
	private final String resourceTypeField;
	private final String uuidField;
	private final String operationField;
	private final int connectTimeoutSeconds;
	private final int requestTimeoutSeconds;

	private OpenMrsIndexConfiguration(Properties properties) {
		String bU = required(properties, "openmrs_base_url");

		this.baseUrl = bU + PATH;

		this.endpoint = required(properties, "openmrs_endpoint");
		this.username = optional(properties, "openmrs_username", null);
		this.password = optional(properties, "openmrs_password", null);
		this.bearerToken = optional(properties, "openmrs_bearer_token", null);
		this.resourceTypeField = optional(properties, "resource_type_field", "resource_type");
		this.uuidField = optional(properties, "uuid_field", "record_uuid");
		this.operationField = optional(properties, "operation_field", "operation_type");
		this.connectTimeoutSeconds = positiveInteger(properties, "connect_timeout_seconds", 60);
		this.requestTimeoutSeconds = positiveInteger(properties, "request_timeout_seconds", 60);

		if (isDefined(username) != isDefined(password)) {
			throw new IllegalStateException(
					"Both epts.etl.openmrs.index.username and epts.etl.openmrs.index.password must be configured");
		}
	}

	public static OpenMrsIndexConfiguration load() {
		Properties properties = new Properties();

		Properties fileProperties = EtlDataConfiguration.loadProperties(System.getProperty("etl.env.file"));
		Properties openMrsProperties = EtlDataConfiguration.loadOpenMrsGlobalProperties();

		for (String name : new String[] { "openmrs_base_url", "openmrs_endpoint", "openmrs_username",
				"openmrs_password", "openmrs_bearer_token", "resource_type_field", "uuid_field", "operation_field",
				"connect_timeout_seconds", "request_timeout_seconds" }) {

			String value = resolve(name, openMrsProperties, fileProperties);

			if (value == null) {

				switch (name) {
				case "openmrs_base_url":
					value = "http://127.0.0.1:8080/openmrs";

					break;
				case "openmrs_endpoint":
					value = "searchindexupdate";

					break;

				default:
					break;
				}
			}

			if (value != null) {
				properties.setProperty(name, EtlDataConfiguration.stripWrappingQuotes(value));
			}

		}

		return new OpenMrsIndexConfiguration(properties);
	}

	private static String resolve(String name, Properties openMrsProperties, Properties fileProperties) {
		String shortName = name;

		String fullName = EtlDataConfiguration.ETL_GLOBAL_PROPERTY_PREFIX + shortName;

		String value = fileProperties.getProperty(utilities.parsetoSnakeCase(shortName));

		if (value == null)
			value = openMrsProperties.getProperty(shortName);

		if (value == null)
			value = fileProperties.getProperty(utilities.parsetoSnakeCase(fullName));
		if (value == null)
			value = fileProperties.getProperty(fullName, fileProperties.getProperty(shortName));
		if (value == null)
			value = System.getProperty(fullName, System.getProperty(shortName));
		if (value == null)
			value = System.getenv(fullName);
		if (value == null)
			value = System.getenv(fullName.toUpperCase().replace('.', '_'));

		return value;
	}

	private static String required(Properties properties, String name) {
		String value = optional(properties, name, null);

		if (!isDefined(value))
			throw new IllegalStateException("Missing required ETL property: epts.etl." + name);

		return value.trim();
	}

	private static String optional(Properties properties, String name, String defaultValue) {
		String value = properties.getProperty(name);
		return isDefined(value) ? value.trim() : defaultValue;
	}

	private static int positiveInteger(Properties properties, String name, int defaultValue) {
		String value = optional(properties, name, null);
		if (value == null)
			return defaultValue;

		try {
			int parsed = Integer.parseInt(value);
			if (parsed <= 0)
				throw new NumberFormatException();
			return parsed;
		} catch (NumberFormatException exception) {
			throw new IllegalStateException(
					"ETL property epts.etl." + name + " must be a positive integer, but was: " + value, exception);
		}
	}

	private static boolean isDefined(String value) {
		return value != null && !value.isBlank();
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public String getResourceTypeField() {
		return resourceTypeField;
	}

	public String getUuidField() {
		return uuidField;
	}

	public String getOperationField() {
		return operationField;
	}

	public int getConnectTimeoutSeconds() {
		return connectTimeoutSeconds;
	}

	public int getRequestTimeoutSeconds() {
		return requestTimeoutSeconds;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
		// TODO Auto-generated method stub

	}
}
