package org.openmrs.module.epts.etl.conf.datasource;

import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.etl.processor.transformer.FieldTransformingInfo;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public class PreparedQueryInfo extends AbstractEtlDataConfiguration {

	private final String query;

	private final List<FieldTransformingInfo> parameters;

	private final Object[] parametersValues;

	public PreparedQueryInfo(String query, List<FieldTransformingInfo> parameters) {
		this.query = query;
		this.parameters = parameters;

		if (utilities.listHasElement(parameters)) {
			this.parametersValues = new Object[parameters.size()];

			for (int i = 0; i < parameters.size(); i++) {
				this.parametersValues[i] = parameters.get(i).getTransformedValue();
			}
		} else
			this.parametersValues = null;

	}

	public String getQuery() {
		return query;
	}

	public List<FieldTransformingInfo> getParameters() {
		return parameters;
	}

	public Object[] extractParametersValueToArray() {
		return this.parametersValues;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return null;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return null;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}
}
