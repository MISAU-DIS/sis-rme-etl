package org.openmrs.module.epts.etl.model.base;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class DBOperation {

	private String sql;

	private Object[] params;

	private Connection conn;

	private int qtyTry;

	private int maxTry;

	private DBException exception;

	private List<Long> generatedIds;

	public DBOperation(String sql, Object[] params, Connection conn, int maxTry, DBException exception) {
		this.sql = sql;
		this.params = params;
		this.conn = conn;
		this.maxTry = maxTry;
		this.qtyTry = 0;
		this.exception = exception;
	}

	public List<Long> getGeneratedIds() {
		return generatedIds;
	}

	public void retryDueTemporaryDBError(String error) throws DBException {

		if (qtyTry < maxTry) {
			qtyTry++;

			logAsWarn(error);

			try {
				this.generatedIds = BaseDAO.executeQueryWithoutRetry(sql, params, conn);

				BaseDAO.LOG.warn("RECOVERED AFTER {}", error.toUpperCase());
			} catch (DBException e) {
				this.exception = e;

				if (e.isTemporaryDBErrr(conn)) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
					}

					retryDueTemporaryDBError(error);
				} else {
					throw e;
				}
			}
		} else {
			throw this.exception;
		}
	}

	private void logAsWarn(String error) {
		String queryInfo = BaseDAO.generateMInimalQueryInfo(sql, params);

		BaseDAO.LOG.warn("{} DETECTED WHILE EXECUTING: {} \nRETRYING OPERATION [{}] OF [{}]", error, queryInfo, qtyTry,
				maxTry);
	}

	@SuppressWarnings("unused")
	private void logAsErr(String error) {
		String queryInfo = BaseDAO.generateMInimalQueryInfo(sql, params);

		BaseDAO.LOG.err(error + " DETECTED WHILE EXECUTING: " + queryInfo + " \nRETRYING OPERATION [" + qtyTry
				+ "] OF [" + maxTry + "]", exception);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public int getQtyTry() {
		return qtyTry;
	}

	public void setQtyTry(int qtyTry) {
		this.qtyTry = qtyTry;
	}

	public int getMaxTry() {
		return maxTry;
	}

	public void setMaxTry(int maxTry) {
		this.maxTry = maxTry;
	}
}
