package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp._query_result;

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

public class TarvStartDateSrcDsQueryResultVO extends AbstractGeneratedDatabaseObject {
	private Field patientId = Field.fastCreateWithType("patient_id", "null");
	private Field dataInicioTarv = Field.fastCreateWithType("data_inicio_tarv", "null");

	public TarvStartDateSrcDsQueryResultVO() {
		this.metadata = false;
		this.fields.add(this.patientId);
		this.fields.add(this.dataInicioTarv);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "data_inicio_tarv")) {
			return this.dataInicioTarv.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			this.patientId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "data_inicio_tarv")) {
			this.dataInicioTarv.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		super.setFieldValue(fieldName, value);
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
		loadGeneratedFieldWithDefaultValue(this.patientId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dataInicioTarv, srcConn, dstConn);
	}

	public void setPatientId(Field patientId) {
		this.patientId = patientId;
	}

	public void setPatientIdValue(String value) {
		this.patientId.setValue(value);
	}

	public Field getPatientId() {
		return this.patientId;
	}

	public void setDataInicioTarv(Field dataInicioTarv) {
		this.dataInicioTarv = dataInicioTarv;
	}

	public void setDataInicioTarvValue(String value) {
		this.dataInicioTarv.setValue(value);
	}

	public Field getDataInicioTarv() {
		return this.dataInicioTarv;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String patientIdAttName = "patient_id";

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "null", rs));

		String dataInicioTarvAttName = "data_inicio_tarv";

		this.dataInicioTarv.setValue(BaseVO.retrieveFieldValue(dataInicioTarvAttName, "null", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO tarv_start_date_src_ds(`patient_id`, `data_inicio_tarv`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO tarv_start_date_src_ds(`patient_id`, `data_inicio_tarv`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.patientId.getValue(), this.dataInicioTarv.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.patientId.getValue(), this.dataInicioTarv.getValue() };
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
		return ""
				+ (this.patientId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.patientId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dataInicioTarv.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.dataInicioTarv.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return ""
				+ (this.patientId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.patientId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dataInicioTarv.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.dataInicioTarv.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		TarvStartDateSrcDsQueryResultVO copy = new TarvStartDateSrcDsQueryResultVO();
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
		return "tarv_start_date_src_ds";
	}

}