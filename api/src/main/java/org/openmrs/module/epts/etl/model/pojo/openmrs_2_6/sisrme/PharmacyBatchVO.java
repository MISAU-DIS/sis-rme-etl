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

public class PharmacyBatchVO extends AbstractGeneratedDatabaseObject {
	private Field batchId = Field.fastCreateWithType("batch_id", "INT");
	private Field drugId = Field.fastCreateWithType("drug_id", "INT");
	private Field batchNumber = Field.fastCreateWithType("batch_number", "VARCHAR");
	private Field expiryDate = Field.fastCreateWithType("expiry_date", "DATE");
	private Field quantity = Field.fastCreateWithType("quantity", "INT");
	private Field balance = Field.fastCreateWithType("balance", "INT");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PharmacyBatchVO() {
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

	public void setBatchId(Field batchId) {
		this.batchId = batchId;
	}

	public void setBatchIdValue(Integer value) {
		this.batchId.setValue(value);
	}

	public Field getBatchId() {
		return this.batchId;
	}

	public void setDrugId(Field drugId) {
		this.drugId = drugId;
	}

	public void setDrugIdValue(Integer value) {
		this.drugId.setValue(value);
	}

	public Field getDrugId() {
		return this.drugId;
	}

	public void setBatchNumber(Field batchNumber) {
		this.batchNumber = batchNumber;
	}

	public void setBatchNumberValue(String value) {
		this.batchNumber.setValue(value);
	}

	public Field getBatchNumber() {
		return this.batchNumber;
	}

	public void setExpiryDate(Field expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setExpiryDateValue(java.util.Date value) {
		this.expiryDate.setValue(value);
	}

	public Field getExpiryDate() {
		return this.expiryDate;
	}

	public void setQuantity(Field quantity) {
		this.quantity = quantity;
	}

	public void setQuantityValue(Integer value) {
		this.quantity.setValue(value);
	}

	public Field getQuantity() {
		return this.quantity;
	}

	public void setBalance(Field balance) {
		this.balance = balance;
	}

	public void setBalanceValue(Integer value) {
		this.balance.setValue(value);
	}

	public Field getBalance() {
		return this.balance;
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

		String batchIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"batch_id", "_");

		this.batchId.setValue(BaseVO.retrieveFieldValue(batchIdAttName, "INT", rs));

		String drugIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"drug_id", "_");

		this.drugId.setValue(BaseVO.retrieveFieldValue(drugIdAttName, "INT", rs));

		String batchNumberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"batch_number", "_");

		this.batchNumber.setValue(BaseVO.retrieveFieldValue(batchNumberAttName, "VARCHAR", rs));

		String expiryDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"expiry_date", "_");

		this.expiryDate.setValue(BaseVO.retrieveFieldValue(expiryDateAttName, "DATE", rs));

		String quantityAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"quantity", "_");

		this.quantity.setValue(BaseVO.retrieveFieldValue(quantityAttName, "INT", rs));

		String balanceAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"balance", "_");

		this.balance.setValue(BaseVO.retrieveFieldValue(balanceAttName, "INT", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

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
		return "INSERT INTO pharmacy_batch(`drug_id`, `batch_number`, `expiry_date`, `quantity`, `balance`, `location_id`, `creator`, `date_created`, `voided`, `void_reason`, `voided_by`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO pharmacy_batch(`batch_id`, `drug_id`, `batch_number`, `expiry_date`, `quantity`, `balance`, `location_id`, `creator`, `date_created`, `voided`, `void_reason`, `voided_by`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.drugId.getValue(), this.batchNumber.getValue(), this.expiryDate.getValue(),
				this.quantity.getValue(), this.balance.getValue(), this.locationId.getValue(), this.creator.getValue(),
				this.dateCreated, this.voided.getValue(), this.voidReason.getValue(), this.voidedBy.getValue(),
				this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.batchId.getValue(), this.drugId.getValue(), this.batchNumber.getValue(),
				this.expiryDate.getValue(), this.quantity.getValue(), this.balance.getValue(),
				this.locationId.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidReason.getValue(), this.voidedBy.getValue(), this.uuid };
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
		Object[] params = { this.batchId.getValue(), this.drugId.getValue(), this.batchNumber.getValue(),
				this.expiryDate.getValue(), this.quantity.getValue(), this.balance.getValue(),
				this.locationId.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidReason.getValue(), this.voidedBy.getValue(), this.uuid, this.batchId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE pharmacy_batch SET `batch_id` = ?, `drug_id` = ?, `batch_number` = ?, `expiry_date` = ?, `quantity` = ?, `balance` = ?, `location_id` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `void_reason` = ?, `voided_by` = ?, `uuid` = ? WHERE pharmacy_batch_1.batch_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.drugId.getValue()) + ","
				+ (this.batchNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.batchNumber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.expiryDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.expiryDate.getValue())
						+ "\"" : null)
				+ "," + (this.quantity.getValue()) + "," + (this.balance.getValue()) + ","
				+ (this.locationId.getValue()) + "," + (this.creator.getValue()) + ","
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
		return "" + (this.batchId.getValue()) + "," + (this.drugId.getValue()) + ","
				+ (this.batchNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.batchNumber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.expiryDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.expiryDate.getValue())
						+ "\"" : null)
				+ "," + (this.quantity.getValue()) + "," + (this.balance.getValue()) + ","
				+ (this.locationId.getValue()) + "," + (this.creator.getValue()) + ","
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
		PharmacyBatchVO copy = new PharmacyBatchVO();

		copy.batchId = copyGeneratedField(this.batchId);
		copy.drugId = copyGeneratedField(this.drugId);
		copy.batchNumber = copyGeneratedField(this.batchNumber);
		copy.expiryDate = copyGeneratedField(this.expiryDate);
		copy.quantity = copyGeneratedField(this.quantity);
		copy.balance = copyGeneratedField(this.balance);
		copy.locationId = copyGeneratedField(this.locationId);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.voided = copyGeneratedField(this.voided);
		copy.voidReason = copyGeneratedField(this.voidReason);
		copy.voidedBy = copyGeneratedField(this.voidedBy);

		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.drugId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("drugId"))
			return this.drugId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "pharmacy_batch";
	}

}