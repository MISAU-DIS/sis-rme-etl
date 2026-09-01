package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp._query_result;

import org.openmrs.module.epts.etl.model.pojo.generic.*; 
 
import org.openmrs.module.epts.etl.model.EtlDatabaseObject; 
 
import org.openmrs.module.epts.etl.model.Field; 
 
import org.openmrs.module.epts.etl.model.base.BaseVO; 
 
import org.openmrs.module.epts.etl.utilities.AttDefinedElements; 
 
import org.openmrs.module.epts.etl.conf.Key; 
 
import java.sql.SQLException; 
import java.sql.ResultSet; 
 
import java.sql.Connection; 
 
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration; 
 
import com.fasterxml.jackson.annotation.JsonIgnore; 
 
public class PrescriptionBatchSrcDsQueryResultVO extends AbstractDatabaseObject implements EtlDatabaseObject { 
	private Field batchNumber = Field.fastCreateWithType("batch_number", "VARCHAR");
 
	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PrescriptionBatchSrcDsQueryResultVO() { 
		this.metadata = false;
	} 
 
	@JsonIgnore
	@Override
	public String generateFullFilledUpdateSql(){ 
 		return null; 
	} 
 
	@JsonIgnore
	@Override
	public void setInsertSQLQuestionMarksWithObjectId(String insertQuestionMarks){ 
 	 
	} 
 
	@JsonIgnore
	@Override
	public void setInsertSQLQuestionMarksWithoutObjectId(String insertQuestionMarks){ 
 	 
	} 
 
	@JsonIgnore
	@Override
	public EtlDatabaseObjectConfiguration getRelatedConfiguration(){ 
 		return this.relatedConfiguration; 
	} 
 
	@JsonIgnore
	@Override
	public void setRelatedConfiguration(EtlDatabaseObjectConfiguration config){ 
 	 	this.relatedConfiguration = config;
		enrichGeneratedFields(config);
	} 
 
	@JsonIgnore
	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn){ 
 	 	utilities.throwForbiddenMethodException();
	} 
 

	public void setBatchNumber(Field batchNumber){ 
	 	this.batchNumber = batchNumber;
	}

	public void setBatchNumberValue(String value){ 
		this.batchNumber.setValue(value);
	}
 
	public Field getBatchNumber(){ 
		return this.batchNumber;
	}
 

 
	@Override
	public void load(ResultSet rs) throws SQLException{ 
		super.load(rs);
 
		String batchNumberAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "batch_number", "_" );

		this.batchNumber.setValue(BaseVO.retrieveFieldValue(batchNumberAttName, "VARCHAR", rs));

String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid", "_" );

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId(){ 
 		return "INSERT INTO prescription_batch_src_ds(`batch_number`, `uuid`) VALUES( ?, ?);"; 
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId(){ 
 		return "INSERT INTO prescription_batch_src_ds(`batch_number`, `uuid`) VALUES( ?, ?);"; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getInsertParamsWithoutObjectId(){ 
 		Object[] params = {this.batchNumber.getValue(), this.uuid};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getInsertParamsWithObjectId(){ 
 		Object[] params = {this.batchNumber.getValue(), this.uuid};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId(){ 
 		return "?, ?";
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId(){ 
 		return "?, ?"; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getUpdateParams(){ 
 		throw new RuntimeException("Impossible auto update command! No primary key is defined for table object!");	} 
 
	@JsonIgnore
	@Override
	public String getUpdateSQL(){ 
 		throw new RuntimeException("Impossible auto update command! No primary key is defined for table object!");	} 
 
	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId(){ 
 		return ""+(this.batchNumber.getValue() != null ? "\""+ utilities.scapeQuotationMarks(this.batchNumber.getValue().toString())  +"\"" : null) + "," + (this.uuid != null ? "\""+ utilities.scapeQuotationMarks(this.uuid.toString())  +"\"" : null); 
	} 
 
	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId(){ 
 		return ""+(this.batchNumber.getValue() != null ? "\""+ utilities.scapeQuotationMarks(this.batchNumber.getValue().toString())  +"\"" : null) + "," + (this.uuid != null ? "\""+ utilities.scapeQuotationMarks(this.uuid.toString())  +"\"" : null); 
	} 
 
	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy(){ 
 		PrescriptionBatchSrcDsQueryResultVO copy = new PrescriptionBatchSrcDsQueryResultVO();

		copy.batchNumber = copyGeneratedField(this.batchNumber);

		return copy; 
	} 
 
	@JsonIgnore
	@Override
	public void copyFrom(EtlDatabaseObject toCopyFrom){ 
 		if (toCopyFrom instanceof PrescriptionBatchSrcDsQueryResultVO){
	    	PrescriptionBatchSrcDsQueryResultVO toCopyFromAsPrescriptionBatchSrcDsQueryResultVO = (PrescriptionBatchSrcDsQueryResultVO)toCopyFrom;

			this.batchNumber = copyGeneratedField(toCopyFromAsPrescriptionBatchSrcDsQueryResultVO.batchNumber);

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
		return "prescription_batch_src_ds";
	}


}