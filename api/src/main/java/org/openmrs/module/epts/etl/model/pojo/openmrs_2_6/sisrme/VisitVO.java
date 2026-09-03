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

public class VisitVO extends AbstractGeneratedDatabaseObject {
	private Field visitId = Field.fastCreateWithType("visit_id", "INT");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field visitTypeId = Field.fastCreateWithType("visit_type_id", "INT");
	private Field dateStarted = Field.fastCreateWithType("date_started", "DATETIME");
	private Field dateStopped = Field.fastCreateWithType("date_stopped", "DATETIME");
	private Field indicationConceptId = Field.fastCreateWithType("indication_concept_id", "INT");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");

	public VisitVO() {
		this.metadata = false;

		this.fields.add(this.visitId);
		this.fields.add(this.patientId);
		this.fields.add(this.visitTypeId);
		this.fields.add(this.dateStarted);
		this.fields.add(this.dateStopped);
		this.fields.add(this.indicationConceptId);
		this.fields.add(this.locationId);
		this.fields.add(this.creator);
		this.fields.add(this.changedBy);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "visit_id")) {
			this.visitId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "visit_id")) {
			return this.visitId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "visit_type_id")) {
			return this.visitTypeId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_started")) {
			return this.dateStarted.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_stopped")) {
			return this.dateStopped.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "indication_concept_id")) {
			return this.indicationConceptId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			return this.locationId.getValue();
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

	public void setVisitId(Field visitId) {
		this.visitId = visitId;
	}

	public void setVisitIdValue(Integer value) {
		this.visitId.setValue(value);
	}

	public Field getVisitId() {
		return this.visitId;
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

	public void setVisitTypeId(Field visitTypeId) {
		this.visitTypeId = visitTypeId;
	}

	public void setVisitTypeIdValue(Integer value) {
		this.visitTypeId.setValue(value);
	}

	public Field getVisitTypeId() {
		return this.visitTypeId;
	}

	public void setDateStarted(Field dateStarted) {
		this.dateStarted = dateStarted;
	}

	public void setDateStartedValue(java.util.Date value) {
		this.dateStarted.setValue(value);
	}

	public Field getDateStarted() {
		return this.dateStarted;
	}

	public void setDateStopped(Field dateStopped) {
		this.dateStopped = dateStopped;
	}

	public void setDateStoppedValue(java.util.Date value) {
		this.dateStopped.setValue(value);
	}

	public Field getDateStopped() {
		return this.dateStopped;
	}

	public void setIndicationConceptId(Field indicationConceptId) {
		this.indicationConceptId = indicationConceptId;
	}

	public void setIndicationConceptIdValue(Integer value) {
		this.indicationConceptId.setValue(value);
	}

	public Field getIndicationConceptId() {
		return this.indicationConceptId;
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

		String visitIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"visit_id", "_");

		this.visitId.setValue(BaseVO.retrieveFieldValue(visitIdAttName, "INT", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String visitTypeIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"visit_type_id", "_");

		this.visitTypeId.setValue(BaseVO.retrieveFieldValue(visitTypeIdAttName, "INT", rs));

		String dateStartedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_started", "_");

		this.dateStarted.setValue(BaseVO.retrieveFieldValue(dateStartedAttName, "DATETIME", rs));

		String dateStoppedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_stopped", "_");

		this.dateStopped.setValue(BaseVO.retrieveFieldValue(dateStoppedAttName, "DATETIME", rs));

		String indicationConceptIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "indication_concept_id", "_");

		this.indicationConceptId.setValue(BaseVO.retrieveFieldValue(indicationConceptIdAttName, "INT", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

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

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO visit(`patient_id`, `visit_type_id`, `date_started`, `date_stopped`, `indication_concept_id`, `location_id`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO visit(`visit_id`, `patient_id`, `visit_type_id`, `date_started`, `date_stopped`, `indication_concept_id`, `location_id`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.patientId.getValue(), this.visitTypeId.getValue(), this.dateStarted.getValue(),
				this.dateStopped.getValue(), this.indicationConceptId.getValue(), this.locationId.getValue(),
				this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.visitId.getValue(), this.patientId.getValue(), this.visitTypeId.getValue(),
				this.dateStarted.getValue(), this.dateStopped.getValue(), this.indicationConceptId.getValue(),
				this.locationId.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided,
				this.voidReason.getValue(), this.uuid };
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
		Object[] params = { this.visitId.getValue(), this.patientId.getValue(), this.visitTypeId.getValue(),
				this.dateStarted.getValue(), this.dateStopped.getValue(), this.indicationConceptId.getValue(),
				this.locationId.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided,
				this.voidReason.getValue(), this.uuid, this.visitId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE visit SET `visit_id` = ?, `patient_id` = ?, `visit_type_id` = ?, `date_started` = ?, `date_stopped` = ?, `indication_concept_id` = ?, `location_id` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ? WHERE visit_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.patientId.getValue()) + "," + (this.visitTypeId.getValue()) + ","
				+ (this.dateStarted.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateStarted.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dateStopped.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateStopped.getValue())
						+ "\"" : null)
				+ "," + (this.indicationConceptId.getValue()) + "," + (this.locationId.getValue()) + ","
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
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.visitId.getValue()) + "," + (this.patientId.getValue()) + "," + (this.visitTypeId.getValue())
				+ ","
				+ (this.dateStarted.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateStarted.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dateStopped.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateStopped.getValue())
						+ "\"" : null)
				+ "," + (this.indicationConceptId.getValue()) + "," + (this.locationId.getValue()) + ","
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
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		VisitVO copy = new VisitVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.indicationConceptId.getValue() != null)
			return true;

		if (this.locationId.getValue() != null)
			return true;

		if (this.patientId.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		if (this.visitTypeId.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("indicationConceptId"))
			return this.indicationConceptId.getValue();
		if (parentAttName.equals("locationId"))
			return this.locationId.getValue();
		if (parentAttName.equals("patientId"))
			return this.patientId.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();
		if (parentAttName.equals("visitTypeId"))
			return this.visitTypeId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "visit";
	}

}