package org.openmrs.module.epts.etl.connector.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonRecordLayout;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SourceResourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.json.JsonRecordParser;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Reads one JSON object per line from a FILE connector resource. */
public final class FileConnectorRecordReader<T extends EtlDatabaseObject> implements ConnectorRecordReader<T> {

	private final ConnectorResource resource;
	private final PrimaryDataSourceConfiguration sourceConfiguration;
	private final Connection supportingConnection;
	private final Path path;
	private final ObjectMapper mapper = new ObjectMapperProvider().getContext(EtlDatabaseObject.class);
	private final SourceResourceConfiguration resourceConfiguration;
	private final JsonRecordParser jsonRecordParser;

	public FileConnectorRecordReader(ConnectorResource resource, PrimaryDataSourceConfiguration sourceConfiguration,
			Connection supportingConnection) {
		if (sourceConfiguration == null || !sourceConfiguration.isFullLoaded()) {
			throw new IllegalArgumentException("The source configuration must be fully loaded before creating a FILE reader");
		}
		this.resource = resource;
		this.sourceConfiguration = sourceConfiguration;
		this.supportingConnection = supportingConnection;
		this.path = FileConnectorPathResolver.resolve(resource);
		this.resourceConfiguration = unwrapSourceResource(resource);
		this.jsonRecordParser = sourceConfiguration instanceof JsonPrimaryDataSourceConfiguration
				? new JsonRecordParser((JsonPrimaryDataSourceConfiguration) sourceConfiguration, mapper)
				: null;
	}

	@Override
	public ConnectorResource getResource() {
		return resource;
	}

	@Override
	public List<T> read(ConnectorReadRequest<T> request) throws DBException {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (resourceConfiguration.jsonLayout() != JsonRecordLayout.JSON_LINES) {
			return readJsonDocument(request);
		}

		IntervalExtremeRecord interval = request.getInterval();
		long firstLine = interval == null ? 1 : Math.max(1, interval.getMinRecordId());
		long lastLine = interval == null ? Long.MAX_VALUE : interval.getMaxRecordId();
		List<T> records = new ArrayList<>();

		try (BufferedReader input = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			long lineNumber = 0;
			while ((line = input.readLine()) != null) {
				lineNumber++;
				if (lineNumber < firstLine)
					continue;
				if (lineNumber > lastLine)
					break;
				if (line.trim().isEmpty())
					throw new EtlConfException("Empty JSON record at line " + lineNumber + " of " + path);
				JsonNode recordNode = JsonRecordParser.select(mapper.readTree(line),
						resourceConfiguration.normalizedRecordPath());
				records.add(parse(recordNode, "line " + lineNumber));
			}
			return records;
		} catch (IOException e) {
			throw new FileConnectorReadException("Unable to read source resource " + path, e);
		}
	}

	private List<T> readJsonDocument(ConnectorReadRequest<T> request) {
		try {
			JsonNode root = mapper.readTree(Files.readString(path, StandardCharsets.UTF_8));
			JsonNode selected = JsonRecordParser.select(root, resourceConfiguration.normalizedRecordPath());
			List<JsonNode> recordNodes = new ArrayList<>();
			if (resourceConfiguration.jsonLayout() == JsonRecordLayout.ARRAY && selected != null && selected.isArray()) {
				selected.forEach(recordNodes::add);
			} else if (resourceConfiguration.jsonLayout() == JsonRecordLayout.OBJECT && selected != null
					&& selected.isObject()) {
				recordNodes.add(selected);
			} else {
				throw new EtlConfException("JSON layout " + resourceConfiguration.jsonLayout()
						+ " is incompatible with the node at recordPath '"
						+ resourceConfiguration.normalizedRecordPath() + "' in " + path);
			}

			IntervalExtremeRecord interval = request.getInterval();
			long first = interval == null ? 1 : Math.max(1, interval.getMinRecordId());
			long last = interval == null ? Long.MAX_VALUE : interval.getMaxRecordId();
			List<T> records = new ArrayList<>();
			for (int index = 0; index < recordNodes.size(); index++) {
				long recordNumber = index + 1L;
				if (recordNumber >= first && recordNumber <= last) {
					records.add(parse(recordNodes.get(index), "record " + recordNumber));
				}
			}
			return records;
		} catch (IOException e) {
			throw new FileConnectorReadException("Unable to read source resource " + path, e);
		}
	}

	@SuppressWarnings("unchecked")
	private T parse(JsonNode document, String recordDescription) {
		if (jsonRecordParser == null) {
			throw new EtlConfException("FILE JSON reading requires source.jsonDataSource");
		}
		return (T) jsonRecordParser.parse(document, recordDescription + " of " + path);
	}

	public static long countRecords(ConnectorResource resource) {
		Path path = FileConnectorPathResolver.resolve(resource);
		SourceResourceConfiguration sourceResource = unwrapSourceResource(resource);
		if (sourceResource.jsonLayout() != JsonRecordLayout.JSON_LINES) {
			try {
				ObjectMapper mapper = new ObjectMapperProvider().getContext(EtlDatabaseObject.class);
				JsonNode selected = JsonRecordParser.select(
						mapper.readTree(Files.readString(path, StandardCharsets.UTF_8)),
						sourceResource.normalizedRecordPath());
				if (selected == null || selected.isMissingNode())
					return 0;
				if (sourceResource.jsonLayout() == JsonRecordLayout.ARRAY)
					return selected.isArray() ? selected.size() : 0;
				return selected.isObject() ? 1 : 0;
			} catch (IOException e) {
				throw new FileConnectorReadException("Unable to count source records in " + path, e);
			}
		}
		try (java.util.stream.Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
			return lines.count();
		} catch (IOException e) {
			throw new FileConnectorReadException("Unable to count source records in " + path, e);
		}
	}

	private static SourceResourceConfiguration unwrapSourceResource(ConnectorResource resource) {
		try {
			return resource.unwrap(SourceResourceConfiguration.class);
		} catch (IllegalArgumentException e) {
			SourceResourceConfiguration defaults = new SourceResourceConfiguration();
			defaults.setName(resource.getName());
			return defaults;
		}
	}

	@Override
	public <N> N unwrap(Class<N> nativeType) {
		if (nativeType != null && nativeType.isInstance(path))
			return nativeType.cast(path);
		if (nativeType != null && nativeType.isInstance(supportingConnection))
			return nativeType.cast(supportingConnection);
		throw new IllegalArgumentException("FILE reader cannot be unwrapped as "
				+ (nativeType == null ? "null" : nativeType.getName()));
	}
}
