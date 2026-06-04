package org.openmrs.module.epts.etl.conf.datasource.binlog;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.module.epts.etl.conf.datasource.JsonDataSource;
import org.openmrs.module.epts.etl.conf.datasource.JsonOutputDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlJsonConverter;
import org.openmrs.module.epts.etl.conf.types.DBOperationType;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.JsonEtlObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class BinlogJsonConvertor extends AbctractBinlogObject implements EtlJsonConverter {
	
	private static final CommonUtilities utilities = CommonUtilities.getInstance();
	
	private DBOperationType type;
	
	private Date timestamp;
	
	private List<BinlogTableParent> parents;
	
	private List<? extends Field> converterStructrureFields;
	
	public BinlogJsonConvertor() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Field> List<T> getConverterStructrureFields() {
		return (List<T>) converterStructrureFields;
	}
	
	@Override
	public <T extends Field> void setConverterStructrureFields(List<T> converterStructrureFields) {
		this.converterStructrureFields = converterStructrureFields;
	}
	
	public List<BinlogTableParent> getParents() {
		return parents;
	}
	
	public void setParents(List<BinlogTableParent> parents) {
		this.parents = parents;
	}
	
	private boolean hasParents() {
		return utilities.listHasElement(this.parents);
	}
	
	public DBOperationType getType() {
		return type;
	}
	
	public void setType(DBOperationType type) {
		this.type = type;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public void setParent(EtlDataConfiguration parent) {
		
		if (parent != null && !(parent instanceof JsonDataSource)) {
			throw new ForbiddenOperationException("The parent of BinlogJsonConvertor must be a JsonDataSource");
		}
		super.setParent(parent);
	}
	
	@Override
	public JsonDataSource getParentConf() {
		return (JsonDataSource) super.getParentConf();
	}
	
	@Override
	public JsonEtlObject convert(String json, Connection srcConn, Connection dstConn) {
		JsonEtlObject obj = null;
		
		try {
			BinlogJsonConvertor converted = new ObjectMapperProvider().getContext(BinlogJsonConvertor.class).readValue(json,
			    BinlogJsonConvertor.class);
			
			JsonOutputDataSource outputDs = this.getParentConf().getOutputDataSource();
			
			obj = JsonEtlObject.fastCreate(outputDs, converted.columnsAsEtlFields());
			
			if (converted.hasParents() && outputDs.hasParents()) {
				obj.setAuxLoadObject(new ArrayList<>(converted.getParents().size()));
				
				for (JsonOutputDataSource p : outputDs.getParentOutputDataSource()) {
					BinlogTableParent selected = p.select(converted.getParents());
					
					if (selected != null) {
						obj.addParent(JsonEtlObject.fastCreate(p, selected.columnsAsEtlFields()));
					}
				}
			}
		}
		catch (JsonParseException e) {
			throw new EtlConfException(e);
		}
		catch (JsonMappingException e) {
			throw new EtlConfException(e);
		}
		catch (IOException e) {
			throw new EtlConfException(e);
		}
		
		return obj;
	}
}
