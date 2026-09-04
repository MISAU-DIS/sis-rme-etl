package org.openmrs.module.epts.etl.model.pojo.generic;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;

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
		if (getRelatedConfiguration() instanceof TableConfiguration
				&& ((TableConfiguration) getRelatedConfiguration()).useSharedPKKey()) {
			if (this.getSharedPkObj() == null) {
				throw new ForbiddenOperationException("The sharedPkObj pk is not loaded");
			}
			return this.getSharedPkObj().getFieldValue(fieldName);
		}

		return super.getFieldValue(fieldName);
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

	@Override
	public void loadWithDefaultValues(Connection srcConn, Connection dstConn) throws DBException {
		EtlDatabaseObjectConfiguration configuration = requireRelatedConfiguration();

		loadInheritedFieldWithDefaultValue(configuration, dateCreatedField, srcConn, dstConn);
		loadInheritedFieldWithDefaultValue(configuration, dateChangedField, srcConn, dstConn);
		loadInheritedFieldWithDefaultValue(configuration, dateVoidedField, srcConn, dstConn);
		loadInheritedFieldWithDefaultValue(configuration, uuidField, srcConn, dstConn);
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
