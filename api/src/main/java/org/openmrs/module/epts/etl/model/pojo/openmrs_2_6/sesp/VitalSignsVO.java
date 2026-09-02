package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.model.base.BaseVO;
import org.openmrs.module.epts.etl.model.pojo.generic.AbstractGeneratedDatabaseObject;
import org.openmrs.module.epts.etl.utilities.AttDefinedElements;
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VitalSignsVO extends AbstractGeneratedDatabaseObject {
	private Field vitalSignsId = Field.fastCreateWithType("vital_signs_id", "BIGINT UNSIGNED");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field encounterId = Field.fastCreateWithType("encounter_id", "INT");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field temperature = Field.fastCreateWithType("temperature", "DECIMAL");
	private Field bloodPressureSystolic = Field.fastCreateWithType("blood_pressure_systolic", "INT");
	private Field bloodPressureDiastolic = Field.fastCreateWithType("blood_pressure_diastolic", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "TEXT");

	public VitalSignsVO() {
		this.metadata = false;

		this.fields.add(this.vitalSignsId);
		this.fields.add(this.patientId);
		this.fields.add(this.encounterId);
		this.fields.add(this.locationId);
		this.fields.add(this.temperature);
		this.fields.add(this.bloodPressureSystolic);
		this.fields.add(this.bloodPressureDiastolic);
		this.fields.add(this.creator);
		this.fields.add(this.changedBy);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "vital_signs_id")) {
			this.vitalSignsId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "vital_signs_id")) {
			return this.vitalSignsId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "encounter_id")) {
			return this.encounterId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			return this.locationId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "temperature")) {
			return this.temperature.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "blood_pressure_systolic")) {
			return this.bloodPressureSystolic.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "blood_pressure_diastolic")) {
			return this.bloodPressureDiastolic.getValue();
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

	public void setVitalSignsId(Field vitalSignsId) {
		this.vitalSignsId = vitalSignsId;
	}

	public void setVitalSignsIdValue(Long value) {
		this.vitalSignsId.setValue(value);
	}

	public Field getVitalSignsId() {
		return this.vitalSignsId;
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

	public void setLocationId(Field locationId) {
		this.locationId = locationId;
	}

	public void setLocationIdValue(Integer value) {
		this.locationId.setValue(value);
	}

	public Field getLocationId() {
		return this.locationId;
	}

	public void setTemperature(Field temperature) {
		this.temperature = temperature;
	}

	public void setTemperatureValue(Double value) {
		this.temperature.setValue(value);
	}

	public Field getTemperature() {
		return this.temperature;
	}

	public void setBloodPressureSystolic(Field bloodPressureSystolic) {
		this.bloodPressureSystolic = bloodPressureSystolic;
	}

	public void setBloodPressureSystolicValue(Integer value) {
		this.bloodPressureSystolic.setValue(value);
	}

	public Field getBloodPressureSystolic() {
		return this.bloodPressureSystolic;
	}

	public void setBloodPressureDiastolic(Field bloodPressureDiastolic) {
		this.bloodPressureDiastolic = bloodPressureDiastolic;
	}

	public void setBloodPressureDiastolicValue(Integer value) {
		this.bloodPressureDiastolic.setValue(value);
	}

	public Field getBloodPressureDiastolic() {
		return this.bloodPressureDiastolic;
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

		String vitalSignsIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"vital_signs_id", "_");

		this.vitalSignsId.setValue(BaseVO.retrieveFieldValue(vitalSignsIdAttName, "BIGINT UNSIGNED", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String encounterIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"encounter_id", "_");

		this.encounterId.setValue(BaseVO.retrieveFieldValue(encounterIdAttName, "INT", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

		String temperatureAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"temperature", "_");

		this.temperature.setValue(BaseVO.retrieveFieldValue(temperatureAttName, "DECIMAL", rs));

		String bloodPressureSystolicAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "blood_pressure_systolic", "_");

		this.bloodPressureSystolic.setValue(BaseVO.retrieveFieldValue(bloodPressureSystolicAttName, "INT", rs));

		String bloodPressureDiastolicAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "blood_pressure_diastolic", "_");

		this.bloodPressureDiastolic.setValue(BaseVO.retrieveFieldValue(bloodPressureDiastolicAttName, "INT", rs));

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

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "TEXT", rs));

		String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_voided", "_");

		this.dateVoided = (java.util.Date) BaseVO.retrieveFieldValue(dateVoidedAttName, "DATETIME", rs);

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO vital_signs(`patient_id`, `encounter_id`, `location_id`, `temperature`, `blood_pressure_systolic`, `blood_pressure_diastolic`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `void_reason`, `date_voided`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO vital_signs(`vital_signs_id`, `patient_id`, `encounter_id`, `location_id`, `temperature`, `blood_pressure_systolic`, `blood_pressure_diastolic`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `void_reason`, `date_voided`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.patientId.getValue(), this.encounterId.getValue(), this.locationId.getValue(),
				this.temperature.getValue(), this.bloodPressureSystolic.getValue(),
				this.bloodPressureDiastolic.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(),
				this.voidReason.getValue(), this.dateVoided, this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.vitalSignsId.getValue(), this.patientId.getValue(), this.encounterId.getValue(),
				this.locationId.getValue(), this.temperature.getValue(), this.bloodPressureSystolic.getValue(),
				this.bloodPressureDiastolic.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(),
				this.voidReason.getValue(), this.dateVoided, this.uuid };
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
		Object[] params = { this.vitalSignsId.getValue(), this.patientId.getValue(), this.encounterId.getValue(),
				this.locationId.getValue(), this.temperature.getValue(), this.bloodPressureSystolic.getValue(),
				this.bloodPressureDiastolic.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(),
				this.voidReason.getValue(), this.dateVoided, this.uuid, this.vitalSignsId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE vital_signs SET `vital_signs_id` = ?, `patient_id` = ?, `encounter_id` = ?, `location_id` = ?, `temperature` = ?, `blood_pressure_systolic` = ?, `blood_pressure_diastolic` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `voided` = ?, `voided_by` = ?, `void_reason` = ?, `date_voided` = ?, `uuid` = ? WHERE vital_signs_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.patientId.getValue()) + "," + (this.encounterId.getValue()) + ","
				+ (this.locationId.getValue()) + "," + (this.temperature.getValue()) + ","
				+ (this.bloodPressureSystolic.getValue()) + "," + (this.bloodPressureDiastolic.getValue()) + ","
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
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.vitalSignsId.getValue()) + "," + (this.patientId.getValue()) + ","
				+ (this.encounterId.getValue()) + "," + (this.locationId.getValue()) + ","
				+ (this.temperature.getValue()) + "," + (this.bloodPressureSystolic.getValue()) + ","
				+ (this.bloodPressureDiastolic.getValue()) + "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		VitalSignsVO copy = new VitalSignsVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.encounterId.getValue() != null)
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

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("encounterId"))
			return this.encounterId.getValue();
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

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "vital_signs";
	}

}