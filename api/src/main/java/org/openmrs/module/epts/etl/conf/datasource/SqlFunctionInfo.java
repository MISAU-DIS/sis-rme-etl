package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;

import org.openmrs.module.epts.etl.conf.interfaces.SqlFunctionType;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class SqlFunctionInfo {
	
	private TableConfiguration mainTable;
	
	private SqlFunctionType type;
	
	private String aliasName;
	
	public SqlFunctionInfo() {
		
	}
	
	public SqlFunctionInfo(SqlFunctionType type, String aliasName) {
		this.type = type;
		this.aliasName = aliasName;
	}
	
	public TableConfiguration getMainTable() {
		return mainTable;
	}
	
	public void setMainTable(TableConfiguration mainTable) {
		this.mainTable = mainTable;
	}
	
	public SqlFunctionType getType() {
		return type;
	}
	
	public void setType(SqlFunctionType type) {
		this.type = type;
	}
	
	public String getAliasName() {
		return aliasName;
	}
	
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
	@Override
	public String toString() {
		String toString = this.type.toString();
		
		toString += getAliasName() != null ? ", alias: " + this.getAliasName() : "";
		
		toString += getMainTable() != null ? ", mainTable: " + this.getMainTable().getTableName() : "";
		
		return toString;
	}
	
	public boolean isCountFunction() {
		return this.getType().isCount();
	}
	
	public IntervalExtremeRecord detemineLimits(Engine<? extends EtlDatabaseObject> engine, Connection conn)
	        throws DBException {
		if (!this.getMainTable().isFullLoaded()) {
			this.getMainTable().fullLoad(conn);
		}
		
		return new IntervalExtremeRecord(this.getMainTable().getMinRecordId(engine, conn),
		        this.getMainTable().getMaxRecordId(engine, conn));
		
	}
	
}
