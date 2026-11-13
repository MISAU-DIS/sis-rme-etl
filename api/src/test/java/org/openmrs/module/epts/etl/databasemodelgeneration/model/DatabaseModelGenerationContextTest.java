package org.openmrs.module.epts.etl.databasemodelgeneration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.DataModelConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
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
	void shouldResolveModelIdentityFromTheResourceDirection() {
		DataModelConfiguration dataModel = new DataModelConfiguration();
		dataModel.setSrcDataModelId("source-model");
		dataModel.setSrcPojoPackageName("source.model");
		dataModel.setDstDataModelId("destination-model");
		dataModel.setDstPojoPackageName("destination.model");
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setDataModel(dataModel);

		ConnectorResource sourceResource = resource(EtlSide.SRC);
		ConnectorResource destinationResource = resource(EtlSide.DST);

		assertEquals("source.model", configuration.getPojoPackage(EtlSide.SRC));
		assertEquals("destination.model", configuration.getPojoPackage(EtlSide.DST));
		assertEquals("source-model", configuration.getDataModelId(EtlSide.SRC));
		assertEquals("destination-model", configuration.getDataModelId(EtlSide.DST));
		assertEquals(Side.SRC, configuration.createDatabaseModelGenerationContext(sourceResource, null).getSide());
		assertEquals(Side.DST, configuration.createDatabaseModelGenerationContext(destinationResource, null).getSide());
	}

	private ConnectorResource resource(EtlSide direction) {
		return new ConnectorResource() {
			@Override
			public String getName() {
				return "model";
			}

			@Override
			public EtlSide getDirection() {
				return direction;
			}

			@Override
			public ConnectorEndpoint getEndpoint() {
				return null;
			}

			@Override
			public <T> T unwrap(Class<T> nativeType) {
				throw new UnsupportedOperationException();
			}
		};
	}
}
