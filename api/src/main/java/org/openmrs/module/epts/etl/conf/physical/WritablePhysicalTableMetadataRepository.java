package org.openmrs.module.epts.etl.conf.physical;

import java.io.IOException;

/** Metadata repository which can publish generated snapshots. */
public interface WritablePhysicalTableMetadataRepository extends PhysicalTableMetadataRepository {

	void save(PhysicalTableMetadata metadata) throws IOException;
}
