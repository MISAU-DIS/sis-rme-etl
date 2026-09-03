package org.openmrs.module.epts.etl.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

public class EtlConfigurationDataModelTest {

	@Test
	public void shouldReadAndResolveSourceAndDestinationDataModelConfiguration() throws Exception {
		String json = "{\"dataModel\":{" + "\"databaseObjectInstantiationMode\":\"PRECOMPILED_POJO\","
				+ "\"schemaMetadataMode\":\"PRECOMPILED_WITH_FALLBACK\"," + "\"srcPojoPackageName\":\"source_openmrs\","
				+ "\"dstPojoPackageName\":\"destination_openmrs\"," + "\"srcSchema\":\"openmrs_source\"," 
				+ "\"dstSchema\":\"openmrs_destination\"," + "\"overrideExistingDataModelElement\":true,"
				+ "\"javaFormatterConfigurationFile\":\"conf/eclipse-formatter.xml\","
				+ "\"srcPojoDirectory\":\"project/src/main/java\","
				+ "\"binPojoDirectory\":\"project/target/classes\"},"
				+ "\"srcConnInfo\":{},\"dstConnInfo\":{}}";
		EtlConfiguration configuration = new ObjectMapperProvider().getContext(EtlConfiguration.class).readValue(json,
				EtlConfiguration.class);

		assertEquals(DatabaseObjectInstantiationMode.PRECOMPILED_POJO,
				configuration.getDatabaseObjectInstantiationMode());
		assertEquals(SchemaMetadataMode.PRECOMPILED_WITH_FALLBACK, configuration.getSchemaMetadataMode());
		assertEquals("source_openmrs", configuration.getPojoPackage(configuration.getSrcConnInfo()));
		assertEquals("destination_openmrs", configuration.getPojoPackage(configuration.getDstConnInfo()));
		assertEquals("openmrs_source", configuration.getSchema(configuration.getSrcConnInfo()));
		assertEquals("openmrs_destination", configuration.getSchema(configuration.getDstConnInfo()));
		assertTrue(configuration.shouldOverrideExistingDataModelElement());
		assertEquals("conf/eclipse-formatter.xml",
				configuration.getDataModel().getJavaFormatterConfigurationFile());
		assertEquals("project/src/main/java", configuration.getDataModel().getSrcPojoDirectory());
		assertEquals("project/target/classes", configuration.getDataModel().getBinPojoDirectory());
	}
}
