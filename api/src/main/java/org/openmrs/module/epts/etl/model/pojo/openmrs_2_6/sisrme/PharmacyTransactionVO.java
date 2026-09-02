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

public class PharmacyTransactionVO extends AbstractGeneratedDatabaseObject {
	private Field pharmacyTransactionId = Field.fastCreateWithType("pharmacy_transaction_id", "INT");
	private Field batchId = Field.fastCreateWithType("batch_id", "INT");
	private Field transactionTypeId = Field.fastCreateWithType("transaction_type_id", "INT");
	private Field quantity = Field.fastCreateWithType("quantity", "INT");
	private Field deliveryNoteId = Field.fastCreateWithType("delivery_note_id", "INT");
	private Field documentNumber = Field.fastCreateWithType("document_number", "VARCHAR");
	private Field notes = Field.fastCreateWithType("notes", "TEXT");
	private Field subscriber = Field.fastCreateWithType("subscriber", "VARCHAR");
	private Field origin = Field.fastCreateWithType("origin", "VARCHAR");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");

	public PharmacyTransactionVO() {
		this.metadata = false;

		this.fields.add(this.pharmacyTransactionId);
		this.fields.add(this.batchId);
		this.fields.add(this.transactionTypeId);
		this.fields.add(this.quantity);
		this.fields.add(this.deliveryNoteId);
		this.fields.add(this.documentNumber);
		this.fields.add(this.notes);
		this.fields.add(this.subscriber);
		this.fields.add(this.origin);
		this.fields.add(this.locationId);
		this.fields.add(this.creator);
		this.fields.add(this.voided);
		this.fields.add(this.voidReason);
		this.fields.add(this.voidedBy);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "pharmacy_transaction_id")) {
			this.pharmacyTransactionId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "pharmacy_transaction_id")) {
			return this.pharmacyTransactionId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "batch_id")) {
			return this.batchId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "transaction_type_id")) {
			return this.transactionTypeId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "quantity")) {
			return this.quantity.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "delivery_note_id")) {
			return this.deliveryNoteId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "document_number")) {
			return this.documentNumber.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "notes")) {
			return this.notes.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "subscriber")) {
			return this.subscriber.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "origin")) {
			return this.origin.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			return this.locationId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "voided")) {
			return this.voided.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "void_reason")) {
			return this.voidReason.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "voided_by")) {
			return this.voidedBy.getValue();
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

	public void setPharmacyTransactionId(Field pharmacyTransactionId) {
		this.pharmacyTransactionId = pharmacyTransactionId;
	}

	public void setPharmacyTransactionIdValue(Integer value) {
		this.pharmacyTransactionId.setValue(value);
	}

	public Field getPharmacyTransactionId() {
		return this.pharmacyTransactionId;
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

	public void setTransactionTypeId(Field transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	public void setTransactionTypeIdValue(Integer value) {
		this.transactionTypeId.setValue(value);
	}

	public Field getTransactionTypeId() {
		return this.transactionTypeId;
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

	public void setNotes(Field notes) {
		this.notes = notes;
	}

	public void setNotesValue(String value) {
		this.notes.setValue(value);
	}

	public Field getNotes() {
		return this.notes;
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

		String pharmacyTransactionIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "pharmacy_transaction_id", "_");

		this.pharmacyTransactionId.setValue(BaseVO.retrieveFieldValue(pharmacyTransactionIdAttName, "INT", rs));

		String batchIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"batch_id", "_");

		this.batchId.setValue(BaseVO.retrieveFieldValue(batchIdAttName, "INT", rs));

		String transactionTypeIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "transaction_type_id", "_");

		this.transactionTypeId.setValue(BaseVO.retrieveFieldValue(transactionTypeIdAttName, "INT", rs));

		String quantityAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"quantity", "_");

		this.quantity.setValue(BaseVO.retrieveFieldValue(quantityAttName, "INT", rs));

		String deliveryNoteIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"delivery_note_id", "_");

		this.deliveryNoteId.setValue(BaseVO.retrieveFieldValue(deliveryNoteIdAttName, "INT", rs));

		String documentNumberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"document_number", "_");

		this.documentNumber.setValue(BaseVO.retrieveFieldValue(documentNumberAttName, "VARCHAR", rs));

		String notesAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "notes",
				"_");

		this.notes.setValue(BaseVO.retrieveFieldValue(notesAttName, "TEXT", rs));

		String subscriberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"subscriber", "_");

		this.subscriber.setValue(BaseVO.retrieveFieldValue(subscriberAttName, "VARCHAR", rs));

		String originAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "origin",
				"_");

		this.origin.setValue(BaseVO.retrieveFieldValue(originAttName, "VARCHAR", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

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
		return "INSERT INTO pharmacy_transaction(`batch_id`, `transaction_type_id`, `quantity`, `delivery_note_id`, `document_number`, `notes`, `subscriber`, `origin`, `location_id`, `creator`, `date_created`, `voided`, `void_reason`, `voided_by`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO pharmacy_transaction(`pharmacy_transaction_id`, `batch_id`, `transaction_type_id`, `quantity`, `delivery_note_id`, `document_number`, `notes`, `subscriber`, `origin`, `location_id`, `creator`, `date_created`, `voided`, `void_reason`, `voided_by`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.batchId.getValue(), this.transactionTypeId.getValue(), this.quantity.getValue(),
				this.deliveryNoteId.getValue(), this.documentNumber.getValue(), this.notes.getValue(),
				this.subscriber.getValue(), this.origin.getValue(), this.locationId.getValue(), this.creator.getValue(),
				this.dateCreated, this.voided.getValue(), this.voidReason.getValue(), this.voidedBy.getValue(),
				this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.pharmacyTransactionId.getValue(), this.batchId.getValue(),
				this.transactionTypeId.getValue(), this.quantity.getValue(), this.deliveryNoteId.getValue(),
				this.documentNumber.getValue(), this.notes.getValue(), this.subscriber.getValue(),
				this.origin.getValue(), this.locationId.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidReason.getValue(), this.voidedBy.getValue(), this.uuid };
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
		Object[] params = { this.pharmacyTransactionId.getValue(), this.batchId.getValue(),
				this.transactionTypeId.getValue(), this.quantity.getValue(), this.deliveryNoteId.getValue(),
				this.documentNumber.getValue(), this.notes.getValue(), this.subscriber.getValue(),
				this.origin.getValue(), this.locationId.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidReason.getValue(), this.voidedBy.getValue(), this.uuid,
				this.pharmacyTransactionId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE pharmacy_transaction SET `pharmacy_transaction_id` = ?, `batch_id` = ?, `transaction_type_id` = ?, `quantity` = ?, `delivery_note_id` = ?, `document_number` = ?, `notes` = ?, `subscriber` = ?, `origin` = ?, `location_id` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `void_reason` = ?, `voided_by` = ?, `uuid` = ? WHERE pharmacy_transaction_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.batchId.getValue()) + "," + (this.transactionTypeId.getValue()) + ","
				+ (this.quantity.getValue()) + "," + (this.deliveryNoteId.getValue()) + ","
				+ (this.documentNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.documentNumber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.notes.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.notes.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.subscriber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.subscriber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.origin.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.origin.getValue().toString()) + "\""
						: null)
				+ "," + (this.locationId.getValue()) + "," + (this.creator.getValue()) + ","
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
		return "" + (this.pharmacyTransactionId.getValue()) + "," + (this.batchId.getValue()) + ","
				+ (this.transactionTypeId.getValue()) + "," + (this.quantity.getValue()) + ","
				+ (this.deliveryNoteId.getValue()) + ","
				+ (this.documentNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.documentNumber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.notes.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.notes.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.subscriber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.subscriber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.origin.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.origin.getValue().toString()) + "\""
						: null)
				+ "," + (this.locationId.getValue()) + "," + (this.creator.getValue()) + ","
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
		PharmacyTransactionVO copy = new PharmacyTransactionVO();
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

		if (this.batchId.getValue() != null)
			return true;

		if (this.deliveryNoteId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("locationId"))
			return this.locationId.getValue();
		if (parentAttName.equals("batchId"))
			return this.batchId.getValue();
		if (parentAttName.equals("deliveryNoteId"))
			return this.deliveryNoteId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "pharmacy_transaction";
	}

}