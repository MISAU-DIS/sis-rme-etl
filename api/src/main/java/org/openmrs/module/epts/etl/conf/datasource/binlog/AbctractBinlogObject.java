package org.openmrs.module.epts.etl.conf.datasource.binlog;

import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;

public abstract class AbctractBinlogObject extends AbstractEtlDataConfiguration {
	
	protected final Object LOCK = new Object();
	
	private String table_name;
	
	private List<BinLogTableColumn> columns;
	
	private EtlDataConfiguration parent;
	
	private volatile Boolean initialized;
	
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
				for (BinLogTableColumn c : this.columns) {
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
	
	public List<BinLogTableColumn> getColumns() {
		return columns;
	}
	
	public void setColumns(List<BinLogTableColumn> columns) {
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
		return BinLogTableColumn.toEtlField(this.columns);
	}
	
}
