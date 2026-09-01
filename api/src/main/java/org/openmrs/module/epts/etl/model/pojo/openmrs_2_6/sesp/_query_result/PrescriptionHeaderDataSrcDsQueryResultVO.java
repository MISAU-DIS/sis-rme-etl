package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp._query_result;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.base.BaseVO;
import org.openmrs.module.epts.etl.model.pojo.generic.AbstractDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrescriptionHeaderDataSrcDsQueryResultVO extends AbstractDatabaseObject implements EtlDatabaseObject {
	private Field nextPickupDate = Field.fastCreateWithType("next_pickup_date", "DATETIME");
	private Field dispenseModeId = Field.fastCreateWithType("dispense_mode_id", "NULL");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PrescriptionHeaderDataSrcDsQueryResultVO() {
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

	public void setNextPickupDate(Field nextPickupDate) {
		this.nextPickupDate = nextPickupDate;
	}

	public void setNextPickupDateValue(java.util.Date value) {
		this.nextPickupDate.setValue(value);
	}

	public Field getNextPickupDate() {
		return this.nextPickupDate;
	}

	public void setDispenseModeId(Field dispenseModeId) {
		this.dispenseModeId = dispenseModeId;
	}

	public void setDispenseModeIdValue(String value) {
		this.dispenseModeId.setValue(value);
	}

	public Field getDispenseModeId() {
		return this.dispenseModeId;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String nextPickupDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"next_pickup_date", "_");

		this.nextPickupDate.setValue(BaseVO.retrieveFieldValue(nextPickupDateAttName, "DATETIME", rs));

		String dispenseModeIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"dispense_mode_id", "_");

		this.dispenseModeId.setValue(BaseVO.retrieveFieldValue(dispenseModeIdAttName, "NULL", rs));
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO prescription_header_data_src_ds(`next_pickup_date`, `dispense_mode_id`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO prescription_header_data_src_ds(`next_pickup_date`, `dispense_mode_id`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.nextPickupDate.getValue(), this.dispenseModeId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.nextPickupDate.getValue(), this.dispenseModeId.getValue() };
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
				+ (this.nextPickupDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.nextPickupDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dispenseModeId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.dispenseModeId.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return ""
				+ (this.nextPickupDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.nextPickupDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.dispenseModeId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.dispenseModeId.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PrescriptionHeaderDataSrcDsQueryResultVO copy = new PrescriptionHeaderDataSrcDsQueryResultVO();

		copy.nextPickupDate = copyGeneratedField(this.nextPickupDate);

		return copy;
	}

	@JsonIgnore
	@Override
	public void copyFrom(EtlDatabaseObject toCopyFrom) {
		if (toCopyFrom instanceof PrescriptionHeaderDataSrcDsQueryResultVO) {
			PrescriptionHeaderDataSrcDsQueryResultVO toCopyFromAsPrescriptionHeaderDataSrcDsQueryResultVO = (PrescriptionHeaderDataSrcDsQueryResultVO) toCopyFrom;

			this.nextPickupDate = copyGeneratedField(
					toCopyFromAsPrescriptionHeaderDataSrcDsQueryResultVO.nextPickupDate);

		}
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
		return "prescription_header_data_src_ds";
	}

}