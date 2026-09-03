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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DrugVO extends AbstractGeneratedDatabaseObject {
	private Field drugId = Field.fastCreateWithType("drug_id", "INT");
	private Field conceptId = Field.fastCreateWithType("concept_id", "INT");
	private Field name = Field.fastCreateWithType("name", "VARCHAR");
	private Field combination = Field.fastCreateWithType("combination", "BIT");
	private Field dosageForm = Field.fastCreateWithType("dosage_form", "INT");
	private Field maximumDailyDose = Field.fastCreateWithType("maximum_daily_dose", "DOUBLE");
	private Field minimumDailyDose = Field.fastCreateWithType("minimum_daily_dose", "DOUBLE");
	private Field route = Field.fastCreateWithType("route", "INT");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field strength = Field.fastCreateWithType("strength", "VARCHAR");
	private Field doseLimitUnits = Field.fastCreateWithType("dose_limit_units", "INT");

	public DrugVO() {
		this.metadata = false;
		this.fields.add(this.drugId);
		this.fields.add(this.conceptId);
		this.fields.add(this.name);
		this.fields.add(this.combination);
		this.fields.add(this.dosageForm);
		this.fields.add(this.maximumDailyDose);
		this.fields.add(this.minimumDailyDose);
		this.fields.add(this.route);
		this.fields.add(this.creator);
		this.fields.add(this.retired);
		this.fields.add(this.retiredBy);
		this.fields.add(this.dateRetired);
		this.fields.add(this.retireReason);
		this.fields.add(this.changedBy);
		this.fields.add(this.strength);
		this.fields.add(this.doseLimitUnits);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "drug_id")) {
			this.drugId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "drug_id")) {
			return this.drugId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "concept_id")) {
			return this.conceptId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "name")) {
			return this.name.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "combination")) {
			return this.combination.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dosage_form")) {
			return this.dosageForm.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "maximum_daily_dose")) {
			return this.maximumDailyDose.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "minimum_daily_dose")) {
			return this.minimumDailyDose.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "route")) {
			return this.route.getValue();
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
		if (utilities.equalsFieldsName(fieldName, "strength")) {
			return this.strength.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "dose_limit_units")) {
			return this.doseLimitUnits.getValue();
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

	public void setDrugId(Field drugId) {
		this.drugId = drugId;
	}

	public void setDrugIdValue(Integer value) {
		this.drugId.setValue(value);
	}

	public Field getDrugId() {
		return this.drugId;
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

	public void setName(Field name) {
		this.name = name;
	}

	public void setNameValue(String value) {
		this.name.setValue(value);
	}

	public Field getName() {
		return this.name;
	}

	public void setCombination(Field combination) {
		this.combination = combination;
	}

	public void setCombinationValue(Boolean value) {
		this.combination.setValue(value);
	}

	public Field getCombination() {
		return this.combination;
	}

	public void setDosageForm(Field dosageForm) {
		this.dosageForm = dosageForm;
	}

	public void setDosageFormValue(Integer value) {
		this.dosageForm.setValue(value);
	}

	public Field getDosageForm() {
		return this.dosageForm;
	}

	public void setMaximumDailyDose(Field maximumDailyDose) {
		this.maximumDailyDose = maximumDailyDose;
	}

	public void setMaximumDailyDoseValue(Double value) {
		this.maximumDailyDose.setValue(value);
	}

	public Field getMaximumDailyDose() {
		return this.maximumDailyDose;
	}

	public void setMinimumDailyDose(Field minimumDailyDose) {
		this.minimumDailyDose = minimumDailyDose;
	}

	public void setMinimumDailyDoseValue(Double value) {
		this.minimumDailyDose.setValue(value);
	}

	public Field getMinimumDailyDose() {
		return this.minimumDailyDose;
	}

	public void setRoute(Field route) {
		this.route = route;
	}

	public void setRouteValue(Integer value) {
		this.route.setValue(value);
	}

	public Field getRoute() {
		return this.route;
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

	public void setStrength(Field strength) {
		this.strength = strength;
	}

	public void setStrengthValue(String value) {
		this.strength.setValue(value);
	}

	public Field getStrength() {
		return this.strength;
	}

	public void setDoseLimitUnits(Field doseLimitUnits) {
		this.doseLimitUnits = doseLimitUnits;
	}

	public void setDoseLimitUnitsValue(Integer value) {
		this.doseLimitUnits.setValue(value);
	}

	public Field getDoseLimitUnits() {
		return this.doseLimitUnits;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String drugIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"drug_id", "_");

		this.drugId.setValue(BaseVO.retrieveFieldValue(drugIdAttName, "INT", rs));

		String conceptIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"concept_id", "_");

		this.conceptId.setValue(BaseVO.retrieveFieldValue(conceptIdAttName, "INT", rs));

		String nameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "name",
				"_");

		this.name.setValue(BaseVO.retrieveFieldValue(nameAttName, "VARCHAR", rs));

		String combinationAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"combination", "_");

		this.combination.setValue(BaseVO.retrieveFieldValue(combinationAttName, "BIT", rs));

		String dosageFormAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"dosage_form", "_");

		this.dosageForm.setValue(BaseVO.retrieveFieldValue(dosageFormAttName, "INT", rs));

		String maximumDailyDoseAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"maximum_daily_dose", "_");

		this.maximumDailyDose.setValue(BaseVO.retrieveFieldValue(maximumDailyDoseAttName, "DOUBLE", rs));

		String minimumDailyDoseAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"minimum_daily_dose", "_");

		this.minimumDailyDose.setValue(BaseVO.retrieveFieldValue(minimumDailyDoseAttName, "DOUBLE", rs));

		String routeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "route",
				"_");

		this.route.setValue(BaseVO.retrieveFieldValue(routeAttName, "INT", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

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

		this.uuid = AttDefinedElements
				.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "CHAR", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String strengthAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"strength", "_");

		this.strength.setValue(BaseVO.retrieveFieldValue(strengthAttName, "VARCHAR", rs));

		String doseLimitUnitsAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"dose_limit_units", "_");

		this.doseLimitUnits.setValue(BaseVO.retrieveFieldValue(doseLimitUnitsAttName, "INT", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO drug(`concept_id`, `name`, `combination`, `dosage_form`, `maximum_daily_dose`, `minimum_daily_dose`, `route`, `creator`, `date_created`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `date_changed`, `changed_by`, `strength`, `dose_limit_units`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO drug(`drug_id`, `concept_id`, `name`, `combination`, `dosage_form`, `maximum_daily_dose`, `minimum_daily_dose`, `route`, `creator`, `date_created`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `uuid`, `date_changed`, `changed_by`, `strength`, `dose_limit_units`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.conceptId.getValue(), this.name.getValue(), this.combination.getValue(),
				this.dosageForm.getValue(), this.maximumDailyDose.getValue(), this.minimumDailyDose.getValue(),
				this.route.getValue(), this.creator.getValue(), this.dateCreated, this.retired.getValue(),
				this.retiredBy.getValue(), this.dateRetired.getValue(), this.retireReason.getValue(), this.uuid,
				this.dateChanged, this.changedBy.getValue(), this.strength.getValue(), this.doseLimitUnits.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.drugId.getValue(), this.conceptId.getValue(), this.name.getValue(),
				this.combination.getValue(), this.dosageForm.getValue(), this.maximumDailyDose.getValue(),
				this.minimumDailyDose.getValue(), this.route.getValue(), this.creator.getValue(), this.dateCreated,
				this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.dateChanged, this.changedBy.getValue(),
				this.strength.getValue(), this.doseLimitUnits.getValue() };
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
		Object[] params = { this.drugId.getValue(), this.conceptId.getValue(), this.name.getValue(),
				this.combination.getValue(), this.dosageForm.getValue(), this.maximumDailyDose.getValue(),
				this.minimumDailyDose.getValue(), this.route.getValue(), this.creator.getValue(), this.dateCreated,
				this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.uuid, this.dateChanged, this.changedBy.getValue(),
				this.strength.getValue(), this.doseLimitUnits.getValue(), this.drugId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE drug SET `drug_id` = ?, `concept_id` = ?, `name` = ?, `combination` = ?, `dosage_form` = ?, `maximum_daily_dose` = ?, `minimum_daily_dose` = ?, `route` = ?, `creator` = ?, `date_created` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `uuid` = ?, `date_changed` = ?, `changed_by` = ?, `strength` = ?, `dose_limit_units` = ? WHERE drug_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.conceptId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ "," + (this.combination.getValue() != null ? "\"" + this.combination.getValue() + "\"" : null) + ","
				+ (this.dosageForm.getValue()) + "," + (this.maximumDailyDose.getValue()) + ","
				+ (this.minimumDailyDose.getValue()) + "," + (this.route.getValue()) + "," + (this.creator.getValue())
				+ ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
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
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.strength.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.strength.getValue().toString()) + "\""
						: null)
				+ "," + (this.doseLimitUnits.getValue());
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.drugId.getValue()) + "," + (this.conceptId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ "," + (this.combination.getValue() != null ? "\"" + this.combination.getValue() + "\"" : null) + ","
				+ (this.dosageForm.getValue()) + "," + (this.maximumDailyDose.getValue()) + ","
				+ (this.minimumDailyDose.getValue()) + "," + (this.route.getValue()) + "," + (this.creator.getValue())
				+ ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ "," + (this.retired.getValue() != null ? "\"" + this.retired.getValue() + "\"" : null) + ","
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
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ "," + (this.changedBy.getValue()) + ","
				+ (this.strength.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.strength.getValue().toString()) + "\""
						: null)
				+ "," + (this.doseLimitUnits.getValue());
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		DrugVO copy = new DrugVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.dosageForm.getValue() != null)
			return true;

		if (this.doseLimitUnits.getValue() != null)
			return true;

		if (this.conceptId.getValue() != null)
			return true;

		if (this.route.getValue() != null)
			return true;

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
		if (parentAttName.equals("dosageForm"))
			return this.dosageForm.getValue();
		if (parentAttName.equals("doseLimitUnits"))
			return this.doseLimitUnits.getValue();
		if (parentAttName.equals("conceptId"))
			return this.conceptId.getValue();
		if (parentAttName.equals("route"))
			return this.route.getValue();
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
		return "drug";
	}

}