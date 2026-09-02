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

public class FormVO extends AbstractGeneratedDatabaseObject {
	private Field formId = Field.fastCreateWithType("form_id", "INT");
	private Field name = Field.fastCreateWithType("name", "VARCHAR");
	private Field version = Field.fastCreateWithType("version", "VARCHAR");
	private Field build = Field.fastCreateWithType("build", "INT");
	private Field published = Field.fastCreateWithType("published", "BIT");
	private Field description = Field.fastCreateWithType("description", "TEXT");
	private Field encounterType = Field.fastCreateWithType("encounter_type", "INT");
	private Field template = Field.fastCreateWithType("template", "MEDIUMTEXT");
	private Field xslt = Field.fastCreateWithType("xslt", "MEDIUMTEXT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retiredReason = Field.fastCreateWithType("retired_reason", "VARCHAR");

	public FormVO() {
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

	public void setFormId(Field formId) {
		this.formId = formId;
	}

	public void setFormIdValue(Integer value) {
		this.formId.setValue(value);
	}

	public Field getFormId() {
		return this.formId;
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

	public void setVersion(Field version) {
		this.version = version;
	}

	public void setVersionValue(String value) {
		this.version.setValue(value);
	}

	public Field getVersion() {
		return this.version;
	}

	public void setBuild(Field build) {
		this.build = build;
	}

	public void setBuildValue(Integer value) {
		this.build.setValue(value);
	}

	public Field getBuild() {
		return this.build;
	}

	public void setPublished(Field published) {
		this.published = published;
	}

	public void setPublishedValue(Boolean value) {
		this.published.setValue(value);
	}

	public Field getPublished() {
		return this.published;
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

	public void setEncounterType(Field encounterType) {
		this.encounterType = encounterType;
	}

	public void setEncounterTypeValue(Integer value) {
		this.encounterType.setValue(value);
	}

	public Field getEncounterType() {
		return this.encounterType;
	}

	public void setTemplate(Field template) {
		this.template = template;
	}

	public void setTemplateValue(String value) {
		this.template.setValue(value);
	}

	public Field getTemplate() {
		return this.template;
	}

	public void setXslt(Field xslt) {
		this.xslt = xslt;
	}

	public void setXsltValue(String value) {
		this.xslt.setValue(value);
	}

	public Field getXslt() {
		return this.xslt;
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

	public void setChangedBy(Field changedBy) {
		this.changedBy = changedBy;
	}

	public void setChangedByValue(Integer value) {
		this.changedBy.setValue(value);
	}

	public Field getChangedBy() {
		return this.changedBy;
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

	public void setRetiredReason(Field retiredReason) {
		this.retiredReason = retiredReason;
	}

	public void setRetiredReasonValue(String value) {
		this.retiredReason.setValue(value);
	}

	public Field getRetiredReason() {
		return this.retiredReason;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String formIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"form_id", "_");

		this.formId.setValue(BaseVO.retrieveFieldValue(formIdAttName, "INT", rs));

		String nameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "name",
				"_");

		this.name.setValue(BaseVO.retrieveFieldValue(nameAttName, "VARCHAR", rs));

		String versionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"version", "_");

		this.version.setValue(BaseVO.retrieveFieldValue(versionAttName, "VARCHAR", rs));

		String buildAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "build",
				"_");

		this.build.setValue(BaseVO.retrieveFieldValue(buildAttName, "INT", rs));

		String publishedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"published", "_");

		this.published.setValue(BaseVO.retrieveFieldValue(publishedAttName, "BIT", rs));

		String descriptionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"description", "_");

		this.description.setValue(BaseVO.retrieveFieldValue(descriptionAttName, "TEXT", rs));

		String encounterTypeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"encounter_type", "_");

		this.encounterType.setValue(BaseVO.retrieveFieldValue(encounterTypeAttName, "INT", rs));

		String templateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"template", "_");

		this.template.setValue(BaseVO.retrieveFieldValue(templateAttName, "MEDIUMTEXT", rs));

		String xsltAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "xslt",
				"_");

		this.xslt.setValue(BaseVO.retrieveFieldValue(xsltAttName, "MEDIUMTEXT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = rs.getTimestamp(dateCreatedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateCreatedAttName).getTime())
				: null;

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = rs.getTimestamp(dateChangedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateChangedAttName).getTime())
				: null;

		String retiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired", "_");

		this.retired.setValue(BaseVO.retrieveFieldValue(retiredAttName, "BIT", rs));

		String retiredByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired_by", "_");

		this.retiredBy.setValue(BaseVO.retrieveFieldValue(retiredByAttName, "INT", rs));

		String dateRetiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_retired", "_");

		this.dateRetired.setValue(BaseVO.retrieveFieldValue(dateRetiredAttName, "DATETIME", rs));

		String retiredReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired_reason", "_");

		this.retiredReason.setValue(BaseVO.retrieveFieldValue(retiredReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO form(`name`, `version`, `build`, `published`, `description`, `encounter_type`, `template`, `xslt`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retired_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO form(`form_id`, `name`, `version`, `build`, `published`, `description`, `encounter_type`, `template`, `xslt`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retired_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.name.getValue(), this.version.getValue(), this.build.getValue(),
				this.published.getValue(), this.description.getValue(), this.encounterType.getValue(),
				this.template.getValue(), this.xslt.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retiredReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.formId.getValue(), this.name.getValue(), this.version.getValue(),
				this.build.getValue(), this.published.getValue(), this.description.getValue(),
				this.encounterType.getValue(), this.template.getValue(), this.xslt.getValue(), this.creator.getValue(),
				this.dateCreated, this.changedBy.getValue(), this.dateChanged, this.retired.getValue(),
				this.retiredBy.getValue(), this.dateRetired.getValue(), this.retiredReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.formId.getValue(), this.name.getValue(), this.version.getValue(),
				this.build.getValue(), this.published.getValue(), this.description.getValue(),
				this.encounterType.getValue(), this.template.getValue(), this.xslt.getValue(), this.creator.getValue(),
				this.dateCreated, this.changedBy.getValue(), this.dateChanged, this.retired.getValue(),
				this.retiredBy.getValue(), this.dateRetired.getValue(), this.retiredReason.getValue(), this.uuid,
				this.formId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE form SET `form_id` = ?, `name` = ?, `version` = ?, `build` = ?, `published` = ?, `description` = ?, `encounter_type` = ?, `template` = ?, `xslt` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retired_reason` = ?, `uuid` = ? WHERE form_36.form_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.version.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.version.getValue().toString()) + "\""
						: null)
				+ "," + (this.build.getValue()) + ","
				+ (this.published.getValue() != null ? "\"" + this.published.getValue() + "\"" : null) + ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ "," + (this.encounterType.getValue()) + ","
				+ (this.template.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.template.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.xslt.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.xslt.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.retiredBy.getValue()) + ","
				+ (this.dateRetired.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateRetired.getValue())
						+ "\"" : null)
				+ ","
				+ (this.retiredReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retiredReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.formId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.version.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.version.getValue().toString()) + "\""
						: null)
				+ "," + (this.build.getValue()) + ","
				+ (this.published.getValue() != null ? "\"" + this.published.getValue() + "\"" : null) + ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ "," + (this.encounterType.getValue()) + ","
				+ (this.template.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.template.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.xslt.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.xslt.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.retiredBy.getValue()) + ","
				+ (this.dateRetired.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateRetired.getValue())
						+ "\"" : null)
				+ ","
				+ (this.retiredReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retiredReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		FormVO copy = new FormVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.encounterType.getValue() != null)
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
		if (parentAttName.equals("encounterType"))
			return this.encounterType.getValue();
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
		return "form";
	}

}