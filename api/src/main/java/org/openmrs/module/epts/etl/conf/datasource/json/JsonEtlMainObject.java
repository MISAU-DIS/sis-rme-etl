package org.openmrs.module.epts.etl.conf.datasource.json;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.module.epts.etl.conf.datasource.JsonDataSource;
import org.openmrs.module.epts.etl.conf.datasource.JsonMainOutputDataSource;
import org.openmrs.module.epts.etl.conf.datasource.JsonSimpleOutputDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.types.DBOperationType;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.JsonEtlObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JsonEtlMainObject extends JsonEtlAbctractObject implements EtlDataConfiguration {
	
	private static final CommonUtilities utilities = CommonUtilities.getInstance();
	
	private DBOperationType type;
	
	private Date timestamp;
	
	private List<JsonEtlObjectParent> parents;
	
	private List<? extends Field> converterStructrureFields;
	
	private JsonEtlMainObject() {
	}

	@SuppressWarnings("unchecked")
	public <T extends Field> List<T> getConverterStructrureFields() {
		return (List<T>) converterStructrureFields;
	}
	
	public <T extends Field> void setConverterStructrureFields(List<T> converterStructrureFields) {
		this.converterStructrureFields = converterStructrureFields;
	}
	
	public List<JsonEtlObjectParent> getParents() {
		return parents;
	}
	
	public void setParents(List<JsonEtlObjectParent> parents) {
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
	
	public static JsonEtlObject convert(JsonDataSource parent, String json, Connection srcConn, Connection dstConn) {
		JsonEtlObject obj = null;
		
		try {
			JsonEtlMainObject mainObject = new ObjectMapperProvider().getContext(JsonEtlMainObject.class).readValue(json,
			    JsonEtlMainObject.class);
			
			mainObject.setParent(parent);
			
			JsonMainOutputDataSource outputDs = parent.getOutputDataSource();
			
			obj = JsonEtlObject.fastCreate(outputDs, mainObject.columnsAsEtlFields());
			
			if (mainObject.hasParents() && outputDs.hasParents()) {
				obj.setAuxLoadObject(new ArrayList<>(mainObject.getParents().size()));
				
				for (JsonSimpleOutputDataSource p : outputDs.getParentOutputDataSource()) {
					JsonEtlObjectParent selected = p.select(mainObject.getParents());
					
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
