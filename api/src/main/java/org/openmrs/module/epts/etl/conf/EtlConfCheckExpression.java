package org.openmrs.module.epts.etl.conf;

import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.types.EtlConfCheckType;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;

public class EtlConfCheckExpression {

	private String confName;

	private EtlConfCheckType operation;

	private String field;

	private EtlDataConfiguration relatedConfiguration;

	public String getConfName() {
		return confName;
	}

	public void setConfName(String confName) {
		this.confName = confName;
	}

	public void setField(String field) {
		this.field = field;
	}

	public EtlConfCheckType getOperation() {
		return operation;
	}

	public void setOperation(EtlConfCheckType operation) {
		this.operation = operation;
	}

	public String getField() {
		return field;
	}

	public EtlDataConfiguration getRelatedConfiguration() {
		return relatedConfiguration;
	}

	public void init(EtlDataConfiguration context) {

		if (context == null)
			throw new EtlConfException("The context is not set");

		if (context instanceof EtlDataSource) {
			EtlDataSource ds = (EtlDataSource) context;

			if (tryToSelectDsToRelatedConfiguration(ds)) {
				return;
			}

			ds = tryToLocateDsWithinSrcConf(ds);

			if (tryToSelectDsToRelatedConfiguration(ds)) {
				return;
			}
		}

		if (context instanceof FieldsMapping) {
			SrcConf ds = ((FieldsMapping) context).getTransformationTargetObject().getSrcConf();

			if (tryToSelectDsToRelatedConfiguration(tryToLocateDsWithinSrcConf(ds))) {
				return;
			}

		}
		if (relatedConfiguration == null) {
			throw new EtlConfException("The related Etl Conf cannot be found within the given context " + this);
		}
	}

	private boolean tryToSelectDsToRelatedConfiguration(EtlDataSource ds) {
		if (ds != null && ds.hasAlias() && ds.getAlias().equals(this.confName)) {
			relatedConfiguration = ds;
			return true;
		}

		return false;
	}

	private EtlDataSource tryToLocateDsWithinSrcConf(EtlDataSource ds) {
		SrcConf srcConf = null;

		if (ds instanceof EtlAdditionalDataSource) {
			srcConf = ((EtlAdditionalDataSource) ds).getRelatedSrcConf();
		} else if (ds instanceof SrcConf) {
			srcConf = (SrcConf) ds;
		} else if (ds instanceof DstConf) {
			srcConf = ((DstConf) ds).getSrcConf();
		}

		try {
			EtlDataSource ds1 = srcConf.findDataSourceOnAllAvaliabeDatasources(this.confName);

			if (ds1 != null) {
				return ds1;
			}
		} catch (DatabaseResourceDoesNotExists e) {
		}

		return null;
	}

	@Override
	public String toString() {
		return "ETL_CONF_CHECK(confName:" + this.confName + ", operation:" + this.operation + ")";
	}
}
