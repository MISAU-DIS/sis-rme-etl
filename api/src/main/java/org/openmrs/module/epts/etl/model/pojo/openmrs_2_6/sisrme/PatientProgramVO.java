package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

import org.openmrs.module.epts.etl.model.pojo.generic.*; 
 
import org.openmrs.module.epts.etl.model.EtlDatabaseObject; 
 
import org.openmrs.module.epts.etl.model.Field; 
 
import org.openmrs.module.epts.etl.model.base.BaseVO; 
 
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities; 
 
import org.openmrs.module.epts.etl.utilities.AttDefinedElements; 
 
import org.openmrs.module.epts.etl.conf.Key; 
 
import java.sql.SQLException; 
import java.sql.ResultSet; 
 
import java.sql.Connection; 
 
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration; 
 
import com.fasterxml.jackson.annotation.JsonIgnore; 
 
public class PatientProgramVO extends AbstractDatabaseObject implements EtlDatabaseObject { 
	private Field patientProgramId = Field.fastCreateWithType("patient_program_id", "INT");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field programId = Field.fastCreateWithType("program_id", "INT");
	private Field dateEnrolled = Field.fastCreateWithType("date_enrolled", "DATETIME");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
 
	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PatientProgramVO() { 
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
 

	public void setPatientProgramId(Field patientProgramId){ 
	 	this.patientProgramId = patientProgramId;
	}

	public void setPatientProgramIdValue(Integer value){ 
		this.patientProgramId.setValue(value);
	}
 
	public Field getPatientProgramId(){ 
		return this.patientProgramId;
	}
 
	public void setPatientId(Field patientId){ 
	 	this.patientId = patientId;
	}

	public void setPatientIdValue(Integer value){ 
		this.patientId.setValue(value);
	}
 
	public Field getPatientId(){ 
		return this.patientId;
	}
 
	public void setProgramId(Field programId){ 
	 	this.programId = programId;
	}

	public void setProgramIdValue(Integer value){ 
		this.programId.setValue(value);
	}
 
	public Field getProgramId(){ 
		return this.programId;
	}
 
	public void setDateEnrolled(Field dateEnrolled){ 
	 	this.dateEnrolled = dateEnrolled;
	}

	public void setDateEnrolledValue(java.util.Date value){ 
		this.dateEnrolled.setValue(value);
	}
 
	public Field getDateEnrolled(){ 
		return this.dateEnrolled;
	}
 
	public void setCreator(Field creator){ 
	 	this.creator = creator;
	}

	public void setCreatorValue(Integer value){ 
		this.creator.setValue(value);
	}
 
	public Field getCreator(){ 
		return this.creator;
	}
 
	public void setChangedBy(Field changedBy){ 
	 	this.changedBy = changedBy;
	}

	public void setChangedByValue(Integer value){ 
		this.changedBy.setValue(value);
	}
 
	public Field getChangedBy(){ 
		return this.changedBy;
	}
 
	public void setVoided(Field voided){ 
	 	this.voided = voided;
	}

	public void setVoidedValue(Boolean value){ 
		this.voided.setValue(value);
	}
 
	public Field getVoided(){ 
		return this.voided;
	}
 
	public void setVoidedBy(Field voidedBy){ 
	 	this.voidedBy = voidedBy;
	}

	public void setVoidedByValue(Integer value){ 
		this.voidedBy.setValue(value);
	}
 
	public Field getVoidedBy(){ 
		return this.voidedBy;
	}
 
	public void setVoidReason(Field voidReason){ 
	 	this.voidReason = voidReason;
	}

	public void setVoidReasonValue(String value){ 
		this.voidReason.setValue(value);
	}
 
	public Field getVoidReason(){ 
		return this.voidReason;
	}
 
	public void setLocationId(Field locationId){ 
	 	this.locationId = locationId;
	}

	public void setLocationIdValue(Integer value){ 
		this.locationId.setValue(value);
	}


 
	public Field getLocationId(){ 
		return this.locationId;
	}
 
	@Override
	public void load(ResultSet rs) throws SQLException{ 
		super.load(rs);
 
			String patientProgramIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "patient_program_id", "_" );

		this.patientProgramId.setValue(BaseVO.retrieveFieldValue(patientProgramIdAttName, "INT", rs));

			String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "patient_id", "_" );

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

			String programIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "program_id", "_" );

		this.programId.setValue(BaseVO.retrieveFieldValue(programIdAttName, "INT", rs));

			String dateEnrolledAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "date_enrolled", "_" );

		this.dateEnrolled.setValue(BaseVO.retrieveFieldValue(dateEnrolledAttName, "DATETIME", rs));

			String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "creator", "_" );

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

			String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "date_created", "_" );

		this.dateCreated =  rs.getTimestamp(dateCreatedAttName) != null ? new java.util.Date( rs.getTimestamp(dateCreatedAttName).getTime() ) : null;

			String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "changed_by", "_" );

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

			String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "date_changed", "_" );

		this.dateChanged =  rs.getTimestamp(dateChangedAttName) != null ? new java.util.Date( rs.getTimestamp(dateChangedAttName).getTime() ) : null;

			String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided", "_" );

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "BIT", rs));

			String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided_by", "_" );

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

			String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "date_voided", "_" );

		this.dateVoided =  rs.getTimestamp(dateVoidedAttName) != null ? new java.util.Date( rs.getTimestamp(dateVoidedAttName).getTime() ) : null;

			String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "void_reason", "_" );

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

			String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid", "_" );

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);

	String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "location_id", "_" );

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId(){ 
 		return "INSERT INTO patient_program(`patient_id`, `program_id`, `date_enrolled`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `location_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId(){ 
 		return "INSERT INTO patient_program(`patient_program_id`, `patient_id`, `program_id`, `date_enrolled`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `location_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getInsertParamsWithoutObjectId(){ 
 		Object[] params = {this.patientId.getValue(), this.programId.getValue(), this.dateEnrolled.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid, this.locationId.getValue()};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getInsertParamsWithObjectId(){ 
 		Object[] params = {this.patientProgramId.getValue(), this.patientId.getValue(), this.programId.getValue(), this.dateEnrolled.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid, this.locationId.getValue()};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId(){ 
 		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	} 
 
	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId(){ 
 		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"; 
	} 
 
	@JsonIgnore
	@Override
	public Object[]  getUpdateParams(){ 
 		Object[] params = {this.patientProgramId.getValue(), this.patientId.getValue(), this.programId.getValue(), this.dateEnrolled.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid, this.locationId.getValue(), this.patientProgramId.getValue()};
		return params; 
	} 
 
	@JsonIgnore
	@Override
	public String getUpdateSQL(){ 
 		return "UPDATE patient_program SET `patient_program_id` = ?, `patient_id` = ?, `program_id` = ?, `date_enrolled` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ?, `location_id` = ? WHERE patient_program_dst_ds.patient_program_id = ? "; 
	} 
 
	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId(){ 
 		return ""+(this.patientId.getValue()) + "," + (this.programId.getValue()) + "," + (this.dateEnrolled.getValue() != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateEnrolled.getValue())  +"\"" : null) + "," + (this.creator.getValue()) + "," + (this.dateCreated != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated)  +"\"" : null) + "," + (this.changedBy.getValue()) + "," + (this.dateChanged != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged)  +"\"" : null) + "," + (this.voided.getValue() != null ? "\""+this.voided.getValue()+"\"" : null) + "," + (this.voidedBy.getValue()) + "," + (this.dateVoided != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided)  +"\"" : null) + "," + (this.voidReason.getValue() != null ? "\""+ utilities.scapeQuotationMarks(this.voidReason.getValue().toString())  +"\"" : null) + "," + (this.uuid != null ? "\""+ utilities.scapeQuotationMarks(this.uuid.toString())  +"\"" : null) + "," + (this.locationId.getValue()); 
	} 
 
	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId(){ 
 		return ""+(this.patientProgramId.getValue()) + "," + (this.patientId.getValue()) + "," + (this.programId.getValue()) + "," + (this.dateEnrolled.getValue() != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateEnrolled.getValue())  +"\"" : null) + "," + (this.creator.getValue()) + "," + (this.dateCreated != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated)  +"\"" : null) + "," + (this.changedBy.getValue()) + "," + (this.dateChanged != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged)  +"\"" : null) + "," + (this.voided.getValue() != null ? "\""+this.voided.getValue()+"\"" : null) + "," + (this.voidedBy.getValue()) + "," + (this.dateVoided != null ? "\""+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided)  +"\"" : null) + "," + (this.voidReason.getValue() != null ? "\""+ utilities.scapeQuotationMarks(this.voidReason.getValue().toString())  +"\"" : null) + "," + (this.uuid != null ? "\""+ utilities.scapeQuotationMarks(this.uuid.toString())  +"\"" : null) + "," + (this.locationId.getValue()); 
	} 
 
	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy(){ 
 		PatientProgramVO copy = new PatientProgramVO();

		copy.patientProgramId = copyGeneratedField(this.patientProgramId);
		copy.patientId = copyGeneratedField(this.patientId);
		copy.programId = copyGeneratedField(this.programId);
		copy.dateEnrolled = copyGeneratedField(this.dateEnrolled);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.changedBy = copyGeneratedField(this.changedBy);
		copy.dateChanged = this.dateChanged;
		copy.voided = copyGeneratedField(this.voided);
		copy.voidedBy = copyGeneratedField(this.voidedBy);
		copy.dateVoided = this.dateVoided;
		copy.voidReason = copyGeneratedField(this.voidReason);
		copy.uuid = this.uuid;

		return copy; 
	} 
 
	@JsonIgnore
	@Override
	public void copyFrom(EtlDatabaseObject toCopyFrom){ 
 		if (toCopyFrom instanceof PatientProgramVO){
	    	PatientProgramVO toCopyFromAsPatientProgramVO = (PatientProgramVO)toCopyFrom;

			this.patientProgramId = copyGeneratedField(toCopyFromAsPatientProgramVO.patientProgramId);
			this.patientId = copyGeneratedField(toCopyFromAsPatientProgramVO.patientId);
			this.programId = copyGeneratedField(toCopyFromAsPatientProgramVO.programId);
			this.dateEnrolled = copyGeneratedField(toCopyFromAsPatientProgramVO.dateEnrolled);
			this.creator = copyGeneratedField(toCopyFromAsPatientProgramVO.creator);
			this.dateCreated = toCopyFromAsPatientProgramVO.dateCreated;
			this.changedBy = copyGeneratedField(toCopyFromAsPatientProgramVO.changedBy);
			this.dateChanged = toCopyFromAsPatientProgramVO.dateChanged;
			this.voided = copyGeneratedField(toCopyFromAsPatientProgramVO.voided);
			this.voidedBy = copyGeneratedField(toCopyFromAsPatientProgramVO.voidedBy);
			this.dateVoided = toCopyFromAsPatientProgramVO.dateVoided;
			this.voidReason = copyGeneratedField(toCopyFromAsPatientProgramVO.voidReason);
			this.uuid = toCopyFromAsPatientProgramVO.uuid;

	    }
	} 
 
	@Override
	public boolean hasParents() {
		if (this.locationId.getValue() != null) return true;

		if (this.patientId.getValue() != null) return true;

		if (this.programId.getValue() != null) return true;

		if (this.creator.getValue() != null) return true;

		if (this.changedBy.getValue() != null) return true;

		if (this.voidedBy.getValue() != null) return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {		
		if (parentAttName.equals("locationId")) return this.locationId.getValue();		
		if (parentAttName.equals("patientId")) return this.patientId.getValue();		
		if (parentAttName.equals("programId")) return this.programId.getValue();		
		if (parentAttName.equals("creator")) return this.creator.getValue();		
		if (parentAttName.equals("changedBy")) return this.changedBy.getValue();		
		if (parentAttName.equals("voidedBy")) return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "patient_program";
	}


}