package org.openmrs.module.epts.etl.databasemodelgeneration.processor;

import java.sql.Connection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationItemResult;
import org.openmrs.module.epts.etl.databasemodelgeneration.controller.DatabaseModelGenerationController;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationRecord;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationSearchParams;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelManifest;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.FileDatabaseModelManifestRepository;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.conf.physical.FilePhysicalTableMetadataRepository;
import org.openmrs.module.epts.etl.conf.physical.JdbcPhysicalTableMetadataRepository;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableKey;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableKeyFactory;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableMetadata;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableMetadataFingerprint;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

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
public class DatabaseModelGenerationProcessor extends TaskProcessor<DatabaseModelGenerationRecord> {

	private List<String> alreadyGeneratedClasses;

	private boolean databaseModelGenerated;

	public DatabaseModelGenerationProcessor(Engine<DatabaseModelGenerationRecord> monitor, IntervalExtremeRecord limits,
			boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency);

		this.alreadyGeneratedClasses = new ArrayList<String>();
	}

	@Override
	public DatabaseModelGenerationSearchParams getSearchParams() {
		return (DatabaseModelGenerationSearchParams) super.getSearchParams();
	}

	@Override
	public void extractTransformAndLoad(boolean useMultiThreadSearch, Connection srcConn, Connection dstConn)
			throws DBException {
		this.getSearchParams().setProcessor(this);

		super.extractTransformAndLoad(false, srcConn, dstConn);
	}

	@Override
	public void transformAndLoad(List<DatabaseModelGenerationRecord> records, Connection srcConn, Connection dstConn)
			throws DBException {

		this.databaseModelGenerated = true;

		DBConnectionInfo mainApp = getEtlItemConfiguration().getSrcConnInfo();

		if (!getEtlItemConfiguration().isFullLoaded()) {
			getEtlItemConfiguration().fullLoad(this.getRelatedEtlOperationConfig());
		}

		generate(mainApp, getSrcConf());

		List<EtlAdditionalDataSource> allAvaliableDataSources = getEtlItemConfiguration().getSrcConf()
				.getAvaliableExtraDataSource();

		for (EtlAdditionalDataSource t : allAvaliableDataSources) {
			generate(mainApp, t);
		}

		DBConnectionInfo mappingAppInfo = null;

		if (getRelatedEtlConfiguration().hasDstConnInfo()) {
			mappingAppInfo = getRelatedEtlConfiguration().getDstConnInfo();

			for (DstConf map : getEtlItemConfiguration().getDstConf()) {
				map.setRelatedConnInfo(mappingAppInfo);

				generate(mappingAppInfo, map);

			}
		}

		getTaskResultInfo().addAllToRecordsWithNoError(EtlOperationItemResult.parseFromEtlDatabaseObject(records));

	}

	private void generate(DBConnectionInfo app, EtlDatabaseObjectConfiguration objectConfiguration) {
		if (!utilities.stringHasValue(app.getPojoPackageName())) {
			throw new ForbiddenOperationException("The connInfo " + app + " has no package name!");
		}

		String fullClassName = objectConfiguration.generateFullClassName(app);

		if (!isAlreadyGenerated(fullClassName)) {
			OpenConnection appConn = null;

			try {
				appConn = app.openConnection(this);

				objectConfiguration.generateRecordClass(app, true);
				persistPhysicalMetadata(app, objectConfiguration, appConn);

				this.alreadyGeneratedClasses.add(fullClassName);
			} catch (DBException e) {
				throw new RuntimeException(e);
			} finally {
				finalizeConnection(appConn);
			}
		}

		if (objectConfiguration instanceof TableConfiguration) {
			TableConfiguration tabConf = (TableConfiguration) objectConfiguration;

			if (tabConf.hasParentRefInfo()) {
				for (ParentTable p : tabConf.getParentRefInfo()) {
					generate(app, p);
				}
			}
		}
	}

	private void persistPhysicalMetadata(DBConnectionInfo app, EtlDatabaseObjectConfiguration tableConfiguration,
			Connection connection) {
		if (!(tableConfiguration instanceof AbstractTableConfiguration))
			return;

		AbstractTableConfiguration table = (AbstractTableConfiguration) tableConfiguration;
		if (table.getPhysicalTableConfiguration() == null)
			return;

		try {
			PhysicalTableKey key = PhysicalTableKeyFactory.create(table, app.getPojoPackageName(), connection);
			if (!table.getPhysicalTableConfiguration().areExportedForeignKeysLoaded()) {
				table.getPhysicalTableConfiguration().initializeExportedForeignKeys(
						new JdbcPhysicalTableMetadataRepository(connection).findExportedForeignKeys(key));
			}
			FilePhysicalTableMetadataRepository repository = new FilePhysicalTableMetadataRepository(
					getRelatedEtlConfiguration().getSchemaMetadataDirectory());
			PhysicalTableMetadata metadata = table.getPhysicalTableConfiguration().toMetadata(key);
			repository.save(metadata);
			new FileDatabaseModelManifestRepository(getRelatedEtlConfiguration().getSchemaMetadataDirectory()).record(
					new DatabaseModelManifest.Entry(key.toString(), tableConfiguration.generateFullClassName(app),
							PhysicalTableMetadataFingerprint.sha256(metadata)));
		} catch (IOException | java.sql.SQLException exception) {
			throw new RuntimeException("Could not persist physical metadata for " + table.getTableName(), exception);
		}
	}

	private boolean isAlreadyGenerated(String fullClassPath) {
		return this.alreadyGeneratedClasses.contains(fullClassPath);
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
		throw new ForbiddenOperationException("Forbiden Method");
	}

	@Override
	public void transform(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
			EtlDatabaseObject parentMigratedRec, LoadingType loadingType, Connection srcConn, Connection dstConn)
			throws DBException {

		throw new ForbiddenOperationException("Unsupported method!");
	}
}
