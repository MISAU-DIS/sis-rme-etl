package org.openmrs.module.epts.etl.conf.datasource;

import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;

/** A mutually exclusive primary source variant configured under {@code source}. */
public interface PrimaryDataSourceConfiguration extends EtlDatabaseObjectConfiguration {

	SourceConfiguration getParentSource();

	void setParentSource(SourceConfiguration parentSource);

	default ConnectorResourceConfiguration getSourceResource() {
		return getParentSource() == null ? null : getParentSource().getResource();
	}

	default String getName() {
		return getObjectName();
	}

	default EtlItemConfiguration getParentItemConfiguration() {
		return getParentSource() == null ? null : (EtlItemConfiguration) getParentSource().getParentConf();
	}

	default boolean isJdbc() {
		return this instanceof SrcConf;
	}

	default SrcConf requireJdbc() {
		if (!(this instanceof SrcConf)) {
			throw new ForbiddenOperationException("Primary data source '" + getObjectName()
					+ "' is not JDBC-backed");
		}
		return (SrcConf) this;
	}
}
