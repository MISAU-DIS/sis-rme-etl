package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlExpansionDataSource;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class ExpansionTableDataSource extends TableDataSourceConfig implements EtlExpansionDataSource {

	@Override
	public List<EtlDatabaseObject> expand(EtlProcessor processor, EtlDatabaseObject primarySourceObject,
			List<EtlDatabaseObject> availableSourceObjects, EtlDatabaseObject dstObject, Connection srcConn)
			throws DBException {

		if (!isPrepared()) {
			prepare(availableSourceObjects, srcConn);
		}

		return this.getDefaultPreparedQuery().query(processor.getRelatedEtlConfiguration(), processor,
				primarySourceObject, dstObject, availableSourceObjects, srcConn);
	}

	@Override
	public EtlDatabaseObject loadRelatedSrcObject(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, Connection srcConn)
			throws DBException {

		throw new ForbiddenOperationException("Forbidden Method!");
	}

}
