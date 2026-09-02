package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp._query_result;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;


import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrescriptionHeaderDataSrcDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field nextPickupDate = Field.fastCreateWithType("next_pickup_date", "DATETIME");
	private Field dispenseModeId = Field.fastCreateWithType("dispense_mode_id", "NULL");

	public PrescriptionHeaderDataSrcDsQueryResultVO() {
		this.metadata = false;

		this.fields.add(this.nextPickupDate);
		this.fields.add(this.dispenseModeId);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "next_pickup_date")) {
			return this.nextPickupDate.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dispense_mode_id")) {
			return this.dispenseModeId.getValue();
		}
		return super.getFieldValue(fieldName);
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

	public void setNextPickupDate(Field nextPickupDate) {
		this.nextPickupDate = nextPickupDate;
	}

	public void setNextPickupDateValue(java.util.Date value) {
		this.nextPickupDate.setValue(value);
	}

	public Field getNextPickupDate() {
		return this.nextPickupDate;
	}

	public void setDispenseModeId(Field dispenseModeId) {
		this.dispenseModeId = dispenseModeId;
	}

	public void setDispenseModeIdValue(String value) {
		this.dispenseModeId.setValue(value);
	}

	public Field getDispenseModeId() {
		return this.dispenseModeId;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String nextPickupDateAttName = "next_pickup_date";

		this.nextPickupDate.setValue(BaseVO.retrieveFieldValue(nextPickupDateAttName, "DATETIME", rs));

		String dispenseModeIdAttName = "dispense_mode_id";

		this.dispenseModeId.setValue(BaseVO.retrieveFieldValue(dispenseModeIdAttName, "NULL", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO prescription_header_data_src_ds(`next_pickup_date`, `dispense_mode_id`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO prescription_header_data_src_ds(`next_pickup_date`, `dispense_mode_id`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.nextPickupDate.getValue(), this.dispenseModeId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.nextPickupDate.getValue(), this.dispenseModeId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?";
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
				+ (this.nextPickupDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.nextPickupDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dispenseModeId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.dispenseModeId.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return ""
				+ (this.nextPickupDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.nextPickupDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dispenseModeId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.dispenseModeId.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PrescriptionHeaderDataSrcDsQueryResultVO copy = new PrescriptionHeaderDataSrcDsQueryResultVO();
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
		return "prescription_header_data_src_ds";
	}

}