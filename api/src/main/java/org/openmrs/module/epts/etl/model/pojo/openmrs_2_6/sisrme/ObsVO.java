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

public class ObsVO extends AbstractGeneratedDatabaseObject {
	private Field obsId = Field.fastCreateWithType("obs_id", "INT");
	private Field personId = Field.fastCreateWithType("person_id", "INT");
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");
	private Field encounterId = Field.fastCreateWithType("encounter_id", "INT");
	private Field orderId = Field.fastCreateWithType("order_id", "INT");
	private Field obsDatetime = Field.fastCreateWithType("obs_datetime", "DATETIME");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field accessionNumber = Field.fastCreateWithType("accession_number", "VARCHAR");
	private Field valueGroupId = Field.fastCreateWithType("value_group_id", "INT");
	private Field valueCoded = Field.fastCreateWithType("value_coded", "INT");
	private Field valueCodedNameId = Field.fastCreateWithType("value_coded_name_id", "INT");
	private Field valueDrug = Field.fastCreateWithType("value_drug", "INT");
	private Field valueDatetime = Field.fastCreateWithType("value_datetime", "DATETIME");
	private Field valueNumeric = Field.fastCreateWithType("value_numeric", "DOUBLE");
	private Field valueModifier = Field.fastCreateWithType("value_modifier", "VARCHAR");
	private Field valueText = Field.fastCreateWithType("value_text", "TEXT");
	private Field comments = Field.fastCreateWithType("comments", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field valueComplex = Field.fastCreateWithType("value_complex", "VARCHAR");
	private Field formNamespaceAndPath = Field.fastCreateWithType("form_namespace_and_path", "VARCHAR");
	private Field status = Field.fastCreateWithType("status", "VARCHAR");
	private Field interpretation = Field.fastCreateWithType("interpretation", "VARCHAR");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public ObsVO() {
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

	public void setObsId(Field obsId) {
		this.obsId = obsId;
	}

	public void setObsIdValue(Integer value) {
		this.obsId.setValue(value);
	}

	public Field getObsId() {
		return this.obsId;
	}

	public void setPersonId(Field personId) {
		this.personId = personId;
	}

	public void setPersonIdValue(Integer value) {
		this.personId.setValue(value);
	}

	public Field getPersonId() {
		return this.personId;
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

	public void setEncounterId(Field encounterId) {
		this.encounterId = encounterId;
	}

	public void setEncounterIdValue(Integer value) {
		this.encounterId.setValue(value);
	}

	public Field getEncounterId() {
		return this.encounterId;
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

	public void setObsDatetime(Field obsDatetime) {
		this.obsDatetime = obsDatetime;
	}

	public void setObsDatetimeValue(java.util.Date value) {
		this.obsDatetime.setValue(value);
	}

	public Field getObsDatetime() {
		return this.obsDatetime;
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

	public void setAccessionNumber(Field accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public void setAccessionNumberValue(String value) {
		this.accessionNumber.setValue(value);
	}

	public Field getAccessionNumber() {
		return this.accessionNumber;
	}

	public void setValueGroupId(Field valueGroupId) {
		this.valueGroupId = valueGroupId;
	}

	public void setValueGroupIdValue(Integer value) {
		this.valueGroupId.setValue(value);
	}

	public Field getValueGroupId() {
		return this.valueGroupId;
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

	public void setValueCodedNameId(Field valueCodedNameId) {
		this.valueCodedNameId = valueCodedNameId;
	}

	public void setValueCodedNameIdValue(Integer value) {
		this.valueCodedNameId.setValue(value);
	}

	public Field getValueCodedNameId() {
		return this.valueCodedNameId;
	}

	public void setValueDrug(Field valueDrug) {
		this.valueDrug = valueDrug;
	}

	public void setValueDrugValue(Integer value) {
		this.valueDrug.setValue(value);
	}

	public Field getValueDrug() {
		return this.valueDrug;
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

	public void setValueNumeric(Field valueNumeric) {
		this.valueNumeric = valueNumeric;
	}

	public void setValueNumericValue(Double value) {
		this.valueNumeric.setValue(value);
	}

	public Field getValueNumeric() {
		return this.valueNumeric;
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

	public void setValueText(Field valueText) {
		this.valueText = valueText;
	}

	public void setValueTextValue(String value) {
		this.valueText.setValue(value);
	}

	public Field getValueText() {
		return this.valueText;
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

	public void setValueComplex(Field valueComplex) {
		this.valueComplex = valueComplex;
	}

	public void setValueComplexValue(String value) {
		this.valueComplex.setValue(value);
	}

	public Field getValueComplex() {
		return this.valueComplex;
	}

	public void setFormNamespaceAndPath(Field formNamespaceAndPath) {
		this.formNamespaceAndPath = formNamespaceAndPath;
	}

	public void setFormNamespaceAndPathValue(String value) {
		this.formNamespaceAndPath.setValue(value);
	}

	public Field getFormNamespaceAndPath() {
		return this.formNamespaceAndPath;
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

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String obsIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "obs_id",
				"_");

		this.obsId.setValue(BaseVO.retrieveFieldValue(obsIdAttName, "INT", rs));

		String personIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_id", "_");

		this.personId.setValue(BaseVO.retrieveFieldValue(personIdAttName, "INT", rs));

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));

		String encounterIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"encounter_id", "_");

		this.encounterId.setValue(BaseVO.retrieveFieldValue(encounterIdAttName, "INT", rs));

		String orderIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_id", "_");

		this.orderId.setValue(BaseVO.retrieveFieldValue(orderIdAttName, "INT", rs));

		String obsDatetimeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"obs_datetime", "_");

		this.obsDatetime.setValue(BaseVO.retrieveFieldValue(obsDatetimeAttName, "DATETIME", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

		String accessionNumberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"accession_number", "_");

		this.accessionNumber.setValue(BaseVO.retrieveFieldValue(accessionNumberAttName, "VARCHAR", rs));

		String valueGroupIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_group_id", "_");

		this.valueGroupId.setValue(BaseVO.retrieveFieldValue(valueGroupIdAttName, "INT", rs));

		String valueCodedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_coded", "_");

		this.valueCoded.setValue(BaseVO.retrieveFieldValue(valueCodedAttName, "INT", rs));

		String valueCodedNameIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_coded_name_id", "_");

		this.valueCodedNameId.setValue(BaseVO.retrieveFieldValue(valueCodedNameIdAttName, "INT", rs));

		String valueDrugAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_drug", "_");

		this.valueDrug.setValue(BaseVO.retrieveFieldValue(valueDrugAttName, "INT", rs));

		String valueDatetimeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_datetime", "_");

		this.valueDatetime.setValue(BaseVO.retrieveFieldValue(valueDatetimeAttName, "DATETIME", rs));

		String valueNumericAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_numeric", "_");

		this.valueNumeric.setValue(BaseVO.retrieveFieldValue(valueNumericAttName, "DOUBLE", rs));

		String valueModifierAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_modifier", "_");

		this.valueModifier.setValue(BaseVO.retrieveFieldValue(valueModifierAttName, "VARCHAR", rs));

		String valueTextAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_text", "_");

		this.valueText.setValue(BaseVO.retrieveFieldValue(valueTextAttName, "TEXT", rs));

		String commentsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"comments", "_");

		this.comments.setValue(BaseVO.retrieveFieldValue(commentsAttName, "VARCHAR", rs));

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

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

		String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_voided", "_");

		this.dateVoided = rs.getTimestamp(dateVoidedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateVoidedAttName).getTime())
				: null;

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String valueComplexAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"value_complex", "_");

		this.valueComplex.setValue(BaseVO.retrieveFieldValue(valueComplexAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);

		String formNamespaceAndPathAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "form_namespace_and_path", "_");

		this.formNamespaceAndPath.setValue(BaseVO.retrieveFieldValue(formNamespaceAndPathAttName, "VARCHAR", rs));

		String statusAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "status",
				"_");

		this.status.setValue(BaseVO.retrieveFieldValue(statusAttName, "VARCHAR", rs));

		String interpretationAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"interpretation", "_");

		this.interpretation.setValue(BaseVO.retrieveFieldValue(interpretationAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO obs(`person_id`, `concept_id`, `encounter_id`, `order_id`, `obs_datetime`, `location_id`, `accession_number`, `value_group_id`, `value_coded`, `value_coded_name_id`, `value_drug`, `value_datetime`, `value_numeric`, `value_modifier`, `value_text`, `comments`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `value_complex`, `uuid`, `form_namespace_and_path`, `status`, `interpretation`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO obs(`obs_id`, `person_id`, `concept_id`, `encounter_id`, `order_id`, `obs_datetime`, `location_id`, `accession_number`, `value_group_id`, `value_coded`, `value_coded_name_id`, `value_drug`, `value_datetime`, `value_numeric`, `value_modifier`, `value_text`, `comments`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `value_complex`, `uuid`, `form_namespace_and_path`, `status`, `interpretation`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.personId.getValue(), this.conceptId.getValue(), this.encounterId.getValue(),
				this.orderId.getValue(), this.obsDatetime.getValue(), this.locationId.getValue(),
				this.accessionNumber.getValue(), this.valueGroupId.getValue(), this.valueCoded.getValue(),
				this.valueCodedNameId.getValue(), this.valueDrug.getValue(), this.valueDatetime.getValue(),
				this.valueNumeric.getValue(), this.valueModifier.getValue(), this.valueText.getValue(),
				this.comments.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.valueComplex.getValue(),
				this.uuid, this.formNamespaceAndPath.getValue(), this.status.getValue(),
				this.interpretation.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.obsId.getValue(), this.personId.getValue(), this.conceptId.getValue(),
				this.encounterId.getValue(), this.orderId.getValue(), this.obsDatetime.getValue(),
				this.locationId.getValue(), this.accessionNumber.getValue(), this.valueGroupId.getValue(),
				this.valueCoded.getValue(), this.valueCodedNameId.getValue(), this.valueDrug.getValue(),
				this.valueDatetime.getValue(), this.valueNumeric.getValue(), this.valueModifier.getValue(),
				this.valueText.getValue(), this.comments.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.valueComplex.getValue(), this.uuid, this.formNamespaceAndPath.getValue(), this.status.getValue(),
				this.interpretation.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.obsId.getValue(), this.personId.getValue(), this.conceptId.getValue(),
				this.encounterId.getValue(), this.orderId.getValue(), this.obsDatetime.getValue(),
				this.locationId.getValue(), this.accessionNumber.getValue(), this.valueGroupId.getValue(),
				this.valueCoded.getValue(), this.valueCodedNameId.getValue(), this.valueDrug.getValue(),
				this.valueDatetime.getValue(), this.valueNumeric.getValue(), this.valueModifier.getValue(),
				this.valueText.getValue(), this.comments.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.valueComplex.getValue(), this.uuid, this.formNamespaceAndPath.getValue(), this.status.getValue(),
				this.interpretation.getValue(), this.obsId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE obs SET `obs_id` = ?, `person_id` = ?, `concept_id` = ?, `encounter_id` = ?, `order_id` = ?, `obs_datetime` = ?, `location_id` = ?, `accession_number` = ?, `value_group_id` = ?, `value_coded` = ?, `value_coded_name_id` = ?, `value_drug` = ?, `value_datetime` = ?, `value_numeric` = ?, `value_modifier` = ?, `value_text` = ?, `comments` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `value_complex` = ?, `uuid` = ?, `form_namespace_and_path` = ?, `status` = ?, `interpretation` = ? WHERE saude_reprodutiva_obs_dst_ds.obs_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.personId.getValue()) + "," + (this.conceptId.getValue()) + "," + (this.encounterId.getValue())
				+ "," + (this.orderId.getValue()) + ","
				+ (this.obsDatetime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.obsDatetime.getValue())
						+ "\"" : null)
				+ "," + (this.locationId.getValue()) + ","
				+ (this.accessionNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.accessionNumber.getValue().toString()) + "\""
						: null)
				+ "," + (this.valueGroupId.getValue()) + "," + (this.valueCoded.getValue()) + ","
				+ (this.valueCodedNameId.getValue()) + "," + (this.valueDrug.getValue()) + ","
				+ (this.valueDatetime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.valueDatetime.getValue())
						+ "\"" : null)
				+ "," + (this.valueNumeric.getValue()) + ","
				+ (this.valueModifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueModifier.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.valueText.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueText.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.comments.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.comments.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
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
				+ ","
				+ (this.valueComplex.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueComplex.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ ","
				+ (this.formNamespaceAndPath.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formNamespaceAndPath.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.status.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.status.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.interpretation.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.interpretation.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.obsId.getValue()) + "," + (this.personId.getValue()) + "," + (this.conceptId.getValue()) + ","
				+ (this.encounterId.getValue()) + "," + (this.orderId.getValue()) + ","
				+ (this.obsDatetime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.obsDatetime.getValue())
						+ "\"" : null)
				+ "," + (this.locationId.getValue()) + ","
				+ (this.accessionNumber.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.accessionNumber.getValue().toString()) + "\""
						: null)
				+ "," + (this.valueGroupId.getValue()) + "," + (this.valueCoded.getValue()) + ","
				+ (this.valueCodedNameId.getValue()) + "," + (this.valueDrug.getValue()) + ","
				+ (this.valueDatetime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.valueDatetime.getValue())
						+ "\"" : null)
				+ "," + (this.valueNumeric.getValue()) + ","
				+ (this.valueModifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueModifier.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.valueText.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueText.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.comments.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.comments.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
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
				+ ","
				+ (this.valueComplex.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueComplex.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ ","
				+ (this.formNamespaceAndPath.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formNamespaceAndPath.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.status.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.status.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.interpretation.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.interpretation.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		ObsVO copy = new ObsVO();

		copy.obsId = copyGeneratedField(this.obsId);
		copy.personId = copyGeneratedField(this.personId);
		copy.conceptId = copyGeneratedField(this.conceptId);
		copy.encounterId = copyGeneratedField(this.encounterId);
		copy.orderId = copyGeneratedField(this.orderId);
		copy.obsDatetime = copyGeneratedField(this.obsDatetime);
		copy.locationId = copyGeneratedField(this.locationId);
		copy.accessionNumber = copyGeneratedField(this.accessionNumber);
		copy.valueGroupId = copyGeneratedField(this.valueGroupId);
		copy.valueCoded = copyGeneratedField(this.valueCoded);
		copy.valueCodedNameId = copyGeneratedField(this.valueCodedNameId);
		copy.valueDrug = copyGeneratedField(this.valueDrug);
		copy.valueDatetime = copyGeneratedField(this.valueDatetime);
		copy.valueNumeric = copyGeneratedField(this.valueNumeric);
		copy.valueModifier = copyGeneratedField(this.valueModifier);
		copy.valueText = copyGeneratedField(this.valueText);
		copy.comments = copyGeneratedField(this.comments);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.voided = copyGeneratedField(this.voided);
		copy.voidedBy = copyGeneratedField(this.voidedBy);
		copy.dateVoided = this.dateVoided;
		copy.voidReason = copyGeneratedField(this.voidReason);
		copy.valueComplex = copyGeneratedField(this.valueComplex);
		copy.uuid = this.uuid;
		copy.formNamespaceAndPath = copyGeneratedField(this.formNamespaceAndPath);
		copy.status = copyGeneratedField(this.status);

		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.valueCoded.getValue() != null)
			return true;

		if (this.conceptId.getValue() != null)
			return true;

		if (this.valueCodedNameId.getValue() != null)
			return true;

		if (this.valueDrug.getValue() != null)
			return true;

		if (this.encounterId.getValue() != null)
			return true;

		if (this.locationId.getValue() != null)
			return true;

		if (this.orderId.getValue() != null)
			return true;

		if (this.personId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("valueCoded"))
			return this.valueCoded.getValue();
		if (parentAttName.equals("conceptId"))
			return this.conceptId.getValue();
		if (parentAttName.equals("valueCodedNameId"))
			return this.valueCodedNameId.getValue();
		if (parentAttName.equals("valueDrug"))
			return this.valueDrug.getValue();
		if (parentAttName.equals("encounterId"))
			return this.encounterId.getValue();
		if (parentAttName.equals("locationId"))
			return this.locationId.getValue();
		if (parentAttName.equals("orderId"))
			return this.orderId.getValue();
		if (parentAttName.equals("personId"))
			return this.personId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "obs";
	}

}