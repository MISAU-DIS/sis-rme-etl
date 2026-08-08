package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlExpansionDataSource;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class ExpansionQueryDataSource extends QueryDataSourceConfig implements EtlExpansionDataSource {

	@Override
	public List<EtlDatabaseObject> expand(EtlProcessor processor, EtlDatabaseObject primarySourceObject,
			List<EtlDatabaseObject> availableSourceObjects, EtlDatabaseObject dstObject, Connection srcConn)
			throws DBException {

		List<EtlDatabaseObject> full = new ArrayList<>();

		full.add(primarySourceObject);

		if (utilities.listHasElement(availableSourceObjects)) {
			full.addAll(availableSourceObjects);
		}

		if (!isPrepared()) {
			prepare(full, srcConn);
		}

		return this.getDefaultPreparedQuery().query(processor.getRelatedEtlConfiguration(), processor,
				primarySourceObject, dstObject, full, srcConn);
	}

	@Override
	public EtlDatabaseObject loadRelatedSrcObject(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, Connection srcConn)
			throws DBException {

		throw new ForbiddenOperationException("Forbidden Method!");
	}
}
