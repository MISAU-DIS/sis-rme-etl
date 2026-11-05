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

public class RemoteAddressVO extends AbstractGeneratedDatabaseObject {
	private Field id = Field.fastCreateWithType("id", "integer");
	private Field personUuid = Field.fastCreateWithType("person_uuid", "string");
	private Field fullAddress = Field.fastCreateWithType("full_address", "string");
	private Field creationDate = Field.fastCreateWithType("creation_date", "date");

	public RemoteAddressVO() {
		this.metadata = false;
		this.fields.add(this.id);
		this.fields.add(this.personUuid);
		this.fields.add(this.fullAddress);
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

		if (utilities.equalsFieldsName(fieldName, "person_uuid")) {
			return this.personUuid.getValue();
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

		if (utilities.equalsFieldsName(fieldName, "person_uuid")) {
			this.personUuid.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.personUuid, srcConn, dstConn);
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

	public void setPersonUuid(Field personUuid) {
		this.personUuid = personUuid;
	}

	public void setPersonUuidValue(String value) {
		this.personUuid.setValue(value);
	}

	public Field getPersonUuid() {
		return this.personUuid;
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

		String personUuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_uuid", "_");

		this.personUuid.setValue(BaseVO.retrieveFieldValue(personUuidAttName, "string", rs));

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
		return "INSERT INTO remote_address(`person_uuid`, `full_address`, `creation_date`, `uuid`) VALUES( ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO remote_address(`id`, `person_uuid`, `full_address`, `creation_date`, `uuid`) VALUES( ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.personUuid.getValue(), this.fullAddress.getValue(), this.creationDate.getValue(),
				this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.id.getValue(), this.personUuid.getValue(), this.fullAddress.getValue(),
				this.creationDate.getValue(), this.uuid };
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
		Object[] params = { this.id.getValue(), this.personUuid.getValue(), this.fullAddress.getValue(),
				this.creationDate.getValue(), this.uuid, this.id.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE remote_address SET `id` = ?, `person_uuid` = ?, `full_address` = ?, `creation_date` = ?, `uuid` = ? WHERE id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.personUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.personUuid.getValue().toString()) + "\""
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
				+ (this.personUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.personUuid.getValue().toString()) + "\""
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
		RemoteAddressVO copy = new RemoteAddressVO();
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
		return "remote_address";
	}

}