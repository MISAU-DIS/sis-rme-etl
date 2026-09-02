package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import org.openmrs.module.epts.etl.utilities.AttDefinedElements;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProgramWorkflowVO extends AbstractGeneratedDatabaseObject {
	private Field programWorkflowId = Field.fastCreateWithType("program_workflow_id", "INT");
	private Field programId = Field.fastCreateWithType("program_id", "INT");
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public ProgramWorkflowVO() {
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

	public void setProgramWorkflowId(Field programWorkflowId) {
		this.programWorkflowId = programWorkflowId;
	}

	public void setProgramWorkflowIdValue(Integer value) {
		this.programWorkflowId.setValue(value);
	}

	public Field getProgramWorkflowId() {
		return this.programWorkflowId;
	}

	public void setProgramId(Field programId) {
		this.programId = programId;
	}

	public void setProgramIdValue(Integer value) {
		this.programId.setValue(value);
	}

	public Field getProgramId() {
		return this.programId;
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

	public void setCreator(Field creator) {
		this.creator = creator;
	}

	public void setCreatorValue(Integer value) {
		this.creator.setValue(value);
	}

	public Field getCreator() {
		return this.creator;
	}

	public void setRetired(Field retired) {
		this.retired = retired;
	}

	public void setRetiredValue(Boolean value) {
		this.retired.setValue(value);
	}

	public Field getRetired() {
		return this.retired;
	}

	public void setChangedBy(Field changedBy) {
		this.changedBy = changedBy;
	}

	public void setChangedByValue(Integer value) {
		this.changedBy.setValue(value);
	}

	public Field getChangedBy() {
		return this.changedBy;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String programWorkflowIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "program_workflow_id", "_");

		this.programWorkflowId.setValue(BaseVO.retrieveFieldValue(programWorkflowIdAttName, "INT", rs));

		String programIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"program_id", "_");

		this.programId.setValue(BaseVO.retrieveFieldValue(programIdAttName, "INT", rs));

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = rs.getTimestamp(dateCreatedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateCreatedAttName).getTime())
				: null;

		String retiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired", "_");

		this.retired.setValue(BaseVO.retrieveFieldValue(retiredAttName, "BIT", rs));

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = rs.getTimestamp(dateChangedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateChangedAttName).getTime())
				: null;

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO program_workflow(`program_id`, `concept_id`, `creator`, `date_created`, `retired`, `changed_by`, `date_changed`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO program_workflow(`program_workflow_id`, `program_id`, `concept_id`, `creator`, `date_created`, `retired`, `changed_by`, `date_changed`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.programId.getValue(), this.conceptId.getValue(), this.creator.getValue(),
				this.dateCreated, this.retired.getValue(), this.changedBy.getValue(), this.dateChanged, this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.programWorkflowId.getValue(), this.programId.getValue(), this.conceptId.getValue(),
				this.creator.getValue(), this.dateCreated, this.retired.getValue(), this.changedBy.getValue(),
				this.dateChanged, this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.programWorkflowId.getValue(), this.programId.getValue(), this.conceptId.getValue(),
				this.creator.getValue(), this.dateCreated, this.retired.getValue(), this.changedBy.getValue(),
				this.dateChanged, this.uuid, this.programWorkflowId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE program_workflow SET `program_workflow_id` = ?, `program_id` = ?, `concept_id` = ?, `creator` = ?, `date_created` = ?, `retired` = ?, `changed_by` = ?, `date_changed` = ?, `uuid` = ? WHERE program_workflow_1.program_workflow_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.programId.getValue()) + "," + (this.conceptId.getValue()) + "," + (this.creator.getValue())
				+ ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.programWorkflowId.getValue()) + "," + (this.programId.getValue()) + ","
				+ (this.conceptId.getValue()) + "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		ProgramWorkflowVO copy = new ProgramWorkflowVO();

		copy.programWorkflowId = copyGeneratedField(this.programWorkflowId);
		copy.programId = copyGeneratedField(this.programId);
		copy.conceptId = copyGeneratedField(this.conceptId);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.retired = copyGeneratedField(this.retired);
		copy.changedBy = copyGeneratedField(this.changedBy);
		copy.dateChanged = this.dateChanged;

		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.conceptId.getValue() != null)
			return true;

		if (this.programId.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("conceptId"))
			return this.conceptId.getValue();
		if (parentAttName.equals("programId"))
			return this.programId.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "program_workflow";
	}

}