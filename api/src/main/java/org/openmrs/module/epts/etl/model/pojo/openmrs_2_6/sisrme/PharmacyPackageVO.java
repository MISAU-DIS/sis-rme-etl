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

import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PharmacyPackageVO extends AbstractGeneratedDatabaseObject {
	private Field pharmacyPackageId = Field.fastCreateWithType("pharmacy_package_id", "INT");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field prescriptionEncounterId = Field.fastCreateWithType("prescription_encounter_id", "INT");
	private Field packageDatetime = Field.fastCreateWithType("package_datetime", "DATETIME");
	private Field nextPickupDate = Field.fastCreateWithType("next_pickup_date", "DATETIME");
	private Field dispenseModeId = Field.fastCreateWithType("dispense_mode_id", "INT");
	private Field notes = Field.fastCreateWithType("notes", "TEXT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PharmacyPackageVO() {
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
	public EtlDatabaseObjectConfiguration getRelatedConfiguration() {
		return this.relatedConfiguration;
	}

	@JsonIgnore
	@Override
	public void setRelatedConfiguration(EtlDatabaseObjectConfiguration config) {
		this.relatedConfiguration = config;
		enrichGeneratedFields(config);
	}

	@JsonIgnore
	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) {
		utilities.throwForbiddenMethodException();
	}

	public void setPharmacyPackageId(Field pharmacyPackageId) {
		this.pharmacyPackageId = pharmacyPackageId;
	}

	public void setPharmacyPackageIdValue(Integer value) {
		this.pharmacyPackageId.setValue(value);
	}

	public Field getPharmacyPackageId() {
		return this.pharmacyPackageId;
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

	public void setPrescriptionEncounterId(Field prescriptionEncounterId) {
		this.prescriptionEncounterId = prescriptionEncounterId;
	}

	public void setPrescriptionEncounterIdValue(Integer value) {
		this.prescriptionEncounterId.setValue(value);
	}

	public Field getPrescriptionEncounterId() {
		return this.prescriptionEncounterId;
	}

	public void setPackageDatetime(Field packageDatetime) {
		this.packageDatetime = packageDatetime;
	}

	public void setPackageDatetimeValue(java.util.Date value) {
		this.packageDatetime.setValue(value);
	}

	public Field getPackageDatetime() {
		return this.packageDatetime;
	}

	public void setNextPickupDate(Field nextPickupDate) {
		this.nextPickupDate = nextPickupDate;
	}

	public void setNextPickupDateValue(java.util.Date value) {
		this.nextPickupDate.setValue(value);
	}

	public Field getNextPickupDate() {
		return this.nextPickupDate;
	}

	public void setDispenseModeId(Field dispenseModeId) {
		this.dispenseModeId = dispenseModeId;
	}

	public void setDispenseModeIdValue(Integer value) {
		this.dispenseModeId.setValue(value);
	}

	public Field getDispenseModeId() {
		return this.dispenseModeId;
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

	public void setCreator(Field creator) {
		this.creator = creator;
	}

	public void setCreatorValue(Integer value) {
		this.creator.setValue(value);
	}

	public Field getCreator() {
		return this.creator;
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

	public void setVoidedBy(Field voidedBy) {
		this.voidedBy = voidedBy;
	}

	public void setVoidedByValue(Integer value) {
		this.voidedBy.setValue(value);
	}

	public Field getVoidedBy() {
		return this.voidedBy;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String pharmacyPackageIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "pharmacy_package_id", "_");

		this.pharmacyPackageId.setValue(BaseVO.retrieveFieldValue(pharmacyPackageIdAttName, "INT", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

		String prescriptionEncounterIdAttName = utilities.concatStringsWithSeparator(
				this.getRelatedConfiguration().getAlias(), "prescription_encounter_id", "_");

		this.prescriptionEncounterId.setValue(BaseVO.retrieveFieldValue(prescriptionEncounterIdAttName, "INT", rs));

		String packageDatetimeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"package_datetime", "_");

		this.packageDatetime.setValue(BaseVO.retrieveFieldValue(packageDatetimeAttName, "DATETIME", rs));

		String nextPickupDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"next_pickup_date", "_");

		this.nextPickupDate.setValue(BaseVO.retrieveFieldValue(nextPickupDateAttName, "DATETIME", rs));

		String dispenseModeIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"dispense_mode_id", "_");

		this.dispenseModeId.setValue(BaseVO.retrieveFieldValue(dispenseModeIdAttName, "INT", rs));

		String notesAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "notes",
				"_");

		this.notes.setValue(BaseVO.retrieveFieldValue(notesAttName, "TEXT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = rs.getTimestamp(dateCreatedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateCreatedAttName).getTime())
				: null;

		String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided",
				"_");

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "BIT", rs));

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO pharmacy_package(`location_id`, `prescription_encounter_id`, `package_datetime`, `next_pickup_date`, `dispense_mode_id`, `notes`, `creator`, `date_created`, `voided`, `void_reason`, `voided_by`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO pharmacy_package(`pharmacy_package_id`, `location_id`, `prescription_encounter_id`, `package_datetime`, `next_pickup_date`, `dispense_mode_id`, `notes`, `creator`, `date_created`, `voided`, `void_reason`, `voided_by`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.locationId.getValue(), this.prescriptionEncounterId.getValue(),
				this.packageDatetime.getValue(), this.nextPickupDate.getValue(), this.dispenseModeId.getValue(),
				this.notes.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidReason.getValue(), this.voidedBy.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.pharmacyPackageId.getValue(), this.locationId.getValue(),
				this.prescriptionEncounterId.getValue(), this.packageDatetime.getValue(),
				this.nextPickupDate.getValue(), this.dispenseModeId.getValue(), this.notes.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidReason.getValue(),
				this.voidedBy.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.pharmacyPackageId.getValue(), this.locationId.getValue(),
				this.prescriptionEncounterId.getValue(), this.packageDatetime.getValue(),
				this.nextPickupDate.getValue(), this.dispenseModeId.getValue(), this.notes.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidReason.getValue(),
				this.voidedBy.getValue(), this.uuid, this.pharmacyPackageId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE pharmacy_package SET `pharmacy_package_id` = ?, `location_id` = ?, `prescription_encounter_id` = ?, `package_datetime` = ?, `next_pickup_date` = ?, `dispense_mode_id` = ?, `notes` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `void_reason` = ?, `voided_by` = ?, `uuid` = ? WHERE prescription_pharmacy_package_dst_ds.pharmacy_package_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.locationId.getValue()) + "," + (this.prescriptionEncounterId.getValue()) + ","
				+ (this.packageDatetime.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.packageDatetime.getValue()) + "\""
						: null)
				+ ","
				+ (this.nextPickupDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.nextPickupDate.getValue())
						+ "\"" : null)
				+ "," + (this.dispenseModeId.getValue()) + ","
				+ (this.notes.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.notes.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.voidedBy.getValue()) + ","
				+ (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.pharmacyPackageId.getValue()) + "," + (this.locationId.getValue()) + ","
				+ (this.prescriptionEncounterId.getValue()) + ","
				+ (this.packageDatetime.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.packageDatetime.getValue()) + "\""
						: null)
				+ ","
				+ (this.nextPickupDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.nextPickupDate.getValue())
						+ "\"" : null)
				+ "," + (this.dispenseModeId.getValue()) + ","
				+ (this.notes.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.notes.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.voidedBy.getValue()) + ","
				+ (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PharmacyPackageVO copy = new PharmacyPackageVO();

		copy.pharmacyPackageId = copyGeneratedField(this.pharmacyPackageId);
		copy.locationId = copyGeneratedField(this.locationId);
		copy.prescriptionEncounterId = copyGeneratedField(this.prescriptionEncounterId);
		copy.packageDatetime = copyGeneratedField(this.packageDatetime);
		copy.nextPickupDate = copyGeneratedField(this.nextPickupDate);
		copy.dispenseModeId = copyGeneratedField(this.dispenseModeId);
		copy.notes = copyGeneratedField(this.notes);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.voided = copyGeneratedField(this.voided);
		copy.voidReason = copyGeneratedField(this.voidReason);
		copy.voidedBy = copyGeneratedField(this.voidedBy);

		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.prescriptionEncounterId.getValue() != null)
			return true;

		if (this.locationId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("prescriptionEncounterId"))
			return this.prescriptionEncounterId.getValue();
		if (parentAttName.equals("locationId"))
			return this.locationId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "pharmacy_package";
	}

}