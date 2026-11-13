package org.openmrs.module.epts.etl.model.pojo.testing.etl_dst;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.conf.Key;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import org.openmrs.module.epts.etl.utilities.AttDefinedElements;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PersonVO extends AbstractGeneratedDatabaseObject {
	private Field id = Field.fastCreateWithType("id", "INT");
	private Field fullName = Field.fastCreateWithType("full_name", "VARCHAR");
	private Field creationDate = Field.fastCreateWithType("creation_date", "DATETIME");
	private Field officeId = Field.fastCreateWithType("office_id", "INT");

	public PersonVO() {
		this.metadata = false;
		this.fields.add(this.id);
		this.fields.add(this.fullName);
		this.fields.add(this.creationDate);
		this.fields.add(this.officeId);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {

		if (utilities.equalsFieldsName(k.getName(), "id")) {
			this.id.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {

		if (utilities.equalsFieldsName(fieldName, "id")) {
			return this.id.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "full_name")) {
			return this.fullName.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "creation_date")) {
			return this.creationDate.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "office_id")) {
			return this.officeId.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {

		if (utilities.equalsFieldsName(fieldName, "id")) {
			this.id.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "full_name")) {
			this.fullName.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "creation_date")) {
			this.creationDate.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "office_id")) {
			this.officeId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.id, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.fullName, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creationDate, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.officeId, srcConn, dstConn);
	}

	public void setId(Field id) {
		this.id = id;
	}

	public void setIdValue(Integer value) {
		this.id.setValue(value);
	}

	public Field getId() {
		return this.id;
	}

	public void setFullName(Field fullName) {
		this.fullName = fullName;
	}

	public void setFullNameValue(String value) {
		this.fullName.setValue(value);
	}

	public Field getFullName() {
		return this.fullName;
	}

	public void setCreationDate(Field creationDate) {
		this.creationDate = creationDate;
	}

	public void setCreationDateValue(java.util.Date value) {
		this.creationDate.setValue(value);
	}

	public Field getCreationDate() {
		return this.creationDate;
	}

	public void setOfficeId(Field officeId) {
		this.officeId = officeId;
	}

	public void setOfficeIdValue(Integer value) {
		this.officeId.setValue(value);
	}

	public Field getOfficeId() {
		return this.officeId;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String idAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "id", "_");

		this.id.setValue(BaseVO.retrieveFieldValue(idAttName, "INT", rs));

		String fullNameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"full_name", "_");

		this.fullName.setValue(BaseVO.retrieveFieldValue(fullNameAttName, "VARCHAR", rs));

		String creationDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creation_date", "_");

		this.creationDate.setValue(BaseVO.retrieveFieldValue(creationDateAttName, "DATETIME", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "CHAR", rs));

		String officeIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"office_id", "_");

		this.officeId.setValue(BaseVO.retrieveFieldValue(officeIdAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO person(`full_name`, `creation_date`, `uuid`, `office_id`) VALUES( ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO person(`id`, `full_name`, `creation_date`, `uuid`, `office_id`) VALUES( ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.fullName.getValue(), this.creationDate.getValue(), this.uuid,
				this.officeId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.id.getValue(), this.fullName.getValue(), this.creationDate.getValue(), this.uuid,
				this.officeId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.id.getValue(), this.fullName.getValue(), this.creationDate.getValue(), this.uuid,
				this.officeId.getValue(), this.id.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE person SET `id` = ?, `full_name` = ?, `creation_date` = ?, `uuid` = ?, `office_id` = ? WHERE id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.fullName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fullName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.creationDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.creationDate.getValue())
						+ "\"" : null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.officeId.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.id.getValue()) + ","
				+ (this.fullName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fullName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.creationDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.creationDate.getValue())
						+ "\"" : null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.officeId.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PersonVO copy = new PersonVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());

		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.officeId.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("officeId"))
			return this.officeId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "person";
	}

}