package org.openmrs.module.epts.etl.conf.interfaces;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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

		if (this.getAllMapping().contains(fm))
			throw new ForbiddenOperationException("The field [" + fm + "] already exists on mapping");

		this.getAllMapping().add(fm);
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

		return null;
	}

	default void tryToLoadDataSourceToFieldMapping(FieldsMapping fm, Connection conn)
			throws FieldNotAvaliableInAnyDataSource, FieldAvaliableInMultipleDataSources, DBException {

		if (fm.hasTransformer()) {
			fm.tryToLoadTransformer(this, conn);

			if (!fm.useDefaultTransformer()) {
				return;
			}
		}

		if (getPrimaryKey() != null && getPrimaryKey().asSimpleKey().getName().equals(fm.getDstField())
				&& isAutoIncrementId()) {
			return;
		}

		if (!isLoadedDataSourceInfo()) {
			loadDataSourceInfo(conn);
		}

		int qtyOccurences = 0;

		if (fm.getSrcValue() != null || fm.isMapToNullValue()) {
			return;
		}

		if (hasMappingResolutionStrategy()) {
			if (!mappingResolutionStrategy().allowAuto() && mappingResolutionStrategy().allowDefault()) {
				EtlDatabaseObject defaultObject = getTargetDefaultObject(conn, conn);

				if (defaultObject != null) {
					fm.setSrcValue(defaultObject.getFieldValue(fm.getDstField()));
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

}
