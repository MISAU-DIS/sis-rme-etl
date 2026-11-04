package org.openmrs.module.epts.etl.conf.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.ParentTableImpl;
import org.openmrs.module.epts.etl.conf.RefMapping;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a parent table.
 */
@JsonDeserialize(as = ParentTableImpl.class)
public interface ParentTable extends RelatedTable {

	TableConfiguration getChildTableConf();

	void setChildTableConf(TableConfiguration childTableConf);

	public List<Field> getConditionalFields();

	public void setConditionalFields(List<Field> conditionalFields);

	default Boolean isGeneric() {
		return Boolean.FALSE;
	}

	default DBConnectionInfo getRelatedConnInfo() {
		return this.getChildTableConf().getRelatedConnInfo();
	}

	default UniqueKeyInfo parseRelationshipToSelfKey() {
		UniqueKeyInfo uk = new UniqueKeyInfo(this);

		for (RefMapping map : this.getRefMapping()) {
			uk.addKey(new Key(map.getParentFieldName()));
		}

		return uk;
	}

	@Override
	default String generateJoinCondition() {
		String conditionFields = "";

		for (int i = 0; i < this.getRefMapping().size(); i++) {
			if (i > 0)
				conditionFields += " AND ";

			RefMapping field = this.getRefMapping().get(i);

			conditionFields += getRelatedTabConf().getTableAlias() + "." + field.getChildFieldName() + " = "
					+ this.getTableAlias() + "." + field.getParentFieldName();
		}

		return conditionFields;
	}

	default Boolean hasConditionalFields() {
		return utilities.listHasElement(this.getConditionalFields());
	}

	/**
	 * Checks whether a record value matches one of the comma-separated values
	 * configured for a conditional field.
	 */
	default boolean matchesConditionalFieldValue(Field conditionalField, Object recordValue) {
		if (conditionalField == null || conditionalField.getValue() == null || recordValue == null) {
			return false;
		}

		String configuredValue = conditionalField.getValue().toString();
		String currentValue = recordValue.toString();

		if (!configuredValue.contains(",")) {
			return configuredValue.equals(currentValue);
		}

		for (String acceptedValue : configuredValue.split(",", -1)) {
			if (acceptedValue.trim().equals(currentValue)) {
				return true;
			}
		}

		return false;
	}

	default List<DstConf> findRelatedDstConf(EtlOperationConfig operationConf) throws DBException {
		List<DstConf> allDstForTable = new ArrayList<>();

		for (EtlItemConfiguration conf : getRelatedEtlConf().resolveEtlItems(operationConf)) {

			if (conf.containsDstTable(getTableName())) {
				if (!conf.isFullLoaded()) {
					conf.fullLoad(operationConf);
				}

				for (DstConf dst : conf.getDstConf()) {
					if (dst.getTableName().equals(this.getTableName())) {
						allDstForTable.add(dst);
					}
				}
			}
		}

		return allDstForTable;
	}

	default PrimaryDataSourceConfiguration findRelatedPrimaryDataSource(EtlOperationConfig operationConf)
			throws DBException {
		for (EtlItemConfiguration conf : getRelatedEtlConf().resolveEtlItems(operationConf)) {

			if (!conf.isFullLoaded()) {
				conf.fullLoad(operationConf);
			}

			PrimaryDataSourceConfiguration primary = conf.getPrimaryDataSource();
			if (primary.getObjectName().equals(this.getTableName())) {
				return primary;
			}
		}

		return null;
	}

	/**
	 * Finds all the srcConf which match with this parent table and have one dstConf
	 * matching with this parent
	 * 
	 * @return
	 * @throws DBException
	 */
	default List<PrimaryDataSourceConfiguration> findRelatedPrimaryDataSourcesWithMatchingDestination(
			EtlOperationConfig operationConf)
			throws DBException {
		List<PrimaryDataSourceConfiguration> sources = new ArrayList<>();

		for (EtlItemConfiguration conf : getRelatedEtlConf().resolveEtlItems(operationConf)) {

			PrimaryDataSourceConfiguration primary = conf.getPrimaryDataSource();
			if (primary.getObjectName().equals(this.getTableName())) {

				if (!conf.isFullLoaded()) {
					conf.fullLoad(operationConf);
				}

				if (conf.containsDstTable(this.getTableName())) {
					sources.add(primary);
				}
			}
		}

		return sources;
	}

	Object getDefaultValueDueInconsistency();

	void setDefaultValueDueInconsistency(Object defaultValueDueInconsistency);

	Boolean isSetNullDueInconsistency();

	void setSetNullDueInconsistency(Boolean setNullDueInconsistency);

	default Boolean hasDefaultValueDueInconsistency() {
		return getDefaultValueDueInconsistency() != null;
	}

	default Boolean hasMoreThanOneConditionalFields() {
		if (hasConditionalFields()) {
			return utilities.arrayHasMoreThanOneElements(getConditionalFields());
		}

		return false;
	}

	default List<Field> parseMappingToChildFields() {
		if (!hasMapping())
			return null;

		List<Field> f = new ArrayList<>();

		for (RefMapping map : this.getRefMapping()) {
			f.add(new Field(map.getChildFieldName()));
		}

		return f;
	}

	default List<Field> parseMappingToParentFields() {
		if (!hasMapping())
			return null;

		List<Field> f = new ArrayList<>();

		for (RefMapping map : this.getRefMapping()) {
			f.add(new Field(map.getParentFieldName()));
		}

		return f;
	}

	default void addRefMapping(RefMapping cloned) {
		if (!hasMapping())
			setRefMapping(new ArrayList<>());

		if (!getRefMapping().contains(cloned)) {
			getRefMapping().add(cloned);
		}

	}

	default String getChildFieldName() {
		if (!this.useSimplePk()) {
			throw new ForbiddenOperationException("The parent " + this + " does not use simple pk");
		}

		RefMapping map = this.getRefMapping().get(0);

		return map.getChildFieldName();
	}

}
