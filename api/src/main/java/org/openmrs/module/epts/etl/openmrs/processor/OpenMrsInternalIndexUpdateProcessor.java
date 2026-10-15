package org.openmrs.module.epts.etl.openmrs.processor;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlActionType;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.model.EtlLoadStatus;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.openmrs.conf.OpenMrsIndexConfiguration;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Updates the OpenMRS search index through the in-process OpenMRS API.
 *
 * <p>
 * This processor must only be used while the ETL is running inside an OpenMRS
 * instance. Unlike {@link OpenMrsIndexUpdateProcessor}, it does not make an
 * HTTP request back to the same OpenMRS server.
 * </p>
 */
public class OpenMrsInternalIndexUpdateProcessor extends org.openmrs.module.epts.etl.etl.processor.JdbcEtlProcessor {

	public OpenMrsInternalIndexUpdateProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits,
			Boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency.booleanValue());
	}

	@Override
	public void transformAndLoad(List<EtlDatabaseObject> records, Connection srcConn, Connection dstConn)
			throws DBException {

		OpenMrsIndexConfiguration configuration = OpenMrsIndexConfiguration.load();
		EtlActionType afterAction = getRelatedEtlOperationConfig().getAfterEtlActionType();

		if (afterAction == null || !afterAction.isDelete()) {
			throw new ForbiddenOperationException(
					"OpenMrsInternalIndexUpdateProcessor requires DELETE as afterEtlActionType so that only "
							+ "successfully indexed records are removed from staging; configured value: "
							+ afterAction);
		}

		OpenMrsContextSession contextSession = OpenMrsContextSession.open(configuration);

		try {
			for (EtlDatabaseObject record : records) {
				try {
					String resourceType = requiredRecordValue(record, configuration.getResourceTypeField());
					String uuid = readUuid(record, configuration);
					OpenmrsObject openMrsObject = resolveObject(resourceType, uuid);

					if (openMrsObject == null) {
						throw new IllegalArgumentException("No OpenMRS " + resourceType + " found with UUID " + uuid);
					}

					Context.updateSearchIndexForObject(openMrsObject);
					getTaskResultInfo().addToRecordsWithNoError(record);

					DatabaseObjectDAO.remove(record, srcConn);
					logDebug("OpenMRS index updated internally for resource {} with UUID {}", resourceType, uuid);
				} catch (Exception exception) {
					recordFailure(record, rootCause(exception), srcConn);
				}
			}
		} finally {
			contextSession.close();
		}
	}

	private OpenmrsObject resolveObject(String resourceType, String uuid) {
		String type = normalizeResourceType(resourceType);

		switch (type) {
		case "patient":
			return Context.getPatientService().getPatientByUuid(uuid);
		case "patientidentifier":
			return Context.getPatientService().getPatientIdentifierByUuid(uuid);
		case "person":
			return Context.getPersonService().getPersonByUuid(uuid);
		case "personname":
			return Context.getPersonService().getPersonNameByUuid(uuid);
		case "personaddress":
			return Context.getPersonService().getPersonAddressByUuid(uuid);
		case "personattribute":
			return Context.getPersonService().getPersonAttributeByUuid(uuid);
		case "concept":
			return Context.getConceptService().getConceptByUuid(uuid);
		case "conceptname":
			return Context.getConceptService().getConceptNameByUuid(uuid);
		case "conceptdescription":
			return Context.getConceptService().getConceptDescriptionByUuid(uuid);
		case "drug":
			return Context.getConceptService().getDrugByUuid(uuid);
		case "encounter":
			return Context.getEncounterService().getEncounterByUuid(uuid);
		case "obs":
		case "observation":
			return Context.getObsService().getObsByUuid(uuid);
		case "location":
			return Context.getLocationService().getLocationByUuid(uuid);
		case "provider":
			return Context.getProviderService().getProviderByUuid(uuid);
		case "visit":
			return Context.getVisitService().getVisitByUuid(uuid);
		case "order":
			return Context.getOrderService().getOrderByUuid(uuid);
		default:
			throw new IllegalArgumentException("Unsupported OpenMRS resource type '" + resourceType
					+ "'. Add its UUID resolver to OpenMrsInternalIndexUpdateProcessor.resolveObject");
		}
	}

	private String normalizeResourceType(String resourceType) {
		return resourceType.trim().toLowerCase(Locale.ROOT).replace("_", "").replace("-", "").replace(" ", "");
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
		int retryCount = Integer.parseInt(record.getFieldValue("retry_count").toString()) + 1;

		record.setFieldValue("retry_count", retryCount);
		record.setFieldValue("processing_state", EtlLoadStatus.NOT_LOADED_DUE_ERRORS);
		record.setFieldValue("processing_date", DateAndTimeUtilities.getCurrentSystemDate(srcConn));
		record.setFieldValue("processing_error",
				exception.getLocalizedMessage() != null ? exception.getLocalizedMessage()
						: exception.getClass().getCanonicalName());

		logWarn("Could not update the OpenMRS index internally for staging record {}. Error: {}", record.getObjectId(),
				record.getFieldValue("processing_error"));
		record.update((TableConfiguration) record.getRelatedConfiguration(), srcConn);
	}

	@Override
	public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(
			IntervalExtremeRecord limits) {
		return new OpenMrsInternalIndexUpdateProcessor(getEngine(), limits, Boolean.FALSE);
	}

	/** Owns only the OpenMRS context session opened by this processor. */
	private static final class OpenMrsContextSession implements AutoCloseable {

		private final boolean openedHere;

		private OpenMrsContextSession(boolean openedHere) {
			this.openedHere = openedHere;
		}

		private static OpenMrsContextSession open(OpenMrsIndexConfiguration configuration) {
			boolean openedHere = false;
			if (!Context.isSessionOpen()) {
				Context.openSession();
				openedHere = true;
			}

			try {
				if (!Context.isAuthenticated()) {
					if (configuration.getUsername() == null || configuration.getPassword() == null) {
						throw new IllegalStateException("The ETL thread has no authenticated OpenMRS user. Configure "
								+ "epts.etl.openmrs.index.username and epts.etl.openmrs.index.password");
					}
					Context.authenticate(configuration.getUsername(), configuration.getPassword());
				}
				return new OpenMrsContextSession(openedHere);
			} catch (RuntimeException exception) {
				if (openedHere)
					Context.closeSession();
				throw exception;
			}
		}

		@Override
		public void close() {
			if (openedHere)
				Context.closeSession();
		}
	}
}
