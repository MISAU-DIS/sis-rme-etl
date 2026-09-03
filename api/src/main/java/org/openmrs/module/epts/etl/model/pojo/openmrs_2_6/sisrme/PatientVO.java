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


import com.fasterxml.jackson.annotation.JsonIgnore;

public class PatientVO extends AbstractGeneratedDatabaseObject {
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field allergyStatus = Field.fastCreateWithType("allergy_status", "VARCHAR");
	private Field ddeSyncPending = Field.fastCreateWithType("dde_sync_pending", "TINYINT");

	public PatientVO() {
		this.metadata = false;
		setSharedPkObj(new org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.PersonVO());

		this.fields.add(this.patientId);
		this.fields.add(this.creator);
		this.fields.add(this.changedBy);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
		this.fields.add(this.allergyStatus);
		this.fields.add(this.ddeSyncPending);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "patient_id")) {
			this.patientId.setValue(k.getValue());
		}
	}

	@JsonIgnore
	@Override
	public org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.PersonVO getSharedPkObj() {
		return (org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.PersonVO) super.getSharedPkObj();
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
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
		if (utilities.equalsFieldsName(fieldName, "allergy_status")) {
			return this.allergyStatus.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dde_sync_pending")) {
			return this.ddeSyncPending.getValue();
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

	public void setPatientId(Field patientId) {
		this.patientId = patientId;
	}

	public void setPatientIdValue(Integer value) {
		this.patientId.setValue(value);
	}

	public Field getPatientId() {
		return this.patientId;
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

	public void setAllergyStatus(Field allergyStatus) {
		this.allergyStatus = allergyStatus;
	}

	public void setAllergyStatusValue(String value) {
		this.allergyStatus.setValue(value);
	}

	public Field getAllergyStatus() {
		return this.allergyStatus;
	}

	public void setDdeSyncPending(Field ddeSyncPending) {
		this.ddeSyncPending = ddeSyncPending;
	}

	public void setDdeSyncPendingValue(Byte value) {
		this.ddeSyncPending.setValue(value);
	}

	public Field getDdeSyncPending() {
		return this.ddeSyncPending;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		if (!hasRelatedConfiguration())
			throw new org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException(
					"The relatedConfiguration is not set");
		if (!getSharedPkObj().isLoadedFromDb())
			getSharedPkObj().load(rs);
		super.load(rs);

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

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

		String allergyStatusAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"allergy_status", "_");

		this.allergyStatus.setValue(BaseVO.retrieveFieldValue(allergyStatusAttName, "VARCHAR", rs));

		String ddeSyncPendingAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"dde_sync_pending", "_");

		this.ddeSyncPending.setValue(BaseVO.retrieveFieldValue(ddeSyncPendingAttName, "TINYINT", rs));

		org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration tableConfiguration = (org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration) getRelatedConfiguration();
		if (!utilities.stringHasValue(getUuid()) && getSharedPkObj() != null
				&& utilities.stringHasValue(getSharedPkObj().getUuid())) {
			setUuid(getSharedPkObj().getUuid());
		}
		loadObjectIdData(tableConfiguration);
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO patient(`creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `allergy_status`, `dde_sync_pending`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO patient(`patient_id`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `allergy_status`, `dde_sync_pending`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.allergyStatus.getValue(), this.ddeSyncPending.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.patientId.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.allergyStatus.getValue(),
				this.ddeSyncPending.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.patientId.getValue(), this.creator.getValue(), this.dateCreated,
				this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.allergyStatus.getValue(),
				this.ddeSyncPending.getValue(), this.patientId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE patient SET `patient_id` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `allergy_status` = ?, `dde_sync_pending` = ? WHERE patient_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.creator.getValue()) + ","
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
				+ ","
				+ (this.allergyStatus.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.allergyStatus.getValue().toString()) + "\""
						: null)
				+ "," + (this.ddeSyncPending.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.patientId.getValue()) + "," + (this.creator.getValue()) + ","
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
				+ ","
				+ (this.allergyStatus.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.allergyStatus.getValue().toString()) + "\""
						: null)
				+ "," + (this.ddeSyncPending.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PatientVO copy = new PatientVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
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
		return "patient";
	}

}