package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp._query_result;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class VitalSignsSrcDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field temperature = Field.fastCreateWithType("temperature", "DOUBLE");
	private Field bloodPressureSystolic = Field.fastCreateWithType("blood_pressure_systolic", "DOUBLE");
	private Field bloodPressureDiastolic = Field.fastCreateWithType("blood_pressure_diastolic", "DOUBLE");

	public VitalSignsSrcDsQueryResultVO() {
		this.metadata = false;
	}

	@JsonIgnore
	@Override
	public String generateFullFilledUpdateSql() {
		return null;
	}

	@JsonIgnore
	@Override
	public void setInsertSQLQuestionMarksWithObjectId(String insertQuestionMarks) {

	}

	@JsonIgnore
	@Override
	public void setInsertSQLQuestionMarksWithoutObjectId(String insertQuestionMarks) {

	}

	@JsonIgnore
	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) {
		utilities.throwForbiddenMethodException();
	}

	public void setTemperature(Field temperature) {
		this.temperature = temperature;
	}

	public void setTemperatureValue(Double value) {
		this.temperature.setValue(value);
	}

	public Field getTemperature() {
		return this.temperature;
	}

	public void setBloodPressureSystolic(Field bloodPressureSystolic) {
		this.bloodPressureSystolic = bloodPressureSystolic;
	}

	public void setBloodPressureSystolicValue(Double value) {
		this.bloodPressureSystolic.setValue(value);
	}

	public Field getBloodPressureSystolic() {
		return this.bloodPressureSystolic;
	}

	public void setBloodPressureDiastolic(Field bloodPressureDiastolic) {
		this.bloodPressureDiastolic = bloodPressureDiastolic;
	}

	public void setBloodPressureDiastolicValue(Double value) {
		this.bloodPressureDiastolic.setValue(value);
	}

	public Field getBloodPressureDiastolic() {
		return this.bloodPressureDiastolic;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String temperatureAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"temperature", "_");

		this.temperature.setValue(BaseVO.retrieveFieldValue(temperatureAttName, "DOUBLE", rs));

		String bloodPressureSystolicAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "blood_pressure_systolic", "_");

		this.bloodPressureSystolic.setValue(BaseVO.retrieveFieldValue(bloodPressureSystolicAttName, "DOUBLE", rs));

		String bloodPressureDiastolicAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "blood_pressure_diastolic", "_");

		this.bloodPressureDiastolic.setValue(BaseVO.retrieveFieldValue(bloodPressureDiastolicAttName, "DOUBLE", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO vital_signs_src_ds(`temperature`, `blood_pressure_systolic`, `blood_pressure_diastolic`) VALUES( ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO vital_signs_src_ds(`temperature`, `blood_pressure_systolic`, `blood_pressure_diastolic`) VALUES( ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.temperature.getValue(), this.bloodPressureSystolic.getValue(),
				this.bloodPressureDiastolic.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.temperature.getValue(), this.bloodPressureSystolic.getValue(),
				this.bloodPressureDiastolic.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		throw new RuntimeException("Impossible auto update command! No primary key is defined for table object!");
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		throw new RuntimeException("Impossible auto update command! No primary key is defined for table object!");
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.temperature.getValue()) + "," + (this.bloodPressureSystolic.getValue()) + ","
				+ (this.bloodPressureDiastolic.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.temperature.getValue()) + "," + (this.bloodPressureSystolic.getValue()) + ","
				+ (this.bloodPressureDiastolic.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		VitalSignsSrcDsQueryResultVO copy = new VitalSignsSrcDsQueryResultVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "vital_signs_src_ds";
	}

}