package org.openmrs.module.epts.etl.conf;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/**
 * Central configuration for the database model used by an ETL execution.
 */
public class DataModelConfiguration extends AbstractEtlDataConfiguration {

	private DatabaseObjectInstantiationMode databaseObjectInstantiationMode;

	private SchemaMetadataMode schemaMetadataMode;

	private String srcPojoPackageName;

	private String dstPojoPackageName;

	private String srcSchema;

	private String dstSchema;

	private Boolean overrideExistingDataModelElement;

	private EtlConfiguration relatedConf;

	public DatabaseObjectInstantiationMode getDatabaseObjectInstantiationMode() {
		return databaseObjectInstantiationMode;
	}

	public void setDatabaseObjectInstantiationMode(DatabaseObjectInstantiationMode databaseObjectInstantiationMode) {
		this.databaseObjectInstantiationMode = databaseObjectInstantiationMode;
	}

	public SchemaMetadataMode getSchemaMetadataMode() {
		return schemaMetadataMode;
	}

	public void setSchemaMetadataMode(SchemaMetadataMode schemaMetadataMode) {
		this.schemaMetadataMode = schemaMetadataMode;
	}

	public String getSrcPojoPackageName() {
		return srcPojoPackageName;
	}

	public void setSrcPojoPackageName(String srcPojoPackageName) {
		this.srcPojoPackageName = srcPojoPackageName;
	}

	public String getDstPojoPackageName() {
		return dstPojoPackageName;
	}

	public void setDstPojoPackageName(String dstPojoPackageName) {
		this.dstPojoPackageName = dstPojoPackageName;
	}

	public String getSrcSchema() {
		return srcSchema;
	}

	public void setSrcSchema(String srcSchema) {
		this.srcSchema = srcSchema;
	}

	public String getDstSchema() {
		return dstSchema;
	}

	public void setDstSchema(String dstSchema) {
		this.dstSchema = dstSchema;
	}

	public Boolean getOverrideExistingDataModelElement() {
		return overrideExistingDataModelElement;
	}

	public void setOverrideExistingDataModelElement(Boolean overrideExistingDataModelElement) {
		this.overrideExistingDataModelElement = overrideExistingDataModelElement;
	}

	public boolean shouldOverrideExistingDataModelElement() {
		return Boolean.TRUE.equals(overrideExistingDataModelElement);
	}

	@Override
	public String toString() {
		return "DataModelConfiguration [databaseObjectInstantiationMode=" + databaseObjectInstantiationMode
				+ ", schemaMetadataMode=" + schemaMetadataMode + ", srcPojoPackageName=" + srcPojoPackageName
				+ ", dstPojoPackageName=" + dstPojoPackageName + ", srcSchema=" + srcSchema + ", dstSchema=" + dstSchema
				+ ", overrideExistingDataModelElement=" + overrideExistingDataModelElement + "]";
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.relatedConf;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.relatedConf;
	}

	public void setRelatedConf(EtlConfiguration relatedConf) {
		this.relatedConf = relatedConf;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}
}
