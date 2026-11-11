package org.openmrs.module.epts.etl.connector.jdbc;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.reader.AbstractConnectorRecordReader;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/**
 * Preserves the existing JDBC search behaviour behind the source-reader port.
 */
public final class JdbcConnectorRecordReader<T extends EtlDatabaseObject> extends AbstractConnectorRecordReader<T> {

	private final ConnectorResource resource;
	private final AbstractEtlSearchParams<T> searchParams;
	private final OpenConnection srcConn;

	public JdbcConnectorRecordReader(ConnectorResource resource, AbstractEtlSearchParams<T> searchParams,
			Connection srcConnection) {

		if (searchParams == null) {
			throw new IllegalArgumentException("searchParams cannot be null");
		}

		this.resource = resource;
		this.searchParams = searchParams;

		this.srcConn = (OpenConnection) srcConnection;
	}

	@Override
	public ConnectorResource getResource() {
		return resource;
	}

	@Override
	public List<T> read(ConnectorReadRequest<T> request) throws DBException {

		if (request.isParallel()) {
			return searchParams.searchNextRecordsInMultiThreads(request.getInterval(), request.getParentObject(),
					request.getAuxiliarySourceObjects(), srcConn, null);
		}

		return searchParams.search(request.getInterval(), request.getParentObject(),
				request.getAuxiliarySourceObjects(), srcConn, null);
	}

	@Override
	public <N> N unwrap(Class<N> nativeType) {

		if (nativeType != null && nativeType.isInstance(srcConn)) {
			return nativeType.cast(srcConn);
		}
		throw new IllegalArgumentException(
				"JDBC reader cannot be unwrapped as " + (nativeType == null ? "null" : nativeType.getName()));
	}

	@Override
	public void finalizeCapability(BaseConfiguration openedFrom) {

		if (this.srcConn != null) {
			this.srcConn.finalizeConnection(openedFrom);
		}
	}

	@Override
	public void markAsSuccessifullyTerminected() {

		if (this.srcConn != null) {
			this.srcConn.markAsSuccessifullyTerminated();
		}
	}
}
