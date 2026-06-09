package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.datasource.json.JsonEtlMainObject;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlSrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.etl.processor.transformer.FieldTransformingInfo;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectLoaderHelper;
import org.openmrs.module.epts.etl.model.pojo.generic.JsonEtlObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

public class JsonDataSource extends AbstractEtlDataConfiguration implements EtlAdditionalDataSource, EtlSrcConf, EtlTransformTarget {
	
	private final Object LOCK = new Object();
	
	private String name;
	
	private String payload;
	
	private FieldsMapping payloadFieldsMap;
	
	private SrcConf relatedSrcConf;
	
	private volatile Boolean fullLoaded;
	
	private JsonMainOutputDataSource outputDataSource;
	
	private List<String> prefferredDataSource;
	
	private List<EtlDataSource> allAvaliableDataSource;
	
	private List<EtlDataSource> allNotPrefferredDataSource;
	
	private List<EtlDataSource> allPrefferredDataSource;
	
	private Boolean loadedDataSourceInfo;
	
	public JsonDataSource() {
		this.loadedDataSourceInfo = false;
	}
	
	public JsonMainOutputDataSource getOutputDataSource() {
		return outputDataSource;
	}
	
	public void setOutputDataSource(JsonMainOutputDataSource outputDataSource) {
		this.outputDataSource = outputDataSource;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setFullLoaded(Boolean fullLoaded) {
		this.fullLoaded = fullLoaded;
	}
	
	public String getPayload() {
		return payload;
	}
	
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public PreparedQuery getDefaultPreparedQuery() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public void setDefaultPreparedQuery(PreparedQuery defaultPreparedQuery) {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public String getQuery() {
		throw new ForbiddenOperationException("Forbiden Method!");
	}
	
	@Override
	public void setParentConf(EtlDataConfiguration relatedParent) {
		if (relatedParent instanceof SrcConf) {
			this.relatedSrcConf = (SrcConf) relatedParent;
		} else {
			throw new ForbiddenOperationException("The parent of JsonDataSource must be a SrcConf!");
		}
	}
	
	@Override
	public void tryToLoadSchemaInfo(EtlDatabaseObject schemaInfoSrc, Connection conn)
	        throws DBException, ForbiddenOperationException, DatabaseResourceDoesNotExists {
	}
	
	@Override
	public Boolean isFullLoaded() {
		return isTrue(this.fullLoaded);
	}
	
	@Override
	public void fullLoad() throws DBException {
		OpenConnection mainConn = getRelatedEtlConf().getSrcConnInfo().openConnection(this);
		
		try {
			fullLoad(mainConn);
		}
		finally {
			mainConn.finalizeConnection(this);
		}
	}
	
	private void ensurePayloadFieldInfoIsLoaded(Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		this.payloadFieldsMap = FieldsMapping.fastCreate(this.payload, "payload", this, conn);
	}
	
	@Override
	public void fullLoad(Connection conn) throws DBException {
		if (this.isFullLoaded()) {
			return;
		}
		
		synchronized (LOCK) {
			if (this.isFullLoaded()) {
				return;
			}
			
			if (!utilities.stringHasValue(this.getName())) {
				throw new EtlConfException("Name is required for JsonDataSOurce.");
			}
			
			if (this.getOutputDataSource() == null) {
				throw new EtlConfException("No outputDataSource was defined for jsonDataSource" + this.getName());
			}
			
			this.getOutputDataSource().setParent(this);
			
			loadDataSourceInfo(conn);
			
			ensurePayloadFieldInfoIsLoaded(conn);
			
			this.setFullLoaded(true);
		}
	}
	
	@Override
	public void init(EtlDataConfiguration relatedParent, EtlDatabaseObject etlSchemaObject, Connection srcConn,
	        Connection dstConn) throws DBException {
		
		EtlAdditionalDataSource.super.init(relatedParent, etlSchemaObject, srcConn, dstConn);
		
		if (this.getOutputDataSource() != null) {
			this.getOutputDataSource().init(this, etlSchemaObject, srcConn, dstConn);
		}
	}
	
	@Override
	public TableConfiguration findFullConfiguredConfInAllRelatedTable(String fullTableName,
	        List<Integer> alreadyCheckedObjects) {
		
		return null;
	}
	
	@Override
	public String generateClassName() {
		return null;
	}
	
	@Override
	public SrcConf getParentConf() {
		return this.relatedSrcConf;
	}
	
	@Override
	public String getObjectName() {
		return this.getName();
	}
	
	@Override
	public <T extends Field> List<T> getFields() {
		return this.outputDataSource.getFields();
	}
	
	@Override
	public UniqueKeyInfo getPrimaryKey() {
		return null;
	}
	
	@Override
	public String getSharePkWith() {
		return null;
	}
	
	@Override
	public Boolean hasPK() {
		return null;
	}
	
	@Override
	public Boolean hasPK(Connection conn) throws DBException {
		return null;
	}
	
	@Override
	public Boolean isMetadata() {
		return false;
	}
	
	@Override
	public DBConnectionInfo getRelatedConnInfo() {
		return this.getSrcConf().getRelatedConnInfo();
	}
	
	@Override
	public void setSyncRecordClass(Class<? extends EtlDatabaseObject> syncRecordClass) {
	}
	
	@Override
	public Boolean isDestinationInstallationType() {
		return null;
	}
	
	@Override
	public void generateRecordClass(DBConnectionInfo connInfo, Boolean fullClass) {
	}
	
	@Override
	public List<ParentTable> getParentRefInfo() {
		return null;
	}
	
	@Override
	public List<ChildTable> getChildRefInfo() {
		return null;
	}
	
	@Override
	public DatabaseObjectLoaderHelper getLoadHealper() {
		return null;
	}
	
	@Override
	public Boolean isMustLoadChildrenInfo() {
		return null;
	}
	
	@Override
	public String getAlias() {
		return this.getName();
	}
	
	@Override
	public String generateSelectFromQuery() {
		return null;
	}
	
	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}
	
	@Override
	public String getSrcObjectCondition() {
		return null;
	}
	
	@Override
	public void loadDataSourceInfo(Connection conn) throws DBException {
		if (isLoadedDataSourceInfo())
			return;
		
		synchronized (LOCK) {
			if (isLoadedDataSourceInfo())
				return;
			
			this.addToAvaliableDataSource(this.getSrcConf());
			this.addAllToAvaliableDataSource(this.getSrcConf().getAvaliableExtraDataSource());
			
			if (this.getSrcConf().hasParentItemConf()) {
				this.addAllToAvaliableDataSource(this.getSrcConf().getParentItemConf().collectAllAvaliableDataSources(conn));
			}
			
			this.setAllNotPrefferredDataSource(this.getAllAvaliableDataSource());
			
			this.loadedDataSourceInfo = true;
		}
	}
	
	@Override
	public Boolean isLoadedDataSourceInfo() {
		return isTrue(this.loadedDataSourceInfo);
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
	public ActionOnEtlIssue onMultipleDataSourceForSameMapping() {
		return ActionOnEtlIssue.ABORT_PROCESS;
	}
	
	@Override
	public SrcConf getSrcConf() {
		return this.relatedSrcConf;
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
	public SrcConf getRelatedSrcConf() {
		return this.relatedSrcConf;
	}
	
	@Override
	public void setRelatedSrcConf(SrcConf relatedSrcConf) {
		this.relatedSrcConf = relatedSrcConf;
		
		this.setRelatedEtlConfig(this.getRelatedSrcConf().getRelatedEtlConf());
	}
	
	@Override
	public EtlDatabaseObject loadRelatedSrcObject(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, Connection conn) throws DBException {
		
		FieldTransformingInfo fi = payloadFieldsMap.getTransformerInstance().transform(processor, srcObject, dstObject,
		    avaliableSrcObjects, payloadFieldsMap, conn, conn);
		
		String json = fi.getTransformedValue().toString();
		
		JsonEtlObject mainObj = JsonEtlMainObject.convert(this, json, conn, conn);
		
		return mainObj;
	}
	
	@Override
	public Boolean isRequired() {
		return true;
	}
	
	@Override
	public Boolean allowMultipleSrcObjectsForLoading() {
		return false;
	}
	
	@Override
	public EtlDatabaseObject getTargetDefaultObject(Connection srcConn, Connection dstConn) throws DBException {
		throw new ForbiddenOperationException("Default Target Object is not Allowed!");
	}
	
	@Override
	public ActionOnEtlIssue onMissingMapping() {
		return ActionOnEtlIssue.ABORT_PROCESS;
	}
}
