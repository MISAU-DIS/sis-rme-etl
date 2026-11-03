package org.openmrs.module.epts.etl.model.pojo.test_file;

import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;

public class PatientVO extends GenericDatabaseObject {

	private static final long serialVersionUID = 1L;

	private Integer patientId;

	private String name;

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
