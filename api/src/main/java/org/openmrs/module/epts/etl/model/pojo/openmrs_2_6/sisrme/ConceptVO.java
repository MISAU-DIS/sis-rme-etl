package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

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

public class ConceptVO extends AbstractGeneratedDatabaseObject {
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field shortName = Field.fastCreateWithType("short_name", "VARCHAR");
	private Field description = Field.fastCreateWithType("description", "TEXT");
	private Field formText = Field.fastCreateWithType("form_text", "TEXT");
	private Field datatypeId = Field.fastCreateWithType("datatype_id", "INT");
	private Field classId = Field.fastCreateWithType("class_id", "INT");
	private Field isSet = Field.fastCreateWithType("is_set", "BIT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field version = Field.fastCreateWithType("version", "VARCHAR");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");

	public ConceptVO() {
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

	public void setRetired(Field retired) {
		this.retired = retired;
	}

	public void setRetiredValue(Boolean value) {
		this.retired.setValue(value);
	}

	public Field getRetired() {
		return this.retired;
	}

	public void setShortName(Field shortName) {
		this.shortName = shortName;
	}

	public void setShortNameValue(String value) {
		this.shortName.setValue(value);
	}

	public Field getShortName() {
		return this.shortName;
	}

	public void setDescription(Field description) {
		this.description = description;
	}

	public void setDescriptionValue(String value) {
		this.description.setValue(value);
	}

	public Field getDescription() {
		return this.description;
	}

	public void setFormText(Field formText) {
		this.formText = formText;
	}

	public void setFormTextValue(String value) {
		this.formText.setValue(value);
	}

	public Field getFormText() {
		return this.formText;
	}

	public void setDatatypeId(Field datatypeId) {
		this.datatypeId = datatypeId;
	}

	public void setDatatypeIdValue(Integer value) {
		this.datatypeId.setValue(value);
	}

	public Field getDatatypeId() {
		return this.datatypeId;
	}

	public void setClassId(Field classId) {
		this.classId = classId;
	}

	public void setClassIdValue(Integer value) {
		this.classId.setValue(value);
	}

	public Field getClassId() {
		return this.classId;
	}

	public void setIsSet(Field isSet) {
		this.isSet = isSet;
	}

	public void setIsSetValue(Boolean value) {
		this.isSet.setValue(value);
	}

	public Field getIsSet() {
		return this.isSet;
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

	public void setVersion(Field version) {
		this.version = version;
	}

	public void setVersionValue(String value) {
		this.version.setValue(value);
	}

	public Field getVersion() {
		return this.version;
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

	public void setRetiredBy(Field retiredBy) {
		this.retiredBy = retiredBy;
	}

	public void setRetiredByValue(Integer value) {
		this.retiredBy.setValue(value);
	}

	public Field getRetiredBy() {
		return this.retiredBy;
	}

	public void setDateRetired(Field dateRetired) {
		this.dateRetired = dateRetired;
	}

	public void setDateRetiredValue(java.util.Date value) {
		this.dateRetired.setValue(value);
	}

	public Field getDateRetired() {
		return this.dateRetired;
	}

	public void setRetireReason(Field retireReason) {
		this.retireReason = retireReason;
	}

	public void setRetireReasonValue(String value) {
		this.retireReason.setValue(value);
	}

	public Field getRetireReason() {
		return this.retireReason;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));

		String retiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired", "_");

		this.retired.setValue(BaseVO.retrieveFieldValue(retiredAttName, "BIT", rs));

		String shortNameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"short_name", "_");

		this.shortName.setValue(BaseVO.retrieveFieldValue(shortNameAttName, "VARCHAR", rs));

		String descriptionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"description", "_");

		this.description.setValue(BaseVO.retrieveFieldValue(descriptionAttName, "TEXT", rs));

		String formTextAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"form_text", "_");

		this.formText.setValue(BaseVO.retrieveFieldValue(formTextAttName, "TEXT", rs));

		String datatypeIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"datatype_id", "_");

		this.datatypeId.setValue(BaseVO.retrieveFieldValue(datatypeIdAttName, "INT", rs));

		String classIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"class_id", "_");

		this.classId.setValue(BaseVO.retrieveFieldValue(classIdAttName, "INT", rs));

		String isSetAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "is_set",
				"_");

		this.isSet.setValue(BaseVO.retrieveFieldValue(isSetAttName, "BIT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String versionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"version", "_");

		this.version.setValue(BaseVO.retrieveFieldValue(versionAttName, "VARCHAR", rs));

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String retiredByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired_by", "_");

		this.retiredBy.setValue(BaseVO.retrieveFieldValue(retiredByAttName, "INT", rs));

		String dateRetiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_retired", "_");

		this.dateRetired.setValue(BaseVO.retrieveFieldValue(dateRetiredAttName, "DATETIME", rs));

		String retireReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retire_reason", "_");

		this.retireReason.setValue(BaseVO.retrieveFieldValue(retireReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO concept(`retired`, `short_name`, `description`, `form_text`, `datatype_id`, `class_id`, `is_set`, `creator`, `date_created`, `version`, `changed_by`, `date_changed`, `retired_by`, `date_retired`, `retire_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO concept(`concept_id`, `retired`, `short_name`, `description`, `form_text`, `datatype_id`, `class_id`, `is_set`, `creator`, `date_created`, `version`, `changed_by`, `date_changed`, `retired_by`, `date_retired`, `retire_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.retired.getValue(), this.shortName.getValue(), this.description.getValue(),
				this.formText.getValue(), this.datatypeId.getValue(), this.classId.getValue(), this.isSet.getValue(),
				this.creator.getValue(), this.dateCreated, this.version.getValue(), this.changedBy.getValue(),
				this.dateChanged, this.retiredBy.getValue(), this.dateRetired.getValue(), this.retireReason.getValue(),
				this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.conceptId.getValue(), this.retired.getValue(), this.shortName.getValue(),
				this.description.getValue(), this.formText.getValue(), this.datatypeId.getValue(),
				this.classId.getValue(), this.isSet.getValue(), this.creator.getValue(), this.dateCreated,
				this.version.getValue(), this.changedBy.getValue(), this.dateChanged, this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.conceptId.getValue(), this.retired.getValue(), this.shortName.getValue(),
				this.description.getValue(), this.formText.getValue(), this.datatypeId.getValue(),
				this.classId.getValue(), this.isSet.getValue(), this.creator.getValue(), this.dateCreated,
				this.version.getValue(), this.changedBy.getValue(), this.dateChanged, this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.conceptId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE concept SET `concept_id` = ?, `retired` = ?, `short_name` = ?, `description` = ?, `form_text` = ?, `datatype_id` = ?, `class_id` = ?, `is_set` = ?, `creator` = ?, `date_created` = ?, `version` = ?, `changed_by` = ?, `date_changed` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ? WHERE concept_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.shortName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.shortName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.formText.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formText.getValue().toString()) + "\""
						: null)
				+ "," + (this.datatypeId.getValue()) + "," + (this.classId.getValue()) + ","
				+ (this.isSet.getValue() != null ? "\"" + this.isSet.getValue() + "\"" : null) + ","
				+ (this.creator.getValue()) + ","
				+ (this.dateCreated != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\"" : null)
				+ ","
				+ (this.version.getValue() != null ? "\""
						+ utilities.scapeQuotationMarks(this.version.getValue().toString()) + "\"" : null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\"" : null)
				+ "," + (this.retiredBy.getValue()) + ","
				+ (this.dateRetired.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateRetired.getValue())
						+ "\"" : null)
				+ ","
				+ (this.retireReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retireReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.conceptId.getValue()) + ","
				+ (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.shortName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.shortName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.formText.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formText.getValue().toString()) + "\""
						: null)
				+ "," + (this.datatypeId.getValue()) + "," + (this.classId.getValue()) + ","
				+ (this.isSet.getValue() != null ? "\"" + this.isSet.getValue() + "\"" : null) + ","
				+ (this.creator.getValue()) + ","
				+ (this.dateCreated != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\"" : null)
				+ ","
				+ (this.version.getValue() != null ? "\""
						+ utilities.scapeQuotationMarks(this.version.getValue().toString()) + "\"" : null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\"" : null)
				+ "," + (this.retiredBy.getValue()) + ","
				+ (this.dateRetired.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateRetired.getValue())
						+ "\"" : null)
				+ ","
				+ (this.retireReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retireReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		ConceptVO copy = new ConceptVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.classId.getValue() != null)
			return true;

		if (this.datatypeId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		if (this.retiredBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("classId"))
			return this.classId.getValue();
		if (parentAttName.equals("datatypeId"))
			return this.datatypeId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("retiredBy"))
			return this.retiredBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "concept";
	}

}