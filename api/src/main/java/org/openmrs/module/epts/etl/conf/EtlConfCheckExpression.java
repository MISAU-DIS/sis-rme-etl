package org.openmrs.module.epts.etl.conf;

import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSourceField;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.types.EtlConfCheckType;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.MissingEtlConfException;

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
			EtlTransformTarget t = ((FieldsMapping) context).getTransformationTargetObject();

			EtlDataSource ds;

			if (t instanceof DstConf) {

				if (tryToSelectDsToRelatedConfiguration(tryToLocateDsWithinSrcConf((DstConf) t))) {
					return;
				}
			}

			ds = ((FieldsMapping) context).getTransformationTargetObject().getPrimaryDataSource();

			if (tryToSelectDsToRelatedConfiguration(tryToLocateDsWithinSrcConf(ds))) {
				return;
			}

		}

		if (context instanceof TransformableDataSourceField) {
			EtlDataSource ds = ((TransformableDataSourceField) context).getTransformationTargetObject()
			        .getPrimaryDataSource();

			if (tryToSelectDsToRelatedConfiguration(tryToLocateDsWithinSrcConf(ds))) {
				return;
			}

		}

		if (relatedConfiguration == null) {
			throw new MissingEtlConfException("The related Etl Conf cannot be found within the given context " + this);
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
		PrimaryDataSourceConfiguration primaryDataSource = null;

		if (ds instanceof DstConf) {
			EtlDataSource ds1 = ((DstConf) ds).findDataSource(this.confName);

			if (ds1 != null) {
				return ds1;
			}
		}

		if (ds instanceof EtlAdditionalDataSource) {
			primaryDataSource = ((EtlAdditionalDataSource) ds).getRelatedPrimaryDataSource();
		} else if (ds instanceof JdbcTablePrimaryDataSourceConfiguration) {
			primaryDataSource = (JdbcTablePrimaryDataSourceConfiguration) ds;
		} else if (ds instanceof DstConf) {
			primaryDataSource = ((DstConf) ds).getPrimaryDataSource();
		}

		if (primaryDataSource != null) {
			if (primaryDataSource.hasAlias() && primaryDataSource.getAlias().equals(this.confName)) {
				return primaryDataSource;
			}

			if (primaryDataSource.getParentSource() != null) {
				for (EtlAdditionalDataSource additional : primaryDataSource.getParentSource()
						.getAvailableAdditionalDataSources()) {
					if (additional.hasAlias() && additional.getAlias().equals(this.confName)) {
						return additional;
					}
				}
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "ETL_CONF_CHECK(confName:" + this.confName + ", operation:" + this.operation + ")";
	}

	public boolean allowMissingEtlConf() {
		return this.getOperation() == EtlConfCheckType.EXISTS || this.getOperation() == EtlConfCheckType.DOES_NOT_EXIST;
	}
}
