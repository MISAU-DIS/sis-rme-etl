package org.openmrs.module.epts.etl.conf.datasource.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonRecordCardinality;
import org.openmrs.module.epts.etl.conf.datasource.JsonRecordDataSourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Converts one raw JSON record into the main ETL object and its auxiliary objects. */
public final class JsonRecordParser {

	private final JsonPrimaryDataSourceConfiguration primaryConfiguration;

	private final ObjectMapper mapper;

	private final List<JsonRecordDataSourceConfiguration> extraDataSources;

	public JsonRecordParser(JsonPrimaryDataSourceConfiguration primaryConfiguration,
			List<JsonRecordDataSourceConfiguration> extraDataSources, ObjectMapper mapper) {
		this.primaryConfiguration = primaryConfiguration;
		this.extraDataSources = extraDataSources == null ? java.util.Collections.emptyList() : extraDataSources;
		this.mapper = mapper;
	}

	public EtlDatabaseObject parse(JsonNode recordNode, String recordDescription) {
		if (recordNode == null || !recordNode.isObject()) {
			throw new EtlConfException("Expected one JSON object at " + recordDescription);
		}

		Set<String> structuralFields = new HashSet<>();
		if (!extraDataSources.isEmpty()) {
			for (JsonRecordDataSourceConfiguration extra : extraDataSources) {
				structuralFields.add(firstPathSegment(extra.getPath()));
			}
		}

		GenericDatabaseObject primary = parseObject(recordNode, primaryConfiguration.getFields(), structuralFields,
				primaryConfiguration, recordDescription);
		List<EtlDatabaseObject> auxiliary = new ArrayList<>();
		if (!extraDataSources.isEmpty()) {
			for (JsonRecordDataSourceConfiguration extra : extraDataSources) {
				parseExtra(recordNode, extra, auxiliary, recordDescription);
			}
		}
		primary.setAuxLoadObject(auxiliary);
		return primary;
	}

	private void parseExtra(JsonNode recordNode, JsonRecordDataSourceConfiguration configuration,
			List<EtlDatabaseObject> target, String recordDescription) {
		JsonNode selected = select(recordNode, configuration.getPath());
		if (selected == null || selected.isMissingNode() || selected.isNull()) {
			if (configuration.isRequired()) {
				throw new EtlConfException("Required JSON data source '" + configuration.getName()
						+ "' was not found at path '" + configuration.getPath() + "' in " + recordDescription);
			}
			return;
		}

		if (configuration.getCardinality() == JsonRecordCardinality.MANY) {
			if (!selected.isArray()) {
				throw new EtlConfException("JSON data source '" + configuration.getName()
						+ "' expects an array at path '" + configuration.getPath() + "'");
			}
			int index = 0;
			for (JsonNode child : selected) {
				target.add(parseObject(child, configuration.getFields(), java.util.Collections.emptySet(), configuration,
						recordDescription + configuration.getPath() + "[" + index++ + "]"));
			}
			return;
		}

		if (!selected.isObject()) {
			throw new EtlConfException("JSON data source '" + configuration.getName()
					+ "' expects an object at path '" + configuration.getPath() + "'");
		}
		target.add(parseObject(selected, configuration.getFields(), java.util.Collections.emptySet(), configuration,
				recordDescription + configuration.getPath()));
	}

	private GenericDatabaseObject parseObject(JsonNode node, List<Field> fields, Set<String> ignoredStructuralFields,
			org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration configuration,
			String description) {
		if (node == null || !node.isObject()) {
			throw new EtlConfException("Expected JSON object at " + description);
		}
		Set<String> knownFields = new HashSet<>();
		for (Field field : fields) {
			knownFields.add(field.getName());
		}
		Iterator<String> names = node.fieldNames();
		while (names.hasNext()) {
			String name = names.next();
			if (!knownFields.contains(name) && !ignoredStructuralFields.contains(name)) {
				throw new EtlConfException("Unknown JSON field '" + name + "' at " + description);
			}
		}

		GenericDatabaseObject object = new GenericDatabaseObject(configuration);
		for (Field field : fields) {
			JsonNode value = node.get(field.getName());
			if ((value == null || value.isNull()) && !field.allowNull()) {
				throw new EtlConfException("Required JSON field '" + field.getName() + "' is missing at " + description);
			}
			Object converted = value == null || value.isNull() ? null
					: field.getTypeClass() == null ? mapper.convertValue(value, Object.class)
							: mapper.convertValue(value, field.getTypeClass());
			object.setFieldValue(field.getName(), converted);
		}
		return object;
	}

	public static JsonNode select(JsonNode root, String pointer) {
		if (root == null || pointer == null || pointer.trim().isEmpty() || "/".equals(pointer.trim())) {
			return root;
		}
		String normalized = pointer.trim();
		if (!normalized.startsWith("/")) {
			throw new EtlConfException("JSON path must use JSON Pointer syntax and start with '/': " + pointer);
		}
		return root.at(normalized);
	}

	private String firstPathSegment(String pointer) {
		if (pointer == null || pointer.trim().isEmpty() || "/".equals(pointer.trim())) {
			throw new EtlConfException("An extraJsonRecordDataSource path must identify a child node");
		}
		String normalized = pointer.startsWith("/") ? pointer.substring(1) : pointer;
		int separator = normalized.indexOf('/');
		return (separator < 0 ? normalized : normalized.substring(0, separator)).replace("~1", "/")
				.replace("~0", "~");
	}
}
