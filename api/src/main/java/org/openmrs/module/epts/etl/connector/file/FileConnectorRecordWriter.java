package org.openmrs.module.epts.etl.connector.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.connector.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorWriteMode;
import org.openmrs.module.epts.etl.connector.ConnectorWriteRequest;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/** Appends one JSON object per line to a file destination resource. */
public final class FileConnectorRecordWriter<T extends EtlDatabaseObject> implements ConnectorRecordWriter<T> {

	private final ConnectorResource resource;
	private final Path path;
	private final ObjectMapper mapper = new ObjectMapperProvider().getContext(EtlDatabaseObject.class)
	        .copy().disable(SerializationFeature.INDENT_OUTPUT);

	public FileConnectorRecordWriter(ConnectorResource resource) {
		this.resource = resource;
		this.path = resolvePath(resource);
	}

	@Override public ConnectorResource getResource() { return resource; }

	@Override
	public EtlOperationResultHeader<EtlDatabaseObject> write(ConnectorWriteRequest<T> request) throws DBException {
		if (request == null) throw new IllegalArgumentException("request cannot be null");
		if (request.getMode() != ConnectorWriteMode.INSERT) {
			throw new UnsupportedOperationException("FILE connector only supports INSERT requests");
		}
		try {
			Path parent = path.getParent();
			if (parent != null) Files.createDirectories(parent);
			try (BufferedWriter output = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
				for (T record : request.getRecords()) {
					output.write(mapper.writeValueAsString(toDocument(record)));
					output.newLine();
				}
			}
			return result(request.getRecords());
		} catch (IOException e) {
			throw new FileConnectorWriteException("Unable to write destination resource " + path, e);
		}
	}

	private Map<String, Object> toDocument(T record) {
		Map<String, Object> document = new LinkedHashMap<>();
		if (record == null || record.getFields() == null) return document;
		for (Field field : record.getFields()) {
			if (field != null && field.getName() != null) document.put(field.getName(), field.getValue());
		}
		return document;
	}

	private EtlOperationResultHeader<EtlDatabaseObject> result(List<T> records) {
		EtlOperationResultHeader<EtlDatabaseObject> result = new EtlOperationResultHeader<>(
				new org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord());
		for (T record : records) result.addToRecordsWithNoError(record);
		return result;
	}

	private static Path resolvePath(ConnectorResource resource) {
		String uri = resource.getEndpoint().getUri();
		if (uri == null || uri.trim().isEmpty()) {
			throw new IllegalArgumentException("FILE connector uri cannot be empty");
		}
		Path root = uri.startsWith("file:") ? Paths.get(URI.create(uri)) : Paths.get(uri);
		ConnectorResourceConfiguration configuration = resource.unwrap(ConnectorResourceConfiguration.class);
		Path resolved = root.resolve(configuration.getName()).normalize().toAbsolutePath();
		Path normalizedRoot = root.normalize().toAbsolutePath();
		if (!resolved.startsWith(normalizedRoot)) {
			throw new IllegalArgumentException("FILE resource must remain below connector uri: " + resource.getName());
		}
		return resolved;
	}

	@Override
	public <N> N unwrap(Class<N> nativeType) {
		if (nativeType != null && nativeType.isInstance(path)) return nativeType.cast(path);
		throw new IllegalArgumentException("FILE writer cannot be unwrapped as "
				+ (nativeType == null ? "null" : nativeType.getName()));
	}
}
