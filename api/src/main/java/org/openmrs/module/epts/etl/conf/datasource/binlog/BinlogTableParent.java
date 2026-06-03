package org.openmrs.module.epts.etl.conf.datasource.binlog;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;

public class BinlogTableParent extends AbctractBinlogObject {
	
	private String child_field;
	
	public String getChild_field() {
		return child_field;
	}
	
	public void setChild_field(String child_field) {
		this.child_field = child_field;
	}
	
	@Override
	public BinlogJsonConvertor getParentConf() {
		return (BinlogJsonConvertor) super.getParentConf();
	}
	
	@Override
	public void setParent(AbstractEtlDataConfiguration parent) {
		
		if (!(parent instanceof BinlogJsonConvertor)) {
			throw new ForbiddenOperationException("The parent of BinlogTableParent must be a BinlogJsonConvertor");
		}
		super.setParent(parent);
	}
}
