package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.conf.Key;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AppointmentVO extends AbstractGeneratedDatabaseObject {
	private Field referralFormNumber = Field.fastCreateWithType("referral_form_number", "VARCHAR");
	private Field typeOfAdmission = Field.fastCreateWithType("type_of_admission", "INT");
	private Field createdAt = Field.fastCreateWithType("created_at", "TIMESTAMP");
	private Field updatedAt = Field.fastCreateWithType("updated_at", "TIMESTAMP");
	private Field appointmentId = Field.fastCreateWithType("appointment_id", "INT UNSIGNED");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field providerId = Field.fastCreateWithType("provider_id", "INT");
	private Field reasonForAdmission = Field.fastCreateWithType("reason_for_admission", "INT");
	private Field serviceId = Field.fastCreateWithType("service_id", "BIGINT UNSIGNED");
	private Field specialityId = Field.fastCreateWithType("speciality_id", "INT");
	private Field status = Field.fastCreateWithType("status", "VARCHAR");
	private Field notes = Field.fastCreateWithType("notes", "TEXT");
	private Field appointmentDate = Field.fastCreateWithType("appointment_date", "DATETIME");

	public AppointmentVO() {
		this.metadata = false;
		this.fields.add(this.appointmentId);
		this.fields.add(this.patientId);
		this.fields.add(this.providerId);
		this.fields.add(this.reasonForAdmission);
		this.fields.add(this.referralFormNumber);
		this.fields.add(this.serviceId);
		this.fields.add(this.specialityId);
		this.fields.add(this.typeOfAdmission);
		this.fields.add(this.status);
		this.fields.add(this.notes);
		this.fields.add(this.createdAt);
		this.fields.add(this.updatedAt);
		this.fields.add(this.appointmentDate);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "appointment_id")) {
			this.appointmentId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "appointment_id")) {
			return this.appointmentId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "provider_id")) {
			return this.providerId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "reason_for_admission")) {
			return this.reasonForAdmission.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "referral_form_number")) {
			return this.referralFormNumber.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "service_id")) {
			return this.serviceId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "speciality_id")) {
			return this.specialityId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "type_of_admission")) {
			return this.typeOfAdmission.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "status")) {
			return this.status.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "notes")) {
			return this.notes.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "created_at")) {
			return this.createdAt.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "updated_at")) {
			return this.updatedAt.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "appointment_date")) {
			return this.appointmentDate.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "appointment_id")) {
			this.appointmentId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			this.patientId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "provider_id")) {
			this.providerId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "reason_for_admission")) {
			this.reasonForAdmission.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "referral_form_number")) {
			this.referralFormNumber.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "service_id")) {
			this.serviceId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "speciality_id")) {
			this.specialityId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "type_of_admission")) {
			this.typeOfAdmission.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "status")) {
			this.status.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "notes")) {
			this.notes.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "created_at")) {
			this.createdAt.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "updated_at")) {
			this.updatedAt.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "appointment_date")) {
			this.appointmentDate.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.appointmentId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.patientId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.providerId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.reasonForAdmission, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.referralFormNumber, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.serviceId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.specialityId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.typeOfAdmission, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.status, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.notes, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.createdAt, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.updatedAt, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.appointmentDate, srcConn, dstConn);
	}

	public void setReferralFormNumber(Field referralFormNumber) {
		this.referralFormNumber = referralFormNumber;
	}

	public void setReferralFormNumberValue(String value) {
		this.referralFormNumber.setValue(value);
	}

	public Field getReferralFormNumber() {
		return this.referralFormNumber;
	}

	public void setTypeOfAdmission(Field typeOfAdmission) {
		this.typeOfAdmission = typeOfAdmission;
	}

	public void setTypeOfAdmissionValue(Integer value) {
		this.typeOfAdmission.setValue(value);
	}

	public Field getTypeOfAdmission() {
		return this.typeOfAdmission;
	}

	public void setCreatedAt(Field createdAt) {
		this.createdAt = createdAt;
	}

	public void setCreatedAtValue(java.util.Date value) {
		this.createdAt.setValue(value);
	}

	public Field getCreatedAt() {
		return this.createdAt;
	}

	public void setUpdatedAt(Field updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setUpdatedAtValue(java.util.Date value) {
		this.updatedAt.setValue(value);
	}

	public Field getUpdatedAt() {
		return this.updatedAt;
	}

	public void setAppointmentId(Field appointmentId) {
		this.appointmentId = appointmentId;
	}

	public void setAppointmentIdValue(Integer value) {
		this.appointmentId.setValue(value);
	}

	public Field getAppointmentId() {
		return this.appointmentId;
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

	public void setProviderId(Field providerId) {
		this.providerId = providerId;
	}

	public void setProviderIdValue(Integer value) {
		this.providerId.setValue(value);
	}

	public Field getProviderId() {
		return this.providerId;
	}

	public void setReasonForAdmission(Field reasonForAdmission) {
		this.reasonForAdmission = reasonForAdmission;
	}

	public void setReasonForAdmissionValue(Integer value) {
		this.reasonForAdmission.setValue(value);
	}

	public Field getReasonForAdmission() {
		return this.reasonForAdmission;
	}

	public void setServiceId(Field serviceId) {
		this.serviceId = serviceId;
	}

	public void setServiceIdValue(Long value) {
		this.serviceId.setValue(value);
	}

	public Field getServiceId() {
		return this.serviceId;
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

	public void setStatus(Field status) {
		this.status = status;
	}

	public void setStatusValue(String value) {
		this.status.setValue(value);
	}

	public Field getStatus() {
		return this.status;
	}

	public void setNotes(Field notes) {
		this.notes = notes;
	}

	public void setNotesValue(String value) {
		this.notes.setValue(value);
	}

	public Field getNotes() {
		return this.notes;
	}

	public void setAppointmentDate(Field appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public void setAppointmentDateValue(java.util.Date value) {
		this.appointmentDate.setValue(value);
	}

	public Field getAppointmentDate() {
		return this.appointmentDate;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		if (getRelatedConfiguration().containsField("referral_form_number")) {
			String referralFormNumberAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "referral_form_number", "_");

			this.referralFormNumber.setValue(BaseVO.retrieveFieldValue(referralFormNumberAttName, "VARCHAR", rs));
		}

		if (getRelatedConfiguration().containsField("type_of_admission")) {
			String typeOfAdmissionAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "type_of_admission", "_");

			this.typeOfAdmission.setValue(BaseVO.retrieveFieldValue(typeOfAdmissionAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("created_at")) {
			String createdAtAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"created_at", "_");

			this.createdAt.setValue(BaseVO.retrieveFieldValue(createdAtAttName, "TIMESTAMP", rs));
		}

		if (getRelatedConfiguration().containsField("updated_at")) {
			String updatedAtAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"updated_at", "_");

			this.updatedAt.setValue(BaseVO.retrieveFieldValue(updatedAtAttName, "TIMESTAMP", rs));
		}

		String appointmentIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"appointment_id", "_");

		this.appointmentId.setValue(BaseVO.retrieveFieldValue(appointmentIdAttName, "INT UNSIGNED", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String providerIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"provider_id", "_");

		this.providerId.setValue(BaseVO.retrieveFieldValue(providerIdAttName, "INT", rs));

		String reasonForAdmissionAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "reason_for_admission", "_");

		this.reasonForAdmission.setValue(BaseVO.retrieveFieldValue(reasonForAdmissionAttName, "INT", rs));

		String serviceIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"service_id", "_");

		this.serviceId.setValue(BaseVO.retrieveFieldValue(serviceIdAttName, "BIGINT UNSIGNED", rs));

		String specialityIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"speciality_id", "_");

		this.specialityId.setValue(BaseVO.retrieveFieldValue(specialityIdAttName, "INT", rs));

		String statusAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "status",
				"_");

		this.status.setValue(BaseVO.retrieveFieldValue(statusAttName, "VARCHAR", rs));

		String notesAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "notes",
				"_");

		this.notes.setValue(BaseVO.retrieveFieldValue(notesAttName, "TEXT", rs));

		String appointmentDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"appointment_date", "_");

		this.appointmentDate.setValue(BaseVO.retrieveFieldValue(appointmentDateAttName, "DATETIME", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO appointment(`patient_id`, `provider_id`, `reason_for_admission`, `service_id`, `speciality_id`, `status`, `notes`, `appointment_date`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO appointment(`appointment_id`, `patient_id`, `provider_id`, `reason_for_admission`, `service_id`, `speciality_id`, `status`, `notes`, `appointment_date`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.patientId.getValue(), this.providerId.getValue(), this.reasonForAdmission.getValue(),
				this.serviceId.getValue(), this.specialityId.getValue(), this.status.getValue(), this.notes.getValue(),
				this.appointmentDate.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.appointmentId.getValue(), this.patientId.getValue(), this.providerId.getValue(),
				this.reasonForAdmission.getValue(), this.serviceId.getValue(), this.specialityId.getValue(),
				this.status.getValue(), this.notes.getValue(), this.appointmentDate.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.appointmentId.getValue(), this.patientId.getValue(), this.providerId.getValue(),
				this.reasonForAdmission.getValue(), this.serviceId.getValue(), this.specialityId.getValue(),
				this.status.getValue(), this.notes.getValue(), this.appointmentDate.getValue(),
				this.appointmentId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE appointment SET `appointment_id` = ?, `patient_id` = ?, `provider_id` = ?, `reason_for_admission` = ?, `service_id` = ?, `speciality_id` = ?, `status` = ?, `notes` = ?, `appointment_date` = ? WHERE appointment_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.patientId.getValue()) + "," + (this.providerId.getValue()) + ","
				+ (this.reasonForAdmission.getValue()) + "," + (this.serviceId.getValue()) + ","
				+ (this.specialityId.getValue()) + ","
				+ (this.status.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.status.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.notes.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.notes.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.appointmentDate.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.appointmentDate.getValue()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.appointmentId.getValue()) + "," + (this.patientId.getValue()) + ","
				+ (this.providerId.getValue()) + "," + (this.reasonForAdmission.getValue()) + ","
				+ (this.serviceId.getValue()) + "," + (this.specialityId.getValue()) + ","
				+ (this.status.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.status.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.notes.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.notes.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.appointmentDate.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.appointmentDate.getValue()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		AppointmentVO copy = new AppointmentVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.serviceId.getValue() != null)
			return true;

		if (this.reasonForAdmission.getValue() != null)
			return true;

		if (this.specialityId.getValue() != null)
			return true;

		if (this.patientId.getValue() != null)
			return true;

		if (this.providerId.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("serviceId"))
			return this.serviceId.getValue();
		if (parentAttName.equals("reasonForAdmission"))
			return this.reasonForAdmission.getValue();
		if (parentAttName.equals("specialityId"))
			return this.specialityId.getValue();
		if (parentAttName.equals("patientId"))
			return this.patientId.getValue();
		if (parentAttName.equals("providerId"))
			return this.providerId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "appointment";
	}

}