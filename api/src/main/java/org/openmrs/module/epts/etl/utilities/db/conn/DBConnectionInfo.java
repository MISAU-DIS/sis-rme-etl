package org.openmrs.module.epts.etl.utilities.db.conn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlDBConnectionType;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.db.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.SQLUtilities;

public class DBConnectionInfo extends AbstractEtlDataConfiguration {

	private static final EtlLogger LOG = EtlLogger.getLogger(DBConnectionInfo.class);

	public static CommonUtilities utilities = CommonUtilities.getInstance();
	private String dataBaseUserName;

	private String dataBaseUserPassword;

	private String connectionURI;

	private String driveClassName;

	private String schema;

	private int maxActiveConnections;

	private int maxIdleConnections;

	private int minIdleConnections;

	private String databaseSchemaPath;

	private String dbHost;

	private Integer dbHostPort;

	private DBConnectionIsolationLevel isolationLevel;

	private DBConnectionService dbService;

	private EtlConfiguration relatedEtlConf;

	private Boolean defaultAutoCommit;

	private EtlDBConnectionType connType;

	/**
	 * @deprecated Configure source and destination packages in
	 *             DataModelConfiguration.
	 */
	@Deprecated
	private String pojoPackageName;

	public DBConnectionInfo() {
		isolationLevel = DBConnectionIsolationLevel.TRANSACTION_REPEATABLE_READ;
	}

	public DBConnectionInfo(String dataBaseUserName, String dataBaseUserPassword, String connectionURI,
			String driveClassName) {
		this.dataBaseUserName = dataBaseUserName;
		this.dataBaseUserPassword = dataBaseUserPassword;
		this.connectionURI = connectionURI;
		this.driveClassName = driveClassName;
	}

	public DBConnectionInfo(String dataBaseUserName, String dataBaseUserPassword, String connectionURI, String schema,
			String driveClassName) {
		this(dataBaseUserName, dataBaseUserPassword, connectionURI, driveClassName);

		this.schema = schema;
	}

	public EtlDBConnectionType getConnType() {
		return connType;
	}

	public void setConnType(EtlDBConnectionType connType) {
		this.connType = connType;
	}

	public boolean isSrcConn() {
		return this.getConnType() != null && this.getConnType().isSrc();
	}

	public boolean isDstConn() {
		return this.getConnType() != null && this.getConnType().isDst();
	}

	public boolean isMainConn() {
		return this.getConnType() != null && this.getConnType().isMain();
	}

	public Boolean getDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

	public boolean isAutoCommit() {
		return isTrue(getDefaultAutoCommit());
	}

	public DBConnectionIsolationLevel getIsolationLevel() {
		return isolationLevel;
	}

	public void setIsolationLevel(DBConnectionIsolationLevel isolationLevel) {
		this.isolationLevel = isolationLevel;
	}

	public EtlConfiguration getRelatedEtlConf() {
		return relatedEtlConf;
	}

	public void setRelatedEtlConf(EtlConfiguration relatedEtlConf) {
		this.relatedEtlConf = relatedEtlConf;
	}

	public void finalize() {
		if (dbService != null)
			dbService.finalize();
	}

	private DBConnectionService getRelatedDBConnectionService() {
		if (this.dbService == null)
			initRelatedDBConnectionService();

		return this.dbService;
	}

	public OpenConnection openConnection(BaseConfiguration opendFrom) throws DBException {
		return getRelatedDBConnectionService().openConnection(opendFrom);
	}

	private synchronized void initRelatedDBConnectionService() {
		if (dbService == null) {
			dbService = DBConnectionService.init(this);
		}
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public Integer getDbHostPort() {
		return dbHostPort;
	}

	public void setDbHostPort(Integer dbHostPort) {
		this.dbHostPort = dbHostPort;
	}

	public String getDatabaseSchemaPath() {
		return databaseSchemaPath;
	}

	public void setDatabaseSchemaPath(String databaseSchemaPath) {
		this.databaseSchemaPath = databaseSchemaPath;
	}

	public int getMaxActiveConnections() {
		return maxActiveConnections;
	}

	public void setMaxActiveConnections(int maxActiveConnections) {
		this.maxActiveConnections = maxActiveConnections;
	}

	public int getMaxIdleConnections() {
		return maxIdleConnections;
	}

	public void setMaxIdleConnections(int maxIdleConnections) {
		this.maxIdleConnections = maxIdleConnections;
	}

	public int getMinIdleConnections() {
		return minIdleConnections;
	}

	public void setMinIdleConnections(int minIdleConnections) {
		this.minIdleConnections = minIdleConnections;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getDataBaseUserName() {
		return dataBaseUserName;
	}

	public void setDataBaseUserName(String dataBaseUserName) {
		this.dataBaseUserName = dataBaseUserName;
	}

	public String getDataBaseUserPassword() {
		return dataBaseUserPassword;
	}

	public void setDataBaseUserPassword(String dataBaseUserPassword) {
		this.dataBaseUserPassword = dataBaseUserPassword;
	}

	public String getConnectionURI() {
		return connectionURI;
	}

	public void setConnectionURI(String connectionURI) {
		this.connectionURI = connectionURI;
	}

	public String getDriveClassName() {
		return driveClassName;
	}

	public void setDriveClassName(String driveClassName) {
		this.driveClassName = driveClassName;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DBConnectionInfo))
			return false;

		DBConnectionInfo dbConn = (DBConnectionInfo) obj;

		return this.connectionURI.equals(dbConn.connectionURI);
	}

	public DBConnectionInfo clone(String connURI) {
		DBConnectionInfo db = new DBConnectionInfo(dataBaseUserName, dataBaseUserPassword, connURI, driveClassName);

		return db;
	}

	public static DBConnectionInfo loadFromFile(File file) throws IOException {
		DBConnectionInfo conf = utilities.loadObjectFormJSON(DBConnectionInfo.class,
				new String(Files.readAllBytes(file.toPath())));

		return conf;
	}

	public static DBConnectionInfo loadFromJson(String json) {
		return utilities.loadObjectFormJSON(DBConnectionInfo.class, json);
	}

	public String determineSchema() {

		if (utilities.stringHasValue(this.schema))
			return schema;

		if (isMySQLConnection()) {
			String[] urlParts = this.getConnectionURI().split("/");

			return urlParts[urlParts.length - 1].split("\\?")[0];
		}

		throw new ForbiddenOperationException("Unrecognized dbms");
	}

	public boolean isMySQLConnection() {
		return this.connectionURI.toUpperCase().contains("MYSQL");
	}

	public void copyFromOther(DBConnectionInfo toCopyFrom) {
		this.setDataBaseUserName(toCopyFrom.getDataBaseUserName());
		this.setDataBaseUserPassword(toCopyFrom.getDataBaseUserPassword());
		this.setConnectionURI(toCopyFrom.getConnectionURI());
		this.setDriveClassName(toCopyFrom.getDriveClassName());
		this.setSchema(toCopyFrom.getSchema());
		this.setMaxActiveConnections(toCopyFrom.getMaxActiveConnections());
		this.setMaxIdleConnections(toCopyFrom.getMaxIdleConnections());
		this.setMinIdleConnections(toCopyFrom.getMinIdleConnections());
		this.setDatabaseSchemaPath(toCopyFrom.getDatabaseSchemaPath());
	}

	public void tryToLoadPlaceHolders(EtlDatabaseObject schemaInfoSrc) {
		this.setDataBaseUserName(tryToLoadPlaceHolders(this.getDataBaseUserName(), schemaInfoSrc));
		this.setDataBaseUserPassword(tryToLoadPlaceHolders(this.getDataBaseUserPassword(), schemaInfoSrc));
		this.setConnectionURI(tryToLoadPlaceHolders(this.getConnectionURI(), schemaInfoSrc));
		this.setSchema(tryToLoadPlaceHolders(this.getSchema(), schemaInfoSrc));
	}

	public void tryToLoadPlaceHolders(EtlConfiguration schemaInfoSrc) {
		this.setDataBaseUserName(tryToLoadPlaceHolders(this.getDataBaseUserName(), schemaInfoSrc));
		this.setDataBaseUserPassword(tryToLoadPlaceHolders(this.getDataBaseUserPassword(), schemaInfoSrc));
		this.setConnectionURI(tryToLoadPlaceHolders(this.getConnectionURI(), schemaInfoSrc));
		this.setSchema(tryToLoadPlaceHolders(this.getSchema(), schemaInfoSrc));
	}

	private String tryToLoadPlaceHolders(String str, EtlConfiguration schemaInfoSrc) {
		return SQLUtilities.tryToReplaceParamsInQuery(str, schemaInfoSrc);
	}

	private String tryToLoadPlaceHolders(String str, EtlDatabaseObject schemaInfoSrc) {
		return SQLUtilities.tryToReplaceParamsInQuery(str, schemaInfoSrc);
	}

	public boolean hasDatabaseSchemaPath() {
		return utilities.stringHasValue(this.getDatabaseSchemaPath());
	}

	public void tryToExtractHostInfoFromMysqlUri() {
		String jdbcUrl = getConnectionURI();

		if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:mysql://")) {
			throw new IllegalArgumentException("Invalid MySQL JDBC URL: " + jdbcUrl);
		}

		String withoutPrefix = jdbcUrl.substring("jdbc:mysql://".length());

		int slashIndex = withoutPrefix.indexOf("/");
		String hostPortPart = (slashIndex != -1) ? withoutPrefix.substring(0, slashIndex) : withoutPrefix;

		if (hostPortPart.contains(",")) {
			hostPortPart = hostPortPart.split(",")[0];
		}

		String host;
		int port = 3306;

		if (hostPortPart.contains(":")) {
			String[] parts = hostPortPart.split(":");
			host = parts[0];
			port = Integer.parseInt(parts[1]);
		} else {
			host = hostPortPart;
		}

		this.setDbHost("localhost".equalsIgnoreCase(host) ? "127.0.0.1" : host);
		this.setDbHostPort(port);
	}

	public void restoreDump(EtlConfiguration etlConf) throws EtlExceptionImpl, DBException {

		String databaseName = this.determineSchema();
		String databaseSchemaFullPath = etlConf.generateDatabaseSchemaFullPath(this);

		logWarn("Database '{}' does not exist but its schema file is available", databaseName);

		logDebug("Creating database '{}' from its schema file", databaseName);

		try {
			DBUtilities.createDb(this, this.determineSchema());

			DBUtilities.runScriptOnDbServer(this, databaseSchemaFullPath);
		} catch (Exception e) {
			logError("An error occurred restoring dump: {}", e, databaseSchemaFullPath);

			try {
				DBUtilities.dropDB(this, this.determineSchema());
			} catch (Exception e1) {
				logError("Error dropping database '{}' after a failed dump restoration", e1, databaseName);
			}

			throw new EtlExceptionImpl(e);
		}
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.relatedEtlConf;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}

	static void logDebug(String msg, Object... arguments) {
		LOG.debug(msg, arguments);
	}

	static void logTrace(String msg, Object... arguments) {
		LOG.trace(msg, arguments);
	}

	static void logInfo(String msg, Object... arguments) {
		LOG.info(msg, arguments);
	}

	static void logWarn(String msg, Object... arguments) {
		LOG.warn(msg, arguments);
	}

	static void logError(String msg, Throwable throwable, Object... arguments) {
		Object[] argumentsWithThrowable = new Object[arguments.length + 1];
		System.arraycopy(arguments, 0, argumentsWithThrowable, 0, arguments.length);
		argumentsWithThrowable[arguments.length] = throwable;
		LOG.err(msg, argumentsWithThrowable);
	}

}
