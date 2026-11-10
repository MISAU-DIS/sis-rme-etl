package org.openmrs.module.epts.etl.conf.interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;

class ConditionalEtlElementContractTest {

	@Test
	void shouldExposeOnlyReaderAndWriterBasedProcessingContract() {
		Method[] methods = Arrays.stream(ConditionalEtlElement.class.getMethods())
				.filter(method -> method.getName().equals("shouldBeProcessed"))
				.toArray(Method[]::new);

		assertEquals(1, methods.length);
		Class<?>[] parameterTypes = methods[0].getParameterTypes();
		assertEquals(ConnectorRecordReader.class, parameterTypes[2]);
		assertEquals(ConnectorRecordWriter.class, parameterTypes[3]);
		assertFalse(Arrays.asList(parameterTypes).contains(Connection.class));
	}
}
