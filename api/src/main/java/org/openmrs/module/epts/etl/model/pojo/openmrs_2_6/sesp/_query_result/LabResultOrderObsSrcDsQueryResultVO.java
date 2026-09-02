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

public class LabResultOrderObsSrcDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field conceptId = Field.fastCreateWithType("concept_id", "VARCHAR");
	private Field valueCoded = Field.fastCreateWithType("value_coded", "VARCHAR");
	private Field obsDatetime = Field.fastCreateWithType("obs_datetime", "DATETIME");

	public LabResultOrderObsSrcDsQueryResultVO() {
		this.metadata = false;

		this.fields.add(this.conceptId);
		this.fields.add(this.valueCoded);
		this.fields.add(this.obsDatetime);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "concept_id")) {
			return this.conceptId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "value_coded")) {
			return this.valueCoded.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "obs_datetime")) {
			return this.obsDatetime.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_created")) return this.dateCreated;
		if (utilities.equalsFieldsName(fieldName, "date_changed")) return this.dateChanged;
		if (utilities.equalsFieldsName(fieldName, "date_voided")) return this.dateVoided;
		if (utilities.equalsFieldsName(fieldName, "uuid")) return this.uuid;
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

	public void setConceptId(Field conceptId) {
		this.conceptId = conceptId;
	}

	public void setConceptIdValue(String value) {
		this.conceptId.setValue(value);
	}

	public Field getConceptId() {
		return this.conceptId;
	}

	public void setValueCoded(Field valueCoded) {
		this.valueCoded = valueCoded;
	}

	public void setValueCodedValue(String value) {
		this.valueCoded.setValue(value);
	}

	public Field getValueCoded() {
		return this.valueCoded;
	}

	public void setObsDatetime(Field obsDatetime) {
		this.obsDatetime = obsDatetime;
	}

	public void setObsDatetimeValue(java.util.Date value) {
		this.obsDatetime.setValue(value);
	}

	public Field getObsDatetime() {
		return this.obsDatetime;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String conceptIdAttName = "concept_id";

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "VARCHAR", rs));

		String valueCodedAttName = "value_coded";

		this.valueCoded.setValue(BaseVO.retrieveFieldValue(valueCodedAttName, "VARCHAR", rs));

		String obsDatetimeAttName = "obs_datetime";

		this.obsDatetime.setValue(BaseVO.retrieveFieldValue(obsDatetimeAttName, "DATETIME", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO lab_result_order_obs_src_ds(`concept_id`, `value_coded`, `obs_datetime`) VALUES( ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO lab_result_order_obs_src_ds(`concept_id`, `value_coded`, `obs_datetime`) VALUES( ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.conceptId.getValue(), this.valueCoded.getValue(), this.obsDatetime.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.conceptId.getValue(), this.valueCoded.getValue(), this.obsDatetime.getValue() };
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
		return ""
				+ (this.conceptId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.conceptId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.valueCoded.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueCoded.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.obsDatetime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.obsDatetime.getValue())
						+ "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return ""
				+ (this.conceptId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.conceptId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.valueCoded.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueCoded.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.obsDatetime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.obsDatetime.getValue())
						+ "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		LabResultOrderObsSrcDsQueryResultVO copy = new LabResultOrderObsSrcDsQueryResultVO();
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
		return "lab_result_order_obs_src_ds";
	}

}