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

public class OrdersVO extends AbstractGeneratedDatabaseObject {
	private Field orderId = Field.fastCreateWithType("order_id", "INT");
	private Field orderTypeId = Field.fastCreateWithType("order_type_id", "INT");
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");
	private Field orderer = Field.fastCreateWithType("orderer", "INT");
	private Field encounterId = Field.fastCreateWithType("encounter_id", "INT");
	private Field dateActivated = Field.fastCreateWithType("date_activated", "DATETIME");
	private Field orderReason = Field.fastCreateWithType("order_reason", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field accessionNumber = Field.fastCreateWithType("accession_number", "VARCHAR");
	private Field urgency = Field.fastCreateWithType("urgency", "VARCHAR");
	private Field orderNumber = Field.fastCreateWithType("order_number", "VARCHAR");
	private Field orderAction = Field.fastCreateWithType("order_action", "VARCHAR");
	private Field commentToFulfiller = Field.fastCreateWithType("comment_to_fulfiller", "VARCHAR");
	private Field careSetting = Field.fastCreateWithType("care_setting", "INT");
	private Field scheduledDate = Field.fastCreateWithType("scheduled_date", "DATETIME");
	private Field discontinued = Field.fastCreateWithType("discontinued", "BIT");
	private Field orderGroupId = Field.fastCreateWithType("order_group_id", "INT");
	private Field fulfillerComment = Field.fastCreateWithType("fulfiller_comment", "VARCHAR");

	public OrdersVO() {
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

	public void setOrderId(Field orderId) {
		this.orderId = orderId;
	}

	public void setOrderIdValue(Integer value) {
		this.orderId.setValue(value);
	}

	public Field getOrderId() {
		return this.orderId;
	}

	public void setOrderTypeId(Field orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public void setOrderTypeIdValue(Integer value) {
		this.orderTypeId.setValue(value);
	}

	public Field getOrderTypeId() {
		return this.orderTypeId;
	}

	public void setConceptId(Field conceptId) {
		this.conceptId = conceptId;
	}

	public void setConceptIdValue(Integer value) {
		this.conceptId.setValue(value);
	}

	public Field getConceptId() {
		return this.conceptId;
	}

	public void setOrderer(Field orderer) {
		this.orderer = orderer;
	}

	public void setOrdererValue(Integer value) {
		this.orderer.setValue(value);
	}

	public Field getOrderer() {
		return this.orderer;
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

	public void setDateActivated(Field dateActivated) {
		this.dateActivated = dateActivated;
	}

	public void setDateActivatedValue(java.util.Date value) {
		this.dateActivated.setValue(value);
	}

	public Field getDateActivated() {
		return this.dateActivated;
	}

	public void setOrderReason(Field orderReason) {
		this.orderReason = orderReason;
	}

	public void setOrderReasonValue(Integer value) {
		this.orderReason.setValue(value);
	}

	public Field getOrderReason() {
		return this.orderReason;
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

	public void setPatientId(Field patientId) {
		this.patientId = patientId;
	}

	public void setPatientIdValue(Integer value) {
		this.patientId.setValue(value);
	}

	public Field getPatientId() {
		return this.patientId;
	}

	public void setAccessionNumber(Field accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public void setAccessionNumberValue(String value) {
		this.accessionNumber.setValue(value);
	}

	public Field getAccessionNumber() {
		return this.accessionNumber;
	}

	public void setUrgency(Field urgency) {
		this.urgency = urgency;
	}

	public void setUrgencyValue(String value) {
		this.urgency.setValue(value);
	}

	public Field getUrgency() {
		return this.urgency;
	}

	public void setOrderNumber(Field orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setOrderNumberValue(String value) {
		this.orderNumber.setValue(value);
	}

	public Field getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderAction(Field orderAction) {
		this.orderAction = orderAction;
	}

	public void setOrderActionValue(String value) {
		this.orderAction.setValue(value);
	}

	public Field getOrderAction() {
		return this.orderAction;
	}

	public void setCommentToFulfiller(Field commentToFulfiller) {
		this.commentToFulfiller = commentToFulfiller;
	}

	public void setCommentToFulfillerValue(String value) {
		this.commentToFulfiller.setValue(value);
	}

	public Field getCommentToFulfiller() {
		return this.commentToFulfiller;
	}

	public void setCareSetting(Field careSetting) {
		this.careSetting = careSetting;
	}

	public void setCareSettingValue(Integer value) {
		this.careSetting.setValue(value);
	}

	public Field getCareSetting() {
		return this.careSetting;
	}

	public void setScheduledDate(Field scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public void setScheduledDateValue(java.util.Date value) {
		this.scheduledDate.setValue(value);
	}

	public Field getScheduledDate() {
		return this.scheduledDate;
	}

	public void setDiscontinued(Field discontinued) {
		this.discontinued = discontinued;
	}

	public void setDiscontinuedValue(Boolean value) {
		this.discontinued.setValue(value);
	}

	public Field getDiscontinued() {
		return this.discontinued;
	}

	public void setOrderGroupId(Field orderGroupId) {
		this.orderGroupId = orderGroupId;
	}

	public void setOrderGroupIdValue(Integer value) {
		this.orderGroupId.setValue(value);
	}

	public Field getOrderGroupId() {
		return this.orderGroupId;
	}

	public void setFulfillerComment(Field fulfillerComment) {
		this.fulfillerComment = fulfillerComment;
	}

	public void setFulfillerCommentValue(String value) {
		this.fulfillerComment.setValue(value);
	}

	public Field getFulfillerComment() {
		return this.fulfillerComment;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String orderIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_id", "_");

		this.orderId.setValue(BaseVO.retrieveFieldValue(orderIdAttName, "INT", rs));

		String orderTypeIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_type_id", "_");

		this.orderTypeId.setValue(BaseVO.retrieveFieldValue(orderTypeIdAttName, "INT", rs));

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));

		String ordererAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"orderer", "_");

		this.orderer.setValue(BaseVO.retrieveFieldValue(ordererAttName, "INT", rs));

		String encounterIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"encounter_id", "_");

		this.encounterId.setValue(BaseVO.retrieveFieldValue(encounterIdAttName, "INT", rs));

		String dateActivatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_activated", "_");

		this.dateActivated.setValue(BaseVO.retrieveFieldValue(dateActivatedAttName, "DATETIME", rs));

		String orderReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_reason", "_");

		this.orderReason.setValue(BaseVO.retrieveFieldValue(orderReasonAttName, "INT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

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

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String accessionNumberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"accession_number", "_");

		this.accessionNumber.setValue(BaseVO.retrieveFieldValue(accessionNumberAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));

		String urgencyAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"urgency", "_");

		this.urgency.setValue(BaseVO.retrieveFieldValue(urgencyAttName, "VARCHAR", rs));

		String orderNumberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_number", "_");

		this.orderNumber.setValue(BaseVO.retrieveFieldValue(orderNumberAttName, "VARCHAR", rs));

		String orderActionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_action", "_");

		this.orderAction.setValue(BaseVO.retrieveFieldValue(orderActionAttName, "VARCHAR", rs));

		String commentToFulfillerAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "comment_to_fulfiller", "_");

		this.commentToFulfiller.setValue(BaseVO.retrieveFieldValue(commentToFulfillerAttName, "VARCHAR", rs));

		String careSettingAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"care_setting", "_");

		this.careSetting.setValue(BaseVO.retrieveFieldValue(careSettingAttName, "INT", rs));

		String scheduledDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"scheduled_date", "_");

		this.scheduledDate.setValue(BaseVO.retrieveFieldValue(scheduledDateAttName, "DATETIME", rs));

		String discontinuedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"discontinued", "_");

		this.discontinued.setValue(BaseVO.retrieveFieldValue(discontinuedAttName, "BIT", rs));

		String orderGroupIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_group_id", "_");

		this.orderGroupId.setValue(BaseVO.retrieveFieldValue(orderGroupIdAttName, "INT", rs));

		String fulfillerCommentAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"fulfiller_comment", "_");

		this.fulfillerComment.setValue(BaseVO.retrieveFieldValue(fulfillerCommentAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO orders(`order_type_id`, `concept_id`, `orderer`, `encounter_id`, `date_activated`, `order_reason`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `patient_id`, `accession_number`, `uuid`, `urgency`, `order_number`, `order_action`, `comment_to_fulfiller`, `care_setting`, `scheduled_date`, `discontinued`, `order_group_id`, `fulfiller_comment`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO orders(`order_id`, `order_type_id`, `concept_id`, `orderer`, `encounter_id`, `date_activated`, `order_reason`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `patient_id`, `accession_number`, `uuid`, `urgency`, `order_number`, `order_action`, `comment_to_fulfiller`, `care_setting`, `scheduled_date`, `discontinued`, `order_group_id`, `fulfiller_comment`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.orderTypeId.getValue(), this.conceptId.getValue(), this.orderer.getValue(),
				this.encounterId.getValue(), this.dateActivated.getValue(), this.orderReason.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.patientId.getValue(), this.accessionNumber.getValue(),
				this.uuid, this.urgency.getValue(), this.orderNumber.getValue(), this.orderAction.getValue(),
				this.commentToFulfiller.getValue(), this.careSetting.getValue(), this.scheduledDate.getValue(),
				this.discontinued.getValue(), this.orderGroupId.getValue(), this.fulfillerComment.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.orderId.getValue(), this.orderTypeId.getValue(), this.conceptId.getValue(),
				this.orderer.getValue(), this.encounterId.getValue(), this.dateActivated.getValue(),
				this.orderReason.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.patientId.getValue(),
				this.accessionNumber.getValue(), this.uuid, this.urgency.getValue(), this.orderNumber.getValue(),
				this.orderAction.getValue(), this.commentToFulfiller.getValue(), this.careSetting.getValue(),
				this.scheduledDate.getValue(), this.discontinued.getValue(), this.orderGroupId.getValue(),
				this.fulfillerComment.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.orderId.getValue(), this.orderTypeId.getValue(), this.conceptId.getValue(),
				this.orderer.getValue(), this.encounterId.getValue(), this.dateActivated.getValue(),
				this.orderReason.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.patientId.getValue(),
				this.accessionNumber.getValue(), this.uuid, this.urgency.getValue(), this.orderNumber.getValue(),
				this.orderAction.getValue(), this.commentToFulfiller.getValue(), this.careSetting.getValue(),
				this.scheduledDate.getValue(), this.discontinued.getValue(), this.orderGroupId.getValue(),
				this.fulfillerComment.getValue(), this.orderId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE orders SET `order_id` = ?, `order_type_id` = ?, `concept_id` = ?, `orderer` = ?, `encounter_id` = ?, `date_activated` = ?, `order_reason` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `patient_id` = ?, `accession_number` = ?, `uuid` = ?, `urgency` = ?, `order_number` = ?, `order_action` = ?, `comment_to_fulfiller` = ?, `care_setting` = ?, `scheduled_date` = ?, `discontinued` = ?, `order_group_id` = ?, `fulfiller_comment` = ? WHERE order_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.orderTypeId.getValue()) + "," + (this.conceptId.getValue()) + "," + (this.orderer.getValue())
				+ "," + (this.encounterId.getValue()) + ","
				+ (this.dateActivated.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateActivated.getValue())
						+ "\"" : null)
				+ "," + (this.orderReason.getValue()) + "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
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
				+ "," + (this.patientId.getValue()) + ","
				+ (this.accessionNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.accessionNumber.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ ","
				+ (this.urgency.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.urgency.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.orderNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.orderNumber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.orderAction.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.orderAction.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.commentToFulfiller.getValue() != null ? "\""
						+ utilities.scapeQuotationMarks(this.commentToFulfiller.getValue().toString()) + "\"" : null)
				+ "," + (this.careSetting.getValue()) + ","
				+ (this.scheduledDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.scheduledDate.getValue())
						+ "\"" : null)
				+ "," + (this.discontinued.getValue() != null ? "\"" + this.discontinued.getValue() + "\"" : null) + ","
				+ (this.orderGroupId.getValue()) + ","
				+ (this.fulfillerComment.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fulfillerComment.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.orderId.getValue()) + "," + (this.orderTypeId.getValue()) + "," + (this.conceptId.getValue())
				+ "," + (this.orderer.getValue()) + "," + (this.encounterId.getValue()) + ","
				+ (this.dateActivated.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateActivated.getValue())
						+ "\"" : null)
				+ "," + (this.orderReason.getValue()) + "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
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
				+ "," + (this.patientId.getValue()) + ","
				+ (this.accessionNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.accessionNumber.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ ","
				+ (this.urgency.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.urgency.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.orderNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.orderNumber.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.orderAction.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.orderAction.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.commentToFulfiller.getValue() != null ? "\""
						+ utilities.scapeQuotationMarks(this.commentToFulfiller.getValue().toString()) + "\"" : null)
				+ "," + (this.careSetting.getValue()) + ","
				+ (this.scheduledDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.scheduledDate.getValue())
						+ "\"" : null)
				+ "," + (this.discontinued.getValue() != null ? "\"" + this.discontinued.getValue() + "\"" : null) + ","
				+ (this.orderGroupId.getValue()) + ","
				+ (this.fulfillerComment.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.fulfillerComment.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		OrdersVO copy = new OrdersVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.careSetting.getValue() != null)
			return true;

		if (this.orderReason.getValue() != null)
			return true;

		if (this.conceptId.getValue() != null)
			return true;

		if (this.encounterId.getValue() != null)
			return true;

		if (this.orderGroupId.getValue() != null)
			return true;

		if (this.orderTypeId.getValue() != null)
			return true;

		if (this.patientId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("careSetting"))
			return this.careSetting.getValue();
		if (parentAttName.equals("orderReason"))
			return this.orderReason.getValue();
		if (parentAttName.equals("conceptId"))
			return this.conceptId.getValue();
		if (parentAttName.equals("encounterId"))
			return this.encounterId.getValue();
		if (parentAttName.equals("orderGroupId"))
			return this.orderGroupId.getValue();
		if (parentAttName.equals("orderTypeId"))
			return this.orderTypeId.getValue();
		if (parentAttName.equals("patientId"))
			return this.patientId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "orders";
	}

}