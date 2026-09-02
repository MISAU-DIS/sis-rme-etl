package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp._query_result;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrescriptionSisrmeDrugDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field drugId = Field.fastCreateWithType("drug_id", "VARCHAR");
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PrescriptionSisrmeDrugDsQueryResultVO() {
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

	public void setDrugId(Field drugId) {
		this.drugId = drugId;
	}

	public void setDrugIdValue(String value) {
		this.drugId.setValue(value);
	}

	public Field getDrugId() {
		return this.drugId;
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

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String drugIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"drug_id", "_");

		this.drugId.setValue(BaseVO.retrieveFieldValue(drugIdAttName, "VARCHAR", rs));

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO prescription_sisrme_drug_ds(`drug_id`, `concept_id`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO prescription_sisrme_drug_ds(`drug_id`, `concept_id`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.drugId.getValue(), this.conceptId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.drugId.getValue(), this.conceptId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?";
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
		return "" + (this.drugId.getValue() != null
				? "\"" + utilities.scapeQuotationMarks(this.drugId.getValue().toString()) + "\""
				: null) + "," + (this.conceptId.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.drugId.getValue() != null
				? "\"" + utilities.scapeQuotationMarks(this.drugId.getValue().toString()) + "\""
				: null) + "," + (this.conceptId.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PrescriptionSisrmeDrugDsQueryResultVO copy = new PrescriptionSisrmeDrugDsQueryResultVO();

		copy.drugId = copyGeneratedField(this.drugId);

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
		return "prescription_sisrme_drug_ds";
	}

}