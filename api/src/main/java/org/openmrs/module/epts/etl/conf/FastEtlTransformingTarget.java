package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTranformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectLoaderHelper;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class FastEtlTransformingTarget implements EtlTranformTarget {
	
	private List<EtlDataSource> allAvaliableDataSource;
	
	private FastEtlTransformingTarget(List<EtlDataSource> allAvaliableDataSource) {
		this.allAvaliableDataSource = allAvaliableDataSource;
	}
	
	@Override
	public Map<String, Object> retrieveAllAvailableTemplateParameters() {
		return null;
	}
	
	@Override
	public void fullLoad() throws DBException {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public void fullLoad(Connection conn) throws DBException {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public TableConfiguration findFullConfiguredConfInAllRelatedTable(String fullTableName,
	        List<Integer> alreadyCheckedObjects) {
		
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public String generateClassName() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public String getObjectName() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public <T extends Field> List<T> getFields() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public UniqueKeyInfo getPrimaryKey() {
		return null;
	}
	
	@Override
	public String getSharePkWith() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public Boolean hasPK() {
		throw new ForbiddenOperationException("Forbiden Method!");
		
	}
	
	@Override
	public Boolean hasPK(Connection conn) throws DBException {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public Boolean isMetadata() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public DBConnectionInfo getRelatedConnInfo() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public void setSyncRecordClass(Class<? extends EtlDatabaseObject> syncRecordClass) {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public Boolean isDestinationInstallationType() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public void generateRecordClass(DBConnectionInfo connInfo, Boolean fullClass) {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public List<ParentTable> getParentRefInfo() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public List<ChildTable> getChildRefInfo() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public DatabaseObjectLoaderHelper getLoadHealper() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public Boolean isMustLoadChildrenInfo() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public String getAlias() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public String generateSelectFromQuery() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public void loadDataSourceInfo(Connection conn) throws DBException {
	}
	
	public static FastEtlTransformingTarget fastCreate(List<EtlDatabaseObject> avaliableSrcObjects, Connection conn)
	        throws DBException {
		
		if (utilities.listHasNoElement(avaliableSrcObjects)) {
			throw new ForbiddenOperationException("You cannot create a FastEtlTransformingTarget withount srcObjects!");
		}
		
		List<EtlDataSource> ds = new ArrayList<>();
		
		for (EtlDatabaseObject obj : avaliableSrcObjects) {
			if (obj.getRelatedConfiguration() instanceof EtlDataSource) {
				ds.add((EtlDataSource) obj.getRelatedConfiguration());
			}
		}
		
		return new FastEtlTransformingTarget(ds);
	}
	
	@Override
	public List<EtlDataSource> getAllAvaliableDataSource() {
		return allAvaliableDataSource;
	}
	
	@Override
	public void setAllAvaliableDataSource(List<EtlDataSource> allAvaliableDataSource) {
		this.allAvaliableDataSource = allAvaliableDataSource;
	}
	
	@Override
	public List<EtlDataSource> getAllNotPrefferredDataSource() {
		return null;
	}
	
	@Override
	public void setAllNotPrefferredDataSource(List<EtlDataSource> allNotPrefferredDataSource) {
	}
	
	@Override
	public List<EtlDataSource> getAllPrefferredDataSource() {
		return null;
	}
	
	@Override
	public void setAllPrefferredDataSource(List<EtlDataSource> allPrefferredDataSource) {
	}
	
	@Override
	public Boolean isLoadedDataSourceInfo() {
		return this.allAvaliableDataSource != null;
	}
	
	@Override
	public ActionOnEtlIssue onMultipleDataSourceForSameMapping() {
		return ActionOnEtlIssue.USE_FIRST;
	}
	
	@Override
	public Boolean isIgnoreUnmappedFields() {
		return false;
	}
	
	@Override
	public List<FieldsMapping> getAllMapping() {
		return null;
	}
	
	@Override
	public void setMapping(List<FieldsMapping> mapping) {
	}
	
	@Override
	public void setAllMapping(List<FieldsMapping> allMapping) {
	}
	
	@Override
	public Boolean isAutoIncrementId() {
		return false;
	}
	
	@Override
	public Boolean isFullLoaded() {
		return null;
	}
	
	@Override
	public EtlDataConfiguration getParentConf() {
		return null;
	}
	
	@Override
	public List<DefaultEtlValidator> getValidators() {
		return null;
	}
	
	@Override
	public void setRelatedEtlConfig(EtlConfiguration relatedSyncConfiguration) {
	}
	
	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}
	
	@Override
	public ActionOnEtlIssue getGeneralBehaviourOnEtlException() {
		return null;
	}
	
	@Override
	public EtlTemplateInfo getTemplate() {
		return null;
	}
	
	@Override
	public void setTemplate(EtlTemplateInfo template) {
	}
	
	@Override
	public List<String> getDynamicElements() {
		return null;
	}
	
	@Override
	public List<Extension> getExtension() {
		return null;
	}
	
	@Override
	public void setExtension(List<Extension> extension) {
	}
	
	@Override
	public SrcConf getSrcConf() {
		return null;
	}
	
	@Override
	public String getSrcObjectCondition() {
		return null;
	}
	
}
