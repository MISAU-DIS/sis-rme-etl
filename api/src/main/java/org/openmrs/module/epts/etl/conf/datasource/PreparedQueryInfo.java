package org.openmrs.module.epts.etl.conf.datasource;

import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.etl.processor.transformer.FieldTransformingInfo;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public class PreparedQueryInfo extends AbstractEtlDataConfiguration {

	private EtlConfiguration relateConfiguration;
	private String originalQuery;
	private final String preparedQuery;

	private final List<FieldTransformingInfo> parameters;

	private final Object[] parametersValues;

	public PreparedQueryInfo(String preparedQuery, String originalQuery, EtlConfiguration relateConfiguration,
			List<FieldTransformingInfo> parameters) {

		this.preparedQuery = preparedQuery;
		this.parameters = parameters;
		this.relateConfiguration = relateConfiguration;

		if (relateConfiguration == null)
			throw new ForbiddenOperationException(
					"Empty relateConfiguration was provided for PreparedQueryInfo (" + originalQuery + "");

		if (utilities.listHasElement(parameters)) {
			this.getRelatedEtlConf().trace(
					"Initializing PreparedQueryInfo for original query={}, prepared query={}, params={}", originalQuery,
					preparedQuery, parameters);

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

	public String getOriginalQuery() {
		return originalQuery;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.relateConfiguration;
	}

	private int determineQtyOfFullParams() {
		if (!this.hasParams())
			return 0;

		int qty = 0;

		for (FieldTransformingInfo p : this.parameters) {

			if (p == null) {
				this.getRelatedEtlConf().err(
						"Error while initializing PreparedQueryInfo for original query={}, prepared query={}, params={}",
						this.originalQuery, this.preparedQuery, this.parameters);

				throw new EtlExceptionImpl("Error while initializing PreparedQueryInfo: " + this);
			} else {
				qty += determineQtyElementsWithinTheParamValue(p.getTransformedValue());
			}
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

		if (transformedValue == null) {
			Object[] params = { transformedValue };

			return params;
		}

		return utilities.parseObjectToArray(transformedValue);
	}

	public String getPreparedQuery() {
		return preparedQuery;
	}

	public List<FieldTransformingInfo> getParameters() {
		return parameters;
	}

	public Object[] extractParametersValueToArray() {
		return this.parametersValues;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return null;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append(getClass().getSimpleName()).append(" {\n");

		sb.append("  originalQuery = ").append(originalQuery).append("\n");

		sb.append("  preparedQuery = ").append(preparedQuery).append("\n");

		sb.append("  parameters = ");

		if (parameters == null || parameters.isEmpty()) {
			sb.append("[]");
		} else {
			sb.append("[\n");

			for (FieldTransformingInfo parameter : parameters) {
				sb.append("    - ").append(parameter).append("\n");
			}

			sb.append("  ]");
		}

		sb.append("\n}");

		return sb.toString();
	}
}
