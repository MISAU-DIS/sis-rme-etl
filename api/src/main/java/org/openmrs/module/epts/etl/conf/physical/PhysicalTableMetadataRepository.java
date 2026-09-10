package org.openmrs.module.epts.etl.conf.physical;

import java.io.IOException;
import java.util.Optional;

/** Source-neutral lookup contract for physical table metadata. */
public interface PhysicalTableMetadataRepository {

	Optional<PhysicalTableMetadata> find(PhysicalTableKey key) throws IOException;
}
