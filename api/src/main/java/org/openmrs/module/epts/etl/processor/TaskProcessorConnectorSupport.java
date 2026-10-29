package org.openmrs.module.epts.etl.processor;

import java.sql.Connection;
import java.util.List;
import java.util.function.Consumer;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Keeps native JDBC compatibility outside the connector-neutral processor
 * contract.
 */
final class TaskProcessorConnectorSupport {

	private TaskProcessorConnectorSupport() {
	}

	static <T extends EtlDatabaseObject> void initializeIdGenerators(TaskProcessor<T> processor, List<T> records,
			ConnectorRecordWriters writers, Consumer<org.openmrs.module.epts.etl.conf.IdGeneratorManager> collector)
			throws DBException {

		for (DstConf destination : processor.getEtlItemConfiguration().getDstConf()) {
			if (!destination.isDisabled() && destination.useManualGeneratedObjectId()) {
				Connection connection = writers.get(destination).unwrap(Connection.class);
				// collector.accept(destination.initIdGenerator(processor, records,
				// connection));
			}
		}
	}

	@SuppressWarnings("unchecked")
	static <T extends EtlDatabaseObject> EtlOperationResultHeader<T> delete(TaskProcessor<T> processor, List<T> records,
			ConnectorRecordWriters writers) throws DBException {
		Connection connection = writers.unwrapShared(Connection.class);

		return (EtlOperationResultHeader<T>) DatabaseObjectDAO.deleteAll((List<EtlDatabaseObject>) records,
				processor.getJdbcPrimaryDataSource(), connection);
	}
}
