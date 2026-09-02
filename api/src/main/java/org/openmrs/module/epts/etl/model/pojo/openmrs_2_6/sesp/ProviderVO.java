package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp;

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


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProviderVO extends AbstractGeneratedDatabaseObject {
	private Field providerId = Field.fastCreateWithType("provider_id", "INT");
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

		this.fields.add(this.providerId);
		this.fields.add(this.personId);
		this.fields.add(this.name);
		this.fields.add(this.identifier);
		this.fields.add(this.creator);
		this.fields.add(this.changedBy);
		this.fields.add(this.retired);
		this.fields.add(this.retiredBy);
		this.fields.add(this.dateRetired);
		this.fields.add(this.retireReason);
		this.fields.add(this.providerRoleId);
		this.fields.add(this.roleId);
		this.fields.add(this.specialityId);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "provider_id")) {
			this.providerId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "provider_id")) {
			return this.providerId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "person_id")) {
			return this.personId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "name")) {
			return this.name.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "identifier")) {
			return this.identifier.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "retired")) {
			return this.retired.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "retired_by")) {
			return this.retiredBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_retired")) {
			return this.dateRetired.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "retire_reason")) {
			return this.retireReason.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "provider_role_id")) {
			return this.providerRoleId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "role_id")) {
			return this.roleId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "speciality_id")) {
			return this.specialityId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_created")) return this.dateCreated;
		if (utilities.equalsFieldsName(fieldName, "date_changed")) return this.dateChanged;
		if (utilities.equalsFieldsName(fieldName, "date_voided")) return this.dateVoided;
		if (utilities.equalsFieldsName(fieldName, "uuid")) return this.uuid;
		return super.getFieldValue(fieldName);
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

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

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

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));

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
		return "INSERT INTO provider(`person_id`, `name`, `identifier`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `provider_role_id`, `role_id`, `speciality_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO provider(`provider_id`, `person_id`, `name`, `identifier`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `provider_role_id`, `role_id`, `speciality_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.personId.getValue(), this.name.getValue(), this.identifier.getValue(),
				this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.providerRoleId.getValue(), this.roleId.getValue(),
				this.specialityId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.providerId.getValue(), this.personId.getValue(), this.name.getValue(),
				this.identifier.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.providerRoleId.getValue(), this.roleId.getValue(),
				this.specialityId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.providerId.getValue(), this.personId.getValue(), this.name.getValue(),
				this.identifier.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.providerRoleId.getValue(), this.roleId.getValue(),
				this.specialityId.getValue(), this.providerId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE provider SET `provider_id` = ?, `person_id` = ?, `name` = ?, `identifier` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ?, `provider_role_id` = ?, `role_id` = ?, `speciality_id` = ? WHERE provider_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.personId.getValue()) + ","
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
		return "" + (this.providerId.getValue()) + "," + (this.personId.getValue()) + ","
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
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.roleId.getValue() != null)
			return true;

		if (this.specialityId.getValue() != null)
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