package org.openmrs.module.epts.etl.model.pojo.generic;

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

	@Override
	@JsonIgnore
	public EtlDatabaseObjectConfiguration getRelatedConfiguration() {
		return relatedConfiguration;
	}

	@Override
	public void setRelatedConfiguration(EtlDatabaseObjectConfiguration configuration) {
		this.relatedConfiguration = configuration;

		enrichGeneratedFields(configuration);
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
