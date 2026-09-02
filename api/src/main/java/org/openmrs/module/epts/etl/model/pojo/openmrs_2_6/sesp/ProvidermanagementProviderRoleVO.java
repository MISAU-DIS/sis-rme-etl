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

public class ProvidermanagementProviderRoleVO extends AbstractGeneratedDatabaseObject {
	private Field providerRoleId = Field.fastCreateWithType("provider_role_id", "INT");
	private Field name = Field.fastCreateWithType("name", "VARCHAR");
	private Field description = Field.fastCreateWithType("description", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");

	public ProvidermanagementProviderRoleVO() {
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

	public void setProviderRoleId(Field providerRoleId) {
		this.providerRoleId = providerRoleId;
	}

	public void setProviderRoleIdValue(Integer value) {
		this.providerRoleId.setValue(value);
	}

	public Field getProviderRoleId() {
		return this.providerRoleId;
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

	public void setDescription(Field description) {
		this.description = description;
	}

	public void setDescriptionValue(String value) {
		this.description.setValue(value);
	}

	public Field getDescription() {
		return this.description;
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

		String providerRoleIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"provider_role_id", "_");

		this.providerRoleId.setValue(BaseVO.retrieveFieldValue(providerRoleIdAttName, "INT", rs));

		String nameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "name",
				"_");

		this.name.setValue(BaseVO.retrieveFieldValue(nameAttName, "VARCHAR", rs));

		String descriptionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"description", "_");

		this.description.setValue(BaseVO.retrieveFieldValue(descriptionAttName, "VARCHAR", rs));

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

		String retireReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retire_reason", "_");

		this.retireReason.setValue(BaseVO.retrieveFieldValue(retireReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO providermanagement_provider_role(`name`, `description`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO providermanagement_provider_role(`provider_role_id`, `name`, `description`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.name.getValue(), this.description.getValue(), this.creator.getValue(),
				this.dateCreated, this.changedBy.getValue(), this.dateChanged, this.retired.getValue(),
				this.retiredBy.getValue(), this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.providerRoleId.getValue(), this.name.getValue(), this.description.getValue(),
				this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.providerRoleId.getValue(), this.name.getValue(), this.description.getValue(),
				this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.providerRoleId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE providermanagement_provider_role SET `provider_role_id` = ?, `name` = ?, `description` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ? WHERE providermanagement_provider_role_17.provider_role_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
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
				+ (this.retireReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retireReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.providerRoleId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
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
				+ (this.retireReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retireReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		ProvidermanagementProviderRoleVO copy = new ProvidermanagementProviderRoleVO();

		copy.providerRoleId = copyGeneratedField(this.providerRoleId);
		copy.name = copyGeneratedField(this.name);
		copy.description = copyGeneratedField(this.description);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.changedBy = copyGeneratedField(this.changedBy);
		copy.dateChanged = this.dateChanged;
		copy.retired = copyGeneratedField(this.retired);
		copy.retiredBy = copyGeneratedField(this.retiredBy);
		copy.dateRetired = copyGeneratedField(this.dateRetired);
		copy.retireReason = copyGeneratedField(this.retireReason);

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
		return "providermanagement_provider_role";
	}

}