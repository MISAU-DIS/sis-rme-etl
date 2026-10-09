package org.openmrs.module.epts.etl.openmrs.processor;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.EtlActionType;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.openmrs.client.OpenMrsIndexClient;
import org.openmrs.module.epts.etl.openmrs.conf.OpenMrsIndexConfiguration;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** Sends staging records to an OpenMRS index-update endpoint. */
public class OpenMrsIndexUpdateProcessor extends EtlProcessor {

	public OpenMrsIndexUpdateProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits,
			Boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency.booleanValue());
	}

	@Override
	public void transformAndLoad(List<EtlDatabaseObject> records, Connection srcConn, Connection dstConn)
			throws DBException {

		OpenMrsIndexConfiguration configuration = OpenMrsIndexClientProvider.getConfiguration();
		OpenMrsIndexClient client = OpenMrsIndexClientProvider.getInstance();
		EtlActionType afterAction = getRelatedEtlOperationConfig().getAfterEtlActionType();

		if (afterAction == null || !afterAction.isDelete()) {
			throw new ForbiddenOperationException("OpenMrsIndexUpdateProcessor requires DELETE as afterEtlActionType "
					+ "so that only successfully indexed records are removed from staging; configured value: "
					+ afterAction);
		}

		for (EtlDatabaseObject record : records) {
			try {
				String resourceType = requiredRecordValue(record, configuration.getResourceTypeField());
				String uuid = readUuid(record, configuration);
				String operation = optionalRecordValue(record, configuration.getOperationField());

				client.updateIndex(resourceType, uuid, operation);
				getTaskResultInfo().addToRecordsWithNoError(record);

				// Removal happens only after a successful HTTP response. Failed records are
				// deliberately kept in staging so that a later ETL run can retry them.
				DatabaseObjectDAO.remove(record, srcConn);

				logDebug("OpenMRS index updated for resource {} with UUID {}", resourceType, uuid);
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
				recordFailure(record, exception);
			} catch (Exception exception) {
				recordFailure(record, exception);
			}
		}
	}

	private String readUuid(EtlDatabaseObject record, OpenMrsIndexConfiguration configuration) {
		String uuid = optionalRecordValue(record, configuration.getUuidField());
		if (uuid == null)
			uuid = record.getUuid();
		if (uuid == null || uuid.isBlank())
			throw new IllegalArgumentException("Staging record does not contain an OpenMRS UUID");
		return uuid;
	}

	private String requiredRecordValue(EtlDatabaseObject record, String fieldName) {
		String value = optionalRecordValue(record, fieldName);
		if (value == null)
			throw new IllegalArgumentException("Staging record field '" + fieldName + "' is required");
		return value;
	}

	private String optionalRecordValue(EtlDatabaseObject record, String fieldName) {
		if (fieldName == null || fieldName.isBlank())
			return null;
		try {
			Object value = record.getFieldValue(fieldName);
			return value == null || value.toString().isBlank() ? null : value.toString().trim();
		} catch (RuntimeException exception) {
			return null;
		}
	}

	private void recordFailure(EtlDatabaseObject record, Exception exception) {
		EtlExceptionImpl etlException = new EtlExceptionImpl(
				"Could not update the OpenMRS index: " + exception.getMessage(), exception, record,
				ActionOnEtlIssue.MARK_RECORD_AS_FAILED);
		getTaskResultInfo().addToRecordsWithUnresolvedErrors(record, etlException);
		logError("Could not update the OpenMRS index for staging record " + record.getObjectId(), exception);
	}

	@Override
	public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(
			IntervalExtremeRecord limits) {
		return new OpenMrsIndexUpdateProcessor(getEngine(), limits, Boolean.FALSE);
	}

	/**
	 * One configuration and one reusable HTTP connection pool per module
	 * classloader.
	 */
	private static final class OpenMrsIndexClientProvider {

		private static final OpenMrsIndexConfiguration CONFIGURATION = OpenMrsIndexConfiguration.load();
		private static final OpenMrsIndexClient INSTANCE = new OpenMrsIndexClient(CONFIGURATION);

		private OpenMrsIndexClientProvider() {
		}

		private static OpenMrsIndexClient getInstance() {
			return INSTANCE;
		}

		private static OpenMrsIndexConfiguration getConfiguration() {
			return CONFIGURATION;
		}
	}
}
