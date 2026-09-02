package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp;

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

public class TestResultVO extends AbstractGeneratedDatabaseObject {
	private Field testResultId = Field.fastCreateWithType("test_result_id", "INT");
	private Field orderId = Field.fastCreateWithType("order_id", "INT");
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field encounterId = Field.fastCreateWithType("encounter_id", "INT");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field resultDate = Field.fastCreateWithType("result_date", "DATETIME");
	private Field valueNumeric = Field.fastCreateWithType("value_numeric", "DOUBLE");
	private Field valueCoded = Field.fastCreateWithType("value_coded", "INT");
	private Field valueText = Field.fastCreateWithType("value_text", "VARCHAR");
	private Field valueDatetime = Field.fastCreateWithType("value_datetime", "DATETIME");
	private Field valueComplex = Field.fastCreateWithType("value_complex", "VARCHAR");
	private Field valueModifier = Field.fastCreateWithType("value_modifier", "VARCHAR");
	private Field status = Field.fastCreateWithType("status", "VARCHAR");
	private Field interpretation = Field.fastCreateWithType("interpretation", "VARCHAR");
	private Field comments = Field.fastCreateWithType("comments", "TEXT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "TINYINT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");

	public TestResultVO() {
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

	public void setTestResultId(Field testResultId) {
		this.testResultId = testResultId;
	}

	public void setTestResultIdValue(Integer value) {
		this.testResultId.setValue(value);
	}

	public Field getTestResultId() {
		return this.testResultId;
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

	public void setConceptId(Field conceptId) {
		this.conceptId = conceptId;
	}

	public void setConceptIdValue(Integer value) {
		this.conceptId.setValue(value);
	}

	public Field getConceptId() {
		return this.conceptId;
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

	public void setResultDate(Field resultDate) {
		this.resultDate = resultDate;
	}

	public void setResultDateValue(java.util.Date value) {
		this.resultDate.setValue(value);
	}

	public Field getResultDate() {
		return this.resultDate;
	}

	public void setValueNumeric(Field valueNumeric) {
		this.valueNumeric = valueNumeric;
	}

	public void setValueNumericValue(Double value) {
		this.valueNumeric.setValue(value);
	}

	public Field getValueNumeric() {
		return this.valueNumeric;
	}

	public void setValueCoded(Field valueCoded) {
		this.valueCoded = valueCoded;
	}

	public void setValueCodedValue(Integer value) {
		this.valueCoded.setValue(value);
	}

	public Field getValueCoded() {
		return this.valueCoded;
	}

	public void setValueText(Field valueText) {
		this.valueText = valueText;
	}

	public void setValueTextValue(String value) {
		this.valueText.setValue(value);
	}

	public Field getValueText() {
		return this.valueText;
	}

	public void setValueDatetime(Field valueDatetime) {
		this.valueDatetime = valueDatetime;
	}

	public void setValueDatetimeValue(java.util.Date value) {
		this.valueDatetime.setValue(value);
	}

	public Field getValueDatetime() {
		return this.valueDatetime;
	}

	public void setValueComplex(Field valueComplex) {
		this.valueComplex = valueComplex;
	}

	public void setValueComplexValue(String value) {
		this.valueComplex.setValue(value);
	}

	public Field getValueComplex() {
		return this.valueComplex;
	}

	public void setValueModifier(Field valueModifier) {
		this.valueModifier = valueModifier;
	}

	public void setValueModifierValue(String value) {
		this.valueModifier.setValue(value);
	}

	public Field getValueModifier() {
		return this.valueModifier;
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

	public void setInterpretation(Field interpretation) {
		this.interpretation = interpretation;
	}

	public void setInterpretationValue(String value) {
		this.interpretation.setValue(value);
	}

	public Field getInterpretation() {
		return this.interpretation;
	}

	public void setComments(Field comments) {
		this.comments = comments;
	}

	public void setCommentsValue(String value) {
		this.comments.setValue(value);
	}

	public Field getComments() {
		return this.comments;
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

	public void setVoidedValue(Byte value) {
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

		String testResultIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"test_result_id", "_");

		this.testResultId.setValue(BaseVO.retrieveFieldValue(testResultIdAttName, "INT", rs));

		String orderIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_id", "_");

		this.orderId.setValue(BaseVO.retrieveFieldValue(orderIdAttName, "INT", rs));

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String encounterIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"encounter_id", "_");

		this.encounterId.setValue(BaseVO.retrieveFieldValue(encounterIdAttName, "INT", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

		String resultDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"result_date", "_");

		this.resultDate.setValue(BaseVO.retrieveFieldValue(resultDateAttName, "DATETIME", rs));

		String valueNumericAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_numeric", "_");

		this.valueNumeric.setValue(BaseVO.retrieveFieldValue(valueNumericAttName, "DOUBLE", rs));

		String valueCodedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_coded", "_");

		this.valueCoded.setValue(BaseVO.retrieveFieldValue(valueCodedAttName, "INT", rs));

		String valueTextAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_text", "_");

		this.valueText.setValue(BaseVO.retrieveFieldValue(valueTextAttName, "VARCHAR", rs));

		String valueDatetimeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_datetime", "_");

		this.valueDatetime.setValue(BaseVO.retrieveFieldValue(valueDatetimeAttName, "DATETIME", rs));

		String valueComplexAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_complex", "_");

		this.valueComplex.setValue(BaseVO.retrieveFieldValue(valueComplexAttName, "VARCHAR", rs));

		String valueModifierAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_modifier", "_");

		this.valueModifier.setValue(BaseVO.retrieveFieldValue(valueModifierAttName, "VARCHAR", rs));

		String statusAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "status",
				"_");

		this.status.setValue(BaseVO.retrieveFieldValue(statusAttName, "VARCHAR", rs));

		String interpretationAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"interpretation", "_");

		this.interpretation.setValue(BaseVO.retrieveFieldValue(interpretationAttName, "VARCHAR", rs));

		String commentsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"comments", "_");

		this.comments.setValue(BaseVO.retrieveFieldValue(commentsAttName, "TEXT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided",
				"_");

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "TINYINT", rs));

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

		String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_voided", "_");

		this.dateVoided = (java.util.Date) BaseVO.retrieveFieldValue(dateVoidedAttName, "DATETIME", rs);

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO test_result(`order_id`, `concept_id`, `patient_id`, `encounter_id`, `location_id`, `result_date`, `value_numeric`, `value_coded`, `value_text`, `value_datetime`, `value_complex`, `value_modifier`, `status`, `interpretation`, `comments`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO test_result(`test_result_id`, `order_id`, `concept_id`, `patient_id`, `encounter_id`, `location_id`, `result_date`, `value_numeric`, `value_coded`, `value_text`, `value_datetime`, `value_complex`, `value_modifier`, `status`, `interpretation`, `comments`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.orderId.getValue(), this.conceptId.getValue(), this.patientId.getValue(),
				this.encounterId.getValue(), this.locationId.getValue(), this.resultDate.getValue(),
				this.valueNumeric.getValue(), this.valueCoded.getValue(), this.valueText.getValue(),
				this.valueDatetime.getValue(), this.valueComplex.getValue(), this.valueModifier.getValue(),
				this.status.getValue(), this.interpretation.getValue(), this.comments.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.testResultId.getValue(), this.orderId.getValue(), this.conceptId.getValue(),
				this.patientId.getValue(), this.encounterId.getValue(), this.locationId.getValue(),
				this.resultDate.getValue(), this.valueNumeric.getValue(), this.valueCoded.getValue(),
				this.valueText.getValue(), this.valueDatetime.getValue(), this.valueComplex.getValue(),
				this.valueModifier.getValue(), this.status.getValue(), this.interpretation.getValue(),
				this.comments.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.testResultId.getValue(), this.orderId.getValue(), this.conceptId.getValue(),
				this.patientId.getValue(), this.encounterId.getValue(), this.locationId.getValue(),
				this.resultDate.getValue(), this.valueNumeric.getValue(), this.valueCoded.getValue(),
				this.valueText.getValue(), this.valueDatetime.getValue(), this.valueComplex.getValue(),
				this.valueModifier.getValue(), this.status.getValue(), this.interpretation.getValue(),
				this.comments.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid,
				this.testResultId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE test_result SET `test_result_id` = ?, `order_id` = ?, `concept_id` = ?, `patient_id` = ?, `encounter_id` = ?, `location_id` = ?, `result_date` = ?, `value_numeric` = ?, `value_coded` = ?, `value_text` = ?, `value_datetime` = ?, `value_complex` = ?, `value_modifier` = ?, `status` = ?, `interpretation` = ?, `comments` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ? WHERE test_result_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.orderId.getValue()) + "," + (this.conceptId.getValue()) + "," + (this.patientId.getValue())
				+ "," + (this.encounterId.getValue()) + "," + (this.locationId.getValue()) + ","
				+ (this.resultDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.resultDate.getValue())
						+ "\"" : null)
				+ "," + (this.valueNumeric.getValue()) + "," + (this.valueCoded.getValue()) + ","
				+ (this.valueText.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueText.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.valueDatetime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.valueDatetime.getValue())
						+ "\"" : null)
				+ ","
				+ (this.valueComplex.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueComplex.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.valueModifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueModifier.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.status.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.status.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.interpretation.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.interpretation.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.comments.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.comments.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue()) + "," + (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.testResultId.getValue()) + "," + (this.orderId.getValue()) + "," + (this.conceptId.getValue())
				+ "," + (this.patientId.getValue()) + "," + (this.encounterId.getValue()) + ","
				+ (this.locationId.getValue()) + ","
				+ (this.resultDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.resultDate.getValue())
						+ "\"" : null)
				+ "," + (this.valueNumeric.getValue()) + "," + (this.valueCoded.getValue()) + ","
				+ (this.valueText.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueText.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.valueDatetime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.valueDatetime.getValue())
						+ "\"" : null)
				+ ","
				+ (this.valueComplex.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueComplex.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.valueModifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueModifier.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.status.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.status.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.interpretation.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.interpretation.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.comments.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.comments.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue()) + "," + (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		TestResultVO copy = new TestResultVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.conceptId.getValue() != null)
			return true;

		if (this.valueCoded.getValue() != null)
			return true;

		if (this.encounterId.getValue() != null)
			return true;

		if (this.locationId.getValue() != null)
			return true;

		if (this.patientId.getValue() != null)
			return true;

		if (this.orderId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("conceptId"))
			return this.conceptId.getValue();
		if (parentAttName.equals("valueCoded"))
			return this.valueCoded.getValue();
		if (parentAttName.equals("encounterId"))
			return this.encounterId.getValue();
		if (parentAttName.equals("locationId"))
			return this.locationId.getValue();
		if (parentAttName.equals("patientId"))
			return this.patientId.getValue();
		if (parentAttName.equals("orderId"))
			return this.orderId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "test_result";
	}

}