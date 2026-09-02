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

public class PharmacyPackagedDrugVO extends AbstractGeneratedDatabaseObject {
	private Field pharmacyPackagedDrugId = Field.fastCreateWithType("pharmacy_packaged_drug_id", "INT");
	private Field pharmacyPackageId = Field.fastCreateWithType("pharmacy_package_id", "INT");
	private Field orderId = Field.fastCreateWithType("order_id", "INT");
	private Field pharmacyTransactionId = Field.fastCreateWithType("pharmacy_transaction_id", "INT");
	private Field quantityDispensed = Field.fastCreateWithType("quantity_dispensed", "INT");
	private Field nextPickupDate = Field.fastCreateWithType("next_pickup_date", "DATETIME");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");

	public PharmacyPackagedDrugVO() {
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

	public void setPharmacyPackagedDrugId(Field pharmacyPackagedDrugId) {
		this.pharmacyPackagedDrugId = pharmacyPackagedDrugId;
	}

	public void setPharmacyPackagedDrugIdValue(Integer value) {
		this.pharmacyPackagedDrugId.setValue(value);
	}

	public Field getPharmacyPackagedDrugId() {
		return this.pharmacyPackagedDrugId;
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

	public void setOrderId(Field orderId) {
		this.orderId = orderId;
	}

	public void setOrderIdValue(Integer value) {
		this.orderId.setValue(value);
	}

	public Field getOrderId() {
		return this.orderId;
	}

	public void setPharmacyTransactionId(Field pharmacyTransactionId) {
		this.pharmacyTransactionId = pharmacyTransactionId;
	}

	public void setPharmacyTransactionIdValue(Integer value) {
		this.pharmacyTransactionId.setValue(value);
	}

	public Field getPharmacyTransactionId() {
		return this.pharmacyTransactionId;
	}

	public void setQuantityDispensed(Field quantityDispensed) {
		this.quantityDispensed = quantityDispensed;
	}

	public void setQuantityDispensedValue(Integer value) {
		this.quantityDispensed.setValue(value);
	}

	public Field getQuantityDispensed() {
		return this.quantityDispensed;
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

		String pharmacyPackagedDrugIdAttName = utilities.concatStringsWithSeparator(
				this.getRelatedConfiguration().getAlias(), "pharmacy_packaged_drug_id", "_");

		this.pharmacyPackagedDrugId.setValue(BaseVO.retrieveFieldValue(pharmacyPackagedDrugIdAttName, "INT", rs));

		String pharmacyPackageIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "pharmacy_package_id", "_");

		this.pharmacyPackageId.setValue(BaseVO.retrieveFieldValue(pharmacyPackageIdAttName, "INT", rs));

		String orderIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_id", "_");

		this.orderId.setValue(BaseVO.retrieveFieldValue(orderIdAttName, "INT", rs));

		String pharmacyTransactionIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "pharmacy_transaction_id", "_");

		this.pharmacyTransactionId.setValue(BaseVO.retrieveFieldValue(pharmacyTransactionIdAttName, "INT", rs));

		String quantityDispensedAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "quantity_dispensed", "_");

		this.quantityDispensed.setValue(BaseVO.retrieveFieldValue(quantityDispensedAttName, "INT", rs));

		String nextPickupDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"next_pickup_date", "_");

		this.nextPickupDate.setValue(BaseVO.retrieveFieldValue(nextPickupDateAttName, "DATETIME", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

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

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO pharmacy_packaged_drug(`pharmacy_package_id`, `order_id`, `pharmacy_transaction_id`, `quantity_dispensed`, `next_pickup_date`, `creator`, `date_created`, `voided`, `void_reason`, `voided_by`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO pharmacy_packaged_drug(`pharmacy_packaged_drug_id`, `pharmacy_package_id`, `order_id`, `pharmacy_transaction_id`, `quantity_dispensed`, `next_pickup_date`, `creator`, `date_created`, `voided`, `void_reason`, `voided_by`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.pharmacyPackageId.getValue(), this.orderId.getValue(),
				this.pharmacyTransactionId.getValue(), this.quantityDispensed.getValue(),
				this.nextPickupDate.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidReason.getValue(), this.voidedBy.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.pharmacyPackagedDrugId.getValue(), this.pharmacyPackageId.getValue(),
				this.orderId.getValue(), this.pharmacyTransactionId.getValue(), this.quantityDispensed.getValue(),
				this.nextPickupDate.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidReason.getValue(), this.voidedBy.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.pharmacyPackagedDrugId.getValue(), this.pharmacyPackageId.getValue(),
				this.orderId.getValue(), this.pharmacyTransactionId.getValue(), this.quantityDispensed.getValue(),
				this.nextPickupDate.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidReason.getValue(), this.voidedBy.getValue(), this.uuid,
				this.pharmacyPackagedDrugId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE pharmacy_packaged_drug SET `pharmacy_packaged_drug_id` = ?, `pharmacy_package_id` = ?, `order_id` = ?, `pharmacy_transaction_id` = ?, `quantity_dispensed` = ?, `next_pickup_date` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `void_reason` = ?, `voided_by` = ?, `uuid` = ? WHERE pharmacy_packaged_drug_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.pharmacyPackageId.getValue()) + "," + (this.orderId.getValue()) + ","
				+ (this.pharmacyTransactionId.getValue()) + "," + (this.quantityDispensed.getValue()) + ","
				+ (this.nextPickupDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.nextPickupDate.getValue())
						+ "\"" : null)
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
		return "" + (this.pharmacyPackagedDrugId.getValue()) + "," + (this.pharmacyPackageId.getValue()) + ","
				+ (this.orderId.getValue()) + "," + (this.pharmacyTransactionId.getValue()) + ","
				+ (this.quantityDispensed.getValue()) + ","
				+ (this.nextPickupDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.nextPickupDate.getValue())
						+ "\"" : null)
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
		PharmacyPackagedDrugVO copy = new PharmacyPackagedDrugVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.orderId.getValue() != null)
			return true;

		if (this.pharmacyPackageId.getValue() != null)
			return true;

		if (this.pharmacyTransactionId.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("orderId"))
			return this.orderId.getValue();
		if (parentAttName.equals("pharmacyPackageId"))
			return this.pharmacyPackageId.getValue();
		if (parentAttName.equals("pharmacyTransactionId"))
			return this.pharmacyTransactionId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "pharmacy_packaged_drug";
	}

}