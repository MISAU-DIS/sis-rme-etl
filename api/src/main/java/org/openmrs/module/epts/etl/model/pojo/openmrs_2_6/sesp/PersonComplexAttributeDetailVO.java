package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import org.openmrs.module.epts.etl.utilities.AttDefinedElements;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class PersonComplexAttributeDetailVO extends AbstractGeneratedDatabaseObject {
	private Field personComplexAttributeDetailId = Field.fastCreateWithType("person_complex_attribute_detail_id",
			"BIGINT UNSIGNED");
	private Field personComplexAttributeId = Field.fastCreateWithType("person_complex_attribute_id", "BIGINT UNSIGNED");
	private Field attributeKey = Field.fastCreateWithType("attribute_key", "VARCHAR");
	private Field attributeValue = Field.fastCreateWithType("attribute_value", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");

	public PersonComplexAttributeDetailVO() {
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

	public void setPersonComplexAttributeDetailId(Field personComplexAttributeDetailId) {
		this.personComplexAttributeDetailId = personComplexAttributeDetailId;
	}

	public void setPersonComplexAttributeDetailIdValue(Long value) {
		this.personComplexAttributeDetailId.setValue(value);
	}

	public Field getPersonComplexAttributeDetailId() {
		return this.personComplexAttributeDetailId;
	}

	public void setPersonComplexAttributeId(Field personComplexAttributeId) {
		this.personComplexAttributeId = personComplexAttributeId;
	}

	public void setPersonComplexAttributeIdValue(Long value) {
		this.personComplexAttributeId.setValue(value);
	}

	public Field getPersonComplexAttributeId() {
		return this.personComplexAttributeId;
	}

	public void setAttributeKey(Field attributeKey) {
		this.attributeKey = attributeKey;
	}

	public void setAttributeKeyValue(String value) {
		this.attributeKey.setValue(value);
	}

	public Field getAttributeKey() {
		return this.attributeKey;
	}

	public void setAttributeValue(Field attributeValue) {
		this.attributeValue = attributeValue;
	}

	public void setAttributeValueValue(String value) {
		this.attributeValue.setValue(value);
	}

	public Field getAttributeValue() {
		return this.attributeValue;
	}

	public void setCreator(Field creator) {
		this.creator = creator;
	}

	public void setCreatorValue(Integer value) {
		this.creator.setValue(value);
	}

	public Field getCreator() {
		return this.creator;
	}

	public void setVoided(Field voided) {
		this.voided = voided;
	}

	public void setVoidedValue(Boolean value) {
		this.voided.setValue(value);
	}

	public Field getVoided() {
		return this.voided;
	}

	public void setVoidedBy(Field voidedBy) {
		this.voidedBy = voidedBy;
	}

	public void setVoidedByValue(Integer value) {
		this.voidedBy.setValue(value);
	}

	public Field getVoidedBy() {
		return this.voidedBy;
	}

	public void setVoidReason(Field voidReason) {
		this.voidReason = voidReason;
	}

	public void setVoidReasonValue(String value) {
		this.voidReason.setValue(value);
	}

	public Field getVoidReason() {
		return this.voidReason;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String personComplexAttributeDetailIdAttName = utilities.concatStringsWithSeparator(
				this.getRelatedConfiguration().getAlias(), "person_complex_attribute_detail_id", "_");

		this.personComplexAttributeDetailId
				.setValue(BaseVO.retrieveFieldValue(personComplexAttributeDetailIdAttName, "BIGINT UNSIGNED", rs));

		String personComplexAttributeIdAttName = utilities.concatStringsWithSeparator(
				this.getRelatedConfiguration().getAlias(), "person_complex_attribute_id", "_");

		this.personComplexAttributeId
				.setValue(BaseVO.retrieveFieldValue(personComplexAttributeIdAttName, "BIGINT UNSIGNED", rs));

		String attributeKeyAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"attribute_key", "_");

		this.attributeKey.setValue(BaseVO.retrieveFieldValue(attributeKeyAttName, "VARCHAR", rs));

		String attributeValueAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"attribute_value", "_");

		this.attributeValue.setValue(BaseVO.retrieveFieldValue(attributeValueAttName, "VARCHAR", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided",
				"_");

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "BIT", rs));

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

		String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_voided", "_");

		this.dateVoided = (java.util.Date) BaseVO.retrieveFieldValue(dateVoidedAttName, "DATETIME", rs);

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO person_complex_attribute_detail(`person_complex_attribute_id`, `attribute_key`, `attribute_value`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO person_complex_attribute_detail(`person_complex_attribute_detail_id`, `person_complex_attribute_id`, `attribute_key`, `attribute_value`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.personComplexAttributeId.getValue(), this.attributeKey.getValue(),
				this.attributeValue.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.personComplexAttributeDetailId.getValue(), this.personComplexAttributeId.getValue(),
				this.attributeKey.getValue(), this.attributeValue.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.personComplexAttributeDetailId.getValue(), this.personComplexAttributeId.getValue(),
				this.attributeKey.getValue(), this.attributeValue.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.uuid, this.personComplexAttributeDetailId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE person_complex_attribute_detail SET `person_complex_attribute_detail_id` = ?, `person_complex_attribute_id` = ?, `attribute_key` = ?, `attribute_value` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ? WHERE person_complex_attribute_detail_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.personComplexAttributeId.getValue()) + ","
				+ (this.attributeKey.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.attributeKey.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.attributeValue.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.attributeValue.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.personComplexAttributeDetailId.getValue()) + "," + (this.personComplexAttributeId.getValue())
				+ ","
				+ (this.attributeKey.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.attributeKey.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.attributeValue.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.attributeValue.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PersonComplexAttributeDetailVO copy = new PersonComplexAttributeDetailVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.personComplexAttributeId.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("personComplexAttributeId"))
			return this.personComplexAttributeId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "person_complex_attribute_detail";
	}

}