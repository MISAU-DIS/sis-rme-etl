package org.openmrs.module.epts.etl.openmrs.processor;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlActionType;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.model.EtlLoadStatus;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.openmrs.conf.OpenMrsIndexConfiguration;
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** Common staging lifecycle for OpenMRS search-index processors. */
public abstract class AbstractOpenMrsIndexUpdateProcessor extends EtlProcessor {

	protected AbstractOpenMrsIndexUpdateProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits,
			Boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency.booleanValue());
	}

	@Override
	public final void transformAndLoad(List<EtlDatabaseObject> records, ConnectorRecordReader<?> reader,
			ConnectorRecordWriters writers) throws DBException {
		OpenMrsIndexConfiguration configuration = getIndexConfiguration();
		logInfo("Loaded OpenMRS index configuration: {}", configuration);
		validateAfterEtlAction();

		beforeBatch(configuration);
		try {
			for (EtlDatabaseObject record : records)
				processRecord(record, configuration, reader.unwrap(Connection.class));
		} finally {
			afterBatch();
		}
	}

	private void processRecord(EtlDatabaseObject record, OpenMrsIndexConfiguration configuration, Connection srcConn)
			throws DBException {
		try {
			String resourceType = requiredRecordValue(record, configuration.getResourceTypeField());
			String uuid = readUuid(record, configuration);
			String operation = optionalRecordValue(record, configuration.getOperationField());

			updateIndex(resourceType, uuid, operation);
			// Failed records are deliberately retained in staging for a later retry.
			DatabaseObjectDAO.remove(record, srcConn);
			getTaskResultInfo().addToRecordsWithNoError(record);
			logDebug("OpenMRS index updated {} for resource {} with UUID {}", getIndexModeDescription(), resourceType,
					uuid);
		} catch (Exception exception) {
			recordFailure(record, rootCause(exception), srcConn);
		}
	}

	private void validateAfterEtlAction() {
		EtlActionType afterAction = getRelatedEtlOperationConfig().getAfterEtlActionType();
		if (afterAction == null || !afterAction.isDelete()) {
			throw new ForbiddenOperationException(getClass().getSimpleName()
					+ " requires DELETE as afterEtlActionType so that only successfully indexed records are removed "
					+ "from staging; configured value: " + afterAction);
		}
	}

	protected abstract OpenMrsIndexConfiguration getIndexConfiguration();

	protected abstract void updateIndex(String resourceType, String uuid, String operation) throws Exception;

	protected abstract String getIndexModeDescription();

	/** Hook for resources that must be acquired once per batch. */
	protected void beforeBatch(OpenMrsIndexConfiguration configuration) {
	}

	/**
	 * Hook for resources acquired by
	 * {@link #beforeBatch(OpenMrsIndexConfiguration)}.
	 */
	protected void afterBatch() {
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

	private Exception rootCause(Exception exception) {
		Throwable cause = exception;
		while (cause.getCause() != null)
			cause = cause.getCause();
		return cause instanceof Exception ? (Exception) cause : exception;
	}

	private void recordFailure(EtlDatabaseObject record, Exception exception, Connection srcConn) throws DBException {
		Object currentRetryCount = record.getFieldValue("retry_count");
		int retryCount = currentRetryCount == null ? 1 : Integer.parseInt(currentRetryCount.toString()) + 1;

		record.setFieldValue("retry_count", retryCount);
		record.setFieldValue("processing_state", EtlLoadStatus.NOT_LOADED_DUE_ERRORS);
		record.setFieldValue("processing_date", DateAndTimeUtilities.getCurrentSystemDate(srcConn));
		record.setFieldValue("processing_error",
				exception.getLocalizedMessage() != null ? exception.getLocalizedMessage()
						: exception.getClass().getCanonicalName());

		logWarn("Could not update the OpenMRS index for staging record {}. Error: {}", record.getObjectId(),
				record.getFieldValue("processing_error"));
		record.update((TableConfiguration) record.getRelatedConfiguration(), srcConn);
	}
}