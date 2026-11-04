package org.openmrs.module.epts.etl.conf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.Field;

/**
 * Connector-neutral description of the records read from or written to a
 * resource. Non-JDBC resources currently support JSON records only.
 */
public class RecordSchemaConfiguration {

	private RecordFormat format = RecordFormat.JSON;

	private List<Field> fields;

	public RecordFormat getFormat() {
		return format;
	}

	public void setFormat(RecordFormat format) {
		this.format = format;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Field> resolveFields(String resourceDescription) {
		if (format == null) {
			format = RecordFormat.JSON;
		}
		if (format != RecordFormat.JSON) {
			throw new EtlConfException("Unsupported record format '" + format + "' for " + resourceDescription);
		}
		if (fields == null || fields.isEmpty()) {
			throw new EtlConfException("recordSchema.fields is required for non-JDBC resource "
					+ resourceDescription);
		}

		Set<String> names = new HashSet<>();
		for (Field field : fields) {
			if (field == null || field.getName() == null || field.getName().trim().isEmpty()) {
				throw new EtlConfException("Every recordSchema field must have a name for " + resourceDescription);
			}
			String normalizedName = field.getName().trim().toLowerCase(Locale.ROOT);
			if (!names.add(normalizedName)) {
				throw new EtlConfException("Duplicated recordSchema field '" + field.getName() + "' for "
						+ resourceDescription);
			}
		}

		return Field.cloneFields(fields, null);
	}

	public static RecordSchemaConfiguration fromFieldNames(List<String> fieldNames) {
		RecordSchemaConfiguration schema = new RecordSchemaConfiguration();
		List<Field> resolvedFields = new ArrayList<>();
		if (fieldNames != null) {
			for (String fieldName : fieldNames) {
				resolvedFields.add(Field.fastCreateField(fieldName));
			}
		}
		schema.setFields(resolvedFields);
		return schema;
	}

	public RecordSchemaConfiguration copy() {
		RecordSchemaConfiguration copy = new RecordSchemaConfiguration();
		copy.setFormat(this.format);
		copy.setFields(Field.cloneFields(this.fields, null));
		return copy;
	}
}
