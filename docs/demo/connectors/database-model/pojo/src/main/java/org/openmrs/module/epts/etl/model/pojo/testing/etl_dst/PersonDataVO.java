package org.openmrs.module.epts.etl.model.pojo.testing.etl_dst;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.conf.Key;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PersonDataVO extends AbstractGeneratedDatabaseObject {
	private Field creationDate = Field.fastCreateWithType("creation_date", "DATETIME");
	private Field id = Field.fastCreateWithType("id", "INT");
	private Field fullName = Field.fastCreateWithType("full_name", "VARCHAR");
	private Field fullAddress = Field.fastCreateWithType("full_address", "VARCHAR");
	private Field personCreationDate = Field.fastCreateWithType("person_creation_date", "DATETIME");
	private Field addressCreationDate = Field.fastCreateWithType("address_creation_date", "DATETIME");
	private Field personUuid = Field.fastCreateWithType("person_uuid", "CHAR");
	private Field addressUuid = Field.fastCreateWithType("address_uuid", "CHAR");

	public PersonDataVO() {
		this.metadata = false;
		this.fields.add(this.id);
		this.fields.add(this.fullName);
		this.fields.add(this.fullAddress);
		this.fields.add(this.personCreationDate);
		this.fields.add(this.addressCreationDate);
		this.fields.add(this.personUuid);
		this.fields.add(this.addressUuid);
		this.fields.add(this.creationDate);
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

		if (utilities.equalsFieldsName(fieldName, "full_address")) {
			return this.fullAddress.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "person_creation_date")) {
			return this.personCreationDate.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "address_creation_date")) {
			return this.addressCreationDate.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "person_uuid")) {
			return this.personUuid.getValue();
		}

		if (utilities.equalsFieldsName(fieldName, "address_uuid")) {
			return this.addressUuid.getValue();
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

		if (utilities.equalsFieldsName(fieldName, "full_name")) {
			this.fullName.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "full_address")) {
			this.fullAddress.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "person_creation_date")) {
			this.personCreationDate.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "address_creation_date")) {
			this.addressCreationDate.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "person_uuid")) {
			this.personUuid.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		if (utilities.equalsFieldsName(fieldName, "address_uuid")) {
			this.addressUuid.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.fullName, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.fullAddress, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.personCreationDate, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.addressCreationDate, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.personUuid, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.addressUuid, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creationDate, srcConn, dstConn);
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

	public void setFullAddress(Field fullAddress) {
		this.fullAddress = fullAddress;
	}

	public void setFullAddressValue(String value) {
		this.fullAddress.setValue(value);
	}

	public Field getFullAddress() {
		return this.fullAddress;
	}

	public void setPersonCreationDate(Field personCreationDate) {
		this.personCreationDate = personCreationDate;
	}

	public void setPersonCreationDateValue(java.util.Date value) {
		this.personCreationDate.setValue(value);
	}

	public Field getPersonCreationDate() {
		return this.personCreationDate;
	}

	public void setAddressCreationDate(Field addressCreationDate) {
		this.addressCreationDate = addressCreationDate;
	}

	public void setAddressCreationDateValue(java.util.Date value) {
		this.addressCreationDate.setValue(value);
	}

	public Field getAddressCreationDate() {
		return this.addressCreationDate;
	}

	public void setPersonUuid(Field personUuid) {
		this.personUuid = personUuid;
	}

	public void setPersonUuidValue(String value) {
		this.personUuid.setValue(value);
	}

	public Field getPersonUuid() {
		return this.personUuid;
	}

	public void setAddressUuid(Field addressUuid) {
		this.addressUuid = addressUuid;
	}

	public void setAddressUuidValue(String value) {
		this.addressUuid.setValue(value);
	}

	public Field getAddressUuid() {
		return this.addressUuid;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		if (getRelatedConfiguration().containsField("creation_date")) {
			String creationDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"creation_date", "_");

			this.creationDate.setValue(BaseVO.retrieveFieldValue(creationDateAttName, "DATETIME", rs));
		}

		String idAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "id", "_");

		this.id.setValue(BaseVO.retrieveFieldValue(idAttName, "INT", rs));

		String fullNameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"full_name", "_");

		this.fullName.setValue(BaseVO.retrieveFieldValue(fullNameAttName, "VARCHAR", rs));

		String fullAddressAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"full_address", "_");

		this.fullAddress.setValue(BaseVO.retrieveFieldValue(fullAddressAttName, "VARCHAR", rs));

		String personCreationDateAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "person_creation_date", "_");

		this.personCreationDate.setValue(BaseVO.retrieveFieldValue(personCreationDateAttName, "DATETIME", rs));

		String addressCreationDateAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "address_creation_date", "_");

		this.addressCreationDate.setValue(BaseVO.retrieveFieldValue(addressCreationDateAttName, "DATETIME", rs));

		String personUuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_uuid", "_");

		this.personUuid.setValue(BaseVO.retrieveFieldValue(personUuidAttName, "CHAR", rs));

		String addressUuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address_uuid", "_");

		this.addressUuid.setValue(BaseVO.retrieveFieldValue(addressUuidAttName, "CHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO person_data(`full_name`, `full_address`, `person_creation_date`, `address_creation_date`, `person_uuid`, `address_uuid`) VALUES( ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO person_data(`id`, `full_name`, `full_address`, `person_creation_date`, `address_creation_date`, `person_uuid`, `address_uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.fullName.getValue(), this.fullAddress.getValue(), this.personCreationDate.getValue(),
				this.addressCreationDate.getValue(), this.personUuid.getValue(), this.addressUuid.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.id.getValue(), this.fullName.getValue(), this.fullAddress.getValue(),
				this.personCreationDate.getValue(), this.addressCreationDate.getValue(), this.personUuid.getValue(),
				this.addressUuid.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.id.getValue(), this.fullName.getValue(), this.fullAddress.getValue(),
				this.personCreationDate.getValue(), this.addressCreationDate.getValue(), this.personUuid.getValue(),
				this.addressUuid.getValue(), this.id.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE person_data SET `id` = ?, `full_name` = ?, `full_address` = ?, `person_creation_date` = ?, `address_creation_date` = ?, `person_uuid` = ?, `address_uuid` = ? WHERE id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.fullName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fullName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.fullAddress.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fullAddress.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.personCreationDate.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.personCreationDate.getValue()) + "\""
						: null)
				+ ","
				+ (this.addressCreationDate.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.addressCreationDate.getValue()) + "\""
						: null)
				+ ","
				+ (this.personUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.personUuid.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.addressUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.addressUuid.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.id.getValue()) + ","
				+ (this.fullName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fullName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.fullAddress.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fullAddress.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.personCreationDate.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.personCreationDate.getValue()) + "\""
						: null)
				+ ","
				+ (this.addressCreationDate.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.addressCreationDate.getValue()) + "\""
						: null)
				+ ","
				+ (this.personUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.personUuid.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.addressUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.addressUuid.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PersonDataVO copy = new PersonDataVO();
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
		return "person_data";
	}

}