package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.FieldMappingResolutionStrategy;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectLoaderHelper;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class FastEtlTransformingTarget implements EtlTransformTarget {

	private List<EtlDataSource> allAvaliableDataSource;
	private String condition;
	private EtlConfiguration relatedEtlConf;

	private FastEtlTransformingTarget(EtlConfiguration relatedEtlConf, List<EtlDataSource> allAvaliableDataSource) {
		this.relatedEtlConf = relatedEtlConf;
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

	public static FastEtlTransformingTarget fastCreate(EtlConfiguration relatedEtlCOnf,
			List<EtlDatabaseObject> avaliableSrcObjects, Connection conn) throws DBException {

		if (relatedEtlCOnf == null) {
			throw new ForbiddenOperationException("relatedEtlConf cannot be null");
		}

		List<EtlDataSource> ds = null;

		if (utilities.listHasElement(avaliableSrcObjects)) {

			/*
			 * if (utilities.listHasNoElement(avaliableSrcObjects)) { throw new
			 * ForbiddenOperationException("You cannot create a FastEtlTransformingTarget withount srcObjects!"
			 * ); }
			 */

			ds = new ArrayList<>();

			for (EtlDatabaseObject obj : avaliableSrcObjects) {
				if (obj.getRelatedConfiguration() instanceof EtlDataSource) {
					ds.add((EtlDataSource) obj.getRelatedConfiguration());
				}
			}
		}

		return new FastEtlTransformingTarget(relatedEtlCOnf, ds);
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.relatedEtlConf;
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

	public void setRelatedEtlConfig(EtlConfiguration relatedEtlConf) {
		this.relatedEtlConf = relatedEtlConf;
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

	@Override
	public EtlDatabaseObject getTargetDefaultObject(Connection srcConn, Connection dstConn) throws DBException {
		return null;
	}

	@Override
	public ActionOnEtlIssue unmappedFieldBehavior() {
		return ActionOnEtlIssue.IGNORE;
	}

	@Override
	public List<EtlFragmentInclude> getInclude() {
		return null;
	}

	@Override
	public FieldMappingResolutionStrategy mappingResolutionStrategy() {
		return null;
	}

	@Override
	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	public List<String> getExcludedFieldsFromObjectDesc() {
		return null;
	}
}
