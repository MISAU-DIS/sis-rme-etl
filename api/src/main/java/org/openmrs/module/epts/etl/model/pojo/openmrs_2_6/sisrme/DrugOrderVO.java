package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class DrugOrderVO extends AbstractGeneratedDatabaseObject {
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
		setSharedPkObj(new org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.OrdersVO());
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
		if (!hasRelatedConfiguration())
			throw new org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException(
					"The relatedConfiguration is not set");
		if (!getSharedPkObj().isLoadedFromDb())
			getSharedPkObj().load(rs);
		super.load(rs);

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