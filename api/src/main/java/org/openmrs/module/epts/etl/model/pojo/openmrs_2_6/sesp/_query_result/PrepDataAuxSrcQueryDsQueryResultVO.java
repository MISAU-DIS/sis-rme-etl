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

public class PrepDataAuxSrcQueryDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field value = Field.fastCreateWithType("value", "BIGINT");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PrepDataAuxSrcQueryDsQueryResultVO() {
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

	public void setValue(Field value) {
		this.value = value;
	}

	public void setValueValue(Integer value) {
		this.value.setValue(value);
	}

	public Field getValue() {
		return this.value;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String valueAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "value",
				"_");

		this.value.setValue(BaseVO.retrieveFieldValue(valueAttName, "BIGINT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO prep_data_aux_src_query_ds(`value`) VALUES( ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO prep_data_aux_src_query_ds(`value`) VALUES( ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.value.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.value.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?";
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
		return "" + (this.value.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.value.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PrepDataAuxSrcQueryDsQueryResultVO copy = new PrepDataAuxSrcQueryDsQueryResultVO();

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
		return "prep_data_aux_src_query_ds";
	}

}