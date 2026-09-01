package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

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
 
public class TestOrderVO extends AbstractDatabaseObject implements EtlDatabaseObject { 
	private Field orderId = Field.fastCreateWithType("order_id", "INT");
	private Field specimenSource = Field.fastCreateWithType("specimen_source", "INT");
	private Field laterality = Field.fastCreateWithType("laterality", "VARCHAR");
	private Field clinicalHistory = Field.fastCreateWithType("clinical_history", "TEXT");
	private Field frequency = Field.fastCreateWithType("frequency", "INT");
	private Field numberOfRepeats = Field.fastCreateWithType("number_of_repeats", "INT");
	private Field conceptId = Field.fastCreateWithType("concept_id", "BIGINT");
	private Field ordersId = Field.fastCreateWithType("orders_id", "BIGINT");
 
	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public TestOrderVO() { 
		this.metadata = true;
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
 

	public void setOrderId(Field orderId){ 
	 	this.orderId = orderId;
	}

	public void setOrderIdValue(Integer value){ 
		this.orderId.setValue(value);
	}
 
	public Field getOrderId(){ 
		return this.orderId;
	}
 
	public void setSpecimenSource(Field specimenSource){ 
	 	this.specimenSource = specimenSource;
	}

	public void setSpecimenSourceValue(Integer value){ 
		this.specimenSource.setValue(value);
	}
 
	public Field getSpecimenSource(){ 
		return this.specimenSource;
	}
 
	public void setLaterality(Field laterality){ 
	 	this.laterality = laterality;
	}

	public void setLateralityValue(String value){ 
		this.laterality.setValue(value);
	}
 
	public Field getLaterality(){ 
		return this.laterality;
	}
 
	public void setClinicalHistory(Field clinicalHistory){ 
	 	this.clinicalHistory = clinicalHistory;
	}

	public void setClinicalHistoryValue(String value){ 
		this.clinicalHistory.setValue(value);
	}
 
	public Field getClinicalHistory(){ 
		return this.clinicalHistory;
	}
 
	public void setFrequency(Field frequency){ 
	 	this.frequency = frequency;
	}

	public void setFrequencyValue(Integer value){ 
		this.frequency.setValue(value);
	}
 
	public Field getFrequency(){ 
		return this.frequency;
	}
 
	public void setNumberOfRepeats(Field numberOfRepeats){ 
	 	this.numberOfRepeats = numberOfRepeats;
	}

	public void setNumberOfRepeatsValue(Integer value){ 
		this.numberOfRepeats.setValue(value);
	}
 
	public Field getNumberOfRepeats(){ 
		return this.numberOfRepeats;
	}
 
	public void setConceptId(Field conceptId){ 
	 	this.conceptId = conceptId;
	}

	public void setConceptIdValue(Integer value){ 
		this.conceptId.setValue(value);
	}
 
	public Field getConceptId(){ 
		return this.conceptId;
	}
 
	public void setOrdersId(Field ordersId){ 
	 	this.ordersId = ordersId;
	}

	public void setOrdersIdValue(Integer value){ 
		this.ordersId.setValue(value);
	}


 
	public Field getOrdersId(){ 
		return this.ordersId;
	}
 
	@Override
	public void load(ResultSet rs) throws SQLException{ 
		super.load(rs);
 
			String orderIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "order_id", "_" );

		this.orderId.setValue(BaseVO.retrieveFieldValue(orderIdAttName, "INT", rs));

			String specimenSourceAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "specimen_source", "_" );

		this.specimenSource.setValue(BaseVO.retrieveFieldValue(specimenSourceAttName, "INT", rs));

			String lateralityAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "laterality", "_" );

		this.laterality.setValue(BaseVO.retrieveFieldValue(lateralityAttName, "VARCHAR", rs));

			String clinicalHistoryAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "clinical_history", "_" );

		this.clinicalHistory.setValue(BaseVO.retrieveFieldValue(clinicalHistoryAttName, "TEXT", rs));

			String frequencyAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "frequency", "_" );

		this.frequency.setValue(BaseVO.retrieveFieldValue(frequencyAttName, "INT", rs));

			String numberOfRepeatsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "number_of_repeats", "_" );

		this.numberOfRepeats.setValue(BaseVO.retrieveFieldValue(numberOfRepeatsAttName, "INT", rs));

			String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "concept_id", "_" );

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "BIGINT", rs));

	String ordersIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "orders_id", "_" );

		this.ordersId.setValue(BaseVO.retrieveFieldValue(ordersIdAttName, "BIGINT", rs));
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId(){ 
 		return "INSERT INTO test_order(`specimen_source`, `laterality`, `clinical_history`, `frequency`, `number_of_repeats`, `concept_id`, `orders_id`) VALUES( ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId(){ 
 		return "INSERT INTO test_order(`order_id`, `specimen_source`, `laterality`, `clinical_history`, `frequency`, `number_of_repeats`, `concept_id`, `orders_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getInsertParamsWithoutObjectId(){ 
 		Object[] params = {this.specimenSource.getValue(), this.laterality.getValue(), this.clinicalHistory.getValue(), this.frequency.getValue(), this.numberOfRepeats.getValue(), this.conceptId.getValue(), this.ordersId.getValue()};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getInsertParamsWithObjectId(){ 
 		Object[] params = {this.orderId.getValue(), this.specimenSource.getValue(), this.laterality.getValue(), this.clinicalHistory.getValue(), this.frequency.getValue(), this.numberOfRepeats.getValue(), this.conceptId.getValue(), this.ordersId.getValue()};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId(){ 
 		return "?, ?, ?, ?, ?, ?, ?";
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId(){ 
 		return "?, ?, ?, ?, ?, ?, ?, ?"; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getUpdateParams(){ 
 		Object[] params = {this.orderId.getValue(), this.specimenSource.getValue(), this.laterality.getValue(), this.clinicalHistory.getValue(), this.frequency.getValue(), this.numberOfRepeats.getValue(), this.conceptId.getValue(), this.ordersId.getValue(), this.orderId.getValue()};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public String getUpdateSQL(){ 
 		return "UPDATE test_order SET `order_id` = ?, `specimen_source` = ?, `laterality` = ?, `clinical_history` = ?, `frequency` = ?, `number_of_repeats` = ?, `concept_id` = ?, `orders_id` = ? WHERE test_order_1.order_id = ? "; 
	} 
 
	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId(){ 
 		return ""+(this.specimenSource.getValue()) + "," + (this.laterality.getValue() != null ? "\""+ utilities.scapeQuotationMarks(this.laterality.getValue().toString())  +"\"" : null) + "," + (this.clinicalHistory.getValue() != null ? "\""+ utilities.scapeQuotationMarks(this.clinicalHistory.getValue().toString())  +"\"" : null) + "," + (this.frequency.getValue()) + "," + (this.numberOfRepeats.getValue()) + "," + (this.conceptId.getValue()) + "," + (this.ordersId.getValue()); 
	} 
 
	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId(){ 
 		return ""+(this.orderId.getValue()) + "," + (this.specimenSource.getValue()) + "," + (this.laterality.getValue() != null ? "\""+ utilities.scapeQuotationMarks(this.laterality.getValue().toString())  +"\"" : null) + "," + (this.clinicalHistory.getValue() != null ? "\""+ utilities.scapeQuotationMarks(this.clinicalHistory.getValue().toString())  +"\"" : null) + "," + (this.frequency.getValue()) + "," + (this.numberOfRepeats.getValue()) + "," + (this.conceptId.getValue()) + "," + (this.ordersId.getValue()); 
	} 
 
	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy(){ 
 		TestOrderVO copy = new TestOrderVO();

		copy.orderId = copyGeneratedField(this.orderId);
		copy.specimenSource = copyGeneratedField(this.specimenSource);
		copy.laterality = copyGeneratedField(this.laterality);
		copy.clinicalHistory = copyGeneratedField(this.clinicalHistory);
		copy.frequency = copyGeneratedField(this.frequency);
		copy.numberOfRepeats = copyGeneratedField(this.numberOfRepeats);
		copy.conceptId = copyGeneratedField(this.conceptId);

		return copy; 
	} 
 
	@JsonIgnore
	@Override
	public void copyFrom(EtlDatabaseObject toCopyFrom){ 
 		if (toCopyFrom instanceof TestOrderVO){
	    	TestOrderVO toCopyFromAsTestOrderVO = (TestOrderVO)toCopyFrom;

			this.orderId = copyGeneratedField(toCopyFromAsTestOrderVO.orderId);
			this.specimenSource = copyGeneratedField(toCopyFromAsTestOrderVO.specimenSource);
			this.laterality = copyGeneratedField(toCopyFromAsTestOrderVO.laterality);
			this.clinicalHistory = copyGeneratedField(toCopyFromAsTestOrderVO.clinicalHistory);
			this.frequency = copyGeneratedField(toCopyFromAsTestOrderVO.frequency);
			this.numberOfRepeats = copyGeneratedField(toCopyFromAsTestOrderVO.numberOfRepeats);
			this.conceptId = copyGeneratedField(toCopyFromAsTestOrderVO.conceptId);

	    }
	} 
 
	@Override
	public boolean hasParents() {
		if (this.specimenSource.getValue() != null) return true;

		if (this.frequency.getValue() != null) return true;

		if (this.orderId.getValue() != null) return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {		
		if (parentAttName.equals("specimenSource")) return this.specimenSource.getValue();		
		if (parentAttName.equals("frequency")) return this.frequency.getValue();		
		if (parentAttName.equals("orderId")) return this.orderId.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "test_order";
	}


}