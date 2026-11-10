package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.util.Set;

import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public interface EtlRecordTransformer {

	EtlDatabaseObject transform(EtlProcessor processor, EtlDatabaseObject srcObject, EtlDestinationConfiguration dstConf,
			EtlDatabaseObject migratedDstParent, TransformationType transformationType, ConnectorRecordReader<?> reader,
			ConnectorRecordWriter<?> writer) throws DBException, EtlTransformationException;

	EtlDatabaseObject transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			Set<EtlDatabaseObject> collectedSrcObjects, EtlDestinationConfiguration dstConf, EtlDatabaseObject migratedDstParent,
			TransformationType transformationType, ConnectorRecordReader<?> reader, ConnectorRecordWriter<?> writer)
			throws DBException, EtlTransformationException;

	Set<EtlDatabaseObject> collectSourceObjects(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject srcObjectExpansion, EtlDatabaseObject dstObject, EtlDatabaseObject migratedDstParent,
			EtlDestinationConfiguration dstConf, TransformationType transformationType, ConnectorRecordReader<?> reader,
			ConnectorRecordWriter<?> writer) throws DBException;

}
