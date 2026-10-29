package org.openmrs.module.epts.etl.conf.datasource;

import java.util.Collections;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlField;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlSrcConf;

/**
 * A mutually exclusive primary source variant configured under {@code source}.
 */
public interface PrimaryDataSourceConfiguration extends EtlDataSource, EtlSrcConf {

	SourceConfiguration getParentSource();

	void setParentSource(SourceConfiguration parentSource);

	default Boolean doNotUseAsDatasource() {
		return Boolean.FALSE;
	}

	default List<EtlField> getEtlFields() {
		return EtlField.converteFromDataSourceFields(this, true);
	}

	default EtlField getEtlField(String fieldName, List<EtlDataSource> preferredDataSources, Boolean deepCheck) {
		for (EtlField field : getEtlFields()) {
			if ((field.getName().equals(fieldName)
			        || field.hasSrcField() && field.getSrcField().getName().equals(fieldName))
			        && matchesPreferredDataSource(field, preferredDataSources)) {
				return field;
			}
		}

		if (Boolean.TRUE.equals(deepCheck) && getParentSource() != null) {
			for (EtlDataSource dataSource : getParentSource().getAvailableAdditionalDataSources()) {
				if (dataSource.containsField(fieldName)
				        && (preferredDataSources == null || preferredDataSources.isEmpty()
				                || preferredDataSources.contains(dataSource))) {
					return EtlField.fastCreate(fieldName, dataSource, false);
				}
			}
		}

		return null;
	}

	default boolean matchesPreferredDataSource(EtlField field, List<EtlDataSource> preferredDataSources) {
		if (preferredDataSources == null || preferredDataSources.isEmpty()) {
			return true;
		}
		for (EtlDataSource preferred : preferredDataSources) {
			if (field.checkIfUsesSameDataSouce(preferred)) {
				return true;
			}
		}
		return false;
	}

	default List<UniqueKeyInfo> getUniqueKeys() {
		return Collections.emptyList();
	}

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
			throw new ForbiddenOperationException("Primary data source '" + getObjectName() + "' is not JDBC-backed");
		}
		return (SrcConf) this;
	}
}
