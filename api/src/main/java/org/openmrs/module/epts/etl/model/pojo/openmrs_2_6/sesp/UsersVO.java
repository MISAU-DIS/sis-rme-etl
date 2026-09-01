package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp;

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

public class UsersVO extends AbstractDatabaseObject implements EtlDatabaseObject {
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
	private Field activationKey = Field.fastCreateWithType("activation_key", "VARCHAR");
	private Field email = Field.fastCreateWithType("email", "VARCHAR");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public UsersVO() {
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

		this.dateCreated = rs.getTimestamp(dateCreatedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateCreatedAttName).getTime())
				: null;

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = rs.getTimestamp(dateChangedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateChangedAttName).getTime())
				: null;

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

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);

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
		return "INSERT INTO users(`system_id`, `username`, `password`, `salt`, `secret_question`, `secret_answer`, `creator`, `date_created`, `changed_by`, `date_changed`, `person_id`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `activation_key`, `email`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO users(`user_id`, `system_id`, `username`, `password`, `salt`, `secret_question`, `secret_answer`, `creator`, `date_created`, `changed_by`, `date_changed`, `person_id`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `activation_key`, `email`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.systemId.getValue(), this.username.getValue(), this.password.getValue(),
				this.salt.getValue(), this.secretQuestion.getValue(), this.secretAnswer.getValue(),
				this.creator.getValue(), this.dateCreated, this.changedBy.getValue(), this.dateChanged,
				this.personId.getValue(), this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.activationKey.getValue(),
				this.email.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.userId.getValue(), this.systemId.getValue(), this.username.getValue(),
				this.password.getValue(), this.salt.getValue(), this.secretQuestion.getValue(),
				this.secretAnswer.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.personId.getValue(), this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.activationKey.getValue(),
				this.email.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.userId.getValue(), this.systemId.getValue(), this.username.getValue(),
				this.password.getValue(), this.salt.getValue(), this.secretQuestion.getValue(),
				this.secretAnswer.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.personId.getValue(), this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid, this.activationKey.getValue(),
				this.email.getValue(), this.userId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE users SET `user_id` = ?, `system_id` = ?, `username` = ?, `password` = ?, `salt` = ?, `secret_question` = ?, `secret_answer` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `person_id` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ?, `activation_key` = ?, `email` = ? WHERE users_7.user_id = ? ";
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
				+ ","
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
				+ ","
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

		copy.userId = copyGeneratedField(this.userId);
		copy.systemId = copyGeneratedField(this.systemId);
		copy.username = copyGeneratedField(this.username);
		copy.password = copyGeneratedField(this.password);
		copy.salt = copyGeneratedField(this.salt);
		copy.secretQuestion = copyGeneratedField(this.secretQuestion);
		copy.secretAnswer = copyGeneratedField(this.secretAnswer);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.changedBy = copyGeneratedField(this.changedBy);
		copy.dateChanged = this.dateChanged;
		copy.personId = copyGeneratedField(this.personId);
		copy.retired = copyGeneratedField(this.retired);
		copy.retiredBy = copyGeneratedField(this.retiredBy);
		copy.dateRetired = copyGeneratedField(this.dateRetired);
		copy.retireReason = copyGeneratedField(this.retireReason);
		copy.uuid = this.uuid;
		copy.activationKey = copyGeneratedField(this.activationKey);

		return copy;
	}

	@JsonIgnore
	@Override
	public void copyFrom(EtlDatabaseObject toCopyFrom) {
		if (toCopyFrom instanceof UsersVO) {
			UsersVO toCopyFromAsUsersVO = (UsersVO) toCopyFrom;

			this.userId = copyGeneratedField(toCopyFromAsUsersVO.userId);
			this.systemId = copyGeneratedField(toCopyFromAsUsersVO.systemId);
			this.username = copyGeneratedField(toCopyFromAsUsersVO.username);
			this.password = copyGeneratedField(toCopyFromAsUsersVO.password);
			this.salt = copyGeneratedField(toCopyFromAsUsersVO.salt);
			this.secretQuestion = copyGeneratedField(toCopyFromAsUsersVO.secretQuestion);
			this.secretAnswer = copyGeneratedField(toCopyFromAsUsersVO.secretAnswer);
			this.creator = copyGeneratedField(toCopyFromAsUsersVO.creator);
			this.dateCreated = toCopyFromAsUsersVO.dateCreated;
			this.changedBy = copyGeneratedField(toCopyFromAsUsersVO.changedBy);
			this.dateChanged = toCopyFromAsUsersVO.dateChanged;
			this.personId = copyGeneratedField(toCopyFromAsUsersVO.personId);
			this.retired = copyGeneratedField(toCopyFromAsUsersVO.retired);
			this.retiredBy = copyGeneratedField(toCopyFromAsUsersVO.retiredBy);
			this.dateRetired = copyGeneratedField(toCopyFromAsUsersVO.dateRetired);
			this.retireReason = copyGeneratedField(toCopyFromAsUsersVO.retireReason);
			this.uuid = toCopyFromAsUsersVO.uuid;
			this.activationKey = copyGeneratedField(toCopyFromAsUsersVO.activationKey);

		}
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