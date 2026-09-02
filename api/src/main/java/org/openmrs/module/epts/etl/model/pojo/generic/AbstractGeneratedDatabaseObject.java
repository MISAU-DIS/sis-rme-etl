package org.openmrs.module.epts.etl.model.pojo.generic;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;

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
	public Object getFieldValue(String fieldName) {
		String fieldNameInSnakeCase = utilities.parsetoSnakeCase(fieldName);
		String fieldNameInCameCase = utilities.parseToCamelCase(fieldName);

		try {
			return utilities.getFieldValueOnFieldList(utilities.parseList(this.fields, Field.class),
					fieldNameInSnakeCase);
		} catch (ForbiddenOperationException e) {

			try {
				return utilities.getFieldValueOnFieldList(utilities.parseList(this.fields, Field.class),
						fieldNameInCameCase);
			} catch (ForbiddenOperationException e1) {
				if (getRelatedConfiguration() instanceof TableConfiguration) {

					if (((TableConfiguration) getRelatedConfiguration()).useSharedPKKey()) {

						if (this.getSharedPkObj() == null) {
							throw new ForbiddenOperationException("The sharedPkObj pk is not loaded");
						}

						return this.getSharedPkObj().getFieldValue(fieldName);
					}
				}
				return super.getFieldValue(fieldName);

			}
		}

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
