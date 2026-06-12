package org.openmrs.module.epts.etl.conf.datasource.json;

import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;

public abstract class JsonEtlAbctractObject extends AbstractEtlDataConfiguration {
	
	protected final Object LOCK = new Object();
	
	private String table_name;
	
	private List<JsonEtlColumnInfo> columns;
	
	private EtlDataConfiguration parent;
	
	private volatile Boolean initialized;
	
	public JsonEtlAbctractObject() {
		
	}
	
	public JsonEtlAbctractObject(JsonDataSource parent) {
		setParent(parent);
	}
	
	private boolean hasColumns() {
		return utilities.listHasElement(this.columns);
	}
	
	public Boolean initialized() {
		return isTrue(this.initialized);
	}
	
	public void init() {
		if (initialized()) {
			return;
		}
		
		synchronized (LOCK) {
			
			if (initialized()) {
				return;
			}
			
			if (hasColumns()) {
				for (JsonEtlColumnInfo c : this.columns) {
					c.init();
				}
			}
			
			initialized = true;
		}
	}
	
	public String getTable_name() {
		return table_name;
	}
	
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	
	public List<JsonEtlColumnInfo> getColumns() {
		return columns;
	}
	
	public void setColumns(List<JsonEtlColumnInfo> columns) {
		this.columns = columns;
	}
	
	@Override
	public EtlDataConfiguration getParentConf() {
		return this.parent;
	}
	
	public void setParent(EtlDataConfiguration parent) {
		this.parent = parent;
	}
	
	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}
	
	public List<Field> columnsAsEtlFields() {
		return JsonEtlColumnInfo.toEtlField(this.columns);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
