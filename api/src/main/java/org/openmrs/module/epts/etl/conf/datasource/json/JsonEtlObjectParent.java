package org.openmrs.module.epts.etl.conf.datasource.json;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;

public class JsonEtlObjectParent extends JsonEtlAbctractObject {
	
	private String child_field;
	
	public String getChild_field() {
		return child_field;
	}
	
	public void setChild_field(String child_field) {
		this.child_field = child_field;
	}
	
	@Override
	public JsonEtlMainObject getParentConf() {
		return (JsonEtlMainObject) super.getParentConf();
	}
	
	@Override
	public void setParent(EtlDataConfiguration parent) {
		
		if (!(parent instanceof JsonEtlMainObject)) {
			throw new ForbiddenOperationException("The parent of BinlogTableParent must be a BinlogJsonConvertor");
		}
		super.setParent(parent);
	}
}
