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

public class AllergyVO extends AbstractGeneratedDatabaseObject {
	private Field severityConceptId = Field.fastCreateWithType("severity_concept_id", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field allergyId = Field.fastCreateWithType("allergy_id", "INT");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field codedAllergen = Field.fastCreateWithType("coded_allergen", "INT");
	private Field nonCodedAllergen = Field.fastCreateWithType("non_coded_allergen", "VARCHAR");
	private Field allergenType = Field.fastCreateWithType("allergen_type", "VARCHAR");
	private Field comments = Field.fastCreateWithType("comments", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");

	public AllergyVO() {
		this.metadata = false;
		this.fields.add(this.allergyId);
		this.fields.add(this.patientId);
		this.fields.add(this.severityConceptId);
		this.fields.add(this.codedAllergen);
		this.fields.add(this.nonCodedAllergen);
		this.fields.add(this.allergenType);
		this.fields.add(this.comments);
		this.fields.add(this.creator);
		this.fields.add(this.changedBy);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "allergy_id")) {
			this.allergyId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "allergy_id")) {
			return this.allergyId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "severity_concept_id")) {
			return this.severityConceptId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "coded_allergen")) {
			return this.codedAllergen.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "non_coded_allergen")) {
			return this.nonCodedAllergen.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "allergen_type")) {
			return this.allergenType.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "comments")) {
			return this.comments.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
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
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "allergy_id")) {
			this.allergyId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			this.patientId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "severity_concept_id")) {
			this.severityConceptId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "coded_allergen")) {
			this.codedAllergen.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "non_coded_allergen")) {
			this.nonCodedAllergen.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "allergen_type")) {
			this.allergenType.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "comments")) {
			this.comments.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			this.creator.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			this.changedBy.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.allergyId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.patientId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.severityConceptId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.codedAllergen, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.nonCodedAllergen, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.allergenType, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.comments, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.changedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voided, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidReason, srcConn, dstConn);
	}

	public void setSeverityConceptId(Field severityConceptId) {
		this.severityConceptId = severityConceptId;
	}

	public void setSeverityConceptIdValue(Integer value) {
		this.severityConceptId.setValue(value);
	}

	public Field getSeverityConceptId() {
		return this.severityConceptId;
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

	public void setAllergyId(Field allergyId) {
		this.allergyId = allergyId;
	}

	public void setAllergyIdValue(Integer value) {
		this.allergyId.setValue(value);
	}

	public Field getAllergyId() {
		return this.allergyId;
	}

	public void setPatientId(Field patientId) {
		this.patientId = patientId;
	}

	public void setPatientIdValue(Integer value) {
		this.patientId.setValue(value);
	}

	public Field getPatientId() {
		return this.patientId;
	}

	public void setCodedAllergen(Field codedAllergen) {
		this.codedAllergen = codedAllergen;
	}

	public void setCodedAllergenValue(Integer value) {
		this.codedAllergen.setValue(value);
	}

	public Field getCodedAllergen() {
		return this.codedAllergen;
	}

	public void setNonCodedAllergen(Field nonCodedAllergen) {
		this.nonCodedAllergen = nonCodedAllergen;
	}

	public void setNonCodedAllergenValue(String value) {
		this.nonCodedAllergen.setValue(value);
	}

	public Field getNonCodedAllergen() {
		return this.nonCodedAllergen;
	}

	public void setAllergenType(Field allergenType) {
		this.allergenType = allergenType;
	}

	public void setAllergenTypeValue(String value) {
		this.allergenType.setValue(value);
	}

	public Field getAllergenType() {
		return this.allergenType;
	}

	public void setComments(Field comments) {
		this.comments = comments;
	}

	public void setCommentsValue(String value) {
		this.comments.setValue(value);
	}

	public Field getComments() {
		return this.comments;
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

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		if (getRelatedConfiguration().containsField("severity_concept_id")) {
			String severityConceptIdAttName = utilities
					.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "severity_concept_id", "_");

			this.severityConceptId.setValue(BaseVO.retrieveFieldValue(severityConceptIdAttName, "INT", rs));
		}

		if (getRelatedConfiguration().containsField("changed_by")) {
			String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"changed_by", "_");

			this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));
		}

		String allergyIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"allergy_id", "_");

		this.allergyId.setValue(BaseVO.retrieveFieldValue(allergyIdAttName, "INT", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String codedAllergenAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"coded_allergen", "_");

		this.codedAllergen.setValue(BaseVO.retrieveFieldValue(codedAllergenAttName, "INT", rs));

		String nonCodedAllergenAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"non_coded_allergen", "_");

		this.nonCodedAllergen.setValue(BaseVO.retrieveFieldValue(nonCodedAllergenAttName, "VARCHAR", rs));

		String allergenTypeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"allergen_type", "_");

		this.allergenType.setValue(BaseVO.retrieveFieldValue(allergenTypeAttName, "VARCHAR", rs));

		String commentsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"comments", "_");

		this.comments.setValue(BaseVO.retrieveFieldValue(commentsAttName, "VARCHAR", rs));

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
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO allergy(`patient_id`, `coded_allergen`, `non_coded_allergen`, `allergen_type`, `comments`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO allergy(`allergy_id`, `patient_id`, `coded_allergen`, `non_coded_allergen`, `allergen_type`, `comments`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.patientId.getValue(), this.codedAllergen.getValue(), this.nonCodedAllergen.getValue(),
				this.allergenType.getValue(), this.comments.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.allergyId.getValue(), this.patientId.getValue(), this.codedAllergen.getValue(),
				this.nonCodedAllergen.getValue(), this.allergenType.getValue(), this.comments.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.allergyId.getValue(), this.patientId.getValue(), this.codedAllergen.getValue(),
				this.nonCodedAllergen.getValue(), this.allergenType.getValue(), this.comments.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid, this.allergyId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE allergy SET `allergy_id` = ?, `patient_id` = ?, `coded_allergen` = ?, `non_coded_allergen` = ?, `allergen_type` = ?, `comments` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ? WHERE allergy_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.patientId.getValue()) + "," + (this.codedAllergen.getValue()) + ","
				+ (this.nonCodedAllergen.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.nonCodedAllergen.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.allergenType.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.allergenType.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.comments.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.comments.getValue().toString()) + "\""
						: null)
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
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.allergyId.getValue()) + "," + (this.patientId.getValue()) + ","
				+ (this.codedAllergen.getValue()) + ","
				+ (this.nonCodedAllergen.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.nonCodedAllergen.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.allergenType.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.allergenType.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.comments.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.comments.getValue().toString()) + "\""
						: null)
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
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		AllergyVO copy = new AllergyVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.codedAllergen.getValue() != null)
			return true;

		if (this.patientId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("codedAllergen"))
			return this.codedAllergen.getValue();
		if (parentAttName.equals("patientId"))
			return this.patientId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "allergy";
	}

}