package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class JsonMainOutputDataSource extends JsonSimpleOutputDataSource {
	
	private List<JsonSimpleOutputDataSource> parentOutputDataSource;
	
	private JsonDataSource parent;
	
	public void setParent(JsonDataSource parent) {
		this.parent = parent;
	}
	
	public JsonDataSource getParent() {
		return parent;
	}
	
	public List<JsonSimpleOutputDataSource> getParentOutputDataSource() {
		return parentOutputDataSource;
	}
	
	public void setParentOutputDataSource(List<JsonSimpleOutputDataSource> parentOutputDataSource) {
		this.parentOutputDataSource = parentOutputDataSource;
	}
	
	public boolean hasParents() {
		return utilities.listHasElement(this.getParentOutputDataSource());
	}
	
	@Override
	public EtlDatabaseObject loadRelatedSrcObject(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, Connection conn) throws DBException {
		
		if (getParent() == null)
			throw new EtlExceptionImpl("The related JsonDataSource was not set!");
		
		return this.parent.loadRelatedSrcObject(processor, srcObject, dstObject, avaliableSrcObjects, conn);
	}
	
	@Override
	public void init(EtlDataConfiguration relatedParent, EtlDatabaseObject etlSchemaObject, Connection srcConn,
	        Connection dstConn) throws DBException {
		
		super.init(relatedParent, etlSchemaObject, srcConn, dstConn);
		
		if (this.hasParents()) {
			for (JsonSimpleOutputDataSource ds : this.getParentOutputDataSource()) {
				ds.init(relatedParent, etlSchemaObject, srcConn, dstConn);
			}
		}
	}
}
