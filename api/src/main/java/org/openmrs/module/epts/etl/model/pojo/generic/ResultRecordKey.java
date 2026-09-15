package org.openmrs.module.epts.etl.model.pojo.generic;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Immutable identity within a configuration instance (the processing scope).
 * Complete primary keys are compared by value. Missing/unsupported keys use
 * object identity, never the mutable domain equals/hashCode implementations.
 */
final class ResultRecordKey {
	private final Object scope;
	private final Map<String, String> values;
	private final EtlDatabaseObject transientRecord;

	private ResultRecordKey(Object scope, Map<String, String> values, EtlDatabaseObject transientRecord) {
		this.scope = scope;
		this.values = values;
		this.transientRecord = transientRecord;
	}

	static ResultRecordKey of(EtlDatabaseObject record) {
		Objects.requireNonNull(record, "record");
		Object scope = record.getRelatedConfiguration();
		if (scope == null) scope = record.getClass();
		Map<String, String> values = new TreeMap<>();
		Oid oid = record.getObjectId();
		if (oid != null && oid.getFields() != null) {
			for (Key key : oid.getFields()) {
				String value = snapshot(key.getValue());
				if (key.getName() == null || value == null) {
					return new ResultRecordKey(scope, null, record);
				}
				values.put(key.getName(), value);
			}
		}
		return values.isEmpty() ? new ResultRecordKey(scope, null, record)
				: new ResultRecordKey(scope, values, null);
	}

	private static String snapshot(Object value) {
		if (value instanceof Number) {
			try { return "number:" + new BigDecimal(value.toString()).stripTrailingZeros().toPlainString(); }
			catch (NumberFormatException ex) { return null; }
		}
		if (value instanceof String || value instanceof Boolean || value instanceof Character
				|| value instanceof java.util.UUID) return value.getClass().getName() + ":" + value;
		if (value instanceof byte[]) return "bytes:" + java.util.Base64.getEncoder().encodeToString((byte[]) value);
		if (value instanceof java.sql.Timestamp) return "timestamp:" + ((java.sql.Timestamp) value).toInstant();
		if (value instanceof java.util.Date) return value.getClass().getName() + ":" + ((java.util.Date) value).getTime();
		return null;
	}

	@Override public boolean equals(Object other) {
		if (!(other instanceof ResultRecordKey)) return false;
		ResultRecordKey key = (ResultRecordKey) other;
		return scope == key.scope && transientRecord == key.transientRecord && Objects.equals(values, key.values);
	}

	@Override public int hashCode() {
		return 31 * System.identityHashCode(scope)
				+ (transientRecord == null ? values.hashCode() : System.identityHashCode(transientRecord));
	}
}
