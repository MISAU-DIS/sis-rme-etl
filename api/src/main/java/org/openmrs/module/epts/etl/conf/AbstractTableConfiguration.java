package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.datasource.EtlQueryOrderingInfo;
import org.openmrs.module.epts.etl.conf.datasource.PreparedQuery;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableConfiguration;
import org.openmrs.module.epts.etl.conf.physical.FilePhysicalTableMetadataRepository;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableIdentity;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableKey;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableKeyFactory;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableMetadata;
import org.openmrs.module.epts.etl.conf.physical.PhysicalForeignKeyMetadata;
import org.openmrs.module.epts.etl.conf.physical.PhysicalExportedForeignKeyMetadata;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableMetadataFingerprint;
import org.openmrs.module.epts.etl.conf.physical.JdbcPhysicalTableMetadataRepository;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelManifest;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.FileDatabaseModelManifestRepository;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.AutoIncrementHandlingType;
import org.openmrs.module.epts.etl.conf.types.ConflictResolutionType;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectLoaderHelper;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.SQLUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractTableConfiguration extends AbstractEtlDataConfiguration
		implements Comparable<AbstractTableConfiguration>, TableConfiguration {

	private String tableName;

	private List<String> ignorableFields;

	private String tableAlias;

	private List<ParentTable> parents;

	private List<? extends ParentTable> parentRefInfo;

	private List<ChildTable> childRefInfo;

	private Class<? extends EtlDatabaseObject> syncRecordClass;

	private EtlDataConfiguration parentConf;

	private PrimaryKey primaryKey;

	private String sharePkWith;

	private Boolean metadata;

	protected Boolean fullLoaded;

	private Boolean removeForbidden;

	/**
	 * List the field to observe when sync by date (ex: date_created, date_update,
	 * etc)
	 */
	private List<String> observationDateFields;

	private List<UniqueKeyInfo> uniqueKeys;

	/**
	 * If present, the value from this field will be mapped as a primary key for all
	 * tables under this configuration that don't have a primary key but have a
	 * field with name matching this field.
	 */
	private String manualMapPrimaryKeyOnField;

	private List<Field> fields;

	@JsonIgnore
	private PhysicalTableConfiguration physicalTableConfiguration;

	private PreparedQuery defaultPreparedQuery;

	/**
	 * When merge existing records, the incoming dstRecord will win if the listed
	 * fields have the specified values. Note that, for the outer list the join
	 * condition will be "OR" and for the inner list the join condition will be
	 * "AND"
	 */
	private List<List<Field>> winningRecordFieldsInfo;

	private Boolean autoIncrementId;

	private Boolean disabled;

	private Boolean mustLoadChildrenInfo;

	private String extraConditionForExtract;

	private String insertSQLWithObjectId;

	private String insertSQLWithoutObjectId;

	private String updateSql;

	protected DatabaseObjectLoaderHelper loadHealper;

	private Boolean allRelatedTablesFullLoaded;

	private String schema;

	private Boolean usingManualDefinedAlias;

	private String insertSQLQuestionMarksWithObjectId;

	private String insertSQLQuestionMarksWithoutObjectId;

	private Boolean includePrimaryKeyOnInsert;

	private Boolean uniqueKeyInfoLoaded;

	private Boolean primaryKeyInfoLoaded;

	private Boolean fieldsLoaded;

	private Boolean tableNameInfoLoaded;

	private Boolean parentsLoaded;

	private ConflictResolutionType onConflict;

	private Boolean useMysqlInsertIgnore;

	private Boolean ignoreMissingParameters;

	private AutoIncrementHandlingType autoIncrementHandlingType;

	private Integer primaryKeyInitialIncrementValue;

	private EtlQueryOrderingInfo queryOrderingInfo;

	/**
	 * Defines the destination fields that should be updated when the conflict
	 * resolution strategy is {@link ConflictResolutionType#PATCH_EXISTING}.
	 *
	 * <p>
	 * When a conflict is detected and the destination record already exists, only
	 * the fields listed in this property will be updated. All other fields remain
	 * unchanged in the existing destination record.
	 * </p>
	 *
	 * <p>
	 * This property is ignored when the conflict resolution strategy is not
	 * {@code PATCH_EXISTING}.
	 * </p>
	 */
	private List<String> patchFields;

	private List<String> excludedFieldsFromObjectDesc;

	private ActionOnEtlIssue inconsistencyBehavior;

	public AbstractTableConfiguration() {
		this.loadHealper = new DatabaseObjectLoaderHelper(this);
	}

	public AbstractTableConfiguration(String tableName) {
		this();

		this.tableName = tableName;
	}

	public ActionOnEtlIssue getInconsistencyBehavior() {
		return inconsistencyBehavior;
	}

	public void setInconsistencyBehavior(ActionOnEtlIssue inconsistencyBehavior) {
		this.inconsistencyBehavior = inconsistencyBehavior;
	}

	@Override
	public ActionOnEtlIssue inconsistencyBehavior() {
		return this.inconsistencyBehavior;
	}

	@Override
	public List<String> getExcludedFieldsFromObjectDesc() {
		return this.excludedFieldsFromObjectDesc;
	}

	public void setExcludedFieldsFromObjectDesc(List<String> excludedFieldsFromObjectDesc) {
		this.excludedFieldsFromObjectDesc = excludedFieldsFromObjectDesc;
	}

	public List<String> getPatchFields() {
		return patchFields;
	}

	public void setPatchFields(List<String> patchFields) {
		this.patchFields = patchFields;
	}

	@Override
	public EtlQueryOrderingInfo getQueryOrderingInfo() {
		return this.queryOrderingInfo;
	}

	@Override
	public void setQueryOrderingInfo(EtlQueryOrderingInfo orderingInfo) {
		this.queryOrderingInfo = orderingInfo;
	}

	@Override
	public void tryToLoadSchemaInfo(EtlDatabaseObject schemaInfoSrc, Connection conn)
			throws DBException, ForbiddenOperationException, DatabaseResourceDoesNotExists {

		TableConfiguration.super.tryToLoadSchemaInfo(schemaInfoSrc, conn);

		if (this.isTableNameInfoLoaded())
			return;

		if (usesPrecompiledSchemaMetadata()) {
			if (this.getSchema() == null)
				this.setSchema(this.getRelatedConnInfo().determineSchema());
			this.setTableNameInfoLoaded(true);
			return;
		}

		if (this.getSchema() == null) {
			this.setSchema(DBUtilities.determineSchemaName(conn));
		}

		Boolean exists = DBUtilities.isTableExists(this.getSchema(), this.getTableName(), conn);

		if (!exists)
			throw new DatabaseResourceDoesNotExists(this.generateFullTableName(conn));

		this.setTableNameInfoLoaded(true);
	}

	@Override
	public void fullLoad(Connection conn) throws DBException {
		this.tryToLoadDumpScriptContentToFieldAndValidate("extraConditionForExtract",
				this.retrieveAllAvailableTemplateParameters(), conn);

		this.tryToLoadSchemaInfo(null, conn);

		this.attachPhysicalTableConfiguration(conn);

		TableConfiguration.super.fullLoad(conn);
	}

	private void attachPhysicalTableConfiguration(Connection conn) throws DBException {
		if (this.physicalTableConfiguration != null)
			return;

		try {
			PhysicalTableMetadata metadata = resolvePhysicalTableMetadata(conn);
			PhysicalTableIdentity identity = new PhysicalTableIdentity(this.getRelatedConnInfo().getConnectionURI(),
					this.getRelatedConnInfo().getDataBaseUserName(), metadata.getKey().getCatalog(),
					metadata.getKey().getSchema(), metadata.getKey().getTableName());

			this.physicalTableConfiguration = this.getRelatedEtlConf().getPhysicalTableConfigurationRegistry()
					.getOrCreate(identity);

			if (!this.physicalTableConfiguration.hasFields())
				this.physicalTableConfiguration.initialize(metadata);
		} catch (java.io.IOException e) {
			throw new DBException(new SQLException(e));
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	private PhysicalTableMetadata resolvePhysicalTableMetadata(Connection conn)
			throws java.io.IOException, SQLException {
		SchemaMetadataMode mode = this.getRelatedEtlConf().getSchemaMetadataMode();
		if (mode != null && mode.usesFilesFirst()) {
			FilePhysicalTableMetadataRepository files = new FilePhysicalTableMetadataRepository(
					this.getRelatedEtlConf().getSchemaMetadataDirectory());
			java.util.Optional<PhysicalTableMetadata> stored = files
					.find(this.getRelatedConnInfo().getPojoPackageName(), this.getSchema(), this.getTableName());
			if (stored.isPresent()) {
				try {
					validateManifestAssociation(stored.get());
					return stored.get();
				} catch (java.io.IOException exception) {
					if (!mode.allowsJdbcFallback())
						throw exception;
					this.logWarn("Ignoring incompatible precompiled metadata for {}.{}: {}", this.getSchema(),
							this.getTableName(), exception.getMessage());
				}
			}
			if (!mode.allowsJdbcFallback()) {
				throw new java.io.IOException("Precompiled schema metadata not found for " + this.getSchema() + "."
						+ this.getTableName() + " under " + this.getRelatedEtlConf().getSchemaMetadataDirectory()
						+ ". Run DATABASE_MODEL_GENERATION to create it.");
			}
		}

		PhysicalTableKey key = PhysicalTableKeyFactory.create(this, this.getRelatedConnInfo().getPojoPackageName(),
				conn);
		return new JdbcPhysicalTableMetadataRepository(conn).find(key)
				.orElseThrow(() -> new java.io.IOException("Live table metadata not found for " + key));
	}

	private void validateManifestAssociation(PhysicalTableMetadata metadata) throws java.io.IOException {
		DatabaseModelManifest manifest = new FileDatabaseModelManifestRepository(
				this.getRelatedEtlConf().getSchemaMetadataDirectory()).read();
		String expectedKey = metadata.getKey().toString();
		String expectedClass = this.generateFullClassName(this.getRelatedConnInfo());
		String expectedFingerprint = PhysicalTableMetadataFingerprint.sha256(metadata);
		for (DatabaseModelManifest.Entry entry : manifest.getEntries()) {
			if (expectedKey.equals(entry.getMetadataKey()) && expectedClass.equals(entry.getGeneratedClassName())
					&& expectedFingerprint.equals(entry.getMetadataFingerprint()))
				return;
		}
		throw new java.io.IOException("No manifest association between " + expectedKey + " and " + expectedClass);
	}

	private boolean usesPrecompiledSchemaMetadata() {
		return this.getRelatedEtlConf() != null && this.getRelatedEtlConf().usesPrecompiledSchemaMetadata();
	}

	private boolean usesStrictlyPrecompiledSchemaMetadata() {
		return this.getRelatedEtlConf() != null && this.getRelatedEtlConf().getSchemaMetadataMode() != null
				&& this.getRelatedEtlConf().getSchemaMetadataMode().isStrictlyPrecompiled();
	}

	@JsonIgnore
	public PhysicalTableConfiguration getPhysicalTableConfiguration() {
		return physicalTableConfiguration;
	}

	@Override
	public void loadFields(Connection conn) throws DBException {
		if (this.physicalTableConfiguration == null) {
			this.tryToLoadSchemaInfo(null, conn);
			this.attachPhysicalTableConfiguration(conn);
		}

		synchronized (this.physicalTableConfiguration) {
			if (this.physicalTableConfiguration.hasFields()) {
				this.fields = filterIgnorableFields(this.physicalTableConfiguration.copyFields());
				this.setFieldsLoaded(true);
				return;
			}

			this.logDebug("Loading physical fields for table " + getFullTableDescription());
			List<Field> physicalFields = DBUtilities.getTableFields(this.getTableName(), this.getSchema(), conn);
			this.physicalTableConfiguration.initializeFields(physicalFields);
			this.fields = filterIgnorableFields(this.physicalTableConfiguration.copyFields());
			this.setFieldsLoaded(true);
		}
	}

	private List<Field> filterIgnorableFields(List<Field> physicalFields) {
		List<Field> contextualFields = new ArrayList<>();
		for (Field field : physicalFields) {
			if (!this.isIgnorableField(field)) {
				contextualFields.add(field);
			}
		}
		return contextualFields;
	}

	@Override
	public void loadPrimaryKeyInfo(Connection conn) throws DBException {
		if (this.isPrimaryKeyInfoLoaded())
			return;

		// Explicit/manual PK configuration is contextual and must not populate the
		// physical cache for other usages of the same table.
		if (this.primaryKey != null || this.hasManualMapPrimaryKeyOnField()) {
			TableConfiguration.super.loadPrimaryKeyInfo(conn);
			return;
		}

		if (this.physicalTableConfiguration == null) {
			this.tryToLoadSchemaInfo(null, conn);
			this.attachPhysicalTableConfiguration(conn);
		}

		synchronized (this.physicalTableConfiguration) {
			if (this.physicalTableConfiguration.isPrimaryKeyLoaded()) {
				this.primaryKey = this.physicalTableConfiguration.copyPrimaryKey(this);
				this.setPrimaryKeyInfoLoaded(true);
				return;
			}

			TableConfiguration.super.loadPrimaryKeyInfo(conn);
			this.physicalTableConfiguration.initializePrimaryKey(this.primaryKey);
		}
	}

	@Override
	public void loadUniqueKeys(Connection conn) {
		if (this.isUniqueKeyInfoLoaded())
			return;

		if (usesStrictlyPrecompiledSchemaMetadata()) {
			if (this.uniqueKeys != null) {
				TableConfiguration.super.loadUniqueKeys(conn);
				return;
			}
			this.uniqueKeys = this.physicalTableConfiguration.copyUniqueKeys(this);
			if (this.uniqueKeys != null && utilities.listHasElement(this.ignorableFields)) {
				this.uniqueKeys.removeIf(key -> key.getFields().stream().anyMatch(this::isIgnorableField));
			}
			this.setUniqueKeyInfoLoaded(true);
			return;
		}

		// Unique-key discovery currently observes contextual field exclusions and
		// shared-PK relationships. Keep those cases local until physical FK DTOs exist.
		if (this.uniqueKeys != null || utilities.listHasElement(this.ignorableFields) || this.useSharedPKKey()) {
			TableConfiguration.super.loadUniqueKeys(conn);
			return;
		}

		if (this.physicalTableConfiguration == null) {
			try {
				this.tryToLoadSchemaInfo(null, conn);
				this.attachPhysicalTableConfiguration(conn);
			} catch (DBException e) {
				throw new RuntimeException(e);
			}
		}

		synchronized (this.physicalTableConfiguration) {
			if (this.physicalTableConfiguration.areUniqueKeysLoaded()) {
				this.uniqueKeys = this.physicalTableConfiguration.copyUniqueKeys(this);
				this.setUniqueKeyInfoLoaded(true);
				return;
			}

			TableConfiguration.super.loadUniqueKeys(conn);
			this.physicalTableConfiguration.initializeUniqueKeys(this.uniqueKeys);
		}
	}

	@Override
	public void loadParents(Connection conn) throws DBException {
		if (!usesStrictlyPrecompiledSchemaMetadata()) {
			TableConfiguration.super.loadParents(conn);
			return;
		}
		if (this.isParentsLoaded())
			return;
		List<ParentTable> resolved = new ArrayList<>();
		for (PhysicalForeignKeyMetadata foreignKey : this.physicalTableConfiguration.getImportedForeignKeys()) {
			ParentTableImpl parent = ParentTableImpl.init(foreignKey.getReferencedTable(), foreignKey.getName(), this);
			parent.setSchema(
					utilities.stringHasValue(foreignKey.getReferencedSchema()) ? foreignKey.getReferencedSchema()
							: foreignKey.getReferencedCatalog());
			parent.setParentConf(this.getParentConf());
			List<RefMapping> mappings = new ArrayList<>();
			for (PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping physicalMapping : foreignKey.getMappings()) {
				if (utilities.containsAll(this.getIgnorableFields(), physicalMapping.getChildColumn()))
					continue;
				RefMapping mapping = RefMapping.fastCreate(physicalMapping.getChildColumn(),
						physicalMapping.getParentColumn());
				Field childField = this.getField(physicalMapping.getChildColumn());
				if (childField != null) {
					mapping.getChildField().setDataType(childField.getDataType());
					mapping.getParentField().setDataType(childField.getDataType());
					mapping.setIgnorable(childField.isAllowNull());
				}
				mapping.setParentTabConf(parent);
				mappings.add(mapping);
			}
			parent.setRefMapping(mappings);
			if (!mappings.isEmpty()) {
				applyConfiguredParentContext(parent);
				resolved.add(parent);
				markSharedPrimaryKey(parent);
			}
		}
		addManualOnlyParents(resolved);
		this.setParentRefInfo(resolved);
		this.setParentsLoaded(true);
	}

	@Override
	public void loadChildren(Connection conn) throws SQLException {
		if (!usesStrictlyPrecompiledSchemaMetadata()) {
			TableConfiguration.super.loadChildren(conn);
			return;
		}
		if (!this.isMustLoadChildrenInfo())
			return;
		List<ChildTable> children = new ArrayList<>();
		for (PhysicalExportedForeignKeyMetadata foreignKey : this.physicalTableConfiguration.getExportedForeignKeys()) {
			ChildTable child = ChildTable.init(foreignKey.getChildTable(), foreignKey.getName(), this);
			child.setSchema(utilities.stringHasValue(foreignKey.getChildSchema()) ? foreignKey.getChildSchema()
					: foreignKey.getChildCatalog());
			child.setParentConf(this.getParentConf());
			List<RefMapping> mappings = new ArrayList<>();
			for (PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping physicalMapping : foreignKey.getMappings()) {
				RefMapping mapping = RefMapping.fastCreate(physicalMapping.getChildColumn(),
						physicalMapping.getParentColumn());
				Field parentField = this.getField(physicalMapping.getParentColumn());
				if (parentField != null) {
					mapping.getChildField().setDataType(parentField.getDataType());
					mapping.getParentField().setDataType(parentField.getDataType());
				}
				mapping.setChildTabConf(child);
				mappings.add(mapping);
			}
			child.setRefMapping(mappings);
			children.add(child);
		}
		this.setChildRefInfo(children);
	}

	private void applyConfiguredParentContext(ParentTableImpl discovered) {
		if (!utilities.listHasElement(this.getParents()))
			return;
		for (ParentTable configured : this.getParents()) {
			boolean sameReference = utilities.stringHasValue(configured.getRefCode())
					&& configured.getRefCode().equals(discovered.getRefCode());
			if (!sameReference && !configured.getTableName().equals(discovered.getTableName()))
				continue;
			discovered.setConditionalFields(configured.getConditionalFields());
			discovered.setDefaultValueDueInconsistency(configured.getDefaultValueDueInconsistency());
			discovered.setSetNullDueInconsistency(configured.isSetNullDueInconsistency());
			discovered.setIgnorableFields(configured.getIgnorableFields());
			if (configured.hasMapping()) {
				for (RefMapping mapping : discovered.getRefMapping()) {
					RefMapping configuredMapping = configured.findRefMapping(mapping.getChildFieldName(),
							mapping.getParentFieldName());
					if (configuredMapping == null) {
						throw new ForbiddenOperationException("Configured parent mapping " + mapping
								+ " does not match physical foreign key " + discovered.getRefCode());
					}
					mapping.setIgnorable(configuredMapping.isIgnorable() || mapping.isIgnorable());
					mapping.setDefaultValueDueInconsistency(configuredMapping.getDefaultValueDueInconsistency());
					mapping.setSetNullDueInconsistency(configuredMapping.isSetNullDueInconsistency());
				}
			}
			return;
		}
	}

	private void addManualOnlyParents(List<ParentTable> resolved) {
		if (!utilities.listHasElement(this.getParents()))
			return;
		for (ParentTable configured : this.getParents()) {
			boolean alreadyResolved = false;
			for (ParentTable parent : resolved) {
				if ((utilities.stringHasValue(configured.getRefCode())
						&& configured.getRefCode().equals(parent.getRefCode())) || configured.equals(parent)) {
					alreadyResolved = true;
					break;
				}
			}
			if (!alreadyResolved && configured.hasMapping()) {
				configured.setChildTableConf(this);
				configured.setManualyConfigured(true);
				resolved.add(configured);
			}
		}
	}

	private void markSharedPrimaryKey(ParentTableImpl parent) {
		if (this.getPrimaryKey() == null || parent.getRefMapping() == null)
			return;
		List<String> primaryKeyFields = this.getPrimaryKey().generateListFromFieldsNames();
		List<String> childFields = new ArrayList<>();
		for (RefMapping mapping : parent.getRefMapping())
			childFields.add(mapping.getChildFieldName());
		if (primaryKeyFields.size() == childFields.size() && childFields.containsAll(primaryKeyFields)) {
			this.setSharePkWith(parent.getTableName());
		}
	}

	@Override
	public Boolean useAutoIncrementId(Connection conn) throws DBException {
		if (!usesStrictlyPrecompiledSchemaMetadata())
			return TableConfiguration.super.useAutoIncrementId(conn);
		if (this.getPrimaryKey() == null || this.getPrimaryKey().isCompositeKey())
			return false;
		Field primaryKeyField = this.getField(this.getPrimaryKey().retrieveSimpleKeyColumnName());
		return primaryKeyField != null && Boolean.TRUE.equals(primaryKeyField.isAutoIncrement());
	}

	@Override
	public void loadOwnElements(EtlDatabaseObject schemaInfo, Connection conn) throws DBException {

		if (hasExtraConditionForExtract()) {
			if (!SQLUtilities.isValidSelectSqlQuery("select * from where " + this.getExtraConditionForExtract(),
					null)) {
				throw new EtlConfException("Invalid extraConditionForExtract  \n" + this.getExtraConditionForExtract());
			}
		}

		if (this.inconsistencyBehavior == null) {
			this.inconsistencyBehavior = this.getRelatedEtlConf().getDefaultInconsistencyBehavior();
		}

		if (this.loadHealper == null) {
			this.loadHealper = new DatabaseObjectLoaderHelper(this);
		}
		if (this.onConflict == null) {
			this.onConflict = ConflictResolutionType.MAKE_YOUR_DECISION;
		} else {
			if (!this.onConflict.isAllowedForConfiguration()) {
				throw new EtlConfException("The value for onConflict " + this.onConflict
						+ " is not allowed for table configuration [" + this.getTableAlias() + "]");
			}
		}
	}

	@Override
	public AutoIncrementHandlingType getAutoIncrementHandlingType() {
		return autoIncrementHandlingType;
	}

	@Override
	public Integer getPrimaryKeyInitialIncrementValue() {
		return primaryKeyInitialIncrementValue;
	}

	@Override
	public void setPrimaryKeyInitialIncrementValue(Integer primaryKeyInitialIncrementValue) {
		this.primaryKeyInitialIncrementValue = primaryKeyInitialIncrementValue;
	}

	@Override
	public void setAutoIncrementHandlingType(AutoIncrementHandlingType autoIncrementHandlingType) {
		this.autoIncrementHandlingType = autoIncrementHandlingType;
	}

	public Boolean isIgnoreMissingParameters() {
		return isTrue(ignoreMissingParameters);
	}

	public Boolean ignoreMissingParameters() {
		return isTrue(ignoreMissingParameters);
	}

	public void setIgnoreMissingParameters(Boolean ignoreMissingParameters) {
		this.ignoreMissingParameters = ignoreMissingParameters;
	}

	public String getManualMapPrimaryKeyOnField() {
		return manualMapPrimaryKeyOnField;
	}

	public void setManualMapPrimaryKeyOnField(String manualMapPrimaryKeyOnField) {
		this.manualMapPrimaryKeyOnField = manualMapPrimaryKeyOnField;
	}

	public Boolean isUseMysqlInsertIgnore() {
		return isTrue(useMysqlInsertIgnore);
	}

	@Override
	public Boolean useMysqlInsertIgnore() {
		return isUseMysqlInsertIgnore();
	}

	public void setUseMysqlInsertIgnore(Boolean useMysqlInsertIgnore) {
		this.useMysqlInsertIgnore = useMysqlInsertIgnore;
	}

	@Override
	public Boolean isParentsLoaded() {
		return isTrue(parentsLoaded);
	}

	@Override
	public void setParentsLoaded(Boolean parentsLoaded) {
		this.parentsLoaded = parentsLoaded;
	}

	@Override
	public Boolean isFieldsLoaded() {
		return isTrue(fieldsLoaded);
	}

	@Override
	public void setFieldsLoaded(Boolean fieldsLoaded) {
		this.fieldsLoaded = fieldsLoaded;
	}

	@Override
	public Boolean isTableNameInfoLoaded() {
		return isTrue(tableNameInfoLoaded);
	}

	@Override
	public void setTableNameInfoLoaded(Boolean tableNameInfoLoaded) {
		this.tableNameInfoLoaded = tableNameInfoLoaded;
	}

	@Override
	public Boolean isPrimaryKeyInfoLoaded() {
		return isTrue(primaryKeyInfoLoaded);
	}

	@Override
	public void setPrimaryKeyInfoLoaded(Boolean primaryKeyInfoLoaded) {
		this.primaryKeyInfoLoaded = primaryKeyInfoLoaded;
	}

	@Override
	public Boolean isUniqueKeyInfoLoaded() {
		return isTrue(uniqueKeyInfoLoaded);
	}

	@Override
	public void setUniqueKeyInfoLoaded(Boolean uniqueKeyInfoLoaded) {
		this.uniqueKeyInfoLoaded = uniqueKeyInfoLoaded;
	}

	@Override
	public Boolean includePrimaryKeyOnInsert() {
		return isTrue(includePrimaryKeyOnInsert);
	}

	@Override
	public void setIncludePrimaryKeyOnInsert(Boolean includePrimaryKeyOnInsert) {
		this.includePrimaryKeyOnInsert = includePrimaryKeyOnInsert;
	}

	public Boolean isIncludePrimaryKeyOnInsert() {
		return isTrue(includePrimaryKeyOnInsert);
	}

	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return insertSQLQuestionMarksWithObjectId;
	}

	@Override
	public void setInsertSQLQuestionMarksWithObjectId(String insertSQLQuestionMarksWithObjectId) {
		this.insertSQLQuestionMarksWithObjectId = insertSQLQuestionMarksWithObjectId;
	}

	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return insertSQLQuestionMarksWithoutObjectId;
	}

	@Override
	public void setInsertSQLQuestionMarksWithoutObjectId(String insertSQLQuestionMarksWithoutObjectId) {
		this.insertSQLQuestionMarksWithoutObjectId = insertSQLQuestionMarksWithoutObjectId;
	}

	public void setUsingManualDefinedAlias(Boolean usingManualDefinedAlias) {
		this.usingManualDefinedAlias = usingManualDefinedAlias;
	}

	@Override
	public Boolean isUsingManualDefinedAlias() {
		return isTrue(this.usingManualDefinedAlias);
	}

	@Override
	public String getSchema() {
		return schema;
	}

	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Override
	public List<String> getIgnorableFields() {
		return ignorableFields;
	}

	@Override
	public void setIgnorableFields(List<String> ignorableFields) {
		this.ignorableFields = ignorableFields;
	}

	@Override
	public void loadManualConfiguredPk(Connection conn) throws ForbiddenOperationException, DBException {
		if (this.primaryKey != null) {
			if (!isPrimaryKeyInfoLoaded()) {
				this.primaryKey.setManualConfigured(true);
				try {
					this.primaryKey.setTabConf(this);
				} catch (NullPointerException e) {
					throw e;
				}

				this.setPrimaryKeyInfoLoaded(true);
				this.primaryKey.setKeyName("pk");

				if (!isFieldsLoaded()) {
					loadFields(conn);
				}

				for (Field key : this.primaryKey.getFields()) {
					Field field = getField(key.getName());

					if (field != null) {
						key.setDataType(field.getDataType());
					} else {
						throw new ForbiddenOperationException("The field '" + key.getName()
								+ "' defined as part of primaryKey cannot found on table " + getFullTableName() + "'");
					}
				}
			}
		} else {
			throw new ForbiddenOperationException("The primaryKey is null!");
		}
	}

	private void tryToManualLoadConfiguredPk(Connection conn) throws DBException {
		try {
			loadManualConfiguredPk(conn);
		} catch (ForbiddenOperationException e) {
		}
	}

	@Override
	public PrimaryKey getPrimaryKey() {
		if (isPrimaryKeyInfoLoaded()) {
			return primaryKey;
		}

		OpenConnection conn = null;

		try {
			conn = getRelatedConnInfo().openConnection(this);

			tryToManualLoadConfiguredPk(conn);

			loadPrimaryKeyInfo(conn);

			return this.primaryKey;
		} catch (DBException e) {
			throw new RuntimeException(e);
		} finally {
			finalizeConnection(conn, this);
		}
	}

	public ConflictResolutionType getOnConflict() {
		return onConflict;
	}

	public void setOnConflict(ConflictResolutionType onConflict) {
		this.onConflict = onConflict;
	}

	@Override
	public ConflictResolutionType onConflict() {
		return getOnConflict();
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;

		if (hasPK()) {
			this.getPrimaryKey().setTabConf(this);
		}
	}

	public Boolean isAllRelatedTablesFullLoaded() {
		return isTrue(allRelatedTablesFullLoaded);
	}

	public void setAllRelatedTablesFullLoaded(Boolean allRelatedTablesFullLoaded) {
		this.allRelatedTablesFullLoaded = allRelatedTablesFullLoaded;
	}

	public Class<? extends EtlDatabaseObject> getSyncRecordClass() {

		if (syncRecordClass == null) {
			this.syncRecordClass = GenericDatabaseObject.class;
		}

		return getSyncRecordClass(getRelatedConnInfo());
	}

	public void setFullLoaded(Boolean fullLoaded) {
		this.fullLoaded = fullLoaded;
	}

	public void setInsertSQLWithObjectId(String insertSQLWithObjectId) {
		this.insertSQLWithObjectId = insertSQLWithObjectId;
	}

	public void setInsertSQLWithoutObjectId(String insertSQLWithoutObjectId) {
		this.insertSQLWithoutObjectId = insertSQLWithoutObjectId;
	}

	@Override
	public void setUpdateSql(String updateSQL) {
		this.updateSql = updateSQL;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	@Override
	public String getAlias() {
		return getTableAlias();
	}

	public void setTableAlias(String tableAlias) {
		if (hasAlias() && !hasDynamicAlias() && !tableAlias.equals(this.getTableAlias())) {
			throw new ForbiddenOperationException("This table has already an alias and change is forbidden!");
		}

		this.tableAlias = tableAlias;
	}

	@Override
	public DatabaseObjectLoaderHelper getLoadHealper() {
		return this.loadHealper;
	}

	public String getInsertSQLWithObjectId() {
		return insertSQLWithObjectId;
	}

	public String getInsertSQLWithoutObjectId() {
		return insertSQLWithoutObjectId;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	@Override
	public String getExtraConditionForExtract() {
		return extraConditionForExtract;
	}

	@Override
	public void setExtraConditionForExtract(String extraConditionForExtract) {
		this.extraConditionForExtract = extraConditionForExtract;
	}

	public Boolean isMustLoadChildrenInfo() {
		return isTrue(mustLoadChildrenInfo);
	}

	public void setMustLoadChildrenInfo(Boolean mustLoadChildrenInfo) {
		this.mustLoadChildrenInfo = mustLoadChildrenInfo;
	}

	public Boolean isAutoIncrementId() {
		return isTrue(autoIncrementId);
	}

	public void setAutoIncrementId(Boolean autoIncrementId) {
		this.autoIncrementId = autoIncrementId;
	}

	public List<List<Field>> getWinningRecordFieldsInfo() {
		return winningRecordFieldsInfo;
	}

	public void setWinningRecordFieldsInfo(List<List<Field>> winningRecordFieldsInfo) {
		this.winningRecordFieldsInfo = winningRecordFieldsInfo;
	}

	public Boolean hasWinningRecordsInfo() {
		return this.winningRecordFieldsInfo != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ParentTable> getParentRefInfo() {
		return (List<ParentTable>) parentRefInfo;
	}

	public void setParentRefInfo(List<? extends ParentTable> parentRefInfo) {
		this.parentRefInfo = parentRefInfo;
	}

	public void setChildRefInfo(List<ChildTable> childRefInfo) {
		this.childRefInfo = childRefInfo;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<UniqueKeyInfo> getUniqueKeys() {
		return uniqueKeys;
	}

	public void setUniqueKeys(List<UniqueKeyInfo> uniqueKeys) {
		this.uniqueKeys = uniqueKeys;
	}

	public List<String> getObservationDateFields() {
		return observationDateFields;
	}

	public void setObservationDateFields(List<String> observationDateFields) {
		this.observationDateFields = observationDateFields;
	}

	public Boolean isRemoveForbidden() {
		return removeForbidden;
	}

	public void setRemoveForbidden(Boolean removeForbidden) {
		this.removeForbidden = removeForbidden;
	}

	@Override
	public List<ChildTable> getChildRefInfo() {
		if (!this.mustLoadChildrenInfo) {
			throw new ForbiddenOperationException(
					"The table configuration is set to not load Children. Please change configuration if you what to access Children ifo.");
		}

		return this.childRefInfo;
	}

	public List<ParentTable> getParents() {
		return parents;
	}

	public void setParents(List<ParentTable> parents) {
		this.parents = parents;
	}

	public String getSharePkWith() {
		return sharePkWith;
	}

	public void setSharePkWith(String sharePkWith) {
		this.sharePkWith = sharePkWith;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return parentConf;
	}

	public void setParentConf(EtlDataConfiguration parentConf) {
		this.parentConf = (EtlDataConfiguration) parentConf;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setSyncRecordClass(Class<? extends EtlDatabaseObject> syncRecordClass) {
		this.syncRecordClass = syncRecordClass;
	}

	@Override
	public Boolean isMetadata() {
		return isTrue(metadata);
	}

	public void setMetadata(Boolean metadata) {
		this.metadata = metadata;
	}

	@JsonIgnore
	public Boolean isFullLoaded() {
		return isTrue(fullLoaded);
	}

	public Boolean isDisabled() {
		return isTrue(disabled);
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public Boolean hasPK() {
		return this.primaryKey != null;
	}

	@Override
	@JsonIgnore
	public String toString() {
		String toString = "Table [" + getFullTableDescription();

		toString += hasPK() ? ", pk: " + this.primaryKey : "";

		toString += "]";

		return toString;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractTableConfiguration))
			return false;

		return getFullTableName().equalsIgnoreCase(((AbstractTableConfiguration) obj).getFullTableName());
	}

	@Override
	public int compareTo(AbstractTableConfiguration o) {
		if (this.equals(o))
			return 0;

		return this.tableName.compareTo(o.getTableName());
	}

	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
		this.setIgnorableFields(utilities.tryToReplacePlaceholdersAll(getIgnorableFields(), schemaInfoSrc));
		setTableAlias(utilities.tryToReplacePlaceholders(getTableAlias(), schemaInfoSrc));

		if (hasParents()) {
			for (ParentTable p : this.getParents()) {
				p.tryToReplacePlaceholders(schemaInfoSrc);
			}
		}

		if (hasPK()) {
			this.getPrimaryKey().tryToReplacePlaceholders(schemaInfoSrc);
		}

		setSharePkWith(utilities.tryToReplacePlaceholders(this.getSharePkWith(), schemaInfoSrc));

		this.setObservationDateFields(
				utilities.tryToReplacePlaceholders(this.getObservationDateFields(), schemaInfoSrc));

		if (hasUniqueKeys()) {
			UniqueKeyInfo.tryToReplacePlaceholders(this.getUniqueKeys(), schemaInfoSrc);
		}

		setExtraConditionForExtract(utilities.tryToReplacePlaceholders(getExtraConditionForExtract(), schemaInfoSrc));

		tryToReplacePlaceholdersOnOwnElements(schemaInfoSrc);
	}

	public PreparedQuery getDefaultPreparedQuery() {
		return defaultPreparedQuery;
	}

	public void setDefaultPreparedQuery(PreparedQuery defaultPreparedQuery) {
		this.defaultPreparedQuery = defaultPreparedQuery;
	}

	@Override
	public void tryToLoadFromTemplate() {
		super.tryToLoadFromTemplate();

		this.loadHealper.setTableConf(this);
	}

	public abstract void tryToReplacePlaceholdersOnOwnElements(EtlDatabaseObject schemaInfoSrc);

}
