package org.openmrs.module.epts.etl.data.validation.missingrecords.model;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.JdbcTableDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.types.DbmsType;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.etl.model.EtlDatabaseObjectSearchParams;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.SearchClauses;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

public class DetectMissingRecordsSearchParams extends EtlDatabaseObjectSearchParams {

	JdbcTableDestinationConfiguration relatedDstConf;

	public DetectMissingRecordsSearchParams(Engine<EtlDatabaseObject> relatedEngine,
			ThreadRecordIntervalsManager<EtlDatabaseObject> limits) {

		super(relatedEngine.getJdbcPrimaryDataSource(), limits);

		this.excludedRecords = null;

		this.relatedDstConf = new JdbcTableDestinationConfiguration();
		this.relatedDstConf.setTableName(getJdbcPrimaryDataSource().getTableName());
		this.relatedDstConf.setParentConf(relatedEngine.getEtlItemConfiguration());

		OpenConnection dstConn = null;

		try {
			dstConn = relatedEngine.tryToOpenDstConn(this);

			this.relatedDstConf.fullLoad(dstConn);
		} catch (DBException e) {
			throw new RuntimeException(e);
		} finally {
			finalizeConnection(dstConn);
		}

	}

	@Override
	public SearchClauses<EtlDatabaseObject> generateSearchClauses(IntervalExtremeRecord intervalExtremeRecord,
			EtlDatabaseObject parentObject, List<EtlDatabaseObject> auxDataSourceObjects, Connection srcConn,
			Connection dstConn) throws DBException {

		JdbcTablePrimaryDataSourceConfiguration srcConfig = this.getJdbcPrimaryDataSource();

		SearchClauses<EtlDatabaseObject> searchClauses = new SearchClauses<EtlDatabaseObject>(this);

		searchClauses.addColumnToSelect("distinct " + srcConfig.generateFullAliasedSelectColumns() + "\n");

		String clauseFrom = srcConfig.generateSelectFromClauseContent();

		// To avoid slowness, don't exclude if there is no limit
		if (hasLimits()) {
			clauseFrom += " LEFT JOIN " + relatedDstConf.generateSelectFromClauseContent() + " ON "
					+ relatedDstConf.generateJoinConditionWithSrc();

			searchClauses.addToClauseFrom(clauseFrom);

			String extraCondition = "";

			for (FieldsMapping uk : relatedDstConf.getJoinFields()) {

				if (!extraCondition.isEmpty()) {
					extraCondition += " OR ";
				}

				extraCondition += this.relatedDstConf.getAlias() + "." + uk.getDstField() + " is null";
			}

			searchClauses.addToClauses(extraCondition);

			tryToAddLimits(intervalExtremeRecord, searchClauses);
		} else {
			searchClauses.addToClauseFrom(clauseFrom);
		}

		tryToAddExtraConditionForExport(searchClauses, parentObject, auxDataSourceObjects,
				DbmsType.determineFromConnection(srcConn),
				org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRuntimeAdapters.reader(srcConn),
				org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRuntimeAdapters.writer(dstConn));

		if (utilities.stringHasValue(getExtraCondition())) {
			searchClauses.addToClauses(getExtraCondition());
		}

		return searchClauses;
	}
}
