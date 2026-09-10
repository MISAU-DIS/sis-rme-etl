package org.openmrs.module.epts.etl.model.pojo.generic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.openmrs.module.epts.etl.conf.interfaces.JoinableEntity;
import org.openmrs.module.epts.etl.conf.interfaces.MainJoiningEntity;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.base.VO;
import org.openmrs.module.epts.etl.model.base.VOLoaderHelper;

public class DatabaseObjectLoaderHelper implements VOLoaderHelper {

	private EtlDatabaseObjectConfiguration tableConf;

	public DatabaseObjectLoaderHelper(EtlDatabaseObjectConfiguration tableConf) {
		this.tableConf = tableConf;
	}

	@Override
	public void beforeLoad(ResultSet rs, VO vo) {
		if (!(vo instanceof EtlDatabaseObject)) {
			throw new ForbiddenOperationException("This method is only applied to EtlDatabaseObject instances");
		}

		EtlDatabaseObject databaseObject = (EtlDatabaseObject) vo;
		databaseObject.setRelatedConfiguration(this.tableConf);

		if (this.tableConf instanceof TableConfiguration) {
			TableConfiguration tableConfiguration = (TableConfiguration) this.tableConf;

			if (tableConfiguration.useSharedPKKey()) {
				if (databaseObject.getSharedPkObj() == null) {
					throw new ForbiddenOperationException(
							"The shared PK object for " + tableConfiguration.getTableName() + " was not initialized");
				}

				databaseObject.getSharedPkObj()
						.setRelatedConfiguration(resolveLoadedSharedPkConfiguration(tableConfiguration));
			}
		}
	}

	private ParentTable resolveLoadedSharedPkConfiguration(TableConfiguration tableConfiguration) {
		if (tableConfiguration.hasParentRefInfo()) {
			for (ParentTable parent : tableConfiguration.getParentRefInfo()) {
				if (parent.getTableName().equalsIgnoreCase(tableConfiguration.getSharePkWith()))
					return parent;
			}
		}

		throw new ForbiddenOperationException("The shared PK table " + tableConfiguration.getSharePkWith() + " of "
				+ tableConfiguration.getTableName() + " is not present in the loaded parent relationships");
	}

	@Override
	public void afterLoad(ResultSet rs, VO vo) throws SQLException {
		if (!(vo instanceof EtlDatabaseObject)) {
			throw new ForbiddenOperationException("This method is only applied to EtlDatabaseObject instances");
		}

		if (!(vo instanceof EtlDatabaseObject)) {
			throw new ForbiddenOperationException("This method is only applied to EtlDatabaseObject instances");
		}

		EtlDatabaseObject voAsEtlDatabaseObject = ((EtlDatabaseObject) vo);

		if (voAsEtlDatabaseObject.getRelatedConfiguration() instanceof TableConfiguration) {
			if (voAsEtlDatabaseObject.getSharedPkObj() != null) {
				this.afterLoad(rs, voAsEtlDatabaseObject.getSharedPkObj());
			}

			voAsEtlDatabaseObject
					.loadUniqueKeyValues((TableConfiguration) voAsEtlDatabaseObject.getRelatedConfiguration());
			voAsEtlDatabaseObject
					.loadObjectIdData((TableConfiguration) voAsEtlDatabaseObject.getRelatedConfiguration());

			if (voAsEtlDatabaseObject.getSharedPkObj() != null && voAsEtlDatabaseObject.getUuid() == null
					&& voAsEtlDatabaseObject.getSharedPkObj().getUuid() != null) {
				voAsEtlDatabaseObject.setUuid(voAsEtlDatabaseObject.getSharedPkObj().getUuid());
			}
		}

		loadAuxiliaryObjects(voAsEtlDatabaseObject, rs);
	}

	private void loadAuxiliaryObjects(EtlDatabaseObject databaseObject, ResultSet rs) throws SQLException {
		if (!(databaseObject.getRelatedConfiguration() instanceof MainJoiningEntity))
			return;

		MainJoiningEntity mainJoiningEntity = (MainJoiningEntity) databaseObject.getRelatedConfiguration();
		if (!mainJoiningEntity.hasAuxExtractTable())
			return;

		databaseObject.setAuxLoadObject(new ArrayList<>());
		for (JoinableEntity auxiliaryConfiguration : mainJoiningEntity.getJoiningTable()) {
			if (auxiliaryConfiguration.doNotUseAsDatasource())
				continue;

			EtlDatabaseObject auxiliaryObject = auxiliaryConfiguration.createRecordInstance();

			DatabaseObjectLoaderHelper auxiliaryLoader = new DatabaseObjectLoaderHelper(auxiliaryConfiguration);

			auxiliaryLoader.beforeLoad(rs, auxiliaryObject);

			auxiliaryObject.load(rs);

			auxiliaryLoader.afterLoad(rs, auxiliaryObject);

			databaseObject.getAuxLoadObject().add(auxiliaryObject);
		}
	}

	@Override
	public String toString() {
		return this.tableConf.toString();
	}

	public void setTableConf(EtlDatabaseObjectConfiguration tableConf) {
		this.tableConf = tableConf;
	}

	public EtlDatabaseObjectConfiguration getTableConf() {
		return tableConf;
	}
}
