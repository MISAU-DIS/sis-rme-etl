package org.openmrs.module.epts.etl.etl.model.parent;

import java.sql.Connection;

import org.openmrs.module.epts.etl.etl.model.persistence.EnginePersistenceRequest;
import org.openmrs.module.epts.etl.etl.model.persistence.PersistenceType;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** Persists the default-parent information generated for one ETL record. */
public final class DefaultParentPersistenceRequest implements EnginePersistenceRequest {

	private final EtlInfo etlInfo;

	public DefaultParentPersistenceRequest(EtlInfo etlInfo) {
		this.etlInfo = etlInfo;
	}

	@Override
	public PersistenceType getType() {
		return PersistenceType.DEFAULT_PARENT;
	}

	@Override
	public int size() {
		return etlInfo.getParentsWithDefaultValues().size();
	}

	@Override
	public void persist(Connection srcConn, Connection dstConn) throws DBException {
		etlInfo.saveRecordsWithDefaultsParents(srcConn, dstConn);
	}
}
