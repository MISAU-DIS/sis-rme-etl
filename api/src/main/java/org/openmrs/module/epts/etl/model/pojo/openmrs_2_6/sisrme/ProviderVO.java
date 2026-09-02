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

public class ProviderVO extends AbstractGeneratedDatabaseObject {
	private Field providerId = Field.fastCreateWithType("provider_id", "INT");
	private Field encounterRoleId = Field.fastCreateWithType("encounter_role_id", "INT");
	private Field personId = Field.fastCreateWithType("person_id", "INT");
	private Field name = Field.fastCreateWithType("name", "VARCHAR");
	private Field identifier = Field.fastCreateWithType("identifier", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");
	private Field providerRoleId = Field.fastCreateWithType("provider_role_id", "INT");
	private Field roleId = Field.fastCreateWithType("role_id", "INT");
	private Field specialityId = Field.fastCreateWithType("speciality_id", "INT");

	public ProviderVO() {
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

	public void setProviderId(Field providerId) {
		this.providerId = providerId;
	}

	public void setProviderIdValue(Integer value) {
		this.providerId.setValue(value);
	}

	public Field getProviderId() {
		return this.providerId;
	}

	public void setEncounterRoleId(Field encounterRoleId) {
		this.encounterRoleId = encounterRoleId;
	}

	public void setEncounterRoleIdValue(Integer value) {
		this.encounterRoleId.setValue(value);
	}

	public Field getEncounterRoleId() {
		return this.encounterRoleId;
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

	public void setName(Field name) {
		this.name = name;
	}

	public void setNameValue(String value) {
		this.name.setValue(value);
	}

	public Field getName() {
		return this.name;
	}

	public void setIdentifier(Field identifier) {
		this.identifier = identifier;
	}

	public void setIdentifierValue(String value) {
		this.identifier.setValue(value);
	}

	public Field getIdentifier() {
		return this.identifier;
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

	public void setProviderRoleId(Field providerRoleId) {
		this.providerRoleId = providerRoleId;
	}

	public void setProviderRoleIdValue(Integer value) {
		this.providerRoleId.setValue(value);
	}

	public Field getProviderRoleId() {
		return this.providerRoleId;
	}

	public void setRoleId(Field roleId) {
		this.roleId = roleId;
	}

	public void setRoleIdValue(Integer value) {
		this.roleId.setValue(value);
	}

	public Field getRoleId() {
		return this.roleId;
	}

	public void setSpecialityId(Field specialityId) {
		this.specialityId = specialityId;
	}

	public void setSpecialityIdValue(Integer value) {
		this.specialityId.setValue(value);
	}

	public Field getSpecialityId() {
		return this.specialityId;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String providerIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"provider_id", "_");

		this.providerId.setValue(BaseVO.retrieveFieldValue(providerIdAttName, "INT", rs));

		String encounterRoleIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"encounter_role_id", "_");

		this.encounterRoleId.setValue(BaseVO.retrieveFieldValue(encounterRoleIdAttName, "INT", rs));

		String personIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_id", "_");

		this.personId.setValue(BaseVO.retrieveFieldValue(personIdAttName, "INT", rs));

		String nameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "name",
				"_");

		this.name.setValue(BaseVO.retrieveFieldValue(nameAttName, "VARCHAR", rs));

		String identifierAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"identifier", "_");

		this.identifier.setValue(BaseVO.retrieveFieldValue(identifierAttName, "VARCHAR", rs));

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

		String providerRoleIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"provider_role_id", "_");

		this.providerRoleId.setValue(BaseVO.retrieveFieldValue(providerRoleIdAttName, "INT", rs));

		String roleIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"role_id", "_");

		this.roleId.setValue(BaseVO.retrieveFieldValue(roleIdAttName, "INT", rs));

		String specialityIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"speciality_id", "_");

		this.specialityId.setValue(BaseVO.retrieveFieldValue(specialityIdAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO provider(`encounter_role_id`, `person_id`, `name`, `identifier`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `provider_role_id`, `role_id`, `speciality_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO provider(`provider_id`, `encounter_role_id`, `person_id`, `name`, `identifier`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `provider_role_id`, `role_id`, `speciality_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.encounterRoleId.getValue(), this.personId.getValue(), this.name.getValue(),
				this.identifier.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.providerRoleId.getValue(), this.roleId.getValue(),
				this.specialityId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.providerId.getValue(), this.encounterRoleId.getValue(), this.personId.getValue(),
				this.name.getValue(), this.identifier.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.providerRoleId.getValue(),
				this.roleId.getValue(), this.specialityId.getValue() };
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
		Object[] params = { this.providerId.getValue(), this.encounterRoleId.getValue(), this.personId.getValue(),
				this.name.getValue(), this.identifier.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.providerRoleId.getValue(),
				this.roleId.getValue(), this.specialityId.getValue(), this.providerId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE provider SET `provider_id` = ?, `encounter_role_id` = ?, `person_id` = ?, `name` = ?, `identifier` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ?, `provider_role_id` = ?, `role_id` = ?, `speciality_id` = ? WHERE provider_21.provider_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.encounterRoleId.getValue()) + "," + (this.personId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.identifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.identifier.getValue().toString()) + "\""
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
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.providerRoleId.getValue()) + "," + (this.roleId.getValue()) + ","
				+ (this.specialityId.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.providerId.getValue()) + "," + (this.encounterRoleId.getValue()) + ","
				+ (this.personId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.identifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.identifier.getValue().toString()) + "\""
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
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.providerRoleId.getValue()) + "," + (this.roleId.getValue()) + ","
				+ (this.specialityId.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		ProviderVO copy = new ProviderVO();

		copy.providerId = copyGeneratedField(this.providerId);
		copy.encounterRoleId = copyGeneratedField(this.encounterRoleId);
		copy.personId = copyGeneratedField(this.personId);
		copy.name = copyGeneratedField(this.name);
		copy.identifier = copyGeneratedField(this.identifier);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.changedBy = copyGeneratedField(this.changedBy);
		copy.dateChanged = this.dateChanged;
		copy.retired = copyGeneratedField(this.retired);
		copy.retiredBy = copyGeneratedField(this.retiredBy);
		copy.dateRetired = copyGeneratedField(this.dateRetired);
		copy.retireReason = copyGeneratedField(this.retireReason);
		copy.uuid = this.uuid;
		copy.providerRoleId = copyGeneratedField(this.providerRoleId);
		copy.roleId = copyGeneratedField(this.roleId);

		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.roleId.getValue() != null)
			return true;

		if (this.specialityId.getValue() != null)
			return true;

		if (this.encounterRoleId.getValue() != null)
			return true;

		if (this.personId.getValue() != null)
			return true;

		if (this.providerRoleId.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.retiredBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("roleId"))
			return this.roleId.getValue();
		if (parentAttName.equals("specialityId"))
			return this.specialityId.getValue();
		if (parentAttName.equals("encounterRoleId"))
			return this.encounterRoleId.getValue();
		if (parentAttName.equals("personId"))
			return this.personId.getValue();
		if (parentAttName.equals("providerRoleId"))
			return this.providerRoleId.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("retiredBy"))
			return this.retiredBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "provider";
	}

}