package org.openmrs.module.epts.etl.etl.model.persistence;

import java.sql.Connection;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** A unit of auxiliary persistence produced by an ETL worker. */
public interface EnginePersistenceRequest {

	PersistenceType getType();

	int size();

	void persist(Connection srcConn, Connection dstConn) throws DBException;
}
