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

public class PatientIdentifierVO extends AbstractGeneratedDatabaseObject {
	private Field docId = Field.fastCreateWithType("doc_id", "VARCHAR");
	private Field patientIdentifierId = Field.fastCreateWithType("patient_identifier_id", "INT");
	private Field patientId = Field.fastCreateWithType("patient_id", "INT");
	private Field identifier = Field.fastCreateWithType("identifier", "VARCHAR");
	private Field identifierType = Field.fastCreateWithType("identifier_type", "INT");
	private Field preferred = Field.fastCreateWithType("preferred", "BIT");
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");

	public PatientIdentifierVO() {
		this.metadata = false;
		this.fields.add(this.patientIdentifierId);
		this.fields.add(this.patientId);
		this.fields.add(this.identifier);
		this.fields.add(this.docId);
		this.fields.add(this.identifierType);
		this.fields.add(this.preferred);
		this.fields.add(this.locationId);
		this.fields.add(this.creator);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
		this.fields.add(this.changedBy);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "patient_identifier_id")) {
			this.patientIdentifierId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "patient_identifier_id")) {
			return this.patientIdentifierId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			return this.patientId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "identifier")) {
			return this.identifier.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "doc_id")) {
			return this.docId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "identifier_type")) {
			return this.identifierType.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "preferred")) {
			return this.preferred.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			return this.locationId.getValue();
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
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "patient_identifier_id")) {
			this.patientIdentifierId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "patient_id")) {
			this.patientId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "identifier")) {
			this.identifier.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "doc_id")) {
			this.docId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "identifier_type")) {
			this.identifierType.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "preferred")) {
			this.preferred.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "location_id")) {
			this.locationId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.patientIdentifierId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.patientId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.identifier, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.docId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.identifierType, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.preferred, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.locationId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voided, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidReason, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.changedBy, srcConn, dstConn);
	}

	public void setDocId(Field docId) {
		this.docId = docId;
	}

	public void setDocIdValue(String value) {
		this.docId.setValue(value);
	}

	public Field getDocId() {
		return this.docId;
	}

	public void setPatientIdentifierId(Field patientIdentifierId) {
		this.patientIdentifierId = patientIdentifierId;
	}

	public void setPatientIdentifierIdValue(Integer value) {
		this.patientIdentifierId.setValue(value);
	}

	public Field getPatientIdentifierId() {
		return this.patientIdentifierId;
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

	public void setIdentifier(Field identifier) {
		this.identifier = identifier;
	}

	public void setIdentifierValue(String value) {
		this.identifier.setValue(value);
	}

	public Field getIdentifier() {
		return this.identifier;
	}

	public void setIdentifierType(Field identifierType) {
		this.identifierType = identifierType;
	}

	public void setIdentifierTypeValue(Integer value) {
		this.identifierType.setValue(value);
	}

	public Field getIdentifierType() {
		return this.identifierType;
	}

	public void setPreferred(Field preferred) {
		this.preferred = preferred;
	}

	public void setPreferredValue(Boolean value) {
		this.preferred.setValue(value);
	}

	public Field getPreferred() {
		return this.preferred;
	}

	public void setLocationId(Field locationId) {
		this.locationId = locationId;
	}

	public void setLocationIdValue(Integer value) {
		this.locationId.setValue(value);
	}

	public Field getLocationId() {
		return this.locationId;
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

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		if (getRelatedConfiguration().containsField("doc_id")) {
			String docIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
					"doc_id", "_");

			this.docId.setValue(BaseVO.retrieveFieldValue(docIdAttName, "VARCHAR", rs));
		}

		String patientIdentifierIdAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "patient_identifier_id", "_");

		this.patientIdentifierId.setValue(BaseVO.retrieveFieldValue(patientIdentifierIdAttName, "INT", rs));

		String patientIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"patient_id", "_");

		this.patientId.setValue(BaseVO.retrieveFieldValue(patientIdAttName, "INT", rs));

		String identifierAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"identifier", "_");

		this.identifier.setValue(BaseVO.retrieveFieldValue(identifierAttName, "VARCHAR", rs));

		String identifierTypeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"identifier_type", "_");

		this.identifierType.setValue(BaseVO.retrieveFieldValue(identifierTypeAttName, "INT", rs));

		String preferredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"preferred", "_");

		this.preferred.setValue(BaseVO.retrieveFieldValue(preferredAttName, "BIT", rs));

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

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
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO patient_identifier(`patient_id`, `identifier`, `identifier_type`, `preferred`, `location_id`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `date_changed`, `changed_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO patient_identifier(`patient_identifier_id`, `patient_id`, `identifier`, `identifier_type`, `preferred`, `location_id`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `date_changed`, `changed_by`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.patientId.getValue(), this.identifier.getValue(), this.identifierType.getValue(),
				this.preferred.getValue(), this.locationId.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.uuid, this.dateChanged, this.changedBy.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.patientIdentifierId.getValue(), this.patientId.getValue(), this.identifier.getValue(),
				this.identifierType.getValue(), this.preferred.getValue(), this.locationId.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid, this.dateChanged, this.changedBy.getValue() };
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
		Object[] params = { this.patientIdentifierId.getValue(), this.patientId.getValue(), this.identifier.getValue(),
				this.identifierType.getValue(), this.preferred.getValue(), this.locationId.getValue(),
				this.creator.getValue(), this.dateCreated, this.voided.getValue(), this.voidedBy.getValue(),
				this.dateVoided, this.voidReason.getValue(), this.uuid, this.dateChanged, this.changedBy.getValue(),
				this.patientIdentifierId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE patient_identifier SET `patient_identifier_id` = ?, `patient_id` = ?, `identifier` = ?, `identifier_type` = ?, `preferred` = ?, `location_id` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ?, `date_changed` = ?, `changed_by` = ? WHERE patient_identifier_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.patientId.getValue()) + ","
				+ (this.identifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.identifier.getValue().toString()) + "\""
						: null)
				+ "," + (this.identifierType.getValue()) + ","
				+ (this.preferred.getValue() != null ? "\"" + this.preferred.getValue() + "\"" : null) + ","
				+ (this.locationId.getValue()) + "," + (this.creator.getValue()) + ","
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
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.patientIdentifierId.getValue()) + "," + (this.patientId.getValue()) + ","
				+ (this.identifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.identifier.getValue().toString()) + "\""
						: null)
				+ "," + (this.identifierType.getValue()) + ","
				+ (this.preferred.getValue() != null ? "\"" + this.preferred.getValue() + "\"" : null) + ","
				+ (this.locationId.getValue()) + "," + (this.creator.getValue()) + ","
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
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PatientIdentifierVO copy = new PatientIdentifierVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.locationId.getValue() != null)
			return true;

		if (this.patientId.getValue() != null)
			return true;

		if (this.identifierType.getValue() != null)
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
		if (parentAttName.equals("locationId"))
			return this.locationId.getValue();
		if (parentAttName.equals("patientId"))
			return this.patientId.getValue();
		if (parentAttName.equals("identifierType"))
			return this.identifierType.getValue();
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
		return "patient_identifier";
	}

}