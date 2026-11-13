package org.openmrs.module.epts.etl.model.pojo.generic;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationContext;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.PojoNotFoundException;
import org.openmrs.module.epts.etl.exceptions.RequireJdbcException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.DatabaseEntityPOJOGenerator;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a data base which can be represented by a Pojo. The
 * {@link EtlObjectConfiguration} can be a database table or a query result
 */
public interface EtlObjectConfiguration extends EtlDataConfiguration {

	Boolean isFullLoaded();

	void fullLoad() throws DBException;

	void fullLoad(Connection conn) throws DBException;

	TableConfiguration findFullConfiguredConfInAllRelatedTable(String fullTableName,
			List<Integer> alreadyCheckedObjects);

	ConnectorResourceConfiguration getResource();

	void setResource(ConnectorResourceConfiguration resource);

	@JsonIgnore
	void setResolvedResource(ConnectorResource resource);

	@JsonIgnore
	EtlSide getEtlSide();

	void setEtlSide(EtlSide side);

	@JsonIgnore
	default EtlSide requireEtlSide() {

		if (getEtlSide() == null) {
			throw new ForbiddenOperationException(
					"The ETL side is not initialized for object configuration '" + getObjectName() + "'");
		}
		return getEtlSide();
	}

	@JsonIgnore
	default File getPOJOCopiledFilesDirectory() {
		return getRelatedEtlConf().getPOJOCompiledFilesDirectory();
	}

	@JsonIgnore
	default File getPOJOSourceFilesDirectory() {
		return getRelatedEtlConf().getPOJOSourceFilesDirectory();
	}

	default DBConnectionInfo getSrcConnInfo() {
		return getRelatedEtlConf().getSrcConnInfo();
	}

	@JsonIgnore
	default String generateFullPackageName(EtlSide side) {
		String rootPackageName = "org.openmrs.module.epts.etl.model.pojo";

		String packageName = getClassPackage(side);

		String fullPackageName = utilities.concatStringsWithSeparator(rootPackageName, packageName, ".");

		return fullPackageName;
	}

	@JsonIgnore
	default String generateFullPackageName(DatabaseModelGenerationContext context) {
		return utilities.concatStringsWithSeparator("org.openmrs.module.epts.etl.model.pojo",
				context.getPojoPackageName(), ".");
	}

	@JsonIgnore
	default String getOriginAppLocationCode() {
		return getRelatedEtlConf().getOriginAppLocationCode();
	}

	@JsonIgnore
	default String getClassPackage(EtlSide side) {

		if (side == null) {
			throw new ForbiddenOperationException(
					"Cannot resolve the POJO package without an ETL side for '" + getObjectName() + "'");
		}
		setEtlSide(side);
		return getRelatedEtlConf().getPojoPackage(side);
	}

	@JsonIgnore
	default String getClassPackage(DatabaseModelGenerationContext context) {
		return context.getPojoPackageName();
	}

	default String getClassPackageForForder(EtlSide side) {
		String packageS = this.getClassPackage(side);

		String[] packageParts = packageS.split("\\.");

		String pF = "";

		for (String p : packageParts) {
			pF = utilities.concatStringsWithSeparator(pF, p, "" + File.separatorChar);
		}

		return pF;
	}

	default String getClassPackageForForder(DatabaseModelGenerationContext context) {
		return getClassPackage(context).replace('.', File.separatorChar);
	}

	@JsonIgnore
	default String generateFullClassName(EtlSide side) {
		String rootPackageName = "org.openmrs.module.epts.etl.model.pojo";

		String packageName = getClassPackage(side);

		String fullPackageName = utilities.concatStringsWithSeparator(rootPackageName, packageName, ".");

		return utilities.concatStringsWithSeparator(fullPackageName, generateClassName(), ".");
	}

	@JsonIgnore
	default String generateFullClassName(DatabaseModelGenerationContext context) {
		return utilities.concatStringsWithSeparator(generateFullPackageName(context), generateClassName(), ".");
	}

	@JsonIgnore
	default List<File> getClassPath() {
		return this.getParentConf().getRelatedEtlConf().getClassPathAsFiles();
	}

	String generateClassName();

	EtlDataConfiguration getParentConf();

	String getObjectName();

	<T extends Field> List<T> getFields();

	UniqueKeyInfo getPrimaryKey();

	/**
	 * Returns the logical unique keys exposed by this record model.
	 * <p>
	 * Non-JDBC record models are not required to define additional unique keys;
	 * their configured primary key is still available through
	 * {@link #getPrimaryKey()}.
	 */
	default List<UniqueKeyInfo> getUniqueKeys() {
		return Collections.emptyList();
	}

	String getSharePkWith();

	Boolean hasPK();

	Boolean hasPK(Connection conn) throws DBException;

	Boolean isMetadata();

	/**
	 * Returns the connector resource resolved for this object, when available.
	 * Legacy configurations may rely exclusively on {@link #getRelatedConnInfo()}.
	 */
	@JsonIgnore
	default ConnectorResource getResolvedResource() {
		return null;
	}

	/**
	 * Opens the JDBC connection owned by this object configuration. The resolved
	 * connector takes precedence over the legacy related connection information.
	 * The returned connection must be finalized by the same {@code openedFrom}
	 * object supplied when it was opened.
	 *
	 * @param openedFrom configuration component that owns this connection lifecycle
	 */
	default OpenConnection openConnection(BaseConfiguration openedFrom) throws DBException {
		return this.getRelatedConnInfo().openConnection(openedFrom);
	}

	default DBConnectionInfo getRelatedConnInfo() {
		ConnectorResource resolvedResource = this.getResolvedResource();

		if (resolvedResource != null && resolvedResource.getEndpoint() != null
				&& resolvedResource.getEndpoint().getType() != ConnectorType.JDBC) {
			throw new RequireJdbcException(
					"Object configuration '" + getObjectName() + "' is bound to a non-JDBC resource");
		}

		if (resolvedResource == null || resolvedResource.getEndpoint() == null) {
			throw new RequireJdbcException(
					"The connector resource is not initialized for object configuration '" + getObjectName() + "'");
		}

		DBConnectionInfo connectionInfo = resolvedResource.getEndpoint().unwrap(DBConnectionInfo.class);

		if (connectionInfo == null) {
			throw new RequireJdbcException(
					"No JDBC connection information is available for object configuration '" + getObjectName() + "'");
		}

		return connectionInfo;
	}

	void setEtlRecordClass(Class<? extends EtlDatabaseObject> syncRecordClass);

	Boolean isDestinationInstallationType();

	void generateRelatedPojoClass(EtlSide side, Boolean fullClass);

	default void generateRelatedPojoClass(DatabaseModelGenerationContext context, Boolean fullClass) {

		try {

			if (fullClass) {
				setEtlRecordClass(DatabaseEntityPOJOGenerator.generate(this, context));
			} else {
				throw new ForbiddenOperationException(
						"Skeleton generation is only supported by the legacy DBConnectionInfo contract");
			}
		} catch (ClassNotFoundException exception) {
			throw new RuntimeException(exception);
		} catch (java.io.IOException | java.sql.SQLException exception) {
			throw new RuntimeException(exception);
		}
	}

	List<ParentTable> getParentRefInfo();

	List<ChildTable> getChildRefInfo();

	DatabaseObjectLoaderHelper getLoadHealper();

	String getAlias();

	/**
	 * Generates a full dump select from query.
	 * 
	 * @return the generated select dump query
	 */
	String generateSelectFromQuery();

	List<String> getExcludedFieldsFromObjectDesc();

	default EtlConfiguration getRelatedEtlConf() {
		return this.getParentConf() != null ? this.getParentConf().getRelatedEtlConf() : null;
	}

	default Boolean hasDateFields() {

		for (Field t : this.getFields()) {

			if (t.isDateField()) {
				return true;
			}
		}

		return false;
	}

	default Class<? extends EtlDatabaseObject> getEtlRecordClass() throws ForbiddenOperationException {
		throw new ForbiddenOperationException("Forbiden method! Please implement your own method!");
	}

	/**
	 * Creates a record using the class selected by the configured data-model mode
	 * and associates this configuration with the new object.
	 */
	default EtlDatabaseObject createRecordInstance() {
		Class<? extends EtlDatabaseObject> recordClass = getEtlRecordClass();

		if (recordClass == null) {
			recordClass = this.generateEtlRecordClass(requireEtlSide());
		}

		try {
			EtlDatabaseObject record = recordClass.getDeclaredConstructor().newInstance();
			record.setRelatedConfiguration(this);
			return record;
		} catch (ReflectiveOperationException exception) {
			throw new PojoNotFoundException(
					"Could not instantiate ETL record class " + recordClass.getName() + " for " + getObjectName(),
					exception);
		}
	}

	@JsonIgnore
	default Class<? extends EtlDatabaseObject> generateEtlRecordClass(EtlSide side) throws PojoNotFoundException {
		return resolveEtlRecordClass(generateFullClassName(side));
	}

	@JsonIgnore
	default Class<? extends EtlDatabaseObject> generateEtlRecordClass(DatabaseModelGenerationContext context)
			throws PojoNotFoundException {
		return resolveEtlRecordClass(generateFullClassName(context));
	}

	private Class<? extends EtlDatabaseObject> resolveEtlRecordClass(String fullClassName) {
		Class<? extends EtlDatabaseObject> recordClass;
		EtlConfiguration etlConfiguration = getRelatedEtlConf();

		if (etlConfiguration.usesPrecompiledPojoObjects()
				|| etlConfiguration.usesPrecompiledPojoObjectsWithFallBack()) {
			recordClass = DatabaseEntityPOJOGenerator.tryToGetExistingCLass(fullClassName, etlConfiguration);

			if (recordClass == null) {
				etlConfiguration.warn("POJO NOT FOUND FOR {}\nUsing DatabaseObjectInstantiationMode: {}", this,
						etlConfiguration.getDatabaseObjectInstantiationMode());

				if (etlConfiguration.usesPrecompiledPojoObjectsWithFallBack()) {
					etlConfiguration.warn("USING GENERIC POJO FOR {}\nUsing DatabaseObjectInstantiationMode: {}", this,
							etlConfiguration.getDatabaseObjectInstantiationMode());
					recordClass = GenericDatabaseObject.class;
				} else {
					throw new PojoNotFoundException(this);
				}
			}
		} else {
			recordClass = GenericDatabaseObject.class;
		}

		setEtlRecordClass(recordClass);
		return recordClass;
	}

	default List<String> getParentRefInfoAsString() {
		List<String> parents = new ArrayList<>();

		if (hasParentRefInfo()) {

			for (ParentTable p : this.getParentRefInfo()) {
				parents.add(p.getTableName());
			}
		}

		return parents;
	}

	default Boolean hasParentRefInfo() {
		return utilities.listHasElement(this.getParentRefInfo());
	}

	default Boolean hasChildRefInfo() {
		return isMustLoadChildrenInfo() && utilities.listHasElement(this.getChildRefInfo());
	}

	Boolean isMustLoadChildrenInfo();

	default Boolean containsField(String fieldName) {

		if (this.hasFields()) {

			for (Field f : this.getFields()) {

				if (f.getName().equals(fieldName)) {
					return true;
				}
			}
		}
		return false;
	}

	default Field getField(String fieldName) {

		if (this.hasFields()) {

			for (Field f : this.getFields()) {

				if (f.getName().equals(fieldName)) {
					return f;
				}
			}
		}

		return null;
	}

	default List<Field> cloneFields(EtlDatabaseObject originalObject) {
		return Field.cloneFields(this.getFields(), originalObject);
	}

	default Boolean hasFields() {
		return utilities.listHasElement(this.getFields());
	}

	default Boolean hasCompositeKey() {
		return this.getPrimaryKey() != null && this.getPrimaryKey().isCompositeKey();
	}

	default boolean fieldIsExcludedFromObjectDesc(Field field) {
		return hasExcludedFieldsFromObjectDesc() && this.getExcludedFieldsFromObjectDesc().contains(field.getName());
	}

	default boolean hasExcludedFieldsFromObjectDesc() {
		return utilities.listHasElement(this.getExcludedFieldsFromObjectDesc());
	}

	default Boolean containsAllFields(List<Field> fields) {
		if (!this.hasFields() || fields == null)
			return false;

		for (Field f : fields) {
			if (!this.containsField(f.getName()))
				return false;
		}

		return true;
	}

	default Boolean hasAlias() {
		return utilities.stringHasValue(this.getAlias());
	}

	TableConfiguration retrieveRelatedTableConf();
}
