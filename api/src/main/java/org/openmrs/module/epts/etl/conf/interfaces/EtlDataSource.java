package org.openmrs.module.epts.etl.conf.interfaces;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.EtlField;
import org.openmrs.module.epts.etl.conf.datasource.PreparedQuery;
import org.openmrs.module.epts.etl.conf.types.DbmsType;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public interface EtlDataSource extends EtlDatabaseObjectConfiguration {

	static final Object LOCK = new Object();

	String getName();

	PreparedQuery getDefaultPreparedQuery();

	void setDefaultPreparedQuery(PreparedQuery defaultPreparedQuery);

	default Boolean isPrepared() {
		return this.getDefaultPreparedQuery() != null;
	}

	public static List<EtlDataSource> extractDataSourceFromObjects(List<EtlDatabaseObject> avaliableSrcObjects) {

		if (utilities.listHasElement(avaliableSrcObjects)) {

			Set<EtlDataSource> datasources = new HashSet<>();

			for (EtlDatabaseObject obj : avaliableSrcObjects) {
				if (obj.getRelatedConfiguration() instanceof EtlDataSource) {

					datasources.add((EtlDataSource) obj.getRelatedConfiguration());
				}
			}

			if (!datasources.isEmpty()) {
				return List.copyOf(datasources);
			}

		}

		return null;
	}

	default void prepare(List<EtlDatabaseObject> avaliableSrcObjects, Connection conn) throws DBException {
		if (isPrepared()) {
			return;
		}

		synchronized (LOCK) {
			PreparedQuery query = PreparedQuery.prepare(this, extractDataSourceFromObjects(avaliableSrcObjects),
					getRelatedEtlConf(), DbmsType.determineFromConnection(conn));

			setDefaultPreparedQuery(query);
		}
	}

	default List<EtlDatabaseObject> searchRecords(Engine<? extends EtlDatabaseObject> engine,
			EtlDatabaseObject parentSrcObject, List<EtlDatabaseObject> auxDataSourceObjects, Connection srcConn)
			throws DBException {

		List<EtlDatabaseObject> avaliableSrcObjects = parentSrcObject != null ? utilities.parseToList(parentSrcObject)
				: null;

		if (!isPrepared()) {
			prepare(avaliableSrcObjects, srcConn);
		}

		return this.getDefaultPreparedQuery().query(null, parentSrcObject, parentSrcObject, avaliableSrcObjects,
				srcConn);
	}

	default void loadOwnFieldsToEtlFields(List<EtlField> etlFields, Boolean presereOriginalNames) {
		if (etlFields == null)
			throw new ForbiddenOperationException("The 'etlFields' is null");

		if (this instanceof MainJoiningEntity) {
			MainJoiningEntity dsAsJoining = (MainJoiningEntity) this;

			if (!dsAsJoining.doNotUseAsDatasource()) {
				etlFields.addAll(EtlField.converteFromDataSourceFields(this, presereOriginalNames));
			}

			if (dsAsJoining.hasAuxExtractTable()) {
				for (JoinableEntity j : dsAsJoining.getJoiningTable()) {
					if (!j.doNotUseAsDatasource()) {
						j.loadOwnFieldsToEtlFields(etlFields, false);
					}
				}
			}
		} else {

			if (this.hasFields()) {
				etlFields.addAll(EtlField.converteFromDataSourceFields(this, presereOriginalNames));
			}
		}
	}

	/**
	 * Gets the SQL query associated with this data source.
	 * <p>
	 * This query is typically used to fetch related data from the database.
	 *
	 * @return the SQL query string
	 */
	String getQuery();

	@SuppressWarnings("deprecation")
	default EtlDatabaseObject newInstance() {
		try {
			EtlDatabaseObject obj = getSyncRecordClass().newInstance();
			obj.setRelatedConfiguration(this);

			return obj;
		} catch (InstantiationException | IllegalAccessException | ForbiddenOperationException e) {
			throw new RuntimeException(e);
		}
	}

	default void init(EtlDataConfiguration relatedParent, EtlDatabaseObject etlSchemaObject, Connection srcConn,
			Connection dstConn) throws DBException {

		this.applyIncludes();

		if (relatedParent == null)
			throw new EtlExceptionImpl("RelatedParent cannot be null!");

		this.setParentConf(relatedParent);

		this.tryToLoadFromTemplate();

		Connection conn = null;

		if (this instanceof EtlSrcConf) {
			conn = srcConn;
		} else if (this instanceof EtlDstConf) {
			conn = dstConn;
		} else
			throw new EtlExceptionImpl("An EtlDatasource must be either a EtlSrcConf or EtlDstConf!!!!");

		this.tryToLoadSchemaInfo(etlSchemaObject, conn);
	}

	void setParentConf(EtlDataConfiguration relatedParent);

	void tryToLoadSchemaInfo(EtlDatabaseObject schemaInfoSrc, Connection conn)
			throws DBException, ForbiddenOperationException, DatabaseResourceDoesNotExists;
}
