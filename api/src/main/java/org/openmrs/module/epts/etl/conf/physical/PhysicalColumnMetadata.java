package org.openmrs.module.epts.etl.conf.physical;

import java.util.Objects;

import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.TypePrecision;

/** Immutable, context-free description of one physical database column. */
public final class PhysicalColumnMetadata {

	private final String name;
	private final String dataType;
	private final Integer length;
	private final Integer decimalDigits;
	private final boolean nullable;
	private final boolean autoIncrement;
	private final boolean timestamp;

	public PhysicalColumnMetadata(String name, String dataType, Integer length, Integer decimalDigits, boolean nullable,
			boolean autoIncrement, boolean timestamp) {
		this.name = name;
		this.dataType = dataType;
		this.length = length;
		this.decimalDigits = decimalDigits;
		this.nullable = nullable;
		this.autoIncrement = autoIncrement;
		this.timestamp = timestamp;
	}

	public static PhysicalColumnMetadata fromField(Field field) {
		TypePrecision precision = field.getPrecision();
		return new PhysicalColumnMetadata(field.getName(), field.getDataType(),
				precision == null ? null : precision.getLength(),
				precision == null ? null : precision.getDecimalDigits(), field.isAllowNull(),
				Boolean.TRUE.equals(field.isAutoIncrement()), Boolean.TRUE.equals(field.isTimeStamp()));
	}

	public Field toField() {
		Field field = new Field(name);
		field.setDataType(dataType);
		if (length != null || decimalDigits != null) field.setPrecision(TypePrecision.init(length, decimalDigits));
		field.setAllowNull(nullable);
		field.setAutoIncrement(autoIncrement);
		field.setTimeStamp(timestamp);
		return field;
	}

	public String getName() { return name; }
	public String getDataType() { return dataType; }
	public Integer getLength() { return length; }
	public Integer getDecimalDigits() { return decimalDigits; }
	public boolean isNullable() { return nullable; }
	public boolean isAutoIncrement() { return autoIncrement; }
	public boolean isTimestamp() { return timestamp; }

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof PhysicalColumnMetadata)) return false;
		PhysicalColumnMetadata other = (PhysicalColumnMetadata) object;
		return nullable == other.nullable && autoIncrement == other.autoIncrement && timestamp == other.timestamp
				&& Objects.equals(name, other.name) && Objects.equals(dataType, other.dataType)
				&& Objects.equals(length, other.length) && Objects.equals(decimalDigits, other.decimalDigits);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, dataType, length, decimalDigits, nullable, autoIncrement, timestamp);
	}
}
