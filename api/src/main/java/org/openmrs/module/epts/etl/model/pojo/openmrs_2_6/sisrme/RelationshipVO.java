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

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RelationshipVO extends AbstractGeneratedDatabaseObject {
	private Field relationshipId = Field.fastCreateWithType("relationship_id", "INT");
	private Field personA = Field.fastCreateWithType("person_a", "INT");
	private Field relationship = Field.fastCreateWithType("relationship", "INT");
	private Field personB = Field.fastCreateWithType("person_b", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field startDate = Field.fastCreateWithType("start_date", "DATETIME");
	private Field endDate = Field.fastCreateWithType("end_date", "DATETIME");

	public RelationshipVO() {
		this.metadata = false;
		this.fields.add(this.relationshipId);
		this.fields.add(this.personA);
		this.fields.add(this.relationship);
		this.fields.add(this.personB);
		this.fields.add(this.creator);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
		this.fields.add(this.changedBy);
		this.fields.add(this.startDate);
		this.fields.add(this.endDate);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "relationship_id")) {
			this.relationshipId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "relationship_id")) {
			return this.relationshipId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "person_a")) {
			return this.personA.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "relationship")) {
			return this.relationship.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "person_b")) {
			return this.personB.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "voided")) {
			return this.voided.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "voided_by")) {
			return this.voidedBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "void_reason")) {
			return this.voidReason.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "start_date")) {
			return this.startDate.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "end_date")) {
			return this.endDate.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "relationship_id")) {
			this.relationshipId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "person_a")) {
			this.personA.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "relationship")) {
			this.relationship.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "person_b")) {
			this.personB.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			this.creator.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "voided")) {
			this.voided.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "voided_by")) {
			this.voidedBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "void_reason")) {
			this.voidReason.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			this.changedBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "start_date")) {
			this.startDate.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "end_date")) {
			this.endDate.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.relationshipId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.personA, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.relationship, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.personB, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voided, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidReason, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.changedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.startDate, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.endDate, srcConn, dstConn);
	}

	public void setRelationshipId(Field relationshipId) {
		this.relationshipId = relationshipId;
	}

	public void setRelationshipIdValue(Integer value) {
		this.relationshipId.setValue(value);
	}

	public Field getRelationshipId() {
		return this.relationshipId;
	}

	public void setPersonA(Field personA) {
		this.personA = personA;
	}

	public void setPersonAValue(Integer value) {
		this.personA.setValue(value);
	}

	public Field getPersonA() {
		return this.personA;
	}

	public void setRelationship(Field relationship) {
		this.relationship = relationship;
	}

	public void setRelationshipValue(Integer value) {
		this.relationship.setValue(value);
	}

	public Field getRelationship() {
		return this.relationship;
	}

	public void setPersonB(Field personB) {
		this.personB = personB;
	}

	public void setPersonBValue(Integer value) {
		this.personB.setValue(value);
	}

	public Field getPersonB() {
		return this.personB;
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

	public void setVoided(Field voided) {
		this.voided = voided;
	}

	public void setVoidedValue(Boolean value) {
		this.voided.setValue(value);
	}

	public Field getVoided() {
		return this.voided;
	}

	public void setVoidedBy(Field voidedBy) {
		this.voidedBy = voidedBy;
	}

	public void setVoidedByValue(Integer value) {
		this.voidedBy.setValue(value);
	}

	public Field getVoidedBy() {
		return this.voidedBy;
	}

	public void setVoidReason(Field voidReason) {
		this.voidReason = voidReason;
	}

	public void setVoidReasonValue(String value) {
		this.voidReason.setValue(value);
	}

	public Field getVoidReason() {
		return this.voidReason;
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

	public void setStartDate(Field startDate) {
		this.startDate = startDate;
	}

	public void setStartDateValue(java.util.Date value) {
		this.startDate.setValue(value);
	}

	public Field getStartDate() {
		return this.startDate;
	}

	public void setEndDate(Field endDate) {
		this.endDate = endDate;
	}

	public void setEndDateValue(java.util.Date value) {
		this.endDate.setValue(value);
	}

	public Field getEndDate() {
		return this.endDate;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String relationshipIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"relationship_id", "_");

		this.relationshipId.setValue(BaseVO.retrieveFieldValue(relationshipIdAttName, "INT", rs));

		String personAAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_a", "_");

		this.personA.setValue(BaseVO.retrieveFieldValue(personAAttName, "INT", rs));

		String relationshipAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"relationship", "_");

		this.relationship.setValue(BaseVO.retrieveFieldValue(relationshipAttName, "INT", rs));

		String personBAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_b", "_");

		this.personB.setValue(BaseVO.retrieveFieldValue(personBAttName, "INT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided",
				"_");

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "BIT", rs));

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

		String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_voided", "_");

		this.dateVoided = (java.util.Date) BaseVO.retrieveFieldValue(dateVoidedAttName, "DATETIME", rs);

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "CHAR", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String startDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"start_date", "_");

		this.startDate.setValue(BaseVO.retrieveFieldValue(startDateAttName, "DATETIME", rs));

		String endDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"end_date", "_");

		this.endDate.setValue(BaseVO.retrieveFieldValue(endDateAttName, "DATETIME", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO relationship(`person_a`, `relationship`, `person_b`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `date_changed`, `changed_by`, `start_date`, `end_date`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO relationship(`relationship_id`, `person_a`, `relationship`, `person_b`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `date_changed`, `changed_by`, `start_date`, `end_date`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.personA.getValue(), this.relationship.getValue(), this.personB.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid, this.dateChanged, this.changedBy.getValue(),
				this.startDate.getValue(), this.endDate.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.relationshipId.getValue(), this.personA.getValue(), this.relationship.getValue(),
				this.personB.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid, this.dateChanged,
				this.changedBy.getValue(), this.startDate.getValue(), this.endDate.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.relationshipId.getValue(), this.personA.getValue(), this.relationship.getValue(),
				this.personB.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid, this.dateChanged,
				this.changedBy.getValue(), this.startDate.getValue(), this.endDate.getValue(),
				this.relationshipId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE relationship SET `relationship_id` = ?, `person_a` = ?, `relationship` = ?, `person_b` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ?, `date_changed` = ?, `changed_by` = ?, `start_date` = ?, `end_date` = ? WHERE relationship_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.personA.getValue()) + "," + (this.relationship.getValue()) + "," + (this.personB.getValue())
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ ","
				+ (this.dateChanged != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\"" : null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.startDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.startDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.endDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.endDate.getValue()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.relationshipId.getValue()) + "," + (this.personA.getValue()) + ","
				+ (this.relationship.getValue()) + "," + (this.personB.getValue()) + "," + (this.creator.getValue())
				+ ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ ","
				+ (this.dateChanged != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\"" : null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.startDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.startDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.endDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.endDate.getValue()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		RelationshipVO copy = new RelationshipVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.personA.getValue() != null)
			return true;

		if (this.personB.getValue() != null)
			return true;

		if (this.relationship.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("personA"))
			return this.personA.getValue();
		if (parentAttName.equals("personB"))
			return this.personB.getValue();
		if (parentAttName.equals("relationship"))
			return this.relationship.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "relationship";
	}

}