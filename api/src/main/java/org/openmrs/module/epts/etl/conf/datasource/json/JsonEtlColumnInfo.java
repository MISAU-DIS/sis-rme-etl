package org.openmrs.module.epts.etl.conf.datasource.json;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;

public class JsonEtlColumnInfo {
	
	private static final CommonUtilities utilities = CommonUtilities.getInstance();
	
	private String name;
	
	private String column_type;
	
	private Object value;
	
	private Field asField;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getColumn_type() {
		return column_type;
	}
	
	public void setColumn_type(String column_type) {
		this.column_type = column_type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public void init() {
		asEtlField();
	}
	
	public Field asEtlField() {
		if (asField != null) {
			return asField;
		}
		
		Field f = Field.fastCreateWithType(name, column_type);
		
		f.setValue(this.getValue());
		
		asField = f;
		
		return asField;
	}
	
	public static List<Field> toEtlField(List<JsonEtlColumnInfo> columns) {
		if (utilities.listHasElement(columns)) {
			List<Field> fields = new ArrayList<>(columns.size());
			
			for (JsonEtlColumnInfo c : columns) {
				fields.add(c.asEtlField());
			}
			
			return fields;
		}
		
		return null;
	}
	
}
