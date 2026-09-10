package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sisrme.sync;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.conf.Key;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SisRmeSyncMsgVO extends AbstractGeneratedDatabaseObject {
	private Field id = Field.fastCreateWithType("id", "BIGINT");
	private Field tableName = Field.fastCreateWithType("table_name", "VARCHAR");
	private Field identifier = Field.fastCreateWithType("identifier", "VARCHAR");
	private Field originSiteUuid = Field.fastCreateWithType("origin_site_uuid", "VARCHAR");
	private Field entityPayload = Field.fastCreateWithType("entity_payload", "TEXT");
	private Field operation = Field.fastCreateWithType("operation", "VARCHAR");
	private Field dateSent = Field.fastCreateWithType("date_sent", "DATETIME");
	private Field dateReceived = Field.fastCreateWithType("date_received", "DATETIME");
	private Field messageUuid = Field.fastCreateWithType("message_uuid", "VARCHAR");
	private Field processingStatus = Field.fastCreateWithType("processing_status", "CHAR");
	private Field processingDate = Field.fastCreateWithType("processing_date", "DATETIME");
	private Field processingError = Field.fastCreateWithType("processing_error", "TEXT");
	private Field retryCount = Field.fastCreateWithType("retry_count", "INT");

	public SisRmeSyncMsgVO() {
		this.metadata = false;
		this.fields.add(this.id);
		this.fields.add(this.tableName);
		this.fields.add(this.identifier);
		this.fields.add(this.originSiteUuid);
		this.fields.add(this.entityPayload);
		this.fields.add(this.operation);
		this.fields.add(this.dateSent);
		this.fields.add(this.dateReceived);
		this.fields.add(this.messageUuid);
		this.fields.add(this.processingStatus);
		this.fields.add(this.processingDate);
		this.fields.add(this.processingError);
		this.fields.add(this.retryCount);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "id")) {
			this.id.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "id")) {
			return this.id.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "table_name")) {
			return this.tableName.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "identifier")) {
			return this.identifier.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "origin_site_uuid")) {
			return this.originSiteUuid.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "entity_payload")) {
			return this.entityPayload.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "operation")) {
			return this.operation.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_sent")) {
			return this.dateSent.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_received")) {
			return this.dateReceived.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "message_uuid")) {
			return this.messageUuid.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "processing_status")) {
			return this.processingStatus.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "processing_date")) {
			return this.processingDate.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "processing_error")) {
			return this.processingError.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "retry_count")) {
			return this.retryCount.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "id")) {
			this.id.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "table_name")) {
			this.tableName.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "identifier")) {
			this.identifier.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "origin_site_uuid")) {
			this.originSiteUuid.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "entity_payload")) {
			this.entityPayload.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "operation")) {
			this.operation.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "date_sent")) {
			this.dateSent.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "date_received")) {
			this.dateReceived.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "message_uuid")) {
			this.messageUuid.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "processing_status")) {
			this.processingStatus.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "processing_date")) {
			this.processingDate.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "processing_error")) {
			this.processingError.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "retry_count")) {
			this.retryCount.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.id, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.tableName, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.identifier, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.originSiteUuid, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.entityPayload, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.operation, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dateSent, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.dateReceived, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.messageUuid, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.processingStatus, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.processingDate, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.processingError, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.retryCount, srcConn, dstConn);
	}

	public void setId(Field id) {
		this.id = id;
	}

	public void setIdValue(Integer value) {
		this.id.setValue(value);
	}

	public Field getId() {
		return this.id;
	}

	public void setTableName(Field tableName) {
		this.tableName = tableName;
	}

	public void setTableNameValue(String value) {
		this.tableName.setValue(value);
	}

	public Field getTableName() {
		return this.tableName;
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

	public void setOriginSiteUuid(Field originSiteUuid) {
		this.originSiteUuid = originSiteUuid;
	}

	public void setOriginSiteUuidValue(String value) {
		this.originSiteUuid.setValue(value);
	}

	public Field getOriginSiteUuid() {
		return this.originSiteUuid;
	}

	public void setEntityPayload(Field entityPayload) {
		this.entityPayload = entityPayload;
	}

	public void setEntityPayloadValue(String value) {
		this.entityPayload.setValue(value);
	}

	public Field getEntityPayload() {
		return this.entityPayload;
	}

	public void setOperation(Field operation) {
		this.operation = operation;
	}

	public void setOperationValue(String value) {
		this.operation.setValue(value);
	}

	public Field getOperation() {
		return this.operation;
	}

	public void setDateSent(Field dateSent) {
		this.dateSent = dateSent;
	}

	public void setDateSentValue(java.util.Date value) {
		this.dateSent.setValue(value);
	}

	public Field getDateSent() {
		return this.dateSent;
	}

	public void setDateReceived(Field dateReceived) {
		this.dateReceived = dateReceived;
	}

	public void setDateReceivedValue(java.util.Date value) {
		this.dateReceived.setValue(value);
	}

	public Field getDateReceived() {
		return this.dateReceived;
	}

	public void setMessageUuid(Field messageUuid) {
		this.messageUuid = messageUuid;
	}

	public void setMessageUuidValue(String value) {
		this.messageUuid.setValue(value);
	}

	public Field getMessageUuid() {
		return this.messageUuid;
	}

	public void setProcessingStatus(Field processingStatus) {
		this.processingStatus = processingStatus;
	}

	public void setProcessingStatusValue(String value) {
		this.processingStatus.setValue(value);
	}

	public Field getProcessingStatus() {
		return this.processingStatus;
	}

	public void setProcessingDate(Field processingDate) {
		this.processingDate = processingDate;
	}

	public void setProcessingDateValue(java.util.Date value) {
		this.processingDate.setValue(value);
	}

	public Field getProcessingDate() {
		return this.processingDate;
	}

	public void setProcessingError(Field processingError) {
		this.processingError = processingError;
	}

	public void setProcessingErrorValue(String value) {
		this.processingError.setValue(value);
	}

	public Field getProcessingError() {
		return this.processingError;
	}

	public void setRetryCount(Field retryCount) {
		this.retryCount = retryCount;
	}

	public void setRetryCountValue(Integer value) {
		this.retryCount.setValue(value);
	}

	public Field getRetryCount() {
		return this.retryCount;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String idAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "id", "_");

		this.id.setValue(BaseVO.retrieveFieldValue(idAttName, "BIGINT", rs));

		String tableNameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"table_name", "_");

		this.tableName.setValue(BaseVO.retrieveFieldValue(tableNameAttName, "VARCHAR", rs));

		String identifierAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"identifier", "_");

		this.identifier.setValue(BaseVO.retrieveFieldValue(identifierAttName, "VARCHAR", rs));

		String originSiteUuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"origin_site_uuid", "_");

		this.originSiteUuid.setValue(BaseVO.retrieveFieldValue(originSiteUuidAttName, "VARCHAR", rs));

		String entityPayloadAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"entity_payload", "_");

		this.entityPayload.setValue(BaseVO.retrieveFieldValue(entityPayloadAttName, "TEXT", rs));

		String operationAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"operation", "_");

		this.operation.setValue(BaseVO.retrieveFieldValue(operationAttName, "VARCHAR", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String dateSentAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_sent", "_");

		this.dateSent.setValue(BaseVO.retrieveFieldValue(dateSentAttName, "DATETIME", rs));

		String dateReceivedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_received", "_");

		this.dateReceived.setValue(BaseVO.retrieveFieldValue(dateReceivedAttName, "DATETIME", rs));

		String messageUuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"message_uuid", "_");

		this.messageUuid.setValue(BaseVO.retrieveFieldValue(messageUuidAttName, "VARCHAR", rs));

		String processingStatusAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"processing_status", "_");

		this.processingStatus.setValue(BaseVO.retrieveFieldValue(processingStatusAttName, "CHAR", rs));

		String processingDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"processing_date", "_");

		this.processingDate.setValue(BaseVO.retrieveFieldValue(processingDateAttName, "DATETIME", rs));

		String processingErrorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"processing_error", "_");

		this.processingError.setValue(BaseVO.retrieveFieldValue(processingErrorAttName, "TEXT", rs));

		String retryCountAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"retry_count", "_");

		this.retryCount.setValue(BaseVO.retrieveFieldValue(retryCountAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO sis_rme_sync_msg(`table_name`, `identifier`, `origin_site_uuid`, `entity_payload`, `operation`, `date_created`, `date_sent`, `date_received`, `message_uuid`, `processing_status`, `processing_date`, `processing_error`, `retry_count`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO sis_rme_sync_msg(`id`, `table_name`, `identifier`, `origin_site_uuid`, `entity_payload`, `operation`, `date_created`, `date_sent`, `date_received`, `message_uuid`, `processing_status`, `processing_date`, `processing_error`, `retry_count`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.tableName.getValue(), this.identifier.getValue(), this.originSiteUuid.getValue(),
				this.entityPayload.getValue(), this.operation.getValue(), this.dateCreated, this.dateSent.getValue(),
				this.dateReceived.getValue(), this.messageUuid.getValue(), this.processingStatus.getValue(),
				this.processingDate.getValue(), this.processingError.getValue(), this.retryCount.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.id.getValue(), this.tableName.getValue(), this.identifier.getValue(),
				this.originSiteUuid.getValue(), this.entityPayload.getValue(), this.operation.getValue(),
				this.dateCreated, this.dateSent.getValue(), this.dateReceived.getValue(), this.messageUuid.getValue(),
				this.processingStatus.getValue(), this.processingDate.getValue(), this.processingError.getValue(),
				this.retryCount.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.id.getValue(), this.tableName.getValue(), this.identifier.getValue(),
				this.originSiteUuid.getValue(), this.entityPayload.getValue(), this.operation.getValue(),
				this.dateCreated, this.dateSent.getValue(), this.dateReceived.getValue(), this.messageUuid.getValue(),
				this.processingStatus.getValue(), this.processingDate.getValue(), this.processingError.getValue(),
				this.retryCount.getValue(), this.id.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE sis_rme_sync_msg SET `id` = ?, `table_name` = ?, `identifier` = ?, `origin_site_uuid` = ?, `entity_payload` = ?, `operation` = ?, `date_created` = ?, `date_sent` = ?, `date_received` = ?, `message_uuid` = ?, `processing_status` = ?, `processing_date` = ?, `processing_error` = ?, `retry_count` = ? WHERE id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.tableName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.tableName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.identifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.identifier.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.originSiteUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.originSiteUuid.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.entityPayload.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.entityPayload.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.operation.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.operation.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dateCreated != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\"" : null)
				+ ","
				+ (this.dateSent.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateSent.getValue()) + "\""
						: null)
				+ ","
				+ (this.dateReceived.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateReceived.getValue())
						+ "\"" : null)
				+ ","
				+ (this.messageUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.messageUuid.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.processingStatus.getValue() != null ? "\""
						+ utilities.scapeQuotationMarks(this.processingStatus.getValue().toString()) + "\"" : null)
				+ ","
				+ (this.processingDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.processingDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.processingError.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.processingError.getValue().toString()) + "\""
						: null)
				+ "," + (this.retryCount.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.id.getValue()) + ","
				+ (this.tableName.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.tableName.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.identifier.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.identifier.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.originSiteUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.originSiteUuid.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.entityPayload.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.entityPayload.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.operation.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.operation.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ ","
				+ (this.dateSent.getValue() != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateSent.getValue())
								+ "\""
						: null)
				+ ","
				+ (this.dateReceived.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateReceived.getValue())
						+ "\"" : null)
				+ ","
				+ (this.messageUuid.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.messageUuid.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.processingStatus.getValue() != null ? "\""
						+ utilities.scapeQuotationMarks(this.processingStatus.getValue().toString()) + "\"" : null)
				+ ","
				+ (this.processingDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.processingDate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.processingError.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.processingError.getValue().toString()) + "\""
						: null)
				+ "," + (this.retryCount.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		SisRmeSyncMsgVO copy = new SisRmeSyncMsgVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
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
		return "sis_rme_sync_msg";
	}

}