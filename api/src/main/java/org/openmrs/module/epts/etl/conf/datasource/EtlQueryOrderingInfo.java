package org.openmrs.module.epts.etl.conf.datasource;

import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.SqlOrderingType;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public class EtlQueryOrderingInfo extends AbstractEtlDataConfiguration {
	
	private TableConfiguration tableConf;
	
	private SqlOrderingType type;
	
	private List<String> fields;
	
	public EtlQueryOrderingInfo() {
		
	}
	
	public EtlQueryOrderingInfo(List<String> fields, SqlOrderingType type) {
		this.fields = fields;
		this.type = type;
	}
	
	public SqlOrderingType getType() {
		return type;
	}
	
	public void setType(SqlOrderingType type) {
		this.type = type;
	}
	
	public List<String> getFields() {
		return fields;
	}
	
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	public String asFieldList() {
		String str = "";
		
		for (String f : this.getFields()) {
			str += (utilities.stringHasValue(str) ? "," : "") + f;
		}
		
		return str;
	}
	
	@Override
	public EtlDataConfiguration getParentConf() {
		return this.tableConf;
	}
	
	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}
	
}
