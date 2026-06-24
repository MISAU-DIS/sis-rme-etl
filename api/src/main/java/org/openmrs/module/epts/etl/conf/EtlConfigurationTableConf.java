package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.types.ConflictResolutionType;
import org.openmrs.module.epts.etl.etl.model.stage.EtlStageAreaObject;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Represents any table related to etl configuration. Ex:
 * "table_operation_progress_info", "inconsistence_info"
 */
public class EtlConfigurationTableConf extends AbstractTableConfiguration {

	private List<EtlConfigurationTableConf> parentTables;
	private EtlConfiguration relatedEtlConf;

	public EtlConfigurationTableConf(String tableName, EtlConfiguration relatedEtlConf) {
		super.setTableName(tableName);

		this.relatedEtlConf = relatedEtlConf;

		setSchema(getSyncStageSchema());

		setOnConflict(ConflictResolutionType.KEEP_EXISTING);

		setSyncRecordClass(EtlStageAreaObject.class);
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return relatedEtlConf;
	}

	public void setRelatedEtlConf(EtlConfiguration relatedEtlConf) {
		this.relatedEtlConf = relatedEtlConf;
	}

	public List<EtlConfigurationTableConf> getParentTables() {
		return parentTables;
	}

	public void setParentTables(List<EtlConfigurationTableConf> parentTables) {
		this.parentTables = parentTables;
	}

	@Override
	public Boolean isGeneric() {
		return false_();
	}

	@Override
	public Class<? extends EtlDatabaseObject> getSyncRecordClass(DBConnectionInfo connInfo)
			throws ForbiddenOperationException {
		return GenericDatabaseObject.class;
	}

	@Override
	public void fullLoad(Connection conn) throws DBException {
		setIgnorableFields(utilities.parseToList("creation_date"));

		super.fullLoad(conn);
	}

	@Override
	public void loadOwnElements(EtlDatabaseObject schemaInfo, Connection conn) throws DBException {

	}

	@Override
	public DBConnectionInfo getRelatedConnInfo() {
		return getSrcConnInfo();
	}

	@Override
	public void generateRecordClass(DBConnectionInfo app, Boolean fullClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tryToReplacePlaceholdersOnOwnElements(EtlDatabaseObject schemaInfoSrc) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return this.getTableAlias() != null ? this.getTableAlias() : getTableName();
	}

	@Override
	public String getQuery() {
		return null;
	}

	public boolean isSrcStageTable() {
		return this.getTableName().endsWith(STAGE_TABLE_SRC_SUFIX);
	}
}
