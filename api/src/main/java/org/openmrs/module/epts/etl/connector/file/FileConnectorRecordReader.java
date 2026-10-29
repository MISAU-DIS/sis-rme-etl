package org.openmrs.module.epts.etl.connector.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
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

	public FileConnectorRecordReader(ConnectorResource resource, PrimaryDataSourceConfiguration sourceConfiguration,
			Connection supportingConnection) {
		if (sourceConfiguration == null || !sourceConfiguration.isFullLoaded()) {
			throw new IllegalArgumentException("The source configuration must be fully loaded before creating a FILE reader");
		}
		this.resource = resource;
		this.sourceConfiguration = sourceConfiguration;
		this.supportingConnection = supportingConnection;
		this.path = FileConnectorPathResolver.resolve(resource);
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
				records.add(parse(line, lineNumber));
			}
			return records;
		} catch (IOException e) {
			throw new FileConnectorReadException("Unable to read source resource " + path, e);
		}
	}

	@SuppressWarnings("unchecked")
	private T parse(String json, long lineNumber) throws IOException {
		JsonNode document = mapper.readTree(json);
		if (document == null || !document.isObject()) {
			throw new EtlConfException("Expected one JSON object at line " + lineNumber + " of " + path);
		}

		Set<String> knownFields = new HashSet<>();
		for (Field field : sourceConfiguration.getFields()) {
			knownFields.add(field.getName());
		}
		Iterator<String> documentFields = document.fieldNames();
		while (documentFields.hasNext()) {
			String fieldName = documentFields.next();
			if (!knownFields.contains(fieldName)) {
				throw new EtlConfException("Unknown JSON field '" + fieldName + "' at line " + lineNumber + " of " + path);
			}
		}

		GenericDatabaseObject record = new GenericDatabaseObject(sourceConfiguration);
		for (Field field : sourceConfiguration.getFields()) {
			JsonNode value = document.get(field.getName());
			if ((value == null || value.isNull()) && !field.allowNull()) {
				throw new EtlConfException("Required JSON field '" + field.getName() + "' is missing at line "
						+ lineNumber + " of " + path);
			}
			Object converted = value == null || value.isNull() ? null
					: field.getTypeClass() == null ? mapper.convertValue(value, Object.class)
							: mapper.convertValue(value, field.getTypeClass());
			record.setFieldValue(field.getName(), converted);
		}
		return (T) record;
	}

	public static long countRecords(ConnectorResource resource) {
		Path path = FileConnectorPathResolver.resolve(resource);
		try (java.util.stream.Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
			return lines.count();
		} catch (IOException e) {
			throw new FileConnectorReadException("Unable to count source records in " + path, e);
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
