package org.openmrs.module.epts.etl.databasepreparation.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.common.model.EtlStageRecordVO;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.ParentTableImpl;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ConflictResolutionType;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.ParentNotYetMigratedException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.EtlDatabaseObjectUniqueKeyInfo;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.Oid;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.InconsistentStateException;

public class DatabasePreparationRecord implements EtlDatabaseObject {
	
	private EtlItemConfiguration itemConf;
	
	public DatabasePreparationRecord(EtlItemConfiguration itemConf) {
		this.itemConf = itemConf;
	}
	
	public EtlItemConfiguration getItemConf() {
		return itemConf;
	}
	
	@Override
	public void load(ResultSet rs) throws SQLException {
	}
	
	@Override
	public void loadObjectIdData() throws ForbiddenOperationException {
	}
	
	@Override
	public void loadObjectIdData(TableConfiguration tabConf) {
	}
	
	@Override
	public boolean isExcluded() {
		return false;
	}
	
	@Override
	public void setExcluded(boolean excluded) {
	}
	
	@Override
	public List<Field> getFields() {
		return null;
	}
	
	@Override
	public void setFields(List<Field> fields) {
	}
	
	@Override
	public void setFieldValue(String fieldName, Object value) {
	}
	
	@Override
	public void refreshLastSyncDateOnOrigin(TableConfiguration tableConfiguration, String recordOriginLocationCode,
	        Connection conn) {
	}
	
	@Override
	public void refreshLastSyncDateOnDestination(TableConfiguration tableConfiguration, String recordOriginLocationCode,
	        Connection conn) {
	}
	
	@Override
	public List<EtlDatabaseObject> getDestinationObjects() {
		return null;
	}
	
	@Override
	public void setDestinationObjects(List<EtlDatabaseObject> destinationObjects) {
	}
	
	@Override
	public Oid getObjectId() {
		return null;
	}
	
	@Override
	public void setObjectId(Oid objectId) {
	}
	
	@Override
	public List<EtlDatabaseObjectUniqueKeyInfo> getUniqueKeysInfo() {
		return null;
	}
	
	@Override
	public void setUniqueKeysInfo(List<EtlDatabaseObjectUniqueKeyInfo> uniqueKeysInfo) {
	}
	
	@Override
	public List<EtlDatabaseObject> getAuxLoadObject() {
		return null;
	}
	
	@Override
	public void setAuxLoadObject(List<EtlDatabaseObject> auxLoadObjects) {
	}
	
	@Override
	public void loadDestParentInfo(TableConfiguration tableInfo, String recordOriginLocationCode, Connection conn)
	        throws ParentNotYetMigratedException, DBException {
	}
	
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		return null;
	}
	
	@Override
	public String getInsertSQLWithoutObjectId() {
		return null;
	}
	
	@Override
	public Object[] getInsertParamsWithObjectId() {
		return null;
	}
	
	@Override
	public String getInsertSQLWithObjectId() {
		return null;
	}
	
	@Override
	public String getUpdateSQL() {
		return null;
	}
	
	@Override
	public Object[] getUpdateParams() {
		return null;
	}
	
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return null;
	}
	
	@Override
	public String generateInsertValuesWithObjectId() {
		return null;
	}
	
	@Override
	public void setInsertSQLQuestionMarksWithObjectId(String insertQuestionMarks) {
	}
	
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return null;
	}
	
	@Override
	public void setInsertSQLQuestionMarksWithoutObjectId(String insertQuestionMarks) {
	}
	
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return null;
	}
	
	@Override
	public void save(TableConfiguration syncTableInfo, ConflictResolutionType onConflict, Connection conn)
	        throws DBException {
	}
	
	@Override
	public void save(TableConfiguration syncTableInfo, Connection conn) throws DBException {
	}
	
	@Override
	public void update(TableConfiguration syncTableInfo, Connection conn) throws DBException {
	}
	
	@Override
	public String getUuid() {
		return null;
	}
	
	@Override
	public void setUuid(String uuid) {
	}
	
	@Override
	public boolean hasParents() {
		return false;
	}
	
	@Override
	public Object getParentValue(ParentTable refInfo) {
		return null;
	}
	
	@Override
	public String generateTableName() {
		return null;
	}
	
	@Override
	public String generateFullFilledUpdateSql() {
		return null;
	}
	
	@Override
	public EtlDatabaseObject getSharedPkObj() {
		return null;
	}
	
	@Override
	public void setSharedPkObj(EtlDatabaseObject sharedPkObj) {
		
	}
	
	@Override
	public EtlInfo getEtlInfo() {
		return null;
	}
	
	@Override
	public void setEtlInfo(EtlInfo info) {
		
	}
	
	@Override
	public void consolidateData(TableConfiguration tableInfo, Connection conn)
	        throws InconsistentStateException, DBException {
		
	}
	
	@Override
	public void resolveInconsistence(TableConfiguration tableInfo, Connection conn)
	        throws InconsistentStateException, DBException {
		
	}
	
	@Override
	public EtlStageRecordVO retrieveRelatedSyncInfo(TableConfiguration tableInfo, String recordOriginLocationCode,
	        Connection conn) throws DBException {
		return null;
	}
	
	@Override
	public EtlDatabaseObject retrieveParentInDestination(Integer parentId, String recordOriginLocationCode,
	        TableConfiguration parentTableConfiguration, boolean ignorable, Connection conn)
	        throws ParentNotYetMigratedException, DBException {
		return null;
	}
	
	@Override
	public EtlStageRecordVO getRelatedSyncInfo() {
		return null;
	}
	
	@Override
	public void setRelatedSyncInfo(EtlStageRecordVO relatedSyncInfo) {
		
	}
	
	@Override
	public String generateMissingInfo(Map<ParentTableImpl, Integer> missingParents) {
		return null;
	}
	
	@Override
	public void remove(Connection conn) throws DBException {
		
	}
	
	@Override
	public Map<ParentTableImpl, Integer> loadMissingParents(TableConfiguration tableInfo, Connection conn)
	        throws DBException {
		return null;
	}
	
	@Override
	public void removeDueInconsistency(TableConfiguration syncTableInfo, Map<ParentTableImpl, Integer> missingParents,
	        Connection conn) throws DBException {
		
	}
	
	@Override
	public void changeParentValue(ParentTable refInfo, EtlDatabaseObject newParent) {
		
	}
	
	@Override
	public void setParentToNull(ParentTableImpl refInfo) {
		
	}
	
	@Override
	public void changeObjectId(TableConfiguration abstractTableConfiguration, Connection conn) throws DBException {
		
	}
	
	@Override
	public void changeParentForAllChildren(EtlDatabaseObject newParent, TableConfiguration syncTableInfo, Connection conn)
	        throws DBException {
		
	}
	
	@Override
	public Date getDateChanged() {
		return null;
	}
	
	@Override
	public Date getDateVoided() {
		return null;
	}
	
	@Override
	public Date getDateCreated() {
		return null;
	}
	
	@Override
	public boolean hasExactilyTheSameDataWith(EtlDatabaseObject srcObj) {
		return false;
	}
	
	@Override
	public void fastCreateSimpleNumericKey(long i) {
		
	}
	
	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) throws DBException {
		
	}
	
	@Override
	public void copyFrom(EtlDatabaseObject parentRecordInOrigin) {
		
	}
	
	@Override
	public void tryToReplaceFieldWithKey(Key k) {
		
	}
}
