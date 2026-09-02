package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp._query_result;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class EnrollmentSrcDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field dateEnrolled = Field.fastCreateWithType("date_enrolled", "null");
	private Field startDate = Field.fastCreateWithType("start_date", "null");
	private Field state = Field.fastCreateWithType("state", "null");
	private Field programId = Field.fastCreateWithType("program_id", "null");
	private Field locationId = Field.fastCreateWithType("location_id", "null");

	public EnrollmentSrcDsQueryResultVO() {
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

	public void setDateEnrolled(Field dateEnrolled) {
		this.dateEnrolled = dateEnrolled;
	}

	public void setDateEnrolledValue(String value) {
		this.dateEnrolled.setValue(value);
	}

	public Field getDateEnrolled() {
		return this.dateEnrolled;
	}

	public void setStartDate(Field startDate) {
		this.startDate = startDate;
	}

	public void setStartDateValue(String value) {
		this.startDate.setValue(value);
	}

	public Field getStartDate() {
		return this.startDate;
	}

	public void setState(Field state) {
		this.state = state;
	}

	public void setStateValue(String value) {
		this.state.setValue(value);
	}

	public Field getState() {
		return this.state;
	}

	public void setProgramId(Field programId) {
		this.programId = programId;
	}

	public void setProgramIdValue(String value) {
		this.programId.setValue(value);
	}

	public Field getProgramId() {
		return this.programId;
	}

	public void setLocationId(Field locationId) {
		this.locationId = locationId;
	}

	public void setLocationIdValue(String value) {
		this.locationId.setValue(value);
	}

	public Field getLocationId() {
		return this.locationId;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String dateEnrolledAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_enrolled", "_");

		this.dateEnrolled.setValue(BaseVO.retrieveFieldValue(dateEnrolledAttName, "null", rs));

		String startDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"start_date", "_");

		this.startDate.setValue(BaseVO.retrieveFieldValue(startDateAttName, "null", rs));

		String stateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "state",
				"_");

		this.state.setValue(BaseVO.retrieveFieldValue(stateAttName, "null", rs));

		String programIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"program_id", "_");

		this.programId.setValue(BaseVO.retrieveFieldValue(programIdAttName, "null", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "null", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO enrollment_src_ds(`date_enrolled`, `start_date`, `state`, `program_id`, `location_id`) VALUES( ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO enrollment_src_ds(`date_enrolled`, `start_date`, `state`, `program_id`, `location_id`) VALUES( ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.dateEnrolled.getValue(), this.startDate.getValue(), this.state.getValue(),
				this.programId.getValue(), this.locationId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.dateEnrolled.getValue(), this.startDate.getValue(), this.state.getValue(),
				this.programId.getValue(), this.locationId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?";
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
		return ""
				+ (this.dateEnrolled.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.dateEnrolled.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.startDate.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.startDate.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.state.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.state.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.programId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.programId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.locationId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.locationId.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return ""
				+ (this.dateEnrolled.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.dateEnrolled.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.startDate.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.startDate.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.state.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.state.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.programId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.programId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.locationId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.locationId.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		EnrollmentSrcDsQueryResultVO copy = new EnrollmentSrcDsQueryResultVO();

		copy.dateEnrolled = copyGeneratedField(this.dateEnrolled);
		copy.startDate = copyGeneratedField(this.startDate);
		copy.state = copyGeneratedField(this.state);
		copy.programId = copyGeneratedField(this.programId);

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
		return "enrollment_src_ds";
	}

}