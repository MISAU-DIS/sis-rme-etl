package org.openmrs.module.epts.etl.conf.interfaces;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;

public interface EtlJsonConverter {
	
	public static CommonUtilities utils = CommonUtilities.getInstance();
	
	EtlDatabaseObject convert(String json, Connection srcConn, Connection dstConn);
	
	@SuppressWarnings("unchecked")
	default <T extends Field> List<T> getFields() {
		
		if (getConverterStructrureFields() == null) {
			setConverterStructrureFields((List<T>) utils.generateObjectFields(this));
		}
		
		return getConverterStructrureFields();
	}
	
	<T extends Field> void setConverterStructrureFields(List<T> converterStructrureFields);
	
	<T extends Field> List<T> getConverterStructrureFields();
	
}
