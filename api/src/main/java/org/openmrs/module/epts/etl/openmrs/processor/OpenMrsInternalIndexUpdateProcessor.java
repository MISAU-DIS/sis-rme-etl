package org.openmrs.module.epts.etl.openmrs.processor;

import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.openmrs.conf.OpenMrsIndexConfiguration;
import org.openmrs.module.epts.etl.processor.TaskProcessor;

/**
 * Updates the OpenMRS search index through the in-process OpenMRS API.
 *
 * <p>
 * This processor must only be used while the ETL is running inside an OpenMRS
 * instance. Unlike {@link OpenMrsIndexUpdateProcessor}, it does not make an
 * HTTP request back to the same OpenMRS server.
 * </p>
 */
public class OpenMrsInternalIndexUpdateProcessor extends AbstractOpenMrsIndexUpdateProcessor {

	private OpenMrsContextSession contextSession;

	public OpenMrsInternalIndexUpdateProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits,
			Boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency.booleanValue());
	}

	@Override
	protected OpenMrsIndexConfiguration getIndexConfiguration() {
		return OpenMrsInternalIndexConfigurationProvider.INSTANCE;
	}

	@Override
	protected void beforeBatch(OpenMrsIndexConfiguration configuration) {
		contextSession = OpenMrsContextSession.open(configuration);
	}

	@Override
	protected void afterBatch() {
		if (contextSession != null) {
			contextSession.close();
			contextSession = null;
		}
	}

	@Override
	protected void updateIndex(String resourceType, String uuid, String operation) {
		OpenmrsObject openMrsObject = resolveObject(resourceType, uuid);
		if (openMrsObject == null) {
			logWarn("No OpenMRS {} found with UUID {}. The staging record will be discarded without an index update.",
					resourceType, uuid);
			return;
		}

		updateIndexableObjects(openMrsObject);
	}

	/**
	 * Root domain objects such as Patient, Person and Concept are not @Indexed in
	 * OpenMRS 2.x. Their searchable data is stored in associated indexed entities.
	 */
	private void updateIndexableObjects(OpenmrsObject openMrsObject) {
		if (openMrsObject instanceof Patient) {
			Patient patient = (Patient) openMrsObject;
			patient.getNames().forEach(Context::updateSearchIndexForObject);
			patient.getAttributes().forEach(Context::updateSearchIndexForObject);
			patient.getIdentifiers().forEach(Context::updateSearchIndexForObject);
			return;
		}

		if (openMrsObject instanceof Person) {
			Person person = (Person) openMrsObject;
			person.getNames().forEach(Context::updateSearchIndexForObject);
			person.getAttributes().forEach(Context::updateSearchIndexForObject);
			return;
		}

		if (openMrsObject instanceof Concept) {
			Concept concept = (Concept) openMrsObject;
			concept.getNames().forEach(Context::updateSearchIndexForObject);
			return;
		}

		Context.updateSearchIndexForObject(openMrsObject);
	}

	@Override
	protected String getIndexModeDescription() {
		return "internally";
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

	@Override
	public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(
			IntervalExtremeRecord limits) {
		return new OpenMrsInternalIndexUpdateProcessor(getEngine(), limits, Boolean.FALSE);
	}

	private static final class OpenMrsInternalIndexConfigurationProvider {

		private static final OpenMrsIndexConfiguration INSTANCE = OpenMrsIndexConfiguration.load(false);

		private OpenMrsInternalIndexConfigurationProvider() {
		}
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