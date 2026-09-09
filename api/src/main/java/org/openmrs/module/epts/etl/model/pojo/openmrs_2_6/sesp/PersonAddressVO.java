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

public class PersonAddressVO extends AbstractGeneratedDatabaseObject {
	private Field personAddressId = Field.fastCreateWithType("person_address_id", "INT");
	private Field personId = Field.fastCreateWithType("person_id", "INT");
	private Field preferred = Field.fastCreateWithType("preferred", "BIT");
	private Field address1 = Field.fastCreateWithType("address1", "VARCHAR");
	private Field address2 = Field.fastCreateWithType("address2", "VARCHAR");
	private Field cityVillage = Field.fastCreateWithType("city_village", "VARCHAR");
	private Field stateProvince = Field.fastCreateWithType("state_province", "VARCHAR");
	private Field postalCode = Field.fastCreateWithType("postal_code", "VARCHAR");
	private Field country = Field.fastCreateWithType("country", "VARCHAR");
	private Field latitude = Field.fastCreateWithType("latitude", "VARCHAR");
	private Field longitude = Field.fastCreateWithType("longitude", "VARCHAR");
	private Field creator = Field.fastCreateWithType("creator", "INT");
	private Field voided = Field.fastCreateWithType("voided", "BIT");
	private Field voidedBy = Field.fastCreateWithType("voided_by", "INT");
	private Field voidReason = Field.fastCreateWithType("void_reason", "VARCHAR");
	private Field countyDistrict = Field.fastCreateWithType("county_district", "VARCHAR");
	private Field address3 = Field.fastCreateWithType("address3", "VARCHAR");
	private Field address6 = Field.fastCreateWithType("address6", "VARCHAR");
	private Field address5 = Field.fastCreateWithType("address5", "VARCHAR");
	private Field address4 = Field.fastCreateWithType("address4", "VARCHAR");
	private Field changedBy = Field.fastCreateWithType("changed_by", "INT");
	private Field startDate = Field.fastCreateWithType("start_date", "DATETIME");
	private Field endDate = Field.fastCreateWithType("end_date", "DATETIME");
	private Field address7 = Field.fastCreateWithType("address7", "VARCHAR");
	private Field address8 = Field.fastCreateWithType("address8", "VARCHAR");
	private Field address9 = Field.fastCreateWithType("address9", "VARCHAR");
	private Field address10 = Field.fastCreateWithType("address10", "VARCHAR");
	private Field address11 = Field.fastCreateWithType("address11", "VARCHAR");
	private Field address12 = Field.fastCreateWithType("address12", "VARCHAR");
	private Field address13 = Field.fastCreateWithType("address13", "VARCHAR");
	private Field address14 = Field.fastCreateWithType("address14", "VARCHAR");
	private Field address15 = Field.fastCreateWithType("address15", "VARCHAR");

	public PersonAddressVO() {
		this.metadata = false;
		this.fields.add(this.personAddressId);
		this.fields.add(this.personId);
		this.fields.add(this.preferred);
		this.fields.add(this.address1);
		this.fields.add(this.address2);
		this.fields.add(this.cityVillage);
		this.fields.add(this.stateProvince);
		this.fields.add(this.postalCode);
		this.fields.add(this.country);
		this.fields.add(this.latitude);
		this.fields.add(this.longitude);
		this.fields.add(this.creator);
		this.fields.add(this.voided);
		this.fields.add(this.voidedBy);
		this.fields.add(this.voidReason);
		this.fields.add(this.countyDistrict);
		this.fields.add(this.address3);
		this.fields.add(this.address6);
		this.fields.add(this.address5);
		this.fields.add(this.address4);
		this.fields.add(this.changedBy);
		this.fields.add(this.startDate);
		this.fields.add(this.endDate);
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
		if (utilities.equalsFieldsName(k.getName(), "person_address_id")) {
			this.personAddressId.setValue(k.getValue());
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "person_address_id")) {
			return this.personAddressId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "person_id")) {
			return this.personId.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "preferred")) {
			return this.preferred.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address1")) {
			return this.address1.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address2")) {
			return this.address2.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "city_village")) {
			return this.cityVillage.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "state_province")) {
			return this.stateProvince.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "postal_code")) {
			return this.postalCode.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "country")) {
			return this.country.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "latitude")) {
			return this.latitude.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "longitude")) {
			return this.longitude.getValue();
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
		if (utilities.equalsFieldsName(fieldName, "county_district")) {
			return this.countyDistrict.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address3")) {
			return this.address3.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address6")) {
			return this.address6.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address5")) {
			return this.address5.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address4")) {
			return this.address4.getValue();
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
		if (utilities.equalsFieldsName(fieldName, "address7")) {
			return this.address7.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address8")) {
			return this.address8.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address9")) {
			return this.address9.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address10")) {
			return this.address10.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address11")) {
			return this.address11.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address12")) {
			return this.address12.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address13")) {
			return this.address13.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address14")) {
			return this.address14.getValue();
		}
		if (utilities.equalsFieldsName(fieldName, "address15")) {
			return this.address15.getValue();
		}
		return super.getFieldValue(fieldName);
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		if (utilities.equalsFieldsName(fieldName, "person_address_id")) {
			this.personAddressId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "person_id")) {
			this.personId.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "preferred")) {
			this.preferred.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address1")) {
			this.address1.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address2")) {
			this.address2.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "city_village")) {
			this.cityVillage.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "state_province")) {
			this.stateProvince.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "postal_code")) {
			this.postalCode.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "country")) {
			this.country.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "latitude")) {
			this.latitude.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "longitude")) {
			this.longitude.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		if (utilities.equalsFieldsName(fieldName, "county_district")) {
			this.countyDistrict.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address3")) {
			this.address3.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address6")) {
			this.address6.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address5")) {
			this.address5.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address4")) {
			this.address4.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		if (utilities.equalsFieldsName(fieldName, "address7")) {
			this.address7.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address8")) {
			this.address8.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address9")) {
			this.address9.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address10")) {
			this.address10.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address11")) {
			this.address11.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address12")) {
			this.address12.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address13")) {
			this.address13.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address14")) {
			this.address14.setValue(value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}
		if (utilities.equalsFieldsName(fieldName, "address15")) {
			this.address15.setValue(value instanceof Field ? ((Field) value).getValue() : value);
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
		loadGeneratedFieldWithDefaultValue(this.personAddressId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.personId, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.preferred, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address1, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address2, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.cityVillage, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.stateProvince, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.postalCode, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.country, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.latitude, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.longitude, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.creator, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voided, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.voidReason, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.countyDistrict, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address3, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address6, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address5, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address4, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.changedBy, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.startDate, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.endDate, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address7, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address8, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address9, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address10, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address11, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address12, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address13, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address14, srcConn, dstConn);
		loadGeneratedFieldWithDefaultValue(this.address15, srcConn, dstConn);
	}

	public void setPersonAddressId(Field personAddressId) {
		this.personAddressId = personAddressId;
	}

	public void setPersonAddressIdValue(Integer value) {
		this.personAddressId.setValue(value);
	}

	public Field getPersonAddressId() {
		return this.personAddressId;
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

	public void setPreferred(Field preferred) {
		this.preferred = preferred;
	}

	public void setPreferredValue(Boolean value) {
		this.preferred.setValue(value);
	}

	public Field getPreferred() {
		return this.preferred;
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

		String personAddressIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_address_id", "_");

		this.personAddressId.setValue(BaseVO.retrieveFieldValue(personAddressIdAttName, "INT", rs));

		String personIdAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"person_id", "_");

		this.personId.setValue(BaseVO.retrieveFieldValue(personIdAttName, "INT", rs));

		String preferredAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"preferred", "_");

		this.preferred.setValue(BaseVO.retrieveFieldValue(preferredAttName, "BIT", rs));

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
		return "INSERT INTO person_address(`person_id`, `preferred`, `address1`, `address2`, `city_village`, `state_province`, `postal_code`, `country`, `latitude`, `longitude`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `county_district`, `address3`, `address6`, `address5`, `address4`, `uuid`, `date_changed`, `changed_by`, `start_date`, `end_date`, `address7`, `address8`, `address9`, `address10`, `address11`, `address12`, `address13`, `address14`, `address15`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO person_address(`person_address_id`, `person_id`, `preferred`, `address1`, `address2`, `city_village`, `state_province`, `postal_code`, `country`, `latitude`, `longitude`, `creator`, `date_created`, `voided`, `voided_by`, `date_voided`, `void_reason`, `county_district`, `address3`, `address6`, `address5`, `address4`, `uuid`, `date_changed`, `changed_by`, `start_date`, `end_date`, `address7`, `address8`, `address9`, `address10`, `address11`, `address12`, `address13`, `address14`, `address15`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.personId.getValue(), this.preferred.getValue(), this.address1.getValue(),
				this.address2.getValue(), this.cityVillage.getValue(), this.stateProvince.getValue(),
				this.postalCode.getValue(), this.country.getValue(), this.latitude.getValue(),
				this.longitude.getValue(), this.creator.getValue(), this.dateCreated, this.voided.getValue(),
				this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(), this.countyDistrict.getValue(),
				this.address3.getValue(), this.address6.getValue(), this.address5.getValue(), this.address4.getValue(),
				this.uuid, this.dateChanged, this.changedBy.getValue(), this.startDate.getValue(),
				this.endDate.getValue(), this.address7.getValue(), this.address8.getValue(), this.address9.getValue(),
				this.address10.getValue(), this.address11.getValue(), this.address12.getValue(),
				this.address13.getValue(), this.address14.getValue(), this.address15.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.personAddressId.getValue(), this.personId.getValue(), this.preferred.getValue(),
				this.address1.getValue(), this.address2.getValue(), this.cityVillage.getValue(),
				this.stateProvince.getValue(), this.postalCode.getValue(), this.country.getValue(),
				this.latitude.getValue(), this.longitude.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.countyDistrict.getValue(), this.address3.getValue(), this.address6.getValue(),
				this.address5.getValue(), this.address4.getValue(), this.uuid, this.dateChanged,
				this.changedBy.getValue(), this.startDate.getValue(), this.endDate.getValue(), this.address7.getValue(),
				this.address8.getValue(), this.address9.getValue(), this.address10.getValue(),
				this.address11.getValue(), this.address12.getValue(), this.address13.getValue(),
				this.address14.getValue(), this.address15.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.personAddressId.getValue(), this.personId.getValue(), this.preferred.getValue(),
				this.address1.getValue(), this.address2.getValue(), this.cityVillage.getValue(),
				this.stateProvince.getValue(), this.postalCode.getValue(), this.country.getValue(),
				this.latitude.getValue(), this.longitude.getValue(), this.creator.getValue(), this.dateCreated,
				this.voided.getValue(), this.voidedBy.getValue(), this.dateVoided, this.voidReason.getValue(),
				this.countyDistrict.getValue(), this.address3.getValue(), this.address6.getValue(),
				this.address5.getValue(), this.address4.getValue(), this.uuid, this.dateChanged,
				this.changedBy.getValue(), this.startDate.getValue(), this.endDate.getValue(), this.address7.getValue(),
				this.address8.getValue(), this.address9.getValue(), this.address10.getValue(),
				this.address11.getValue(), this.address12.getValue(), this.address13.getValue(),
				this.address14.getValue(), this.address15.getValue(), this.personAddressId.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE person_address SET `person_address_id` = ?, `person_id` = ?, `preferred` = ?, `address1` = ?, `address2` = ?, `city_village` = ?, `state_province` = ?, `postal_code` = ?, `country` = ?, `latitude` = ?, `longitude` = ?, `creator` = ?, `date_created` = ?, `voided` = ?, `voided_by` = ?, `date_voided` = ?, `void_reason` = ?, `county_district` = ?, `address3` = ?, `address6` = ?, `address5` = ?, `address4` = ?, `uuid` = ?, `date_changed` = ?, `changed_by` = ?, `start_date` = ?, `end_date` = ?, `address7` = ?, `address8` = ?, `address9` = ?, `address10` = ?, `address11` = ?, `address12` = ?, `address13` = ?, `address14` = ?, `address15` = ? WHERE person_address_id = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return "" + (this.personId.getValue()) + ","
				+ (this.preferred.getValue() != null ? "\"" + this.preferred.getValue() + "\"" : null) + ","
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
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
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
		return "" + (this.personAddressId.getValue()) + "," + (this.personId.getValue()) + ","
				+ (this.preferred.getValue() != null ? "\"" + this.preferred.getValue() + "\"" : null) + ","
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
				+ "," + (this.voided.getValue() != null ? "\"" + this.voided.getValue() + "\"" : null) + ","
				+ (this.voidedBy.getValue()) + ","
				+ (this.dateVoided != null
						? "\"" + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) this.dateVoided) + "\""
						: null)
				+ ","
				+ (this.voidReason.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.voidReason.getValue().toString()) + "\""
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
		PersonAddressVO copy = new PersonAddressVO();
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

		if (this.voidedBy.getValue() != null)
			return true;

		if (this.changedBy.getValue() != null)
			return true;

		return false;
	}

	@Override
	public Object getParentValue(String parentAttName) {
		if (parentAttName.equals("personId"))
			return this.personId.getValue();
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
		return "person_address";
	}

}