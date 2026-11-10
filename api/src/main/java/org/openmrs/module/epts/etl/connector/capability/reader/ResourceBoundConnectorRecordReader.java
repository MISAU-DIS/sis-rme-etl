package org.openmrs.module.epts.etl.connector.capability.reader;

import java.util.List;

import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Resource-bound reader used while connector-specific related reads are being
 * migrated. It exposes the correct resource and optional native state, but does
 * not pretend to implement record extraction.
 */
public final class ResourceBoundConnectorRecordReader<T extends EtlDatabaseObject>
		extends AbstractConnectorRecordReader<T> {

	private final ConnectorResource resource;
	private final Object nativeState;

	public ResourceBoundConnectorRecordReader(ConnectorResource resource, Object nativeState) {

		if (resource == null) {
			throw new IllegalArgumentException("resource cannot be null");
		}
		this.resource = resource;
		this.nativeState = nativeState;
	}

	@Override
	public ConnectorResource getResource() {
		return resource;
	}

	@Override
	public List<T> read(ConnectorReadRequest<T> request) throws DBException {
		throw new UnsupportedOperationException(
				"Related record extraction has not yet been implemented for resource " + resource.getName());
	}

	@Override
	public <N> N unwrap(Class<N> nativeType) {

		if (nativeType != null && nativeState != null && nativeType.isInstance(nativeState)) {
			return nativeType.cast(nativeState);
		}
		throw new IllegalArgumentException("Reader for resource '" + resource.getName() + "' cannot be unwrapped as "
				+ (nativeType == null ? "null" : nativeType.getName()));
	}
}
