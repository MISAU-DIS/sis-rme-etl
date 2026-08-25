package org.openmrs.module.epts.etl.etl.model.stage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.module.epts.etl.etl.model.persistence.EnginePersistenceRequest;
import org.openmrs.module.epts.etl.etl.model.persistence.PersistenceType;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Immutable request envelope to generate and persist StageArea information at a
 * safe synchronization point. The source object references are preserved, while
 * the list structure is defensively copied.
 */
public final class StageAreaPersistenceRequest implements EnginePersistenceRequest {

	private final Object owner;

	private final List<EtlDatabaseObject> sourceObjects;

	public StageAreaPersistenceRequest(Object owner, List<EtlDatabaseObject> sourceObjects) {
		this.owner = owner;
		this.sourceObjects = Collections.unmodifiableList(new ArrayList<>(sourceObjects));
	}

	public Object getOwner() {
		return owner;
	}

	public List<EtlDatabaseObject> getSourceObjects() {
		return sourceObjects;
	}

	@Override
	public PersistenceType getType() {
		return PersistenceType.STAGE_AREA;
	}

	@Override
	public int size() {
		return sourceObjects.size();
	}

	@Override
	public void persist(Connection srcConn, Connection dstConn) throws DBException {
		List<EtlStageObjectInfo> stageInfo = new ArrayList<>();
		for (EtlDatabaseObject sourceObject : sourceObjects) {
			stageInfo.add(EtlStageObjectInfo.generate(sourceObject, srcConn, dstConn));
		}
		EtlStageAreaObjectDAO.saveAll(stageInfo, srcConn);
	}
}
