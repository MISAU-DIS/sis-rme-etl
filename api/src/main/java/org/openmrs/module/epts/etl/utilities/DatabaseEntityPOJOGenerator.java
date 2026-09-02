
package org.openmrs.module.epts.etl.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.RefMapping;
import org.openmrs.module.epts.etl.conf.interfaces.JoinableEntity;
import org.openmrs.module.epts.etl.conf.interfaces.MainJoiningEntity;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

public class DatabaseEntityPOJOGenerator {

	private static final CommonUtilities utilities = CommonUtilities.getInstance();

	private static final String[] IGNORABLE_FIELDS = { "date_changed", "date_created", "date_voided", "uuid" };

	private static final ThreadLocal<Set<String>> DEPENDENCIES_BEING_GENERATED = ThreadLocal.withInitial(HashSet::new);

	private DatabaseEntityPOJOGenerator() {
		// Utility class.
	}

	public static Class<EtlDatabaseObject> generate(EtlDatabaseObjectConfiguration pojoble, DBConnectionInfo connInfo)
			throws IOException, SQLException, ClassNotFoundException {

		if (!pojoble.isFullLoaded())
			pojoble.fullLoad();

		String className = pojoble.generateClassName();

		String pojoRootFolder = pojoble.getPOJOSourceFilesDirectory().getAbsolutePath();

		pojoRootFolder += "/org/openmrs/module/epts/etl/model/pojo/";

		File sourceFile = new File(
				pojoRootFolder + pojoble.getClassPackageForForder(connInfo) + "/" + className + ".java");

		String fullClassName = pojoble.generateFullClassName(connInfo);

		Class<EtlDatabaseObject> existingCLass = shouldOverrideExistingDataModelElement(pojoble) ? null
				: tryToGetExistingCLass(fullClassName, pojoble.getRelatedEtlConf());

		if (existingCLass != null && !shouldOverrideExistingDataModelElement(pojoble)) {
			return existingCLass;
		}

		generateCompileTimeDependencies(pojoble, connInfo, fullClassName);

		String attsDefinition = "";

		String gettersAndSetterDefinition = "";
		String resultSetLoadDefinition = "";

		String insertSQLFieldsWithoutObjectId = "";
		String insertSQLFieldsWithObjectId = "";

		String insertSQLQuestionMarksWithoutObjectId = "";
		String insertSQLQuestionMarksWithObjectId = "";

		String updateSQLDefinition = "UPDATE " + pojoble.getObjectName() + " SET ";

		String insertParamsWithoutObjectId = "";
		String insertParamsWithObjectId = "";

		String updateParamsDefinition = "Object[] params = {";

		String insertValuesWithoutObjectIdDefinition = "";
		String insertValuesWithObjectIdDefinition = "";

		AttDefinedElements attElements;

		int qtyAttrs = pojoble.getFields().size();

		for (int i = 0; i < qtyAttrs - 1; i++) {
			Field field = pojoble.getFields().get(i);

			attElements = AttDefinedElements.define(field.getName(), field.getDataType(), false, pojoble,
					!isIgnorableField(field.getName()));

			if (!isIgnorableField(field.getName())) {
				attsDefinition = utilities.concatStringsWithSeparator(attsDefinition, attElements.getAttDefinition(),
						"\n");
				gettersAndSetterDefinition = utilities.concatStrings(gettersAndSetterDefinition,
						attElements.getSetterDefinition());

				gettersAndSetterDefinition += "\n \n";
				gettersAndSetterDefinition = utilities.concatStrings(gettersAndSetterDefinition,
						attElements.getGetterDefinition());

				gettersAndSetterDefinition += "\n \n";
			}

			if (!attElements.isPartOfObjectId()) {
				insertSQLFieldsWithoutObjectId = utilities.concatStrings(insertSQLFieldsWithoutObjectId,
						attElements.getSqlInsertFirstPartDefinition());

				insertSQLQuestionMarksWithoutObjectId = utilities.concatStrings(insertSQLQuestionMarksWithoutObjectId,
						attElements.getSqlInsertLastEndPartDefinition());

				insertValuesWithoutObjectIdDefinition = utilities.concatStrings(insertValuesWithoutObjectIdDefinition,
						attElements.getSqlInsertValues());

				insertParamsWithoutObjectId = utilities.concatStrings(insertParamsWithoutObjectId,
						attElements.getSqlInsertParamDefinifion());
			}

			insertSQLFieldsWithObjectId = utilities.concatStrings(insertSQLFieldsWithObjectId,
					attElements.getSqlInsertFirstPartDefinition());

			insertSQLQuestionMarksWithObjectId = utilities.concatStrings(insertSQLQuestionMarksWithObjectId,
					attElements.getSqlInsertLastEndPartDefinition());

			insertValuesWithObjectIdDefinition = utilities.concatStrings(insertValuesWithObjectIdDefinition,
					attElements.getSqlInsertValues());

			insertParamsWithObjectId = utilities.concatStrings(insertParamsWithObjectId,
					attElements.getSqlInsertParamDefinifion());

			updateSQLDefinition = utilities.concatStrings(updateSQLDefinition, attElements.getSqlUpdateDefinition());

			updateParamsDefinition = utilities.concatStrings(updateParamsDefinition,
					attElements.getSqlUpdateParamDefinifion());

			resultSetLoadDefinition = utilities.concatStrings(resultSetLoadDefinition,
					"		" + attElements.getResultSetLoadDefinition());

			resultSetLoadDefinition += "\n\n";
		}

		Field field = pojoble.getFields().get(qtyAttrs - 1);

		attElements = AttDefinedElements.define(field.getName(), field.getDataType(), true, pojoble,
				!isIgnorableField(field.getName()));

		if (!isIgnorableField(field.getName())) {
			attsDefinition = utilities.concatStringsWithSeparator(attsDefinition, attElements.getAttDefinition(), "\n");
			gettersAndSetterDefinition = utilities.concatStrings(gettersAndSetterDefinition,
					attElements.getSetterDefinition());

			gettersAndSetterDefinition += "\n\n";

			gettersAndSetterDefinition += "\n \n";
			gettersAndSetterDefinition = utilities.concatStrings(gettersAndSetterDefinition,
					attElements.getGetterDefinition());
		}

		// insertValuesWithoutObjectIdDefinition

		updateSQLDefinition += attElements.getSqlUpdateDefinition();

		updateParamsDefinition += attElements.getSqlUpdateParamDefinifion();

		resultSetLoadDefinition += attElements.getResultSetLoadDefinition();
		resultSetLoadDefinition += "\n";

		if (!attElements.isPartOfObjectId()) {
			insertParamsWithoutObjectId += attElements.getSqlInsertParamDefinifion();

			insertSQLFieldsWithoutObjectId = utilities.concatStrings(insertSQLFieldsWithoutObjectId,
					attElements.getSqlInsertFirstPartDefinition());
			insertSQLQuestionMarksWithoutObjectId = utilities.concatStrings(insertSQLQuestionMarksWithoutObjectId,
					attElements.getSqlInsertLastEndPartDefinition());

			insertValuesWithoutObjectIdDefinition += attElements.getSqlInsertValues();
		}

		insertParamsWithObjectId += attElements.getSqlInsertParamDefinifion();

		insertSQLFieldsWithObjectId = utilities.concatStrings(insertSQLFieldsWithObjectId,
				attElements.getSqlInsertFirstPartDefinition());

		insertSQLQuestionMarksWithObjectId = utilities.concatStrings(insertSQLQuestionMarksWithObjectId,
				attElements.getSqlInsertLastEndPartDefinition());

		insertValuesWithObjectIdDefinition += attElements.getSqlInsertValues();

		if (pojoble.getPrimaryKey() != null) {
			updateSQLDefinition += " WHERE " + pojoble.getPrimaryKey().parseToParametrizedStringConditionWithoutAlias();

			for (Key key : pojoble.getPrimaryKey().getFields()) {
				updateParamsDefinition += ", this." + key.getNameAsClassAtt() + ".getValue()";
			}

			updateParamsDefinition += "};";

		}

		String insertSQLDefinitionWithoutObjectId = "INSERT INTO " + pojoble.getObjectName() + "("
				+ insertSQLFieldsWithoutObjectId + ") VALUES( " + insertSQLQuestionMarksWithoutObjectId + ");";

		String insertSQLDefinitionWithObjectId = "INSERT INTO " + pojoble.getObjectName() + "("
				+ insertSQLFieldsWithObjectId + ") VALUES( " + insertSQLQuestionMarksWithObjectId + ");";

		String insertParamsWithoutObjectIdDefinition = "Object[] params = {" + insertParamsWithoutObjectId + "};";

		String insertParamsWithObjectIdDefinition = "Object[] params = {" + insertParamsWithObjectId + "};";

		String methodFromSuperClass = "";

		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public void load(ResultSet rs) throws SQLException{ \n";
		methodFromSuperClass += "		super.load(rs);\n \n";
		methodFromSuperClass += generateSharedPkLoad(pojoble);
		methodFromSuperClass += resultSetLoadDefinition;
		methodFromSuperClass += generateSharedPkPostLoad(pojoble);
		methodFromSuperClass += generateAuxLoadObjects(pojoble, connInfo);
		methodFromSuperClass += "\t\tthis.loadedFromDb = true;\n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public String getInsertSQLWithoutObjectId(){ \n ";
		methodFromSuperClass += "		return \"" + insertSQLDefinitionWithoutObjectId + "\"; \n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public String getInsertSQLWithObjectId(){ \n ";
		methodFromSuperClass += "		return \"" + insertSQLDefinitionWithObjectId + "\"; \n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public Object[]  getInsertParamsWithoutObjectId(){ \n ";
		methodFromSuperClass += "		" + insertParamsWithoutObjectIdDefinition + "\n";
		methodFromSuperClass += "		return params; \n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public Object[]  getInsertParamsWithObjectId(){ \n ";
		methodFromSuperClass += "		" + insertParamsWithObjectIdDefinition + "\n";
		methodFromSuperClass += "		return params; \n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public String getInsertSQLQuestionMarksWithoutObjectId(){ \n ";
		methodFromSuperClass += "		return \"" + insertSQLQuestionMarksWithoutObjectId + "\";\n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public String getInsertSQLQuestionMarksWithObjectId(){ \n ";
		methodFromSuperClass += "		return \"" + insertSQLQuestionMarksWithObjectId + "\"; \n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public Object[]  getUpdateParams(){ \n ";

		if (pojoble.getPrimaryKey() != null) {
			methodFromSuperClass += "		" + updateParamsDefinition + "\n";
			methodFromSuperClass += "		return params; \n";
		} else {
			methodFromSuperClass += "		throw new RuntimeException(\"Impossible auto update command! No primary key is defined for table object!\");";
		}

		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public String getUpdateSQL(){ \n ";

		if (pojoble.getPrimaryKey() != null) {
			methodFromSuperClass += "		return \"" + updateSQLDefinition + "\"; \n";
		} else {
			methodFromSuperClass += "		throw new RuntimeException(\"Impossible auto update command! No primary key is defined for table object!\");";
		}
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public String generateInsertValuesWithoutObjectId(){ \n ";
		methodFromSuperClass += "		return \"\"+" + insertValuesWithoutObjectIdDefinition + "; \n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public String generateInsertValuesWithObjectId(){ \n ";
		methodFromSuperClass += "		return \"\"+" + insertValuesWithObjectIdDefinition + "; \n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@JsonIgnore\n";
		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public EtlDatabaseObject createACopy(){ \n ";
		methodFromSuperClass += "		" + className + " copy = new " + className + "();\n";
		methodFromSuperClass += "		copy.setRelatedConfiguration(getRelatedConfiguration());\n";
		methodFromSuperClass += "		if (getSharedPkObj() != null && copy.getSharedPkObj() != null) {\n";
		methodFromSuperClass += "			copy.getSharedPkObj().setRelatedConfiguration(getSharedPkObj().getRelatedConfiguration());\n";
		methodFromSuperClass += "		}\n";
		methodFromSuperClass += "		copy.copyFrom(this);\n";
		methodFromSuperClass += "		return copy; \n";
		methodFromSuperClass += "	} \n \n";

		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public boolean hasParents() {\n";

		if (utilities.listHasElement(pojoble.getParentRefInfo())) {
			for (ParentTable refInfo : pojoble.getParentRefInfo()) {

				for (RefMapping map : refInfo.getRefMapping()) {

					methodFromSuperClass += "		if (this." + map.getChildFieldNameAsAttClass()
							+ ".getValue() != null) return true;\n\n";
				}
			}
		}

		methodFromSuperClass += "		return false;\n";

		methodFromSuperClass += "	}\n\n";

		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public Object getParentValue(String parentAttName) {";

		if (utilities.listHasElement(pojoble.getParentRefInfo())) {
			for (ParentTable refInfo : pojoble.getParentRefInfo()) {
				methodFromSuperClass += "		\n		if (parentAttName.equals(\""
						+ refInfo.getChildColumnAsClassAttOnSimpleMapping() + "\")) return this."
						+ refInfo.getChildColumnAsClassAttOnSimpleMapping() + ".getValue();";
			}
		}

		methodFromSuperClass += "\n\n";

		methodFromSuperClass += "		throw new RuntimeException(\"No found parent for: \" + parentAttName);\n";

		methodFromSuperClass += "	}\n\n";

		methodFromSuperClass += "	@Override\n";
		methodFromSuperClass += "	public String generateTableName() {\n";
		methodFromSuperClass += "		return " + utilities.quote(pojoble.getObjectName()) + ";\n";
		methodFromSuperClass += "	}\n\n";

		String classDefinition = "package " + pojoble.generateFullPackageName(connInfo) + ";\n\n";

		classDefinition += "import org.openmrs.module.epts.etl.model.pojo.generic.*; \n \n";
		classDefinition += "import org.openmrs.module.epts.etl.model.EtlDatabaseObject; \n \n";
		classDefinition += "import org.openmrs.module.epts.etl.model.Field; \n \n";
		classDefinition += "import org.openmrs.module.epts.etl.conf.Key; \n \n";
		classDefinition += "import org.openmrs.module.epts.etl.model.base.BaseVO; \n \n";

		if (pojoble.hasDateFields()) {
			classDefinition += "import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities; \n \n";
		}

		if (methodFromSuperClass.contains("removeStrangeCharactersOnString")) {
			classDefinition += "import org.openmrs.module.epts.etl.utilities.AttDefinedElements; \n \n";
		}

		classDefinition += "import java.sql.SQLException; \n";
		classDefinition += "import java.sql.ResultSet; \n \n";
		classDefinition += "import java.sql.Connection; \n \n";
		classDefinition += "import com.fasterxml.jackson.annotation.JsonIgnore; \n \n";

		classDefinition += "public class " + className + " extends AbstractGeneratedDatabaseObject{ \n";
		classDefinition += attsDefinition + "\n \n";
		classDefinition += generateCommonMethods(pojoble, connInfo) + "\n";
		classDefinition += gettersAndSetterDefinition + "\n \n";
		classDefinition += methodFromSuperClass + "\n";

		classDefinition += "}";

		writeSourceFile(sourceFile, classDefinition, pojoble.getRelatedEtlConf());

		compile(sourceFile, pojoble, connInfo);
		pojoble.getRelatedEtlConf().refreshDataModelClassLoader();

		existingCLass = tryToGetExistingCLass(fullClassName, pojoble.getRelatedEtlConf());

		if (existingCLass == null) {
			throw new EtlExceptionImpl("The class for " + pojoble.getObjectName() + " was not created!") {

				private static final long serialVersionUID = 1L;
			};
		}

		return existingCLass;
	}

	private static String generateCommonMethods(EtlDatabaseObjectConfiguration pojoble, DBConnectionInfo connInfo) {
		String className = pojoble.generateClassName();

		String commonMethods = "";

		commonMethods += "	public " + className + "() { \n";
		commonMethods += "		this.metadata = " + pojoble.isMetadata() + ";\n";
		for (Field field : pojoble.getFields()) {
			if (!isIgnorableField(field.getName())) {
				commonMethods += "		this.fields.add(this." + field.getNameAsClassAtt() + ");\n";
			}
		}
		if (usesSharedPk(pojoble)) {
			ParentTable shared = resolveSharedPkConfiguration((TableConfiguration) pojoble);
			commonMethods += "\t\tsetSharedPkObj(new " + shared.generateFullClassName(connInfo) + "());\n";
		}
		commonMethods += "	} \n \n";

		commonMethods += "	@Override\n";
		commonMethods += "	public void tryToReplaceFieldValueWithKeyValue(Key k) {\n";
		if (pojoble.getPrimaryKey() != null) {
			for (Key key : pojoble.getPrimaryKey().getFields()) {
				commonMethods += "		if (utilities.equalsFieldsName(k.getName(), \"" + key.getName() + "\")) {\n";
				if (isIgnorableField(key.getName())) {
					commonMethods += generateInheritedKeyAssignment(key);
				} else {
					commonMethods += "			this." + key.getNameAsClassAtt() + ".setValue(k.getValue());\n";
				}
				commonMethods += "		}\n";
			}
		}
		commonMethods += "	}\n\n";

		commonMethods += "	@Override\n";
		commonMethods += "	public Object getFieldValue(String fieldName) {\n";
		for (Field field : pojoble.getFields()) {
			if (!isIgnorableField(field.getName())) {
				commonMethods += "		if (utilities.equalsFieldsName(fieldName, \"" + field.getName() + "\")) {\n";
				commonMethods += "			return this." + field.getNameAsClassAtt() + ".getValue();\n";
				commonMethods += "		}\n";
			}
		}
		commonMethods += generateInheritedFieldValueAccess();
		commonMethods += "		return super.getFieldValue(fieldName);\n";
		commonMethods += "	}\n\n";

		if (usesSharedPk(pojoble)) {
			ParentTable shared = resolveSharedPkConfiguration((TableConfiguration) pojoble);
			String sharedClass = shared.generateFullClassName(connInfo);
			commonMethods += "\t@JsonIgnore\n";
			commonMethods += "\t@Override\n";
			commonMethods += "\tpublic " + sharedClass + " getSharedPkObj() {\n";
			commonMethods += "\t\treturn (" + sharedClass + ") super.getSharedPkObj();\n";
			commonMethods += "\t}\n\n";
		}

		commonMethods += "	@JsonIgnore\n";
		commonMethods += "	@Override\n";
		commonMethods += "	public String generateFullFilledUpdateSql(){ \n ";
		commonMethods += "		return null; \n";
		commonMethods += "	} \n \n";

		commonMethods += "	@JsonIgnore\n";
		commonMethods += "	@Override\n";
		commonMethods += "	public void setInsertSQLQuestionMarksWithObjectId(String insertQuestionMarks){ \n ";
		commonMethods += "	 \n";
		commonMethods += "	} \n \n";

		commonMethods += "	@JsonIgnore\n";
		commonMethods += "	@Override\n";
		commonMethods += "	public void setInsertSQLQuestionMarksWithoutObjectId(String insertQuestionMarks){ \n ";
		commonMethods += "	 \n";
		commonMethods += "	} \n \n";

		commonMethods += "	@JsonIgnore\n";
		commonMethods += "	@Override\n";
		commonMethods += "	public void loadWithDefaultValues(Connection srcConn, Connection dstConn){ \n ";
		commonMethods += "	 	utilities.throwForbiddenMethodException();\n";
		commonMethods += "	} \n \n";

		return commonMethods;

	}

	private static String generateInheritedKeyAssignment(Key key) {
		if (utilities.equalsFieldsName(key.getName(), "uuid")) {
			return "			this.uuid = k.getValue() == null ? null : k.getValue().toString();\n";
		}
		return "			this." + key.getNameAsClassAtt() + " = (java.util.Date) k.getValue();\n";
	}

	private static String generateInheritedFieldValueAccess() {
		String code = "";
		code += "		if (utilities.equalsFieldsName(fieldName, \"date_created\")) return this.dateCreated;\n";
		code += "		if (utilities.equalsFieldsName(fieldName, \"date_changed\")) return this.dateChanged;\n";
		code += "		if (utilities.equalsFieldsName(fieldName, \"date_voided\")) return this.dateVoided;\n";
		code += "		if (utilities.equalsFieldsName(fieldName, \"uuid\")) return this.uuid;\n";
		return code;
	}

	private static boolean usesSharedPk(EtlDatabaseObjectConfiguration configuration) {
		return configuration instanceof TableConfiguration && ((TableConfiguration) configuration).useSharedPKKey();
	}

	private static void generateCompileTimeDependencies(EtlDatabaseObjectConfiguration configuration,
			DBConnectionInfo connInfo, String currentClassName)
			throws IOException, SQLException, ClassNotFoundException {
		Set<String> resolving = DEPENDENCIES_BEING_GENERATED.get();
		if (!resolving.add(currentClassName)) {
			throw new EtlExceptionImpl("Cyclic generated POJO dependency detected at " + currentClassName);
		}
		try {
			if (usesSharedPk(configuration)) {
				generate(resolveSharedPkConfiguration((TableConfiguration) configuration), connInfo);
			}
			if (configuration instanceof MainJoiningEntity) {
				MainJoiningEntity joining = (MainJoiningEntity) configuration;
				if (joining.hasAuxExtractTable()) {
					for (JoinableEntity auxiliary : joining.getJoiningTable()) {
						if (!auxiliary.doNotUseAsDatasource())
							generate(auxiliary, connInfo);
					}
				}
			}
		} finally {
			resolving.remove(currentClassName);
			if (resolving.isEmpty())
				DEPENDENCIES_BEING_GENERATED.remove();
		}
	}

	static String generateSharedPkLoad(EtlDatabaseObjectConfiguration configuration) {
		if (!usesSharedPk(configuration))
			return "";
		String code = "\t\tif (!hasRelatedConfiguration()) throw new "
				+ "org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException("
				+ "\"The relatedConfiguration is not set\");\n";
		code += "\t\tif (!getSharedPkObj().isLoadedFromDb()) getSharedPkObj().load(rs);\n";
		return code;
	}

	static String generateSharedPkPostLoad(EtlDatabaseObjectConfiguration configuration) {
		if (!usesSharedPk(configuration))
			return "";
		String code = "\n\t\torg.openmrs.module.epts.etl.conf.interfaces.TableConfiguration tableConfiguration = "
				+ "(org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration) getRelatedConfiguration();\n";
		code += "\t\tif (!utilities.stringHasValue(getUuid()) && getSharedPkObj() != null "
				+ "&& utilities.stringHasValue(getSharedPkObj().getUuid())) {\n";
		code += "\t\t\tsetUuid(getSharedPkObj().getUuid());\n";
		code += "\t\t}\n";
		code += "\t\tloadObjectIdData(tableConfiguration);\n";
		return code;
	}

	private static ParentTable resolveSharedPkConfiguration(TableConfiguration configuration) {
		if (configuration.hasParentRefInfo()) {
			for (ParentTable parent : configuration.getParentRefInfo()) {
				if (parent.getTableName().equalsIgnoreCase(configuration.getSharePkWith()))
					return parent;
			}
		}
		throw new EtlExceptionImpl("The shared PK table " + configuration.getSharePkWith() + " of "
				+ configuration.getTableName() + " is not present in the loaded parent relationships");
	}

	private static String generateAuxLoadObjects(EtlDatabaseObjectConfiguration configuration,
			DBConnectionInfo connInfo) {
		if (!(configuration instanceof MainJoiningEntity))
			return "";
		MainJoiningEntity joining = (MainJoiningEntity) configuration;
		if (!joining.hasAuxExtractTable())
			return "";
		String code = "\n\t\tif (!hasRelatedConfiguration()) throw new "
				+ "org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException("
				+ "\"The relatedConfiguration is not set\");\n";
		code += "\t\tsetAuxLoadObject(new java.util.ArrayList<>());\n";
		int index = 0;
		for (JoinableEntity auxiliary : joining.getJoiningTable()) {
			if (!auxiliary.doNotUseAsDatasource()) {
				String auxiliaryClass = auxiliary.generateFullClassName(connInfo);
				String variable = "auxLoadObject" + index;
				code += "\t\t" + auxiliaryClass + " " + variable + " = new " + auxiliaryClass + "();\n";
				code += "\t\t" + variable
						+ ".setRelatedConfiguration(((org.openmrs.module.epts.etl.conf.interfaces.MainJoiningEntity) "
						+ "getRelatedConfiguration()).getJoiningTable().get(" + index + "));\n";
				code += "\t\t" + variable + ".load(rs);\n";
				code += "\t\tgetAuxLoadObject().add(" + variable + ");\n";
			}
			index++;
		}
		return code;
	}

	private static boolean isIgnorableField(String columnName) {

		for (String field : IGNORABLE_FIELDS) {
			if (field.equals(columnName)) {
				return true;
			}
		}

		return false;
	}

	private static boolean shouldOverrideExistingDataModelElement(EtlDatabaseObjectConfiguration configuration) {
		return configuration.getRelatedEtlConf() != null
				&& configuration.getRelatedEtlConf().shouldOverrideExistingDataModelElement();
	}

	public static Class<EtlDatabaseObject> generateSkeleton(EtlDatabaseObjectConfiguration pojoable,
			DBConnectionInfo connInfo) throws IOException, SQLException, ClassNotFoundException {
		if (!pojoable.isFullLoaded())
			pojoable.fullLoad();

		String pojoRootPackage = pojoable.getPOJOSourceFilesDirectory().getAbsolutePath();

		pojoRootPackage += pojoable.isDestinationInstallationType() ? "/org/openmrs/module/epts.etl/model/pojo/"
				: "/org/openmrs/module/epts.etl/model/pojo/source/";

		File sourceFile = new File(
				pojoRootPackage + pojoable.getClassPackage(connInfo) + "/" + pojoable.generateClassName() + ".java");

		String fullClassName = "org.openmrs.module.epts.etl.model.pojo";

		fullClassName += pojoable.isDestinationInstallationType() ? "." : fullClassName + "source.";

		fullClassName += pojoable.getClassPackage(connInfo) + "."
				+ FileUtilities.generateFileNameFromRealPathWithoutExtension(sourceFile.getName());

		Class<EtlDatabaseObject> existingCLass = shouldOverrideExistingDataModelElement(pojoable) ? null
				: tryToGetExistingCLass(fullClassName, pojoable.getRelatedEtlConf());

		if (existingCLass != null && !shouldOverrideExistingDataModelElement(pojoable))
			return existingCLass;

		String classDefinition = "package org.openmrs.module.epts.etl.model.pojo.";

		classDefinition += pojoable.isDestinationInstallationType() ? "" : "source.";

		classDefinition += pojoable.getClassPackage(connInfo) + "; \n \n";

		classDefinition += "import org.openmrs.module.epts.etl.model.pojo.generic.*; \n \n";

		classDefinition += "public abstract class " + pojoable.generateClassName()
				+ " extends AbstractGeneratedDatabaseObject implements EtlDatabaseObject { \n";
		classDefinition += "	public " + pojoable.generateClassName() + "() { \n";
		classDefinition += "	} \n \n";
		classDefinition += "}";

		writeSourceFile(sourceFile, classDefinition, pojoable.getRelatedEtlConf());

		compile(sourceFile, pojoable, connInfo);
		pojoable.getRelatedEtlConf().refreshDataModelClassLoader();

		return tryToGetExistingCLass(fullClassName, pojoable.getRelatedEtlConf());
	}

	private static void writeSourceFile(File sourceFile, String classDefinition, EtlConfiguration etlConfiguration)
			throws IOException {
		FileUtilities.tryToCreateDirectoryStructureForFile(sourceFile.getAbsolutePath());
		File formatterProfile = resolveFormatterProfile(etlConfiguration);
		String source = EclipseJavaSourceFormatter.format(classDefinition, formatterProfile);
		Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));
	}

	private static File resolveFormatterProfile(EtlConfiguration etlConfiguration) {
		if (etlConfiguration == null
				|| !utilities.stringHasValue(etlConfiguration.getDataModel().getJavaFormatterConfigurationFile()))
			return null;

		File configuredFile = new File(etlConfiguration.getDataModel().getJavaFormatterConfigurationFile());
		if (configuredFile.isAbsolute())
			return configuredFile;

		return new File(etlConfiguration.getEtlRootDirectory(), configuredFile.getPath());
	}

	@SuppressWarnings("unchecked")
	public static Class<EtlDatabaseObject> tryToGetExistingCLass(String fullClassName,
			EtlConfiguration etlConfiguration) {
		if (etlConfiguration == null)
			return tryToLoadFromOpenMRSClassLoader(fullClassName);
		try {
			return (Class<EtlDatabaseObject>) etlConfiguration.loadDataModelClass(fullClassName);
		} catch (ClassNotFoundException exception) {
			return null;
		}
	}

	public static Class<EtlDatabaseObject> tryToGetExistingCLass(String fullClassName) {
		return tryToLoadFromOpenMRSClassLoader(fullClassName);
	}

	@SuppressWarnings({ "unchecked" })
	private static Class<EtlDatabaseObject> tryToLoadFromOpenMRSClassLoader(String fullClassName) {
		try {
			return (Class<EtlDatabaseObject>) EtlDatabaseObject.class.getClassLoader().loadClass(fullClassName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private static void addAllToClassPath(List<File> classPath, File file) {
		classPath.add(file);

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					addAllToClassPath(classPath, child);
				}
			}
		}
	}

	public static void compile(File sourceFile, EtlDatabaseObjectConfiguration pojoble, DBConnectionInfo connInfo)
			throws IOException {
		File destinationFile = pojoble.getPOJOCopiledFilesDirectory();

		if (!destinationFile.exists()) {
			FileUtilities.tryToCreateDirectoryStructure(destinationFile.getAbsolutePath());
		}

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new IOException("A JDK is required to compile generated POJO classes.");
		}

		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(destinationFile));

			List<File> classPathFiles = new ArrayList<File>();

			classPathFiles.add(destinationFile);

			for (File entry : pojoble.getClassPath())
				addAllToClassPath(classPathFiles, entry);

			fileManager.setLocation(StandardLocation.CLASS_PATH, classPathFiles);

			Boolean compiled = compiler.getTask(null, fileManager, null, null, null,
					fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile))).call();
			if (!Boolean.TRUE.equals(compiled)) {
				throw new IOException("Could not compile generated POJO source: " + sourceFile.getAbsolutePath());
			}
		}

		ClassPathUtilities.addClassToClassPath(pojoble, connInfo);
	}

}
