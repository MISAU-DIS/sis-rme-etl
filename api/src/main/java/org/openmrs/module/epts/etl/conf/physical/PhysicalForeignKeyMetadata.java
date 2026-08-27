package org.openmrs.module.epts.etl.conf.physical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Immutable description of an imported physical foreign key. */
public final class PhysicalForeignKeyMetadata {

	private final String name;
	private final String referencedCatalog;
	private final String referencedSchema;
	private final String referencedTable;
	private final List<PhysicalForeignKeyMapping> mappings;

	@JsonCreator
	public PhysicalForeignKeyMetadata(@JsonProperty("name") String name,
			@JsonProperty("referencedCatalog") String referencedCatalog,
			@JsonProperty("referencedSchema") String referencedSchema,
			@JsonProperty("referencedTable") String referencedTable,
			@JsonProperty("mappings") List<PhysicalForeignKeyMapping> mappings) {
		this.name = name;
		this.referencedCatalog = referencedCatalog;
		this.referencedSchema = referencedSchema;
		this.referencedTable = referencedTable;
		this.mappings = Collections.unmodifiableList(new ArrayList<>(mappings));
	}

	public String getName() { return name; }
	public String getReferencedCatalog() { return referencedCatalog; }
	public String getReferencedSchema() { return referencedSchema; }
	public String getReferencedTable() { return referencedTable; }
	public List<PhysicalForeignKeyMapping> getMappings() { return mappings; }

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof PhysicalForeignKeyMetadata)) return false;
		PhysicalForeignKeyMetadata other = (PhysicalForeignKeyMetadata) object;
		return Objects.equals(name, other.name) && Objects.equals(referencedCatalog, other.referencedCatalog)
				&& Objects.equals(referencedSchema, other.referencedSchema)
				&& Objects.equals(referencedTable, other.referencedTable) && Objects.equals(mappings, other.mappings);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, referencedCatalog, referencedSchema, referencedTable, mappings);
	}

	public static final class PhysicalForeignKeyMapping {
		private final String childColumn;
		private final String parentColumn;

		@JsonCreator
		public PhysicalForeignKeyMapping(@JsonProperty("childColumn") String childColumn,
				@JsonProperty("parentColumn") String parentColumn) {
			this.childColumn = childColumn;
			this.parentColumn = parentColumn;
		}

		public String getChildColumn() { return childColumn; }
		public String getParentColumn() { return parentColumn; }

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof PhysicalForeignKeyMapping)) return false;
			PhysicalForeignKeyMapping other = (PhysicalForeignKeyMapping) object;
			return Objects.equals(childColumn, other.childColumn) && Objects.equals(parentColumn, other.parentColumn);
		}

		@Override
		public int hashCode() { return Objects.hash(childColumn, parentColumn); }
	}
}
