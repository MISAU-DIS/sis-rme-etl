package org.openmrs.module.epts.etl.model.pojo.generic;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.JdbcTableDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.exceptions.RequireJdbcException;

class EtlObjectConfigurationRelatedTableTest {

	@Test
	void shouldReturnItselfForAJdbcTableConfiguration() {
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();

		assertSame(source, source.retrieveRelatedTableConf());
		assertSame(source, source.requireRelatedTableConf());
	}

	@Test
	void shouldResolveTheJdbcAdapterFromALogicalDestination() {
		EtlDestinationConfiguration destination = new EtlDestinationConfiguration();
		JdbcTableDestinationConfiguration table = new JdbcTableDestinationConfiguration();
		destination.setTableDataDestination(table);

		assertSame(table, destination.retrieveRelatedTableConf());
		assertSame(table, destination.requireRelatedTableConf());
	}

	@Test
	void shouldNotPretendThatANonJdbcConfigurationHasATable() {
		JsonPrimaryDataSourceConfiguration source = new JsonPrimaryDataSourceConfiguration();

		assertNull(source.retrieveRelatedTableConf());
		assertThrows(RequireJdbcException.class, source::requireRelatedTableConf);
	}
}
