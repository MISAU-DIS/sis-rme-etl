package org.openmrs.module.epts.etl.conf.interfaces;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.datasource.DataSourceField;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.FieldNotAvaliableInAnyDataSource;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.MissingParameterOnEtlTransformationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.SQLUtilities;

public interface EtlTranformTarget extends EtlDatabaseObjectConfiguration {
	
	void loadDataSourceInfo(Connection conn) throws DBException;
	
	Boolean isLoadedDataSourceInfo();
	
	List<EtlDataSource> getAllPrefferredDataSource();
	
	void setAllNotPrefferredDataSource(List<EtlDataSource> ds);
	
	void setAllAvaliableDataSource(List<EtlDataSource> ds);
	
	ActionOnEtlIssue onMultipleDataSourceForSameMapping();
	
	Boolean isIgnoreUnmappedFields();
	
	SrcConf getSrcConf();
	
	List<FieldsMapping> getAllMapping();
	
	List<EtlDataSource> getAllNotPrefferredDataSource();
	
	List<EtlDataSource> getAllAvaliableDataSource();
	
	void setMapping(List<FieldsMapping> mapping);
	
	void setAllMapping(List<FieldsMapping> allMapping);
	
	String getSrcObjectCondition();
	
	Boolean isAutoIncrementId();
	
	EtlDatabaseObject getTargetDefaultObject(Connection srcConn, Connection dstConn) throws DBException;
	
	default Boolean ignoreUnmappedFields() {
		try {
			return this.isIgnoreUnmappedFields();
		}
		catch (NullPointerException e) {
			return false;
		}
	}
	
	default Boolean hasSrcObjectCondition() {
		return utilities.stringHasValue(this.getSrcObjectCondition());
	}
	
	default Boolean checkIfSrcObjectCanBeLoaded(EtlDatabaseObject srcObject, List<EtlDatabaseObject> avaliableSrcObjects,
	        Connection srcConn, Connection dstConn) throws DBException {
		if (this.hasSrcObjectCondition()) {
			try {
				
				List<EtlDatabaseObject> list = utilities.listHasElement(avaliableSrcObjects) ? avaliableSrcObjects
				        : utilities.parseToList(srcObject);
				
				String preparedCondition = SQLUtilities.ensureDataSourceElementsReplaced(this.getSrcObjectCondition(), list,
				    dstConn);
				
				if (matchesCondition(srcObject, preparedCondition)) {
					return true;
				}
			}
			catch (MissingParameterOnEtlTransformationException e) {
				return false;
			}
			
		} else {
			return true;
		}
		
		return false;
	}
	
	default Boolean matchesCondition(EtlDatabaseObject obj, String condition) {
		
		condition = condition.replaceAll("(?i)\\s+or\\s+", "||");
		condition = condition.replaceAll("(?i)\\s+and\\s+", "&&");
		
		String[] orConditions = condition.split("\\|\\|");
		
		for (String orCond : orConditions) {
			
			Boolean andResult = true;
			
			String[] andConditions = orCond.split("&&");
			
			for (String andCond : andConditions) {
				
				if (!evaluateCondition(obj, andCond.trim())) {
					andResult = false;
					break;
				}
			}
			
			if (andResult) {
				return true;
			}
		}
		
		return false;
	}
	
	default Boolean evaluateCondition(EtlDatabaseObject obj, String condition) {
		
		// IN
		if (condition.matches("(?i).+\\s+in\\s*\\(.+\\)")) {
			return evaluateIn(obj, condition);
		}
		
		// LIKE
		if (condition.matches("(?i).+\\s+like\\s+.+")) {
			return evaluateLike(obj, condition);
		}
		
		// operadores de comparação
		String operator = null;
		
		if (condition.contains(">="))
			operator = ">=";
		else if (condition.contains("<="))
			operator = "<=";
		else if (condition.contains("!="))
			operator = "!=";
		else if (condition.contains(">"))
			operator = ">";
		else if (condition.contains("<"))
			operator = "<";
		else if (condition.contains("="))
			operator = "=";
		
		if (operator == null) {
			throw new IllegalArgumentException("Unsupported condition: " + condition);
		}
		
		String[] parts = condition.split("\\Q" + operator + "\\E");
		
		String field = parts[0].trim();
		String expected = stripQuotes(parts[1].trim());
		
		Object value;
		
		try {
			value = obj.getFieldValue(field);
		}
		catch (ForbiddenOperationException e) {
			value = field;
		}
		
		if (value == null) {
			value = "null";
		}
		
		String actual = value.toString();
		
		switch (operator) {
			
			case "=":
				return actual.equals(expected);
			
			case "!=":
				return !actual.equals(expected);
			
			case ">":
				return compare(actual, expected) > 0;
			
			case "<":
				return compare(actual, expected) < 0;
			
			case ">=":
				return compare(actual, expected) >= 0;
			
			case "<=":
				return compare(actual, expected) <= 0;
			
			default:
				throw new IllegalArgumentException("Unsupported operator");
		}
	}
	
	default int compare(String a, String b) {
		
		try {
			Double da = Double.valueOf(a);
			Double db = Double.valueOf(b);
			
			return da.compareTo(db);
			
		}
		catch (NumberFormatException e) {
			return a.compareTo(b);
		}
	}
	
	default Boolean evaluateIn(EtlDatabaseObject obj, String condition) {
		
		String[] parts = condition.split("(?i)in");
		
		String field = parts[0].trim();
		String valuesPart = parts[1].trim();
		
		valuesPart = valuesPart.replaceAll("[()]", "");
		
		Object value = obj.getFieldValue(field);
		
		if (value == null)
			return false;
		
		String actual = value.toString();
		
		for (String v : valuesPart.split(",")) {
			
			if (actual.equals(stripQuotes(v.trim()))) {
				return true;
			}
		}
		
		return false;
	}
	
	default Boolean evaluateLike(EtlDatabaseObject obj, String condition) {
		
		String[] parts = condition.split("(?i)like");
		
		String field = parts[0].trim();
		String pattern = stripQuotes(parts[1].trim());
		
		Object value = obj.getFieldValue(field);
		
		if (value == null)
			return false;
		
		String actual = value.toString();
		
		String regex = pattern.replace("%", ".*").replace("_", ".");
		
		return actual.matches(regex);
	}
	
	default String stripQuotes(String s) {
		return s.replaceAll("^['\"]|['\"]$", "");
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
		for (EtlDataSource ds : this.getAllAvaliableDataSource()) {
			if (ds.getAlias().trim().equals(dsName.trim())) {
				return ds;
			}
		}
		
		for (EtlDataSource ds : this.getAllAvaliableDataSource()) {
			if (ds.getName().equals(dsName)) {
				return ds;
			}
		}
		
		return null;
	}
	
	default void tryToLoadDataSourceToFieldMapping(FieldsMapping fm, Connection conn)
	        throws FieldNotAvaliableInAnyDataSource, FieldAvaliableInMultipleDataSources, DBException {
		
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
		
		if (utilities.listHasElement(this.getAllPrefferredDataSource())) {
			for (EtlDataSource pref : this.getAllPrefferredDataSource()) {
				if (pref.containsField(fm.getSrcField())) {
					fm.setDataSourceName(pref.getAlias());
					fm.setDataSource(pref);
					
					fm.loadType(this, pref, conn);
					
					if (fm.getDefaultValue() == null) {
						
						Field f = pref.getField(fm.getSrcField());
						
						if (f instanceof DataSourceField) {
							DataSourceField prefField = (DataSourceField) f;
							
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
			if (ignoreUnmappedFields()) {
				EtlDatabaseObject defaultObject = getTargetDefaultObject(conn, conn);
				
				if (defaultObject != null) {
					fm.setSrcValue(defaultObject.getFieldValue(fm.getDstField()));
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
