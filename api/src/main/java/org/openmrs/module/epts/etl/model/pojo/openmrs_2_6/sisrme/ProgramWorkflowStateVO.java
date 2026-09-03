package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;


import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import org.openmrs.module.epts.etl.utilities.AttDefinedElements;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProgramWorkflowStateVO extends AbstractGeneratedDatabaseObject {
	private Field programWorkflowStateId = Field.fastCreateWithType("program_workflow_state_id", "INT");
	private Field programWorkflowId = Field.fastCreateWithType("program_workflow_id", "INT");
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");
	private Field initial = Field.fastCreateWithType("initial", "BIT");
	private Field terminal = Field.fastCreateWithType("terminal", "BIT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");

	public ProgramWorkflowStateVO() {
		this.metadata = false;

		this.fields.add(this.programWorkflowStateId);
		this.fields.add(this.programWorkflowId);
		this.fields.add(this.conceptId);
		this.fields.add(this.initial);
		this.fields.add(this.terminal);
		this.fields.add(this.creator);
		this.fields.add(this.retired);
		this.fields.add(this.changedBy);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "program_workflow_state_id")) {
			this.programWorkflowStateId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "program_workflow_state_id")) {
			return this.programWorkflowStateId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "program_workflow_id")) {
			return this.programWorkflowId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "concept_id")) {
			return this.conceptId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "initial")) {
			return this.initial.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "terminal")) {
			return this.terminal.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "retired")) {
			return this.retired.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
		}
		return super.getFieldValue(fieldName);
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

	public void setProgramWorkflowStateId(Field programWorkflowStateId) {
		this.programWorkflowStateId = programWorkflowStateId;
	}

	public void setProgramWorkflowStateIdValue(Integer value) {
		this.programWorkflowStateId.setValue(value);
	}

	public Field getProgramWorkflowStateId() {
		return this.programWorkflowStateId;
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

	public void setConceptId(Field conceptId) {
		this.conceptId = conceptId;
	}

	public void setConceptIdValue(Integer value) {
		this.conceptId.setValue(value);
	}

	public Field getConceptId() {
		return this.conceptId;
	}

	public void setInitial(Field initial) {
		this.initial = initial;
	}

	public void setInitialValue(Boolean value) {
		this.initial.setValue(value);
	}

	public Field getInitial() {
		return this.initial;
	}

	public void setTerminal(Field terminal) {
		this.terminal = terminal;
	}

	public void setTerminalValue(Boolean value) {
		this.terminal.setValue(value);
	}

	public Field getTerminal() {
		return this.terminal;
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

		String programWorkflowStateIdAttName = utilities.concatStringsWithSeparator(
				this.getRelatedConfiguration().getAlias(), "program_workflow_state_id", "_");

		this.programWorkflowStateId.setValue(BaseVO.retrieveFieldValue(programWorkflowStateIdAttName, "INT", rs));

		String programWorkflowIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "program_workflow_id", "_");

		this.programWorkflowId.setValue(BaseVO.retrieveFieldValue(programWorkflowIdAttName, "INT", rs));

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));

		String initialAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"initial", "_");

		this.initial.setValue(BaseVO.retrieveFieldValue(initialAttName, "BIT", rs));

		String terminalAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"terminal", "_");

		this.terminal.setValue(BaseVO.retrieveFieldValue(terminalAttName, "BIT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String retiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired", "_");

		this.retired.setValue(BaseVO.retrieveFieldValue(retiredAttName, "BIT", rs));

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO program_workflow_state(`program_workflow_id`, `concept_id`, `initial`, `terminal`, `creator`, `date_created`, `retired`, `changed_by`, `date_changed`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO program_workflow_state(`program_workflow_state_id`, `program_workflow_id`, `concept_id`, `initial`, `terminal`, `creator`, `date_created`, `retired`, `changed_by`, `date_changed`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.programWorkflowId.getValue(), this.conceptId.getValue(), this.initial.getValue(),
				this.terminal.getValue(), this.creator.getValue(), this.dateCreated, this.retired.getValue(),
				this.changedBy.getValue(), this.dateChanged, this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.programWorkflowStateId.getValue(), this.programWorkflowId.getValue(),
				this.conceptId.getValue(), this.initial.getValue(), this.terminal.getValue(), this.creator.getValue(),
				this.dateCreated, this.retired.getValue(), this.changedBy.getValue(), this.dateChanged, this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.programWorkflowStateId.getValue(), this.programWorkflowId.getValue(),
				this.conceptId.getValue(), this.initial.getValue(), this.terminal.getValue(), this.creator.getValue(),
				this.dateCreated, this.retired.getValue(), this.changedBy.getValue(), this.dateChanged, this.uuid,
				this.programWorkflowStateId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE program_workflow_state SET `program_workflow_state_id` = ?, `program_workflow_id` = ?, `concept_id` = ?, `initial` = ?, `terminal` = ?, `creator` = ?, `date_created` = ?, `retired` = ?, `changed_by` = ?, `date_changed` = ?, `uuid` = ? WHERE program_workflow_state_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.programWorkflowId.getValue()) + "," + (this.conceptId.getValue()) + ","
				+ (this.initial.getValue() != null ? "\"" + this.initial.getValue() + "\"" : null) + ","
				+ (this.terminal.getValue() != null ? "\"" + this.terminal.getValue() + "\"" : null) + ","
				+ (this.creator.getValue()) + ","
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
		return "" + (this.programWorkflowStateId.getValue()) + "," + (this.programWorkflowId.getValue()) + ","
				+ (this.conceptId.getValue()) + ","
				+ (this.initial.getValue() != null ? "\"" + this.initial.getValue() + "\"" : null) + ","
				+ (this.terminal.getValue() != null ? "\"" + this.terminal.getValue() + "\"" : null) + ","
				+ (this.creator.getValue()) + ","
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
		ProgramWorkflowStateVO copy = new ProgramWorkflowStateVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.conceptId.getValue() != null)
			return true;

		if (this.programWorkflowId.getValue() != null)
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
		if (parentAttName.equals("programWorkflowId"))
			return this.programWorkflowId.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "program_workflow_state";
	}

}