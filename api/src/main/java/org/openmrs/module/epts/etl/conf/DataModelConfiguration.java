package org.openmrs.module.epts.etl.conf;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import com.fasterxml.jackson.annotation.JsonFormat;

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

	private String javaFormatterConfigurationFile;

	/** Optional directory where generated POJO source files are written. */
	private String srcPojoDirectory;

	/** Optional directory where compiled POJO classes are written. */
	private String binPojoDirectory;

	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private List<String> classPath = new ArrayList<>();

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

	public String getJavaFormatterConfigurationFile() {
		return javaFormatterConfigurationFile;
	}

	public void setJavaFormatterConfigurationFile(String javaFormatterConfigurationFile) {
		this.javaFormatterConfigurationFile = javaFormatterConfigurationFile;
	}

	public String getSrcPojoDirectory() {
		return srcPojoDirectory;
	}

	public void setSrcPojoDirectory(String srcPojoDirectory) {
		this.srcPojoDirectory = srcPojoDirectory;
	}

	public String getBinPojoDirectory() {
		return binPojoDirectory;
	}

	public void setBinPojoDirectory(String binPojoDirectory) {
		this.binPojoDirectory = binPojoDirectory;
	}

	public List<String> getClassPath() {
		return classPath;
	}

	public void setClassPath(List<String> classPath) {
		this.classPath = classPath == null ? new ArrayList<>() : new ArrayList<>(classPath);
	}

	@Override
	public String toString() {
		return "DataModelConfiguration [databaseObjectInstantiationMode=" + databaseObjectInstantiationMode
				+ ", schemaMetadataMode=" + schemaMetadataMode + ", srcPojoPackageName=" + srcPojoPackageName
				+ ", dstPojoPackageName=" + dstPojoPackageName + ", srcSchema=" + srcSchema + ", dstSchema=" + dstSchema
				+ ", overrideExistingDataModelElement=" + overrideExistingDataModelElement
				+ ", javaFormatterConfigurationFile=" + javaFormatterConfigurationFile + ", srcPojoDirectory="
				+ srcPojoDirectory + ", binPojoDirectory=" + binPojoDirectory + ", classPath=" + classPath + "]";
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
