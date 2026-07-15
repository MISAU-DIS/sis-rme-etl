package org.openmrs.module.epts.etl.conf.interfaces;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public interface EtlExpansionDataSource extends EtlDataSource {

	List<EtlDatabaseObject> expand(EtlProcessor processor, EtlDatabaseObject primarySourceObject,
			List<EtlDatabaseObject> availableSourceObjects, EtlDatabaseObject dstObject, Connection srcConn)
			throws DBException;
}