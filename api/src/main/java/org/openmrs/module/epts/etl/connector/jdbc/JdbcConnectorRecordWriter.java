package org.openmrs.module.epts.etl.connector.jdbc;

import java.sql.Connection;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorWriteRequest;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** JDBC destination adapter preserving the current loading operation. */
public final class JdbcConnectorRecordWriter<T extends EtlDatabaseObject> implements ConnectorRecordWriter<T> {

	private final ConnectorResource resource;
	private final Connection destinationConnection;

	public JdbcConnectorRecordWriter(ConnectorResource resource, Connection destinationConnection) {

		if (destinationConnection == null) {
			throw new IllegalArgumentException("destinationConnection cannot be null");
		}

		if (resource == null) {
			throw new IllegalArgumentException("resource cannot be null");
		}
		this.resource = resource;
		this.destinationConnection = destinationConnection;
	}

	@Override
	public ConnectorResource getResource() {
		return resource;
	}

	@Override
	public EtlOperationResultHeader<EtlDatabaseObject> write(ConnectorWriteRequest<T> request) throws DBException {

		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}

		DstConf configuration = resource.unwrap(DstConf.class);

		switch (request.getMode()) {
		case INSERT:
			return DatabaseObjectDAO.load(cast(request), configuration, destinationConnection);
		case UPDATE:
			return DatabaseObjectDAO.update(cast(request), configuration, destinationConnection);
		case UPDATE_ALL:
			return DatabaseObjectDAO.updateAll(cast(request), configuration, destinationConnection);
		case DELETE_ALL:
			return DatabaseObjectDAO.deleteAll(cast(request), configuration, destinationConnection);
		default:
			throw new IllegalArgumentException("Unsupported JDBC write mode " + request.getMode());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private java.util.List<EtlDatabaseObject> cast(ConnectorWriteRequest<T> request) {
		return (java.util.List) request.getRecords();
	}

	@Override
	public <N> N unwrap(Class<N> nativeType) {
		if (nativeType != null && nativeType.isInstance(destinationConnection)) {
			return nativeType.cast(destinationConnection);
		}

		throw new IllegalArgumentException(
				"JDBC writer cannot be unwrapped as " + (nativeType == null ? "null" : nativeType.getName()));
	}
}
