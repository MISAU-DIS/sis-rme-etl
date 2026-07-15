package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public interface EtlRecordTransformer {

	EtlDatabaseObject transform(EtlProcessor processor, EtlDatabaseObject srcObject, DstConf dstConf,
			EtlDatabaseObject migratedDstParent, TransformationType transformationType, Connection srcConn,
			Connection dstConn) throws DBException, EtlTransformationException;

	EtlDatabaseObject transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			Set<EtlDatabaseObject> collectedSrcObjects, DstConf dstConf, EtlDatabaseObject migratedDstParent,
			TransformationType transformationType, Connection srcConn, Connection dstConn)
			throws DBException, EtlTransformationException;

	Set<EtlDatabaseObject> collectSourceObjects(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject srcObjectExpansion, EtlDatabaseObject dstObject, EtlDatabaseObject migratedDstParent,
			DstConf dstConf, TransformationType transformationType, Connection srcConn) throws DBException;

}
