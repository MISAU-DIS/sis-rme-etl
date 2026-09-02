package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp._query_result;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrescriptionItemDataSrcDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field valueDrug = Field.fastCreateWithType("value_drug", "null");
	private Field formulacaoConceptId = Field.fastCreateWithType("formulacao_concept_id", "null");
	private Field packageDatetime = Field.fastCreateWithType("package_datetime", "null");
	private Field posologia = Field.fastCreateWithType("posologia", "null");
	private Field specifiedQty = Field.fastCreateWithType("specified_qty", "null");
	private Field calculatedQty = Field.fastCreateWithType("calculated_qty", "null");

	public PrescriptionItemDataSrcDsQueryResultVO() {
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

	public void setValueDrug(Field valueDrug) {
		this.valueDrug = valueDrug;
	}

	public void setValueDrugValue(String value) {
		this.valueDrug.setValue(value);
	}

	public Field getValueDrug() {
		return this.valueDrug;
	}

	public void setFormulacaoConceptId(Field formulacaoConceptId) {
		this.formulacaoConceptId = formulacaoConceptId;
	}

	public void setFormulacaoConceptIdValue(String value) {
		this.formulacaoConceptId.setValue(value);
	}

	public Field getFormulacaoConceptId() {
		return this.formulacaoConceptId;
	}

	public void setPackageDatetime(Field packageDatetime) {
		this.packageDatetime = packageDatetime;
	}

	public void setPackageDatetimeValue(String value) {
		this.packageDatetime.setValue(value);
	}

	public Field getPackageDatetime() {
		return this.packageDatetime;
	}

	public void setPosologia(Field posologia) {
		this.posologia = posologia;
	}

	public void setPosologiaValue(String value) {
		this.posologia.setValue(value);
	}

	public Field getPosologia() {
		return this.posologia;
	}

	public void setSpecifiedQty(Field specifiedQty) {
		this.specifiedQty = specifiedQty;
	}

	public void setSpecifiedQtyValue(String value) {
		this.specifiedQty.setValue(value);
	}

	public Field getSpecifiedQty() {
		return this.specifiedQty;
	}

	public void setCalculatedQty(Field calculatedQty) {
		this.calculatedQty = calculatedQty;
	}

	public void setCalculatedQtyValue(String value) {
		this.calculatedQty.setValue(value);
	}

	public Field getCalculatedQty() {
		return this.calculatedQty;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String valueDrugAttName = "value_drug";

		this.valueDrug.setValue(BaseVO.retrieveFieldValue(valueDrugAttName, "null", rs));

		String formulacaoConceptIdAttName = "formulacao_concept_id";

		this.formulacaoConceptId.setValue(BaseVO.retrieveFieldValue(formulacaoConceptIdAttName, "null", rs));

		String packageDatetimeAttName = "package_datetime";

		this.packageDatetime.setValue(BaseVO.retrieveFieldValue(packageDatetimeAttName, "null", rs));

		String posologiaAttName = "posologia";

		this.posologia.setValue(BaseVO.retrieveFieldValue(posologiaAttName, "null", rs));

		String specifiedQtyAttName = "specified_qty";

		this.specifiedQty.setValue(BaseVO.retrieveFieldValue(specifiedQtyAttName, "null", rs));

		String calculatedQtyAttName = "calculated_qty";

		this.calculatedQty.setValue(BaseVO.retrieveFieldValue(calculatedQtyAttName, "null", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO prescription_item_data_src_ds(`value_drug`, `formulacao_concept_id`, `package_datetime`, `posologia`, `specified_qty`, `calculated_qty`) VALUES( ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO prescription_item_data_src_ds(`value_drug`, `formulacao_concept_id`, `package_datetime`, `posologia`, `specified_qty`, `calculated_qty`) VALUES( ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.valueDrug.getValue(), this.formulacaoConceptId.getValue(),
				this.packageDatetime.getValue(), this.posologia.getValue(), this.specifiedQty.getValue(),
				this.calculatedQty.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.valueDrug.getValue(), this.formulacaoConceptId.getValue(),
				this.packageDatetime.getValue(), this.posologia.getValue(), this.specifiedQty.getValue(),
				this.calculatedQty.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		throw new RuntimeException("Impossible auto update command! No primary key is defined for table object!");
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		throw new RuntimeException("Impossible auto update command! No primary key is defined for table object!");
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.valueDrug.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueDrug.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.formulacaoConceptId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formulacaoConceptId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.packageDatetime.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.packageDatetime.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.posologia.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.posologia.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.specifiedQty.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.specifiedQty.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.calculatedQty.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.calculatedQty.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return ""
				+ (this.valueDrug.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.valueDrug.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.formulacaoConceptId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.formulacaoConceptId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.packageDatetime.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.packageDatetime.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.posologia.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.posologia.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.specifiedQty.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.specifiedQty.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.calculatedQty.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.calculatedQty.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PrescriptionItemDataSrcDsQueryResultVO copy = new PrescriptionItemDataSrcDsQueryResultVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "prescription_item_data_src_ds";
	}

}