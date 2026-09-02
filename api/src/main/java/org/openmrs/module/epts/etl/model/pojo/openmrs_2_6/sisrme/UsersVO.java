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

public class UsersVO extends AbstractGeneratedDatabaseObject {
	private Field userId = Field.fastCreateWithType("user_id", "INT");
	private Field systemId = Field.fastCreateWithType("system_id", "VARCHAR");
	private Field username = Field.fastCreateWithType("username", "VARCHAR");
	private Field password = Field.fastCreateWithType("password", "VARCHAR");
	private Field salt = Field.fastCreateWithType("salt", "VARCHAR");
	private Field secretQuestion = Field.fastCreateWithType("secret_question", "VARCHAR");
	private Field secretAnswer = Field.fastCreateWithType("secret_answer", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field personId = Field.fastCreateWithType("person_id", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");
	private Field creatorId = Field.fastCreateWithType("creator_id", "BIGINT");
	private Field activationKey = Field.fastCreateWithType("activation_key", "VARCHAR");
	private Field email = Field.fastCreateWithType("email", "VARCHAR");

	public UsersVO() {
		this.metadata = false;

		this.fields.add(this.userId);
		this.fields.add(this.systemId);
		this.fields.add(this.username);
		this.fields.add(this.password);
		this.fields.add(this.salt);
		this.fields.add(this.secretQuestion);
		this.fields.add(this.secretAnswer);
		this.fields.add(this.creator);
		this.fields.add(this.changedBy);
		this.fields.add(this.personId);
		this.fields.add(this.retired);
		this.fields.add(this.retiredBy);
		this.fields.add(this.dateRetired);
		this.fields.add(this.retireReason);
		this.fields.add(this.creatorId);
		this.fields.add(this.activationKey);
		this.fields.add(this.email);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "user_id")) {
			this.userId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "user_id")) {
			return this.userId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "system_id")) {
			return this.systemId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "username")) {
			return this.username.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "password")) {
			return this.password.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "salt")) {
			return this.salt.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "secret_question")) {
			return this.secretQuestion.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "secret_answer")) {
			return this.secretAnswer.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "creator")) {
			return this.creator.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "changed_by")) {
			return this.changedBy.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "person_id")) {
			return this.personId.getValue();
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
		if (utilities.equalsFieldsName(fieldName, "creator_id")) {
			return this.creatorId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "activation_key")) {
			return this.activationKey.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "email")) {
			return this.email.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "date_created")) return this.dateCreated;
		if (utilities.equalsFieldsName(fieldName, "date_changed")) return this.dateChanged;
		if (utilities.equalsFieldsName(fieldName, "date_voided")) return this.dateVoided;
		if (utilities.equalsFieldsName(fieldName, "uuid")) return this.uuid;
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

	public void setUserId(Field userId) {
		this.userId = userId;
	}

	public void setUserIdValue(Integer value) {
		this.userId.setValue(value);
	}

	public Field getUserId() {
		return this.userId;
	}

	public void setSystemId(Field systemId) {
		this.systemId = systemId;
	}

	public void setSystemIdValue(String value) {
		this.systemId.setValue(value);
	}

	public Field getSystemId() {
		return this.systemId;
	}

	public void setUsername(Field username) {
		this.username = username;
	}

	public void setUsernameValue(String value) {
		this.username.setValue(value);
	}

	public Field getUsername() {
		return this.username;
	}

	public void setPassword(Field password) {
		this.password = password;
	}

	public void setPasswordValue(String value) {
		this.password.setValue(value);
	}

	public Field getPassword() {
		return this.password;
	}

	public void setSalt(Field salt) {
		this.salt = salt;
	}

	public void setSaltValue(String value) {
		this.salt.setValue(value);
	}

	public Field getSalt() {
		return this.salt;
	}

	public void setSecretQuestion(Field secretQuestion) {
		this.secretQuestion = secretQuestion;
	}

	public void setSecretQuestionValue(String value) {
		this.secretQuestion.setValue(value);
	}

	public Field getSecretQuestion() {
		return this.secretQuestion;
	}

	public void setSecretAnswer(Field secretAnswer) {
		this.secretAnswer = secretAnswer;
	}

	public void setSecretAnswerValue(String value) {
		this.secretAnswer.setValue(value);
	}

	public Field getSecretAnswer() {
		return this.secretAnswer;
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

	public void setChangedBy(Field changedBy) {
		this.changedBy = changedBy;
	}

	public void setChangedByValue(Integer value) {
		this.changedBy.setValue(value);
	}

	public Field getChangedBy() {
		return this.changedBy;
	}

	public void setPersonId(Field personId) {
		this.personId = personId;
	}

	public void setPersonIdValue(Integer value) {
		this.personId.setValue(value);
	}

	public Field getPersonId() {
		return this.personId;
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

	public void setCreatorId(Field creatorId) {
		this.creatorId = creatorId;
	}

	public void setCreatorIdValue(Integer value) {
		this.creatorId.setValue(value);
	}

	public Field getCreatorId() {
		return this.creatorId;
	}

	public void setActivationKey(Field activationKey) {
		this.activationKey = activationKey;
	}

	public void setActivationKeyValue(String value) {
		this.activationKey.setValue(value);
	}

	public Field getActivationKey() {
		return this.activationKey;
	}

	public void setEmail(Field email) {
		this.email = email;
	}

	public void setEmailValue(String value) {
		this.email.setValue(value);
	}

	public Field getEmail() {
		return this.email;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String userIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"user_id", "_");

		this.userId.setValue(BaseVO.retrieveFieldValue(userIdAttName, "INT", rs));

		String systemIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"system_id", "_");

		this.systemId.setValue(BaseVO.retrieveFieldValue(systemIdAttName, "VARCHAR", rs));

		String usernameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"username", "_");

		this.username.setValue(BaseVO.retrieveFieldValue(usernameAttName, "VARCHAR", rs));

		String passwordAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"password", "_");

		this.password.setValue(BaseVO.retrieveFieldValue(passwordAttName, "VARCHAR", rs));

		String saltAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "salt",
				"_");

		this.salt.setValue(BaseVO.retrieveFieldValue(saltAttName, "VARCHAR", rs));

		String secretQuestionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"secret_question", "_");

		this.secretQuestion.setValue(BaseVO.retrieveFieldValue(secretQuestionAttName, "VARCHAR", rs));

		String secretAnswerAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"secret_answer", "_");

		this.secretAnswer.setValue(BaseVO.retrieveFieldValue(secretAnswerAttName, "VARCHAR", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String personIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_id", "_");

		this.personId.setValue(BaseVO.retrieveFieldValue(personIdAttName, "INT", rs));

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

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));

		String creatorIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator_id", "_");

		this.creatorId.setValue(BaseVO.retrieveFieldValue(creatorIdAttName, "BIGINT", rs));

		String activationKeyAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"activation_key", "_");

		this.activationKey.setValue(BaseVO.retrieveFieldValue(activationKeyAttName, "VARCHAR", rs));

		String emailAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "email",
				"_");

		this.email.setValue(BaseVO.retrieveFieldValue(emailAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO users(`system_id`, `username`, `password`, `salt`, `secret_question`, `secret_answer`, `creator`, `date_created`, `changed_by`, `date_changed`, `person_id`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `creator_id`, `activation_key`, `email`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO users(`user_id`, `system_id`, `username`, `password`, `salt`, `secret_question`, `secret_answer`, `creator`, `date_created`, `changed_by`, `date_changed`, `person_id`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `creator_id`, `activation_key`, `email`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.systemId.getValue(), this.username.getValue(), this.password.getValue(),
				this.salt.getValue(), this.secretQuestion.getValue(), this.secretAnswer.getValue(),
				this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.personId.getValue(), this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.creatorId.getValue(),
				this.activationKey.getValue(), this.email.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.userId.getValue(), this.systemId.getValue(), this.username.getValue(),
				this.password.getValue(), this.salt.getValue(), this.secretQuestion.getValue(),
				this.secretAnswer.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.personId.getValue(), this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.creatorId.getValue(),
				this.activationKey.getValue(), this.email.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.userId.getValue(), this.systemId.getValue(), this.username.getValue(),
				this.password.getValue(), this.salt.getValue(), this.secretQuestion.getValue(),
				this.secretAnswer.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.personId.getValue(), this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.creatorId.getValue(),
				this.activationKey.getValue(), this.email.getValue(), this.userId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE users SET `user_id` = ?, `system_id` = ?, `username` = ?, `password` = ?, `salt` = ?, `secret_question` = ?, `secret_answer` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `person_id` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ?, `creator_id` = ?, `activation_key` = ?, `email` = ? WHERE user_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.systemId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.systemId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.username.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.username.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.password.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.password.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.salt.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.salt.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.secretQuestion.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.secretQuestion.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.secretAnswer.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.secretAnswer.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.personId.getValue()) + ","
				+ (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.retiredBy.getValue()) + ","
				+ (this.dateRetired.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateRetired.getValue())
						+ "\"" : null)
				+ ","
				+ (this.retireReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retireReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.creatorId.getValue()) + ","
				+ (this.activationKey.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.activationKey.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.email.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.email.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.userId.getValue()) + ","
				+ (this.systemId.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.systemId.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.username.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.username.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.password.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.password.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.salt.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.salt.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.secretQuestion.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.secretQuestion.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.secretAnswer.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.secretAnswer.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.personId.getValue()) + ","
				+ (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
				+ (this.retiredBy.getValue()) + ","
				+ (this.dateRetired.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateRetired.getValue())
						+ "\"" : null)
				+ ","
				+ (this.retireReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.retireReason.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null)
				+ "," + (this.creatorId.getValue()) + ","
				+ (this.activationKey.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.activationKey.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.email.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.email.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		UsersVO copy = new UsersVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.personId.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		if (this.retiredBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("personId"))
			return this.personId.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("retiredBy"))
			return this.retiredBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "users";
	}

}