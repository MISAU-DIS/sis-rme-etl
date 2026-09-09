package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp;

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

public class RelationshipTypeVO extends AbstractGeneratedDatabaseObject {
	private Field relationshipTypeId = Field.fastCreateWithType("relationship_type_id", "INT");
	private Field aIsToB = Field.fastCreateWithType("a_is_to_b", "VARCHAR");
	private Field bIsToA = Field.fastCreateWithType("b_is_to_a", "VARCHAR");
	private Field preferred = Field.fastCreateWithType("preferred", "INT");
	private Field weight = Field.fastCreateWithType("weight", "INT");
	private Field description = Field.fastCreateWithType("description", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");

	public RelationshipTypeVO() {
		this.metadata = true;
		this.fields.add(this.relationshipTypeId);
		this.fields.add(this.aIsToB);
		this.fields.add(this.bIsToA);
		this.fields.add(this.preferred);
		this.fields.add(this.weight);
		this.fields.add(this.description);
		this.fields.add(this.creator);
		this.fields.add(this.retired);
		this.fields.add(this.retiredBy);
		this.fields.add(this.dateRetired);
		this.fields.add(this.retireReason);
		this.fields.add(this.changedBy);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "relationship_type_id")) {
			this.relationshipTypeId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "relationship_type_id")) {
			return this.relationshipTypeId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "a_is_to_b")) {
			return this.aIsToB.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "b_is_to_a")) {
			return this.bIsToA.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "preferred")) {
			return this.preferred.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "weight")) {
			return this.weight.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "description")) {
			return this.description.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "retired")) {
			return this.retired.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "retired_by")) {
			return this.retiredBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_retired")) {
			return this.dateRetired.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "retire_reason")) {
			return this.retireReason.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "relationship_type_id")) {
			this.relationshipTypeId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "a_is_to_b")) {
			this.aIsToB.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "b_is_to_a")) {
			this.bIsToA.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "preferred")) {
			this.preferred.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "weight")) {
			this.weight.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "description")) {
			this.description.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			this.creator.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "retired")) {
			this.retired.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "retired_by")) {
			this.retiredBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "date_retired")) {
			this.dateRetired.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "retire_reason")) {
			this.retireReason.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			this.changedBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.relationshipTypeId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.aIsToB, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.bIsToA, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.preferred, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.weight, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.description, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.retired, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.retiredBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dateRetired, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.retireReason, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.changedBy, srcConn, dstConn);
	}

	public void setRelationshipTypeId(Field relationshipTypeId) {
		this.relationshipTypeId = relationshipTypeId;
	}

	public void setRelationshipTypeIdValue(Integer value) {
		this.relationshipTypeId.setValue(value);
	}

	public Field getRelationshipTypeId() {
		return this.relationshipTypeId;
	}

	public void setAIsToB(Field aIsToB) {
		this.aIsToB = aIsToB;
	}

	public void setAIsToBValue(String value) {
		this.aIsToB.setValue(value);
	}

	public Field getAIsToB() {
		return this.aIsToB;
	}

	public void setBIsToA(Field bIsToA) {
		this.bIsToA = bIsToA;
	}

	public void setBIsToAValue(String value) {
		this.bIsToA.setValue(value);
	}

	public Field getBIsToA() {
		return this.bIsToA;
	}

	public void setPreferred(Field preferred) {
		this.preferred = preferred;
	}

	public void setPreferredValue(Integer value) {
		this.preferred.setValue(value);
	}

	public Field getPreferred() {
		return this.preferred;
	}

	public void setWeight(Field weight) {
		this.weight = weight;
	}

	public void setWeightValue(Integer value) {
		this.weight.setValue(value);
	}

	public Field getWeight() {
		return this.weight;
	}

	public void setDescription(Field description) {
		this.description = description;
	}

	public void setDescriptionValue(String value) {
		this.description.setValue(value);
	}

	public Field getDescription() {
		return this.description;
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

	public void setRetiredBy(Field retiredBy) {
		this.retiredBy = retiredBy;
	}

	public void setRetiredByValue(Integer value) {
		this.retiredBy.setValue(value);
	}

	public Field getRetiredBy() {
		return this.retiredBy;
	}

	public void setDateRetired(Field dateRetired) {
		this.dateRetired = dateRetired;
	}

	public void setDateRetiredValue(java.util.Date value) {
		this.dateRetired.setValue(value);
	}

	public Field getDateRetired() {
		return this.dateRetired;
	}

	public void setRetireReason(Field retireReason) {
		this.retireReason = retireReason;
	}

	public void setRetireReasonValue(String value) {
		this.retireReason.setValue(value);
	}

	public Field getRetireReason() {
		return this.retireReason;
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

		String relationshipTypeIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "relationship_type_id", "_");

		this.relationshipTypeId.setValue(BaseVO.retrieveFieldValue(relationshipTypeIdAttName, "INT", rs));

		String aIsToBAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"a_is_to_b", "_");

		this.aIsToB.setValue(BaseVO.retrieveFieldValue(aIsToBAttName, "VARCHAR", rs));

		String bIsToAAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"b_is_to_a", "_");

		this.bIsToA.setValue(BaseVO.retrieveFieldValue(bIsToAAttName, "VARCHAR", rs));

		String preferredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"preferred", "_");

		this.preferred.setValue(BaseVO.retrieveFieldValue(preferredAttName, "INT", rs));

		String weightAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "weight",
				"_");

		this.weight.setValue(BaseVO.retrieveFieldValue(weightAttName, "INT", rs));

		String descriptionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"description", "_");

		this.description.setValue(BaseVO.retrieveFieldValue(descriptionAttName, "VARCHAR", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "CHAR", rs));

		String retiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired", "_");

		this.retired.setValue(BaseVO.retrieveFieldValue(retiredAttName, "BIT", rs));

		String retiredByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retired_by", "_");

		this.retiredBy.setValue(BaseVO.retrieveFieldValue(retiredByAttName, "INT", rs));

		String dateRetiredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_retired", "_");

		this.dateRetired.setValue(BaseVO.retrieveFieldValue(dateRetiredAttName, "DATETIME", rs));

		String retireReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retire_reason", "_");

		this.retireReason.setValue(BaseVO.retrieveFieldValue(retireReasonAttName, "VARCHAR", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO relationship_type(`a_is_to_b`, `b_is_to_a`, `preferred`, `weight`, `description`, `creator`, `date_created`, `uuid`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `date_changed`, `changed_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO relationship_type(`relationship_type_id`, `a_is_to_b`, `b_is_to_a`, `preferred`, `weight`, `description`, `creator`, `date_created`, `uuid`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `date_changed`, `changed_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.aIsToB.getValue(), this.bIsToA.getValue(), this.preferred.getValue(),
				this.weight.getValue(), this.description.getValue(), this.creator.getValue(), this.dateCreated,
				this.uuid, this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.dateChanged, this.changedBy.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.relationshipTypeId.getValue(), this.aIsToB.getValue(), this.bIsToA.getValue(),
				this.preferred.getValue(), this.weight.getValue(), this.description.getValue(), this.creator.getValue(),
				this.dateCreated, this.uuid, this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.dateChanged,
				this.changedBy.getValue() };
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
		Object[] params = { this.relationshipTypeId.getValue(), this.aIsToB.getValue(), this.bIsToA.getValue(),
				this.preferred.getValue(), this.weight.getValue(), this.description.getValue(), this.creator.getValue(),
				this.dateCreated, this.uuid, this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.dateChanged, this.changedBy.getValue(),
				this.relationshipTypeId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE relationship_type SET `relationship_type_id` = ?, `a_is_to_b` = ?, `b_is_to_a` = ?, `preferred` = ?, `weight` = ?, `description` = ?, `creator` = ?, `date_created` = ?, `uuid` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `date_changed` = ?, `changed_by` = ? WHERE relationship_type_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.aIsToB.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.aIsToB.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.bIsToA.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.bIsToA.getValue().toString()) + "\""
						: null)
				+ "," + (this.preferred.getValue()) + "," + (this.weight.getValue()) + ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.retiredBy.getValue()) + ","
				+ (this.dateRetired.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateRetired.getValue())
						+ "\"" : null)
				+ ","
				+ (this.retireReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retireReason.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.relationshipTypeId.getValue()) + ","
				+ (this.aIsToB.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.aIsToB.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.bIsToA.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.bIsToA.getValue().toString()) + "\""
						: null)
				+ "," + (this.preferred.getValue()) + "," + (this.weight.getValue()) + ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.retiredBy.getValue()) + ","
				+ (this.dateRetired.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateRetired.getValue())
						+ "\"" : null)
				+ ","
				+ (this.retireReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retireReason.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		RelationshipTypeVO copy = new RelationshipTypeVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.changedBy.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.retiredBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("retiredBy"))
			return this.retiredBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "relationship_type";
	}

}