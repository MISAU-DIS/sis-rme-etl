package org.openmrs.module.epts.etl.model.pojo.generic;

import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
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
	public void beforeLoad(VO vo) {
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
	public void afterLoad(VO vo) {
		if (!(vo instanceof EtlDatabaseObject)) {
			throw new ForbiddenOperationException("This method is only applied to EtlDatabaseObject instances");
		}

		EtlDatabaseObject voAsEtlDatabaseObject = ((EtlDatabaseObject) vo);

		if (voAsEtlDatabaseObject.getRelatedConfiguration() instanceof TableConfiguration) {
			if (voAsEtlDatabaseObject.getSharedPkObj() != null) {
				this.afterLoad(voAsEtlDatabaseObject.getSharedPkObj());
			}

			voAsEtlDatabaseObject
					.loadUniqueKeyValues((TableConfiguration) voAsEtlDatabaseObject.getRelatedConfiguration());
			voAsEtlDatabaseObject
					.loadObjectIdData((TableConfiguration) voAsEtlDatabaseObject.getRelatedConfiguration());
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
