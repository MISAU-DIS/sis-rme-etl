package org.openmrs.module.epts.etl.conf.interfaces;

import org.openmrs.module.epts.etl.etl.processor.transformer.EtlFieldTransformer;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.MissingParameterOnEtlTransformationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public interface GenericEtlTransformTarget {
	
	static final CommonUtilities utils = CommonUtilities.getInstance();
	
	String getSrcObjectCondition();
	
	default Boolean hasSrcObjectCondition() {
		return utils.stringHasValue(this.getSrcObjectCondition());
	}
	
	default Boolean checkIfSrcObjectCanBeLoaded(EtlDatabaseObject srcObject) throws DBException {
		if (this.hasSrcObjectCondition()) {
			
			try {
				if (matchesCondition(srcObject,
				    EtlFieldTransformer.tryToReplaceParametersOnSrcValue(srcObject.collectAllAvaliableSrcObjects(),
				        this.getSrcObjectCondition()).toString())) {
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
}
