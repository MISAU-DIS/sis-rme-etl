package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
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

public class EtlChildItemConfiguration extends EtlItemConfiguration implements EtlTransformTarget {
	
	private final Object LOCK = new Object();
	
	private EtlItemConfiguration parentItemConf;
	
	private String srcObjectCondition;
	
	private DstConf relatedParentDstConf;
	
	private String relatedParentDstConfName;
	
	private List<String> prefferredDataSource;
	
	private List<EtlDataSource> allAvaliableDataSource;
	
	private List<EtlDataSource> allNotPrefferredDataSource;
	
	private List<EtlDataSource> allPrefferredDataSource;
	
	private Boolean loadedDataSourceInfo;
	
	public String getSrcObjectCondition() {
		return srcObjectCondition;
	}
	
	public void setSrcObjectCondition(String srcObjectCondition) {
		this.srcObjectCondition = srcObjectCondition;
	}
	
	public String getRelatedParentDstConfName() {
		return relatedParentDstConfName;
	}
	
	public void setRelatedParentDstConfName(String relatedParentDstConfName) {
		this.relatedParentDstConfName = relatedParentDstConfName;
	}
	
	public DstConf getRelatedParentDstConf() {
		return relatedParentDstConf;
	}
	
	public void setRelatedParentDstConf(DstConf relatedParentDstConf) {
		this.relatedParentDstConf = relatedParentDstConf;
	}
	
	public EtlItemConfiguration getParentItemConf() {
		return parentItemConf;
	}
	
	public void setParentItemConf(EtlItemConfiguration parentItemConf) {
		this.parentItemConf = parentItemConf;
	}
	
	@Override
	public Map<String, Object> retrieveAllAvailableTemplateParameters() {
		super.retrieveAllAvailableTemplateParameters();
		
		Map<String, Object> allParameters = new HashMap<>();
		
		if (hasParentItemConf()) {
			Map<String, Object> parentParameters = this.getParentItemConf().retrieveAllAvailableTemplateParameters();
			
			if (parentParameters != null && !parentParameters.isEmpty()) {
				allParameters.putAll(parentParameters);
			}
		}
		
		return allParameters;
	}
	
	@Override
	public List<EtlDataSource> collectAllAvaliableDataSources(Connection conn) {
		List<EtlDataSource> ds = super.collectAllAvaliableDataSources(conn);
		
		if (ds == null) {
			ds = new ArrayList<>();
		}
		
		if (this.hasParentItemConf()) {
			DstConf parentDstConf = this.getParentItemConf().findDstConf(this.relatedParentDstConfName);
			
			ds.add(parentDstConf);
			
			ds.addAll(parentDstConf.getAllAvaliableDataSource());
		}
		
		return ds;
	}
	
	@Override
	protected String putOperationElementoOnCode(String codeElements) {
		
		codeElements += "_on_" + getRelatedEtlConf().generateProcessId()
		        + (this.hasParentItemConf() ? "_within_" + this.getParentItemConf().getShortCode() : "");
		
		return codeElements;
		
	}
	
	public Boolean hasParentItemConf() {
		return this.parentItemConf != null;
	}
	
	@Override
	public void fullLoad(EtlOperationConfig operationConfig, Connection srcConn, Connection dstConn) throws DBException {
		
		super.fullLoad(operationConfig, srcConn, dstConn);
		
		if (this.hasParentItemConf() && this.getRelatedParentDstConf() == null) {
			
			if (!utilities.stringHasValue(this.getRelatedParentDstConfName())) {
				if (utilities.listHasExactlyOneElement(this.getParentItemConf().getDstConf())) {
					this.setRelatedParentDstConfName(this.getParentItemConf().getDstConf().get(0).getName());
				} else {
					throw new ForbiddenOperationException("The relatedParentDstConfName was not defined for the conf "
					        + this.getParentItemConf().getConfigCode());
				}
			}
			
			this.setRelatedParentDstConf(this.getParentItemConf().findDstConf(this.getRelatedParentDstConfName()));
		}
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
		
		if (isLoadedDataSourceInfo())
			return;
		
		synchronized (LOCK) {
			if (isLoadedDataSourceInfo())
				return;
			
			this.addToAvaliableDataSource(this.getParentItemConf().getSrcConf());
			
			this.addAllToAvaliableDataSource(this.getParentItemConf().getSrcConf().getAvaliableExtraDataSource());
			
			this.addAllToAvaliableDataSource(this.getSrcConf().getParentItemConf().collectAllAvaliableDataSources(conn));
			
			this.setAllNotPrefferredDataSource(this.getAllAvaliableDataSource());
			
			this.loadedDataSourceInfo = true;
		}
	}
	
	public static EtlChildItemConfiguration fastCreate(AbstractTableConfiguration tableConfig, Connection conn)
	        throws DBException {
		
		return fastCreate(tableConfig, false, conn);
	}
	
	public static EtlChildItemConfiguration fastCreate(AbstractTableConfiguration tableConfig,
	        boolean useMainEtlTableAsSrcConfIfNotExists, Connection conn) throws DBException {
		
		EtlChildItemConfiguration etl = new EtlChildItemConfiguration();
		
		SrcConf src = SrcConf.fastCreate(tableConfig, etl, conn);
		
		etl.setSrcConf(src);
		
		etl.setRelatedEtlConfig(tableConfig.getRelatedEtlConf());
		
		return etl;
	}
	
	public static EtlChildItemConfiguration fastCreate(String configCode) {
		EtlChildItemConfiguration etl = new EtlChildItemConfiguration();
		
		etl.setConfigCode(configCode);
		
		return etl;
	}
	
	public List<String> getPrefferredDataSource() {
		return prefferredDataSource;
	}
	
	public void setPrefferredDataSource(List<String> prefferredDataSource) {
		this.prefferredDataSource = prefferredDataSource;
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
		return allNotPrefferredDataSource;
	}
	
	@Override
	public void setAllNotPrefferredDataSource(List<EtlDataSource> allNotPrefferredDataSource) {
		this.allNotPrefferredDataSource = allNotPrefferredDataSource;
	}
	
	@Override
	public List<EtlDataSource> getAllPrefferredDataSource() {
		return allPrefferredDataSource;
	}
	
	@Override
	public void setAllPrefferredDataSource(List<EtlDataSource> allPrefferredDataSource) {
		this.allPrefferredDataSource = allPrefferredDataSource;
	}
	
	@Override
	public Boolean isLoadedDataSourceInfo() {
		return isTrue(loadedDataSourceInfo);
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
	public EtlDatabaseObject getTargetDefaultObject(Connection srcConn, Connection dstConn) throws DBException {
		return null;
	}
	
	@Override
	public ActionOnEtlIssue onMissingMapping() {
		return ActionOnEtlIssue.IGNORE;
	}
	
}
