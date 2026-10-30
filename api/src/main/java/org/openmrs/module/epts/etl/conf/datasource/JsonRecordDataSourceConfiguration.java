package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** Logical ETL data source extracted from one node of a primary JSON record. */
public class JsonRecordDataSourceConfiguration extends AbstractRecordPrimaryDataSourceConfiguration
		implements EtlAdditionalDataSource {

	private String path;

	private JsonRecordCardinality cardinality = JsonRecordCardinality.ONE;

	private Boolean required;

	private String condition;

	private List<Field> fields;

	private transient PrimaryDataSourceConfiguration relatedPrimaryDataSource;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public JsonRecordCardinality getCardinality() {
		return cardinality == null ? JsonRecordCardinality.ONE : cardinality;
	}

	public void setCardinality(JsonRecordCardinality cardinality) {
		this.cardinality = cardinality;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	@Override
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	public Boolean isRequired() {
		return isTrue(required);
	}

	@Override
	public List<Field> getFields() {
		return fields;
	}

	@Override
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	@Override
	protected String getSourceTypeName() {
		return "extraJsonRecordDataSource";
	}

	@Override
	public PrimaryDataSourceConfiguration getRelatedPrimaryDataSource() {
		return relatedPrimaryDataSource;
	}

	@Override
	public void setRelatedPrimaryDataSource(PrimaryDataSourceConfiguration primaryDataSource) {
		this.relatedPrimaryDataSource = primaryDataSource;
		setParentSource(primaryDataSource == null ? null : primaryDataSource.getParentSource());
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return relatedPrimaryDataSource;
	}

	@Override
	public void setParentConf(EtlDataConfiguration relatedParent) {
		if (!(relatedParent instanceof PrimaryDataSourceConfiguration)) {
			throw new org.openmrs.module.epts.etl.exceptions.EtlConfException(
					"An extra JSON record data source must belong to a primary data source");
		}
		setRelatedPrimaryDataSource((PrimaryDataSourceConfiguration) relatedParent);
	}

	@Override
	public SrcConf getRelatedSrcConf() {
		return relatedPrimaryDataSource instanceof SrcConf ? (SrcConf) relatedPrimaryDataSource : null;
	}

	@Override
	public void setRelatedSrcConf(SrcConf relatedSrcConf) {
		setRelatedPrimaryDataSource(relatedSrcConf);
	}

	@Override
	public EtlDatabaseObject loadRelatedSrcObject(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject dstObject, List<EtlDatabaseObject> avaliableSrcObjects, Connection conn)
			throws DBException {
		if (avaliableSrcObjects != null) {
			for (EtlDatabaseObject object : avaliableSrcObjects) {
				if (object != null && object.getRelatedConfiguration() == this)
					return object;
			}
		}
		if (srcObject != null && srcObject.getAuxLoadObject() != null) {
			for (EtlDatabaseObject object : srcObject.getAuxLoadObject()) {
				if (object != null && object.getRelatedConfiguration() == this)
					return object;
			}
		}
		return null;
	}
}
