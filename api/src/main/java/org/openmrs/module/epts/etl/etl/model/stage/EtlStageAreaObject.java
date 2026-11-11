package org.openmrs.module.epts.etl.etl.model.stage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlConfigurationTableConf;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.types.ConflictResolutionType;
import org.openmrs.module.epts.etl.etl.model.EtlLoadStatus;
import org.openmrs.module.epts.etl.exceptions.EtlException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.MissingFieldException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlObjectConfiguration;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class EtlStageAreaObject extends GenericDatabaseObject {

	private List<EtlDatabaseObject> keyInfo;

	private EtlDatabaseObject relatedEtlObject;

	private Boolean alreadyExistOnDB;

	private Boolean keyInfoAlreadyExistsOnDb;

	private EtlStageTableType type;

	private EtlLoadStatus status;

	public EtlStageAreaObject() {
	}

	private EtlStageAreaObject(EtlStageAreaObject srcStageInfoObject, EtlDatabaseObject obj, EtlStageTableType type,
			EtlConfigurationTableConf keyInfoTabConf, Connection srcConn, Connection dstConn) throws DBException {

		this.type = type;

		this.relatedEtlObject = obj;

		this.setRelatedConfiguration(keyInfoTabConf);
	}

	private EtlStageAreaObject(EtlStageAreaObject srcStageInfoObject, EtlDatabaseObject obj, EtlStageTableType type,
			Connection srcConn) throws DBException {

		this.type = type;
		this.relatedEtlObject = obj;

		EtlObjectConfiguration objectConfiguration = obj.getRelatedConfiguration();

		EtlConfigurationTableConf keyInfoTabConf = null;

		if (type.isSrc()) {
			EtlConfigurationTableConf srcStageTable = StageAreaStorageManager.resolveStageTable(objectConfiguration,
					type, srcConn);
			this.setRelatedConfiguration(srcStageTable);

			this.setFieldValue("record_origin_location_code", objectConfiguration.getOriginAppLocationCode());
			this.setFieldValue("compacted_object_uk", UniqueKeyInfo.compactAll(this.getAllKeys()));

			this.status = EtlLoadStatus.NOT_LOADED;

			if (obj.hasDestinationRecords()) {
				status = obj.getLoadStatus();
			}

			this.setFieldValue("migration_status", status.toInt());

			keyInfoTabConf = StageAreaStorageManager.resolveKeyTable(objectConfiguration, type, srcConn);

			EtlStageAreaObject existing = EtlStageAreaObjectDAO.getByKey(srcStageTable,
					this.getFieldValue("compacted_object_uk").toString(),
					this.getFieldValue("record_origin_location_code").toString(), srcConn);

			if (existing != null) {
				this.setFieldValue("id", existing.getFieldValue("id"));
				this.loadObjectIdData();

				setAlreadyExistOnDB(true);
				setKeyInfoAlreadyExistsOnDb(true);

			} else {
				setAlreadyExistOnDB(false);
				setKeyInfoAlreadyExistsOnDb(false);
			}

		} else if (type.isDst()) {
			if (!obj.isInEtlProcess())
				throw new EtlExceptionImpl("The destination object " + obj + ". Is not in etl process");

			if (srcStageInfoObject == null)
				throw new EtlExceptionImpl("The related srcStageInfoObject cannot be null");

			EtlConfigurationTableConf dstStageTable = StageAreaStorageManager.resolveStageTable(objectConfiguration,
					type, srcConn);

			this.setRelatedConfiguration(dstStageTable);

			EtlObjectConfiguration sourceConfiguration = this.getRelatedEtlObject().getEtlInfo().getPrimaryDataSource();
			EtlConfigurationTableConf srcStageTable = StageAreaStorageManager.resolveStageTable(sourceConfiguration,
					EtlStageTableType.SRC, srcConn);

			keyInfoTabConf = StageAreaStorageManager.resolveKeyTable(objectConfiguration, type, srcConn);

			String operation_id = obj.getEtlInfo().getRelatedItemConf().getConfigCode();

			EtlStageAreaObject existing = null;

			if (srcStageInfoObject.hasValuedObjectId()) {
				String condition = "etl_confing_id = ? and src_stage_table_name = ? and stage_record_id = ? and dst_table_name = ? ";
				Object[] params = { operation_id, srcStageTable.getTableName(),
						srcStageInfoObject.getObjectId().asSimpleValue(),
						StageAreaStorageManager.resolveObjectName(objectConfiguration) };

				existing = EtlStageAreaObjectDAO.get(dstStageTable, condition, params, srcConn);

				if (existing != null) {
					this.setFieldValue("id", existing.getFieldValue("id"));
					this.setFieldValue("stage_record_id", existing.getFieldValue("id"));
					this.loadObjectIdData();

					existing.loadObjectIdData();

					setAlreadyExistOnDB(true);
				} else {
					setAlreadyExistOnDB(false);
				}
			} else {
				setAlreadyExistOnDB(false);
			}

			this.status = obj.getEtlInfo().hasExceptionOnEtl() ? EtlLoadStatus.NOT_LOADED_DUE_ERRORS
					: EtlLoadStatus.FULL_LOADED;

			this.setFieldValue("last_sync_try_err",
					!status.isFullLoaded()
							? utilities.garantirXCaracteres(obj.getEtlInfo().getExceptionOnEtl().getLocalizedMessage(),
									500)
							: null);

			this.setFieldValue("etl_confing_id", operation_id);
			this.setFieldValue("src_stage_table_name", srcStageTable.getTableName());
			this.setFieldValue("migration_status", status.toInt());
			this.setFieldValue("last_sync_date", DateAndTimeUtilities.getCurrentSystemDate(srcConn));
			this.setFieldValue("dst_table_name", StageAreaStorageManager.resolveObjectName(objectConfiguration));

			if (status.isFullLoaded()) {
				this.setFieldValue("dst_compacted_object_uk", UniqueKeyInfo.compactAll(this.getAllKeys()));
				this.setFieldValue("conflict_resolution_type",
						this.getRelatedEtlObject().getEtlInfo().getConflictResolutionType().toString());
			} else {
				this.setFieldValue("conflict_resolution_type", ConflictResolutionType.NONE.toString());
			}

		} else if (type.isProcessedRecord()) {
			this.alreadyExistOnDB = false;

			EtlConfigurationTableConf processedRecordStageTable = StageAreaStorageManager
					.resolveStageTable(objectConfiguration, type, srcConn);

			this.setRelatedConfiguration(processedRecordStageTable);

			EtlException etlDefaultException = obj.getEtlDefaultEtlException();

			this.status = EtlLoadStatus.NOT_LOADED;

			if (obj.hasDestinationRecordsRecursively()) {
				status = obj.getLoadStatus();
			}

			for (Field f : obj.getFields()) {

				try {
					this.setFieldValue(f.getName(), f.getValue());
				} catch (MissingFieldException e) {
				}
			}

			EtlStageAreaObject existing = EtlStageAreaObjectDAO.getByUniqueKeys(this, srcConn);

			int retry = -1;

			if (existing != null) {
				retry = (int) existing.getField("retry_count").getValue();
			}

			this.setFieldValue("retry_count", retry++);
			this.setFieldValue("processing_status", this.status.name());
			this.setFieldValue("processing_date", DateAndTimeUtilities.getCurrentSystemDate(srcConn));
			this.setFieldValue("processing_error",
					etlDefaultException != null
							? utilities.garantirXCaracteres(etlDefaultException.getLocalizedMessage(), 65534)
							: null);

		} else {
			throw new EtlExceptionImpl("Unsupported stage type (" + type + ")");
		}

		if (alreadyExistOnDB()) {
			String condition = "stage_record_id = ?";
			Object[] params = { this.getObjectId().asSimpleValue() };

			this.keyInfo = EtlStageAreaObjectDAO.getAll(keyInfoTabConf, condition, params, srcConn);
		}

		boolean loadKey = utilities.listHasNoElement(this.keyInfo)
				&& (this.isSrc() || (isDst() && isLoadedSuccessifuly()));

		if (loadKey) {
			this.generateUniqueKeyInfoRecord(keyInfoTabConf);
		} else {

			// We intencionaly null the keyInfo as we do want them to be stored again
			this.keyInfo = null;
		}
	}

	public EtlStageTableType getType() {
		return type;
	}

	public boolean isLoadedSuccessifuly() {
		return this.status.isFullLoaded();
	}

	public boolean isSrc() {
		return this.type.isSrc();
	}

	public boolean isDst() {
		return this.type.isDst();
	}

	public Boolean keyInfoAlreadyExistsOnDb() {
		return keyInfoAlreadyExistsOnDb;
	}

	public void setKeyInfoAlreadyExistsOnDb(Boolean keyInfoExistsOnDb) {
		this.keyInfoAlreadyExistsOnDb = keyInfoExistsOnDb;
	}

	public boolean alreadyExistOnDB() {
		return this.alreadyExistOnDB;
	}

	public void setAlreadyExistOnDB(Boolean alreadyExistOnDB) {
		this.alreadyExistOnDB = alreadyExistOnDB;
	}

	private static EtlStageAreaObject generate(EtlStageAreaObject srcStageInfoObject,
			EtlDatabaseObject relatedEtlObject, EtlStageTableType type, Connection srcConn) throws DBException {

		return new EtlStageAreaObject(srcStageInfoObject, relatedEtlObject, type, srcConn);
	}

	public static EtlStageAreaObject generateSrc(EtlDatabaseObject relatedEtlObject, Connection srcConn)
			throws DBException {

		EtlObjectConfiguration tabConf = relatedEtlObject.getRelatedConfiguration();

		if (tabConf.hasPK()) {
			return generate(null, relatedEtlObject, EtlStageTableType.SRC, srcConn);
		} else {
			return null;
		}
	}

	public static EtlStageAreaObject generateProcessedRecord(EtlDatabaseObject relatedEtlObject, Connection srcConn)
			throws DBException {

		EtlObjectConfiguration tabConf = relatedEtlObject.getRelatedConfiguration();

		if (tabConf.hasPK()) {
			return generate(null, relatedEtlObject, EtlStageTableType.PROCESSED_RECORD, srcConn);
		} else {
			return null;
		}
	}

	public static EtlStageAreaObject generateSrcStageRecord(EtlDatabaseObject relatedEtlObject, Connection srcConn)
			throws DBException {

		EtlObjectConfiguration tabConf = relatedEtlObject.getRelatedConfiguration();

		if (tabConf.hasPK()) {
			return generate(null, relatedEtlObject, EtlStageTableType.SRC, srcConn);
		} else {
			return null;
		}
	}

	public static List<EtlStageAreaObject> generateDst(EtlStageAreaObject srcStageInfoObject,
			List<EtlDatabaseObject> relatedEtlObject, Connection srcConn) throws DBException {

		List<EtlStageAreaObject> recs = new ArrayList<>();

		if (utilities.listHasElement(relatedEtlObject)) {

			for (EtlDatabaseObject etlObject : relatedEtlObject) {
				EtlObjectConfiguration tabConf = etlObject.getRelatedConfiguration();

				if (tabConf.hasPK()) {
					recs.add(generate(srcStageInfoObject, etlObject, EtlStageTableType.DST, srcConn));
				}
			}
		}

		return recs;
	}

	@Override
	public EtlConfigurationTableConf getRelatedConfiguration() {
		return (EtlConfigurationTableConf) super.getRelatedConfiguration();
	}

	public List<EtlDatabaseObject> getKeyInfo() {
		return keyInfo;
	}

	public void setKeyInfo(List<EtlDatabaseObject> keyInfo) {
		this.keyInfo = keyInfo;
	}

	private void addKeyInfo(EtlDatabaseObject uniqueKeyInfoRecord) {

		if (getKeyInfo() == null) {
			setKeyInfo(new ArrayList<>());
		}

		getKeyInfo().add(uniqueKeyInfoRecord);
	}

	private List<UniqueKeyInfo> getAllKeys() {
		EtlDatabaseObject etlObject = this.relatedEtlObject;

		List<UniqueKeyInfo> allKeys = new ArrayList<>();

		EtlObjectConfiguration objectConfiguration = etlObject.getRelatedConfiguration();

		etlObject.loadObjectIdData(objectConfiguration);

		if (objectConfiguration.hasPK()) {
			// We want to preserve the UK name
			UniqueKeyInfo uk = new UniqueKeyInfo();
			uk.copy(etlObject.getObjectId());
			uk.setKeyName(objectConfiguration.getPrimaryKey().getKeyName());

			allKeys.add(uk);
		}

		etlObject.loadUniqueKeyValues();

		if (etlObject.hasUniqueKeys()) {
			// We want to preserve the key name
			allKeys.addAll(
					UniqueKeyInfo.cloneAllWithKeyNameAndLoadValues(objectConfiguration.getUniqueKeys(), etlObject));
		}

		return allKeys;
	}

	public EtlDatabaseObject getRelatedEtlObject() {
		return this.relatedEtlObject;
	}

	public EtlConfigurationTableConf getStageTableConfiguration() {
		return getRelatedConfiguration();
	}

	/** @deprecated use {@link #getStageTableConfiguration()} instead. */
	@Deprecated
	public EtlConfigurationTableConf getRelatedEtlTableConf() {
		return getStageTableConfiguration();
	}

	public EtlObjectConfiguration getRelatedEtlObjectConfiguration() {
		return this.getRelatedEtlObject().getRelatedConfiguration();
	}

	private void generateUniqueKeyInfoRecord(EtlConfigurationTableConf tabConf) throws DBException {
		List<UniqueKeyInfo> allKeys = new ArrayList<>();

		EtlObjectConfiguration objectConfiguration = getRelatedEtlObjectConfiguration();
		UniqueKeyInfo pk = objectConfiguration.getPrimaryKey();

		if (pk != null) {
			UniqueKeyInfo stagePk = new UniqueKeyInfo();
			stagePk.copy(pk);
			stagePk.setKeyName("pk");

			allKeys.add(stagePk);
		}

		if (this.getRelatedEtlObject().hasUniqueKeys()) {
			allKeys.addAll(objectConfiguration.getUniqueKeys());
		}

		for (UniqueKeyInfo uKey : UniqueKeyInfo.cloneAllWithKeyNameAndLoadValues(allKeys, this.getRelatedEtlObject())) {

			for (Key key : uKey.getFields()) {

				if (key.hasValue()) {
					EtlStageAreaObject obj = new EtlStageAreaObject(this, relatedEtlObject, type, tabConf, null, null);

					obj.setFieldValue("key_name", uKey.getKeyName());
					obj.setFieldValue("column_name", key.getName());
					obj.setFieldValue("key_value", key.getValue());

					this.addKeyInfo(obj);
				}
			}

		}
	}

	public void loadIdToChilds() {
		if (this.getKeyInfo() == null)
			return;

		for (EtlDatabaseObject obj : this.getKeyInfo()) {
			obj.setFieldValue("stage_record_id", this.getFieldValue("id"));
		}
	}

	public static List<EtlDatabaseObject> collectAllKeyInfo(List<EtlStageAreaObject> stageInfo) {

		if (utilities.listHasElement(stageInfo)) {

			List<EtlDatabaseObject> collected = new ArrayList<>(stageInfo.size());

			for (EtlStageAreaObject sti : stageInfo) {

				if (sti.getKeyInfo() != null && sti.hasParentKey()) {
					collected.addAll(sti.getKeyInfo());
				}
			}

			return collected;
		} else
			return new ArrayList<>();
	}

	private boolean hasParentKey() {

		if (!this.getRelatedConfiguration().isSrcStageTable()) {
			return getFieldValue("stage_record_id") != null;
		} else {
			return true;
		}
	}

	public static void loadParentIdToChild(List<EtlStageAreaObject> stageAreaObjects) {

		for (EtlStageAreaObject stageObject : stageAreaObjects) {
			stageObject.loadIdToChilds();
		}
	}

	public boolean hasNoError() {
		return isLoadedSuccessifuly() || this.status.isNotLoaded();
	}
}
