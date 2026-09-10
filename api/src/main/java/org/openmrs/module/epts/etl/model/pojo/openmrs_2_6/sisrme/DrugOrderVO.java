package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.conf.Key;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DrugOrderVO extends AbstractGeneratedDatabaseObject {
	private Field drugInventoryId = Field.fastCreateWithType("drug_inventory_id", "INT");
	private Field dosingType = Field.fastCreateWithType("dosing_type", "VARCHAR");
	private Field asNeededCondition = Field.fastCreateWithType("as_needed_condition", "VARCHAR");
	private Field numRefills = Field.fastCreateWithType("num_refills", "INT");
	private Field dosingInstructions = Field.fastCreateWithType("dosing_instructions", "TEXT");
	private Field duration = Field.fastCreateWithType("duration", "INT");
	private Field durationUnits = Field.fastCreateWithType("duration_units", "INT");
	private Field quantityUnits = Field.fastCreateWithType("quantity_units", "INT");
	private Field route = Field.fastCreateWithType("route", "INT");
	private Field doseUnits = Field.fastCreateWithType("dose_units", "INT");
	private Field brandName = Field.fastCreateWithType("brand_name", "VARCHAR");
	private Field dispenseAsWritten = Field.fastCreateWithType("dispense_as_written", "BIT");
	private Field equivalentDailyDose = Field.fastCreateWithType("equivalent_daily_dose", "DOUBLE");
	private Field ordersId = Field.fastCreateWithType("orders_id", "BIGINT");
	private Field units = Field.fastCreateWithType("units", "VARCHAR");
	private Field drugNonCoded = Field.fastCreateWithType("drug_non_coded", "VARCHAR");
	private Field orderId = Field.fastCreateWithType("order_id", "INT");
	private Field dose = Field.fastCreateWithType("dose", "DOUBLE");
	private Field asNeeded = Field.fastCreateWithType("as_needed", "SMALLINT");
	private Field quantity = Field.fastCreateWithType("quantity", "DOUBLE");
	private Field frequency = Field.fastCreateWithType("frequency", "INT");
	private Field complex = Field.fastCreateWithType("complex", "BIT");
	private Field drugId = Field.fastCreateWithType("drug_id", "BIGINT");
	private Field prn = Field.fastCreateWithType("prn", "BIT");

	public DrugOrderVO() {
		this.metadata = false;
		this.fields.add(this.orderId);
		this.fields.add(this.drugInventoryId);
		this.fields.add(this.dose);
		this.fields.add(this.asNeeded);
		this.fields.add(this.dosingType);
		this.fields.add(this.quantity);
		this.fields.add(this.asNeededCondition);
		this.fields.add(this.numRefills);
		this.fields.add(this.dosingInstructions);
		this.fields.add(this.duration);
		this.fields.add(this.durationUnits);
		this.fields.add(this.quantityUnits);
		this.fields.add(this.route);
		this.fields.add(this.doseUnits);
		this.fields.add(this.frequency);
		this.fields.add(this.brandName);
		this.fields.add(this.dispenseAsWritten);
		this.fields.add(this.complex);
		this.fields.add(this.drugId);
		this.fields.add(this.equivalentDailyDose);
		this.fields.add(this.ordersId);
		this.fields.add(this.prn);
		this.fields.add(this.units);
		this.fields.add(this.drugNonCoded);
		setSharedPkObj(new org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.OrdersVO());
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "order_id")) {
			this.orderId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "order_id")) {
			return this.orderId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "drug_inventory_id")) {
			return this.drugInventoryId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dose")) {
			return this.dose.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "as_needed")) {
			return this.asNeeded.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dosing_type")) {
			return this.dosingType.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "quantity")) {
			return this.quantity.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "as_needed_condition")) {
			return this.asNeededCondition.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "num_refills")) {
			return this.numRefills.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dosing_instructions")) {
			return this.dosingInstructions.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "duration")) {
			return this.duration.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "duration_units")) {
			return this.durationUnits.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "quantity_units")) {
			return this.quantityUnits.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "route")) {
			return this.route.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dose_units")) {
			return this.doseUnits.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "frequency")) {
			return this.frequency.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "brand_name")) {
			return this.brandName.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dispense_as_written")) {
			return this.dispenseAsWritten.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "complex")) {
			return this.complex.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "drug_id")) {
			return this.drugId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "equivalent_daily_dose")) {
			return this.equivalentDailyDose.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "orders_id")) {
			return this.ordersId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "prn")) {
			return this.prn.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "units")) {
			return this.units.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "drug_non_coded")) {
			return this.drugNonCoded.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "order_id")) {
			this.orderId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "drug_inventory_id")) {
			this.drugInventoryId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "dose")) {
			this.dose.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "as_needed")) {
			this.asNeeded.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "dosing_type")) {
			this.dosingType.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "quantity")) {
			this.quantity.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "as_needed_condition")) {
			this.asNeededCondition.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "num_refills")) {
			this.numRefills.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "dosing_instructions")) {
			this.dosingInstructions.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "duration")) {
			this.duration.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "duration_units")) {
			this.durationUnits.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "quantity_units")) {
			this.quantityUnits.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "route")) {
			this.route.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "dose_units")) {
			this.doseUnits.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "frequency")) {
			this.frequency.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "brand_name")) {
			this.brandName.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "dispense_as_written")) {
			this.dispenseAsWritten.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "complex")) {
			this.complex.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "drug_id")) {
			this.drugId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "equivalent_daily_dose")) {
			this.equivalentDailyDose.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "orders_id")) {
			this.ordersId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "prn")) {
			this.prn.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "units")) {
			this.units.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "drug_non_coded")) {
			this.drugNonCoded.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		super.setFieldValue(fieldName, value);
	}

	@JsonIgnore
	@Override
	public org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.OrdersVO getSharedPkObj() {
		return (org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.OrdersVO) super.getSharedPkObj();
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

	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) throws DBException {
		super.loadWithDefaultValues(srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.orderId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.drugInventoryId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dose, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.asNeeded, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dosingType, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.quantity, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.asNeededCondition, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.numRefills, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dosingInstructions, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.duration, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.durationUnits, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.quantityUnits, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.route, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.doseUnits, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.frequency, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.brandName, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dispenseAsWritten, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.complex, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.drugId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.equivalentDailyDose, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.ordersId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.prn, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.units, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.drugNonCoded, srcConn, dstConn);
	}

	public void setDrugInventoryId(Field drugInventoryId) {
		this.drugInventoryId = drugInventoryId;
	}

	public void setDrugInventoryIdValue(Integer value) {
		this.drugInventoryId.setValue(value);
	}

	public Field getDrugInventoryId() {
		return this.drugInventoryId;
	}

	public void setDosingType(Field dosingType) {
		this.dosingType = dosingType;
	}

	public void setDosingTypeValue(String value) {
		this.dosingType.setValue(value);
	}

	public Field getDosingType() {
		return this.dosingType;
	}

	public void setAsNeededCondition(Field asNeededCondition) {
		this.asNeededCondition = asNeededCondition;
	}

	public void setAsNeededConditionValue(String value) {
		this.asNeededCondition.setValue(value);
	}

	public Field getAsNeededCondition() {
		return this.asNeededCondition;
	}

	public void setNumRefills(Field numRefills) {
		this.numRefills = numRefills;
	}

	public void setNumRefillsValue(Integer value) {
		this.numRefills.setValue(value);
	}

	public Field getNumRefills() {
		return this.numRefills;
	}

	public void setDosingInstructions(Field dosingInstructions) {
		this.dosingInstructions = dosingInstructions;
	}

	public void setDosingInstructionsValue(String value) {
		this.dosingInstructions.setValue(value);
	}

	public Field getDosingInstructions() {
		return this.dosingInstructions;
	}

	public void setDuration(Field duration) {
		this.duration = duration;
	}

	public void setDurationValue(Integer value) {
		this.duration.setValue(value);
	}

	public Field getDuration() {
		return this.duration;
	}

	public void setDurationUnits(Field durationUnits) {
		this.durationUnits = durationUnits;
	}

	public void setDurationUnitsValue(Integer value) {
		this.durationUnits.setValue(value);
	}

	public Field getDurationUnits() {
		return this.durationUnits;
	}

	public void setQuantityUnits(Field quantityUnits) {
		this.quantityUnits = quantityUnits;
	}

	public void setQuantityUnitsValue(Integer value) {
		this.quantityUnits.setValue(value);
	}

	public Field getQuantityUnits() {
		return this.quantityUnits;
	}

	public void setRoute(Field route) {
		this.route = route;
	}

	public void setRouteValue(Integer value) {
		this.route.setValue(value);
	}

	public Field getRoute() {
		return this.route;
	}

	public void setDoseUnits(Field doseUnits) {
		this.doseUnits = doseUnits;
	}

	public void setDoseUnitsValue(Integer value) {
		this.doseUnits.setValue(value);
	}

	public Field getDoseUnits() {
		return this.doseUnits;
	}

	public void setBrandName(Field brandName) {
		this.brandName = brandName;
	}

	public void setBrandNameValue(String value) {
		this.brandName.setValue(value);
	}

	public Field getBrandName() {
		return this.brandName;
	}

	public void setDispenseAsWritten(Field dispenseAsWritten) {
		this.dispenseAsWritten = dispenseAsWritten;
	}

	public void setDispenseAsWrittenValue(Boolean value) {
		this.dispenseAsWritten.setValue(value);
	}

	public Field getDispenseAsWritten() {
		return this.dispenseAsWritten;
	}

	public void setEquivalentDailyDose(Field equivalentDailyDose) {
		this.equivalentDailyDose = equivalentDailyDose;
	}

	public void setEquivalentDailyDoseValue(Double value) {
		this.equivalentDailyDose.setValue(value);
	}

	public Field getEquivalentDailyDose() {
		return this.equivalentDailyDose;
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

	public void setUnits(Field units) {
		this.units = units;
	}

	public void setUnitsValue(String value) {
		this.units.setValue(value);
	}

	public Field getUnits() {
		return this.units;
	}

	public void setDrugNonCoded(Field drugNonCoded) {
		this.drugNonCoded = drugNonCoded;
	}

	public void setDrugNonCodedValue(String value) {
		this.drugNonCoded.setValue(value);
	}

	public Field getDrugNonCoded() {
		return this.drugNonCoded;
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

	public void setDose(Field dose) {
		this.dose = dose;
	}

	public void setDoseValue(Double value) {
		this.dose.setValue(value);
	}

	public Field getDose() {
		return this.dose;
	}

	public void setAsNeeded(Field asNeeded) {
		this.asNeeded = asNeeded;
	}

	public void setAsNeededValue(Short value) {
		this.asNeeded.setValue(value);
	}

	public Field getAsNeeded() {
		return this.asNeeded;
	}

	public void setQuantity(Field quantity) {
		this.quantity = quantity;
	}

	public void setQuantityValue(Double value) {
		this.quantity.setValue(value);
	}

	public Field getQuantity() {
		return this.quantity;
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

	public void setComplex(Field complex) {
		this.complex = complex;
	}

	public void setComplexValue(Boolean value) {
		this.complex.setValue(value);
	}

	public Field getComplex() {
		return this.complex;
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

	public void setPrn(Field prn) {
		this.prn = prn;
	}

	public void setPrnValue(Boolean value) {
		this.prn.setValue(value);
	}

	public Field getPrn() {
		return this.prn;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		if (!hasRelatedConfiguration())
			throw new org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException(
					"The relatedConfiguration is not set");
		if (!getSharedPkObj().isLoadedFromDb())
			getSharedPkObj().load(rs);
		if (getRelatedConfiguration().containsField("drug_inventory_id")) {
			String drugInventoryIdAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "drug_inventory_id", "_");

			this.drugInventoryId.setValue(BaseVO.retrieveFieldValue(drugInventoryIdAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("dosing_type")) {
			String dosingTypeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"dosing_type", "_");

			this.dosingType.setValue(BaseVO.retrieveFieldValue(dosingTypeAttName, "VARCHAR", rs));
		}

		if (getRelatedConfiguration().containsField("as_needed_condition")) {
			String asNeededConditionAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "as_needed_condition", "_");

			this.asNeededCondition.setValue(BaseVO.retrieveFieldValue(asNeededConditionAttName, "VARCHAR", rs));
		}

		if (getRelatedConfiguration().containsField("num_refills")) {
			String numRefillsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"num_refills", "_");

			this.numRefills.setValue(BaseVO.retrieveFieldValue(numRefillsAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("dosing_instructions")) {
			String dosingInstructionsAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "dosing_instructions", "_");

			this.dosingInstructions.setValue(BaseVO.retrieveFieldValue(dosingInstructionsAttName, "TEXT", rs));
		}

		if (getRelatedConfiguration().containsField("duration")) {
			String durationAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"duration", "_");

			this.duration.setValue(BaseVO.retrieveFieldValue(durationAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("duration_units")) {
			String durationUnitsAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "duration_units", "_");

			this.durationUnits.setValue(BaseVO.retrieveFieldValue(durationUnitsAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("quantity_units")) {
			String quantityUnitsAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "quantity_units", "_");

			this.quantityUnits.setValue(BaseVO.retrieveFieldValue(quantityUnitsAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("route")) {
			String routeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"route", "_");

			this.route.setValue(BaseVO.retrieveFieldValue(routeAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("dose_units")) {
			String doseUnitsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"dose_units", "_");

			this.doseUnits.setValue(BaseVO.retrieveFieldValue(doseUnitsAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("brand_name")) {
			String brandNameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"brand_name", "_");

			this.brandName.setValue(BaseVO.retrieveFieldValue(brandNameAttName, "VARCHAR", rs));
		}

		if (getRelatedConfiguration().containsField("dispense_as_written")) {
			String dispenseAsWrittenAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "dispense_as_written", "_");

			this.dispenseAsWritten.setValue(BaseVO.retrieveFieldValue(dispenseAsWrittenAttName, "BIT", rs));
		}

		if (getRelatedConfiguration().containsField("equivalent_daily_dose")) {
			String equivalentDailyDoseAttName = utilities.concatStringsWithSeparator(
					this.getRelatedConfiguration().getAlias(), "equivalent_daily_dose", "_");

			this.equivalentDailyDose.setValue(BaseVO.retrieveFieldValue(equivalentDailyDoseAttName, "DOUBLE", rs));
		}

		if (getRelatedConfiguration().containsField("orders_id")) {
			String ordersIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"orders_id", "_");

			this.ordersId.setValue(BaseVO.retrieveFieldValue(ordersIdAttName, "BIGINT", rs));
		}

		if (getRelatedConfiguration().containsField("units")) {
			String unitsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"units", "_");

			this.units.setValue(BaseVO.retrieveFieldValue(unitsAttName, "VARCHAR", rs));
		}

		if (getRelatedConfiguration().containsField("drug_non_coded")) {
			String drugNonCodedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"drug_non_coded", "_");

			this.drugNonCoded.setValue(BaseVO.retrieveFieldValue(drugNonCodedAttName, "VARCHAR", rs));
		}

		String orderIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"order_id", "_");

		this.orderId.setValue(BaseVO.retrieveFieldValue(orderIdAttName, "INT", rs));

		String doseAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "dose",
				"_");

		this.dose.setValue(BaseVO.retrieveFieldValue(doseAttName, "DOUBLE", rs));

		String asNeededAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"as_needed", "_");

		this.asNeeded.setValue(BaseVO.retrieveFieldValue(asNeededAttName, "SMALLINT", rs));

		String quantityAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"quantity", "_");

		this.quantity.setValue(BaseVO.retrieveFieldValue(quantityAttName, "DOUBLE", rs));

		String frequencyAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"frequency", "_");

		this.frequency.setValue(BaseVO.retrieveFieldValue(frequencyAttName, "INT", rs));

		String complexAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"complex", "_");

		this.complex.setValue(BaseVO.retrieveFieldValue(complexAttName, "BIT", rs));

		String drugIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"drug_id", "_");

		this.drugId.setValue(BaseVO.retrieveFieldValue(drugIdAttName, "BIGINT", rs));

		String prnAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "prn", "_");

		this.prn.setValue(BaseVO.retrieveFieldValue(prnAttName, "BIT", rs));

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
		return "INSERT INTO drug_order(`dose`, `as_needed`, `quantity`, `frequency`, `complex`, `drug_id`, `prn`) VALUES( ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO drug_order(`order_id`, `dose`, `as_needed`, `quantity`, `frequency`, `complex`, `drug_id`, `prn`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.dose.getValue(), this.asNeeded.getValue(), this.quantity.getValue(),
				this.frequency.getValue(), this.complex.getValue(), this.drugId.getValue(), this.prn.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.orderId.getValue(), this.dose.getValue(), this.asNeeded.getValue(),
				this.quantity.getValue(), this.frequency.getValue(), this.complex.getValue(), this.drugId.getValue(),
				this.prn.getValue() };
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
		Object[] params = { this.orderId.getValue(), this.dose.getValue(), this.asNeeded.getValue(),
				this.quantity.getValue(), this.frequency.getValue(), this.complex.getValue(), this.drugId.getValue(),
				this.prn.getValue(), this.orderId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE drug_order SET `order_id` = ?, `dose` = ?, `as_needed` = ?, `quantity` = ?, `frequency` = ?, `complex` = ?, `drug_id` = ?, `prn` = ? WHERE order_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.dose.getValue()) + "," + (this.asNeeded.getValue()) + "," + (this.quantity.getValue()) + ","
				+ (this.frequency.getValue()) + ","
				+ (this.complex.getValue() != null ? "\"" + this.complex.getValue() + "\"" : null) + ","
				+ (this.drugId.getValue()) + ","
				+ (this.prn.getValue() != null ? "\"" + this.prn.getValue() + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.orderId.getValue()) + "," + (this.dose.getValue()) + "," + (this.asNeeded.getValue()) + ","
				+ (this.quantity.getValue()) + "," + (this.frequency.getValue()) + ","
				+ (this.complex.getValue() != null ? "\"" + this.complex.getValue() + "\"" : null) + ","
				+ (this.drugId.getValue()) + ","
				+ (this.prn.getValue() != null ? "\"" + this.prn.getValue() + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		DrugOrderVO copy = new DrugOrderVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.frequency.getValue() != null)
			return true;

		if (this.orderId.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("frequency"))
			return this.frequency.getValue();
		if (parentAttName.equals("orderId"))
			return this.orderId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "drug_order";
	}

}