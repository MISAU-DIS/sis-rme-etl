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


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ConditionsVO extends AbstractGeneratedDatabaseObject {
	private Field conditionId = Field.fastCreateWithType("condition_id", "INT");
	private Field conditionCoded = Field.fastCreateWithType("condition_coded", "INT");
	private Field conditionNonCoded = Field.fastCreateWithType("condition_non_coded", "VARCHAR");
	private Field clinicalStatus = Field.fastCreateWithType("clinical_status", "VARCHAR");
	private Field onsetDate = Field.fastCreateWithType("onset_date", "DATETIME");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field encounterId = Field.fastCreateWithType("encounter_id", "INT");
	private Field formNamespaceAndPath = Field.fastCreateWithType("form_namespace_and_path", "VARCHAR");

	public ConditionsVO() {
		this.metadata = false;

		this.fields.add(this.conditionId);
		this.fields.add(this.conditionCoded);
		this.fields.add(this.conditionNonCoded);
		this.fields.add(this.clinicalStatus);
		this.fields.add(this.onsetDate);
		this.fields.add(this.voided);
		this.fields.add(this.voidReason);
		this.fields.add(this.creator);
		this.fields.add(this.voidedBy);
		this.fields.add(this.changedBy);
		this.fields.add(this.patientId);
		this.fields.add(this.encounterId);
		this.fields.add(this.formNamespaceAndPath);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "condition_id")) {
			this.conditionId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "condition_id")) {
			return this.conditionId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "condition_coded")) {
			return this.conditionCoded.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "condition_non_coded")) {
			return this.conditionNonCoded.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "clinical_status")) {
			return this.clinicalStatus.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "onset_date")) {
			return this.onsetDate.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "voided")) {
			return this.voided.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "void_reason")) {
			return this.voidReason.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "voided_by")) {
			return this.voidedBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "encounter_id")) {
			return this.encounterId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "form_namespace_and_path")) {
			return this.formNamespaceAndPath.getValue();
		}
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

	public void setConditionId(Field conditionId) {
		this.conditionId = conditionId;
	}

	public void setConditionIdValue(Integer value) {
		this.conditionId.setValue(value);
	}

	public Field getConditionId() {
		return this.conditionId;
	}

	public void setConditionCoded(Field conditionCoded) {
		this.conditionCoded = conditionCoded;
	}

	public void setConditionCodedValue(Integer value) {
		this.conditionCoded.setValue(value);
	}

	public Field getConditionCoded() {
		return this.conditionCoded;
	}

	public void setConditionNonCoded(Field conditionNonCoded) {
		this.conditionNonCoded = conditionNonCoded;
	}

	public void setConditionNonCodedValue(String value) {
		this.conditionNonCoded.setValue(value);
	}

	public Field getConditionNonCoded() {
		return this.conditionNonCoded;
	}

	public void setClinicalStatus(Field clinicalStatus) {
		this.clinicalStatus = clinicalStatus;
	}

	public void setClinicalStatusValue(String value) {
		this.clinicalStatus.setValue(value);
	}

	public Field getClinicalStatus() {
		return this.clinicalStatus;
	}

	public void setOnsetDate(Field onsetDate) {
		this.onsetDate = onsetDate;
	}

	public void setOnsetDateValue(java.util.Date value) {
		this.onsetDate.setValue(value);
	}

	public Field getOnsetDate() {
		return this.onsetDate;
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

	public void setVoidReason(Field voidReason) {
		this.voidReason = voidReason;
	}

	public void setVoidReasonValue(String value) {
		this.voidReason.setValue(value);
	}

	public Field getVoidReason() {
		return this.voidReason;
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

	public void setVoidedBy(Field voidedBy) {
		this.voidedBy = voidedBy;
	}

	public void setVoidedByValue(Integer value) {
		this.voidedBy.setValue(value);
	}

	public Field getVoidedBy() {
		return this.voidedBy;
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

	public void setPatientId(Field patientId) {
		this.patientId = patientId;
	}

	public void setPatientIdValue(Integer value) {
		this.patientId.setValue(value);
	}

	public Field getPatientId() {
		return this.patientId;
	}

	public void setEncounterId(Field encounterId) {
		this.encounterId = encounterId;
	}

	public void setEncounterIdValue(Integer value) {
		this.encounterId.setValue(value);
	}

	public Field getEncounterId() {
		return this.encounterId;
	}

	public void setFormNamespaceAndPath(Field formNamespaceAndPath) {
		this.formNamespaceAndPath = formNamespaceAndPath;
	}

	public void setFormNamespaceAndPathValue(String value) {
		this.formNamespaceAndPath.setValue(value);
	}

	public Field getFormNamespaceAndPath() {
		return this.formNamespaceAndPath;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String conditionIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"condition_id", "_");

		this.conditionId.setValue(BaseVO.retrieveFieldValue(conditionIdAttName, "INT", rs));

		String conditionCodedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"condition_coded", "_");

		this.conditionCoded.setValue(BaseVO.retrieveFieldValue(conditionCodedAttName, "INT", rs));

		String conditionNonCodedAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "condition_non_coded", "_");

		this.conditionNonCoded.setValue(BaseVO.retrieveFieldValue(conditionNonCodedAttName, "VARCHAR", rs));

		String clinicalStatusAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"clinical_status", "_");

		this.clinicalStatus.setValue(BaseVO.retrieveFieldValue(clinicalStatusAttName, "VARCHAR", rs));

		String onsetDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"onset_date", "_");

		this.onsetDate.setValue(BaseVO.retrieveFieldValue(onsetDateAttName, "DATETIME", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided",
				"_");

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "BIT", rs));

		String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_voided", "_");

		this.dateVoided = (java.util.Date) BaseVO.retrieveFieldValue(dateVoidedAttName, "DATETIME", rs);

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String encounterIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"encounter_id", "_");

		this.encounterId.setValue(BaseVO.retrieveFieldValue(encounterIdAttName, "INT", rs));

		String formNamespaceAndPathAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "form_namespace_and_path", "_");

		this.formNamespaceAndPath.setValue(BaseVO.retrieveFieldValue(formNamespaceAndPathAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO conditions(`condition_coded`, `condition_non_coded`, `clinical_status`, `onset_date`, `date_created`, `voided`, `date_voided`, `void_reason`, `uuid`, `creator`, `voided_by`, `changed_by`, `patient_id`, `date_changed`, `encounter_id`, `form_namespace_and_path`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO conditions(`condition_id`, `condition_coded`, `condition_non_coded`, `clinical_status`, `onset_date`, `date_created`, `voided`, `date_voided`, `void_reason`, `uuid`, `creator`, `voided_by`, `changed_by`, `patient_id`, `date_changed`, `encounter_id`, `form_namespace_and_path`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.conditionCoded.getValue(), this.conditionNonCoded.getValue(),
				this.clinicalStatus.getValue(), this.onsetDate.getValue(), this.dateCreated, this.voided.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid, this.creator.getValue(),
				this.voidedBy.getValue(), this.changedBy.getValue(), this.patientId.getValue(), this.dateChanged,
				this.encounterId.getValue(), this.formNamespaceAndPath.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.conditionId.getValue(), this.conditionCoded.getValue(),
				this.conditionNonCoded.getValue(), this.clinicalStatus.getValue(), this.onsetDate.getValue(),
				this.dateCreated, this.voided.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid,
				this.creator.getValue(), this.voidedBy.getValue(), this.changedBy.getValue(), this.patientId.getValue(),
				this.dateChanged, this.encounterId.getValue(), this.formNamespaceAndPath.getValue() };
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
		Object[] params = { this.conditionId.getValue(), this.conditionCoded.getValue(),
				this.conditionNonCoded.getValue(), this.clinicalStatus.getValue(), this.onsetDate.getValue(),
				this.dateCreated, this.voided.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid,
				this.creator.getValue(), this.voidedBy.getValue(), this.changedBy.getValue(), this.patientId.getValue(),
				this.dateChanged, this.encounterId.getValue(), this.formNamespaceAndPath.getValue(),
				this.conditionId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE conditions SET `condition_id` = ?, `condition_coded` = ?, `condition_non_coded` = ?, `clinical_status` = ?, `onset_date` = ?, `date_created` = ?, `voided` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ?, `creator` = ?, `voided_by` = ?, `changed_by` = ?, `patient_id` = ?, `date_changed` = ?, `encounter_id` = ?, `form_namespace_and_path` = ? WHERE condition_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.conditionCoded.getValue()) + ","
				+ (this.conditionNonCoded.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.conditionNonCoded.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.clinicalStatus.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.clinicalStatus.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.onsetDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.onsetDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.creator.getValue()) + "," + (this.voidedBy.getValue()) + "," + (this.changedBy.getValue())
				+ "," + (this.patientId.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.encounterId.getValue()) + ","
				+ (this.formNamespaceAndPath.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formNamespaceAndPath.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.conditionId.getValue()) + "," + (this.conditionCoded.getValue()) + ","
				+ (this.conditionNonCoded.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.conditionNonCoded.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.clinicalStatus.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.clinicalStatus.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.onsetDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.onsetDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.creator.getValue()) + "," + (this.voidedBy.getValue()) + "," + (this.changedBy.getValue())
				+ "," + (this.patientId.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.encounterId.getValue()) + ","
				+ (this.formNamespaceAndPath.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formNamespaceAndPath.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		ConditionsVO copy = new ConditionsVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.conditionCoded.getValue() != null)
			return true;

		if (this.encounterId.getValue() != null)
			return true;

		if (this.patientId.getValue() != null)
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
		if (parentAttName.equals("conditionCoded"))
			return this.conditionCoded.getValue();
		if (parentAttName.equals("encounterId"))
			return this.encounterId.getValue();
		if (parentAttName.equals("patientId"))
			return this.patientId.getValue();
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
		return "conditions";
	}

}