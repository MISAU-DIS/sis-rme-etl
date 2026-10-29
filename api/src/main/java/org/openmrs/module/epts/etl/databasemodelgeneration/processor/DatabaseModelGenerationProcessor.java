package org.openmrs.module.epts.etl.databasemodelgeneration.processor;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.DestinationConnectorConnections;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.conf.physical.FilePhysicalTableMetadataRepository;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableKey;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableKeyFactory;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableMetadata;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableMetadataFingerprint;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.databasemodelgeneration.controller.DatabaseModelGenerationController;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationRecord;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationContext;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationSearchParams;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelManifest;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.FileDatabaseModelManifestRepository;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.etl.processor.transformer.ParentOnDemandLoadTransformer;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * The processor responsible for transport synchronization files from origin to
 * destination site
 * <p>
 * This is temporariy transportation method which suppose that the origin and
 * destination are in the same matchine, so the transport process consist on
 * moving files from export directory to import directory
 * <p>
 * In the future a propery transportation method should be implemented.
 * 
 * @author jpboane
 */
public class DatabaseModelGenerationProcessor extends org.openmrs.module.epts.etl.processor.JdbcTaskProcessor<DatabaseModelGenerationRecord> {

	private final DatabaseModelGenerationVisitTracker<String> generationVisitTracker;

	private final DatabaseModelGenerationVisitTracker<PhysicalTableKey> physicalMetadataVisitTracker;

	private boolean databaseModelGenerated;

	private Set<EtlItemConfiguration> visitedItemConfigurations;

	/**
	 * Activates live-schema regeneration before table configurations can attach
	 * static physical metadata. Existing artifacts are preserved until the
	 * corresponding configuration tree element is actually regenerated.
	 */
	public static void prepareForRegeneration(EtlConfiguration configuration) {
		if (configuration == null || !configuration.shouldOverrideExistingDataModelElement()
				|| !hasDatabaseModelGenerationOperation(configuration)) {
			return;
		}

		configuration.setDatabaseModelRegenerationMode(true);
		configuration.getPhysicalTableConfigurationRegistry().clear();
	}

	private static boolean hasDatabaseModelGenerationOperation(EtlConfiguration configuration) {
		if (configuration.getOperations() == null) return false;
		for (EtlOperationConfig operation : configuration.getOperations()) {
			if (hasEnabledDatabaseModelGenerationOperation(operation)) return true;
		}
		return false;
	}

	private static boolean hasEnabledDatabaseModelGenerationOperation(EtlOperationConfig operation) {
		if (operation == null || operation.isDisabled()) return false;
		if (operation.getOperationType() != null && operation.isDatabaseModelGeneration()) return true;
		return hasEnabledDatabaseModelGenerationOperation(operation.getChild());
	}


	public DatabaseModelGenerationProcessor(Engine<DatabaseModelGenerationRecord> monitor, IntervalExtremeRecord limits,
			boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency);

		this.generationVisitTracker = new DatabaseModelGenerationVisitTracker<>();
		this.physicalMetadataVisitTracker = new DatabaseModelGenerationVisitTracker<>();
		this.visitedItemConfigurations = Collections.newSetFromMap(new IdentityHashMap<>());
	}

	@Override
	public DatabaseModelGenerationSearchParams getSearchParams() {
		return (DatabaseModelGenerationSearchParams) super.getSearchParams();
	}

	@Override
	public void extractTransformAndLoad(boolean useMultiThreadSearch,
			ConnectorRecordReader<DatabaseModelGenerationRecord> reader, ConnectorRecordWriters writers)
			throws DBException {
		this.getSearchParams().setProcessor(this);

		super.extractTransformAndLoad(false, reader, writers);
	}

	@Override
	public void transformAndLoad(List<DatabaseModelGenerationRecord> records, Connection srcConn, Connection dstConn)
			throws DBException {

		generateConfigurationTree(getEtlItemConfiguration(), getRelatedEtlOperationConfig(), srcConn, dstConn);

		getTaskResultInfo().addSuccessfulRecords(records);

		this.databaseModelGenerated = true;
	}

	private void generateConfigurationTree(EtlItemConfiguration item, EtlOperationConfig operationConfig,
			Connection srcConn, Connection dstConn) throws DBException {
		if (item == null || !visitedItemConfigurations.add(item))
			return;

		item.init(getRelatedEtlConf(), item.isTesting(), srcConn);

		try (DestinationConnectorConnections destinationConnections = item.createDestinationConnections(this)) {
			item.fullLoad(operationConfig, srcConn, destinationConnections);

			DatabaseModelGenerationContext sourceContext = DatabaseModelGenerationContext.source(
					getRelatedEtlConfiguration().getDataModel(), srcConn);

			if (!item.getSrcConf().doNotUseAsDatasource()) {
				generate(sourceContext, item.getSrcConf());

				for (EtlAdditionalDataSource dataSource : item.getSrcConf().getAvaliableExtraDataSource()) {
					generate(sourceContext, dataSource);
				}
			}

			if (item.hasDstConf()) {
				for (DstConf destination : item.getDstConf()) {
					if (destination.isDisabled() || !destinationConnections.isJdbc(destination))
						continue;

					Connection destinationConnection = destinationConnections.get(destination);
					DatabaseModelGenerationContext destinationContext = DatabaseModelGenerationContext.destination(
							getRelatedEtlConfiguration().getDataModel(), destinationConnection);

					destination.init(item, srcConn, destinationConnection);

					generate(destinationContext, destination);

					generateOnDemandConfigurationTrees(destination, operationConfig, srcConn,
					        destinationConnection);
				}
			}
		}

		if (item.hasChildItemConf()) {
			for (EtlItemConfiguration child : item.getChildItemConf()) {
				generateConfigurationTree(child, operationConfig, srcConn, dstConn);
			}
		}
	}

	private void generateOnDemandConfigurationTrees(DstConf destination, EtlOperationConfig operationConfig,
			Connection srcConn, Connection dstConn) throws DBException {

		if (!destination.hasMapping())
			return;

		destination.fullLoad(dstConn);

		for (FieldsMapping mapping : destination.getMapping()) {
			if (!mapping.hasTransformer())
				continue;
			mapping.tryToLoadTransformer(destination, srcConn);
			mapping.getTransformerInstance().init(srcConn, dstConn);
			mapping.getTransformerInstance().determineTransformerType();
			if (!mapping.getTransformerType().isParentOnDemand()
					&& !mapping.getTransformerType().isParentOnDemandWithDefaults())
				continue;

			ParentOnDemandLoadTransformer onDemand = (ParentOnDemandLoadTransformer) mapping.getTransformerInstance();
			onDemand.init(srcConn, dstConn);
			generateConfigurationTree(onDemand.getExistingParentItemConf(), operationConfig, srcConn, dstConn);
			generateConfigurationTree(onDemand.getOnDemandCreateParentItemConf(), operationConfig, srcConn, dstConn);
		}
	}

	private void generate(DatabaseModelGenerationContext context,
			EtlDatabaseObjectConfiguration objectConfiguration) {
		if (!utilities.stringHasValue(context.getPojoPackageName())) {
			throw new ForbiddenOperationException("No POJO package is configured for " + context.getSide());
		}

		String fullClassName = objectConfiguration.generateFullClassName(context);
		if (!generationVisitTracker.begin(fullClassName))
			return;

		try {
			if (!objectConfiguration.isFullLoaded())
				objectConfiguration.fullLoad(context.getConnection());

			objectConfiguration.generateRelatedPojoClass(context, true);
			persistPhysicalMetadata(context, objectConfiguration);
			generationVisitTracker.complete(fullClassName);

		} catch (DBException e) {
			generationVisitTracker.fail(fullClassName);
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			generationVisitTracker.fail(fullClassName);
			throw e;
		}

		if (objectConfiguration instanceof TableConfiguration) {
			TableConfiguration tabConf = (TableConfiguration) objectConfiguration;

			if (tabConf.hasParentRefInfo() && tabConf.autoGenerateDataModelForParents()) {
				for (ParentTable p : tabConf.getParentRefInfo()) {
					generate(context, p);
				}
			}
		}
	}

	private void persistPhysicalMetadata(DatabaseModelGenerationContext context,
			EtlDatabaseObjectConfiguration tableConfiguration) {
		if (!(tableConfiguration instanceof AbstractTableConfiguration))
			return;

		AbstractTableConfiguration table = (AbstractTableConfiguration) tableConfiguration;
		if (table.getPhysicalTableConfiguration() == null)
			return;

		try {
			PhysicalTableKey key = PhysicalTableKeyFactory.create(table,
					context.getDataModelId(), context.getConnection());
			if (!physicalMetadataVisitTracker.begin(key))
				return;

			try {
				table.synchronizePhysicalTableConfiguration();
				FilePhysicalTableMetadataRepository repository = new FilePhysicalTableMetadataRepository(
						getRelatedEtlConfiguration().getSchemaMetadataDirectory());
				PhysicalTableMetadata metadata = table.getPhysicalTableConfiguration().toMetadata(key);
				boolean saved = repository.save(metadata,
						getRelatedEtlConfiguration().shouldOverrideExistingDataModelElement());
				PhysicalTableMetadata persistedMetadata = saved ? metadata
						: repository.find(key).orElseThrow(() -> new IOException(
								"Existing physical metadata could not be loaded after it was preserved: " + key));
				new FileDatabaseModelManifestRepository(getRelatedEtlConfiguration().getSchemaMetadataDirectory())
						.record(new DatabaseModelManifest.Entry(key.toString(),
								tableConfiguration.generateFullClassName(context),
								PhysicalTableMetadataFingerprint.sha256(persistedMetadata)));
				physicalMetadataVisitTracker.complete(key);
			} catch (IOException | RuntimeException exception) {
				physicalMetadataVisitTracker.fail(key);
				throw exception;
			}
		} catch (IOException | java.sql.SQLException exception) {
			throw new RuntimeException("Could not persist physical metadata for " + table.getTableName(), exception);
		}
	}

	public boolean isDatabaseModelGenerated() {
		return databaseModelGenerated;
	}

	@Override
	public DatabaseModelGenerationController getRelatedOperationController() {
		return (DatabaseModelGenerationController) super.getRelatedOperationController();
	}

	@Override
	public TaskProcessor<DatabaseModelGenerationRecord> initReloadRecordsWithDefaultParentsTaskProcessor(
			IntervalExtremeRecord limits) {
		return null;
	}

	@Override
	public void transform(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
			EtlDatabaseObject parentMigratedRec, LoadingType loadingType, Connection srcConn, Connection dstConn)
			throws DBException {

		throw new ForbiddenOperationException("Unsupported method!");
	}
}
