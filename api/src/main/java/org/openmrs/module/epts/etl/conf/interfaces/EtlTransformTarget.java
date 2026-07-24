package org.openmrs.module.epts.etl.conf.interfaces;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.AtomicCondition;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSourceField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.FieldMappingResolutionStrategy;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.transformer.FieldTransformerType;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.FieldNotAvaliableInAnyDataSource;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.InvalidAtomicConditionException;
import org.openmrs.module.epts.etl.exceptions.MissingFieldException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public interface EtlTransformTarget extends EtlDatabaseObjectConfiguration, ConditionalEtlElement {

	void loadDataSourceInfo(Connection conn) throws DBException;

	Boolean isLoadedDataSourceInfo();

	List<EtlDataSource> getAllPrefferredDataSource();

	void setAllNotPrefferredDataSource(List<EtlDataSource> ds);

	void setAllAvaliableDataSource(List<EtlDataSource> ds);

	ActionOnEtlIssue onMultipleDataSourceForSameMapping();

	SrcConf getSrcConf();

	List<FieldsMapping> getAllMapping();

	List<EtlDataSource> getAllNotPrefferredDataSource();

	List<EtlDataSource> getAllAvaliableDataSource();

	void setMapping(List<FieldsMapping> mapping);

	void setAllMapping(List<FieldsMapping> allMapping);

	String getSrcObjectCondition();

	Boolean isAutoIncrementId();

	EtlDatabaseObject getTargetDefaultObject(Connection srcConn, Connection dstConn) throws DBException;

	ActionOnEtlIssue unmappedFieldBehavior();

	FieldMappingResolutionStrategy mappingResolutionStrategy();

	default Boolean useAsDataSource() {
		return false;
	}

	default Boolean hasUnmappedFieldBehavior() {
		return this.unmappedFieldBehavior() != null;
	}

	default Boolean hasMappingResolutionStrategy() {
		return this.mappingResolutionStrategy() != null;
	}

	default void addMapping(FieldsMapping fm) throws ForbiddenOperationException {
		if (this.getAllMapping() == null) {
			this.setAllMapping(new ArrayList<FieldsMapping>());
		}

		List<FieldsMapping> duplicates = findMappingsByDstField(fm.getDstField());

		if (duplicates.isEmpty()) {
			this.getAllMapping().add(fm);
			return;
		}

		if (!allowDuplicateDestinationMappings()) {
			throw new ForbiddenOperationException("Duplicate mapping for destination field '" + fm.getDstField()
					+ "' is not allowed. Enable allowDuplicateDestinationMappings "
					+ "only when duplicate mappings are controlled by mutually exclusive applyCondition values.");
		}

		for (FieldsMapping existing : duplicates) {
			validateDuplicateMapping(existing, fm);
		}

		this.getAllMapping().add(fm);
	}

	default List<FieldsMapping> findMappingsByDstField(String dstField) {

		List<FieldsMapping> result = new ArrayList<>();

		if (this.getAllMapping() == null || dstField == null) {
			return result;
		}

		for (FieldsMapping mapping : this.getAllMapping()) {
			if (dstField.equals(mapping.getDstField())) {
				result.add(mapping);
			}
		}

		return result;
	}

	default FieldsMapping findMappingByDstField(String dstField) {

		if (this.getAllMapping() == null || dstField == null) {
			return null;
		}

		for (FieldsMapping mapping : this.getAllMapping()) {
			if (dstField.equals(mapping.getDstField())) {
				return mapping;
			}
		}

		return null;
	}

	default void validateDuplicateMapping(FieldsMapping existing, FieldsMapping candidate) {

		if (!hasCondition(existing) || !hasCondition(candidate)) {
			throw new ForbiddenOperationException("Duplicate mappings for destination field '" + candidate.getDstField()
					+ "' are only allowed when all duplicated mappings define an applyCondition.");
		}

		if (!conditionsDoNotIntersect(existing.getCondition(), candidate.getCondition())) {
			throw new ForbiddenOperationException("Duplicate mappings for destination field '" + candidate.getDstField()
					+ "' must have mutually exclusive applyCondition values. " + "Existing condition: ["
					+ existing.getCondition() + "], " + "new condition: [" + candidate.getCondition() + "]");
		}
	}

	default boolean hasCondition(FieldsMapping mapping) {
		return mapping != null && mapping.getCondition() != null && !mapping.getCondition().trim().isEmpty();
	}

	default Boolean allowDuplicateDestinationMappings() {
		return false;
	}

	default boolean conditionsDoNotIntersect(String conditionA, String conditionB)
			throws InvalidAtomicConditionException {

		try {
			AtomicCondition a = AtomicCondition.parse(conditionA);
			AtomicCondition b = AtomicCondition.parse(conditionB);

			return a.doesNotIntersectWith(b);

		} catch (InvalidAtomicConditionException e) {

			throw new InvalidAtomicConditionException(
					"Unable to determine whether two applyCondition expressions may overlap.\n\n" + "Condition A: "
							+ conditionA + "\n" + "Condition B: " + conditionB + "\n\n" + e.getMessage(),
					e);
		}
	}

	default EtlDataSource findDataSource(String dsName) {

		if (this.getAllAvaliableDataSource() != null) {
			for (EtlDataSource ds : this.getAllAvaliableDataSource()) {
				if (ds instanceof SrcConf && ((SrcConf) ds).doNotUseAsDatasource()) {
					continue;
				}

				if (ds.getAlias().trim().equals(dsName.trim())) {
					return ds;
				}
			}
		}

		if (this.getAllAvaliableDataSource() != null) {
			for (EtlDataSource ds : this.getAllAvaliableDataSource()) {
				if (ds instanceof SrcConf && ((SrcConf) ds).doNotUseAsDatasource()) {
					continue;
				}

				if (ds.getName().trim().equals(dsName.trim())) {
					return ds;
				}
			}
		}

		if (this.hasPreviousDataSourceTargets()) {
			for (EtlTransformTarget ds : this.retrievePreviousDataSourceTargets()) {
				if (((EtlDataSource) ds).getAlias().trim().equals(dsName.trim())) {
					return (EtlDataSource) ds;
				}
			}

			for (EtlTransformTarget ds : this.retrievePreviousDataSourceTargets()) {
				if (((EtlDataSource) ds).getName().trim().equals(dsName.trim())) {
					return (EtlDataSource) ds;
				}
			}
		}

		return null;
	}

	default void tryToLoadDataSourceToFieldMapping(FieldsMapping fm, Connection conn)
			throws FieldNotAvaliableInAnyDataSource, FieldAvaliableInMultipleDataSources, DBException {

		getRelatedEtlConf().trace(
				"Initializing dataSource info loading within FieldsMapping {} transformer {} on target {}", fm,
				fm.getTransformer(), this);

		if (fm.hasTransformer()) {
			fm.tryToLoadTransformer(this, conn);

			if (!fm.useDefaultTransformer()) {
				return;
			}
		}

		if (this.getPrimaryKey() != null && this.getPrimaryKey().asSimpleKey().getName().equals(fm.getDstField())
				&& this.isAutoIncrementId()) {
			return;
		}

		if (!this.isLoadedDataSourceInfo()) {
			this.loadDataSourceInfo(conn);
		}

		int qtyOccurences = 0;

		if (fm.getSrcValue() != null || fm.isMapToNullValue()) {
			return;
		}

		if (fm.hasDataSourceName()) {
			fm.setDataSource(findDataSource(fm.getDataSourceName()));
			return;
		}

		if (this.hasMappingResolutionStrategy()) {
			if (!this.mappingResolutionStrategy().allowAuto() && this.mappingResolutionStrategy().allowDefault()) {
				EtlDatabaseObject defaultObject = this.getTargetDefaultObject(conn, conn);

				if (defaultObject != null) {
					try {
						fm.setSrcValue(defaultObject.getFieldValue(fm.getDstField()));
					} catch (MissingFieldException e) {
						getRelatedEtlConf().err(
								"Error while loading data source info within FieldsMapping {} transformer {} on target {}",
								fm, fm.getTransformer(), this);

						throw e;
					}
					fm.resetAndLoadTransformer(this, FieldTransformerType.SIMPLE_VALUE_TRANSFORMER, conn);
				} else {
					throw new ForbiddenOperationException("The default object was not generated...!");
				}

				return;
			}
		}

		if (utilities.listHasElement(this.getAllPrefferredDataSource())) {
			for (EtlDataSource pref : this.getAllPrefferredDataSource()) {
				if (pref.containsField(fm.getSrcField())) {
					fm.setDataSourceName(pref.getAlias());
					fm.setDataSource(pref);

					fm.loadType(this, pref, conn);

					if (fm.getDefaultValue() == null) {

						Field f = pref.getField(fm.getSrcField());

						if (f instanceof TransformableDataSourceField) {
							TransformableDataSourceField prefField = (TransformableDataSourceField) f;

							if (prefField.getDefaultValue() != null) {
								fm.setDefaultValue(prefField.getDefaultValue());
								fm.setOverrideTriggerValue(prefField.getOverrideTriggerValue());
							}
						}
					}

					qtyOccurences++;

					break;
				}
			}
		}

		if (qtyOccurences == 0 && utilities.listHasElement(this.getAllNotPrefferredDataSource())) {
			for (EtlDataSource notPref : this.getAllNotPrefferredDataSource()) {
				if (notPref.containsField(fm.getSrcField())) {
					qtyOccurences++;

					if (qtyOccurences > 1) {
						fm.getPossibleSrc().add(notPref.getAlias());

						break;
					} else {
						fm.setDataSourceName(notPref.getAlias());
						fm.setDataSource(notPref);
						fm.loadType(this, notPref, conn);
					}
				}
			}
		}

		if (qtyOccurences == 0 && this instanceof DstConf) {
			DstConf c = (DstConf) this;

			if (c.hasParentDstConf()) {
				ParentTable ref = c.getFieldIsRelatedParent(fm);

				if (ref != null) {
					EtlDataSource parentRelatedDs = c.findParentDataSource(ref);

					qtyOccurences++;

					fm.setSrcField(parentRelatedDs.getPrimaryKey().asSimpleKey().getName());
					fm.setDataSourceName(parentRelatedDs.getAlias());
					fm.setDataSource(parentRelatedDs);
					fm.loadType(this, parentRelatedDs, conn);
				}
			}

		}

		List<EtlTransformTarget> previousDataSourceTarget = this.retrievePreviousDataSourceTargets();

		if (qtyOccurences == 0 && utilities.listHasElement(previousDataSourceTarget)) {
			for (EtlTransformTarget ds : previousDataSourceTarget) {
				if (ds.containsField(fm.getSrcField())) {
					qtyOccurences++;

					if (qtyOccurences > 1) {
						fm.getPossibleSrc().add(ds.getAlias());

						break;
					} else {
						fm.setDataSourceName(ds.getAlias());
						fm.setDataSource((EtlDataSource) ds);
						fm.loadType(this, (EtlDataSource) ds, conn);
					}
				}
			}
		}

		Boolean hasTransformer = fm.hasTransformer() && !fm.useDefaultTransformer();

		if (hasTransformer) {
			fm.loadType(this, null, conn);

			if (fm.getDataSource() == null) {
				fm.setDataSource(this.getSrcConf());
			}
		}

		if (qtyOccurences == 0 && !hasTransformer) {
			if (hasUnmappedFieldBehavior()) {
				if (unmappedFieldBehavior().useDefaultOnMissingMapping()) {
					EtlDatabaseObject defaultObject = getTargetDefaultObject(conn, conn);

					if (defaultObject != null) {
						fm.setSrcValue(defaultObject.getFieldValue(fm.getDstField()));
						fm.resetAndLoadTransformer(this, FieldTransformerType.SIMPLE_VALUE_TRANSFORMER, conn);
					}
				} else if (!unmappedFieldBehavior().ignore()) {
					throw new FieldNotAvaliableInAnyDataSource(fm.getSrcField());
				}
			} else {
				throw new FieldNotAvaliableInAnyDataSource(fm.getSrcField());
			}
		}

		if (qtyOccurences > 1 && !hasTransformer && !this.onMultipleDataSourceForSameMapping().useLast()) {
			throw new FieldAvaliableInMultipleDataSources(fm.getSrcField());
		}

	}

	/**
	 * Returns all destination configurations that:
	 * <ul>
	 * <li>belong to the same parent ETL item configuration;</li>
	 * <li>appear before this destination configuration;</li>
	 * <li>are configured to be used as transformation data sources.</li>
	 * </ul>
	 *
	 * <p>
	 * The returned list preserves the declaration order in the ETL configuration,
	 * allowing subsequent destination mappings to reference records produced by
	 * previously executed destination configurations.
	 *
	 * @param conn the database connection (currently unused)
	 * @return all preceding destination configurations that can be used as data
	 *         sources
	 */
	default List<EtlTransformTarget> retrievePreviousDataSourceTargets() {
		return null;
	}

	default Boolean hasPreviousDataSourceTargets() {
		return utilities.listHasElement(this.retrievePreviousDataSourceTargets());
	}

	default void addToPrefferedDataSource(EtlDataSource ds) {
		if (this.getAllPrefferredDataSource() == null) {
			this.setAllPrefferredDataSource(new ArrayList<>());
		}

		if (ds == null)
			throw new EtlExceptionImpl("Empty ds was provided");

		for (EtlDataSource ds1 : this.getAllPrefferredDataSource()) {
			if (ds == ds1) {
				return;
			}
		}

		this.getAllPrefferredDataSource().add(ds);
	}

	void setAllPrefferredDataSource(List<EtlDataSource> arrayList);

	default void addToNotPrefferedDataSource(EtlDataSource ds) {
		if (this.getAllNotPrefferredDataSource() == null) {
			this.setAllNotPrefferredDataSource(new ArrayList<>());
		}

		if (ds == null)
			throw new ForbiddenOperationException("Empty ds was provided");

		for (EtlDataSource ds1 : this.getAllNotPrefferredDataSource()) {
			if (ds == ds1) {
				return;
			}
		}

		this.getAllNotPrefferredDataSource().add(ds);
	}

	default void addToAvaliableDataSource(EtlDataSource ds) {
		if (this.getAllAvaliableDataSource() == null) {
			this.setAllAvaliableDataSource(new ArrayList<>());
		}

		if (ds == null)
			throw new ForbiddenOperationException("Empty ds was provided");

		for (EtlDataSource ds1 : this.getAllAvaliableDataSource()) {
			if (ds == ds1) {
				return;
			}
		}

		this.getAllAvaliableDataSource().add(ds);
	}

	default void addAllToAvaliableDataSource(List<? extends EtlDataSource> ds) {
		if (utilities.listHasElement(ds)) {
			for (EtlDataSource d : ds) {
				addToAvaliableDataSource(d);
			}
		}
	}

	default void addAllToPreferredDataSource(List<EtlDataSource> ds) {
		if (utilities.listHasElement(ds)) {
			for (EtlDataSource d : ds) {
				addToPrefferedDataSource(d);
			}
		}
	}

	default boolean hasDataSource() {
		return utilities.listHasElement(this.getAllAvaliableDataSource());
	}

}
