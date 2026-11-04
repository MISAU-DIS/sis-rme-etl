package org.openmrs.module.epts.etl.model.pojo.test_file;

import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;

public class PatientVO extends GenericDatabaseObject {

	private static final long serialVersionUID = 1L;

	private final Field patientId = Field.fastCreateWithType("patient_id", "java.lang.Integer");

	private final Field name = Field.fastCreateWithType("name", "java.lang.String");

	@Override
	public Object getFieldValue(String fieldName) {
		if ("patient_id".equals(fieldName)) {
			return patientId.getValue();
		}
		if ("name".equals(fieldName)) {
			return name.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if ("patient_id".equals(fieldName)) {
			patientId.setValue(value);
			return;
		}
		if ("name".equals(fieldName)) {
			name.setValue(value);
			return;
		}
		super.setFieldValue(fieldName, value);
	}
}
