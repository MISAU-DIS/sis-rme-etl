package org.openmrs.module.epts.etl.openmrs.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.epts.etl.openmrs.conf.OpenMrsIndexConfiguration;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Small HTTP client dedicated to index-update requests. */
public class OpenMrsIndexClient {

	private final OpenMrsIndexConfiguration configuration;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public OpenMrsIndexClient(OpenMrsIndexConfiguration configuration) {
		this.configuration = configuration;
		this.httpClient = HttpClient.newBuilder()
		        .connectTimeout(Duration.ofSeconds(configuration.getConnectTimeoutSeconds())).build();
		this.objectMapper = new ObjectMapperProvider().getContext(OpenMrsIndexClient.class);
	}

	public void updateIndex(String resourceType, String uuid, String operation) throws IOException, InterruptedException {
		Map<String, String> payload = new LinkedHashMap<>();
		payload.put("resourceType", resourceType);
		payload.put("uuid", uuid);
		if (operation != null && !operation.isBlank())
			payload.put("operationType", operation);

		HttpRequest.Builder request = HttpRequest.newBuilder(buildUri())
		        .timeout(Duration.ofSeconds(configuration.getRequestTimeoutSeconds()))
		        .header("Content-Type", "application/json")
		        .header("Accept", "application/json")
		        .POST(HttpRequest.BodyPublishers.ofString(toJson(payload), StandardCharsets.UTF_8));

		applyAuthentication(request);

		HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new IOException("OpenMRS index endpoint returned HTTP " + response.statusCode() + ": "
			        + abbreviate(response.body(), 1000));
		}
	}

	private URI buildUri() {
		String baseUrl = configuration.getBaseUrl().replaceAll("/+$", "");
		String endpoint = configuration.getEndpoint().replaceAll("^/+", "");
		return URI.create(baseUrl + "/" + endpoint);
	}

	private String toJson(Map<String, String> payload) throws JsonProcessingException {
		return objectMapper.writeValueAsString(payload);
	}

	private void applyAuthentication(HttpRequest.Builder request) {
		if (configuration.getBearerToken() != null) {
			request.header("Authorization", "Bearer " + configuration.getBearerToken());
		} else if (configuration.getUsername() != null) {
			String credentials = configuration.getUsername() + ":" + configuration.getPassword();
			request.header("Authorization", "Basic " + Base64.getEncoder()
			        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
		}
	}

	private static String abbreviate(String value, int maximumLength) {
		if (value == null || value.length() <= maximumLength)
			return value;
		return value.substring(0, maximumLength) + "...";
	}
}
