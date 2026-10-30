package org.openmrs.module.epts.etl.conf.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectLoaderHelper;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.DatabaseEntityPOJOGenerator;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Common ETL record-model capabilities for non-JDBC primary sources. */
public abstract class AbstractRecordPrimaryDataSourceConfiguration extends AbstractEtlDataConfiguration
		implements PrimaryDataSourceConfiguration, EtlDataSource {

	private String name;
	private PrimaryKey primaryKey;
	private List<String> excludedFieldsFromObjectDesc;

	@JsonIgnore
	private transient SourceConfiguration parentSource;
	@JsonIgnore
	private transient Boolean fullLoaded;
	@JsonIgnore
	private transient Class<? extends EtlDatabaseObject> etlRecordClass = GenericDatabaseObject.class;
	@JsonIgnore
	private transient DatabaseObjectLoaderHelper loadHelper = new DatabaseObjectLoaderHelper(this);
	@JsonIgnore
	private transient PreparedQuery defaultPreparedQuery;

	public abstract void setFields(List<Field> fields);

	protected abstract String getSourceTypeName();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setExcludedFieldsFromObjectDesc(List<String> excludedFieldsFromObjectDesc) {
		this.excludedFieldsFromObjectDesc = excludedFieldsFromObjectDesc;
	}

	@Override
	public PreparedQuery getDefaultPreparedQuery() {
		return defaultPreparedQuery;
	}

	@Override
	public void setDefaultPreparedQuery(PreparedQuery defaultPreparedQuery) {
		this.defaultPreparedQuery = defaultPreparedQuery;
	}

	@Override
	public String getQuery() {
		return null;
	}

	@Override
	public SourceConfiguration getParentSource() {
		return parentSource;
	}

	@Override
	public void setParentSource(SourceConfiguration parentSource) {
		this.parentSource = parentSource;
	}

	@Override
	public Boolean isFullLoaded() {
		return isTrue(fullLoaded);
	}

	@Override
	public void fullLoad() throws DBException {
		fullLoad(null);
	}

	@Override
	public void fullLoad(Connection conn) throws DBException {
		validateFields();
		this.etlRecordClass = GenericDatabaseObject.class;
		this.fullLoaded = true;
	}

	private void validateFields() {
		if (getFields() == null || getFields().isEmpty()) {
			throw new EtlConfException("source." + getSourceTypeName() + ".fields is required");
		}
		
		Set<String> names = new HashSet<>();
		
		for (Field field : getFields()) {
			if (field == null || field.getName() == null || field.getName().trim().isEmpty()) {
				throw new EtlConfException("Every source." + getSourceTypeName() + " field must have a name");
			}
			
			if (!names.add(field.getName().trim().toLowerCase(Locale.ROOT))) {
				throw new EtlConfException(
						"Duplicated source field '" + field.getName() + "' in source." + getSourceTypeName());
			}
		}
	}

	@Override
	public TableConfiguration findFullConfiguredConfInAllRelatedTable(String fullTableName,
			List<Integer> alreadyCheckedObjects) {
		return null;
	}

	@Override
	public String generateClassName() {
		String objectName = getObjectName();
		String normalized = objectName == null ? getSourceTypeName() + "_record"
				: objectName.replaceFirst("\\.[^.]+$", "").replaceAll("[^A-Za-z0-9]+", "_");
		StringBuilder className = new StringBuilder();
		for (String part : normalized.split("_")) {
			if (!part.isEmpty())
				className.append(utilities.capitalize(part));
		}
		return className.append("VO").toString();
	}

	@Override
	public String getObjectName() {
		if (utilities.stringHasValue(name))
			return name;
		return getSourceTypeName() + "_record";
	}

	@Override
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	@Override
	public String getSharePkWith() {
		return null;
	}

	@Override
	public Boolean hasPK() {
		return primaryKey != null && primaryKey.hasFields();
	}

	@Override
	public Boolean hasPK(Connection conn) {
		return hasPK();
	}

	@Override
	public Boolean isMetadata() {
		return Boolean.FALSE;
	}

	@Override
	public DBConnectionInfo getRelatedConnInfo() {
		return null;
	}

	@Override
	public void setEtlRecordClass(Class<? extends EtlDatabaseObject> etlRecordClass) {
		this.etlRecordClass = etlRecordClass;
	}

	@Override
	public Class<? extends EtlDatabaseObject> getEtlRecordClass() {
		return etlRecordClass;
	}

	@Override
	public Class<? extends EtlDatabaseObject> generateEtlRecordClass(DBConnectionInfo connInfo) {
		return etlRecordClass;
	}

	@Override
	public Boolean isDestinationInstallationType() {
		return Boolean.FALSE;
	}

	@Override
	public void generateRelatedPojoClass(DBConnectionInfo connInfo, Boolean fullClass) {
		try {
			setEtlRecordClass(fullClass ? DatabaseEntityPOJOGenerator.generate(this, connInfo)
					: DatabaseEntityPOJOGenerator.generateSkeleton(this, connInfo));
		} catch (IOException | SQLException | ClassNotFoundException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public List<ParentTable> getParentRefInfo() {
		return Collections.emptyList();
	}

	@Override
	public List<ChildTable> getChildRefInfo() {
		return Collections.emptyList();
	}

	@Override
	public DatabaseObjectLoaderHelper getLoadHealper() {
		return loadHelper;
	}

	@Override
	public String getAlias() {
		return getObjectName();
	}

	@Override
	public String generateSelectFromQuery() {
		throw new ForbiddenOperationException("A " + getSourceTypeName() + " source does not generate SQL");
	}

	@Override
	public List<String> getExcludedFieldsFromObjectDesc() {
		return excludedFieldsFromObjectDesc;
	}

	@Override
	public Boolean isMustLoadChildrenInfo() {
		return Boolean.FALSE;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return parentSource == null ? null : parentSource.getRelatedEtlConf();
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return parentSource;
	}

	@Override
	public void setParentConf(EtlDataConfiguration relatedParent) {
		if (!(relatedParent instanceof SourceConfiguration))
			throw new EtlConfException("A record primary source must belong to SourceConfiguration");
		this.parentSource = (SourceConfiguration) relatedParent;
	}

	@Override
	public void tryToLoadSchemaInfo(EtlDatabaseObject schemaInfoSrc, Connection conn)
			throws DBException, ForbiddenOperationException, DatabaseResourceDoesNotExists {
		// The record schema is declared by this source configuration.
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
		if (name != null)
			name = utilities.tryToReplacePlaceholders(name, schemaInfoSrc);
	}
}
