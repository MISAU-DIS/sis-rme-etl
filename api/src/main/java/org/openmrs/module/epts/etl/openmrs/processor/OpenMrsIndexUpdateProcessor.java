package org.openmrs.module.epts.etl.openmrs.processor;

import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.openmrs.client.OpenMrsIndexClient;
import org.openmrs.module.epts.etl.openmrs.conf.OpenMrsIndexConfiguration;
import org.openmrs.module.epts.etl.processor.TaskProcessor;

/** Sends staging records to an OpenMRS index-update endpoint. */
public class OpenMrsIndexUpdateProcessor extends AbstractOpenMrsIndexUpdateProcessor {

	public OpenMrsIndexUpdateProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits,
			Boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency.booleanValue());
	}

	@Override
	protected OpenMrsIndexConfiguration getIndexConfiguration() {
		return OpenMrsIndexClientProvider.getConfiguration();
	}

	@Override
	protected void updateIndex(String resourceType, String uuid, String operation) throws Exception {
		OpenMrsIndexClientProvider.getInstance().updateIndex(resourceType, uuid, operation);
	}

	@Override
	protected String getIndexModeDescription() {
		return "through HTTP";
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

		private static final OpenMrsIndexConfiguration CONFIGURATION = OpenMrsIndexConfiguration.load(true);
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
