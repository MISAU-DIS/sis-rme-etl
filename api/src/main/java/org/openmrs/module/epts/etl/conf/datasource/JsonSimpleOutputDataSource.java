package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.datasource.json.JsonEtlObjectParent;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlSrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectLoaderHelper;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class JsonSimpleOutputDataSource extends AbstractEtlDataConfiguration implements EtlAdditionalDataSource, EtlSrcConf {
	
	private final Object LOCK = new Object();
	
	private String name;
	
	private SrcConf parent;
	
	private List<String> fields;
	
	private List<Field> dataSourceFields;
	
	private String childField;
	
	public String getChildField() {
		return childField;
	}
	
	public void setChildField(String childField) {
		this.childField = childField;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
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
			this.parent = (SrcConf) relatedParent;
		} else {
			throw new ForbiddenOperationException("The parent of JsonOutputDataSource must be a SrcConf!");
		}
	}
	
	@Override
	public void tryToLoadSchemaInfo(EtlDatabaseObject schemaInfoSrc, Connection conn)
	        throws DBException, ForbiddenOperationException, DatabaseResourceDoesNotExists {
	}
	
	@Override
	public Boolean isFullLoaded() {
		return dataSourceFields != null;
	}
	
	@Override
	public void fullLoad() throws DBException {
		fullLoad();
	}
	
	@Override
	public void fullLoad(Connection conn) throws DBException {
		if (isFullLoaded())
			return;
		
		synchronized (LOCK) {
			if (isFullLoaded())
				return;
			
			List<Field> dsFields = new ArrayList<>();
			
			if (!utilities.stringHasValue(this.getName())) {
				throw new EtlConfException("Name is required for outputDatasource.");
			}
			
			if (utilities.listHasNoElement(fields)) {
				throw new EtlConfException("The fields list cannot be null in the Datasource " + this.getName());
			}
			
			for (String fieldName : this.fields) {
				dsFields.add(Field.fastCreateField(fieldName));
			}
			
			this.dataSourceFields = dsFields;
			
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
		return this.parent;
	}
	
	@Override
	public String getObjectName() {
		return this.getName();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Field> List<T> getFields() {
		return (List<T>) this.dataSourceFields;
	}
	
	public void setFields(List<Field> fields) {
		this.dataSourceFields = fields;
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
		return this.getRelatedSrcConf().getRelatedConnInfo();
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
		return false;
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
	public EtlDatabaseObject loadRelatedSrcObject(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, Connection conn) throws DBException {
		
		return null;
	}
	
	@Override
	public Boolean isRequired() {
		return false;
	}
	
	@Override
	public Boolean allowMultipleSrcObjectsForLoading() {
		return false;
	}
	
	@Override
	public SrcConf getRelatedSrcConf() {
		return this.parent;
	}
	
	@Override
	public void setRelatedSrcConf(SrcConf relatedSrcConf) {
		this.parent = relatedSrcConf;
	}
	
	public JsonEtlObjectParent select(List<JsonEtlObjectParent> parents) {
		if (parents == null)
			return null;
		
		for (JsonEtlObjectParent p : parents) {
			if (this.childField.equals(p.getChild_field())) {
				return p;
			}
		}
		
		return null;
	}
}
