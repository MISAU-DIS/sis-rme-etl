package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.datasource.EtlItemSrcConf;
import org.openmrs.module.epts.etl.conf.datasource.EtlSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonDataSource;
import org.openmrs.module.epts.etl.conf.datasource.JsonMainOutputDataSource;
import org.openmrs.module.epts.etl.conf.datasource.JsonSimpleOutputDataSource;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.QueryDataSourceConfig;
import org.openmrs.module.epts.etl.conf.datasource.SourceProcessingPolicy;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.AutoIncrementHandlingType;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.conf.types.ParallelProcessingStrategyType;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReaderFactory;
import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReadersAggregator;
import org.openmrs.module.epts.etl.connector.capability.reader.ResourceBoundConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWriterFactory;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.connector.conf.ConnectorConfigurationResolver;
import org.openmrs.module.epts.etl.connector.conf.ConnectorEndpointConfiguration;
import org.openmrs.module.epts.etl.connector.conf.ConnectorRegistry;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorEndpoint;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.etl.model.EtlDatabaseObjectSearchParams;
import org.openmrs.module.epts.etl.etl.model.EtlDynamicItemSearchParams;
import org.openmrs.module.epts.etl.etl.model.stage.StageAreaStorageManager;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.SearchClauses;
import org.openmrs.module.epts.etl.model.base.BaseDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlObjectConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EtlItemConfiguration extends AbstractEtlDataConfiguration {

	private String id;

	private String configCode;

	private EtlItemSrcConf etlItemSrcConf;

	private EtlSourceConfiguration source;

	/**
	 * JDBC view of this item's record model on the ETL database. Unlike the primary
	 * data source, this datasource is always bound to the legacy source connector
	 * created from {@link EtlConfiguration#getSrcConnInfo()}.
	 */
	@JsonIgnore
	private transient JdbcTablePrimaryDataSourceConfiguration etlDatabaseDataSource;

	private List<EtlDestinationConfiguration> destinations;

	private Boolean disabled;

	private Boolean fullLoaded;

	/**
	 * If present, the value from this field will be mapped as a primary key for all
	 * tables under this configuration that don't have a primary key but have a
	 * field with name matching this field. <br>
	 * This value will be overridden by the correspondent value on
	 * {@link TableConfiguration} if present there
	 */
	private String manualMapPrimaryKeyOnField;

	private Boolean createDstTableIfNotExists;

	private Boolean testing;

	private EtlDatabaseObject relatedEtlSchemaObject;

	private ParallelProcessingStrategyType threadingMode;

	private AutoIncrementHandlingType autoIncrementHandlingType;

	/**
	 * A numeric value added to the primary key of the very first destination record
	 * for all tables defined in the ETL Item Configuration. For a MySQL destination
	 * whose primary key is AUTO_INCREMENT, this value initializes the native table
	 * counter only while the table is empty; subsequent IDs are generated by the
	 * database. Otherwise it is the initial value used by manual ID generation.
	 */
	private Integer primaryKeyInitialIncrementValue;

	private List<EtlChildItemConfiguration> childItemConf;

	private Boolean doNotFullLoadDestinations;

	private String shortCode;

	private EtlConfiguration relatedEtlConf;

	public EtlItemConfiguration() {
	}

	public void setRelatedEtlConf(EtlConfiguration relatedEtlConf) {
		this.relatedEtlConf = relatedEtlConf;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.relatedEtlConf;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public Boolean isDoNotFullLoadDestinations() {
		return doNotFullLoadDestinations != null && doNotFullLoadDestinations;
	}

	public void setDoNotFullLoadDestinations(Boolean doNotFullLoadDestinations) {
		this.doNotFullLoadDestinations = doNotFullLoadDestinations;
	}

	public List<EtlChildItemConfiguration> getChildItemConf() {
		return childItemConf;
	}

	public void setChildItemConf(List<EtlChildItemConfiguration> childItemConf) {
		this.childItemConf = childItemConf;
	}

	public Integer getPrimaryKeyInitialIncrementValue() {
		return primaryKeyInitialIncrementValue;
	}

	public void setPrimaryKeyInitialIncrementValue(Integer primaryKeyInitialIncrementValue) {
		this.primaryKeyInitialIncrementValue = primaryKeyInitialIncrementValue;
	}

	public AutoIncrementHandlingType getAutoIncrementHandlingType() {
		return autoIncrementHandlingType;
	}

	public void setAutoIncrementHandlingType(AutoIncrementHandlingType autoIncrementHandlingType) {
		this.autoIncrementHandlingType = autoIncrementHandlingType;
	}

	public ParallelProcessingStrategyType getParallelProcessingStrategyType() {
		return threadingMode;
	}

	public void setThreadingMode(ParallelProcessingStrategyType threadingMode) {
		this.threadingMode = threadingMode;
	}

	public Boolean hasParallelProcessingStrategyType() {
		return this.getParallelProcessingStrategyType() != null;
	}

	public EtlDatabaseObject getRelatedEtlSchemaObject() {
		return relatedEtlSchemaObject;
	}

	public void setRelatedEtlSchemaObject(EtlDatabaseObject relatedEtlSchemaObject) {
		this.relatedEtlSchemaObject = relatedEtlSchemaObject;
	}

	public EtlItemSrcConf getEtlItemSrcConf() {
		return etlItemSrcConf;
	}

	public Boolean isTesting() {
		return testing != null && testing;
	}

	public void setTesting(Boolean testing) {
		this.testing = testing;
	}

	public void setEtlItemSrcConf(EtlItemSrcConf srcOfSrc) {
		this.etlItemSrcConf = srcOfSrc;
	}

	public void setCreateDstTableIfNotExists(Boolean createDstTableIfNotExists) {
		this.createDstTableIfNotExists = createDstTableIfNotExists;
	}

	public Boolean isCreateDstTableIfNotExists() {
		return createDstTableIfNotExists != null && createDstTableIfNotExists;
	}

	public Boolean createDstTableIfNotExists() {
		return this.isCreateDstTableIfNotExists();
	}

	public String getManualMapPrimaryKeyOnField() {
		return manualMapPrimaryKeyOnField;
	}

	public void setManualMapPrimaryKeyOnField(String manualMapPrimaryKeyOnField) {
		this.manualMapPrimaryKeyOnField = manualMapPrimaryKeyOnField;
	}

	public EtlSourceConfiguration getSource() {
		return source;
	}

	public void setSource(EtlSourceConfiguration source) {
		this.source = source;
	}

	void prepareSourceConfiguration() {

		if (source == null) {
			throw new EtlConfException("etlItemConfiguration must define 'source'");
		}

		this.source.initialize(this);
		applySourceProcessingPolicy();
	}

	private void applySourceProcessingPolicy() {
		SourceProcessingPolicy policy = source.getProcessingPolicy();
		PrimaryDataSourceConfiguration primary = source.getPrimaryDataSource();

		if (policy != null && primary instanceof JdbcTablePrimaryDataSourceConfiguration) {
			JdbcTablePrimaryDataSourceConfiguration jdbc = (JdbcTablePrimaryDataSourceConfiguration) primary;

			if (policy.getLimitToOneResult() != null) {
				jdbc.setLimitToOneResult(policy.getLimitToOneResult());
			}

			if (policy.getTrackProcessingState() != null) {
				jdbc.setTrackProcessingState(policy.getTrackProcessingState());
			}

			if (policy.getMissingRequiredObjectBehavior() != null) {
				jdbc.setMissingRequiredObjectBehavior(policy.getMissingRequiredObjectBehavior());
			}
		}
	}

	@JsonIgnore
	public PrimaryDataSourceConfiguration getPrimaryDataSource() {

		if (source == null) {
			throw new IllegalStateException("Source configuration has not been prepared");
		}

		return source.getPrimaryDataSource();
	}

	/**
	 * Returns the JDBC datasource used by processors that operate on the ETL
	 * database itself. It deliberately does not replace the configured primary
	 * datasource, which may be JSON, CSV or use another JDBC connector.
	 */
	@JsonIgnore
	public synchronized JdbcTablePrimaryDataSourceConfiguration getEtlDatabaseDataSource() {

		if (etlDatabaseDataSource == null) {
			etlDatabaseDataSource = createEtlDatabaseDataSource();
		}
		return etlDatabaseDataSource;
	}

	private JdbcTablePrimaryDataSourceConfiguration createEtlDatabaseDataSource() {

		prepareSourceConfiguration();

		if (getRelatedEtlConf() == null || getRelatedEtlConf().getSrcConnInfo() == null) {
			throw new IllegalStateException(
					"srcConnInfo is required to create the ETL database datasource for item '" + getConfigCode() + "'");
		}

		PrimaryDataSourceConfiguration primary = getPrimaryDataSource();

		if (primary instanceof JdbcTablePrimaryDataSourceConfiguration && primary.getParentConf() == null) {
			primary.setParentConf(this);
		}

		JdbcTablePrimaryDataSourceConfiguration jdbc = new JdbcTablePrimaryDataSourceConfiguration();

		String tableName = primary instanceof JdbcTablePrimaryDataSourceConfiguration
				? ((JdbcTablePrimaryDataSourceConfiguration) primary).getTableName()
				: primary.getName();

		if (!utilities.stringHasValue(tableName)) {
			throw new EtlConfException(
					"Unable to determine the ETL database table name for item '" + getConfigCode() + "'");
		}

		jdbc.setTableName(tableName);
		jdbc.setTableAlias(primary.getAlias());
		jdbc.setSchema(getRelatedEtlConf().getSrcConnInfo().determineSchema());
		jdbc.setParentConf(this);
		jdbc.setParentSource(source);
		jdbc.setFields(copyFields(primary.getFields()));

		if (Boolean.TRUE.equals(primary.hasPK()) && primary.getPrimaryKey() instanceof PrimaryKey) {
			PrimaryKey primaryKey = new PrimaryKey();
			primaryKey.copy(primary.getPrimaryKey());
			jdbc.setPrimaryKeyInfoLoaded(true);
			jdbc.setPrimaryKey(primaryKey);
		}

		jdbc.setFullLoaded(true);

		if (primary instanceof JdbcTablePrimaryDataSourceConfiguration) {
			JdbcTablePrimaryDataSourceConfiguration configuredJdbc = (JdbcTablePrimaryDataSourceConfiguration) primary;
			jdbc.setDoNotUseAsDatasource(configuredJdbc.getDoNotUseAsDatasource());
			jdbc.setEtlFields(configuredJdbc.getEtlFields());
		}

		ConnectorEndpoint endpoint = resolveEtlDatabaseConnector();

		jdbc.setResolvedResource(ConnectorConfigurationResolver.resolveResource(null, jdbc, EtlSide.SRC, endpoint));

		return jdbc;
	}

	private ConnectorEndpoint resolveEtlDatabaseConnector() {
		ConnectorRegistry registry = getRelatedEtlConf().getInitializedConnectorRegistry();
		ConnectorEndpoint endpoint = registry.getEndpoints().get(ConnectorRegistry.LEGACY_SOURCE_CONNECTOR_ID);

		if (endpoint == null && utilities.stringHasValue(getRelatedEtlConf().getDefaultSourceConnector())) {
			endpoint = registry.get(getRelatedEtlConf().getDefaultSourceConnector());
		}

		if (endpoint == null || endpoint.getType() != ConnectorType.JDBC) {
			throw new EtlConfException("No JDBC connector is available for the ETL database datasource of item '"
					+ getConfigCode() + "'; configure srcConnInfo or a JDBC defaultSourceConnector");
		}

		return endpoint;
	}

	private List<Field> copyFields(List<Field> fields) {

		if (fields == null) {
			return new ArrayList<>();
		}

		List<Field> copies = new ArrayList<>(fields.size());

		for (Field field : fields) {

			if (field != null) {
				Field copy = new Field();
				copy.copyFrom(field);
				copies.add(copy);
			}
		}
		return copies;
	}

	@JsonIgnore
	public JdbcTablePrimaryDataSourceConfiguration requireJdbcPrimaryDataSource() {
		return getPrimaryDataSource().requireJdbc();
	}

	public List<EtlDestinationConfiguration> getDestinations() {
		return destinations;
	}

	/** JDBC-only view used by relational processing internals. */
	@JsonIgnore
	public List<JdbcTableDestinationConfiguration> getJdbcDestinations() {
		List<JdbcTableDestinationConfiguration> jdbcDestinations = new ArrayList<>();

		if (destinations != null) {

			for (EtlDestinationConfiguration destination : destinations) {

				if (destination.isJdbcDestination()) {
					jdbcDestinations.add(destination.requireJdbcTableDestination());
				}
			}
		}
		return jdbcDestinations;
	}

	@JsonIgnore
	public JdbcTableDestinationConfiguration findJdbcDestination(String destinationName) {
		EtlDestinationConfiguration destination = findDestination(destinationName);
		return destination == null ? null : destination.requireJdbcTableDestination();
	}

	@JsonIgnore
	public JdbcTableDestinationConfiguration findJdbcDestination(EtlOperationConfig operationConfig, String name)
			throws DBException {
		EtlDestinationConfiguration destination = findDestination(operationConfig, name);
		return destination == null ? null : destination.requireJdbcTableDestination();
	}

	public void setDestinations(List<EtlDestinationConfiguration> destinations) {
		this.destinations = destinations;
	}

	@JsonIgnore
	public ConnectorResource getResolvedSrcResource() {
		return source == null ? null : source.getResolvedResource();
	}

	@JsonIgnore
	public List<ConnectorResource> getResolvedDstResources() {

		if (destinations == null || destinations.isEmpty()) {
			return Collections.emptyList();
		}

		List<ConnectorResource> resources = new ArrayList<>(destinations.size());

		for (EtlDestinationConfiguration destination : destinations) {
			ConnectorResource resource = destination.getResolvedResource();

			if (resource == null) {
				return Collections.emptyList();
			}

			resources.add(resource);
		}

		return Collections.unmodifiableList(resources);
	}

	@JsonIgnore
	public ConnectorResource getResolvedDstResource(EtlDestinationConfiguration dstConfiguration) {

		ConnectorResource resource = dstConfiguration.getResolvedResource();

		if (resource == null) {
			throw new IllegalStateException("Destination resource has not been initialized for " + dstConfiguration);
		}

		return resource;
	}

	@JsonIgnore
	public boolean usesLegacyDestinationResource(EtlDestinationConfiguration dstConfiguration) {
		return dstConfiguration.getResource() == null;
	}

	@JsonIgnore
	public boolean usesLegacyDestinationResource(JdbcTableDestinationConfiguration jdbcAdapter) {
		return jdbcAdapter instanceof JdbcTableDestinationConfiguration && usesLegacyDestinationResource(
				((JdbcTableDestinationConfiguration) jdbcAdapter).getParentDestination());
	}

	private void initializeConnectorResources() {
		prepareSourceConfiguration();
		ConnectorRegistry registry = getRelatedEtlConf().getInitializedConnectorRegistry();
		ConnectorResource source = resolveSrcResource(registry);
		List<ConnectorResource> destinationResources = Collections.unmodifiableList(resolveDstResources(registry));

		this.source.setResolvedResource(source);
		resolveAdditionalSourceResources(registry, source, true);

		if (this.source.getPrimaryDataSource() instanceof JdbcTablePrimaryDataSourceConfiguration) {
			JdbcTablePrimaryDataSourceConfiguration jdbcSource = (JdbcTablePrimaryDataSourceConfiguration) this.source
					.getPrimaryDataSource();
			jdbcSource.setResolvedResource(source);

			if (this.source.getResource() != null && utilities.stringHasValue(this.source.getResource().getAlias())) {
				jdbcSource.setTableAlias(this.source.getResource().getAlias());
			}
		}

		if (destinationResources != null) {

			for (int index = 0; index < destinationResources.size(); index++) {

				if (index >= this.destinations.size()) {
					throw new IllegalStateException("No resolved destination resource for dstConf at index " + index);
				}

				EtlDestinationConfiguration destinationConfiguration = this.destinations.get(index);
				ConnectorResource destinationResource = destinationResources.get(index);
				destinationConfiguration.initialize(this);
				destinationConfiguration.setResolvedResource(destinationResource);
			}
		}
	}

	public <T extends EtlDatabaseObject> ConnectorRecordReader<T> createSourceReader(
			AbstractEtlSearchParams<T> searchParams, BaseConfiguration openedFrom, OpenConnection etlSrcConn,
			ConnectorRecordWritersAggregator writers) throws DBException {

		ConnectorResource sourceResource = this.getResolvedSrcResource();

		if (sourceResource == null) {
			throw new IllegalStateException("Source resource has not been initialized");
		}

		OpenConnection srcConnection = null;

		try {

			if (sourceResource.getEndpoint().getType() == ConnectorType.JDBC) {

				if (((JdbcConnectorEndpoint) sourceResource.getEndpoint()).getConnectionInfo() == etlSrcConn
						.getDbConnInfo()) {
					srcConnection = etlSrcConn;
				} else {
					srcConnection = getRelatedEtlConf().openSourceConnectorConnection(sourceResource, openedFrom);
				}
			} else {
				srcConnection = getRelatedEtlConf().openSrcConn(openedFrom);
			}

			return ConnectorRecordReaderFactory.create(sourceResource, searchParams, srcConnection);
		} catch (RuntimeException e) {

			if (srcConnection != null) {
				srcConnection.finalizeConnection(openedFrom);
			}

			throw e;
		}
	}

	/** Creates the primary and additional reader scope for one ETL worker. */
	public <T extends EtlDatabaseObject> ConnectorRecordReadersAggregator createSourceReaders(
			AbstractEtlSearchParams<T> searchParams, BaseConfiguration openedFrom,
			ConnectorRecordWritersAggregator writers) throws DBException {

		Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers = new IdentityHashMap<>();
		Map<EtlDataSource, ConnectorResource> resources = new IdentityHashMap<>();
		Map<ConnectorEndpoint, OpenConnection> jdbcConnections = new IdentityHashMap<>();
		List<OpenConnection> openedConnections = new ArrayList<>();

		OpenConnection etlSrcConn = this.getRelatedEtlConf().openSrcConn(openedFrom);

		try {
			ConnectorRecordReader<T> primaryReader = this.createSourceReader(searchParams, openedFrom, etlSrcConn,
					writers);

			if (source == null) {

				try {
					OpenConnection primaryConnection = primaryReader.unwrap(OpenConnection.class);
					openedConnections.add(primaryConnection);
				} catch (IllegalArgumentException ignored) {
				}

				return new ConnectorRecordReadersAggregator(etlSrcConn, primaryReader, readers, resources);
			}

			PrimaryDataSourceConfiguration primary = this.getPrimaryDataSource();
			ConnectorResource primaryResource = this.getResolvedSrcResource();

			readers.put(primaryResource, primaryReader);
			resources.put(primary, primaryResource);

			try {
				OpenConnection primaryConnection = primaryReader.unwrap(OpenConnection.class);
				openedConnections.add(primaryConnection);
			} catch (IllegalArgumentException ignored) {
				// A connector reader is not required to expose a JDBC supporting connection.
			}

			if (primaryResource.getEndpoint().getType() == ConnectorType.JDBC) {
				OpenConnection primaryConnection = primaryReader.unwrap(OpenConnection.class);
				jdbcConnections.put(primaryResource.getEndpoint(), primaryConnection);
			}

			Map<EtlDataSource, ConnectorResource> sourceResources = collectSourceConfigurations();

			for (Map.Entry<EtlDataSource, ConnectorResource> entry : sourceResources.entrySet()) {
				EtlDataSource dataSource = entry.getKey();
				ConnectorResource resource = entry.getValue();

				if (dataSource == primary) {
					continue;
				}

				if (resource == null) {
					throw new IllegalStateException("No resolved resource for data source " + dataSource);
				}

				ConnectorRecordReader<? extends EtlDatabaseObject> reader = readers.get(resource);

				if (reader == null) {
					Object nativeState = null;

					if (resource.getEndpoint().getType() == ConnectorType.JDBC) {
						ConnectorEndpoint endpoint = resource.getEndpoint();
						OpenConnection connection = jdbcConnections.get(endpoint);

						if (connection == null) {
							connection = getRelatedEtlConf().openSourceConnectorConnection(resource, openedFrom);
							jdbcConnections.put(endpoint, connection);
							openedConnections.add(connection);
						}
						nativeState = connection;
					}
					reader = new ResourceBoundConnectorRecordReader<>(resource, nativeState);
					readers.put(resource, reader);
				}
				resources.put(dataSource, resource);
			}

			ConnectorRecordReadersAggregator agg = new ConnectorRecordReadersAggregator(etlSrcConn, primaryReader,
					readers, resources);

			return agg;
		} catch (DBException | RuntimeException exception) {
			OpenConnection.finalizeAllConnections(openedConnections, openedFrom);
			throw exception;
		}
	}

	private Map<EtlDataSource, ConnectorResource> collectSourceConfigurations() {
		Map<EtlDataSource, ConnectorResource> sourceResources = new IdentityHashMap<>();
		Set<EtlItemConfiguration> visited = Collections.newSetFromMap(new IdentityHashMap<>());
		collectSourceConfigurations(this, sourceResources, visited);
		return sourceResources;
	}

	private void collectSourceConfigurations(EtlItemConfiguration item,
			Map<EtlDataSource, ConnectorResource> sourceResources, Set<EtlItemConfiguration> visited) {

		if (item == null || !visited.add(item) || item.getSource() == null) {
			return;
		}

		PrimaryDataSourceConfiguration itemPrimary = item.getPrimaryDataSource();

		if (itemPrimary != null) {
			sourceResources.put(itemPrimary, item.getResolvedSrcResource());
		}

		Set<EtlAdditionalDataSource> additionalSources = Collections.newSetFromMap(new IdentityHashMap<>());
		additionalSources.addAll(item.getSource().getConfiguredAdditionalDataSources());
		additionalSources.addAll(item.getSource().getAvailableAdditionalDataSources());

		for (EtlAdditionalDataSource additional : additionalSources) {
			sourceResources.put(additional, additional.getResolvedResource());
		}

		if (item.getChildItemConf() != null) {

			for (EtlChildItemConfiguration child : item.getChildItemConf()) {
				collectSourceConfigurations(child, sourceResources, visited);
			}
		}
	}

	public ConnectorRecordWritersAggregator createDestinationWriters(BaseConfiguration openedFrom) throws DBException {

		Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers = new IdentityHashMap<>();
		Map<EtlDestinationConfiguration, ConnectorResource> resources = new IdentityHashMap<>();
		Map<String, OpenConnection> connections = new HashMap<>();

		try {

			for (EtlDestinationConfiguration destination : collectDestinationConfigurations()) {
				ConnectorResource resource = getResolvedDstResource(destination);

				String endpointKey = resource.getEndpoint().getType() + "|" + resource.getEndpoint().getUri() + "|"
						+ resource.getEndpoint().getUserName();

				OpenConnection connection = connections.get(endpointKey);

				if (connection == null && resource.getEndpoint().getType() == ConnectorType.JDBC) {
					connection = getRelatedEtlConf().openDestinationConnectorConnection(resource, openedFrom);
					connections.put(endpointKey, connection);
				}

				writers.put(resource,
						ConnectorRecordWriterFactory.create(resource,
								destination.isJdbcDestination() ? destination.requireJdbcTableDestination() : null,
								connection));
				resources.put(destination, resource);
			}
		} catch (DBException | RuntimeException exception) {
			OpenConnection.finalizeAllConnections(new ArrayList<>(connections.values()), openedFrom);
			throw exception;
		}

		ConnectorRecordWritersAggregator agg = new ConnectorRecordWritersAggregator(writers, resources);

		return agg;
	}

	/**
	 * Creates the writer scope used by processors that persist on the ETL database.
	 */
	public ConnectorRecordWritersAggregator createEtlDatabaseWriters(BaseConfiguration openedFrom) throws DBException {
		JdbcTablePrimaryDataSourceConfiguration dataSource = getEtlDatabaseDataSource();
		ConnectorEndpoint endpoint = dataSource.getResolvedResource().getEndpoint();
		ConnectorResource resource = ConnectorConfigurationResolver.resolveResource(null, dataSource, EtlSide.DST,
				endpoint);
		Connection connection = getRelatedEtlConf().openDestinationConnectorConnection(resource, openedFrom);

		Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers = new IdentityHashMap<>();
		writers.put(resource, ConnectorRecordWriterFactory.create(resource, dataSource, connection));

		ConnectorRecordWritersAggregator agg = new ConnectorRecordWritersAggregator(writers, Collections.emptyMap());

		return agg;
	}

	/** Creates the reader used by processors that operate on the ETL database. */
	/*
	 * public <T extends EtlDatabaseObject> ConnectorRecordReader<T>
	 * createEtlDatabaseReader_( AbstractEtlSearchParams<T> searchParams,
	 * BaseConfiguration openedFrom, ConnectorRecordWritersAggregator writers)
	 * throws DBException { ConnectorResource resource =
	 * getEtlDatabaseDataSource().getResolvedResource(); OpenConnection
	 * sourceConnection = null;
	 * 
	 * try { sourceConnection =
	 * getRelatedEtlConf().openSourceConnectorConnection(resource, openedFrom);
	 * 
	 * return ConnectorRecordReaderFactory.create(resource, searchParams,
	 * sourceConnection); } catch (RuntimeException exception) {
	 * 
	 * if (sourceConnection != null) {
	 * sourceConnection.finalizeConnection(openedFrom); } throw exception; } }
	 */

	/**
	 * Creates the lazy connection scope for this item's destinations. No JDBC
	 * connection is opened until a destination is requested from the returned
	 * aggregator.
	 */
	public DestinationConnectorConnections createDestinationConnections(BaseConfiguration openedFrom) {
		prepareDestinationConfigurationsForConnectorFullLoad();

		return new DestinationConnectorConnections(getRelatedEtlConf(), getDestinations(), openedFrom);
	}

	private List<EtlDestinationConfiguration> collectDestinationConfigurations() {

		List<EtlDestinationConfiguration> destinations = new ArrayList<>();

		Set<EtlItemConfiguration> visited = Collections.newSetFromMap(new IdentityHashMap<>());

		collectDestinationConfigurations(this, destinations, visited);

		return destinations;
	}

	private void collectDestinationConfigurations(EtlItemConfiguration item,
			List<EtlDestinationConfiguration> destinations, Set<EtlItemConfiguration> visited) {

		if (item == null || !visited.add(item)) {
			return;
		}

		if (item.getDestinations() != null) {
			destinations.addAll(item.getDestinations());
		}

		if (item.getChildItemConf() != null) {

			for (EtlItemConfiguration child : item.getChildItemConf()) {
				collectDestinationConfigurations(child, destinations, visited);
			}
		}
	}

	private ConnectorResource resolveSrcResource(ConnectorRegistry registry) {
		prepareSourceConfiguration();

		PrimaryDataSourceConfiguration primary = source.getPrimaryDataSource();
		ConnectorResourceConfiguration configured;

		if (source.getResource() != null) {
			configured = source.getResource();
		} else {
			configured = new ConnectorResourceConfiguration();
			configured.setConnector(source.getConnector());
			configured.setName(primary.getResourceName());
			configured.setProperties(primary.getResourceProperties());
		}

		ConnectorEndpoint endpoint = selectConnector(registry, source.getConnector(), EtlSide.SRC,
				source.getConnector() == null && primary instanceof JdbcTablePrimaryDataSourceConfiguration);

		TableConfiguration legacyTable = primary instanceof TableConfiguration ? (TableConfiguration) primary : null;

		return ConnectorConfigurationResolver.resolveResource(configured, legacyTable, EtlSide.SRC, endpoint);
	}

	private List<ConnectorResource> resolveAdditionalSourceResources(ConnectorRegistry registry,
			ConnectorResource primaryResource, boolean assign) {
		List<ConnectorResource> resolved = new ArrayList<>();

		for (EtlAdditionalDataSource additional : source.getConfiguredAdditionalDataSources()) {
			ConnectorResource resource = resolveAdditionalSourceResource(registry, primaryResource, additional);
			resolved.add(resource);

			if (assign) {
				additional.setResolvedResource(resource);
			}

			if (additional instanceof JsonDataSource) {
				resolveJsonOutputResources(registry, (JsonDataSource) additional, resource, assign, resolved);
			}
		}

		return resolved;
	}

	private void resolveJsonOutputResources(ConnectorRegistry registry, JsonDataSource jsonDataSource,
			ConnectorResource parentResource, boolean assign, List<ConnectorResource> resolved) {
		JsonMainOutputDataSource output = jsonDataSource.getOutputDataSource();

		if (output == null) {
			return;
		}

		ConnectorResource outputResource = resolveAdditionalSourceResource(registry, parentResource, output);
		resolved.add(outputResource);

		if (assign) {
			output.setResolvedResource(outputResource);
		}

		if (output.hasParents()) {

			for (JsonSimpleOutputDataSource parent : output.getParentOutputDataSource()) {
				ConnectorResource parentOutputResource = resolveAdditionalSourceResource(registry, outputResource,
						parent);
				resolved.add(parentOutputResource);

				if (assign) {
					parent.setResolvedResource(parentOutputResource);
				}
			}
		}
	}

	private ConnectorResource resolveAdditionalSourceResource(ConnectorRegistry registry,
			ConnectorResource primaryResource, EtlAdditionalDataSource additional) {
		ConnectorResourceConfiguration configured = additional.getResource();

		if (configured == null && !(additional instanceof TableConfiguration)
				&& !(additional instanceof QueryDataSourceConfig)) {
			return primaryResource;
		}

		ConnectorResourceConfiguration effective = configured == null ? new ConnectorResourceConfiguration()
				: configured.copy();

		if (!utilities.stringHasValue(effective.getName())) {
			effective.setName(additional.getName());
		}

		ConnectorEndpoint endpoint = utilities.stringHasValue(effective.getConnector())
				? selectConnector(registry, effective.getConnector(), EtlSide.SRC, false)
				: primaryResource.getEndpoint();

		if ((additional instanceof TableConfiguration || additional instanceof QueryDataSourceConfig)
				&& endpoint.getType() != ConnectorType.JDBC) {
			throw new EtlConfException(
					"Additional data source '" + additional.getName() + "' requires a JDBC connector");
		}

		TableConfiguration legacyTable = additional instanceof TableConfiguration ? (TableConfiguration) additional
				: null;
		return ConnectorConfigurationResolver.resolveResource(effective, legacyTable, EtlSide.SRC, endpoint);
	}

	private List<ConnectorResource> resolveDstResources(ConnectorRegistry registry) {

		List<ConnectorResource> resolved = new ArrayList<>();

		if (destinations == null) {
			return resolved;
		}

		for (EtlDestinationConfiguration destination : destinations) {
			ConnectorResourceConfiguration configured = destination.getResource();

			String connectorId = configured == null ? null : configured.getConnector();

			ConnectorEndpoint endpoint = selectConnector(registry, connectorId, EtlSide.DST, configured == null);

			resolved.add(ConnectorConfigurationResolver.resolveResource(configured, null, EtlSide.DST, endpoint));
		}

		return resolved;
	}

	void validateConnectorResources(ConnectorRegistry registry, List<String> issues) {

		try {
			prepareSourceConfiguration();

			if (source != null) {
				ConnectorResource source = resolveSrcResource(registry);
				resolveAdditionalSourceResources(registry, source, false);

				if (getPrimaryDataSource() instanceof AbstractTableConfiguration) {
					validateRecordSchema((AbstractTableConfiguration) getPrimaryDataSource(), source);
				}
			}
		} catch (RuntimeException e) {
			issues.add("Invalid source connector on etlItemConfiguration '" + getConfigCode() + "': " + e.getMessage());
		}

		try {
			List<ConnectorResource> destinations = resolveDstResources(registry);

			for (int index = 0; this.destinations != null && index < this.destinations.size(); index++) {
				EtlDestinationConfiguration destination = this.destinations.get(index);
				destination.initialize(this);
				destination.setResolvedResource(destinations.get(index));

				if (destination.getTableDataDestination() != null) {
					validateRecordSchema(destination.getTableDataDestination(), destinations.get(index));
				}
			}
		} catch (RuntimeException e) {
			issues.add("Invalid destination connector on etlItemConfiguration '" + getConfigCode() + "': "
					+ e.getMessage());
		}
	}

	private void validateRecordSchema(AbstractTableConfiguration configuration, ConnectorResource resource) {

		if (resource != null && resource.getEndpoint().getType() != ConnectorType.JDBC) {

			if (configuration.getRecordSchema() == null) {
				throw new IllegalArgumentException(
						"recordSchema is required for non-JDBC resource '" + resource.getName() + "'");
			}

			configuration.getRecordSchema()
					.resolveFields(resource.getEndpoint().getType() + " resource '" + resource.getName() + "'");
		}
	}

	private ConnectorEndpoint selectConnector(ConnectorRegistry registry, String connectorId, EtlSide direction,
			boolean preferLegacy) {

		String defaultId = direction == EtlSide.SRC ? getRelatedEtlConf().getDefaultSourceConnector()
				: getRelatedEtlConf().getDefaultDestinationConnector();

		return registry.select(connectorId, defaultId, direction, preferLegacy);
	}

	public static EtlItemConfiguration fastCreate(AbstractTableConfiguration tableConfig,
			boolean useMainEtlTableAsPrimarySourceIfNotExists, Connection conn) throws DBException {

		EtlItemConfiguration etl = new EtlItemConfiguration();
		JdbcTablePrimaryDataSourceConfiguration jdbcSource;

		if (tableConfig instanceof JdbcTablePrimaryDataSourceConfiguration) {
			jdbcSource = (JdbcTablePrimaryDataSourceConfiguration) tableConfig;
		} else {
			jdbcSource = JdbcTablePrimaryDataSourceConfiguration.fastCreate(tableConfig, etl, conn);
		}
		etl.setSource(sourceWithJdbcPrimary(jdbcSource));

		etl.setRelatedEtlConfig(tableConfig.getRelatedEtlConf());

		return etl;

	}

	protected static EtlSourceConfiguration sourceWithJdbcPrimary(
			JdbcTablePrimaryDataSourceConfiguration primaryDataSource) {
		EtlSourceConfiguration source = new EtlSourceConfiguration();
		source.setTableDataSource(primaryDataSource);
		return source;
	}

	public static EtlItemConfiguration fastCreate(AbstractTableConfiguration tableConfig, Connection conn)
			throws DBException {

		return fastCreate(tableConfig, false, conn);
	}

	public static EtlItemConfiguration fastCreate(String configCode) {
		EtlItemConfiguration etl = new EtlItemConfiguration();

		etl.setConfigCode(configCode);

		return etl;
	}

	public Boolean isFullLoaded() {
		return fullLoaded != null && fullLoaded;
	}

	public void setFullLoaded(Boolean fullLoaded) {
		this.fullLoaded = fullLoaded;
	}

	public Boolean hasDestinations() {
		return utilities.listHasElement(this.getDestinations());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void init(EtlConfiguration relatedEtlConf, boolean testing, Connection srcConn) throws DBException {

		if (isInitialized()) {
			return;
		}

		this.setRelatedEtlConf(relatedEtlConf);

		this.applyIncludes();
		this.tryToLoadFromTemplate();

		this.setRelatedEtlConfig(relatedEtlConf);
		this.setTesting(testing);
		this.prepareSourceConfiguration();
		this.initializeConnectorResources();

		this.getSource().initializeDataSources();

		String codeElements = "";

		List<String> alreadyIncludedTables = new ArrayList<>();

		if (utilities.listHasElement(this.getDestinations())) {

			try (DestinationConnectorConnections destinationConnections = createDestinationConnections(this)) {

				for (EtlDestinationConfiguration dst : this.getDestinations()) {
					dst.initialize(this);

					if (destinationConnections.isJdbc(dst)) {
						relatedEtlConf.addConfiguredTable(dst.requireJdbcTableDestination());
					}

					if (!alreadyIncludedTables.contains(dst.getName())) {
						alreadyIncludedTables.add(dst.getName());

						codeElements = utilities.stringHasValue(codeElements) ? codeElements + "_and_" + dst.getAlias()
								: dst.getAlias();
					}
				}
			}
		}

		String sourceAlias = this.getPrimaryDataSource().getAlias();

		codeElements = utilities.stringHasValue(codeElements) ? codeElements : sourceAlias;

		codeElements = sourceAlias + "_to_" + codeElements;

		this.setShortCode(codeElements);

		if (!hasManualConfigCode()) {
			this.setConfigCode(getRelatedEtlConf().finalizeItemCodeGeneration(codeElements));
		}

		this.tryToLoadChildItemConf(testing, srcConn);

		this.markAsInitialized();
	}

	protected String putOperationElementoOnCode(String codeElements) {
		codeElements += "_on_" + getRelatedEtlConf().generateProcessId();

		return codeElements;
	}

	private void tryToLoadChildItemConf(boolean testing, Connection srcConn) throws DBException {

		if (this.hasChildItemConf()) {

			for (EtlChildItemConfiguration childItem : this.getChildItemConf()) {
				childItem.setParentItemConf(this);

				if (!utilities.stringHasValue(childItem.getRelatedParentDstConfName())) {

					if (utilities.listHasExactlyOneElement(this.getDestinations())) {
						childItem.setRelatedParentDstConfName(this.getDestinations().get(0).getName());
					} else {
						throw new ForbiddenOperationException(
								"The relatedParentDstConfName was not defined for the conf " + this.getConfigCode());
					}
				}

				childItem.setRelatedParentDstConf(this.findDestination(childItem.getRelatedParentDstConfName()));
				childItem.setRelatedEtlConfig(getRelatedEtlConf());
				childItem.init(getRelatedEtlConf(), testing, srcConn);
			}
		}
	}

	public void tryToCreateDefaultRecordsForAllTables() throws DBException {
		OpenConnection srcConn = getRelatedEtlConf().openSrcConn(this);

		try {

			try (DestinationConnectorConnections destinationConnections = createDestinationConnections(this)) {

				if (this.hasDestinations()) {

					for (EtlDestinationConfiguration dst : this.getDestinations()) {

						if (!destinationConnections.isJdbc(dst)) {
							continue;
						}
						Connection dstConn = destinationConnections.get(dst);

						JdbcTableDestinationConfiguration jdbcDestination = dst.requireJdbcTableDestination();

						if (!jdbcDestination.isParentsLoaded()) {
							jdbcDestination.loadParents(dstConn);
						}

						if (!jdbcDestination.hasParentRefInfo()) {
							continue;
						}

						for (ParentTable refInfo : jdbcDestination.getParentRefInfo()) {

							if (refInfo.isMetadata()) {
								continue;
							}

							if (!refInfo.isFullLoaded()) {
								refInfo.fullLoad(dstConn);
							}

							if (refInfo.useSharedPKKey()) {

								if (!refInfo.getSharedKeyRefInfo(dstConn).isFullLoaded()) {
									refInfo.getSharedKeyRefInfo(dstConn).fullLoad(dstConn);
								}
							}

							if (refInfo.getDefaultObject(dstConn) == null) {
								getRelatedEtlConf().debug(
										"Creating default dstRecord for table " + refInfo.getFullTableDescription());

								try {
									refInfo.generateAndSaveDefaultObject(srcConn, dstConn);
								} catch (Exception e) {
									getRelatedEtlConf().err(
											"Error creating default record for table: " + refInfo.getTableAlias(), e);
								}
							}
						}
					}
					destinationConnections.markAllAsSuccessfullyTerminated();
				}
			}
			srcConn.markAsSuccessifullyTerminated();
		} finally {
			srcConn.finalizeConnection(this);
		}
	}

	public Boolean hasManualMapPrimaryKeyOnField() {
		return getManualMapPrimaryKeyOnField() != null;
	}

	public synchronized void fullLoad(EtlOperationConfig operationConfig) throws DBException {

		if (this.isFullLoaded()) {
			return;
		}

		prepareDestinationConfigurationsForConnectorFullLoad();

		this.getSource().fullLoad();

		JdbcTablePrimaryDataSourceConfiguration jdbcSource = getPrimaryDataSource() instanceof JdbcTablePrimaryDataSourceConfiguration
				? (JdbcTablePrimaryDataSourceConfiguration) getPrimaryDataSource()
				: null;

		EtlObjectConfiguration sourceConnectionOwner = jdbcSource != null ? jdbcSource
				: this.getEtlDatabaseDataSource();
		OpenConnection sourceConnection = sourceConnectionOwner.openConnection(sourceConnectionOwner);

		try {
			fullLoad(operationConfig, jdbcSource, sourceConnection);

			sourceConnection.markAsSuccessifullyTerminated();
		} finally {
			sourceConnection.finalizeConnection(sourceConnectionOwner);
		}
	}

	private void prepareDestinationConfigurationsForConnectorFullLoad() {
		prepareSourceConfiguration();

		if (!hasDestinations() && (getRelatedEtlConf().hasDstConnInfo()
				|| utilities.stringHasValue(getRelatedEtlConf().getDefaultDestinationConnector()))) {
			ConnectorEndpoint endpoint = selectConnector(getRelatedEtlConf().getInitializedConnectorRegistry(), null,
					EtlSide.DST, true);

			if (endpoint.getType() == ConnectorType.JDBC) {
				EtlDestinationConfiguration destination = new EtlDestinationConfiguration();
				ConnectorResourceConfiguration resource = new ConnectorResourceConfiguration();
				resource.setConnector(getRelatedEtlConf().getDefaultDestinationConnector());
				resource.setName(getPrimaryDataSource().getName());
				destination.setResource(resource);
				JdbcTableDestinationConfiguration table = new JdbcTableDestinationConfiguration(
						getPrimaryDataSource().getName());
				Object schema = null;

				if (getRelatedEtlConf().getConnectors() != null) {

					for (ConnectorEndpointConfiguration configuredEndpoint : getRelatedEtlConf().getConnectors()) {

						if (resource.getConnector() != null
								&& resource.getConnector().equals(configuredEndpoint.getId())) {
							schema = configuredEndpoint.getProperties().get("schema");
							break;
						}
					}
				}

				if (schema != null) {
					table.setSchema(String.valueOf(schema));
				}
				destination.setTableDataDestination(table);
				setDestinations(Collections.singletonList(destination));
			}
		}

		initializeConnectorResources();
	}

	private void fullLoad(EtlOperationConfig operationConfig, JdbcTablePrimaryDataSourceConfiguration jdbcSource,
			Connection sourceConnection) throws DBException {

		this.getRelatedEtlConf().info("Starting full load of etlItemConfiguration {}", this);

		if (!hasManualMapPrimaryKeyOnField()) {
			setManualMapPrimaryKeyOnField(getRelatedEtlConf().getManualMapPrimaryKeyOnField());
		}

		if (jdbcSource != null && !jdbcSource.hasDstType()) {
			jdbcSource.setDstType(operationConfig.getDstType());
		}

		try {

			if (jdbcSource != null) {
				jdbcSource.setParentConf(this);
			}

			if (hasDestinations()) {

				for (EtlDestinationConfiguration map : getDestinations()) {

					if (map.isDisabled()) {
						continue;
					}

					map.initialize(this);

					if (map.getResolvedResource().getEndpoint().getType() != ConnectorType.JDBC) {
						map.fullLoad(null);
					} else {
						this.fullLoadJdbcDestination(map, jdbcSource, sourceConnection);
					}
				}
			}

			if (operationConfig.writeOperationHistory()
					|| operationConfig.getRelatedEtlConf().getGeneralBehaviourOnEtlException().log()) {

				if (jdbcSource != null && !jdbcSource.doNotUseAsDatasource()) {
					jdbcSource.generateStagingTables(sourceConnection);
				}
			}

			setFullLoaded(true);

			getRelatedEtlConf().info("Full load done for etlItemConfiguration {}", this);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	private void fullLoadJdbcDestination(EtlDestinationConfiguration map,
			JdbcTablePrimaryDataSourceConfiguration jdbcSource, Connection sourceConnection)
			throws DBException, SQLException {
		OpenConnection destinationConnection = map.openConnection(map);
		JdbcTableDestinationConfiguration jdbcDestination = map.requireJdbcTableDestination();

		try {

			try {
				jdbcDestination.tryToLoadSchemaInfo(relatedEtlSchemaObject, destinationConnection);
			} catch (DatabaseResourceDoesNotExists e) {

				if (!createDstTableIfNotExists()) {
					throw e;
				}
				jdbcDestination.setInMemoryTable(true);
			}

			if (jdbcDestination.isInMemoryTable()) {
				map.fullLoad(destinationConnection);

				if (createDstTableIfNotExists()) {
					jdbcDestination.createTable(destinationConnection);
				}
			} else {
				jdbcDestination.loadFields(destinationConnection);

				if (jdbcDestination.isAutomaticalyGenerated() && jdbcSource != null && jdbcSource.hasParents()) {
					jdbcDestination.setParents(
							jdbcSource.tryToCloneAllParentsForOtherTable(jdbcDestination, destinationConnection));
				}

				if (!isDoNotFullLoadDestinations()) {
					map.fullLoad(destinationConnection);
					map.generateAllFieldsMapping(destinationConnection);
				}
			}
			destinationConnection.markAsSuccessifullyTerminated();
		} finally {
			destinationConnection.finalizeConnection(map);
		}
	}

	public EtlDestinationConfiguration findDestination(String destinationName) {

		if (this.hasDestinations()) {

			for (EtlDestinationConfiguration conf : this.getDestinations()) {

				if (conf.getAlias() != null && conf.getAlias().equals(destinationName)) {
					return conf;
				}
			}

			EtlDestinationConfiguration found = null;

			for (EtlDestinationConfiguration conf : this.getDestinations()) {

				if (conf.getName().equals(destinationName)) {

					if (found != null) {
						throw new ForbiddenOperationException("Multiple destinations found with name '"
								+ destinationName + "'. Please use an alias to refer to one of them.");
					}

					found = conf;
				}
			}

			return found;
		}

		return null;
	}

	public Boolean hasChildItemConf() {
		return utilities.listHasElement(this.childItemConf);
	}

	public void setRelatedEtlConfig(EtlConfiguration relatedEtlConf) {
		this.setRelatedEtlConf(relatedEtlConf);

		if (this.hasChildItemConf()) {

			for (EtlChildItemConfiguration child : this.getChildItemConf()) {
				child.setRelatedEtlConf(relatedEtlConf);
			}
		}
	}

	public Boolean isDisabled() {
		return disabled != null && disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getConfigCode() {
		return hasManualConfigCode() ? configCode : source == null ? null : getPrimaryDataSource().getObjectName();
	}

	public void setConfigCode(String configCode) {
		this.configCode = configCode == null ? null : configCode.trim();
	}

	public boolean hasManualConfigCode() {
		return utilities.stringHasValue(this.configCode);
	}

	public String getOriginAppLocationCode() {
		return getRelatedEtlConf().getOriginAppLocationCode();
	}

	@Override
	public DBConnectionInfo getSrcConnInfo() {
		return getRelatedEtlConf().getSrcConnInfo();
	}

	public Boolean hasDstWithJoinFieldsToSrc() {

		for (EtlDestinationConfiguration dst : this.destinations) {

			if (utilities.listHasElement(dst.getJoinFields())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return this.getConfigCode();
	}

	public EtlDatabaseObject retrieveRecordInSrc(EtlDatabaseObject parentRecordInOrigin, Connection srcConn)
			throws DBException {
		JdbcTablePrimaryDataSourceConfiguration jdbcSource = requireJdbcPrimaryDataSource();

		if (!jdbcSource.isComplex()) {
			return DatabaseObjectDAO.getByOid(jdbcSource, parentRecordInOrigin.getObjectId(), srcConn);
		} else {

			EtlDatabaseObjectSearchParams searchParams = new EtlDatabaseObjectSearchParams(jdbcSource, null);

			searchParams.setExtraCondition(jdbcSource.getPrimaryKey().parseToParametrizedStringConditionWithAlias());

			searchParams.setSyncStartDate(getRelatedEtlConf().getStartDate());

			SearchClauses<EtlDatabaseObject> searchClauses = searchParams.generateSearchClauses(null, null, null,
					srcConn, null);

			searchClauses.addToParameters(parentRecordInOrigin.getObjectId().parseValuesToArray());

			String sql = searchClauses.generateSQL(srcConn);

			EtlDatabaseObject simpleValue = BaseDAO.find(jdbcSource.getLoadHealper(),
					jdbcSource.generateEtlRecordClass(EtlSide.SRC), sql, searchClauses.getParameters(), srcConn);

			return simpleValue;
		}
	}

	public Boolean containsDstTable(String tableName) {

		if (utilities.listHasElement(getDestinations())) {

			for (EtlDestinationConfiguration dst : getDestinations()) {

				if (dst.getName().equals(tableName)) {
					return true;
				}
			}
		} else {

			if (getPrimaryDataSource().getObjectName().equals(tableName)) {
				return true;
			}
		}

		return false;
	}

	public EtlDestinationConfiguration findDestination(EtlOperationConfig operationConfig, String tableName)
			throws DBException {

		if (!containsDstTable(tableName)) {
			return null;
		}

		if (!isFullLoaded()) {
			fullLoad(operationConfig);
		}

		for (EtlDestinationConfiguration dst : getDestinations()) {

			if (dst.getName().equals(tableName)) {
				return dst;
			}
		}
		return null;
	}

	public Boolean isDynamic() {
		return this.getEtlItemSrcConf() != null;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.getRelatedEtlConf();
	}

	public List<EtlItemConfiguration> generateDynamicItems(EtlConfiguration relatedEtlConf, Connection conn)
			throws DBException {

		if (!this.isDynamic()) {
			throw new ForbiddenOperationException(
					"This item [" + this.getConfigCode() + " Is not dynamic!!! You cannot generate Dynamic Items");
		}

		this.setRelatedEtlConf(relatedEtlConf);
		this.getEtlItemSrcConf().setRelatedItemConf(this);

		this.getEtlItemSrcConf().fullLoad(conn);

		EtlDynamicItemSearchParams searchParams = new EtlDynamicItemSearchParams(this.getEtlItemSrcConf());

		List<EtlDatabaseObject> itemsSrc = searchParams.search(null, null, null, conn, conn);

		List<EtlItemConfiguration> items = new ArrayList<>(itemsSrc.size());

		for (EtlDatabaseObject itemSrc : itemsSrc) {
			items.add(this.cloneDynamic(itemSrc, relatedEtlConf, conn));
		}

		return items;
	}

	private EtlItemConfiguration cloneDynamic(EtlDatabaseObject schemaInfoSrc, EtlConfiguration relatedEtlConf,
			Connection conn) throws DBException {

		EtlItemConfiguration item = new EtlItemConfiguration();

		item.setRelatedEtlConfig(relatedEtlConf);

		if (hasManualConfigCode()) {
			item.setConfigCode(this.configCode);
		}

		JdbcTablePrimaryDataSourceConfiguration clonedPrimary = new JdbcTablePrimaryDataSourceConfiguration();
		item.setSource(sourceWithJdbcPrimary(clonedPrimary));
		item.prepareSourceConfiguration();
		clonedPrimary.copyFromOther(this.requireJdbcPrimaryDataSource(), schemaInfoSrc, item, conn);

		if (this.hasDestinations()) {
			item.setDestinations(EtlDestinationConfiguration.copyAll(this.getDestinations()));
		}
		item.setThreadingMode(this.getParallelProcessingStrategyType());
		item.setDisabled(this.isDisabled());
		item.setManualMapPrimaryKeyOnField(this.getManualMapPrimaryKeyOnField());
		item.setCreateDstTableIfNotExists(this.isCreateDstTableIfNotExists());
		item.setTesting(this.isTesting());
		item.setRelatedEtlSchemaObject(schemaInfoSrc);
		item.setAutoIncrementHandlingType(this.getAutoIncrementHandlingType());
		item.setPrimaryKeyInitialIncrementValue(this.getPrimaryKeyInitialIncrementValue());
		item.tryToReplacePlaceholders(schemaInfoSrc);

		return item;
	}

	public void copyFromOther(EtlItemConfiguration toCopyFrom, EtlConfiguration relatedEtlConf,
			Boolean ignoreMissingParamsOnElements, Connection conn) throws DBException {

		this.setRelatedEtlConfig(relatedEtlConf);
		JdbcTablePrimaryDataSourceConfiguration clonedPrimary = new JdbcTablePrimaryDataSourceConfiguration();
		this.setSource(sourceWithJdbcPrimary(clonedPrimary));
		this.prepareSourceConfiguration();
		clonedPrimary.setIgnoreMissingParameters(ignoreMissingParamsOnElements);
		clonedPrimary.copyFromOther(toCopyFrom.requireJdbcPrimaryDataSource(), null, this, conn);

		if (toCopyFrom.hasDestinations()) {
			this.setDestinations(EtlDestinationConfiguration.copyAll(toCopyFrom.getDestinations()));
		}

		this.setDisabled(toCopyFrom.isDisabled());
		this.setManualMapPrimaryKeyOnField(toCopyFrom.getManualMapPrimaryKeyOnField());
		this.setCreateDstTableIfNotExists(toCopyFrom.isCreateDstTableIfNotExists());
		this.setTesting(toCopyFrom.isTesting());
		this.setAutoIncrementHandlingType(toCopyFrom.getAutoIncrementHandlingType());
		this.setPrimaryKeyInitialIncrementValue(toCopyFrom.getPrimaryKeyInitialIncrementValue());
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {

		if (hasManualConfigCode()) {
			setConfigCode(utilities.tryToReplacePlaceholders(this.configCode, schemaInfoSrc));
		}

		this.getSource().tryToReplacePlaceholders(schemaInfoSrc);

		if (hasDestinations()) {

			for (EtlDestinationConfiguration destination : this.getDestinations()) {
				destination.tryToReplacePlaceholders(schemaInfoSrc);
			}
		}
	}

	public void doMinimalTableInitialization(Connection srcConn, DestinationConnectorConnections destinationConnections)
			throws DatabaseResourceDoesNotExists, DBException, ForbiddenOperationException {

		this.getPrimaryDataSource().tryToLoadSchemaInfo(null, srcConn);

		if (hasDestinations()) {

			for (EtlDestinationConfiguration conf : this.getDestinations()) {
				conf.tryToLoadSchemaInfo(null, destinationConnections.getIfJdbc(conf));
			}
		}
	}

	@Override
	public void tryToLoadFromTemplate() {
		Boolean disabled = this.isDisabled();
		String manuallyConfiguredCode = this.configCode;

		super.tryToLoadFromTemplate();

		setDisabled(disabled);

		if (utilities.stringHasValue(manuallyConfiguredCode)) {
			setConfigCode(manuallyConfiguredCode);
		}
	}

	@Override
	public Map<String, Object> retrieveAllAvailableTemplateParameters() {
		Map<String, Object> allParameters = new HashMap<>();

		Map<String, Object> ownParameters = super.retrieveAllAvailableTemplateParameters();

		if (ownParameters != null && !ownParameters.isEmpty()) {
			allParameters.putAll(ownParameters);
		}

		return allParameters;
	}

	public void ensureEtlStageTableExists(EtlCounter counter, EtlOperationConfig operationConfig, Connection srcConn)
			throws DBException {

		try (DestinationConnectorConnections destinationConnections = createDestinationConnections(this)) {
			ensureEtlStageTableExists(counter, operationConfig, srcConn, destinationConnections);
		}
	}

	public void ensureEtlStageTableExists(EtlCounter counter, EtlOperationConfig operationConfig, Connection srcConn,
			DestinationConnectorConnections destinationConnections) throws DBException {
		counter.increase();

		this.fullLoad(operationConfig);

		PrimaryDataSourceConfiguration primaryDataSource = getPrimaryDataSource();

		if (!(primaryDataSource instanceof JdbcTablePrimaryDataSourceConfiguration)
				|| !((JdbcTablePrimaryDataSourceConfiguration) primaryDataSource).doNotUseAsDatasource()) {
			StageAreaStorageManager.ensureSourceStageTables(primaryDataSource, srcConn);
		}

		if (hasDestinations()) {

			for (EtlDestinationConfiguration destination : this.getDestinations()) {

				if (destinationConnections.isJdbc(destination)) {
					destination.requireJdbcTableDestination().ensureEtlStageTableExists(counter, srcConn,
							destinationConnections);
				}
			}
		}

		if (hasChildItemConf()) {

			for (EtlItemConfiguration child : this.getChildItemConf()) {
				child.ensureEtlStageTableExists(counter, operationConfig, srcConn);
			}
		}
	}

	public List<EtlDataSource> collectAllAvaliableDataSources(Connection conn) {
		List<EtlDataSource> ds = new ArrayList<>();

		PrimaryDataSourceConfiguration primary = this.getPrimaryDataSource();

		if (primary instanceof EtlDataSource && (!(primary instanceof JdbcTablePrimaryDataSourceConfiguration)
				|| !((JdbcTablePrimaryDataSourceConfiguration) primary).doNotUseAsDatasource())) {
			ds.add(primary);
			ds.addAll(this.getSource().getAvailableAdditionalDataSources());
		}

		if (primary instanceof JdbcTablePrimaryDataSourceConfiguration
				&& ((JdbcTablePrimaryDataSourceConfiguration) primary).hasExpansionDs()) {
			ds.add(((JdbcTablePrimaryDataSourceConfiguration) primary).getExpansionDataSource());
		}

		if (primary instanceof JdbcTablePrimaryDataSourceConfiguration
				&& ((JdbcTablePrimaryDataSourceConfiguration) primary).hasAuxExtractTable()) {
			ds.addAll(((JdbcTablePrimaryDataSourceConfiguration) primary).getAuxExtractTable());
		}

		return ds;
	}

	public boolean ignoreNoDstIssue() {
		return hasAllDestinationsDisabled() || allDestinationsIgnoreNoDstIssue();
	}

	public boolean hasAllDestinationsDisabled() {

		for (EtlDestinationConfiguration destination : this.getDestinations()) {

			if (!destination.isDisabled()) {
				return false;
			}
		}

		return true;
	}

	public boolean allDestinationsIgnoreNoDstIssue() {

		for (EtlDestinationConfiguration destination : this.getDestinations()) {

			if (!destination.ignoreNoDstIssue()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof EtlItemConfiguration) {
			EtlItemConfiguration objAsItem = (EtlItemConfiguration) obj;

			if (this.getConfigCode() != null && objAsItem.getConfigCode() != null) {
				return this.getConfigCode().equals(objAsItem.getConfigCode());
			}
		}

		return super.equals(obj);
	}

	public boolean canQuickTransform(EtlProcessor processor) {

		if (this.getDestinations().size() != 1) {
			return false;
		}

		EtlDestinationConfiguration destination = this.getDestinations().get(0);
		ConnectorType connectorType = getResolvedDstResource(destination).getEndpoint().getType();

		if (connectorType == ConnectorType.FILE) {
			return true;
		}

		if (connectorType == ConnectorType.JDBC) {
			EtlDstType dstType = processor.determineDstType(destination.requireJdbcTableDestination());

			if (dstType.isFile()) {
				return true;
			}
		}

		if (this.getRelatedEtlConf().isDoNotTransformsPrimaryKeys()) {
			return true;
		}

		return false;
	}
}
