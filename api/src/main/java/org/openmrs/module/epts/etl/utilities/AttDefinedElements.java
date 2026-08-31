package org.openmrs.module.epts.etl.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;

/**
 * Utilities class which help to define att elements for class like Att
 * definition, getter and setter definition, etc
 * 
 * @author jpboane
 */
public class AttDefinedElements {

	private static CommonUtilities utilities = CommonUtilities.getInstance();

	private String attDefinition;

	private String setterDefinition;

	private String getterDefinition;

	private String resultSetLoadDefinition;

	private String sqlInsertFirstPartDefinition;

	private String sqlInsertLastEndPartDefinition;

	private String sqlUpdateDefinition;

	private String sqlInsertParamDefinifion;

	private String sqlUpdateParamDefinifion;

	private String sqlInsertValues;

	private String attName;

	private String attType;

	private String dbAttName;

	private String aliasedDbAttName;

	private String dbAttType;

	private boolean isPartOfObjectId;

	private boolean isLast;
	private boolean usesFieldWrapper;

	private EtlDatabaseObjectConfiguration pojoble;

	public static String aspasAbrir = "\"";

	public static String aspasFechar = "\"";

	private AttDefinedElements(String dbAttName, String dbAttType, boolean isLast,
			EtlDatabaseObjectConfiguration pojoble, boolean usesFieldWrapper) {
		this.dbAttName = dbAttName;
		this.dbAttType = dbAttType;
		this.isLast = isLast;
		this.pojoble = pojoble;
		this.usesFieldWrapper = usesFieldWrapper;

		Key key = new Key(dbAttName);

		if (this.pojoble.getPrimaryKey() != null) {
			this.isPartOfObjectId = this.pojoble.getPrimaryKey().containsKey(key);
		}
	}

	public String getAliasedDbAttName() {
		return aliasedDbAttName;
	}

	public void setAliasedDbAttName(String aliasedDbAttName) {
		this.aliasedDbAttName = aliasedDbAttName;
	}

	public boolean isLast() {
		return isLast;
	}

	public boolean isPartOfObjectId() {
		return isPartOfObjectId;
	}

	public String getAttDefinition() {
		return attDefinition;
	}

	public void setAttDefinigtion(String attDefinigtion) {
		this.attDefinition = attDefinigtion;
	}

	public String getSetterDefinition() {
		return setterDefinition;
	}

	public void setSetterDefinition(String setterDefinition) {
		this.setterDefinition = setterDefinition;
	}

	public String getGetterDefinition() {
		return getterDefinition;
	}

	public void setGetterDefinition(String getterDefinition) {
		this.getterDefinition = getterDefinition;
	}

	public String getSqlInsertFirstPartDefinition() {
		return sqlInsertFirstPartDefinition;
	}

	public void setSqlInsertFirstPartDefinition(String sqlInsertFirstPartDefinition) {
		this.sqlInsertFirstPartDefinition = sqlInsertFirstPartDefinition;
	}

	public String getSqlInsertLastEndPartDefinition() {
		return sqlInsertLastEndPartDefinition;
	}

	public void setSqlInsertLastEndPartDefinition(String sqlInsertLastEndPartDefinition) {
		this.sqlInsertLastEndPartDefinition = sqlInsertLastEndPartDefinition;
	}

	public String getSqlUpdateDefinition() {
		return sqlUpdateDefinition;
	}

	public String getSqlUpdateDefinition(EtlDatabaseObject obj) {
		Object value = null;

		try {
			value = obj.getFieldValue(this.dbAttName);
		} catch (ForbiddenOperationException e) {
			value = obj.getFieldValue(this.attName);
		}

		if (value == null) {
			value = "null";
		} else if (isNumeric()) {
			value = value.toString();
		} else if (isDate()) {
			value = aspasAbrir + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((Date) value) + aspasFechar;
		} else if (isString()) {
			value = aspasAbrir + utilities.scapeQuotationMarks(value.toString()) + aspasFechar;
		} else {
			value = aspasAbrir + value.toString() + aspasFechar;
		}

		return sqlUpdateDefinition.replaceAll("\\?", value.toString());
	}

	public void setSqlUpdateDefinition(String sqlUpdateDefinition) {
		this.sqlUpdateDefinition = sqlUpdateDefinition;
	}

	public String getSqlInsertParamDefinifion() {
		return sqlInsertParamDefinifion;
	}

	public void setSqlInsertParamDefinifion(String sqlInsertParamDefinifion) {
		this.sqlInsertParamDefinifion = sqlInsertParamDefinifion;
	}

	public String getSqlUpdateParamDefinifion() {
		return sqlUpdateParamDefinifion;
	}

	public void setSqlUpdateParamDefinifion(String sqlUpdateParamDefinifion) {
		this.sqlUpdateParamDefinifion = sqlUpdateParamDefinifion;
	}

	public String getAttName() {
		return attName;
	}

	public void setAttName(String attName) {
		this.attName = attName;
	}

	public String getAttType() {
		return attType;
	}

	public void setAttType(String attType) {
		this.attType = attType;
	}

	public String getResultSetLoadDefinition() {
		return resultSetLoadDefinition;
	}

	private void generateElemets(boolean useAliasedDbAttName) {
		try {
			this.attType = convertDatabaseTypeTOJavaType(this.dbAttName, dbAttType);
		} catch (Exception e) {
			this.attType = "String";
		}

		this.attName = convertTableAttNameToClassAttName(dbAttName);

		if (useAliasedDbAttName) {
			this.aliasedDbAttName = defineAliasedDbAttName(dbAttName);
		}

		this.attDefinition = usesFieldWrapper ? defineAtt(attName, dbAttName, dbAttType) : defineAtt(attName, attType);
		this.setterDefinition = usesFieldWrapper ? defineSetterMethod(attName, attType)
				: defineLegacySetterMethod(attName, attType);
		this.getterDefinition = usesFieldWrapper ? defineGetterMethod(attName)
				: defineLegacyGetterMethod(attName, attType);
		this.resultSetLoadDefinition = defineResultSetLoadDefinition(useAliasedDbAttName);

		String aspasAbrir = "\"\\\"\"+";
		String aspasFechar = "+\"\\\"\"";

		this.sqlInsertFirstPartDefinition = "`" + dbAttName + "`" + (isLast ? "" : ", ");
		this.sqlInsertLastEndPartDefinition = "?" + (isLast ? "" : ", ");
		this.sqlUpdateDefinition = "`" + dbAttName + "`" + " = ?" + (isLast ? "" : ", ");

		String valueExpression = "this." + attName + (usesFieldWrapper ? ".getValue()" : "");
		this.sqlInsertParamDefinifion = valueExpression + (isLast ? "" : ", ");
		this.sqlUpdateParamDefinifion = valueExpression + (isLast ? "" : ", ");

		if (isNumeric()) {
			this.sqlInsertValues = valueExpression;
		} else if (isDate()) {
			this.sqlInsertValues = valueExpression + " != null ? " + aspasAbrir
					+ " DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((java.util.Date) " + valueExpression + ")  "
					+ aspasFechar + " : null";
		} else if (isString()) {
			this.sqlInsertValues = valueExpression + " != null ? " + aspasAbrir + " utilities.scapeQuotationMarks("
					+ valueExpression + ".toString())  " + aspasFechar + " : null";
		} else {
			this.sqlInsertValues = valueExpression + " != null ? " + aspasAbrir + valueExpression + aspasFechar
					+ " : null";
		}

		this.sqlInsertValues = "(" + this.sqlInsertValues + (isLast ? ")" : ") + \",\" + ");
	}

	private String defineAliasedDbAttName(String dbAttName) {
		return "utilities.concatStringsWithSeparator(this.getRelatedConfiguration().getAlias(), \"" + dbAttName
				+ "\", \"_\" )";
	}

	public String defineSqlInsertValue(EtlDatabaseObject obj) {
		String sqlInsertValues = "";

		Object value = null;

		try {
			value = obj.getFieldValue(this.dbAttName);
		} catch (ForbiddenOperationException e) {
			value = obj.getFieldValue(this.attName);
		}

		if (value == null) {
			sqlInsertValues = "null";
		} else if (isNumeric()) {
			sqlInsertValues = value.toString();
		} else if (isDate()) {
			sqlInsertValues = aspasAbrir + DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((Date) value) + aspasFechar;
		} else if (isString()) {
			sqlInsertValues = aspasAbrir + utilities.scapeQuotationMarks(value.toString()) + aspasFechar;
		} else {
			sqlInsertValues = aspasAbrir + value.toString() + aspasFechar;
		}

		sqlInsertValues = "(" + sqlInsertValues + (this.isLast ? ")" : ")" + ",");

		return sqlInsertValues;
	}

	public static String removeStrangeCharactersOnString(String str) {
		if (!utilities.stringHasValue(str))
			return str;

		return utilities.removeCharactersOnString(str, "\\\\");
	}

	public String getSqlInsertValues() {
		return sqlInsertValues;
	}

	private String defineResultSetLoadDefinition(boolean useAlias) {

		String attDefinition = utilities.parseToCamelCase(this.dbAttName) + "AttName";

		String loadStr = "String " + attDefinition + " = "
				+ (useAlias ? this.aliasedDbAttName : "\"" + this.dbAttName + "\"") + ";\n\n";

		if (usesFieldWrapper) {
			loadStr += "\t\tthis." + this.attName + ".setValue(BaseVO.retrieveFieldValue(" + attDefinition + ", \""
					+ removeStrangeCharactersOnString(this.dbAttType) + "\", rs));";
		} else if (attType.equals("Integer") || attType.toLowerCase().equals("int")) {
			loadStr += "		if (rs.getObject(" + attDefinition + ") != null){ \n";
			loadStr += "			this." + this.attName + " = rs.getInt(" + attDefinition + ");\n";
			loadStr += "		}";
		} else if (attType.toLowerCase().equals("double")) {
			loadStr += "		if (rs.getObject(" + attDefinition + ") != null)\n";
			loadStr += "			this." + this.attName + " = rs.getDouble(" + attDefinition + ");\n";
			loadStr += "		}";
		} else if (attType.toLowerCase().equals("long")) {
			loadStr += "		if (rs.getObject(" + attDefinition + ") != null){\n ";
			loadStr += "			this." + this.attName + " = rs.getLong(" + attDefinition + ");\n";
			loadStr += "		}";
		} else if (attType.toLowerCase().equals("float")) {
			loadStr += "		if (rs.getObject(" + attDefinition + ") != null) \n";
			loadStr += "			this." + this.attName + " = rs.getFloat(" + attDefinition + ");\n";
			loadStr += "		}";
		} else if (attType.toLowerCase().equals("boolean")) {
			loadStr += "		this." + this.attName + " = rs.getBoolean(" + attDefinition + ");";
		} else if (attType.equals("String")) {
			loadStr += "		this." + this.attName
					+ " = AttDefinedElements.removeStrangeCharactersOnString(rs.getString(" + attDefinition
					+ ") != null ? rs.getString(" + attDefinition + ").trim() : null);";
		} else if (attType.equals("java.util.Date")) {
			loadStr += "		this." + this.attName + " =  rs.getTimestamp(" + attDefinition
					+ ") != null ? new java.util.Date( rs.getTimestamp(" + attDefinition + ").getTime() ) : null;";
		} else if (attType.equals("java.io.InputStream")) {
			loadStr += "	this." + this.attName + " = rs.getBlob(" + attDefinition + ") != null ? rs.getBlob("
					+ attDefinition + ").getBinaryStream() : null;";
		} else if (attType.toLowerCase().equals("byte")) {
			loadStr += "		this." + this.attName + " = rs.getByte(" + attDefinition + ");";
		} else if (attType.toLowerCase().equals("short")) {
			loadStr += "if (rs.getObject(" + attDefinition + ") != null)\n";
			loadStr += "		this." + this.attName + " = rs.getShort(" + attDefinition + ");\n";
			loadStr += "}";
		} else if (attType.equals("byte[]")) {
			loadStr += "		this." + this.attName + " = rs.getBytes(" + attDefinition + ");";
		} else {
			loadStr += "		this." + this.attName + " = rs.getObject(" + attDefinition + ");";
		}

		return loadStr;
	}

	public static String defineSqlAtribuitionString(String attName, Object attValue) {
		String sqlAtribuitionString = "";

		if (attValue instanceof String && ((String) attValue).startsWith("@")) {
			sqlAtribuitionString = attName + " = " + attValue;
		} else if (utilities.isNumeric(attValue.toString())) {
			sqlAtribuitionString = attName + " = " + attValue;
		} else if (attValue instanceof Date) {
			sqlAtribuitionString = attName + " = " + aspasAbrir
					+ DateAndTimeUtilities.formatToYYYYMMDD_HHMISS((Date) attValue) + aspasFechar;
		} else if (attValue instanceof String) {
			sqlAtribuitionString = attName + " = " + aspasAbrir + utilities.scapeQuotationMarks(attValue.toString())
					+ aspasFechar;
		} else {
			sqlAtribuitionString = attName + " = " + aspasAbrir + attName + aspasFechar;
		}

		return sqlAtribuitionString;
	}

	private boolean isDate() {
		return utilities.isStringIn(this.attType, "java.util.Date", "Date");
	}

	private boolean isString() {
		return utilities.isStringIn(this.attType, "java.lang.String", "String");
	}

	private boolean isNumeric() {
		return utilities.isStringIn(this.attType.toLowerCase(), "int", "integer", "long", "byte", "short", "double",
				"float");
	}

	public static boolean isNumeric(String attType) {
		return utilities.isStringIn(attType.toLowerCase(), "int", "integer", "long", "byte", "short", "double", "float",
				"tinyint", "bigint", "bigint unsigned", "int unsigned");
	}

	public static boolean isString(String attType) {
		return utilities.isStringIn(attType.toLowerCase(), "java.lang.string", "string", "varchar", "char", "text",
				"mediumtext", "longtext");
	}

	public static AttDefinedElements define(String dbAttName, String dbAttType, boolean isLast,
			EtlDatabaseObjectConfiguration pojoble) {
		return define(dbAttName, dbAttType, isLast, pojoble, true);
	}

	public static AttDefinedElements define(String dbAttName, String dbAttType, boolean isLast,
			EtlDatabaseObjectConfiguration pojoble, boolean usesFieldWrapper) {

		AttDefinedElements elements = new AttDefinedElements(dbAttName, dbAttType, isLast, pojoble, usesFieldWrapper);
		elements.generateElemets(true);

		return elements;
	}

	public static String defineAtt(String attName, String dbAttName, String dbAttType) {
		return "	private Field " + attName + " = Field.fastCreateWithType(\""
				+ removeStrangeCharactersOnString(dbAttName) + "\", \"" + removeStrangeCharactersOnString(dbAttType)
				+ "\");";
	}

	public static String defineAtt(String attName, String attType) {
		return "	private " + attType + " " + attName + ";";
	}

	public static String defineGetterMethod(String attName) {
		String cAttName = attName.toUpperCase().charAt(0) + attName.substring(1);

		return "	public Field get" + cAttName + "(){ \n" + "		return this." + attName + ";\n" + "	}";

	}

	public static String defineSetterMethod(String attName, String attType) {
		String cAttName = attName.toUpperCase().charAt(0) + attName.substring(1);

		return "	public void set" + cAttName + "(Field " + attName + "){ \n" + "	 	this." + attName + " = "
				+ attName + ";\n" + "	}\n\n" + "	public void set" + cAttName + "Value(" + attType
				+ " value){ \n		this." + attName + ".setValue(value);\n	}";
	}

	private static String defineLegacyGetterMethod(String attName, String attType) {
		String cAttName = attName.toUpperCase().charAt(0) + attName.substring(1);
		return "	public " + attType + " get" + cAttName + "(){ \n" + "		return this." + attName + ";\n" + "	}";
	}

	private static String defineLegacySetterMethod(String attName, String attType) {
		String cAttName = attName.toUpperCase().charAt(0) + attName.substring(1);
		return "	public void set" + cAttName + "(" + attType + " " + attName + "){ \n" + "	 	this." + attName
				+ " = " + attName + ";\n" + "	}";
	}

	public static String defineDefaultGetterMethod(String attName, String attType) {
		String cAttName = attName.toUpperCase().charAt(0) + attName.substring(1);

		if (isNumeric(attType))
			return "	public " + attType + " get" + cAttName + "(){ \n" + "		return 0;\n" + "	}";

		return "	public " + attType + " get" + cAttName + "(){ \n" + "		return null;\n" + "	}";

	}

	public static String defineDefaultSetterMethod(String attName, String attType) {
		String cAttName = attName.toUpperCase().charAt(0) + attName.substring(1);

		return "	public void set" + cAttName + "(" + attType + " " + attName + "){ }";
	}

	public static String convertTableAttNameToClassAttName(String tableAttName) {
		return utilities.convertTableAttNameToClassAttName(tableAttName);
	}

	public static String convertDatabaseTypeTOJavaType(String fieldName, String databaseType) {
		databaseType = databaseType.toUpperCase();

		/*
		 * NOTE: Temporary Convert INT8 and SERIAL as Integer as Postgres use INT8 for
		 * serial columns (PK) Which is INT8. note that this type should be converted to
		 * LONG but as if the core of Epts-Etl use Integer for PK for now we are forcing
		 * INT8 to be Integer
		 */
		if (utilities.isStringIn(databaseType, "INT", "MEDIUMINT", "INT8", "BIGINT", "SERIAL", "SERIAL4",
				"INT UNSIGNED"))
			return "Integer";
		if (utilities.isStringIn(databaseType, "TINYINT"))
			return "Byte";
		if (utilities.isStringIn(databaseType, "BIT"))
			return "Boolean";
		if (utilities.isStringIn(databaseType, "YEAR", "SMALLINT", "SMALLINT UNSIGNED"))
			return "Short";
		if (utilities.isStringIn(databaseType, "BIGINT", "INT8", "SERIAL", "BIGINT UNSIGNED"))
			return "Long";
		if (utilities.isStringIn(databaseType, "DECIMAL", "NUMERIC", "REAL", "DOUBLE"))
			return "Double";
		if (utilities.isStringIn(databaseType, "FLOAT", "NUMERIC"))
			return "Float";
		if (utilities.isStringIn(databaseType, "VARCHAR", "CHAR", "TEXT", "MEDIUMTEXT", "LONGTEXT"))
			return "String";
		if (utilities.isStringIn(databaseType, "MEDIUMBLOB", "VARBINARY", "BLOB", "LONGBLOB"))
			return "byte[]";
		if (utilities.isStringIn(databaseType, "DATE", "DATETIME", "TIME", "TIMESTAMP"))
			return "java.util.Date";

		throw new ForbiddenOperationException("Unknown data type for field " + fieldName + " [" + databaseType + "]");
	}

	public static boolean isClob(String type) {
		return utilities.isStringIn(type, "java.io.InputStream", "[B", "MEDIUMBLOB", "VARBINARY", "BLOB", "LONGBLOB");
	}

	public static boolean isDateType(String type) {
		boolean isDatabaseDateType = utilities.isStringIn(type.toUpperCase(), "DATE", "DATETIME", "TIME", "TIMESTAMP");
		boolean isJavaDateType = utilities.isStringIn(type.toUpperCase(), "java.util.Date");

		return isDatabaseDateType || isJavaDateType;
	}

	public static boolean isBooleanType(String type) {
		return utilities.isStringIn(type.toLowerCase(), "boolean", "java.lang.Boolean", "bit");
	}

	public static String[] convertTableAttNameToClassAttName(String[] dbAtts) {
		List<String> atts = new ArrayList<String>();

		for (String att : dbAtts) {
			atts.add(convertTableAttNameToClassAttName(att));
		}

		return utilities.parseListToArray(atts);
	}

	public static boolean isPrimitive(String type) {
		return isNumeric(type) || utilities.isStringIn(type, "char", "boolean");
	}

	public static boolean isSmallInt(String type) {
		return utilities.isStringIn(type.toLowerCase(), "tinyint");
	}

	public static boolean isInteger(String type) {
		return utilities.isStringIn(type.toLowerCase(), "int", "integer", "java.lang.integer");
	}

	public static boolean isLong(String type) {
		return utilities.isStringIn(type.toLowerCase(), "long", "java.lang.long");
	}

	public static boolean isDecimal(String type) {
		return utilities.isStringIn(type.toUpperCase(), "JAVA.LANG.DOUBLE", "FLOAT", "JAVA.LANG.FLOAT", "DECIMAL",
				"NUMERIC", "REAL", "DOUBLE");
	}

	public String generateCopyToOtherCommand(String otheObjectIdentifier) {
		return otheObjectIdentifier + "." + this.getAttName() + " = "
				+ (usesFieldWrapper ? "copyGeneratedField(this." + this.getAttName() + ")"
						: "this." + this.getAttName())
				+ ";";
	}

	public String generateCopyToThisCommand(String toCopyFromObjectIdentifier) {
		return "this." + this.getAttName() + " = "
				+ (usesFieldWrapper ? "copyGeneratedField(" + toCopyFromObjectIdentifier + "." + this.getAttName() + ")"
						: toCopyFromObjectIdentifier + "." + this.getAttName())
				+ ";";
	}

}
