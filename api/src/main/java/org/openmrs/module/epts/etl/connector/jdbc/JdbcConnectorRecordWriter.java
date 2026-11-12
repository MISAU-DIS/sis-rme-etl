package org.openmrs.module.epts.etl.connector.jdbc;


import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.JdbcTableDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.writer.AbstractConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorWriteRequest;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/** JDBC destination adapter preserving the current loading operation. */
public final class JdbcConnectorRecordWriter<T extends EtlDatabaseObject> extends AbstractConnectorRecordWriter<T> {

	private final ConnectorResource resource;
	private final AbstractTableConfiguration tableConfiguration;
	private final OpenConnection dstConn;

	public JdbcConnectorRecordWriter(ConnectorResource resource, AbstractTableConfiguration tableConfiguration,
			OpenConnection destinationConnection) {

		if (destinationConnection == null) {
			throw new IllegalArgumentException("destinationConnection cannot be null");
		}

		if (resource == null) {
			throw new IllegalArgumentException("resource cannot be null");
		}

		if (tableConfiguration == null) {
			throw new IllegalArgumentException("tableConfiguration cannot be null");
		}
		this.resource = resource;
		this.tableConfiguration = tableConfiguration;
		this.dstConn = destinationConnection;
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

		switch (request.getMode()) {
		case INSERT:
			return DatabaseObjectDAO.load(cast(request), tableConfiguration, dstConn);

		case UPDATE:
			return DatabaseObjectDAO.update(cast(request), requireDestinationConfiguration(), dstConn);

		case UPDATE_ALL:
			return DatabaseObjectDAO.updateAll(cast(request), tableConfiguration, dstConn);

		case DELETE_ALL:
			return DatabaseObjectDAO.deleteAll(cast(request), tableConfiguration, dstConn);

		default:
			throw new IllegalArgumentException("Unsupported JDBC write mode " + request.getMode());
		}
	}

	private JdbcTableDestinationConfiguration requireDestinationConfiguration() {

		if (tableConfiguration instanceof JdbcTableDestinationConfiguration) {
			return (JdbcTableDestinationConfiguration) tableConfiguration;
		}
		throw new IllegalArgumentException("JDBC UPDATE requires a JdbcTableDestinationConfiguration, but received "
				+ tableConfiguration.getClass().getName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private java.util.List<EtlDatabaseObject> cast(ConnectorWriteRequest<T> request) {
		return (java.util.List) request.getRecords();
	}

	@Override
	public <N> N unwrap(Class<N> nativeType) {

		if (nativeType != null && nativeType.isInstance(dstConn)) {
			return nativeType.cast(dstConn);
		}

		throw new IllegalArgumentException(
				"JDBC writer cannot be unwrapped as " + (nativeType == null ? "null" : nativeType.getName()));
	}

	@Override
	public void finalizeCapability(BaseConfiguration openedFrom) {

		if (this.dstConn != null) {
			this.dstConn.finalizeConnection(openedFrom);
		}
	}

	@Override
	public void markAsSuccessifullyTerminected() {

		if (this.dstConn != null) {
			this.dstConn.markAsSuccessifullyTerminated();
		}
	}
}
