package org.openmrs.module.epts.etl.model.pojo.testing.etl_src;

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

public class AddressVO extends AbstractGeneratedDatabaseObject {
	private Field id = Field.fastCreateWithType("id", "integer");
	private Field personId = Field.fastCreateWithType("person_id", "integer");
	private Field fullAddress = Field.fastCreateWithType("full_address", "string");
	private Field creationDate = Field.fastCreateWithType("creation_date", "date");

	public AddressVO() {
		this.metadata = false;
		this.fields.add(this.id);
		this.fields.add(this.personId);
		this.fields.add(this.fullAddress);
		this.fields.add(this.creationDate);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
	}

	@Override
	public Object getFieldValue(String fieldName) {

		if (utilities.equalsFieldsName(fieldName, "id")) {
			return this.id.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "person_id")) {
			return this.personId.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "full_address")) {
			return this.fullAddress.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "creation_date")) {
			return this.creationDate.getValue();
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

		if (utilities.equalsFieldsName(fieldName, "person_id")) {
			this.personId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "full_address")) {
			this.fullAddress.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "creation_date")) {
			this.creationDate.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.personId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.fullAddress, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creationDate, srcConn, dstConn);
	}

	public void setId(Field id) {
		this.id = id;
	}

	public void setIdValue(String value) {
		this.id.setValue(value);
	}

	public Field getId() {
		return this.id;
	}

	public void setPersonId(Field personId) {
		this.personId = personId;
	}

	public void setPersonIdValue(String value) {
		this.personId.setValue(value);
	}

	public Field getPersonId() {
		return this.personId;
	}

	public void setFullAddress(Field fullAddress) {
		this.fullAddress = fullAddress;
	}

	public void setFullAddressValue(String value) {
		this.fullAddress.setValue(value);
	}

	public Field getFullAddress() {
		return this.fullAddress;
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

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String idAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "id", "_");

		this.id.setValue(BaseVO.retrieveFieldValue(idAttName, "integer", rs));

		String personIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_id", "_");

		this.personId.setValue(BaseVO.retrieveFieldValue(personIdAttName, "integer", rs));

		String fullAddressAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"full_address", "_");

		this.fullAddress.setValue(BaseVO.retrieveFieldValue(fullAddressAttName, "string", rs));

		String creationDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creation_date", "_");

		this.creationDate.setValue(BaseVO.retrieveFieldValue(creationDateAttName, "date", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "string", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO address(`id`, `person_id`, `full_address`, `creation_date`, `uuid`) VALUES( ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO address(`id`, `person_id`, `full_address`, `creation_date`, `uuid`) VALUES( ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.id.getValue(), this.personId.getValue(), this.fullAddress.getValue(),
				this.creationDate.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.id.getValue(), this.personId.getValue(), this.fullAddress.getValue(),
				this.creationDate.getValue(), this.uuid };
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
				+ (this.id.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.id.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.personId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.personId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.fullAddress.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fullAddress.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.creationDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.creationDate.getValue())
						+ "\"" : null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return ""
				+ (this.id.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.id.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.personId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.personId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.fullAddress.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fullAddress.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.creationDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.creationDate.getValue())
						+ "\"" : null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		AddressVO copy = new AddressVO();
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
		return "address";
	}

}