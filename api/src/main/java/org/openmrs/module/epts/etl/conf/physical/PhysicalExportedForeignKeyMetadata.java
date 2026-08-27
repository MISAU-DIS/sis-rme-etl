package org.openmrs.module.epts.etl.conf.physical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Immutable description of a foreign key exported to a child table. */
public final class PhysicalExportedForeignKeyMetadata {

	private final String name;
	private final String childCatalog;
	private final String childSchema;
	private final String childTable;
	private final List<PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping> mappings;

	@JsonCreator
	public PhysicalExportedForeignKeyMetadata(@JsonProperty("name") String name,
			@JsonProperty("childCatalog") String childCatalog, @JsonProperty("childSchema") String childSchema,
			@JsonProperty("childTable") String childTable,
			@JsonProperty("mappings") List<PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping> mappings) {
		this.name = name;
		this.childCatalog = childCatalog;
		this.childSchema = childSchema;
		this.childTable = childTable;
		this.mappings = mappings == null ? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(mappings));
	}

	public String getName() { return name; }
	public String getChildCatalog() { return childCatalog; }
	public String getChildSchema() { return childSchema; }
	public String getChildTable() { return childTable; }
	public List<PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping> getMappings() { return mappings; }

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof PhysicalExportedForeignKeyMetadata)) return false;
		PhysicalExportedForeignKeyMetadata other = (PhysicalExportedForeignKeyMetadata) object;
		return Objects.equals(name, other.name) && Objects.equals(childCatalog, other.childCatalog)
				&& Objects.equals(childSchema, other.childSchema) && Objects.equals(childTable, other.childTable)
				&& Objects.equals(mappings, other.mappings);
	}

	@Override
	public int hashCode() { return Objects.hash(name, childCatalog, childSchema, childTable, mappings); }
}
