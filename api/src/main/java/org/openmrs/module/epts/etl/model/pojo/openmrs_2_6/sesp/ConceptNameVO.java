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

public class ConceptNameVO extends AbstractGeneratedDatabaseObject {
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");
	private Field name = Field.fastCreateWithType("name", "VARCHAR");
	private Field locale = Field.fastCreateWithType("locale", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field conceptNameId = Field.fastCreateWithType("concept_name_id", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field conceptNameType = Field.fastCreateWithType("concept_name_type", "VARCHAR");
	private Field localePreferred = Field.fastCreateWithType("locale_preferred", "BIT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");

	public ConceptNameVO() {
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

	public void setConceptId(Field conceptId) {
		this.conceptId = conceptId;
	}

	public void setConceptIdValue(Integer value) {
		this.conceptId.setValue(value);
	}

	public Field getConceptId() {
		return this.conceptId;
	}

	public void setName(Field name) {
		this.name = name;
	}

	public void setNameValue(String value) {
		this.name.setValue(value);
	}

	public Field getName() {
		return this.name;
	}

	public void setLocale(Field locale) {
		this.locale = locale;
	}

	public void setLocaleValue(String value) {
		this.locale.setValue(value);
	}

	public Field getLocale() {
		return this.locale;
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

	public void setConceptNameId(Field conceptNameId) {
		this.conceptNameId = conceptNameId;
	}

	public void setConceptNameIdValue(Integer value) {
		this.conceptNameId.setValue(value);
	}

	public Field getConceptNameId() {
		return this.conceptNameId;
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

	public void setConceptNameType(Field conceptNameType) {
		this.conceptNameType = conceptNameType;
	}

	public void setConceptNameTypeValue(String value) {
		this.conceptNameType.setValue(value);
	}

	public Field getConceptNameType() {
		return this.conceptNameType;
	}

	public void setLocalePreferred(Field localePreferred) {
		this.localePreferred = localePreferred;
	}

	public void setLocalePreferredValue(Boolean value) {
		this.localePreferred.setValue(value);
	}

	public Field getLocalePreferred() {
		return this.localePreferred;
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

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));

		String nameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "name",
				"_");

		this.name.setValue(BaseVO.retrieveFieldValue(nameAttName, "VARCHAR", rs));

		String localeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "locale",
				"_");

		this.locale.setValue(BaseVO.retrieveFieldValue(localeAttName, "VARCHAR", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = rs.getTimestamp(dateCreatedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateCreatedAttName).getTime())
				: null;

		String conceptNameIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_name_id", "_");

		this.conceptNameId.setValue(BaseVO.retrieveFieldValue(conceptNameIdAttName, "INT", rs));

		String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided",
				"_");

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "BIT", rs));

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

		String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_voided", "_");

		this.dateVoided = rs.getTimestamp(dateVoidedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateVoidedAttName).getTime())
				: null;

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);

		String conceptNameTypeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_name_type", "_");

		this.conceptNameType.setValue(BaseVO.retrieveFieldValue(conceptNameTypeAttName, "VARCHAR", rs));

		String localePreferredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"locale_preferred", "_");

		this.localePreferred.setValue(BaseVO.retrieveFieldValue(localePreferredAttName, "BIT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = rs.getTimestamp(dateChangedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateChangedAttName).getTime())
				: null;

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO concept_name(`concept_id`, `name`, `locale`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `concept_name_type`, `locale_preferred`, `date_changed`, `changed_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO concept_name(`concept_id`, `name`, `locale`, `creator`, `date_created`, `concept_name_id`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `concept_name_type`, `locale_preferred`, `date_changed`, `changed_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.conceptId.getValue(), this.name.getValue(), this.locale.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid, this.conceptNameType.getValue(),
				this.localePreferred.getValue(), this.dateChanged, this.changedBy.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.conceptId.getValue(), this.name.getValue(), this.locale.getValue(),
				this.creator.getValue(), this.dateCreated, this.conceptNameId.getValue(), this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid,
				this.conceptNameType.getValue(), this.localePreferred.getValue(), this.dateChanged,
				this.changedBy.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.conceptId.getValue(), this.name.getValue(), this.locale.getValue(),
				this.creator.getValue(), this.dateCreated, this.conceptNameId.getValue(), this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid,
				this.conceptNameType.getValue(), this.localePreferred.getValue(), this.dateChanged,
				this.changedBy.getValue(), this.conceptNameId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE concept_name SET `concept_id` = ?, `name` = ?, `locale` = ?, `creator` = ?, `date_created` = ?, `concept_name_id` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ?, `concept_name_type` = ?, `locale_preferred` = ?, `date_changed` = ?, `changed_by` = ? WHERE concept_name_32.concept_name_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.conceptId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.locale.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.locale.getValue().toString()) + "\""
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
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ ","
				+ (this.conceptNameType.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.conceptNameType.getValue().toString()) + "\""
						: null)
				+ "," + (this.localePreferred.getValue() != null ? "\"" + this.localePreferred.getValue() + "\"" : null)
				+ ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.conceptId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.locale.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.locale.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.conceptNameId.getValue()) + ","
				+ (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ ","
				+ (this.conceptNameType.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.conceptNameType.getValue().toString()) + "\""
						: null)
				+ "," + (this.localePreferred.getValue() != null ? "\"" + this.localePreferred.getValue() + "\"" : null)
				+ ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		ConceptNameVO copy = new ConceptNameVO();

		copy.conceptId = copyGeneratedField(this.conceptId);
		copy.name = copyGeneratedField(this.name);
		copy.locale = copyGeneratedField(this.locale);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.conceptNameId = copyGeneratedField(this.conceptNameId);
		copy.voided = copyGeneratedField(this.voided);
		copy.voidedBy = copyGeneratedField(this.voidedBy);
		copy.dateVoided = this.dateVoided;
		copy.voidReason = copyGeneratedField(this.voidReason);
		copy.uuid = this.uuid;
		copy.conceptNameType = copyGeneratedField(this.conceptNameType);
		copy.localePreferred = copyGeneratedField(this.localePreferred);
		copy.dateChanged = this.dateChanged;

		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.conceptId.getValue() != null)
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
		if (parentAttName.equals("conceptId"))
			return this.conceptId.getValue();
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
		return "concept_name";
	}

}