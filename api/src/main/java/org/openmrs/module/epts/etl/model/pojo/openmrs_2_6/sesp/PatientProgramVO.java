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

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PatientProgramVO extends AbstractGeneratedDatabaseObject {
	private Field patientProgramId = Field.fastCreateWithType("patient_program_id", "INT");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field programId = Field.fastCreateWithType("program_id", "INT");
	private Field dateEnrolled = Field.fastCreateWithType("date_enrolled", "DATETIME");
	private Field dateCompleted = Field.fastCreateWithType("date_completed", "DATETIME");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field outcomeConceptId = Field.fastCreateWithType("outcome_concept_id", "INT");
	private Field idart = Field.fastCreateWithType("idart", "VARCHAR");

	public PatientProgramVO() {
		this.metadata = false;
		this.fields.add(this.patientProgramId);
		this.fields.add(this.patientId);
		this.fields.add(this.programId);
		this.fields.add(this.dateEnrolled);
		this.fields.add(this.dateCompleted);
		this.fields.add(this.creator);
		this.fields.add(this.changedBy);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
		this.fields.add(this.locationId);
		this.fields.add(this.outcomeConceptId);
		this.fields.add(this.idart);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "patient_program_id")) {
			this.patientProgramId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "patient_program_id")) {
			return this.patientProgramId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "program_id")) {
			return this.programId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_enrolled")) {
			return this.dateEnrolled.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_completed")) {
			return this.dateCompleted.getValue();
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
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			return this.locationId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "outcome_concept_id")) {
			return this.outcomeConceptId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "idart")) {
			return this.idart.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "patient_program_id")) {
			this.patientProgramId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			this.patientId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "program_id")) {
			this.programId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "date_enrolled")) {
			this.dateEnrolled.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "date_completed")) {
			this.dateCompleted.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			this.creator.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			this.changedBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			this.locationId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "outcome_concept_id")) {
			this.outcomeConceptId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "idart")) {
			this.idart.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.patientProgramId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.patientId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.programId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dateEnrolled, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dateCompleted, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.changedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voided, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidReason, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.locationId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.outcomeConceptId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.idart, srcConn, dstConn);
	}

	public void setPatientProgramId(Field patientProgramId) {
		this.patientProgramId = patientProgramId;
	}

	public void setPatientProgramIdValue(Integer value) {
		this.patientProgramId.setValue(value);
	}

	public Field getPatientProgramId() {
		return this.patientProgramId;
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

	public void setProgramId(Field programId) {
		this.programId = programId;
	}

	public void setProgramIdValue(Integer value) {
		this.programId.setValue(value);
	}

	public Field getProgramId() {
		return this.programId;
	}

	public void setDateEnrolled(Field dateEnrolled) {
		this.dateEnrolled = dateEnrolled;
	}

	public void setDateEnrolledValue(java.util.Date value) {
		this.dateEnrolled.setValue(value);
	}

	public Field getDateEnrolled() {
		return this.dateEnrolled;
	}

	public void setDateCompleted(Field dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	public void setDateCompletedValue(java.util.Date value) {
		this.dateCompleted.setValue(value);
	}

	public Field getDateCompleted() {
		return this.dateCompleted;
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

	public void setLocationId(Field locationId) {
		this.locationId = locationId;
	}

	public void setLocationIdValue(Integer value) {
		this.locationId.setValue(value);
	}

	public Field getLocationId() {
		return this.locationId;
	}

	public void setOutcomeConceptId(Field outcomeConceptId) {
		this.outcomeConceptId = outcomeConceptId;
	}

	public void setOutcomeConceptIdValue(Integer value) {
		this.outcomeConceptId.setValue(value);
	}

	public Field getOutcomeConceptId() {
		return this.outcomeConceptId;
	}

	public void setIdart(Field idart) {
		this.idart = idart;
	}

	public void setIdartValue(String value) {
		this.idart.setValue(value);
	}

	public Field getIdart() {
		return this.idart;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String patientProgramIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_program_id", "_");

		this.patientProgramId.setValue(BaseVO.retrieveFieldValue(patientProgramIdAttName, "INT", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String programIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"program_id", "_");

		this.programId.setValue(BaseVO.retrieveFieldValue(programIdAttName, "INT", rs));

		String dateEnrolledAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_enrolled", "_");

		this.dateEnrolled.setValue(BaseVO.retrieveFieldValue(dateEnrolledAttName, "DATETIME", rs));

		String dateCompletedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_completed", "_");

		this.dateCompleted.setValue(BaseVO.retrieveFieldValue(dateCompletedAttName, "DATETIME", rs));

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

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "CHAR", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

		String outcomeConceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"outcome_concept_id", "_");

		this.outcomeConceptId.setValue(BaseVO.retrieveFieldValue(outcomeConceptIdAttName, "INT", rs));

		String idartAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "idart",
				"_");

		this.idart.setValue(BaseVO.retrieveFieldValue(idartAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO patient_program(`patient_id`, `program_id`, `date_enrolled`, `date_completed`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `location_id`, `outcome_concept_id`, `idart`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO patient_program(`patient_program_id`, `patient_id`, `program_id`, `date_enrolled`, `date_completed`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `location_id`, `outcome_concept_id`, `idart`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.patientId.getValue(), this.programId.getValue(), this.dateEnrolled.getValue(),
				this.dateCompleted.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided,
				this.voidReason.getValue(), this.uuid, this.locationId.getValue(), this.outcomeConceptId.getValue(),
				this.idart.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.patientProgramId.getValue(), this.patientId.getValue(), this.programId.getValue(),
				this.dateEnrolled.getValue(), this.dateCompleted.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid, this.locationId.getValue(),
				this.outcomeConceptId.getValue(), this.idart.getValue() };
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
		Object[] params = { this.patientProgramId.getValue(), this.patientId.getValue(), this.programId.getValue(),
				this.dateEnrolled.getValue(), this.dateCompleted.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid, this.locationId.getValue(),
				this.outcomeConceptId.getValue(), this.idart.getValue(), this.patientProgramId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE patient_program SET `patient_program_id` = ?, `patient_id` = ?, `program_id` = ?, `date_enrolled` = ?, `date_completed` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ?, `location_id` = ?, `outcome_concept_id` = ?, `idart` = ? WHERE patient_program_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.patientId.getValue()) + "," + (this.programId.getValue()) + ","
				+ (this.dateEnrolled.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateEnrolled.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dateCompleted.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCompleted.getValue())
						+ "\"" : null)
				+ "," + (this.creator.getValue()) + ","
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
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.locationId.getValue()) + "," + (this.outcomeConceptId.getValue()) + ","
				+ (this.idart.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.idart.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.patientProgramId.getValue()) + "," + (this.patientId.getValue()) + ","
				+ (this.programId.getValue()) + ","
				+ (this.dateEnrolled.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateEnrolled.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dateCompleted.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCompleted.getValue())
						+ "\"" : null)
				+ "," + (this.creator.getValue()) + ","
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
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.locationId.getValue()) + "," + (this.outcomeConceptId.getValue()) + ","
				+ (this.idart.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.idart.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PatientProgramVO copy = new PatientProgramVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.outcomeConceptId.getValue() != null)
			return true;

		if (this.locationId.getValue() != null)
			return true;

		if (this.patientId.getValue() != null)
			return true;

		if (this.programId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("outcomeConceptId"))
			return this.outcomeConceptId.getValue();
		if (parentAttName.equals("locationId"))
			return this.locationId.getValue();
		if (parentAttName.equals("patientId"))
			return this.patientId.getValue();
		if (parentAttName.equals("programId"))
			return this.programId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "patient_program";
	}

}