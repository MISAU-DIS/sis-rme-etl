package org.openmrs.module.epts.etl.utilities.db.conn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author jpboane
 */
public class DBConnectionService {
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

		int maxActive = dbConnInfo.getMaxActiveConnections() > 0 ? dbConnInfo.getMaxActiveConnections() : 16;

		int maxIdle = dbConnInfo.getMaxIdleConnections() > 0 ? dbConnInfo.getMaxIdleConnections() : 8;

		int minIdle = dbConnInfo.getMinIdleConnections() > 0 ? dbConnInfo.getMinIdleConnections() : 2;

		this.dataSource.setInitialSize(minIdle);
		this.dataSource.setMinIdle(minIdle);
		this.dataSource.setMaxIdle(maxIdle);
		this.dataSource.setMaxActive(maxActive);
		this.dataSource.setMaxWait(30_000);

		this.dataSource.setDefaultAutoCommit(dbConnInfo.isAutoCommit());
		this.dataSource.setDefaultTransactionIsolation(dbConnInfo.getIsolationLevel().level);

		/*
		 * Validação.
		 */
		this.dataSource.setTestOnConnect(true);
		this.dataSource.setTestOnBorrow(true);
		this.dataSource.setTestWhileIdle(true);

		this.dataSource.setValidationQuery("/* ping */ SELECT 1");
		// this.dataSource.setValidationQueryTimeout(5);
		this.dataSource.setValidationInterval(30_000);
		this.dataSource.setLogValidationErrors(true);

		/*
		 * Limpeza periódica.
		 */
		this.dataSource.setTimeBetweenEvictionRunsMillis(60_000);
		this.dataSource.setMinEvictableIdleTimeMillis(5 * 60_000);

		/*
		 * Renovação obrigatória.
		 *
		 * Deve ser menor que wait_timeout e que os timeouts de firewall, proxy ou
		 * balanceador.
		 */
		this.dataSource.setMaxAge(20 * 60_000L);

		/*
		 * Diagnóstico de conexões não devolvidas.
		 */
		this.dataSource.setRemoveAbandoned(true);
		this.dataSource.setRemoveAbandonedTimeout(600);
		this.dataSource.setLogAbandoned(true);
		this.dataSource.setAbandonWhenPercentageFull(75);
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

	private Connection openConnection() throws DBException {

		final int maxAttempts = 5;
		final long retryDelayMillis = 2_000L;

		SQLException lastException = null;

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {

			getDbConnInfo().getRelatedEtlConf().debug(
					"Obtendo conexao. attempt={}/{}, active={}, idle={}, size={}, waitCount={}", attempt, maxAttempts,
					dataSource.getActive(), dataSource.getIdle(), dataSource.getSize(), dataSource.getWaitCount());

			try {
				Connection connection = dataSource.getConnection();

				getDbConnInfo().getRelatedEtlConf().trace(
						"Conexão obtida. attempt={}, active={}, idle={}, size={}, waitCount={}", attempt,
						dataSource.getActive(), dataSource.getIdle(), dataSource.getSize(), dataSource.getWaitCount());

				return connection;

			} catch (SQLException e) {
				lastException = e;

				getDbConnInfo().getRelatedEtlConf()
						.err("Falha ao obter conexão. attempt={}/{}, active={}, idle={}, "
								+ "size={}, waitCount={}, sqlState={}, errorCode={}, message={}", e, attempt,
								maxAttempts, dataSource.getActive(), dataSource.getIdle(), dataSource.getSize(),
								dataSource.getWaitCount(), e.getSQLState(), e.getErrorCode(), e.getMessage());

				logExceptionChain(e);

				if (isUnknownDatabase(e)) {
					throw new DBException(e);
				}

				if (!isTransientConnectionFailure(e)) {
					throw new DBException(e);
				}

				if (attempt < maxAttempts) {
					sleepBeforeRetry(retryDelayMillis);
				}
			}
		}

		throw new DBException(lastException);
	}

	private boolean isUnknownDatabase(SQLException e) {
		return DBUtilities.MYSQL_DATABASE.equals(DBUtilities.determineDataBaseFromException(e))
				&& DBException.checkIfExceptionContainsMessage(e, "Unknown database");
	}

	private boolean isTransientConnectionFailure(SQLException e) {

		SQLException current = e;

		while (current != null) {
			String sqlState = current.getSQLState();

			/*
			 * SQLState classe 08 = connection exception.
			 */
			if (sqlState != null && sqlState.startsWith("08")) {
				return true;
			}

			Throwable cause = current.getCause();

			while (cause != null) {
				if (cause instanceof java.net.SocketException || cause instanceof java.net.ConnectException
						|| cause instanceof java.net.SocketTimeoutException) {
					return true;
				}

				cause = cause.getCause();
			}

			current = current.getNextException();
		}

		return false;
	}

	private void logExceptionChain(SQLException exception) {

		SQLException current = exception;
		int index = 1;

		while (current != null) {
			getDbConnInfo().getRelatedEtlConf().err("SQLException[{}]: type={}, sqlState={}, errorCode={}, message={}",
					current, index, current.getClass().getName(), current.getSQLState(), current.getErrorCode(),
					current.getMessage());

			current = current.getNextException();
			index++;
		}
	}

	private void sleepBeforeRetry(long millis) throws EtlConfException {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new EtlConfException(e);
		}
	}
}
