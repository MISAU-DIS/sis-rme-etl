package org.openmrs.module.epts.etl.model.pojo.generic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.module.epts.etl.common.model.EtlStageRecordVO;
import org.openmrs.module.epts.etl.common.model.SyncImportInfoDAO;
import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.ParentTableImpl;
import org.openmrs.module.epts.etl.conf.RefMapping;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ConflictResolutionType;
import org.openmrs.module.epts.etl.etl.model.stage.EtlStageAreaObject;
import org.openmrs.module.epts.etl.etl.model.stage.EtlStageObjectInfo;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.ParentNotYetMigratedException;
import org.openmrs.module.epts.etl.inconsistenceresolver.model.InconsistenceInfo;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.EtlDatabaseObjectUniqueKeyInfo;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.base.BaseVO;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.InconsistentStateException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractDatabaseObject extends BaseVO implements EtlDatabaseObject {

	protected boolean metadata;

	protected Oid objectId;

	protected String uuid;

	protected EtlStageRecordVO relatedSyncInfo;

	protected List<EtlDatabaseObjectUniqueKeyInfo> uniqueKeysInfo;

	protected List<EtlDatabaseObject> childObjects;

	protected List<EtlDatabaseObject> destinationObjects;

	protected EtlStageObjectInfo etlStageObjectInfo;

	protected EtlInfo etlInfo;

	private Boolean collactable;

	private EtlDatabaseObject sharedPkObj;

	/**
	 * Stable wrappers for configured columns represented by scalar fields inherited
	 * from the base classes. Their identity must survive repeated getFields() calls
	 * because ETL processing stores transient state such as transformingInfo here.
	 */
	private final Map<String, Field> inheritedFieldWrappers = new LinkedHashMap<>();

	/**
	 * If the {@link #relatedConfiguration} is instance of {@link EtlDataSource} the
	 * the objects related to tables presents on
	 * {@link EtlDataSource#getAuxExtractTable()} will be placed on this field.
	 */
	private List<EtlDatabaseObject> auxLoadObject;

	public AbstractDatabaseObject() {
		this.objectId = new Oid();

		this.collactable = true;
	}

	@Override
	@JsonIgnore
	public EtlDatabaseObject getSharedPkObj() {
		return sharedPkObj;
	}

	@Override
	public void setSharedPkObj(EtlDatabaseObject sharedPkObj) {
		this.sharedPkObj = sharedPkObj;
	}

	@Override
	public List<EtlDatabaseObject> getAuxLoadObject() {
		return auxLoadObject;
	}

	@Override
	public void setAuxLoadObject(List<EtlDatabaseObject> auxLoadObjects) {
		this.auxLoadObject = auxLoadObjects;
	}

	public Boolean getCollactable() {
		return collactable;
	}

	public void setCollactable(Boolean collactable) {
		this.collactable = collactable;
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (this.hasFields()) {
			Field f = this.getField(k.getName());

			f.setValue(k.getValue());
		}
	}

	@Override
	public void markAsNotCollactable() {
		setCollactable(false);
	}

	@Override
	public boolean collactable() {
		return this.collactable != null && this.collactable;
	}

	@Override
	public EtlStageObjectInfo getEtlStageObjectInfo() {
		return etlStageObjectInfo;
	}

	@Override
	public void setEtlStageObjectInfo(EtlStageObjectInfo etlStageObjectInfo) {
		this.etlStageObjectInfo = etlStageObjectInfo;
	}

	@Override
	public List<EtlDatabaseObject> getChildObjects() {
		return childObjects;
	}

	@Override
	public void setChildObjects(List<EtlDatabaseObject> childObjects) {
		this.childObjects = childObjects;
	}

	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		try {
			this.uuid = rs.getString("uuid");
		} catch (SQLException e) {
		}

		try {
			this.relatedSyncInfo = new EtlStageRecordVO();
			this.relatedSyncInfo.load(rs);

		} catch (SQLException e) {
		}
	}

	@Override
	public Oid getObjectId() {
		return this.objectId;
	}

	public void setObjectId(Oid objectId) {
		this.objectId = objectId;

		if (objectId != null) {
			for (Key key : objectId.getFields()) {

				String originalName = key.getName();
				String camelName = utilities.parseToCamelCase(originalName);
				String snakeName = utilities.parsetoSnakeCase(originalName);

				try {
					setFieldValue(originalName, key.getValue());
				} catch (ForbiddenOperationException e) {
					try {
						setFieldValue(camelName, key.getValue());
					} catch (ForbiddenOperationException e1) {
						setFieldValue(snakeName, key.getValue());
					}
				}
			}
		}

	}

	/**
	 * Retrieve a specific parent of this dstRecord. The parent is loaded using the
	 * origin (source) identification key
	 * 
	 * @param <T>
	 * @param parentClass parent class
	 * @param parentId    in origin (source database)
	 * @param ignorable
	 * @param conn
	 * @return
	 * @throws ParentNotYetMigratedException if the parent is not ignorable and is
	 *                                       not found on database
	 * @throws DBException
	 */
	@Override
	public EtlDatabaseObject retrieveParentInDestination(Integer parentId, String recordOriginLocationCode,
			TableConfiguration parentTableConfiguration, boolean ignorable, Connection conn)
			throws ParentNotYetMigratedException, DBException {
		if (parentId == null)
			return null;

		EtlDatabaseObject parentOnDestination;

		try {
			parentOnDestination = DatabaseObjectDAO.thinGetByRecordOrigin(parentId, recordOriginLocationCode,
					parentTableConfiguration, conn);
		} catch (DBException e) {
			e.printStackTrace();

			TimeCountDown.sleep(2000);

			throw new RuntimeException(e);
		}

		if (parentOnDestination != null) {
			return parentOnDestination;
		}

		if (ignorable) {
			this.etlInfo.setHasIgnoredParent(true);
			return null;
		}

		throw new ParentNotYetMigratedException(parentId, parentTableConfiguration.getTableName(),
				this.relatedSyncInfo.getRecordOriginLocationCode(), null);
	}

	@Override
	public boolean hasExactilyTheSameDataWith(EtlDatabaseObject srcObj) {
		for (Field field : getFields()) {
			Object thisValue = this.getFieldValue(field.getNameAsClassAtt());
			Object otherValue = srcObj.getFieldValue(field.getNameAsClassAtt());

			if (thisValue == null && otherValue != null || otherValue == null && thisValue != null) {
				return false;
			}

			if (thisValue != null && !thisValue.equals(otherValue)) {
				return false;
			}

		}

		return true;
	}

	@Override
	public List<Field> getFields() {
		List<Field> generatedFields = new ArrayList<>();

		for (java.lang.reflect.Field instanceField : getInstanceFields()) {
			if (!Field.class.isAssignableFrom(instanceField.getType()))
				continue;
			try {
				Field field = (Field) instanceField.get(this);

				if (field != null)
					generatedFields.add(field);
			} catch (IllegalAccessException exception) {
				throw new RuntimeException(exception);
			}
		}

		EtlDatabaseObjectConfiguration configuration = getRelatedConfiguration();

		if (configuration != null && configuration.getFields() != null) {
			for (Field configured : configuration.getFields()) {
				boolean present = generatedFields.stream()
						.anyMatch(field -> utilities.equalsFieldsName(field.getName(), configured.getName()));
				if (present)
					continue;
				String key = normalizeFieldName(configured.getName());
				Field contextual = inheritedFieldWrappers.get(key);
				if (contextual == null) {
					contextual = new Field();
					contextual.copyFrom(configured);
					inheritedFieldWrappers.put(key, contextual);
				}
				try {
					contextual.setValue(getFieldValue(configured.getName()));
				} catch (ForbiddenOperationException ignored) {
					// The configuration may expose a contextual field not represented by this
					// class.
				}
				generatedFields.add(contextual);
			}
		}
		if (generatedFields.isEmpty())
			return super.getFields();

		return generatedFields;
	}

	private String normalizeFieldName(String fieldName) {
		return utilities.parsetoSnakeCase(fieldName).toLowerCase();
	}

	/**
	 * Enriches generated field wrappers with the complete runtime table metadata.
	 */
	protected void enrichGeneratedFields(EtlDatabaseObjectConfiguration configuration) {
		if (configuration == null || configuration.getFields() == null)
			return;
		for (Field configured : configuration.getFields()) {
			boolean generatedFieldFound = false;
			for (java.lang.reflect.Field instanceField : getInstanceFields()) {
				if (!Field.class.isAssignableFrom(instanceField.getType())
						|| !utilities.equalsFieldsName(instanceField.getName(), configured.getName()))
					continue;
				try {
					Field generated = (Field) instanceField.get(this);
					Object value = generated == null ? null : generated.getValue();
					if (generated == null)
						generated = new Field();
					generated.copyFrom(configured);
					generated.setValue(value);
					instanceField.set(this, generated);
					generatedFieldFound = true;
				} catch (IllegalAccessException exception) {
					throw new RuntimeException(exception);
				}
				break;
			}
			if (!generatedFieldFound) {
				String key = normalizeFieldName(configured.getName());
				Field contextual = inheritedFieldWrappers.get(key);
				if (contextual == null) {
					contextual = new Field();
					inheritedFieldWrappers.put(key, contextual);
				}
				Object value = contextual.getValue();
				contextual.copyFrom(configured);
				contextual.setValue(value);
			}
		}
	}

	protected static Field copyGeneratedField(Field source) {
		if (source == null)
			return null;
		Field copy = new Field();
		copy.copyFrom(source);
		return copy;
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		try {

			for (java.lang.reflect.Field field : getInstanceFields()) {
				if (utilities.equalsFieldsName(field.getName(), fieldName)) {
					if (Field.class.isAssignableFrom(field.getType())) {
						Field generated = (Field) field.get(this);
						if (value instanceof Field) {
							field.set(this, value);
						} else {
							if (generated == null)
								generated = Field.fastCreateField(fieldName);
							generated.setValue(value);
							field.set(this, generated);
						}
					} else if (value == null) {
						field.set(this, null);
					} else if (field.getType().equals(String.class)) {
						field.set(this, value.toString());
					} else if (field.getType().equals(Integer.class) && value instanceof Double) {
						/*
						 * Cast value to int if the field type is Integer.
						 * 
						 * This was added to resolve some issues when using generic etl where some field
						 * from query come with double value to be inserted in int fields
						 */
						String str = utilities.displayDoubleOnIntegerFormat((Double) value);

						field.set(this, Integer.parseInt(str));
					} else if (field.getType().isEnum()) {

						@SuppressWarnings("rawtypes")
						Class enumClazz = field.getType();

						@SuppressWarnings("unchecked")
						Enum<?> eValue = Enum.valueOf(enumClazz, value.toString());

						field.set(this, eValue);
					} else {
						field.set(this, value);
					}

					Field inheritedWrapper = inheritedFieldWrappers.get(normalizeFieldName(fieldName));
					if (!Field.class.isAssignableFrom(field.getType()) && inheritedWrapper != null) {
						inheritedWrapper.setValue(value instanceof Field ? ((Field) value).getValue() : value);
					}

					return;
				}
			}

			throw new ForbiddenOperationException(
					"The field " + fieldName + " was not found on entity " + this.getClass().getName());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Object getFieldValue(String fieldsName) throws ForbiddenOperationException {
		Object value = utilities.getFieldValue(this, fieldsName);
		return value instanceof Field ? ((Field) value).getValue() : value;
	}

	@Override
	@JsonIgnore
	public List<EtlDatabaseObjectUniqueKeyInfo> getUniqueKeysInfo() {
		return this.uniqueKeysInfo;
	}

	@Override
	public void setUniqueKeysInfo(List<EtlDatabaseObjectUniqueKeyInfo> uniqueKeysInfo) {
		this.uniqueKeysInfo = uniqueKeysInfo;

		if (utilities.listHasElement(this.uniqueKeysInfo)) {
			for (UniqueKeyInfo uk : this.uniqueKeysInfo) {
				uk.loadValuesToFields(this);
			}
		}
	}

	@Override
	@JsonIgnore
	public EtlStageRecordVO getRelatedSyncInfo() {
		return relatedSyncInfo;
	}

	@Override
	public void setRelatedSyncInfo(EtlStageRecordVO relatedSyncInfo) {
		this.relatedSyncInfo = relatedSyncInfo;
	}

	@Override
	public void setUuid(String uuid) {
		this.uuid = uuid;

		try {
			setFieldValue("uuid", uuid);
		} catch (ForbiddenOperationException e) {
		}
	}

	@Override
	public String getUuid() {
		return this.uuid;
	}

	@Override
	public void save(TableConfiguration tableConfiguration, ConflictResolutionType onConflict, Connection conn)
			throws DBException {
		try {
			DatabaseObjectDAO.insert(this, tableConfiguration, conn);
		} catch (DBException | EtlExceptionImpl e) {
			DBException rootException = null;

			if (e instanceof DBException) {
				rootException = (DBException) e;
			} else {
				if (((EtlExceptionImpl) e).getCause() instanceof DBException) {
					rootException = (DBException) ((EtlExceptionImpl) e).getCause();
				}
			}

			if (rootException != null) {
				DBUtilities.handlePostgresExceptionIssue(conn);

				if (rootException.isDuplicatePrimaryOrUniqueKeyException()) {
					if (onConflict == null && this instanceof EtlStageAreaObject) {
						onConflict = ConflictResolutionType.UPDATE_EXISTING;
					}

					if (onConflict != null) {
						resolveConflictWithExistingRecord(tableConfiguration, onConflict, rootException, conn);
					} else {
						resolveConflictWithExistingRecord(tableConfiguration, ConflictResolutionType.REJECT,
								rootException, conn);
					}
				} else if (rootException.isEtlStageAreaIssue(this)
						&& tableConfiguration.getRelatedEtlConf().getStageRecordIssueBehavior().ignore()) {
					tableConfiguration.logErr("Issue found while persisting stage record {}", e, this);
				} else if (rootException.isInconsistentDataException()) {
					if (tableConfiguration.inconsistencyBehavior().logging()) {
						this.getEtlInfo().setExceptionOnEtl(e);
					} else {
						throw e;
					}

				} else
					throw e;

			} else
				throw e;
		}

	}

	@Override
	public void save(TableConfiguration tableConfiguration, Connection conn) throws DBException {
		try {
			save(tableConfiguration, tableConfiguration.onConflict(), conn);
		} catch (Exception e) {
			tableConfiguration.logWarn("Error happened while persisting record {}", this);

			throw e;
		}
	}

	@Override
	public void update(TableConfiguration syncTableInfo, Connection conn) throws DBException {
		try {
			DatabaseObjectDAO.update(this, conn);
		} catch (DBException e) {
			throw e;
		}
	}

	/**
	 * Resolve collision between existing metadata (in destination) and newly coming
	 * metadata (from any source). The collision resolution consist on changind
	 * existing children to point the newly coming metadata
	 * 
	 * @param tableConfig
	 * @param recordInConflict
	 * @param conn
	 * @throws DBException
	 */
	@SuppressWarnings("unused")
	private void resolveMetadataCollision(EtlDatabaseObject recordInConflict, TableConfiguration tableConfig,
			Connection conn) throws DBException {
		// Object Id Collision
		if (this.getObjectId() == recordInConflict.getObjectId()) {
			recordInConflict.changeObjectId(tableConfig, conn);

			DatabaseObjectDAO.insert(this, tableConfig, conn);
		} else if (this.getUuid() != null && this.getUuid().equals(recordInConflict.getUuid())) {
			// In case of uuid collision it is assumed that the records are same then the
			// old dstRecord must be changed to the new one

			// 1. Change existing dstRecord Uuid
			recordInConflict.setUuid(recordInConflict.getUuid() + "_");

			DatabaseObjectDAO.update(recordInConflict, conn);

			// 2. Check if the new object id is avaliable
			EtlDatabaseObject recOnDBById = DatabaseObjectDAO.getByOid(tableConfig, this.getObjectId(), conn);

			if (recOnDBById == null) {
				// 3. Save the new dstRecord
				DatabaseObjectDAO.insert(this, tableConfig, conn);
			} else {
				recOnDBById.changeObjectId(tableConfig, conn);

				DatabaseObjectDAO.insert(this, tableConfig, conn);
			}

			recordInConflict.changeParentForAllChildren(this, tableConfig, conn);

			recordInConflict.remove(conn);
		}
	}

	@Override
	public void changeObjectId(TableConfiguration syncTableInfo, Connection conn) throws DBException {
		if (syncTableInfo.getPrimaryKey().isCompositeKey()) {
			throw new ForbiddenOperationException("The related table (" + syncTableInfo.getTableName()
					+ ") has composite pk. YOu cannot change the object Id!");
		}

		// 1. backup the old dstRecord
		GenericDatabaseObject oldRecod = GenericDatabaseObject.fastCreate(getRelatedSyncInfo(), syncTableInfo);

		// 2. Retrieve any avaliable id for old dstRecord
		Integer avaliableId = DatabaseObjectDAO.getAvaliableObjectId(syncTableInfo, 999999999, conn);

		this.getObjectId().retrieveSimpleKey().setValue(avaliableId);
		this.setUuid("tmp" + avaliableId);
		this.setRelatedSyncInfo(null);

		// 3. Save the new recod
		DatabaseObjectDAO.insert(this, syncTableInfo, conn);

		// 4. Change existing dstRecord's children to point to new parent
		oldRecod.changeParentForAllChildren(this, syncTableInfo, conn);

		// 5. Remove old dstRecord
		oldRecod.remove(conn);

		// 6. Reset dstRecord info
		this.setUuid(oldRecod.getUuid());
		this.setRelatedSyncInfo(oldRecod.getRelatedSyncInfo());

		DatabaseObjectDAO.update(this, conn);
	}

	@Override
	public void changeParentForAllChildren(EtlDatabaseObject newParent, TableConfiguration syncTableInfo,
			Connection conn) throws DBException {

		if (syncTableInfo.getPrimaryKey().isCompositeKey()) {
			throw new ForbiddenOperationException("The related table (" + syncTableInfo.getTableName()
					+ ") has composite pk. YOu cannot change the parent for children!");
		}

		this.loadObjectIdData(syncTableInfo);

		for (ChildTable refInfo : syncTableInfo.getChildRefInfo()) {

			List<EtlDatabaseObject> children = DatabaseObjectDAO.getByParentId(refInfo.getParentTableConf(),
					refInfo.getSimpleRefMapping().getParentField().getName(), this.getObjectId().getSimpleValueAsInt(),
					conn);

			for (EtlDatabaseObject child : children) {
				child.changeParentValue((ParentTableImpl) refInfo.getParentTableConf(), newParent);
				DatabaseObjectDAO.update(child, conn);
			}
		}
	}

	@Override
	public void refreshLastSyncDateOnOrigin(TableConfiguration tableConfiguration, String recordOriginLocationCode,
			Connection conn) {
		try {
			DatabaseObjectDAO.refreshLastSyncDateOnOrigin(this, tableConfiguration, recordOriginLocationCode, conn);
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void refreshLastSyncDateOnDestination(TableConfiguration tableConfiguration, String recordOriginLocationCode,
			Connection conn) {
		try {
			DatabaseObjectDAO.refreshLastSyncDateOnDestination(this, tableConfiguration, recordOriginLocationCode,
					conn);
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void resolveInconsistence(TableConfiguration tableConfiguration, Connection conn)
			throws InconsistentStateException, DBException {
		if (!tableConfiguration.isFullLoaded())
			tableConfiguration.fullLoad();

		// this.generateRelatedSyncInfo(tableConfiguration,
		// tableConfiguration.getOriginAppLocationCode(), conn);

		Map<ParentTableImpl, Integer> missingParents = loadMissingParents(tableConfiguration, conn);

		int qtyInconsistence = missingParents.size();

		if (qtyInconsistence == 0) {
			getRelatedSyncInfo().setConsistent(EtlDatabaseObject.CONSISTENCE_STATUS);
		} else {
			boolean solvedCurrentInconsistency = true;

			for (Entry<ParentTableImpl, Integer> entry : missingParents.entrySet()) {
				// try to load the default parent

				if (entry.getKey().getSimpleRefMapping().isSetNullDueInconsistency()) {
					this.setParentToNull(entry.getKey());
					this.save(tableConfiguration, conn);

					qtyInconsistence--;
				} else if (entry.getKey().getSimpleRefMapping().getDefaultValueDueInconsistency() != null) {
					Oid oid = Oid.fastCreate(tableConfiguration.getPrimaryKey().retrieveSimpleKey().getNameAsClassAtt(),
							entry.getKey().getSimpleRefMapping().getDefaultValueDueInconsistency());

					EtlDatabaseObject parent = DatabaseObjectDAO.getByOid(entry.getKey(), oid, conn);

					if (parent == null) {
						solvedCurrentInconsistency = false;
					} else {
						this.changeParentValue(entry.getKey(), parent);
						this.save(tableConfiguration, conn);

						qtyInconsistence--;
					}
				} else {
					solvedCurrentInconsistency = false;
				}

				saveInconsistence(tableConfiguration, entry, solvedCurrentInconsistency,
						getRelatedSyncInfo().getRecordOriginLocationCode(), conn);
			}

			if (qtyInconsistence == 0) {
				getRelatedSyncInfo().setConsistent(EtlDatabaseObject.CONSISTENCE_STATUS);
			} else {
				getRelatedSyncInfo().setLastSyncTryErr(generateMissingInfo(missingParents));
				this.remove(conn);
				resolveChildrenInconsistences(tableConfiguration, missingParents, conn);
			}
		}

		getRelatedSyncInfo().save(tableConfiguration, conn);
	}

	private void saveInconsistence(TableConfiguration tableConfiguration,
			Entry<ParentTableImpl, Integer> inconsistenceInfoSource, boolean inconsistenceResoloved,
			String recordOriginLocationCode, Connection conn) throws DBException {

		if (tableConfiguration.getPrimaryKey().isCompositeKey()) {
			throw new ForbiddenOperationException("The related table (" + tableConfiguration.getTableName()
					+ ") has composite pk. You cannot performe the request action!");
		}

		InconsistenceInfo info = InconsistenceInfo.generate(this, inconsistenceInfoSource.getKey(),
				recordOriginLocationCode);
		info.save(tableConfiguration, conn);
	}

	public void resolveChildrenInconsistences(TableConfiguration syncTableInfo,
			Map<ParentTableImpl, Integer> missingParents, Connection conn) throws DBException {

		if (syncTableInfo.getPrimaryKey().isCompositeKey()) {
			throw new ForbiddenOperationException("The related table (" + syncTableInfo.getTableName()
					+ ") has composite pk. You cannot performe the request action!");
		}

		if (!syncTableInfo.getRelatedEtlConf().isSourceSyncProcess())
			throw new EtlExceptionImpl(
					"You cannot move dstRecord to stage area in a installation different to source") {

				private static final long serialVersionUID = 1L;

			};

		if ((syncTableInfo.isMetadata() || syncTableInfo.isRemoveForbidden()) && !syncTableInfo.isRemovableMetadata())
			throw new EtlExceptionImpl("This metadata metadata [" + syncTableInfo.getTableName() + " = "
					+ this.getObjectId() + ". is missing its some parents [" + generateMissingInfo(missingParents)
					+ "] You must resolve this inconsistence manual") {

				private static final long serialVersionUID = 1L;
			};

		for (ChildTable refInfo : syncTableInfo.getChildRefInfo()) {
			if (!refInfo.isConfigured())
				continue;

			Integer qtyChildren = DatabaseObjectDAO.countAllOfParentId(
					refInfo.generateSyncRecordClass(syncTableInfo.getSrcConnInfo()),
					refInfo.getSimpleRefMapping().getChildField().getName(), this.getObjectId().getSimpleValueAsInt(),
					conn);

			if (qtyChildren == 0) {
				continue;
			}

			List<EtlDatabaseObject> children = DatabaseObjectDAO.getByParentId(refInfo,
					refInfo.getSimpleRefMapping().getChildField().getName(), this.getObjectId().getSimpleValueAsInt(),
					conn);

			for (EtlDatabaseObject child : children) {
				child.resolveInconsistence(refInfo, conn);
			}
		}
	}

	@Override
	public void consolidateData(TableConfiguration tableConfiguration, Connection conn) throws DBException {
		utilities.throwReviewMethodException();
	}

	@Override
	public List<EtlDatabaseObject> getDestinationObjects() {
		return this.destinationObjects;
	}

	@Override
	public void setDestinationObjects(List<EtlDatabaseObject> destinationObjects) {
		this.destinationObjects = destinationObjects;
	}

	@Override
	public void loadDestParentInfo(TableConfiguration tableInfo, String recordOriginLocationCode, Connection conn)
			throws ParentNotYetMigratedException, DBException {

		utilities.throwReviewMethodException();
	}

	@Override
	public EtlStageRecordVO retrieveRelatedSyncInfo(TableConfiguration tableInfo, String recordOriginLocationCode,
			Connection conn) throws DBException {
		return SyncImportInfoDAO.retrieveFromOpenMRSObject(tableInfo, this, recordOriginLocationCode, conn);
	}

	public void removeDueInconsistency(TableConfiguration syncTableInfo, Map<ParentTableImpl, Integer> missingParents,
			Connection conn) throws DBException {

		utilities.throwReviewMethodException();

	}

	public void remove(Connection conn) throws DBException {
		DatabaseObjectDAO.remove(this, conn);
	}

	public Map<ParentTableImpl, Integer> loadMissingParents(TableConfiguration tableInfo, Connection conn)
			throws DBException {

		utilities.throwReviewMethodException();

		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null)
			return false;

		if (!obj.getClass().equals(this.getClass()))
			return false;

		AbstractDatabaseObject objAsOpenMrs = (AbstractDatabaseObject) obj;

		if (this.getObjectId().equals(objAsOpenMrs.getObjectId()))
			return this.getObjectId() == objAsOpenMrs.getObjectId();

		if (utilities.stringHasValue(this.getUuid()) && utilities.stringHasValue(objAsOpenMrs.getUuid())) {
			return this.getUuid().equals(objAsOpenMrs.getUuid());
		}

		if (this.hasUniqueKeys()) {
			for (UniqueKeyInfo key : this.getUniqueKeysInfo()) {

				UniqueKeyInfo otherKey = objAsOpenMrs.getUniqueKeyInfo(key);

				if (otherKey != null && key.hasSameValues(otherKey)) {
					return true;
				}

			}
		}

		return super.equals(obj);
	}

	public String generateMissingInfo(Map<ParentTableImpl, Integer> missingParents) {
		String missingInfo = "";

		for (Entry<ParentTableImpl, Integer> missing : missingParents.entrySet()) {
			missingInfo = utilities.concatStringsWithSeparator(missingInfo,
					"[" + missing.getKey().getTableName() + ": " + missing.getValue() + "]", ";");
		}

		return "The dstRecord [" + this.generateTableName() + " = " + this.getObjectId()
				+ "] is in inconsistent state. There are missing these parents: " + missingInfo;
	}

	public String generateMissingInfoForSolvedInconsistency(Map<ParentTableImpl, Integer> missingParents) {
		String missingInfo = "";

		for (Entry<ParentTableImpl, Integer> missing : missingParents.entrySet()) {
			missingInfo = utilities.concatStringsWithSeparator(missingInfo,
					"[" + missing.getKey().getTableName() + ": " + missing.getValue() + "]", ";");
		}

		return "The dstRecord [" + this.generateTableName() + " = " + this.getObjectId()
				+ "] is was in inconsistent state solved using some default parents.  These are missing parents: "
				+ missingInfo;
	}

	@SuppressWarnings("unchecked")
	public Class<EtlDatabaseObject> tryToGetExistingCLass(File targetDirectory, String fullClassName) {
		try {
			URLClassLoader loader = URLClassLoader.newInstance(new URL[] { targetDirectory.toURI().toURL() });

			Class<EtlDatabaseObject> c = (Class<EtlDatabaseObject>) loader.loadClass(fullClassName);

			loader.close();

			return c;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		}
	}

	@Override
	@JsonIgnore
	public String toString() {

		String objectId = "objectId = " + (this.getObjectId() != null ? this.getObjectId() : "");

		String objectName = null;

		if (this.getRelatedConfiguration() instanceof TableConfiguration) {
			objectName = ((TableConfiguration) this.getRelatedConfiguration()).getTableAlias();
		} else {
			objectName = this.getObjectName();
		}

		StringBuilder uniqueKeysDesc = new StringBuilder();

		if (utilities.listHasElement(getUniqueKeysInfo())) {
			for (UniqueKeyInfo uniqueKey : getUniqueKeysInfo()) {
				uniqueKey.loadValuesToFields(this);

				if (uniqueKeysDesc.length() > 0) {
					uniqueKeysDesc.append(", ");
				}

				uniqueKeysDesc.append(uniqueKey);
			}
		}

		StringBuilder fieldsDesc = new StringBuilder();

		if (utilities.listHasElement(getFields())) {
			for (Field field : getFields()) {
				if (getRelatedConfiguration().fieldIsExcludedFromObjectDesc(field)) {
					continue;
				}

				if (fieldsDesc.length() > 0) {
					fieldsDesc.append(", ");
				}

				fieldsDesc.append(field.getName()).append(" = ")
						.append(field.getValue() != null ? field.getValue() : "");
			}
		}

		StringBuilder description = new StringBuilder(objectName + "[");

		description.append(objectId);

		if (uniqueKeysDesc.length() > 0) {
			description.append(", uniqueKeys = {").append(uniqueKeysDesc).append("}");
		}

		if (fieldsDesc.length() > 0) {
			description.append(", fields = {").append(fieldsDesc).append("}");
		}

		description.append("]");

		return description.toString();
	}

	@Override
	public void fastCreateSimpleNumericKey(long i) {
		Oid oid = new Oid();

		Key k = new Key();
		k.setValue(i);

		oid.addKey(k);
	}

	@Override
	public void setParentToNull(ParentTableImpl refInfo) {
		for (RefMapping map : refInfo.getRefMapping()) {
			setFieldValue(map.getChildFieldNameAsAttClass(), null);
		}
	}

	@Override
	public void changeParentValue(ParentTable refInfo, EtlDatabaseObject newParent) {
		for (RefMapping map : refInfo.getRefMapping()) {
			Object parentValue = newParent.getFieldValue(map.getParentFieldNameAsAttClass());
			this.setFieldValue(map.getChildFieldNameAsAttClass(), parentValue);
		}
	}

	@Override
	public EtlInfo getEtlInfo() {
		return this.etlInfo;
	}

	@Override
	public void setEtlInfo(EtlInfo info) {
		this.etlInfo = info;
	}

}
