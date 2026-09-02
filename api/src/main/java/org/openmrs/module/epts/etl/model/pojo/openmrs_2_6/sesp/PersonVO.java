package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp;

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

public class PersonVO extends AbstractGeneratedDatabaseObject {
	private Field personId = Field.fastCreateWithType("person_id", "INT");
	private Field gender = Field.fastCreateWithType("gender", "VARCHAR");
	private Field birthdate = Field.fastCreateWithType("birthdate", "DATE");
	private Field birthdateEstimated = Field.fastCreateWithType("birthdate_estimated", "BIT");
	private Field dead = Field.fastCreateWithType("dead", "BIT");
	private Field deathDate = Field.fastCreateWithType("death_date", "DATETIME");
	private Field causeOfDeath = Field.fastCreateWithType("cause_of_death", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field deathdateEstimated = Field.fastCreateWithType("deathdate_estimated", "BIT");
	private Field birthtime = Field.fastCreateWithType("birthtime", "TIME");
	private Field causeOfDeathNonCoded = Field.fastCreateWithType("cause_of_death_non_coded", "VARCHAR");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PersonVO() {
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

	public void setPersonId(Field personId) {
		this.personId = personId;
	}

	public void setPersonIdValue(Integer value) {
		this.personId.setValue(value);
	}

	public Field getPersonId() {
		return this.personId;
	}

	public void setGender(Field gender) {
		this.gender = gender;
	}

	public void setGenderValue(String value) {
		this.gender.setValue(value);
	}

	public Field getGender() {
		return this.gender;
	}

	public void setBirthdate(Field birthdate) {
		this.birthdate = birthdate;
	}

	public void setBirthdateValue(java.util.Date value) {
		this.birthdate.setValue(value);
	}

	public Field getBirthdate() {
		return this.birthdate;
	}

	public void setBirthdateEstimated(Field birthdateEstimated) {
		this.birthdateEstimated = birthdateEstimated;
	}

	public void setBirthdateEstimatedValue(Boolean value) {
		this.birthdateEstimated.setValue(value);
	}

	public Field getBirthdateEstimated() {
		return this.birthdateEstimated;
	}

	public void setDead(Field dead) {
		this.dead = dead;
	}

	public void setDeadValue(Boolean value) {
		this.dead.setValue(value);
	}

	public Field getDead() {
		return this.dead;
	}

	public void setDeathDate(Field deathDate) {
		this.deathDate = deathDate;
	}

	public void setDeathDateValue(java.util.Date value) {
		this.deathDate.setValue(value);
	}

	public Field getDeathDate() {
		return this.deathDate;
	}

	public void setCauseOfDeath(Field causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}

	public void setCauseOfDeathValue(Integer value) {
		this.causeOfDeath.setValue(value);
	}

	public Field getCauseOfDeath() {
		return this.causeOfDeath;
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

	public void setDeathdateEstimated(Field deathdateEstimated) {
		this.deathdateEstimated = deathdateEstimated;
	}

	public void setDeathdateEstimatedValue(Boolean value) {
		this.deathdateEstimated.setValue(value);
	}

	public Field getDeathdateEstimated() {
		return this.deathdateEstimated;
	}

	public void setBirthtime(Field birthtime) {
		this.birthtime = birthtime;
	}

	public void setBirthtimeValue(java.util.Date value) {
		this.birthtime.setValue(value);
	}

	public Field getBirthtime() {
		return this.birthtime;
	}

	public void setCauseOfDeathNonCoded(Field causeOfDeathNonCoded) {
		this.causeOfDeathNonCoded = causeOfDeathNonCoded;
	}

	public void setCauseOfDeathNonCodedValue(String value) {
		this.causeOfDeathNonCoded.setValue(value);
	}

	public Field getCauseOfDeathNonCoded() {
		return this.causeOfDeathNonCoded;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String personIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_id", "_");

		this.personId.setValue(BaseVO.retrieveFieldValue(personIdAttName, "INT", rs));

		String genderAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "gender",
				"_");

		this.gender.setValue(BaseVO.retrieveFieldValue(genderAttName, "VARCHAR", rs));

		String birthdateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"birthdate", "_");

		this.birthdate.setValue(BaseVO.retrieveFieldValue(birthdateAttName, "DATE", rs));

		String birthdateEstimatedAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "birthdate_estimated", "_");

		this.birthdateEstimated.setValue(BaseVO.retrieveFieldValue(birthdateEstimatedAttName, "BIT", rs));

		String deadAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "dead",
				"_");

		this.dead.setValue(BaseVO.retrieveFieldValue(deadAttName, "BIT", rs));

		String deathDateAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"death_date", "_");

		this.deathDate.setValue(BaseVO.retrieveFieldValue(deathDateAttName, "DATETIME", rs));

		String causeOfDeathAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"cause_of_death", "_");

		this.causeOfDeath.setValue(BaseVO.retrieveFieldValue(causeOfDeathAttName, "INT", rs));

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

		String voidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "voided",
				"_");

		this.voided.setValue(BaseVO.retrieveFieldValue(voidedAttName, "BIT", rs));

		String voidedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"voided_by", "_");

		this.voidedBy.setValue(BaseVO.retrieveFieldValue(voidedByAttName, "INT", rs));

		String dateVoidedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_voided", "_");

		this.dateVoided = rs.getTimestamp(dateVoidedAttName) != null
				? new java.util.Date(rs.getTimestamp(dateVoidedAttName).getTime())
				: null;

		String voidReasonAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"void_reason", "_");

		this.voidReason.setValue(BaseVO.retrieveFieldValue(voidReasonAttName, "VARCHAR", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);

		String deathdateEstimatedAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "deathdate_estimated", "_");

		this.deathdateEstimated.setValue(BaseVO.retrieveFieldValue(deathdateEstimatedAttName, "BIT", rs));

		String birthtimeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"birthtime", "_");

		this.birthtime.setValue(BaseVO.retrieveFieldValue(birthtimeAttName, "TIME", rs));

		String causeOfDeathNonCodedAttName = utilities
				.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "cause_of_death_non_coded", "_");

		this.causeOfDeathNonCoded.setValue(BaseVO.retrieveFieldValue(causeOfDeathNonCodedAttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO person(`gender`, `birthdate`, `birthdate_estimated`, `dead`, `death_date`, `cause_of_death`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `deathdate_estimated`, `birthtime`, `cause_of_death_non_coded`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO person(`person_id`, `gender`, `birthdate`, `birthdate_estimated`, `dead`, `death_date`, `cause_of_death`, `creator`, `date_created`, `changed_by`, `date_changed`, `voided`, `voided_by`, `date_voided`, `void_reason`, `uuid`, `deathdate_estimated`, `birthtime`, `cause_of_death_non_coded`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.gender.getValue(), this.birthdate.getValue(), this.birthdateEstimated.getValue(),
				this.dead.getValue(), this.deathDate.getValue(), this.causeOfDeath.getValue(), this.creator.getValue(),
				this.dateCreated, this.changedBy.getValue(), this.dateChanged, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.uuid,
				this.deathdateEstimated.getValue(), this.birthtime.getValue(), this.causeOfDeathNonCoded.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.personId.getValue(), this.gender.getValue(), this.birthdate.getValue(),
				this.birthdateEstimated.getValue(), this.dead.getValue(), this.deathDate.getValue(),
				this.causeOfDeath.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided,
				this.voidReason.getValue(), this.uuid, this.deathdateEstimated.getValue(), this.birthtime.getValue(),
				this.causeOfDeathNonCoded.getValue() };
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
		Object[] params = { this.personId.getValue(), this.gender.getValue(), this.birthdate.getValue(),
				this.birthdateEstimated.getValue(), this.dead.getValue(), this.deathDate.getValue(),
				this.causeOfDeath.getValue(), this.creator.getValue(), this.dateCreated, this.changedBy.getValue(),
				this.dateChanged, this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided,
				this.voidReason.getValue(), this.uuid, this.deathdateEstimated.getValue(), this.birthtime.getValue(),
				this.causeOfDeathNonCoded.getValue(), this.personId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE person SET `person_id` = ?, `gender` = ?, `birthdate` = ?, `birthdate_estimated` = ?, `dead` = ?, `death_date` = ?, `cause_of_death` = ?, `creator` = ?, `date_created` = ?, `changed_by` = ?, `date_changed` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `uuid` = ?, `deathdate_estimated` = ?, `birthtime` = ?, `cause_of_death_non_coded` = ? WHERE person_97.person_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.gender.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.gender.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.birthdate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.birthdate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.birthdateEstimated.getValue() != null ? "\"" + this.birthdateEstimated.getValue() + "\"" : null)
				+ "," + (this.dead.getValue() != null ? "\"" + this.dead.getValue() + "\"" : null) + ","
				+ (this.deathDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.deathDate.getValue())
						+ "\"" : null)
				+ "," + (this.causeOfDeath.getValue()) + "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
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
				+ (this.deathdateEstimated.getValue() != null ? "\"" + this.deathdateEstimated.getValue() + "\"" : null)
				+ ","
				+ (this.birthtime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.birthtime.getValue())
						+ "\"" : null)
				+ ","
				+ (this.causeOfDeathNonCoded.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.causeOfDeathNonCoded.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.personId.getValue()) + ","
				+ (this.gender.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.gender.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.birthdate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.birthdate.getValue())
						+ "\"" : null)
				+ ","
				+ (this.birthdateEstimated.getValue() != null ? "\"" + this.birthdateEstimated.getValue() + "\"" : null)
				+ "," + (this.dead.getValue() != null ? "\"" + this.dead.getValue() + "\"" : null) + ","
				+ (this.deathDate.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.deathDate.getValue())
						+ "\"" : null)
				+ "," + (this.causeOfDeath.getValue()) + "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
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
				+ (this.deathdateEstimated.getValue() != null ? "\"" + this.deathdateEstimated.getValue() + "\"" : null)
				+ ","
				+ (this.birthtime.getValue() != null ? "\""
						+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.birthtime.getValue())
						+ "\"" : null)
				+ ","
				+ (this.causeOfDeathNonCoded.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.causeOfDeathNonCoded.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PersonVO copy = new PersonVO();

		copy.personId = copyGeneratedField(this.personId);
		copy.gender = copyGeneratedField(this.gender);
		copy.birthdate = copyGeneratedField(this.birthdate);
		copy.birthdateEstimated = copyGeneratedField(this.birthdateEstimated);
		copy.dead = copyGeneratedField(this.dead);
		copy.deathDate = copyGeneratedField(this.deathDate);
		copy.causeOfDeath = copyGeneratedField(this.causeOfDeath);
		copy.creator = copyGeneratedField(this.creator);
		copy.dateCreated = this.dateCreated;
		copy.changedBy = copyGeneratedField(this.changedBy);
		copy.dateChanged = this.dateChanged;
		copy.voided = copyGeneratedField(this.voided);
		copy.voidedBy = copyGeneratedField(this.voidedBy);
		copy.dateVoided = this.dateVoided;
		copy.voidReason = copyGeneratedField(this.voidReason);
		copy.uuid = this.uuid;
		copy.deathdateEstimated = copyGeneratedField(this.deathdateEstimated);
		copy.birthtime = copyGeneratedField(this.birthtime);

		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.causeOfDeath.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		if (this.creator.getValue() != null)
			return true;

		if (this.voidedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("causeOfDeath"))
			return this.causeOfDeath.getValue();
		if (parentAttName.equals("changedBy"))
			return this.changedBy.getValue();
		if (parentAttName.equals("creator"))
			return this.creator.getValue();
		if (parentAttName.equals("voidedBy"))
			return this.voidedBy.getValue();

		throw new RuntimeException("No found parent for: " + parentAttName);
	}

	@Override
	public String generateTableName() {
		return "person";
	}

}