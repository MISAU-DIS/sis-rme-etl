package org.openmrs.module.epts.etl.connector.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReaderFactory;
import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.model.SearchClauses;
import org.openmrs.module.epts.etl.model.base.VOLoaderHelper;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnectionTestFactory;

class JdbcConnectorRecordReaderTest {

	@Test
	void shouldDelegateSingleThreadReadToExistingSearchPath() throws DBException {
		JdbcTableResource resource = resource(EtlSide.SRC);
		TrackingSearchParams searchParams = new TrackingSearchParams();
		ConnectorRecordReader<GenericDatabaseObject> reader = ConnectorRecordReaderFactory.create(resource,
				searchParams, newConnectionMarker());
		IntervalExtremeRecord interval = new IntervalExtremeRecord(1, 10);

		List<GenericDatabaseObject> result = reader.read(new ConnectorReadRequest<>(interval, null, null, false));

		assertSame(searchParams.singleResult, result);
		assertSame(interval, searchParams.receivedInterval);
		assertEquals(1, searchParams.singleCalls);
		assertEquals(0, searchParams.parallelCalls);
	}

	@Test
	void shouldExposeJdbcConnectionOnlyThroughNativeUnwrap() {
		OpenConnection connection = newConnectionMarker();
		ConnectorRecordReader<GenericDatabaseObject> reader = ConnectorRecordReaderFactory
				.create(resource(EtlSide.SRC), new TrackingSearchParams(), connection);

		assertSame(connection, reader.unwrap(Connection.class));
		assertThrows(IllegalArgumentException.class, () -> reader.unwrap(String.class));
	}

	@Test
	void shouldDelegateParallelReadWithoutChangingJdbcSemantics() throws DBException {
		TrackingSearchParams searchParams = new TrackingSearchParams();
		ConnectorRecordReader<GenericDatabaseObject> reader = ConnectorRecordReaderFactory
				.create(resource(EtlSide.SRC), searchParams, newConnectionMarker());

		List<GenericDatabaseObject> result = reader
				.read(new ConnectorReadRequest<>(new IntervalExtremeRecord(1, 10), null, null, true));

		assertSame(searchParams.parallelResult, result);
		assertEquals(0, searchParams.singleCalls);
		assertEquals(1, searchParams.parallelCalls);
	}

	@Test
	void shouldRejectDestinationResourceAsReaderSource() {
		assertThrows(IllegalArgumentException.class, () -> ConnectorRecordReaderFactory
				.create(resource(EtlSide.DST), new TrackingSearchParams(), null));
	}

	private JdbcTableResource resource(EtlSide direction) {
		return new JdbcTableResource(new GenericTableConfiguration("patient"), direction, new DBConnectionInfo());
	}

	private OpenConnection newConnectionMarker() {
		return OpenConnectionTestFactory.create(new GenericTableConfiguration("connection_owner"));
	}

	private static final class TrackingSearchParams extends AbstractEtlSearchParams<GenericDatabaseObject> {

		private final List<GenericDatabaseObject> singleResult = Collections.emptyList();
		private final List<GenericDatabaseObject> parallelResult = Collections.emptyList();
		private int singleCalls;
		private int parallelCalls;
		private IntervalExtremeRecord receivedInterval;

		private TrackingSearchParams() {
			super(null, (ThreadRecordIntervalsManager<GenericDatabaseObject>) null);
		}

		@Override
		public List<GenericDatabaseObject> search(IntervalExtremeRecord interval, GenericDatabaseObject parent,
				List<GenericDatabaseObject> auxiliary, Connection src, Connection dst) {
			singleCalls++;
			receivedInterval = interval;
			return singleResult;
		}

		@Override
		public List<GenericDatabaseObject> searchNextRecordsInMultiThreads(IntervalExtremeRecord interval,
				GenericDatabaseObject parent, List<GenericDatabaseObject> auxiliary, Connection src, Connection dst) {
			parallelCalls++;
			receivedInterval = interval;
			return parallelResult;
		}

		@Override
		public AbstractEtlSearchParams<GenericDatabaseObject> cloneMe() {
			return this;
		}

		@Override
		protected VOLoaderHelper getLoaderHealper() {
			return null;
		}

		@Override
		public int countNotProcessedRecords(OperationController<GenericDatabaseObject> controller, Connection conn) {
			return 0;
		}

		@Override
		public String generateDestinationExclusionClause(Connection srcConn, Connection dstConn) {
			return null;
		}

		@Override
		public SearchClauses<GenericDatabaseObject> generateSearchClauses(IntervalExtremeRecord recordLimits,
				GenericDatabaseObject parentObject, List<GenericDatabaseObject> auxiliary, Connection srcConn,
				Connection dstConn) {
			return null;
		}

		@Override
		public Class<GenericDatabaseObject> getRecordClass() {
			return GenericDatabaseObject.class;
		}
	}
}
