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
			this.parametersValues = new Object[this.determineQtyOfFullParams()];

			int j = 0;
			for (int i = 0; i < parameters.size(); i++) {

				Object[] paramsWithinParam = determineParamsWithinParam(parameters.get(i).getTransformedValue());

				for (int k = 0; k < paramsWithinParam.length; k++, j++) {
					this.parametersValues[j] = paramsWithinParam[k];
				}
			}
		} else
			this.parametersValues = null;

	}

	private int determineQtyOfFullParams() {
		if (!this.hasParams())
			return 0;

		int qty = 0;

		for (FieldTransformingInfo p : this.parameters) {
			qty += determineQtyElementsWithinTheParamValue(p.getTransformedValue());
		}

		return qty;
	}

	private boolean hasParams() {
		return utilities.listHasElement(this.parameters);
	}

	public static int determineQtyElementsWithinTheParamValue(Object paramValue) {
		if (paramValue == null || !(paramValue instanceof String))
			return 1;

		String[] valueParts = paramValue.toString().split("\\,");

		for (String p : valueParts) {
			if (!utilities.isNumeric(p)) {
				return 1;
			}
		}

		return valueParts.length;
	}

	private Object[] determineParamsWithinParam(Object transformedValue) {

		if (determineQtyElementsWithinTheParamValue(transformedValue) > 1) {
			return transformedValue.toString().split("\\,");
		}

		return utilities.parseObjectToArray(transformedValue);
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
