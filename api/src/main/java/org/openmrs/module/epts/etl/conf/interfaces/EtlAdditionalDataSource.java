package org.openmrs.module.epts.etl.conf.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents an additional data source used in the ETL process.
 * <p>
 * An additional data source is any data source configuration associated with
 * the primary ETL source, regardless of the connector technology. It can
 * represent:
 * <ul>
 * <li>A related table</li>
 * <li>A join with another dataset</li>
 * <li>A custom SQL query</li>
 * <li>A nested JSON record or another non-JDBC source</li>
 * </ul>
 * This interface defines how related data is fetched and linked to the main ETL
 * flow.
 */
public interface EtlAdditionalDataSource extends EtlDataSource, ConditionalEtlElement {

	ConnectorResourceConfiguration getResource();

	void setResource(ConnectorResourceConfiguration resource);

	@JsonIgnore
	ConnectorResource getResolvedResource();

	@JsonIgnore
	void setResolvedResource(ConnectorResource resource);

	default ActionOnEtlIssue onMultipleSrcObjectsFound() {
		return ActionOnEtlIssue.ABORT_PROCESS;
	}

	PrimaryDataSourceConfiguration getRelatedPrimaryDataSource();

	void setRelatedPrimaryDataSource(PrimaryDataSourceConfiguration primaryDataSource);

	/**
	 * Loads related source object based on the current data source configuration.
	 * <p>
	 * This method may use the provided list of already available source objects to
	 * assist in resolving relationships (e.g., joins or dependent queries).
	 *
	 * @param avaliableSrcObjects list of already loaded source objects that may be
	 *                            used as context
	 * @param reader              reader resolved for this data source resource
	 * @return the related {@link EtlDatabaseObject} instance
	 * @throws DBException if a database error occurs during execution
	 */
	EtlDatabaseObject loadRelatedSrcObject(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, ConnectorRecordReader<?> reader)
			throws DBException;

	/**
	 * Indicates whether this data source is mandatory.
	 * <p>
	 * If this data source is marked as required and no data is returned, the main
	 * destination record will be skipped (i.e., not written to the destination).
	 *
	 * @return {@code true} if the data source is required; {@code false} otherwise
	 */
	Boolean isRequired();

	@Override
	default Map<String, Object> retrieveAllAvailableTemplateParameters() {
		Map<String, Object> allParameters = new HashMap<>();

		// 1. Parameters inherited from the parent source configuration
		Map<String, Object> parentParameters =
				this.getRelatedPrimaryDataSource().retrieveAllAvailableTemplateParameters();

		if (parentParameters != null && !parentParameters.isEmpty()) {
			allParameters.putAll(parentParameters);
		}

		// 2. Parameters defined by this additional data source
		Map<String, Object> ownParameters =
				EtlDataSource.super.retrieveAllAvailableTemplateParameters();

		if (ownParameters != null && !ownParameters.isEmpty()) {
			allParameters.putAll(ownParameters);
		}

		// 3. Global ETL parameters defined in EtlConfiguration
		EtlConfiguration etlConfiguration =
				this.getRelatedPrimaryDataSource().getRelatedEtlConf();

		if (etlConfiguration != null
				&& etlConfiguration.getParams() != null
				&& !etlConfiguration.getParams().isEmpty()) {

			allParameters.putAll(etlConfiguration.getParams());
		}

		return allParameters;
	}
}
