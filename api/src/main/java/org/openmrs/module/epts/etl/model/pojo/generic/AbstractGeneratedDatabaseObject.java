package org.openmrs.module.epts.etl.model.pojo.generic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.sql.Connection;

import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Common behaviour for database objects whose concrete classes are generated
 * from the data model.
 */
public abstract class AbstractGeneratedDatabaseObject extends AbstractDatabaseObject {
	private EtlDatabaseObjectConfiguration relatedConfiguration;

	/**
	 * Stable collection containing the Field instances exposed by this generated
	 * object, including contextual wrappers for scalar fields inherited from
	 * BaseVO.
	 */
	protected final List<Field> fields = new ArrayList<>();
	private final List<Field> dynamicallyAttachedPhysicalFields = new ArrayList<>();
	private transient boolean regeneratingObjectId;

	private final Field dateCreatedField;
	private final Field dateChangedField;
	private final Field dateVoidedField;
	private final Field uuidField;

	protected AbstractGeneratedDatabaseObject() {
		dateCreatedField = Field.fastCreateWithType("date_created", "DATETIME");
		dateChangedField = Field.fastCreateWithType("date_changed", "DATETIME");
		dateVoidedField = Field.fastCreateWithType("date_voided", "DATETIME");
		uuidField = Field.fastCreateWithType("uuid", "VARCHAR");

		fields.add(dateCreatedField);
		fields.add(dateChangedField);
		fields.add(dateVoidedField);
		fields.add(uuidField);
	}

	@Override
	@JsonIgnore
	public EtlDatabaseObjectConfiguration getRelatedConfiguration() {
		return relatedConfiguration;
	}

	@Override
	public void setRelatedConfiguration(EtlDatabaseObjectConfiguration configuration) {
		this.relatedConfiguration = configuration;

		attachMissingPhysicalFields(configuration);
		enrichGeneratedFields(configuration);
		enrichInheritedFields(configuration);

		refreshFields();
	}

	@Override
	public List<Field> getFields() {
		refreshFields();

		return fields;
	}

	@Override
	public Object getFieldValue(String fieldName) {
		if (utilities.equalsFieldsName(fieldName, "date_created"))
			return this.dateCreated;
		if (utilities.equalsFieldsName(fieldName, "date_changed"))
			return this.dateChanged;
		if (utilities.equalsFieldsName(fieldName, "date_voided"))
			return this.dateVoided;
		if (utilities.equalsFieldsName(fieldName, "uuid"))
			return this.uuid;
		for (Field field : dynamicallyAttachedPhysicalFields) {
			if (utilities.equalsFieldsName(fieldName, field.getName()))
				return field.getValue();
		}
		if (getRelatedConfiguration() instanceof TableConfiguration
				&& ((TableConfiguration) getRelatedConfiguration()).useSharedPKKey()) {
			if (this.getSharedPkObj() == null) {
				throw new ForbiddenOperationException("The sharedPkObj pk is not loaded");
			}
			return this.getSharedPkObj().getFieldValue(fieldName);
		}

		return super.getFieldValue(fieldName);
	}

	private void attachMissingPhysicalFields(EtlDatabaseObjectConfiguration configuration) {
		if (!(configuration instanceof AbstractTableConfiguration))
			return;

		AbstractTableConfiguration table = (AbstractTableConfiguration) configuration;
		if (table.getPhysicalTableConfiguration() == null || !table.getPhysicalTableConfiguration().hasFields())
			return;

		for (Field physicalField : table.getPhysicalTableConfiguration().copyFields()) {
			if (findField(fields, physicalField.getName()) != null)
				continue;
			Field attached = new Field();
			attached.copyFrom(physicalField);
			fields.add(attached);
			dynamicallyAttachedPhysicalFields.add(attached);
		}
	}

	private Field findField(List<Field> source, String fieldName) {
		for (Field field : source) {
			if (utilities.equalsFieldsName(field.getName(), fieldName))
				return field;
		}
		return null;
	}

	@Override
	public void setFieldValue(String fieldName, Object value) {
		Field inheritedField = findInheritedField(fieldName);
		if (inheritedField != null) {
			setInheritedFieldValue(inheritedField, value instanceof Field ? ((Field) value).getValue() : value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		}

		try {
			super.setFieldValue(fieldName, value);
			regenerateObjectIdIfKeyField(fieldName);
			return;
		} catch (ForbiddenOperationException ignored) {
			// Compatibility path for POJOs compiled before this physical field was known.
		}

		Field field = findField(dynamicallyAttachedPhysicalFields, fieldName);
		if (field == null) {
			throw new ForbiddenOperationException(
					"The field " + fieldName + " was not found on entity " + this.getClass().getName());
		}
		field.setValue(value instanceof Field ? ((Field) value).getValue() : value);
		regenerateObjectIdIfKeyField(fieldName);
	}

	protected final void regenerateObjectIdIfKeyField(String fieldName) {
		if (regeneratingObjectId || !(getRelatedConfiguration() instanceof TableConfiguration)) return;

		TableConfiguration tableConfiguration = (TableConfiguration) getRelatedConfiguration();
		if (!Boolean.TRUE.equals(tableConfiguration.isPrimaryKeyInfoLoaded())) return;
		if (tableConfiguration.getPrimaryKey() == null || tableConfiguration.getPrimaryKey().getFields() == null) return;

		for (org.openmrs.module.epts.etl.conf.Key key : tableConfiguration.getPrimaryKey().getFields()) {
			if (utilities.equalsFieldsName(fieldName, key.getName())) {
				try {
					regeneratingObjectId = true;
					loadObjectIdData(tableConfiguration);
				} finally {
					regeneratingObjectId = false;
				}
				return;
			}
		}
	}

	private void refreshFields() {
		dateCreatedField.setValue(this.dateCreated);
		dateChangedField.setValue(this.dateChanged);
		dateVoidedField.setValue(this.dateVoided);
		uuidField.setValue(this.uuid);
	}

	private void enrichInheritedFields(EtlDatabaseObjectConfiguration configuration) {
		if (configuration == null || configuration.getFields() == null)
			return;

		for (Field configured : configuration.getFields()) {
			Field inherited = findInheritedField(configured.getName());
			if (inherited == null)
				continue;
			Object value = inherited.getValue();
			inherited.copyFrom(configured);
			inherited.setValue(value);
		}
	}

	private Field findInheritedField(String name) {
		if (utilities.equalsFieldsName(name, "date_created"))
			return dateCreatedField;
		if (utilities.equalsFieldsName(name, "date_changed"))
			return dateChangedField;
		if (utilities.equalsFieldsName(name, "date_voided"))
			return dateVoidedField;
		if (utilities.equalsFieldsName(name, "uuid"))
			return uuidField;
		return null;
	}

	private void setInheritedFieldValue(Field field, Object value) {
		field.setValue(value);

		if (field == dateCreatedField) {
			this.dateCreated = (Date) value;
		} else if (field == dateChangedField) {
			this.dateChanged = (Date) value;
		} else if (field == dateVoidedField) {
			this.dateVoided = (Date) value;
		} else if (field == uuidField) {
			this.uuid = value == null ? null : value.toString();
		}
	}

	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) throws DBException {
		EtlDatabaseObjectConfiguration configuration = requireRelatedConfiguration();

		this.loadInheritedFieldWithDefaultValue(configuration, dateCreatedField, srcConn, dstConn);
		this.loadInheritedFieldWithDefaultValue(configuration, dateChangedField, srcConn, dstConn);
		this.loadInheritedFieldWithDefaultValue(configuration, dateVoidedField, srcConn, dstConn);
		this.loadInheritedFieldWithDefaultValue(configuration, uuidField, srcConn, dstConn);

		for (Field field : dynamicallyAttachedPhysicalFields) {
			if (configuration.containsField(field.getName())) {
				loadGeneratedFieldWithDefaultValue(field, srcConn, dstConn);
			}
		}
	}

	protected final void loadGeneratedFieldWithDefaultValue(Field field, Connection srcConn, Connection dstConn)
			throws DBException {

		EtlDatabaseObjectConfiguration configuration = requireRelatedConfiguration();

		if (configuration instanceof TableConfiguration) {
			TableConfiguration tableConfiguration = (TableConfiguration) configuration;
			ParentTable parent = tableConfiguration.getFieldIsRelatedParent(field);

			if (parent != null) {
				loadDefaultParentValue(tableConfiguration, parent, srcConn, dstConn);
				return;
			}
		}

		if (applyKnownDefaultValue(field) || field.allowNull())
			return;

		field.loadWithDefaultValue();
	}

	private EtlDatabaseObjectConfiguration requireRelatedConfiguration() {
		if (this.relatedConfiguration == null) {
			throw new ForbiddenOperationException("The relatedConfiguration is not set");
		}
		return this.relatedConfiguration;
	}

	private void loadInheritedFieldWithDefaultValue(EtlDatabaseObjectConfiguration configuration, Field field,
			Connection srcConn, Connection dstConn) throws DBException {

		if (configuration.getField(field.getName()) != null) {
			loadGeneratedFieldWithDefaultValue(field, srcConn, dstConn);
			setInheritedFieldValue(field, field.getValue());
		}
	}

	private boolean applyKnownDefaultValue(Field field) {
		if (field == null || field.getName() == null || getRelatedConfiguration().getRelatedEtlConf() == null
				|| !getRelatedConfiguration().getRelatedEtlConf().hasDefaultFieldsValues())
			return false;

		String fieldName = field.getName().toLowerCase();

		if (!getRelatedConfiguration().getRelatedEtlConf().getDefaultFieldValues().containsKey(fieldName))
			return false;

		Object defaultValue = getRelatedConfiguration().getRelatedEtlConf().getDefaultFieldValues().get(fieldName);

		field.setValue(utilities.parseValue(defaultValue, field.getTypeClass()));

		return true;
	}

	private void loadDefaultParentValue(TableConfiguration configuration, ParentTable parent, Connection srcConn,
			Connection dstConn) throws DBException {
		
		EtlDatabaseObject defaultParent;

		try {
			if (!parent.hasAlias())
				parent.tryToGenerateTableAlias(configuration.getRelatedEtlConf());

			if (!parent.isFullLoaded())
				parent.fullLoad(dstConn);

			defaultParent = parent.getDefaultObject(dstConn);
		} catch (Exception exception) {
			throw new EtlExceptionImpl(exception);
		}

		if (defaultParent == null) {
			defaultParent = generateDefaultParent(configuration, parent, srcConn, dstConn);
		}

		this.changeParentValue(parent, defaultParent);
	}

	private EtlDatabaseObject generateDefaultParent(TableConfiguration configuration, ParentTable parent,
			Connection srcConn, Connection dstConn) throws DBException {
		try {
			EtlDatabaseObject defaultParent = configuration.getEtlRecordClass().getDeclaredConstructor().newInstance();
			defaultParent.setRelatedConfiguration(parent);

			if (defaultParent.checkIfAllRelationshipCanBeresolved(configuration, dstConn)) {
				return parent.generateAndSaveDefaultObject(srcConn, dstConn);
			}

			throw new ForbiddenOperationException("There are recursive relationships between "
					+ configuration.getTableName() + " and " + parent.getTableName()
					+ " which cannot be automatically resolved. Please manually create default dstRecord "
					+ "for one of these tables using id '-1'.");
		} catch (ReflectiveOperationException exception) {
			throw new EtlExceptionImpl(exception);
		}
	}

	@Override
	public void copyFrom(EtlDatabaseObject source) {
		if (source == null) {
			throw new ForbiddenOperationException("You cannot copy from empty record!!!");
		}

		TableConfiguration destinationConfiguration = this.getDestinationTableConfiguration();

		for (Field destinationField : getFields()) {
			if (destinationConfiguration.isIgnorableField(destinationField)) {
				continue;
			}

			this.copyCompatibleFieldValue(source, destinationField);
		}

		this.setUuid(source.getUuid());

		this.loadUniqueKeyValues(destinationConfiguration);
		this.copySharedPkFrom(source);
	}

	private TableConfiguration getDestinationTableConfiguration() {
		if (!this.hasRelatedConfiguration()) {
			throw new ForbiddenOperationException("The relatedConfiguration is not set for dstRecord [" + this + "]");
		}
		if (!(this.getRelatedConfiguration() instanceof TableConfiguration)) {
			throw new ForbiddenOperationException("The relatedConfiguration is not a table configuration");
		}

		TableConfiguration configuration = (TableConfiguration) this.getRelatedConfiguration();
		if (!configuration.isFullLoaded()) {
			throw new ForbiddenOperationException("The relatedConfiguration is not full loaded");
		}
		return configuration;
	}

	private void copyCompatibleFieldValue(EtlDatabaseObject source, Field destinationField) {
		try {
			setFieldValue(destinationField.getName(), source.getFieldValue(destinationField.getName()));
		} catch (ForbiddenOperationException noPhysicalNameMatch) {
			try {
				setFieldValue(destinationField.getName(), source.getFieldValue(destinationField.getNameAsClassAtt()));
			} catch (ForbiddenOperationException noClassAttributeMatch) {
				// Source and destination models need not expose exactly the same fields.
			}
		}
	}

	private void copySharedPkFrom(EtlDatabaseObject source) {
		if (getSharedPkObj() != null && source.getSharedPkObj() != null) {
			getSharedPkObj().copyFrom(source.getSharedPkObj());
		}
	}
}
