package org.openmrs.module.epts.etl.connector.jdbc;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Preserves the existing JDBC search behaviour behind the source-reader port.
 */
public final class JdbcConnectorRecordReader<T extends EtlDatabaseObject> implements ConnectorRecordReader<T> {

	private final ConnectorResource resource;
	private final AbstractEtlSearchParams<T> searchParams;
	private final Connection srcConnection;
	private final Connection dstConnection;

	public JdbcConnectorRecordReader(ConnectorResource resource, AbstractEtlSearchParams<T> searchParams,
			Connection srcConnection, Connection dstConnection) {

		if (searchParams == null) {
			throw new IllegalArgumentException("searchParams cannot be null");
		}

		this.resource = resource;
		this.searchParams = searchParams;
		this.srcConnection = srcConnection;
		this.dstConnection = dstConnection;
	}

	@Override
	public ConnectorResource getResource() {
		return resource;
	}

	@Override
	public List<T> read(ConnectorReadRequest<T> request) throws DBException {

		if (request.isParallel()) {
			return searchParams.searchNextRecordsInMultiThreads(request.getInterval(), request.getParentObject(),
					request.getAuxiliarySourceObjects(), srcConnection, dstConnection);
		}

		return searchParams.search(request.getInterval(), request.getParentObject(),
				request.getAuxiliarySourceObjects(), srcConnection, dstConnection);
	}

	@Override
	public <N> N unwrap(Class<N> nativeType) {
		if (nativeType != null && nativeType.isInstance(srcConnection)) {
			return nativeType.cast(srcConnection);
		}
		throw new IllegalArgumentException("JDBC reader cannot be unwrapped as "
				+ (nativeType == null ? "null" : nativeType.getName()));
	}
}
