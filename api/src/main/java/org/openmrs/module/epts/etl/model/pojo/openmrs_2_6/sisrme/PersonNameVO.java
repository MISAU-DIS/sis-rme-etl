package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

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

public class PersonNameVO extends AbstractGeneratedDatabaseObject {
	private Field personNameId = Field.fastCreateWithType("person_name_id", "INT");
	private Field preferred = Field.fastCreateWithType("preferred", "BIT");
	private Field personId = Field.fastCreateWithType("person_id", "INT");
	private Field prefix = Field.fastCreateWithType("prefix", "VARCHAR");
	private Field givenName = Field.fastCreateWithType("given_name", "VARCHAR");
	private Field middleName = Field.fastCreateWithType("middle_name", "VARCHAR");
	private Field familyNamePrefix = Field.fastCreateWithType("family_name_prefix", "VARCHAR");
	private Field familyName = Field.fastCreateWithType("family_name", "VARCHAR");
	private Field familyName2 = Field.fastCreateWithType("family_name2", "VARCHAR");
	private Field familyNameSuffix = Field.fastCreateWithType("family_name_suffix", "VARCHAR");
	private Field degree = Field.fastCreateWithType("degree", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");

	public PersonNameVO() {
		this.metadata = false;
		this.fields.add(this.personNameId);
		this.fields.add(this.preferred);
		this.fields.add(this.personId);
		this.fields.add(this.prefix);
		this.fields.add(this.givenName);
		this.fields.add(this.middleName);
		this.fields.add(this.familyNamePrefix);
		this.fields.add(this.familyName);
		this.fields.add(this.familyName2);
		this.fields.add(this.familyNameSuffix);
		this.fields.add(this.degree);
		this.fields.add(this.creator);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
		this.fields.add(this.changedBy);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "person_name_id")) {
			this.personNameId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "person_name_id")) {
			return this.personNameId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "preferred")) {
			return this.preferred.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "person_id")) {
			return this.personId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "prefix")) {
			return this.prefix.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "given_name")) {
			return this.givenName.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "middle_name")) {
			return this.middleName.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "family_name_prefix")) {
			return this.familyNamePrefix.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "family_name")) {
			return this.familyName.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "family_name2")) {
			return this.familyName2.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "family_name_suffix")) {
			return this.familyNameSuffix.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "degree")) {
			return this.degree.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "voided")) {
			return this.voided.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "voided_by")) {
			return this.voidedBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "void_reason")) {
			return this.voidReason.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "person_name_id")) {
			this.personNameId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "preferred")) {
			this.preferred.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "person_id")) {
			this.personId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "prefix")) {
			this.prefix.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "given_name")) {
			this.givenName.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "middle_name")) {
			this.middleName.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "family_name_prefix")) {
			this.familyNamePrefix.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "family_name")) {
			this.familyName.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "family_name2")) {
			this.familyName2.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "family_name_suffix")) {
			this.familyNameSuffix.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "degree")) {
			this.degree.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			this.creator.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "voided")) {
			this.voided.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "voided_by")) {
			this.voidedBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "void_reason")) {
			this.voidReason.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			this.changedBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.personNameId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.preferred, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.personId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.prefix, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.givenName, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.middleName, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.familyNamePrefix, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.familyName, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.familyName2, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.familyNameSuffix, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.degree, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voided, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidReason, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.changedBy, srcConn, dstConn);
	}

	public void setPersonNameId(Field personNameId) {
		this.personNameId = personNameId;
	}

	public void setPersonNameIdValue(Integer value) {
		this.personNameId.setValue(value);
	}

	public Field getPersonNameId() {
		return this.personNameId;
	}

	public void setPreferred(Field preferred) {
		this.preferred = preferred;
	}

	public void setPreferredValue(Boolean value) {
		this.preferred.setValue(value);
	}

	public Field getPreferred() {
		return this.preferred;
	}

	public void setPersonId(Field personId) {
		this.personId = personId;
	}

	public void setPersonIdValue(Integer value) {
		this.personId.setValue(value);
	}

	public Field getPersonId() {
		return this.personId;
	}

	public void setPrefix(Field prefix) {
		this.prefix = prefix;
	}

	public void setPrefixValue(String value) {
		this.prefix.setValue(value);
	}

	public Field getPrefix() {
		return this.prefix;
	}

	public void setGivenName(Field givenName) {
		this.givenName = givenName;
	}

	public void setGivenNameValue(String value) {
		this.givenName.setValue(value);
	}

	public Field getGivenName() {
		return this.givenName;
	}

	public void setMiddleName(Field middleName) {
		this.middleName = middleName;
	}

	public void setMiddleNameValue(String value) {
		this.middleName.setValue(value);
	}

	public Field getMiddleName() {
		return this.middleName;
	}

	public void setFamilyNamePrefix(Field familyNamePrefix) {
		this.familyNamePrefix = familyNamePrefix;
	}

	public void setFamilyNamePrefixValue(String value) {
		this.familyNamePrefix.setValue(value);
	}

	public Field getFamilyNamePrefix() {
		return this.familyNamePrefix;
	}

	public void setFamilyName(Field familyName) {
		this.familyName = familyName;
	}

	public void setFamilyNameValue(String value) {
		this.familyName.setValue(value);
	}

	public Field getFamilyName() {
		return this.familyName;
	}

	public void setFamilyName2(Field familyName2) {
		this.familyName2 = familyName2;
	}

	public void setFamilyName2Value(String value) {
		this.familyName2.setValue(value);
	}

	public Field getFamilyName2() {
		return this.familyName2;
	}

	public void setFamilyNameSuffix(Field familyNameSuffix) {
		this.familyNameSuffix = familyNameSuffix;
	}

	public void setFamilyNameSuffixValue(String value) {
		this.familyNameSuffix.setValue(value);
	}

	public Field getFamilyNameSuffix() {
		return this.familyNameSuffix;
	}

	public void setDegree(Field degree) {
		this.degree = degree;
	}

	public void setDegreeValue(String value) {
		this.degree.setValue(value);
	}

	public Field getDegree() {
		return this.degree;
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

	public void setChangedBy(Field changedBy) {
		this.changedBy = changedBy;
	}

	public void setChangedByValue(Integer value) {
		this.changedBy.setValue(value);
	}

	public Field getChangedBy() {
		return this.changedBy;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String personNameIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_name_id", "_");

		this.personNameId.setValue(BaseVO.retrieveFieldValue(personNameIdAttName, "INT", rs));

		String preferredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"preferred", "_");

		this.preferred.setValue(BaseVO.retrieveFieldValue(preferredAttName, "BIT", rs));

		String personIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_id", "_");

		this.personId.setValue(BaseVO.retrieveFieldValue(personIdAttName, "INT", rs));

		String prefixAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "prefix",
				"_");

		this.prefix.setValue(BaseVO.retrieveFieldValue(prefixAttName, "VARCHAR", rs));

		String givenNameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"given_name", "_");

		this.givenName.setValue(BaseVO.retrieveFieldValue(givenNameAttName, "VARCHAR", rs));

		String middleNameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"middle_name", "_");

		this.middleName.setValue(BaseVO.retrieveFieldValue(middleNameAttName, "VARCHAR", rs));

		String familyNamePrefixAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"family_name_prefix", "_");

		this.familyNamePrefix.setValue(BaseVO.retrieveFieldValue(familyNamePrefixAttName, "VARCHAR", rs));

		String familyNameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"family_name", "_");

		this.familyName.setValue(BaseVO.retrieveFieldValue(familyNameAttName, "VARCHAR", rs));

		String familyName2AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"family_name2", "_");

		this.familyName2.setValue(BaseVO.retrieveFieldValue(familyName2AttName, "VARCHAR", rs));

		String familyNameSuffixAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"family_name_suffix", "_");

		this.familyNameSuffix.setValue(BaseVO.retrieveFieldValue(familyNameSuffixAttName, "VARCHAR", rs));

		String degreeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "degree",
				"_");

		this.degree.setValue(BaseVO.retrieveFieldValue(degreeAttName, "VARCHAR", rs));

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

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "CHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO person_name(`preferred`, `person_id`, `prefix`, `given_name`, `middle_name`, `family_name_prefix`, `family_name`, `family_name2`, `family_name_suffix`, `degree`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `changed_by`, `date_changed`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO person_name(`person_name_id`, `preferred`, `person_id`, `prefix`, `given_name`, `middle_name`, `family_name_prefix`, `family_name`, `family_name2`, `family_name_suffix`, `degree`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `changed_by`, `date_changed`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.preferred.getValue(), this.personId.getValue(), this.prefix.getValue(),
				this.givenName.getValue(), this.middleName.getValue(), this.familyNamePrefix.getValue(),
				this.familyName.getValue(), this.familyName2.getValue(), this.familyNameSuffix.getValue(),
				this.degree.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.changedBy.getValue(),
				this.dateChanged, this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.personNameId.getValue(), this.preferred.getValue(), this.personId.getValue(),
				this.prefix.getValue(), this.givenName.getValue(), this.middleName.getValue(),
				this.familyNamePrefix.getValue(), this.familyName.getValue(), this.familyName2.getValue(),
				this.familyNameSuffix.getValue(), this.degree.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.changedBy.getValue(), this.dateChanged, this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.personNameId.getValue(), this.preferred.getValue(), this.personId.getValue(),
				this.prefix.getValue(), this.givenName.getValue(), this.middleName.getValue(),
				this.familyNamePrefix.getValue(), this.familyName.getValue(), this.familyName2.getValue(),
				this.familyNameSuffix.getValue(), this.degree.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.changedBy.getValue(), this.dateChanged, this.uuid, this.personNameId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE person_name SET `person_name_id` = ?, `preferred` = ?, `person_id` = ?, `prefix` = ?, `given_name` = ?, `middle_name` = ?, `family_name_prefix` = ?, `family_name` = ?, `family_name2` = ?, `family_name_suffix` = ?, `degree` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `changed_by` = ?, `date_changed` = ?, `uuid` = ? WHERE person_name_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.preferred.getValue() != null ? "\"" + this.preferred.getValue() + "\"" : null) + ","
				+ (this.personId.getValue()) + ","
				+ (this.prefix.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.prefix.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.givenName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.givenName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.middleName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.middleName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.familyNamePrefix.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.familyNamePrefix.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.familyName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.familyName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.familyName2.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.familyName2.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.familyNameSuffix.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.familyNameSuffix.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.degree.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.degree.getValue().toString()) + "\""
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
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.personNameId.getValue()) + ","
				+ (this.preferred.getValue() != null ? "\"" + this.preferred.getValue() + "\"" : null) + ","
				+ (this.personId.getValue()) + ","
				+ (this.prefix.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.prefix.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.givenName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.givenName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.middleName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.middleName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.familyNamePrefix.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.familyNamePrefix.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.familyName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.familyName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.familyName2.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.familyName2.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.familyNameSuffix.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.familyNameSuffix.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.degree.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.degree.getValue().toString()) + "\""
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
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PersonNameVO copy = new PersonNameVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.personId.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("personId"))
			return this.personId.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "person_name";
	}

}