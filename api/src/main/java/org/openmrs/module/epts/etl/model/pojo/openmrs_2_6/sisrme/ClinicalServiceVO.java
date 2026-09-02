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


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ClinicalServiceVO extends AbstractGeneratedDatabaseObject {
	private Field clinicalServiceId = Field.fastCreateWithType("clinical_service_id", "BIGINT UNSIGNED");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field serviceConceptId = Field.fastCreateWithType("service_concept_id", "INT");
	private Field active = Field.fastCreateWithType("active", "BIT");
	private Field hasQueue = Field.fastCreateWithType("has_queue", "BIT");
	private Field hasPharmacy = Field.fastCreateWithType("has_pharmacy", "BIT");
	private Field isOneStop = Field.fastCreateWithType("is_one_stop", "BIT");
	private Field pharmacyLocationId = Field.fastCreateWithType("pharmacy_location_id", "INT");
	private Field displayOrder = Field.fastCreateWithType("display_order", "SMALLINT UNSIGNED");
	private Field creator = Field.fastCreateWithType("creator", "INT UNSIGNED");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT UNSIGNED");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT UNSIGNED");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "TIMESTAMP");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");

	public ClinicalServiceVO() {
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
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) {
		utilities.throwForbiddenMethodException();
	}

	public void setClinicalServiceId(Field clinicalServiceId) {
		this.clinicalServiceId = clinicalServiceId;
	}

	public void setClinicalServiceIdValue(Long value) {
		this.clinicalServiceId.setValue(value);
	}

	public Field getClinicalServiceId() {
		return this.clinicalServiceId;
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

	public void setServiceConceptId(Field serviceConceptId) {
		this.serviceConceptId = serviceConceptId;
	}

	public void setServiceConceptIdValue(Integer value) {
		this.serviceConceptId.setValue(value);
	}

	public Field getServiceConceptId() {
		return this.serviceConceptId;
	}

	public void setActive(Field active) {
		this.active = active;
	}

	public void setActiveValue(Boolean value) {
		this.active.setValue(value);
	}

	public Field getActive() {
		return this.active;
	}

	public void setHasQueue(Field hasQueue) {
		this.hasQueue = hasQueue;
	}

	public void setHasQueueValue(Boolean value) {
		this.hasQueue.setValue(value);
	}

	public Field getHasQueue() {
		return this.hasQueue;
	}

	public void setHasPharmacy(Field hasPharmacy) {
		this.hasPharmacy = hasPharmacy;
	}

	public void setHasPharmacyValue(Boolean value) {
		this.hasPharmacy.setValue(value);
	}

	public Field getHasPharmacy() {
		return this.hasPharmacy;
	}

	public void setIsOneStop(Field isOneStop) {
		this.isOneStop = isOneStop;
	}

	public void setIsOneStopValue(Boolean value) {
		this.isOneStop.setValue(value);
	}

	public Field getIsOneStop() {
		return this.isOneStop;
	}

	public void setPharmacyLocationId(Field pharmacyLocationId) {
		this.pharmacyLocationId = pharmacyLocationId;
	}

	public void setPharmacyLocationIdValue(Integer value) {
		this.pharmacyLocationId.setValue(value);
	}

	public Field getPharmacyLocationId() {
		return this.pharmacyLocationId;
	}

	public void setDisplayOrder(Field displayOrder) {
		this.displayOrder = displayOrder;
	}

	public void setDisplayOrderValue(Short value) {
		this.displayOrder.setValue(value);
	}

	public Field getDisplayOrder() {
		return this.displayOrder;
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

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String clinicalServiceIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "clinical_service_id", "_");

		this.clinicalServiceId.setValue(BaseVO.retrieveFieldValue(clinicalServiceIdAttName, "BIGINT UNSIGNED", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

		String serviceConceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"service_concept_id", "_");

		this.serviceConceptId.setValue(BaseVO.retrieveFieldValue(serviceConceptIdAttName, "INT", rs));

		String activeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "active",
				"_");

		this.active.setValue(BaseVO.retrieveFieldValue(activeAttName, "BIT", rs));

		String hasQueueAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"has_queue", "_");

		this.hasQueue.setValue(BaseVO.retrieveFieldValue(hasQueueAttName, "BIT", rs));

		String hasPharmacyAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"has_pharmacy", "_");

		this.hasPharmacy.setValue(BaseVO.retrieveFieldValue(hasPharmacyAttName, "BIT", rs));

		String isOneStopAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"is_one_stop", "_");

		this.isOneStop.setValue(BaseVO.retrieveFieldValue(isOneStopAttName, "BIT", rs));

		String pharmacyLocationIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "pharmacy_location_id", "_");

		this.pharmacyLocationId.setValue(BaseVO.retrieveFieldValue(pharmacyLocationIdAttName, "INT", rs));

		String displayOrderAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"display_order", "_");

		this.displayOrder.setValue(BaseVO.retrieveFieldValue(displayOrderAttName, "SMALLINT UNSIGNED", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT UNSIGNED", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT UNSIGNED", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String retiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired", "_");

		this.retired.setValue(BaseVO.retrieveFieldValue(retiredAttName, "BIT", rs));

		String retiredByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired_by", "_");

		this.retiredBy.setValue(BaseVO.retrieveFieldValue(retiredByAttName, "INT UNSIGNED", rs));

		String dateRetiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_retired", "_");

		this.dateRetired.setValue(BaseVO.retrieveFieldValue(dateRetiredAttName, "TIMESTAMP", rs));

		String retireReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retire_reason", "_");

		this.retireReason.setValue(BaseVO.retrieveFieldValue(retireReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO clinical_service(`location_id`, `service_concept_id`, `active`, `has_queue`, `has_pharmacy`, `is_one_stop`, `pharmacy_location_id`, `display_order`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO clinical_service(`clinical_service_id`, `location_id`, `service_concept_id`, `active`, `has_queue`, `has_pharmacy`, `is_one_stop`, `pharmacy_location_id`, `display_order`, `creator`, `date_created`, `changed_by`, `date_changed`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.locationId.getValue(), this.serviceConceptId.getValue(), this.active.getValue(),
				this.hasQueue.getValue(), this.hasPharmacy.getValue(), this.isOneStop.getValue(),
				this.pharmacyLocationId.getValue(), this.displayOrder.getValue(), this.creator.getValue(),
				this.dateCreated, this.changedBy.getValue(), this.dateChanged, this.retired.getValue(),
				this.retiredBy.getValue(), this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.clinicalServiceId.getValue(), this.locationId.getValue(),
				this.serviceConceptId.getValue(), this.active.getValue(), this.hasQueue.getValue(),
				this.hasPharmacy.getValue(), this.isOneStop.getValue(), this.pharmacyLocationId.getValue(),
				this.displayOrder.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.clinicalServiceId.getValue(), this.locationId.getValue(),
				this.serviceConceptId.getValue(), this.active.getValue(), this.hasQueue.getValue(),
				this.hasPharmacy.getValue(), this.isOneStop.getValue(), this.pharmacyLocationId.getValue(),
				this.displayOrder.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.clinicalServiceId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE clinical_service SET `clinical_service_id` = ?, `location_id` = ?, `service_concept_id` = ?, `active` = ?, `has_queue` = ?, `has_pharmacy` = ?, `is_one_stop` = ?, `pharmacy_location_id` = ?, `display_order` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ? WHERE clinical_service_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.locationId.getValue()) + "," + (this.serviceConceptId.getValue()) + ","
				+ (this.active.getValue() != null ? "\"" + this.active.getValue() + "\"" : null) + ","
				+ (this.hasQueue.getValue() != null ? "\"" + this.hasQueue.getValue() + "\"" : null) + ","
				+ (this.hasPharmacy.getValue() != null ? "\"" + this.hasPharmacy.getValue() + "\"" : null) + ","
				+ (this.isOneStop.getValue() != null ? "\"" + this.isOneStop.getValue() + "\"" : null) + ","
				+ (this.pharmacyLocationId.getValue()) + "," + (this.displayOrder.getValue()) + ","
				+ (this.creator.getValue()) + ","
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
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.clinicalServiceId.getValue()) + "," + (this.locationId.getValue()) + ","
				+ (this.serviceConceptId.getValue()) + ","
				+ (this.active.getValue() != null ? "\"" + this.active.getValue() + "\"" : null) + ","
				+ (this.hasQueue.getValue() != null ? "\"" + this.hasQueue.getValue() + "\"" : null) + ","
				+ (this.hasPharmacy.getValue() != null ? "\"" + this.hasPharmacy.getValue() + "\"" : null) + ","
				+ (this.isOneStop.getValue() != null ? "\"" + this.isOneStop.getValue() + "\"" : null) + ","
				+ (this.pharmacyLocationId.getValue()) + "," + (this.displayOrder.getValue()) + ","
				+ (this.creator.getValue()) + ","
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
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		ClinicalServiceVO copy = new ClinicalServiceVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.locationId.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("locationId"))
			return this.locationId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "clinical_service";
	}

}