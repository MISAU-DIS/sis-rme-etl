package org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp;

import org.openmrs.module.epts.etl.model.pojo.generic.*;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

import org.openmrs.module.epts.etl.model.Field;

import org.openmrs.module.epts.etl.model.base.BaseVO;

import org.openmrs.module.epts.etl.utilities.AttDefinedElements;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.sql.Connection;

import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrivilegeVO extends AbstractGeneratedDatabaseObject {
	private Field privilege = Field.fastCreateWithType("privilege", "VARCHAR");
	private Field description = Field.fastCreateWithType("description", "TEXT");

	private EtlDatabaseObjectConfiguration relatedConfiguration;

	public PrivilegeVO() {
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

	public void setPrivilege(Field privilege) {
		this.privilege = privilege;
	}

	public void setPrivilegeValue(String value) {
		this.privilege.setValue(value);
	}

	public Field getPrivilege() {
		return this.privilege;
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

	@Override
	public void load(ResultSet rs) throws SQLException {
		super.load(rs);

		String privilegeAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"privilege", "_");

		this.privilege.setValue(BaseVO.retrieveFieldValue(privilegeAttName, "VARCHAR", rs));

		String descriptionAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(),
				"description", "_");

		this.description.setValue(BaseVO.retrieveFieldValue(descriptionAttName, "TEXT", rs));

		String uuidAttName = utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), "uuid",
				"_");

		this.uuid = AttDefinedElements.removeStrangeCharactersOnString(
				rs.getString(uuidAttName) != null ? rs.getString(uuidAttName).trim() : null);
		this.loadedFromDb = true;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithoutObjectId() {
		return "INSERT INTO privilege(`description`, `uuid`) VALUES( ?, ?);";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLWithObjectId() {
		return "INSERT INTO privilege(`privilege`, `description`, `uuid`) VALUES( ?, ?, ?);";
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithoutObjectId() {
		Object[] params = { this.description.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public Object[] getInsertParamsWithObjectId() {
		Object[] params = { this.privilege.getValue(), this.description.getValue(), this.uuid };
		return params;
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		return "?, ?";
	}

	@JsonIgnore
	@Override
	public String getInsertSQLQuestionMarksWithObjectId() {
		return "?, ?, ?";
	}

	@JsonIgnore
	@Override
	public Object[] getUpdateParams() {
		Object[] params = { this.privilege.getValue(), this.description.getValue(), this.uuid,
				this.privilege.getValue() };
		return params;
	}

	@JsonIgnore
	@Override
	public String getUpdateSQL() {
		return "UPDATE privilege SET `privilege` = ?, `description` = ?, `uuid` = ? WHERE privilege_19.privilege = ? ";
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithoutObjectId() {
		return ""
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public String generateInsertValuesWithObjectId() {
		return ""
				+ (this.privilege.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.privilege.getValue().toString()) + "\""
						: null)
				+ ","
				+ (this.description.getValue() != null
						? "\"" + utilities.scapeQuotationMarks(this.description.getValue().toString()) + "\""
						: null)
				+ "," + (this.uuid != null ? "\"" + utilities.scapeQuotationMarks(this.uuid.toString()) + "\"" : null);
	}

	@JsonIgnore
	@Override
	public EtlDatabaseObject createACopy() {
		PrivilegeVO copy = new PrivilegeVO();

		copy.privilege = copyGeneratedField(this.privilege);
		copy.description = copyGeneratedField(this.description);

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
		return "privilege";
	}

}