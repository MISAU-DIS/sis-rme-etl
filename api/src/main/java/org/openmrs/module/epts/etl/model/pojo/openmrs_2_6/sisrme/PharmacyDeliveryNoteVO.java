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

public class PharmacyDeliveryNoteVO extends AbstractGeneratedDatabaseObject {
	private Field deliveryNoteId = Field.fastCreateWithType("delivery_note_id", "INT");
	private Field documentNumber = Field.fastCreateWithType("document_number", "VARCHAR");
	private Field origin = Field.fastCreateWithType("origin", "VARCHAR");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field transactionDate = Field.fastCreateWithType("transaction_date", "DATETIME");
	private Field subscriber = Field.fastCreateWithType("subscriber", "VARCHAR");
	private Field notes = Field.fastCreateWithType("notes", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");

	public PharmacyDeliveryNoteVO() {
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

	public void setDeliveryNoteId(Field deliveryNoteId) {
		this.deliveryNoteId = deliveryNoteId;
	}

	public void setDeliveryNoteIdValue(Integer value) {
		this.deliveryNoteId.setValue(value);
	}

	public Field getDeliveryNoteId() {
		return this.deliveryNoteId;
	}

	public void setDocumentNumber(Field documentNumber) {
		this.documentNumber = documentNumber;
	}

	public void setDocumentNumberValue(String value) {
		this.documentNumber.setValue(value);
	}

	public Field getDocumentNumber() {
		return this.documentNumber;
	}

	public void setOrigin(Field origin) {
		this.origin = origin;
	}

	public void setOriginValue(String value) {
		this.origin.setValue(value);
	}

	public Field getOrigin() {
		return this.origin;
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

	public void setTransactionDate(Field transactionDate) {
		this.transactionDate = transactionDate;
	}

	public void setTransactionDateValue(java.util.Date value) {
		this.transactionDate.setValue(value);
	}

	public Field getTransactionDate() {
		return this.transactionDate;
	}

	public void setSubscriber(Field subscriber) {
		this.subscriber = subscriber;
	}

	public void setSubscriberValue(String value) {
		this.subscriber.setValue(value);
	}

	public Field getSubscriber() {
		return this.subscriber;
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

		String deliveryNoteIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"delivery_note_id", "_");

		this.deliveryNoteId.setValue(BaseVO.retrieveFieldValue(deliveryNoteIdAttName, "INT", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));

		String documentNumberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"document_number", "_");

		this.documentNumber.setValue(BaseVO.retrieveFieldValue(documentNumberAttName, "VARCHAR", rs));

		String originAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "origin",
				"_");

		this.origin.setValue(BaseVO.retrieveFieldValue(originAttName, "VARCHAR", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

		String transactionDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"transaction_date", "_");

		this.transactionDate.setValue(BaseVO.retrieveFieldValue(transactionDateAttName, "DATETIME", rs));

		String subscriberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"subscriber", "_");

		this.subscriber.setValue(BaseVO.retrieveFieldValue(subscriberAttName, "VARCHAR", rs));

		String notesAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "notes",
				"_");

		this.notes.setValue(BaseVO.retrieveFieldValue(notesAttName, "VARCHAR", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided",
				"_");

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "BIT", rs));

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO pharmacy_delivery_note(`uuid`, `document_number`, `origin`, `location_id`, `transaction_date`, `subscriber`, `notes`, `creator`, `date_created`, `date_changed`, `voided`, `void_reason`, `voided_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO pharmacy_delivery_note(`delivery_note_id`, `uuid`, `document_number`, `origin`, `location_id`, `transaction_date`, `subscriber`, `notes`, `creator`, `date_created`, `date_changed`, `voided`, `void_reason`, `voided_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.uuid, this.documentNumber.getValue(), this.origin.getValue(),
				this.locationId.getValue(), this.transactionDate.getValue(), this.subscriber.getValue(),
				this.notes.getValue(), this.creator.getValue(), this.dateCreated, this.dateChanged,
				this.voided.getValue(), this.voidReason.getValue(), this.voidedBy.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.deliveryNoteId.getValue(), this.uuid, this.documentNumber.getValue(),
				this.origin.getValue(), this.locationId.getValue(), this.transactionDate.getValue(),
				this.subscriber.getValue(), this.notes.getValue(), this.creator.getValue(), this.dateCreated,
				this.dateChanged, this.voided.getValue(), this.voidReason.getValue(), this.voidedBy.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.deliveryNoteId.getValue(), this.uuid, this.documentNumber.getValue(),
				this.origin.getValue(), this.locationId.getValue(), this.transactionDate.getValue(),
				this.subscriber.getValue(), this.notes.getValue(), this.creator.getValue(), this.dateCreated,
				this.dateChanged, this.voided.getValue(), this.voidReason.getValue(), this.voidedBy.getValue(),
				this.deliveryNoteId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE pharmacy_delivery_note SET `delivery_note_id` = ?, `uuid` = ?, `document_number` = ?, `origin` = ?, `location_id` = ?, `transaction_date` = ?, `subscriber` = ?, `notes` = ?, `creator` = ?, `date_created` = ?, `date_changed` = ?, `voided` = ?, `void_reason` = ?, `voided_by` = ? WHERE delivery_note_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null) + ","
				+ (this.documentNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.documentNumber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.origin.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.origin.getValue().toString()) + "\""
						: null)
				+ "," + (this.locationId.getValue()) + ","
				+ (this.transactionDate.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.transactionDate.getValue()) + "\""
						: null)
				+ ","
				+ (this.subscriber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.subscriber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.notes.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.notes.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.voidedBy.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.deliveryNoteId.getValue()) + ","
				+ (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null) + ","
				+ (this.documentNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.documentNumber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.origin.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.origin.getValue().toString()) + "\""
						: null)
				+ "," + (this.locationId.getValue()) + ","
				+ (this.transactionDate.getValue() != null
						? "\"" + DateAndTimeUtilities
								.formatToYYYYMMDD_HHMISS((java.util.Date) this.transactionDate.getValue()) + "\""
						: null)
				+ ","
				+ (this.subscriber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.subscriber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.notes.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.notes.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.voidedBy.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PharmacyDeliveryNoteVO copy = new PharmacyDeliveryNoteVO();
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
		return "pharmacy_delivery_note";
	}

}