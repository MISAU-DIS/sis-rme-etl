package org.openmrs.module.epts.etl.databasemodelgeneration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.DataModelConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationContext.Side;

class DatabaseModelGenerationContextTest {

	@Test
	void shouldResolveModelIdentityWithoutAConnectionInfo() {
		DataModelConfiguration dataModel = new DataModelConfiguration();
		dataModel.setSrcDataModelId("openmrs_2_6_sesp");
		dataModel.setSrcPojoPackageName("openmrs_2_6.sesp");
		dataModel.setDstDataModelId("openmrs_2_6_sisrme");
		dataModel.setDstPojoPackageName("openmrs_2_6.sisrme");

		DatabaseModelGenerationContext source = DatabaseModelGenerationContext.source(dataModel, null);
		DatabaseModelGenerationContext destination = DatabaseModelGenerationContext.destination(dataModel, null);

		assertEquals(Side.SRC, source.getSide());
		assertEquals("openmrs_2_6_sesp", source.getDataModelId());
		assertEquals("openmrs_2_6.sesp", source.getPojoPackageName());
		assertEquals(Side.DST, destination.getSide());
		assertEquals("openmrs_2_6_sisrme", destination.getDataModelId());
		assertEquals("openmrs_2_6.sisrme", destination.getPojoPackageName());
	}

	@Test
	void shouldResolveModelIdentityFromTheExplicitEtlSide() {
		DataModelConfiguration dataModel = new DataModelConfiguration();
		dataModel.setSrcDataModelId("source-model");
		dataModel.setSrcPojoPackageName("source.model");
		dataModel.setDstDataModelId("destination-model");
		dataModel.setDstPojoPackageName("destination.model");
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setDataModel(dataModel);

		assertEquals("source.model", configuration.getPojoPackage(EtlSide.SRC));
		assertEquals("destination.model", configuration.getPojoPackage(EtlSide.DST));
		assertEquals("source-model", configuration.getDataModelId(EtlSide.SRC));
		assertEquals("destination-model", configuration.getDataModelId(EtlSide.DST));
		assertEquals(Side.SRC, configuration.createDatabaseModelGenerationContext(EtlSide.SRC, null).getSide());
		assertEquals(Side.DST, configuration.createDatabaseModelGenerationContext(EtlSide.DST, null).getSide());
	}
}
