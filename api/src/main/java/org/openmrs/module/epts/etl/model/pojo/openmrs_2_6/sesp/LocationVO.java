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

public class LocationVO extends AbstractGeneratedDatabaseObject {
	private Field locationId = Field.fastCreateWithType("location_id", "INT");
	private Field name = Field.fastCreateWithType("name", "VARCHAR");
	private Field description = Field.fastCreateWithType("description", "VARCHAR");
	private Field address1 = Field.fastCreateWithType("address1", "VARCHAR");
	private Field address2 = Field.fastCreateWithType("address2", "VARCHAR");
	private Field cityVillage = Field.fastCreateWithType("city_village", "VARCHAR");
	private Field stateProvince = Field.fastCreateWithType("state_province", "VARCHAR");
	private Field postalCode = Field.fastCreateWithType("postal_code", "VARCHAR");
	private Field country = Field.fastCreateWithType("country", "VARCHAR");
	private Field latitude = Field.fastCreateWithType("latitude", "VARCHAR");
	private Field longitude = Field.fastCreateWithType("longitude", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field countyDistrict = Field.fastCreateWithType("county_district", "VARCHAR");
	private Field address3 = Field.fastCreateWithType("address3", "VARCHAR");
	private Field address6 = Field.fastCreateWithType("address6", "VARCHAR");
	private Field address5 = Field.fastCreateWithType("address5", "VARCHAR");
	private Field address4 = Field.fastCreateWithType("address4", "VARCHAR");
	private Field retired = Field.fastCreateWithType("retired", "BIT");
	private Field retiredBy = Field.fastCreateWithType("retired_by", "INT");
	private Field dateRetired = Field.fastCreateWithType("date_retired", "DATETIME");
	private Field retireReason = Field.fastCreateWithType("retire_reason", "VARCHAR");
	private Field parentLocation = Field.fastCreateWithType("parent_location", "INT");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field address7 = Field.fastCreateWithType("address7", "VARCHAR");
	private Field address8 = Field.fastCreateWithType("address8", "VARCHAR");
	private Field address9 = Field.fastCreateWithType("address9", "VARCHAR");
	private Field address10 = Field.fastCreateWithType("address10", "VARCHAR");
	private Field address11 = Field.fastCreateWithType("address11", "VARCHAR");
	private Field address12 = Field.fastCreateWithType("address12", "VARCHAR");
	private Field address13 = Field.fastCreateWithType("address13", "VARCHAR");
	private Field address14 = Field.fastCreateWithType("address14", "VARCHAR");
	private Field address15 = Field.fastCreateWithType("address15", "VARCHAR");

	public LocationVO() {
		this.metadata = false;

		this.fields.add(this.locationId);
		this.fields.add(this.name);
		this.fields.add(this.description);
		this.fields.add(this.address1);
		this.fields.add(this.address2);
		this.fields.add(this.cityVillage);
		this.fields.add(this.stateProvince);
		this.fields.add(this.postalCode);
		this.fields.add(this.country);
		this.fields.add(this.latitude);
		this.fields.add(this.longitude);
		this.fields.add(this.creator);
		this.fields.add(this.countyDistrict);
		this.fields.add(this.address3);
		this.fields.add(this.address6);
		this.fields.add(this.address5);
		this.fields.add(this.address4);
		this.fields.add(this.retired);
		this.fields.add(this.retiredBy);
		this.fields.add(this.dateRetired);
		this.fields.add(this.retireReason);
		this.fields.add(this.parentLocation);
		this.fields.add(this.changedBy);
		this.fields.add(this.address7);
		this.fields.add(this.address8);
		this.fields.add(this.address9);
		this.fields.add(this.address10);
		this.fields.add(this.address11);
		this.fields.add(this.address12);
		this.fields.add(this.address13);
		this.fields.add(this.address14);
		this.fields.add(this.address15);
	}

	@Override
	public void tryToReplaceFieldValueWithKeyValue(Key k) {
		if (utilities.equalsFieldsName(k.getName(), "location_id")) {
			this.locationId.setValue(k.getValue());
		}
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

	public void setLocationId(Field locationId) {
		this.locationId = locationId;
	}

	public void setLocationIdValue(Integer value) {
		this.locationId.setValue(value);
	}

	public Field getLocationId() {
		return this.locationId;
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

	public void setDescription(Field description) {
		this.description = description;
	}

	public void setDescriptionValue(String value) {
		this.description.setValue(value);
	}

	public Field getDescription() {
		return this.description;
	}

	public void setAddress1(Field address1) {
		this.address1 = address1;
	}

	public void setAddress1Value(String value) {
		this.address1.setValue(value);
	}

	public Field getAddress1() {
		return this.address1;
	}

	public void setAddress2(Field address2) {
		this.address2 = address2;
	}

	public void setAddress2Value(String value) {
		this.address2.setValue(value);
	}

	public Field getAddress2() {
		return this.address2;
	}

	public void setCityVillage(Field cityVillage) {
		this.cityVillage = cityVillage;
	}

	public void setCityVillageValue(String value) {
		this.cityVillage.setValue(value);
	}

	public Field getCityVillage() {
		return this.cityVillage;
	}

	public void setStateProvince(Field stateProvince) {
		this.stateProvince = stateProvince;
	}

	public void setStateProvinceValue(String value) {
		this.stateProvince.setValue(value);
	}

	public Field getStateProvince() {
		return this.stateProvince;
	}

	public void setPostalCode(Field postalCode) {
		this.postalCode = postalCode;
	}

	public void setPostalCodeValue(String value) {
		this.postalCode.setValue(value);
	}

	public Field getPostalCode() {
		return this.postalCode;
	}

	public void setCountry(Field country) {
		this.country = country;
	}

	public void setCountryValue(String value) {
		this.country.setValue(value);
	}

	public Field getCountry() {
		return this.country;
	}

	public void setLatitude(Field latitude) {
		this.latitude = latitude;
	}

	public void setLatitudeValue(String value) {
		this.latitude.setValue(value);
	}

	public Field getLatitude() {
		return this.latitude;
	}

	public void setLongitude(Field longitude) {
		this.longitude = longitude;
	}

	public void setLongitudeValue(String value) {
		this.longitude.setValue(value);
	}

	public Field getLongitude() {
		return this.longitude;
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

	public void setCountyDistrict(Field countyDistrict) {
		this.countyDistrict = countyDistrict;
	}

	public void setCountyDistrictValue(String value) {
		this.countyDistrict.setValue(value);
	}

	public Field getCountyDistrict() {
		return this.countyDistrict;
	}

	public void setAddress3(Field address3) {
		this.address3 = address3;
	}

	public void setAddress3Value(String value) {
		this.address3.setValue(value);
	}

	public Field getAddress3() {
		return this.address3;
	}

	public void setAddress6(Field address6) {
		this.address6 = address6;
	}

	public void setAddress6Value(String value) {
		this.address6.setValue(value);
	}

	public Field getAddress6() {
		return this.address6;
	}

	public void setAddress5(Field address5) {
		this.address5 = address5;
	}

	public void setAddress5Value(String value) {
		this.address5.setValue(value);
	}

	public Field getAddress5() {
		return this.address5;
	}

	public void setAddress4(Field address4) {
		this.address4 = address4;
	}

	public void setAddress4Value(String value) {
		this.address4.setValue(value);
	}

	public Field getAddress4() {
		return this.address4;
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

	public void setParentLocation(Field parentLocation) {
		this.parentLocation = parentLocation;
	}

	public void setParentLocationValue(Integer value) {
		this.parentLocation.setValue(value);
	}

	public Field getParentLocation() {
		return this.parentLocation;
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

	public void setAddress7(Field address7) {
		this.address7 = address7;
	}

	public void setAddress7Value(String value) {
		this.address7.setValue(value);
	}

	public Field getAddress7() {
		return this.address7;
	}

	public void setAddress8(Field address8) {
		this.address8 = address8;
	}

	public void setAddress8Value(String value) {
		this.address8.setValue(value);
	}

	public Field getAddress8() {
		return this.address8;
	}

	public void setAddress9(Field address9) {
		this.address9 = address9;
	}

	public void setAddress9Value(String value) {
		this.address9.setValue(value);
	}

	public Field getAddress9() {
		return this.address9;
	}

	public void setAddress10(Field address10) {
		this.address10 = address10;
	}

	public void setAddress10Value(String value) {
		this.address10.setValue(value);
	}

	public Field getAddress10() {
		return this.address10;
	}

	public void setAddress11(Field address11) {
		this.address11 = address11;
	}

	public void setAddress11Value(String value) {
		this.address11.setValue(value);
	}

	public Field getAddress11() {
		return this.address11;
	}

	public void setAddress12(Field address12) {
		this.address12 = address12;
	}

	public void setAddress12Value(String value) {
		this.address12.setValue(value);
	}

	public Field getAddress12() {
		return this.address12;
	}

	public void setAddress13(Field address13) {
		this.address13 = address13;
	}

	public void setAddress13Value(String value) {
		this.address13.setValue(value);
	}

	public Field getAddress13() {
		return this.address13;
	}

	public void setAddress14(Field address14) {
		this.address14 = address14;
	}

	public void setAddress14Value(String value) {
		this.address14.setValue(value);
	}

	public Field getAddress14() {
		return this.address14;
	}

	public void setAddress15(Field address15) {
		this.address15 = address15;
	}

	public void setAddress15Value(String value) {
		this.address15.setValue(value);
	}

	public Field getAddress15() {
		return this.address15;
	}

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String locationIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"location_id", "_");

		this.locationId.setValue(BaseVO.retrieveFieldValue(locationIdAttName, "INT", rs));

		String nameAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "name",
				"_");

		this.name.setValue(BaseVO.retrieveFieldValue(nameAttName, "VARCHAR", rs));

		String descriptionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"description", "_");

		this.description.setValue(BaseVO.retrieveFieldValue(descriptionAttName, "VARCHAR", rs));

		String address1AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address1", "_");

		this.address1.setValue(BaseVO.retrieveFieldValue(address1AttName, "VARCHAR", rs));

		String address2AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address2", "_");

		this.address2.setValue(BaseVO.retrieveFieldValue(address2AttName, "VARCHAR", rs));

		String cityVillageAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"city_village", "_");

		this.cityVillage.setValue(BaseVO.retrieveFieldValue(cityVillageAttName, "VARCHAR", rs));

		String stateProvinceAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"state_province", "_");

		this.stateProvince.setValue(BaseVO.retrieveFieldValue(stateProvinceAttName, "VARCHAR", rs));

		String postalCodeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"postal_code", "_");

		this.postalCode.setValue(BaseVO.retrieveFieldValue(postalCodeAttName, "VARCHAR", rs));

		String countryAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"country", "_");

		this.country.setValue(BaseVO.retrieveFieldValue(countryAttName, "VARCHAR", rs));

		String latitudeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"latitude", "_");

		this.latitude.setValue(BaseVO.retrieveFieldValue(latitudeAttName, "VARCHAR", rs));

		String longitudeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"longitude", "_");

		this.longitude.setValue(BaseVO.retrieveFieldValue(longitudeAttName, "VARCHAR", rs));

		String creatorAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"creator", "_");

		this.creator.setValue(BaseVO.retrieveFieldValue(creatorAttName, "INT", rs));

		String dateCreatedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_created", "_");

		this.dateCreated = (java.util.Date) BaseVO.retrieveFieldValue(dateCreatedAttName, "DATETIME", rs);

		String countyDistrictAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"county_district", "_");

		this.countyDistrict.setValue(BaseVO.retrieveFieldValue(countyDistrictAttName, "VARCHAR", rs));

		String address3AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address3", "_");

		this.address3.setValue(BaseVO.retrieveFieldValue(address3AttName, "VARCHAR", rs));

		String address6AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address6", "_");

		this.address6.setValue(BaseVO.retrieveFieldValue(address6AttName, "VARCHAR", rs));

		String address5AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address5", "_");

		this.address5.setValue(BaseVO.retrieveFieldValue(address5AttName, "VARCHAR", rs));

		String address4AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address4", "_");

		this.address4.setValue(BaseVO.retrieveFieldValue(address4AttName, "VARCHAR", rs));

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

		String parentLocationAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"parent_location", "_");

		this.parentLocation.setValue(BaseVO.retrieveFieldValue(parentLocationAttName, "INT", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString((String) BaseVO.retrieveFieldValue(uuidAttName, "VARCHAR", rs));

		String changedByAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"changed_by", "_");

		this.changedBy.setValue(BaseVO.retrieveFieldValue(changedByAttName, "INT", rs));

		String dateChangedAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"date_changed", "_");

		this.dateChanged = (java.util.Date) BaseVO.retrieveFieldValue(dateChangedAttName, "DATETIME", rs);

		String address7AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address7", "_");

		this.address7.setValue(BaseVO.retrieveFieldValue(address7AttName, "VARCHAR", rs));

		String address8AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address8", "_");

		this.address8.setValue(BaseVO.retrieveFieldValue(address8AttName, "VARCHAR", rs));

		String address9AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address9", "_");

		this.address9.setValue(BaseVO.retrieveFieldValue(address9AttName, "VARCHAR", rs));

		String address10AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address10", "_");

		this.address10.setValue(BaseVO.retrieveFieldValue(address10AttName, "VARCHAR", rs));

		String address11AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address11", "_");

		this.address11.setValue(BaseVO.retrieveFieldValue(address11AttName, "VARCHAR", rs));

		String address12AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address12", "_");

		this.address12.setValue(BaseVO.retrieveFieldValue(address12AttName, "VARCHAR", rs));

		String address13AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address13", "_");

		this.address13.setValue(BaseVO.retrieveFieldValue(address13AttName, "VARCHAR", rs));

		String address14AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address14", "_");

		this.address14.setValue(BaseVO.retrieveFieldValue(address14AttName, "VARCHAR", rs));

		String address15AttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"address15", "_");

		this.address15.setValue(BaseVO.retrieveFieldValue(address15AttName, "VARCHAR", rs));
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO location(`name`, `description`, `address1`, `address2`, `city_village`, `state_province`, `postal_code`, `country`, `latitude`, `longitude`, `creator`, `date_created`, `county_district`, `address3`, `address6`, `address5`, `address4`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `parent_location`, `uuid`, `changed_by`, `date_changed`, `address7`, `address8`, `address9`, `address10`, `address11`, `address12`, `address13`, `address14`, `address15`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO location(`location_id`, `name`, `description`, `address1`, `address2`, `city_village`, `state_province`, `postal_code`, `country`, `latitude`, `longitude`, `creator`, `date_created`, `county_district`, `address3`, `address6`, `address5`, `address4`, `retired`, `retired_by`, `date_retired`, `retire_reason`, `parent_location`, `uuid`, `changed_by`, `date_changed`, `address7`, `address8`, `address9`, `address10`, `address11`, `address12`, `address13`, `address14`, `address15`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.name.getValue(), this.description.getValue(), this.address1.getValue(),
				this.address2.getValue(), this.cityVillage.getValue(), this.stateProvince.getValue(),
				this.postalCode.getValue(), this.country.getValue(), this.latitude.getValue(),
				this.longitude.getValue(), this.creator.getValue(), this.dateCreated, this.countyDistrict.getValue(),
				this.address3.getValue(), this.address6.getValue(), this.address5.getValue(), this.address4.getValue(),
				this.retired.getValue(), this.retiredBy.getValue(), this.dateRetired.getValue(),
				this.retireReason.getValue(), this.parentLocation.getValue(), this.uuid, this.changedBy.getValue(),
				this.dateChanged, this.address7.getValue(), this.address8.getValue(), this.address9.getValue(),
				this.address10.getValue(), this.address11.getValue(), this.address12.getValue(),
				this.address13.getValue(), this.address14.getValue(), this.address15.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.locationId.getValue(), this.name.getValue(), this.description.getValue(),
				this.address1.getValue(), this.address2.getValue(), this.cityVillage.getValue(),
				this.stateProvince.getValue(), this.postalCode.getValue(), this.country.getValue(),
				this.latitude.getValue(), this.longitude.getValue(), this.creator.getValue(), this.dateCreated,
				this.countyDistrict.getValue(), this.address3.getValue(), this.address6.getValue(),
				this.address5.getValue(), this.address4.getValue(), this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.parentLocation.getValue(), this.uuid,
				this.changedBy.getValue(), this.dateChanged, this.address7.getValue(), this.address8.getValue(),
				this.address9.getValue(), this.address10.getValue(), this.address11.getValue(),
				this.address12.getValue(), this.address13.getValue(), this.address14.getValue(),
				this.address15.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.locationId.getValue(), this.name.getValue(), this.description.getValue(),
				this.address1.getValue(), this.address2.getValue(), this.cityVillage.getValue(),
				this.stateProvince.getValue(), this.postalCode.getValue(), this.country.getValue(),
				this.latitude.getValue(), this.longitude.getValue(), this.creator.getValue(), this.dateCreated,
				this.countyDistrict.getValue(), this.address3.getValue(), this.address6.getValue(),
				this.address5.getValue(), this.address4.getValue(), this.retired.getValue(), this.retiredBy.getValue(),
				this.dateRetired.getValue(), this.retireReason.getValue(), this.parentLocation.getValue(), this.uuid,
				this.changedBy.getValue(), this.dateChanged, this.address7.getValue(), this.address8.getValue(),
				this.address9.getValue(), this.address10.getValue(), this.address11.getValue(),
				this.address12.getValue(), this.address13.getValue(), this.address14.getValue(),
				this.address15.getValue(), this.locationId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE location SET `location_id` = ?, `name` = ?, `description` = ?, `address1` = ?, `address2` = ?, `city_village` = ?, `state_province` = ?, `postal_code` = ?, `country` = ?, `latitude` = ?, `longitude` = ?, `creator` = ?, `date_created` = ?, `county_district` = ?, `address3` = ?, `address6` = ?, `address5` = ?, `address4` = ?, `retired` = ?, `retired_by` = ?, `date_retired` = ?, `retire_reason` = ?, `parent_location` = ?, `uuid` = ?, `changed_by` = ?, `date_changed` = ?, `address7` = ?, `address8` = ?, `address9` = ?, `address10` = ?, `address11` = ?, `address12` = ?, `address13` = ?, `address14` = ?, `address15` = ? WHERE location_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address1.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address1.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address2.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address2.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.cityVillage.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.cityVillage.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.stateProvince.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.stateProvince.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.postalCode.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.postalCode.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.country.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.country.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.latitude.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.latitude.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.longitude.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.longitude.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ ","
				+ (this.countyDistrict.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.countyDistrict.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address3.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address3.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address6.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address6.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address5.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address5.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address4.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address4.getValue().toString()) + "\""
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
				+ "," + (this.parentLocation.getValue()) + ","
				+ (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null) + ","
				+ (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ ","
				+ (this.address7.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address7.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address8.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address8.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address9.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address9.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address10.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address10.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address11.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address11.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address12.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address12.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address13.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address13.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address14.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address14.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address15.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address15.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return "" + (this.locationId.getValue()) + ","
				+ (this.name.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.name.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address1.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address1.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address2.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address2.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.cityVillage.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.cityVillage.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.stateProvince.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.stateProvince.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.postalCode.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.postalCode.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.country.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.country.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.latitude.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.latitude.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.longitude.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.longitude.getValue().toString()) + "\""
						: null)
				+ "," + (this.creator.getValue()) + ","
				+ (this.dateCreated != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateCreated) + "\""
						: null)
				+ ","
				+ (this.countyDistrict.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.countyDistrict.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address3.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address3.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address6.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address6.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address5.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address5.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address4.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address4.getValue().toString()) + "\""
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
				+ "," + (this.parentLocation.getValue()) + ","
				+ (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null) + ","
				+ (this.changedBy.getValue()) + ","
				+ (this.dateChanged != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateChanged) + "\""
						: null)
				+ ","
				+ (this.address7.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address7.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address8.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address8.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address9.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address9.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address10.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address10.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address11.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address11.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address12.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address12.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address13.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address13.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address14.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address14.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.address15.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.address15.getValue().toString()) + "\""
						: null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		LocationVO copy = new LocationVO();
		copy.setRelatedConfiguration(getRelatedConfiguration());
		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {
			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());
		}
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public boolean hasParents() {
		if (this.parentLocation.getValue() != null)
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
		if (parentAttName.equals("parentLocation"))
			return this.parentLocation.getValue();
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
		return "location";
	}

}