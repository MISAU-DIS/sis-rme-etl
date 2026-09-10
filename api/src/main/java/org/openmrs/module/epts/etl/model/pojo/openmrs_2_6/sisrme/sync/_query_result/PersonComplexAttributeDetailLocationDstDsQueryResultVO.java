package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.sync._query_result;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.conf.Key;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PersonComplexAttributeDetailLocationDstDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field locationId = Field.fastCreateWithType("location_id", "null");

	public PersonComplexAttributeDetailLocationDstDsQueryResultVO() {
		this.metadata = false;
		this.fields.add(this.locationId);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			return this.locationId.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			this.locationId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		super.setFieldValue(fieldName, value);
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

	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) throws DBException {
		super.loadWithDefaultValues(srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.locationId, srcConn, dstConn);
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

		String locationIdAttName = "location_id";

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "null", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO person_complex_attribute_detail_location_dst_ds(`location_id`) VALUES( ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO person_complex_attribute_detail_location_dst_ds(`location_id`) VALUES( ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.locationId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.locationId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?";
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
		return "" + (this.locationId.getValue() != null
				? "\"" + utilities.scapeQuotationMarks(this.locationId.getValue().toString()) + "\""
				: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.locationId.getValue() != null
				? "\"" + utilities.scapeQuotationMarks(this.locationId.getValue().toString()) + "\""
				: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PersonComplexAttributeDetailLocationDstDsQueryResultVO copy = new PersonComplexAttributeDetailLocationDstDsQueryResultVO();
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
		return "person_complex_attribute_detail_location_dst_ds";
	}

}