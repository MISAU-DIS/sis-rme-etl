package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.EtlChildItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.SQLUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Connector-neutral aggregate for one ETL item's source. Its primary source is
 * inferred from exactly one configured variant.
 */
public class SourceConfiguration extends AbstractEtlDataConfiguration {

	private String connector;

	private SourceResourceConfiguration resource;

	private SrcConf tableDataSource;

	private JsonPrimaryDataSourceConfiguration jsonDataSource;

	private CsvPrimaryDataSourceConfiguration csvDataSource;

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

	@JsonIgnore
	private transient boolean dataSourcesInitialized;

	@JsonIgnore
	private transient boolean fullLoaded;

	private ActionOnEtlIssue missingRequiredObjectBehavior;

	public ActionOnEtlIssue getMissingRequiredObjectBehavior() {
		return missingRequiredObjectBehavior;
	}

	public void setMissingRequiredObjectBehavior(ActionOnEtlIssue missingRequiredObjectBehavior) {
		this.missingRequiredObjectBehavior = missingRequiredObjectBehavior;
	}

	public ActionOnEtlIssue missingRequiredObjectBehavior() {
		return this.missingRequiredObjectBehavior;
	}

	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	public SourceResourceConfiguration getResource() {
		return resource;
	}

	public void setResource(SourceResourceConfiguration resource) {
		this.resource = resource;
	}

	public SrcConf getTableDataSource() {
		return tableDataSource;
	}

	public void setTableDataSource(SrcConf tableDataSource) {
		this.tableDataSource = tableDataSource;
	}

	public JsonPrimaryDataSourceConfiguration getJsonDataSource() {
		return jsonDataSource;
	}

	public void setJsonDataSource(JsonPrimaryDataSourceConfiguration jsonDataSource) {
		this.jsonDataSource = jsonDataSource;
	}

	public CsvPrimaryDataSourceConfiguration getCsvDataSource() {
		return csvDataSource;
	}

	public void setCsvDataSource(CsvPrimaryDataSourceConfiguration csvDataSource) {
		this.csvDataSource = csvDataSource;
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

		if (isInitialized()) {
			return;
		}

		this.parentItem = parentItem;

		applyIncludes();
		resolvePrimaryDataSource();
		markAsInitialized();
	}

	public synchronized void initializeDataSources(Connection srcConn, Connection dstConn) throws DBException {

		if (dataSourcesInitialized) {
			return;
		}

		if (!isInitialized()) {
			throw new IllegalStateException("SourceConfiguration must be structurally initialized first");
		}

		if (primaryDataSource instanceof SrcConf) {
			SrcConf jdbcSource = (SrcConf) primaryDataSource;

			if (!jdbcSource.doNotUseAsDatasource()) {
				jdbcSource.init(parentItem, srcConn, dstConn);

				getRelatedEtlConf().addConfiguredTable(jdbcSource);
			}
		}

		for (EtlAdditionalDataSource additional : getConfiguredAdditionalDataSources()) {
			initAdditionalDataSource(additional, srcConn, dstConn);
		}

		dataSourcesInitialized = true;
	}

	private void initAdditionalDataSource(EtlAdditionalDataSource additional, Connection srcConn, Connection dstConn)
			throws DBException {

		if (isConnectorNeutralAdditionalSource(additional)) {
			additional.setRelatedPrimaryDataSource(primaryDataSource);
			additional.init(primaryDataSource, parentItem.getRelatedEtlSchemaObject(), srcConn, dstConn);
		} else {
			SrcConf jdbcSource = this.requireJdbcPrimaryForAdditionalSources();
			additional.init(jdbcSource, parentItem.getRelatedEtlSchemaObject(), srcConn, dstConn);
			additional.setRelatedSrcConf(jdbcSource);
		}

		if (additional instanceof AbstractTableConfiguration) {
			TableConfiguration table = (TableConfiguration) additional;

			if (table.hasAlias()) {
				table.setUsingManualDefinedAlias(true);
				getRelatedEtlConf().tryToAddToBusyTableAliasName(table.getTableAlias());
			} else {
				table.tryToGenerateTableAlias(getRelatedEtlConf());
			}

			getRelatedEtlConf().addConfiguredTable((AbstractTableConfiguration) additional);
		}
	}

	private boolean isConnectorNeutralAdditionalSource(EtlAdditionalDataSource additional) {
		return additional instanceof TransformableDataSource || additional instanceof JsonDataSource
				|| additional instanceof JsonSimpleOutputDataSource
				|| additional instanceof JsonRecordDataSourceConfiguration;
	}

	public synchronized void fullLoad(Connection srcConn) throws DBException {

		if (fullLoaded) {
			return;
		}

		initializeDataSources(srcConn, null);

		primaryDataSource.fullLoad(srcConn);

		loadAdditionalDataSources(null, srcConn);

		if (primaryDataSource instanceof SrcConf) {
			loadEtlFields((SrcConf) primaryDataSource);
		}

		fullLoaded = true;
	}

	private void loadAdditionalDataSources(EtlDatabaseObject schemaInfo, Connection srcConn) throws DBException {

		if (!hasAdditionalDataSource()) {
			return;
		}

		SrcConf jdbcSource = primaryDataSource instanceof SrcConf ? (SrcConf) primaryDataSource : null;

		if (hasExtraTableDataSourceConfig()) {

			if (jdbcSource == null) {
				requireJdbcPrimaryForAdditionalSources();
			}

			for (TableDataSourceConfig table : extraTableDataSource) {
				TableConfiguration loaded = jdbcSource.findFullConfiguredConfInAllRelatedTable(table.getFullTableName(),
						new ArrayList<>());

				table.tryToGenerateTableAlias(getRelatedEtlConf());

				if (loaded != null) {
					table.clone(loaded, jdbcSource, null, srcConn);
				} else {
					table.fullLoad(srcConn);
				}

				table.setRelatedSrcConf(jdbcSource);

				if (table.useSharedPKKey()) {
					table.getSharedKeyRefInfo(srcConn).tryToGenerateTableAlias(getRelatedEtlConf());

					loaded = jdbcSource.findFullConfiguredConfInAllRelatedTable(
							table.getSharedKeyRefInfo(srcConn).getFullTableName(), new ArrayList<>());

					if (loaded != null) {
						table.getSharedKeyRefInfo(srcConn).clone(loaded, jdbcSource, null, srcConn);
					} else {
						table.getSharedKeyRefInfo(srcConn).fullLoad(srcConn);
					}
				}
			}
		}

		if (hasExtraQueryDataSourceConfig()) {

			if (jdbcSource == null) {
				requireJdbcPrimaryForAdditionalSources();
			}

			for (QueryDataSourceConfig query : extraQueryDataSource) {
				query.setRelatedSrcConf(jdbcSource);
				query.fullLoad(srcConn);
			}
		}

		if (hasExtraObjectDataSourceConfig()) {

			for (TransformableDataSource dataSource : extraObjectDataSource) {
				dataSource.setRelatedPrimaryDataSource(primaryDataSource);

				if (dataSource.hasObjectFields()) {

					for (DataSourceField field : dataSource.getObjectFields()) {
						field.setName(SQLUtilities.tryToReplaceParamsInQuery(field.getName().toString(), schemaInfo));

						if (field.hasValue() && schemaInfo != null) {
							field.setValue(
									SQLUtilities.tryToReplaceParamsInQuery(field.getValue().toString(), schemaInfo));
						}
					}
				}

				dataSource.fullLoad(srcConn);
			}
		}

		if (hasExtraJsonSourceConfig()) {

			for (JsonDataSource dataSource : extraJsonDataSource) {
				dataSource.setRelatedPrimaryDataSource(primaryDataSource);
				dataSource.fullLoad(srcConn);
			}
		}

		if (hasExtraJsonRecordDataSourceConfig()) {

			for (JsonRecordDataSourceConfiguration dataSource : getExtraJsonRecordDataSource()) {
				dataSource.setRelatedPrimaryDataSource(primaryDataSource);
				dataSource.fullLoad(srcConn);
			}
		}
	}

	private void loadEtlFields(SrcConf jdbcSource) {
		jdbcSource.setEtlFields(new ArrayList<>());

		boolean preserveOriginalNames = !hasAdditionalDataSource() && !jdbcSource.hasAuxExtractTable();

		jdbcSource.loadOwnFieldsToEtlFields(jdbcSource.getEtlFields(), preserveOriginalNames);

		if (!preserveOriginalNames) {

			for (EtlDataSource dataSource : getAvailableAdditionalDataSources()) {
				dataSource.loadOwnFieldsToEtlFields(jdbcSource.getEtlFields(), false);
			}
		}
	}

	private SrcConf requireJdbcPrimaryForAdditionalSources() {

		if (!(primaryDataSource instanceof SrcConf)) {
			throw new EtlConfException("extraTableDataSource and extraQueryDataSource require source.tableDataSource");
		}

		return (SrcConf) primaryDataSource;
	}

	@JsonIgnore
	public List<EtlAdditionalDataSource> getAvailableAdditionalDataSources() {
		List<EtlAdditionalDataSource> dataSources = new ArrayList<>();

		if (primaryDataSource instanceof SrcConf) {
			SrcConf jdbcSource = (SrcConf) primaryDataSource;

			if (jdbcSource.useSharedPKKey() && jdbcSource.isFullLoaded()) {
				dataSources.add((EtlAdditionalDataSource) jdbcSource.getSharedKeyRefInfo(null));
			}
		}

		if (hasExtraJsonSourceConfig()) {

			for (JsonDataSource jsonSource : extraJsonDataSource) {
				dataSources.add(jsonSource.getOutputDataSource());

				if (jsonSource.getOutputDataSource().hasParents()) {
					dataSources.addAll(jsonSource.getOutputDataSource().getParentOutputDataSource());
				}
			}
		}

		if (hasExtraTableDataSourceConfig()) {
			dataSources.addAll(extraTableDataSource);
		}

		if (hasExtraQueryDataSourceConfig()) {
			dataSources.addAll(extraQueryDataSource);
		}

		if (hasExtraObjectDataSourceConfig()) {
			dataSources.addAll(extraObjectDataSource);
		}

		if (hasExtraJsonRecordDataSourceConfig()) {
			dataSources.addAll(getExtraJsonRecordDataSource());
		}

		if (parentItem instanceof EtlChildItemConfiguration) {
			EtlItemConfiguration parent = ((EtlChildItemConfiguration) parentItem).getParentItemConf();

			if (parent != null && parent.getSource() != null) {
				dataSources.addAll(parent.getSource().getAvailableAdditionalDataSources());
			}
		}

		return dataSources;
	}

	@JsonIgnore
	public List<EtlAdditionalDataSource> getConfiguredAdditionalDataSources() {
		List<EtlAdditionalDataSource> configured = new ArrayList<>();

		if (hasExtraTableDataSourceConfig()) {
			configured.addAll(extraTableDataSource);
		}

		if (hasExtraQueryDataSourceConfig()) {
			configured.addAll(extraQueryDataSource);
		}

		if (hasExtraObjectDataSourceConfig()) {
			configured.addAll(extraObjectDataSource);
		}

		if (hasExtraJsonSourceConfig()) {
			configured.addAll(extraJsonDataSource);
		}

		if (hasExtraJsonRecordDataSourceConfig()) {
			configured.addAll(getExtraJsonRecordDataSource());
		}
		return configured;
	}

	@JsonIgnore
	public List<JsonRecordDataSourceConfiguration> getExtraJsonRecordDataSource() {
		return jsonDataSource == null || jsonDataSource.getExtraJsonRecordDataSource() == null
				? java.util.Collections.<JsonRecordDataSourceConfiguration>emptyList()
				: jsonDataSource.getExtraJsonRecordDataSource();
	}

	public boolean hasExtraJsonRecordDataSourceConfig() {
		return utilities.listHasElement(getExtraJsonRecordDataSource());
	}

	public boolean hasExtraTableDataSourceConfig() {
		return utilities.listHasElement(extraTableDataSource);
	}

	public boolean hasExtraQueryDataSourceConfig() {
		return utilities.listHasElement(extraQueryDataSource);
	}

	public boolean hasExtraObjectDataSourceConfig() {
		return utilities.listHasElement(extraObjectDataSource);
	}

	public boolean hasExtraJsonSourceConfig() {
		return utilities.listHasElement(extraJsonDataSource);
	}

	public boolean hasAdditionalDataSource() {
		return utilities.listHasElement(getConfiguredAdditionalDataSources());
	}

	@JsonIgnore
	public boolean isFullLoaded() {
		return fullLoaded;
	}

	public List<Field> generateAllAvailableFields() {
		List<Field> fields = new ArrayList<>();

		if (primaryDataSource.getFields() != null) {
			fields.addAll(primaryDataSource.getFields());
		}

		for (TableDataSourceConfig table : hasExtraTableDataSourceConfig() ? extraTableDataSource
				: java.util.Collections.<TableDataSourceConfig>emptyList()) {

			for (Field field : table.getFields()) {

				if (!fields.contains(field)) {
					fields.add(field);
				}
			}
		}
		return fields;
	}

	private void resolvePrimaryDataSource() {
		List<PrimaryDataSourceConfiguration> configured = new ArrayList<>();
		List<String> names = new ArrayList<>();
		add(configured, names, "tableDataSource", tableDataSource);
		add(configured, names, "jsonDataSource", jsonDataSource);
		add(configured, names, "csvDataSource", csvDataSource);

		if (configured.size() != 1) {
			throw new EtlConfException(
					"source must define exactly one of [tableDataSource, jsonDataSource, csvDataSource]; configured: "
							+ names);
		}
		PrimaryDataSourceConfiguration resolvedPrimary = configured.get(0);
		validatePrimaryDataSource(resolvedPrimary);
		this.primaryDataSource = resolvedPrimary;
		this.primaryDataSource.setParentSource(this);
	}

	private void validatePrimaryDataSource(PrimaryDataSourceConfiguration resolvedPrimary) {

		if (resolvedPrimary == tableDataSource && tableDataSource.getResource() != null) {
			throw new EtlConfException(
					"source.tableDataSource.resource is not allowed; configure source.resource instead");
		}

		if (resource != null && utilities.stringHasValue(resource.getConnector())) {

			if (utilities.stringHasValue(connector) && !connector.equals(resource.getConnector())) {
				throw new EtlConfException("source.connector differs from source.resource.connector");
			}
			connector = resource.getConnector();
		}

		if (resolvedPrimary != tableDataSource && resource == null) {
			throw new EtlConfException("source.resource is required for non-JDBC primary data sources");
		}

		if (resolvedPrimary != tableDataSource && !utilities.stringHasValue(resolvedPrimary.getName())) {
			throw new EtlConfException("The logical primary data source name is required");
		}

		if (resource != null && !utilities.stringHasValue(resource.getName())) {
			throw new EtlConfException("source.resource.name is required");
		}

		if (resolvedPrimary != tableDataSource
				&& (resolvedPrimary.getFields() == null || resolvedPrimary.getFields().isEmpty())) {
			throw new EtlConfException("source."
					+ (resolvedPrimary == jsonDataSource ? "jsonDataSource" : "csvDataSource") + ".fields is required");
		}

		if (resolvedPrimary == jsonDataSource) {
			validateJsonDataSources();
		}
	}

	private void validateJsonDataSources() {
		String recordPath = resource.normalizedRecordPath();

		if (!"/".equals(recordPath) && !recordPath.startsWith("/")) {
			throw new EtlConfException("source.resource.recordPath must use JSON Pointer syntax");
		}
		Set<String> names = new java.util.HashSet<>();
		names.add(jsonDataSource.getName().trim().toLowerCase(java.util.Locale.ROOT));

		for (JsonRecordDataSourceConfiguration extra : getExtraJsonRecordDataSource()) {

			if (extra == null || !utilities.stringHasValue(extra.getName())) {
				throw new EtlConfException("Every extraJsonRecordDataSource must have a name");
			}

			if (!names.add(extra.getName().trim().toLowerCase(java.util.Locale.ROOT))) {
				throw new EtlConfException("Duplicated JSON data source name '" + extra.getName() + "'");
			}

			if (!utilities.stringHasValue(extra.getPath()) || "/".equals(extra.getPath().trim())
					|| !extra.getPath().trim().startsWith("/")) {
				throw new EtlConfException("extraJsonRecordDataSource '" + extra.getName()
						+ "' must define a child path using JSON Pointer syntax");
			}

			if (extra.getFields() == null || extra.getFields().isEmpty()) {
				throw new EtlConfException("extraJsonRecordDataSource '" + extra.getName() + "' must define fields");
			}
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
