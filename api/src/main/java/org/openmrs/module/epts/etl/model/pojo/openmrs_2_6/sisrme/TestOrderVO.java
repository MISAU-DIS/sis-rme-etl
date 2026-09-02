package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;


import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.model.base.BaseVO;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class TestOrderVO extends AbstractGeneratedDatabaseObject {
	private Field orderId = Field.fastCreateWithType("order_id", "INT");
	private Field specimenSource = Field.fastCreateWithType("specimen_source", "INT");
	private Field laterality = Field.fastCreateWithType("laterality", "VARCHAR");
	private Field clinicalHistory = Field.fastCreateWithType("clinical_history", "TEXT");
	private Field frequency = Field.fastCreateWithType("frequency", "INT");
	private Field numberOfRepeats = Field.fastCreateWithType("number_of_repeats", "INT");
	private Field conceptId = Field.fastCreateWithType("concept_id", "BIGINT");
	private Field ordersId = Field.fastCreateWithType("orders_id", "BIGINT");

	public TestOrderVO() {
		this.metadata = false;
		setSharedPkObj(new org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.OrdersVO());

		this.fields.add(this.orderId);
		this.fields.add(this.specimenSource);
		this.fields.add(this.laterality);
		this.fields.add(this.clinicalHistory);
		this.fields.add(this.frequency);
		this.fields.add(this.numberOfRepeats);
		this.fields.add(this.conceptId);
		this.fields.add(this.ordersId);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "order_id")) {
			this.orderId.setValue(k.getValue());
		}
	}

	@JsonIgnore
	@Override
	public org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.OrdersVO getSharedPkObj() {
		return (org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.OrdersVO) super.getSharedPkObj();
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "order_id")) {
			return this.orderId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "specimen_source")) {
			return this.specimenSource.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "laterality")) {
			return this.laterality.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "clinical_history")) {
			return this.clinicalHistory.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "frequency")) {
			return this.frequency.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "number_of_repeats")) {
			return this.numberOfRepeats.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "concept_id")) {
			return this.conceptId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "orders_id")) {
			return this.ordersId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_created")) return this.dateCreated;
		if (utilities.equalsFieldsName(fieldName, "date_changed")) return this.dateChanged;
		if (utilities.equalsFieldsName(fieldName, "date_voided")) return this.dateVoided;
		if (utilities.equalsFieldsName(fieldName, "uuid")) return this.uuid;
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

	public void setOrderId(Field orderId) {
		this.orderId = orderId;
	}

	public void setOrderIdValue(Integer value) {
		this.orderId.setValue(value);
	}

	public Field getOrderId() {
		return this.orderId;
	}

	public void setSpecimenSource(Field specimenSource) {
		this.specimenSource = specimenSource;
	}

	public void setSpecimenSourceValue(Integer value) {
		this.specimenSource.setValue(value);
	}

	public Field getSpecimenSource() {
		return this.specimenSource;
	}

	public void setLaterality(Field laterality) {
		this.laterality = laterality;
	}

	public void setLateralityValue(String value) {
		this.laterality.setValue(value);
	}

	public Field getLaterality() {
		return this.laterality;
	}

	public void setClinicalHistory(Field clinicalHistory) {
		this.clinicalHistory = clinicalHistory;
	}

	public void setClinicalHistoryValue(String value) {
		this.clinicalHistory.setValue(value);
	}

	public Field getClinicalHistory() {
		return this.clinicalHistory;
	}

	public void setFrequency(Field frequency) {
		this.frequency = frequency;
	}

	public void setFrequencyValue(Integer value) {
		this.frequency.setValue(value);
	}

	public Field getFrequency() {
		return this.frequency;
	}

	public void setNumberOfRepeats(Field numberOfRepeats) {
		this.numberOfRepeats = numberOfRepeats;
	}

	public void setNumberOfRepeatsValue(Integer value) {
		this.numberOfRepeats.setValue(value);
	}

	public Field getNumberOfRepeats() {
		return this.numberOfRepeats;
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

	public void setOrdersId(Field ordersId) {
		this.ordersId = ordersId;
	}

	public void setOrdersIdValue(Integer value) {
		this.ordersId.setValue(value);
	}

	public Field getOrdersId() {
		return this.ordersId;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		if (!hasRelatedConfiguration())
			throw new org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException(
					"The relatedConfiguration is not set");
		if (!getSharedPkObj().isLoadedFromDb())
			getSharedPkObj().load(rs);
		super.load(rs);

		String orderIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_id", "_");

		this.orderId.setValue(BaseVO.retrieveFieldValue(orderIdAttName, "INT", rs));

		String specimenSourceAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"specimen_source", "_");

		this.specimenSource.setValue(BaseVO.retrieveFieldValue(specimenSourceAttName, "INT", rs));

		String lateralityAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"laterality", "_");

		this.laterality.setValue(BaseVO.retrieveFieldValue(lateralityAttName, "VARCHAR", rs));

		String clinicalHistoryAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"clinical_history", "_");

		this.clinicalHistory.setValue(BaseVO.retrieveFieldValue(clinicalHistoryAttName, "TEXT", rs));

		String frequencyAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"frequency", "_");

		this.frequency.setValue(BaseVO.retrieveFieldValue(frequencyAttName, "INT", rs));

		String numberOfRepeatsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"number_of_repeats", "_");

		this.numberOfRepeats.setValue(BaseVO.retrieveFieldValue(numberOfRepeatsAttName, "INT", rs));

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "BIGINT", rs));

		String ordersIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"orders_id", "_");

		this.ordersId.setValue(BaseVO.retrieveFieldValue(ordersIdAttName, "BIGINT", rs));

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
		return "INSERT INTO test_order(`specimen_source`, `laterality`, `clinical_history`, `frequency`, `number_of_repeats`, `concept_id`, `orders_id`) VALUES( ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO test_order(`order_id`, `specimen_source`, `laterality`, `clinical_history`, `frequency`, `number_of_repeats`, `concept_id`, `orders_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.specimenSource.getValue(), this.laterality.getValue(), this.clinicalHistory.getValue(),
				this.frequency.getValue(), this.numberOfRepeats.getValue(), this.conceptId.getValue(),
				this.ordersId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.orderId.getValue(), this.specimenSource.getValue(), this.laterality.getValue(),
				this.clinicalHistory.getValue(), this.frequency.getValue(), this.numberOfRepeats.getValue(),
				this.conceptId.getValue(), this.ordersId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.orderId.getValue(), this.specimenSource.getValue(), this.laterality.getValue(),
				this.clinicalHistory.getValue(), this.frequency.getValue(), this.numberOfRepeats.getValue(),
				this.conceptId.getValue(), this.ordersId.getValue(), this.orderId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE test_order SET `order_id` = ?, `specimen_source` = ?, `laterality` = ?, `clinical_history` = ?, `frequency` = ?, `number_of_repeats` = ?, `concept_id` = ?, `orders_id` = ? WHERE order_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.specimenSource.getValue()) + ","
				+ (this.laterality.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.laterality.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.clinicalHistory.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.clinicalHistory.getValue().toString()) + "\""
						: null)
				+ "," + (this.frequency.getValue()) + "," + (this.numberOfRepeats.getValue()) + ","
				+ (this.conceptId.getValue()) + "," + (this.ordersId.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.orderId.getValue()) + "," + (this.specimenSource.getValue()) + ","
				+ (this.laterality.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.laterality.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.clinicalHistory.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.clinicalHistory.getValue().toString()) + "\""
						: null)
				+ "," + (this.frequency.getValue()) + "," + (this.numberOfRepeats.getValue()) + ","
				+ (this.conceptId.getValue()) + "," + (this.ordersId.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		TestOrderVO copy = new TestOrderVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.specimenSource.getValue() != null)
			return true;

		if (this.frequency.getValue() != null)
			return true;

		if (this.orderId.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("specimenSource"))
			return this.specimenSource.getValue();
		if (parentAttName.equals("frequency"))
			return this.frequency.getValue();
		if (parentAttName.equals("orderId"))
			return this.orderId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "test_order";
	}

}