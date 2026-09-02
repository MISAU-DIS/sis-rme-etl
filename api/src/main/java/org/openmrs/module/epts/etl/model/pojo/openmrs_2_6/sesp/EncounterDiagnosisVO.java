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

public class EncounterDiagnosisVO extends AbstractGeneratedDatabaseObject {
	private Field diagnosisId = Field.fastCreateWithType("diagnosis_id", "INT");
	private Field diagnosisCoded = Field.fastCreateWithType("diagnosis_coded", "INT");
	private Field diagnosisNonCoded = Field.fastCreateWithType("diagnosis_non_coded", "VARCHAR");
	private Field encounterId = Field.fastCreateWithType("encounter_id", "INT");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field conditionId = Field.fastCreateWithType("condition_id", "INT");
	private Field certainty = Field.fastCreateWithType("certainty", "VARCHAR");
	private Field rank = Field.fastCreateWithType("rank", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");

	public EncounterDiagnosisVO() {
		this.metadata = false;

		this.fields.add(this.diagnosisId);
		this.fields.add(this.diagnosisCoded);
		this.fields.add(this.diagnosisNonCoded);
		this.fields.add(this.encounterId);
		this.fields.add(this.patientId);
		this.fields.add(this.conditionId);
		this.fields.add(this.certainty);
		this.fields.add(this.rank);
		this.fields.add(this.creator);
		this.fields.add(this.changedBy);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "diagnosis_id")) {
			this.diagnosisId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "diagnosis_id")) {
			return this.diagnosisId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "diagnosis_coded")) {
			return this.diagnosisCoded.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "diagnosis_non_coded")) {
			return this.diagnosisNonCoded.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "encounter_id")) {
			return this.encounterId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "condition_id")) {
			return this.conditionId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "certainty")) {
			return this.certainty.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "rank")) {
			return this.rank.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
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

	public void setDiagnosisId(Field diagnosisId) {
		this.diagnosisId = diagnosisId;
	}

	public void setDiagnosisIdValue(Integer value) {
		this.diagnosisId.setValue(value);
	}

	public Field getDiagnosisId() {
		return this.diagnosisId;
	}

	public void setDiagnosisCoded(Field diagnosisCoded) {
		this.diagnosisCoded = diagnosisCoded;
	}

	public void setDiagnosisCodedValue(Integer value) {
		this.diagnosisCoded.setValue(value);
	}

	public Field getDiagnosisCoded() {
		return this.diagnosisCoded;
	}

	public void setDiagnosisNonCoded(Field diagnosisNonCoded) {
		this.diagnosisNonCoded = diagnosisNonCoded;
	}

	public void setDiagnosisNonCodedValue(String value) {
		this.diagnosisNonCoded.setValue(value);
	}

	public Field getDiagnosisNonCoded() {
		return this.diagnosisNonCoded;
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

	public void setPatientId(Field patientId) {
		this.patientId = patientId;
	}

	public void setPatientIdValue(Integer value) {
		this.patientId.setValue(value);
	}

	public Field getPatientId() {
		return this.patientId;
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

	public void setCertainty(Field certainty) {
		this.certainty = certainty;
	}

	public void setCertaintyValue(String value) {
		this.certainty.setValue(value);
	}

	public Field getCertainty() {
		return this.certainty;
	}

	public void setRank(Field rank) {
		this.rank = rank;
	}

	public void setRankValue(Integer value) {
		this.rank.setValue(value);
	}

	public Field getRank() {
		return this.rank;
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

		String diagnosisIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"diagnosis_id", "_");

		this.diagnosisId.setValue(BaseVO.retrieveFieldValue(diagnosisIdAttName, "INT", rs));

		String diagnosisCodedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"diagnosis_coded", "_");

		this.diagnosisCoded.setValue(BaseVO.retrieveFieldValue(diagnosisCodedAttName, "INT", rs));

		String diagnosisNonCodedAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "diagnosis_non_coded", "_");

		this.diagnosisNonCoded.setValue(BaseVO.retrieveFieldValue(diagnosisNonCodedAttName, "VARCHAR", rs));

		String encounterIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"encounter_id", "_");

		this.encounterId.setValue(BaseVO.retrieveFieldValue(encounterIdAttName, "INT", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String conditionIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"condition_id", "_");

		this.conditionId.setValue(BaseVO.retrieveFieldValue(conditionIdAttName, "INT", rs));

		String certaintyAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"certainty", "_");

		this.certainty.setValue(BaseVO.retrieveFieldValue(certaintyAttName, "VARCHAR", rs));

		String rankAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "rank",
				"_");

		this.rank.setValue(BaseVO.retrieveFieldValue(rankAttName, "INT", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));

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
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO encounter_diagnosis(`diagnosis_coded`, `diagnosis_non_coded`, `encounter_id`, `patient_id`, `condition_id`, `certainty`, `rank`, `uuid`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO encounter_diagnosis(`diagnosis_id`, `diagnosis_coded`, `diagnosis_non_coded`, `encounter_id`, `patient_id`, `condition_id`, `certainty`, `rank`, `uuid`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.diagnosisCoded.getValue(), this.diagnosisNonCoded.getValue(),
				this.encounterId.getValue(), this.patientId.getValue(), this.conditionId.getValue(),
				this.certainty.getValue(), this.rank.getValue(), this.uuid, this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.diagnosisId.getValue(), this.diagnosisCoded.getValue(),
				this.diagnosisNonCoded.getValue(), this.encounterId.getValue(), this.patientId.getValue(),
				this.conditionId.getValue(), this.certainty.getValue(), this.rank.getValue(), this.uuid,
				this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue() };
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
		Object[] params = { this.diagnosisId.getValue(), this.diagnosisCoded.getValue(),
				this.diagnosisNonCoded.getValue(), this.encounterId.getValue(), this.patientId.getValue(),
				this.conditionId.getValue(), this.certainty.getValue(), this.rank.getValue(), this.uuid,
				this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.diagnosisId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE encounter_diagnosis SET `diagnosis_id` = ?, `diagnosis_coded` = ?, `diagnosis_non_coded` = ?, `encounter_id` = ?, `patient_id` = ?, `condition_id` = ?, `certainty` = ?, `rank` = ?, `uuid` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ? WHERE diagnosis_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.diagnosisCoded.getValue()) + ","
				+ (this.diagnosisNonCoded.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.diagnosisNonCoded.getValue().toString()) + "\""
						: null)
				+ "," + (this.encounterId.getValue()) + "," + (this.patientId.getValue()) + ","
				+ (this.conditionId.getValue()) + ","
				+ (this.certainty.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.certainty.getValue().toString()) + "\""
						: null)
				+ "," + (this.rank.getValue()) + ","
				+ (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null) + ","
				+ (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.diagnosisId.getValue()) + "," + (this.diagnosisCoded.getValue()) + ","
				+ (this.diagnosisNonCoded.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.diagnosisNonCoded.getValue().toString()) + "\""
						: null)
				+ "," + (this.encounterId.getValue()) + "," + (this.patientId.getValue()) + ","
				+ (this.conditionId.getValue()) + ","
				+ (this.certainty.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.certainty.getValue().toString()) + "\""
						: null)
				+ "," + (this.rank.getValue()) + ","
				+ (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null) + ","
				+ (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		EncounterDiagnosisVO copy = new EncounterDiagnosisVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.diagnosisCoded.getValue() != null)
			return true;

		if (this.conditionId.getValue() != null)
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
		if (parentAttName.equals("diagnosisCoded"))
			return this.diagnosisCoded.getValue();
		if (parentAttName.equals("conditionId"))
			return this.conditionId.getValue();
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
		return "encounter_diagnosis";
	}

}