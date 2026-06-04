package org.openmrs.module.epts.etl.conf.interfaces;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.JsonEtlObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;

public interface EtlJsonConverter extends EtlDataConfiguration {
	
	public static CommonUtilities utils = CommonUtilities.getInstance();
	
	JsonEtlObject convert(String json, Connection srcConn, Connection dstConn);
	
	@SuppressWarnings("unchecked")
	default <T extends Field> List<T> getFields() {
		
		if (getConverterStructrureFields() == null) {
			setConverterStructrureFields((List<T>) utils.generateObjectFields(this));
		}
		
		return getConverterStructrureFields();
	}
	
	<T extends Field> void setConverterStructrureFields(List<T> converterStructrureFields);
	
	<T extends Field> List<T> getConverterStructrureFields();
	
	void setParent(EtlDataConfiguration jsonDataSource);
}
