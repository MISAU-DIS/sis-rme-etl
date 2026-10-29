package org.openmrs.module.epts.etl.conf.datasource;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Connector-neutral aggregate for one ETL item's source. Its primary source is
 * inferred from exactly one configured variant.
 */
public class SourceConfiguration extends AbstractEtlDataConfiguration {

	private ConnectorResourceConfiguration resource;

	private SrcConf jdbcTable;

	private JsonPrimaryDataSourceConfiguration json;

	private CsvPrimaryDataSourceConfiguration csv;

	private List<TableDataSourceConfig> extraTableDataSource;

	private List<QueryDataSourceConfig> extraQueryDataSource;

	private List<TransformableDataSource> extraObjectDataSource;

	private List<JsonDataSource> extraJsonDataSource;

	private SourceProcessingPolicy processingPolicy;

	@JsonIgnore
	private transient PrimaryDataSourceConfiguration primaryDataSource;

	@JsonIgnore
	private transient ConnectorResource resolvedResource;

	@JsonIgnore
	private transient EtlItemConfiguration parentItem;

	public ConnectorResourceConfiguration getResource() {
		return resource;
	}

	public void setResource(ConnectorResourceConfiguration resource) {
		this.resource = resource;
	}

	public SrcConf getJdbcTable() {
		return jdbcTable;
	}

	public void setJdbcTable(SrcConf jdbcTable) {
		this.jdbcTable = jdbcTable;
	}

	public JsonPrimaryDataSourceConfiguration getJson() {
		return json;
	}

	public void setJson(JsonPrimaryDataSourceConfiguration json) {
		this.json = json;
	}

	public CsvPrimaryDataSourceConfiguration getCsv() {
		return csv;
	}

	public void setCsv(CsvPrimaryDataSourceConfiguration csv) {
		this.csv = csv;
	}

	public List<TableDataSourceConfig> getExtraTableDataSource() {
		return extraTableDataSource;
	}

	public void setExtraTableDataSource(List<TableDataSourceConfig> extraTableDataSource) {
		this.extraTableDataSource = extraTableDataSource;
	}

	public List<QueryDataSourceConfig> getExtraQueryDataSource() {
		return extraQueryDataSource;
	}

	public void setExtraQueryDataSource(List<QueryDataSourceConfig> extraQueryDataSource) {
		this.extraQueryDataSource = extraQueryDataSource;
	}

	public List<TransformableDataSource> getExtraObjectDataSource() {
		return extraObjectDataSource;
	}

	public void setExtraObjectDataSource(List<TransformableDataSource> extraObjectDataSource) {
		this.extraObjectDataSource = extraObjectDataSource;
	}

	public List<JsonDataSource> getExtraJsonDataSource() {
		return extraJsonDataSource;
	}

	public void setExtraJsonDataSource(List<JsonDataSource> extraJsonDataSource) {
		this.extraJsonDataSource = extraJsonDataSource;
	}

	public SourceProcessingPolicy getProcessingPolicy() {
		return processingPolicy;
	}

	public void setProcessingPolicy(SourceProcessingPolicy processingPolicy) {
		this.processingPolicy = processingPolicy;
	}

	@JsonIgnore
	public PrimaryDataSourceConfiguration getPrimaryDataSource() {
		if (primaryDataSource == null) {
			throw new IllegalStateException("Primary data source has not been resolved; initialize source first");
		}
		return primaryDataSource;
	}

	@JsonIgnore
	public ConnectorResource getResolvedResource() {
		return resolvedResource;
	}

	@JsonIgnore
	public void setResolvedResource(ConnectorResource resolvedResource) {
		this.resolvedResource = resolvedResource;
	}

	public void initialize(EtlItemConfiguration parentItem) {
		if (isInitialized())
			return;
		this.parentItem = parentItem;
		applyIncludes();
		resolvePrimaryDataSource();
		markAsInitialized();
	}

	private void resolvePrimaryDataSource() {
		List<PrimaryDataSourceConfiguration> configured = new ArrayList<>();
		List<String> names = new ArrayList<>();
		add(configured, names, "jdbcTable", jdbcTable);
		add(configured, names, "json", json);
		add(configured, names, "csv", csv);

		if (configured.size() != 1) {
			throw new EtlConfException("source must define exactly one of [jdbcTable, json, csv]; configured: "
					+ names);
		}
		PrimaryDataSourceConfiguration resolvedPrimary = configured.get(0);
		if (resource == null && resolvedPrimary != jdbcTable) {
			throw new EtlConfException("source.resource is required");
		}
		validatePrimaryDataSource(resolvedPrimary);
		this.primaryDataSource = resolvedPrimary;
		this.primaryDataSource.setParentSource(this);
	}

	private void validatePrimaryDataSource(PrimaryDataSourceConfiguration resolvedPrimary) {
		if (resolvedPrimary == jdbcTable && jdbcTable.getResource() != null) {
			throw new EtlConfException(
					"source.jdbcTable.resource is not allowed; configure the common source.resource instead");
		}
		if (resolvedPrimary != jdbcTable && (resolvedPrimary.getFields() == null
				|| resolvedPrimary.getFields().isEmpty())) {
			throw new EtlConfException("source."
					+ (resolvedPrimary == json ? "json" : "csv") + ".fields is required");
		}
	}

	private void add(List<PrimaryDataSourceConfiguration> configured, List<String> names, String name,
			PrimaryDataSourceConfiguration candidate) {
		if (candidate != null) {
			configured.add(candidate);
			names.add(name);
		}
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return parentItem == null ? null : parentItem.getRelatedEtlConf();
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return parentItem;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}
}
