package org.openmrs.module.epts.etl.utilities.db.conn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author jpboane
 */
public class DBConnectionService {

	private static final EtlLogger logger = EtlLogger.getLogger(DBConnectionService.class);

	private final Object LOCK = new Object();

	private static List<DBConnectionService> services = new CopyOnWriteArrayList<DBConnectionService>();

	private DBConnectionInfo dbConnInfo;

	private DataSource dataSource;

	private final List<OpenConnection> openConnections = new CopyOnWriteArrayList<>();

	private DBConnectionService(DBConnectionInfo dbConnInfo) {

		this.dbConnInfo = dbConnInfo;
		this.dataSource = new DataSource();

		this.dataSource.setDriverClassName(dbConnInfo.getDriveClassName());
		this.dataSource.setUrl(dbConnInfo.getConnectionURI());
		this.dataSource.setUsername(dbConnInfo.getDataBaseUserName());
		this.dataSource.setPassword(dbConnInfo.getDataBaseUserPassword());

		this.dataSource.setInitialSize(10);

		this.dataSource
				.setMaxActive(dbConnInfo.getMaxActiveConnections() > 0 ? dbConnInfo.getMaxActiveConnections() : 16);

		this.dataSource.setMaxWait(30_000);

		this.dataSource.setMaxIdle(dbConnInfo.getMaxIdleConnections() > 0 ? dbConnInfo.getMaxIdleConnections() : 8);

		this.dataSource.setMinIdle(dbConnInfo.getMinIdleConnections() > 0 ? dbConnInfo.getMinIdleConnections() : 2);

		this.dataSource.setDefaultAutoCommit(dbConnInfo.isAutoCommit());
		this.dataSource.setDefaultTransactionIsolation(dbConnInfo.getIsolationLevel().level);

		/*
		 * Validação das conexões.
		 */
		this.dataSource.setTestOnConnect(true);
		this.dataSource.setTestOnBorrow(true);
		this.dataSource.setTestWhileIdle(true);

		this.dataSource.setValidationQuery("SELECT 1");
		this.dataSource.setValidationInterval(30_000);
		this.dataSource.setLogValidationErrors(true);

		/*
		 * Limpeza das conexões ociosas.
		 */
		this.dataSource.setTimeBetweenEvictionRunsMillis(60_000);
		this.dataSource.setMinEvictableIdleTimeMillis(5 * 60_000);

		/*
		 * Diagnóstico.
		 */
		this.dataSource.setRemoveAbandoned(false);
		this.dataSource.setLogAbandoned(true);
	}

	@Override
	public void finalize() {
		try {
			this.dataSource.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized DBConnectionService init(String driveClassName, String connectionURI,
			String dataBaseUserName, String dataBaseUserPassword) {
		DBConnectionInfo connInfo = new DBConnectionInfo();

		connInfo.setDriveClassName(driveClassName);
		connInfo.setConnectionURI(connectionURI);
		connInfo.setDataBaseUserName(dataBaseUserName);
		connInfo.setDataBaseUserPassword(dataBaseUserPassword);

		return init(connInfo);
	}

	public DBConnectionInfo getDbConnInfo() {
		return dbConnInfo;
	}

	/**
	 * @param connURI the connection URI for new Service
	 * @return
	 */
	public DBConnectionService clone(String connURI) {
		return init(this.dbConnInfo.clone(connURI));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof DBConnectionService)) {
			return false;
		}

		DBConnectionService objAsService = (DBConnectionService) obj;

		return this.dbConnInfo.equals(objAsService.dbConnInfo);
	}

	private static DBConnectionService retrieveExistingService(DBConnectionInfo info) {
		for (DBConnectionService service : services) {
			if (service.dbConnInfo.equals(info))
				return service;
		}

		return null;
	}

	public synchronized static DBConnectionService init(DBConnectionInfo dbConnInfo) {
		DBConnectionService service = retrieveExistingService(dbConnInfo);

		if (service == null) {
			service = new DBConnectionService(dbConnInfo);

			services.add(service);
		}

		return service;
	}

	@JsonIgnore
	public OpenConnection openConnection(BaseConfiguration openedFrom) throws DBException {
		OpenConnection conn = new OpenConnection(openConnection(), openedFrom, this);
		addOpenConnection(conn);

		return conn;
	}

	private void addOpenConnection(OpenConnection conn) {
		synchronized (LOCK) {
			this.openConnections.add(conn);
		}
	}

	void removeOpenConnection(OpenConnection conn) {
		synchronized (LOCK) {
			this.openConnections.remove(conn);
		}
	}

	private synchronized Connection openConnection() throws DBException {

		int qtyTry = 50;

		SQLException exception = null;

		while (qtyTry >= 0) {
			try {
				Connection conn = this.dataSource.getConnection();

				return conn;
			} catch (SQLException e) {
				exception = e;

				logger.warn("OpenedConnections: " + OpenConnection.qtyOpenedConnections + ", ClosedConnections: "
						+ OpenConnection.qtyClosedConnections);

				if (DBUtilities.determineDataBaseFromException(e).equals(DBUtilities.MYSQL_DATABASE)) {
					if (DBException.checkIfExceptionContainsMessage(e, "Unknown database")) {
						throw new DBException(e);
					}
				}

				logger.error("Nao foi possivel obter a conexao. Tentando novamente obter a conexao novamente...", e);

				TimeCountDown.sleep(2);
			}

			qtyTry--;
		}

		throw new DBException(exception);
	}
}
