package org.openmrs.module.epts.etl.model.pojo.generic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.ParentTableImpl;
import org.openmrs.module.epts.etl.conf.RefMapping;
import org.openmrs.module.epts.etl.conf.datasource.JsonSimpleOutputDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.MissingFieldException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonEtlObject extends AbstractDatabaseObject {

	private JsonSimpleOutputDataSource relatedConfiguration;

	private List<JsonEtlObject> parents;

	public JsonEtlObject() {
	}

	@Override
	public List<EtlDatabaseObject> getAuxLoadObject() {
		return utilities.parseList(parents, EtlDatabaseObject.class);
	}

	@Override
	public void setAuxLoadObject(List<EtlDatabaseObject> auxLoadObjects) {
	}

	@Override
	@JsonIgnore
	public Oid getObjectId() {
		return super.getObjectId();
	}

	@Override
	@JsonIgnore
	public String getUuid() {
		return super.getUuid();
	}

	@Override
	@JsonIgnore
	public JsonEtlObject getSharedPkObj() {
		return null;
	}

	@Override
	public void setSharedPkObj(EtlDatabaseObject sharedPkObj) {
	}

	public JsonEtlObject(JsonSimpleOutputDataSource relatedConfiguration) {
		setRelatedConfiguration(relatedConfiguration);
	}

	@Override
	public Object getFieldValue(String fieldName) {
		String fieldNameInSnakeCase = utilities.parsetoSnakeCase(fieldName);
		String fieldNameInCameCase = utilities.parsetoCamelCase(fieldName);

		try {
			return utilities.getFieldValueOnFieldList(utilities.parseList(this.fields, Field.class),
					fieldNameInSnakeCase);
		} catch (ForbiddenOperationException e) {
			return utilities.getFieldValueOnFieldList(utilities.parseList(this.fields, Field.class),
					fieldNameInCameCase);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) throws DBException {
		if (this.relatedConfiguration == null) {
			throw new ForbiddenOperationException("The relatedConfiguration  is not set");
		}

		TableConfiguration conf = (TableConfiguration) getRelatedConfiguration();

		for (Field f : this.fields) {
			if (!f.allowNull()) {

				ParentTable p = conf.getFieldIsRelatedParent(f);

				if (p != null) {
					EtlDatabaseObject defaultParent = null;

					try {
						if (!p.hasAlias()) {
							p.tryToGenerateTableAlias(conf.getRelatedEtlConf());
						}

						if (!p.isFullLoaded()) {
							p.fullLoad(dstConn);
						}

						defaultParent = p.getDefaultObject(dstConn);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (defaultParent == null) {
						try {
							defaultParent = conf.getSyncRecordClass().newInstance();
							defaultParent.setRelatedConfiguration(p);

							if (defaultParent.checkIfAllRelationshipCanBeresolved(conf, dstConn)) {
								defaultParent = p.generateAndSaveDefaultObject(srcConn, dstConn);
							} else {
								throw new ForbiddenOperationException("There are recursive relationship between "
										+ conf.getTableName() + " and " + p.getTableName()
										+ " which cannot automatically resolved...! Please manual create default dstRecord for one of thise table using id '-1'");
							}
						} catch (InstantiationException | IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}

					this.changeParentValue(p, defaultParent);
				} else {
					f.loadWithDefaultValue();
				}
			}
		}
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		try {
			super.setFieldValue(fieldName, value);
		} catch (ForbiddenOperationException e) {
		}

		for (Field field : this.fields) {

			if (field.getName().equals(fieldName) || field.getNameAsClassAtt().equals(fieldName)) {
				field.setValue(value);

				return;
			}
		}

		throw new MissingFieldException(fieldName, this.getRelatedConfiguration());
	}

	@Override
	public void setRelatedConfiguration(EtlDatabaseObjectConfiguration config) {
		if (!(config instanceof JsonSimpleOutputDataSource)) {
			throw new ForbiddenOperationException("The config must be a JsonOutputDataSource");
		}

		this.relatedConfiguration = (JsonSimpleOutputDataSource) config;

		this.fields = this.getRelatedConfiguration().cloneFields(this);
	}

	@Override
	@JsonIgnore
	public JsonSimpleOutputDataSource getRelatedConfiguration() {
		return this.relatedConfiguration;
	}

	@Override
	@JsonIgnore
	public Object[] getInsertParamsWithoutObjectId() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public String getInsertSQLWithoutObjectId() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public Object[] getInsertParamsWithObjectId() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public String getInsertSQLWithObjectId() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public String getUpdateSQL() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public Object[] getUpdateParams() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public String generateInsertValuesWithoutObjectId() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public String generateInsertValuesWithObjectId() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	public void setInsertSQLQuestionMarksWithObjectId(String insertQuestionMarks) {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public String getInsertSQLQuestionMarksWithObjectId() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	public void setInsertSQLQuestionMarksWithoutObjectId(String insertQuestionMarks) {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public String getInsertSQLQuestionMarksWithoutObjectId() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public String generateFullFilledUpdateSql() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	@JsonIgnore
	public boolean hasParents() {
		return hasAuxLoadObject();
	}

	@Override
	public Object getParentValue(ParentTable parent) {
		if (!parent.useSimplePk()) {
			throw new ForbiddenOperationException("The parent " + parent + " does not use simple pk");
		}

		RefMapping map = parent.getRefMapping().get(0);

		return getFieldValue(map.getChildFieldName());
	}

	@Override
	@JsonIgnore
	public String generateTableName() {
		return this.relatedConfiguration.getName();
	}

	@Override
	@JsonIgnore
	public String getObjectName() {
		return this.generateTableName();
	}

	public static JsonEtlObject fastCreate(JsonSimpleOutputDataSource etlConf, List<Field> fields) {
		JsonEtlObject obj = new JsonEtlObject(etlConf);
		obj.setFields(fields);

		for (Field f : etlConf.getFields()) {
			if (!obj.contaisField(f.getName())) {
				obj.getFields().add(Field.fastCreateField(f.getName()));
			}
		}

		return obj;
	}

	@Override
	public void setParentToNull(ParentTableImpl refInfo) {
		for (RefMapping map : refInfo.getRefMapping()) {
			setFieldValue(map.getChildFieldName(), null);
		}
	}

	@Override
	public void changeParentValue(ParentTable refInfo, EtlDatabaseObject newParent) {
		for (RefMapping map : refInfo.getRefMapping()) {
			String parentFieldName = map.getParentFieldName();
			String childFieldName = map.getChildFieldName();

			Object parentValue = newParent.getFieldValue(parentFieldName);
			this.setFieldValue(childFieldName, parentValue);
		}

	}

	@Override
	@JsonIgnore
	public String toString() {

		String tableName = this.relatedConfiguration != null ? this.relatedConfiguration.getObjectName()
				: "Aknown Object";

		String tableNameAlias = "N/A";

		if (this.relatedConfiguration instanceof TableConfiguration) {
			tableNameAlias = ((TableConfiguration) this.relatedConfiguration).getTableAlias();
		}

		return tableName + "(" + tableNameAlias + "): " + super.toString();
	}

	@Override
	public void copyFrom(EtlDatabaseObject copyFrom) {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	public void generateFields() {
		throw new ForbiddenOperationException("Forbiden method!");
	}

	@Override
	public void setObjectId(Oid objectId) {
		super.setObjectId(objectId);

		for (Key key : objectId.getFields()) {
			setFieldValue(key.getName(), key.getValue());
		}

	}

	@Override
	public void tryToReplaceFieldWithKey(Key k) {
		if (this.hasFields()) {
			utilities.updateOnArray(this.getFields(), k, k);
		}
	}

	public void addParent(JsonEtlObject parent) {
		if (this.parents == null) {
			this.parents = new ArrayList<>();
		}

		this.parents.add(parent);
	}

	@Override
	public EtlDatabaseObject createACopy() {
		throw new ForbiddenOperationException("Forbiden method!");
	}
}
