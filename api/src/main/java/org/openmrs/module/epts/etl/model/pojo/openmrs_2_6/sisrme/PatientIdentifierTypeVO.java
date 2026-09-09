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

public class PatientIdentifierTypeVO extends AbstractGeneratedDatabaseObject {
	private Field patientIdentifierTypeId = Field.fastCreateWithType("patient_identifier_type_id", "INT");
	private Field name = Field.fastCreateWithType("name", "VARCHAR");
	private Field description = Field.fastCreateWithType("description", "TEXT");
	private Field format = Field.fastCreateWithType("format", "VARCHAR");
	private Field checkDigit = Field.fastCreateWithType("check_digit", "BIT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field required = Field.fastCreateWithType("required", "BIT");
	private Field formatDescription = Field.fastCreateWithType("format_description", "VARCHAR");
	private Field validator = Field.fastCreateWithType("validator", "VARCHAR");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");
	private Field locationBehavior = Field.fastCreateWithType("location_behavior", "VARCHAR");
	private Field uniquenessBehavior = Field.fastCreateWithType("uniqueness_behavior", "VARCHAR");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");

	public PatientIdentifierTypeVO() {
		this.metadata = true;
		this.fields.add(this.patientIdentifierTypeId);
		this.fields.add(this.name);
		this.fields.add(this.description);
		this.fields.add(this.format);
		this.fields.add(this.checkDigit);
		this.fields.add(this.creator);
		this.fields.add(this.required);
		this.fields.add(this.formatDescription);
		this.fields.add(this.validator);
		this.fields.add(this.retired);
		this.fields.add(this.retiredBy);
		this.fields.add(this.dateRetired);
		this.fields.add(this.retireReason);
		this.fields.add(this.locationBehavior);
		this.fields.add(this.uniquenessBehavior);
		this.fields.add(this.changedBy);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "patient_identifier_type_id")) {
			this.patientIdentifierTypeId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "patient_identifier_type_id")) {
			return this.patientIdentifierTypeId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "name")) {
			return this.name.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "description")) {
			return this.description.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "format")) {
			return this.format.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "check_digit")) {
			return this.checkDigit.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "required")) {
			return this.required.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "format_description")) {
			return this.formatDescription.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "validator")) {
			return this.validator.getValue();
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
		if (utilities.equalsFieldsName(fieldName, "location_behavior")) {
			return this.locationBehavior.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "uniqueness_behavior")) {
			return this.uniquenessBehavior.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "patient_identifier_type_id")) {
			this.patientIdentifierTypeId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "name")) {
			this.name.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "description")) {
			this.description.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "format")) {
			this.format.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "check_digit")) {
			this.checkDigit.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			this.creator.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "required")) {
			this.required.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "format_description")) {
			this.formatDescription.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "validator")) {
			this.validator.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "retired")) {
			this.retired.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "retired_by")) {
			this.retiredBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "date_retired")) {
			this.dateRetired.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "retire_reason")) {
			this.retireReason.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "location_behavior")) {
			this.locationBehavior.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "uniqueness_behavior")) {
			this.uniquenessBehavior.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.patientIdentifierTypeId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.name, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.description, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.format, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.checkDigit, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.required, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.formatDescription, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.validator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.retired, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.retiredBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dateRetired, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.retireReason, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.locationBehavior, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.uniquenessBehavior, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.changedBy, srcConn, dstConn);
	}

	public void setPatientIdentifierTypeId(Field patientIdentifierTypeId) {
		this.patientIdentifierTypeId = patientIdentifierTypeId;
	}

	public void setPatientIdentifierTypeIdValue(Integer value) {
		this.patientIdentifierTypeId.setValue(value);
	}

	public Field getPatientIdentifierTypeId() {
		return this.patientIdentifierTypeId;
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

	public void setFormat(Field format) {
		this.format = format;
	}

	public void setFormatValue(String value) {
		this.format.setValue(value);
	}

	public Field getFormat() {
		return this.format;
	}

	public void setCheckDigit(Field checkDigit) {
		this.checkDigit = checkDigit;
	}

	public void setCheckDigitValue(Boolean value) {
		this.checkDigit.setValue(value);
	}

	public Field getCheckDigit() {
		return this.checkDigit;
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

	public void setRequired(Field required) {
		this.required = required;
	}

	public void setRequiredValue(Boolean value) {
		this.required.setValue(value);
	}

	public Field getRequired() {
		return this.required;
	}

	public void setFormatDescription(Field formatDescription) {
		this.formatDescription = formatDescription;
	}

	public void setFormatDescriptionValue(String value) {
		this.formatDescription.setValue(value);
	}

	public Field getFormatDescription() {
		return this.formatDescription;
	}

	public void setValidator(Field validator) {
		this.validator = validator;
	}

	public void setValidatorValue(String value) {
		this.validator.setValue(value);
	}

	public Field getValidator() {
		return this.validator;
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

	public void setLocationBehavior(Field locationBehavior) {
		this.locationBehavior = locationBehavior;
	}

	public void setLocationBehaviorValue(String value) {
		this.locationBehavior.setValue(value);
	}

	public Field getLocationBehavior() {
		return this.locationBehavior;
	}

	public void setUniquenessBehavior(Field uniquenessBehavior) {
		this.uniquenessBehavior = uniquenessBehavior;
	}

	public void setUniquenessBehaviorValue(String value) {
		this.uniquenessBehavior.setValue(value);
	}

	public Field getUniquenessBehavior() {
		return this.uniquenessBehavior;
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

		String patientIdentifierTypeIdAttName = utilities.concatStringsWithSeparator(
				this.getRelatedConfiguration().getAlias(), "patient_identifier_type_id", "_");

		this.patientIdentifierTypeId.setValue(BaseVO.retrieveFieldValue(patientIdentifierTypeIdAttName, "INT", rs));

		String nameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "name",
				"_");

		this.name.setValue(BaseVO.retrieveFieldValue(nameAttName, "VARCHAR", rs));

		String descriptionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"description", "_");

		this.description.setValue(BaseVO.retrieveFieldValue(descriptionAttName, "TEXT", rs));

		String formatAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "format",
				"_");

		this.format.setValue(BaseVO.retrieveFieldValue(formatAttName, "VARCHAR", rs));

		String checkDigitAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"check_digit", "_");

		this.checkDigit.setValue(BaseVO.retrieveFieldValue(checkDigitAttName, "BIT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String requiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"required", "_");

		this.required.setValue(BaseVO.retrieveFieldValue(requiredAttName, "BIT", rs));

		String formatDescriptionAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "format_description", "_");

		this.formatDescription.setValue(BaseVO.retrieveFieldValue(formatDescriptionAttName, "VARCHAR", rs));

		String validatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"validator", "_");

		this.validator.setValue(BaseVO.retrieveFieldValue(validatorAttName, "VARCHAR", rs));

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

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "CHAR", rs));

		String locationBehaviorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_behavior", "_");

		this.locationBehavior.setValue(BaseVO.retrieveFieldValue(locationBehaviorAttName, "VARCHAR", rs));

		String uniquenessBehaviorAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uniqueness_behavior", "_");

		this.uniquenessBehavior.setValue(BaseVO.retrieveFieldValue(uniquenessBehaviorAttName, "VARCHAR", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO patient_identifier_type(`name`, `description`, `format`, `check_digit`, `creator`, `date_created`, `required`, `format_description`, `validator`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `location_behavior`, `uniqueness_behavior`, `date_changed`, `changed_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO patient_identifier_type(`patient_identifier_type_id`, `name`, `description`, `format`, `check_digit`, `creator`, `date_created`, `required`, `format_description`, `validator`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `location_behavior`, `uniqueness_behavior`, `date_changed`, `changed_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.name.getValue(), this.description.getValue(), this.format.getValue(),
				this.checkDigit.getValue(), this.creator.getValue(), this.dateCreated, this.required.getValue(),
				this.formatDescription.getValue(), this.validator.getValue(), this.retired.getValue(),
				this.retiredBy.getValue(), this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid,
				this.locationBehavior.getValue(), this.uniquenessBehavior.getValue(), this.dateChanged,
				this.changedBy.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.patientIdentifierTypeId.getValue(), this.name.getValue(), this.description.getValue(),
				this.format.getValue(), this.checkDigit.getValue(), this.creator.getValue(), this.dateCreated,
				this.required.getValue(), this.formatDescription.getValue(), this.validator.getValue(),
				this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.locationBehavior.getValue(),
				this.uniquenessBehavior.getValue(), this.dateChanged, this.changedBy.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.patientIdentifierTypeId.getValue(), this.name.getValue(), this.description.getValue(),
				this.format.getValue(), this.checkDigit.getValue(), this.creator.getValue(), this.dateCreated,
				this.required.getValue(), this.formatDescription.getValue(), this.validator.getValue(),
				this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.locationBehavior.getValue(),
				this.uniquenessBehavior.getValue(), this.dateChanged, this.changedBy.getValue(),
				this.patientIdentifierTypeId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE patient_identifier_type SET `patient_identifier_type_id` = ?, `name` = ?, `description` = ?, `format` = ?, `check_digit` = ?, `creator` = ?, `date_created` = ?, `required` = ?, `format_description` = ?, `validator` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ?, `location_behavior` = ?, `uniqueness_behavior` = ?, `date_changed` = ?, `changed_by` = ? WHERE patient_identifier_type_id = ? ";
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
				+ ","
				+ (this.format.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.format.getValue().toString()) + "\""
						: null)
				+ "," + (this.checkDigit.getValue() != null ? "\"" + this.checkDigit.getValue() + "\"" : null) + ","
				+ (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.required.getValue() != null ? "\"" + this.required.getValue() + "\"" : null) + ","
				+ (this.formatDescription.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formatDescription.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.validator.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.validator.getValue().toString()) + "\""
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
				+ ","
				+ (this.locationBehavior.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.locationBehavior.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.uniquenessBehavior.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.uniquenessBehavior.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.patientIdentifierTypeId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.format.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.format.getValue().toString()) + "\""
						: null)
				+ "," + (this.checkDigit.getValue() != null ? "\"" + this.checkDigit.getValue() + "\"" : null) + ","
				+ (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.required.getValue() != null ? "\"" + this.required.getValue() + "\"" : null) + ","
				+ (this.formatDescription.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formatDescription.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.validator.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.validator.getValue().toString()) + "\""
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
				+ ","
				+ (this.locationBehavior.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.locationBehavior.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.uniquenessBehavior.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.uniquenessBehavior.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PatientIdentifierTypeVO copy = new PatientIdentifierTypeVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
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
		return "patient_identifier_type";
	}

}