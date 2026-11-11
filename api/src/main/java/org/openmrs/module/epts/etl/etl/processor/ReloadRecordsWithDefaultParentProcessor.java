package org.openmrs.module.epts.etl.etl.processor;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReadersAggregator;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.controller.EtlController;
import org.openmrs.module.epts.etl.etl.model.EtlDatabaseObjectSearchParams;
import org.openmrs.module.epts.etl.etl.processor.transformer.TransformationType;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.RecordNotFoundException;
import org.openmrs.module.epts.etl.inconsistenceresolver.model.InconsistenceInfo;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.RecordWithDefaultParentInfo;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Represents a generic processor for ETL operation
 * 
 * @author jpboane
 */
public class ReloadRecordsWithDefaultParentProcessor extends EtlProcessor {

	private EtlProcessor relatedEtlProcessor;

	public ReloadRecordsWithDefaultParentProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits,
			boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency);
	}

	public EtlProcessor getRelatedEtlProcessor() {
		return relatedEtlProcessor;
	}

	public void setRelatedEtlProcessor(EtlProcessor relatedEtlProcessor) {
		this.relatedEtlProcessor = relatedEtlProcessor;
	}

	public EtlItemConfiguration getRelatedEtlItemConfiguration() {
		return relatedEtlProcessor.getEtlItemConfiguration();
	}

	@Override
	public AbstractEtlSearchParams<EtlDatabaseObject> getSearchParams() {
		return (EtlDatabaseObjectSearchParams) super.getSearchParams();
	}

	public DBConnectionInfo getDstConnInfo() {
		return this.getRelatedOperationController().getDstConnInfo();
	}

	public DBConnectionInfo getSrcConnInfo() {
		return this.getRelatedOperationController().getSrcConnInfo();
	}

	@Override
	public EtlController getRelatedOperationController() {
		return (EtlController) super.getRelatedOperationController();
	}

	@Override
	public void transformAndLoad(List<EtlDatabaseObject> etlObjects, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers) throws DBException {

		Connection srcConn = readers.getPrimary().unwrap(Connection.class);
		Connection dstConn = writers.unwrapShared(Connection.class);

		try {

			PrimaryDataSourceConfiguration primarySource = getRelatedEtlItemConfiguration().getPrimaryDataSource();

			if (!primarySource.isFullLoaded()) {
				primarySource.fullLoad(srcConn);
			}

			for (EtlDatabaseObject etlObj : etlObjects) {
				EtlDatabaseObject dstObject = this.reloadDefaultsParents(primarySource, etlObj, readers, writers);

				if (dstObject != null) {
					dstObject.update((TableConfiguration) dstObject.getRelatedConfiguration(), dstConn);
				}
			}
		} catch (Exception e) {
			logWarn("Error ocurred on thread " + getProcessorId() + " On Records [" + getLimits() + "]... \n");
			logError(e.getLocalizedMessage(), e);

			getTaskResultInfo().setFatalException(e);
		}

	}

	private EtlDatabaseObject reloadDefaultsParents(PrimaryDataSourceConfiguration primarySource,
			EtlDatabaseObject etlObj, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers) throws DBException, ForbiddenOperationException {

		ConnectorRecordReader<EtlDatabaseObject> reader = null;
		ConnectorRecordWriter<EtlDatabaseObject> writer = null;

		Connection srcConn = readers.getEtlSrcConn();
		Connection dstConn = null;

		List<RecordWithDefaultParentInfo> rcs = RecordWithDefaultParentInfo.getAllOfSrcRecord(primarySource,
				etlObj.getObjectId().asSimpleNumericValue(), srcConn);

		EtlDatabaseObject srcObject = null;
		EtlDatabaseObject dstObject = null;

		for (RecordWithDefaultParentInfo recWithDefaultParentInfo : rcs) {

			if (srcObject != null) {
				recWithDefaultParentInfo.setSrcRelatedObject(srcObject);
				recWithDefaultParentInfo.setDstRelatedObject(dstObject);
			}

			try {
				recWithDefaultParentInfo.fullLoad(this.getRelatedEtlItemConfiguration(), reader, writer);
			} catch (RecordNotFoundException e) {
				logWarn("A issue happened while processing record with default values: " + e.getLocalizedMessage());

				recWithDefaultParentInfo.setFieldValue("status", "ERR");
				recWithDefaultParentInfo.setFieldValue("last_err", e.getLocalizedMessage());

				recWithDefaultParentInfo.save((TableConfiguration) recWithDefaultParentInfo.getRelatedConfiguration(),
						srcConn);

				continue;
			} catch (Exception e) {
				throw e;
			}

			srcObject = recWithDefaultParentInfo.getSrcRelatedObject();
			dstObject = recWithDefaultParentInfo.getDstRelatedObject();

			ParentTable parentRefInfo = recWithDefaultParentInfo.getParentRefInfo();

			List<PrimaryDataSourceConfiguration> availableSourcesForCurrentParent = parentRefInfo
					.findRelatedPrimaryDataSourcesWithMatchingDestination(getRelatedEtlOperationConfig());

			if (utilities.listHasNoElement(availableSourcesForCurrentParent)) {
				throw new ForbiddenOperationException(
						"There are relashioship which cannot auto resolved as there is no configured etl for "
								+ parentRefInfo.getTableName() + " as source and destination!");
			}

			EtlDatabaseObject dstParent = null;
			EtlDatabaseObject parentAsSrc = null;

			for (PrimaryDataSourceConfiguration source : availableSourcesForCurrentParent) {
				EtlDestinationConfiguration dst = source.getParentItemConfiguration()
						.findDestination(parentRefInfo.getTableName());

				dstConn = writers.get(dst.getResolvedResource()).unwrap(Connection.class);

				parentAsSrc = source.newInstance();
				parentAsSrc.setRelatedConfiguration(source);

				parentAsSrc.copyFrom(recWithDefaultParentInfo.getParentRecordInOrigin());

				dstParent = dst.getTransformerInstance().transform(this, parentAsSrc, dst, null,
						TransformationType.INNER, reader, writer);

				if (dstParent != null) {
					DBException exception = null;
					parentAsSrc.addDestinationRecord(dstParent);

					try {
						EtlLoadHelper.performeParentLoading(parentAsSrc, readers, writers);

						dstParent = parentAsSrc.getDestinationObjects().get(0);

						dstObject.changeParentValue(recWithDefaultParentInfo.getParentRefInfo(), dstParent);
					} catch (DBException e) {

						if (e.isDuplicatePrimaryOrUniqueKeyException()) {
							dstParent = DatabaseObjectDAO.getByUniqueKeys(dstParent, dstConn);
							dstObject.changeParentValue(recWithDefaultParentInfo.getParentRefInfo(), dstParent);
						} else
							throw new EtlExceptionImpl(e);
					} finally {

						if (dstParent.getEtlInfo() != null && dstParent.getEtlInfo().hasParentsWithDefaultValues()
								|| (exception != null && exception.isIntegrityConstraintViolationException())) {

							String msg = "The parent for default for parent ["
									+ recWithDefaultParentInfo.getParentRecordInOrigin()
									+ "] could not be loaded. The dstRecord [" + dstObject + "]";

							logDebug(msg);

							InconsistenceInfo incInfo = InconsistenceInfo.generate(dstObject, parentRefInfo,
									primarySource.getParentItemConfiguration().getOriginAppLocationCode());

							incInfo.save(primarySource.requireJdbc(), srcConn);

							recWithDefaultParentInfo.setAsInconsistent(srcConn);
						}
					}

					break;
				}

			}
		}

		return dstObject;
	}

	@Override
	public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(
			IntervalExtremeRecord limits) {
		throw new ForbiddenOperationException("This method is forbidden");
	}
}
