package org.openmrs.module.epts.etl.conf.interfaces;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.epts.etl.conf.DefaultEtlValidator;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlFragmentInclude;
import org.openmrs.module.epts.etl.conf.EtlTemplateConfiguration;
import org.openmrs.module.epts.etl.conf.EtlTemplateInfo;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.SQLUtilities;

public interface EtlDataConfiguration extends BaseConfiguration {

	Pattern PLACEHOLDER = Pattern.compile("\\$\\{([A-Za-z0-9_.-]+)}");

	EtlConfiguration getRelatedEtlConf();

	EtlDataConfiguration getParentConf();

	public List<DefaultEtlValidator> getValidators();

	void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc);

	ActionOnEtlIssue getGeneralBehaviourOnEtlException();

	EtlTemplateInfo getTemplate();

	void setTemplate(EtlTemplateInfo template);

	List<String> getDynamicElements();

	List<EtlFragmentInclude> getInclude();

	default boolean hasInclude() {
		return utilities.listHasElement(this.getInclude());
	}

	default boolean hasDynamicElements() {
		return utilities.listHasElement(this.getDynamicElements());
	}

	default boolean hasValidator() {
		return utilities.listHasElement(this.getValidators());
	}

	default DBConnectionInfo getSrcConnInfo() {
		return this.getRelatedEtlConf().getSrcConnInfo();
	}

	default String getTemplateName() {
		return hasTemplate() ? getTemplate().getName() : null;
	}

	default Map<String, Object> retrieveAllAvailableTemplateParameters() {
		if (hasTemplate()) {
			return this.getTemplate().getAllAvailableParameters();
		} else {
			return null;
		}
	}

	default void tryToLoadDumpScriptContentToFieldAndValidate(String fieldName, Map<String, Object> templateParameters,
			Connection conn) throws DBException {

		Object fieldValue = utilities.getFieldValue(this, fieldName);

		if (fieldValue instanceof String) {

			String sqlType = "query";

			String fromFile = "";
			String originalScript = fieldValue.toString();
			String queryWithReplacedParameters = originalScript;

			if (this.getRelatedEtlConf().checkIfIsValidDumpScript(fieldValue.toString())) {
				fromFile = " from file " + fieldValue;
				originalScript = this.getRelatedEtlConf().readDumpScriptContent(fieldValue.toString());

				queryWithReplacedParameters = EtlDataConfiguration.resolvePlaceholders(originalScript, null,
						templateParameters);

				utilities.setFieldValue(this, fieldName, queryWithReplacedParameters);
			}

			String toValidate = queryWithReplacedParameters;

			if (!SQLUtilities.startsWithSelectSqlOperation(queryWithReplacedParameters)) {
				sqlType = "condition";

				toValidate = "select * from tab where " + queryWithReplacedParameters;
			}

			if (!SQLUtilities.isValidSelectSqlQuery(toValidate, DBUtilities.determineDbmsType(conn))) {
				String msg = "Ivalid sql " + sqlType + fromFile + " within the field '" + fieldName + "'.\n\t" + sqlType
						+ "> " + originalScript;

				throw new EtlConfException(msg);
			}
		}
	}

	default void tryToLoadFromTemplate() {
		if (this.hasTemplate()) {
			EtlTemplateConfiguration template = EtlTemplateConfiguration.findTemplate(this.getRelatedEtlConf(),
					this.getTemplate().getName());

			template.setRelatedEtlConf(getRelatedEtlConf());

			EtlDataConfiguration fromTemplate = template.parseToEtlDataConfiguration(this.getClass(),
					this.getTemplate());

			this.copyFromTemplate(fromTemplate, this.getTemplate() != null ? this.getTemplate().getName() : null,
					this.getTemplate());
		}

	}

	default boolean hasTemplate() {
		return this.getTemplate() != null;
	}

	default void copyFromTemplate(EtlDataConfiguration toCopyFrom, String mainTemplateName,
			EtlTemplateInfo templateInfo) {

		if (toCopyFrom == null) {
			return;
		}

		String errorSufix = "Error happened Within template: " + mainTemplateName;

		if (!this.getClass().isAssignableFrom(toCopyFrom.getClass())
				&& !toCopyFrom.getClass().isAssignableFrom(this.getClass())) {
			throw new EtlExceptionImpl(errorSufix + "> Incompatible template type: " + toCopyFrom.getClass().getName());
		}

		copyFieldsFromTemplate(toCopyFrom, errorSufix);

		applyDynamicElementsIfMissing(this, templateInfo, errorSufix);
	}

	default void applyIncludes() {
		if (hasInclude()) {
			for (EtlFragmentInclude i : this.getInclude()) {
				i.include(this);
			}
		}
	}

	@SuppressWarnings("unchecked")
	default void copyFieldsFromTemplate(Object toCopyFrom, String errorSufix) {

		Class<?> currentClass = this.getClass();

		while (currentClass != null && currentClass != Object.class) {

			Field[] fields = currentClass.getDeclaredFields();

			for (Field field : fields) {

				int modifiers = field.getModifiers();

				if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
					continue;
				}

				if ("template".equals(field.getName())) {
					continue;
				}

				try {
					field.setAccessible(true);

					Object templateValue = field.get(toCopyFrom);

					if (templateValue == null) {
						continue;
					}

					Object currentValue = field.get(this);

					if (templateValue instanceof List<?>) {

						List<?> templateList = (List<?>) templateValue;

						if (currentValue == null) {
							field.set(this, new ArrayList<>(templateList));
						} else if (currentValue instanceof List<?>) {
							List<Object> currentList = (List<Object>) currentValue;
							currentList.addAll(templateList);
						} else {
							throw new EtlExceptionImpl(errorSufix + "> Field '" + field.getName()
									+ "' is not a List but template provides a List.");
						}

					} else {

						if (!canBeOverriten(currentValue, field)) {
							throw new EtlExceptionImpl(errorSufix + "> Field '" + field.getName()
									+ "' already has a value and cannot be overridden by template.");
						}

						field.set(this, templateValue);
					}

				} catch (IllegalAccessException e) {
					throw new EtlExceptionImpl(
							errorSufix + "> Error copying field '" + field.getName() + "' from template.", e);
				}
			}

			currentClass = currentClass.getSuperclass();
		}
	}

	public static void applyDynamicElementsIfMissing(Object target, EtlTemplateInfo templateInfo, String errorSufix) {

		if (target == null || templateInfo == null || templateInfo.getDynamicElements() == null) {
			return;
		}

		applyDynamicElementsOnCurrentObject(target, templateInfo.getDynamicElements(), errorSufix);

		for (Field field : getAllFields(target.getClass())) {

			int modifiers = field.getModifiers();

			if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
				continue;
			}

			if (field.getType().isPrimitive() || field.getType().isEnum()
					|| field.getType().getName().startsWith("java.")) {
				continue;
			}

			try {
				field.setAccessible(true);

				Object value = field.get(target);

				if (value == null) {
					continue;
				}

				if (value instanceof Collection<?>) {
					for (Object item : (Collection<?>) value) {
						applyDynamicElementsIfMissing(item, templateInfo, errorSufix);
					}
				} else {
					applyDynamicElementsIfMissing(value, templateInfo, errorSufix);
				}

			} catch (IllegalAccessException e) {
				throw new EtlExceptionImpl(
						errorSufix + "> Error applying dynamicElements on field '" + field.getName() + "'.", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void applyDynamicElementsOnCurrentObject(Object target, List<?> dynamicElements, String errorSufix) {

		Field dynamicField = findField(target.getClass(), "dynamicElements");

		if (dynamicField == null) {
			return;
		}

		try {
			dynamicField.setAccessible(true);

			Object currentValue = dynamicField.get(target);

			if (currentValue == null) {
				dynamicField.set(target, new ArrayList<>(dynamicElements));
				return;
			}

			if (currentValue instanceof List<?>) {
				List<Object> currentList = (List<Object>) currentValue;

				if (currentList.isEmpty()) {
					currentList.addAll(dynamicElements);
				}

				return;
			}

			throw new EtlExceptionImpl(errorSufix + "> Field 'dynamicElements' exists but is not a List in class "
					+ target.getClass().getName());

		} catch (IllegalAccessException e) {
			throw new EtlExceptionImpl(
					errorSufix + "> Error setting dynamicElements in class " + target.getClass().getName(), e);
		}
	}

	public static Field findField(Class<?> clazz, String fieldName) {

		Class<?> current = clazz;

		while (current != null && current != Object.class) {
			try {
				return current.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ignored) {
				current = current.getSuperclass();
			}
		}

		return null;
	}

	public static List<Field> getAllFields(Class<?> clazz) {

		List<Field> fields = new ArrayList<>();

		Class<?> current = clazz;

		while (current != null && current != Object.class) {
			fields.addAll(Arrays.asList(current.getDeclaredFields()));
			current = current.getSuperclass();
		}

		return fields;
	}

	static String[] SAFE_FIELDS = { "joinExtraConditionScope", "useAsDataSource", "relatedEtlConf", "loadHealper",
			"onMultipleDataSourceForSameMapping", "onMultipleDataSourceWithSameName", "limitToOneResult",
			"relationshipResolutionStrategy", "nullValueBehavior", "manuallyConfigured", "possibleSrc",
			"manuallyConfigured", "loadedDataSourceInfo" };

	public static boolean canBeOverriten(Object value, Field field) {

		Class<?> type = field.getType();

		if (value == null) {
			return true;
		}

		if (type.isPrimitive()) {
			if (type == boolean.class)
				return !(Boolean) value;
			if (type == char.class)
				return ((Character) value) == '\u0000';
			if (type == byte.class)
				return ((Byte) value) == 0;
			if (type == short.class)
				return ((Short) value) == 0;
			if (type == int.class)
				return ((Integer) value) == 0;
			if (type == long.class)
				return ((Long) value) == 0L;
			if (type == float.class)
				return ((Float) value) == 0f;
			if (type == double.class)
				return ((Double) value) == 0d;
		} else {
			try {
				return utilities.getPosOnArray(SAFE_FIELDS, field.getName()) >= 0;
			} catch (RuntimeException e) {
				return false;
			}
		}

		return false;
	}

	public static Properties loadProperties(String path) {
		if (path == null)
			return null;

		Properties props = new Properties();

		try (InputStream is = new FileInputStream(path)) {
			props.load(is);
		} catch (IOException e) {
			throw new RuntimeException("Error loading properties file: " + path, e);
		}

		return props;
	}

	public static String resolvePlaceholders(String text, Set<String> allowedPlaceholders, Map<String, ?> env) {

		Properties prefProps = utilities.toProperties(env);
		Properties appProps = loadProperties(System.getProperty("etl.env.file"));
		Properties javaProps = System.getProperties();
		Properties sysProps = utilities.toProperties(System.getenv());

		if (text == null || text.isBlank()) {
			return text;
		}

		Matcher m = PLACEHOLDER.matcher(text);

		StringBuffer sb = new StringBuffer();

		while (m.find()) {

			String key = m.group(1);

			// whitelist
			if (allowedPlaceholders != null && !allowedPlaceholders.contains(key)) {
				m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));

				continue;
			}

			Object value = null;

			value = prefProps.get(key);

			if (value == null) {
				value = appProps.getProperty(key);
			}

			if (value == null) {
				value = javaProps.getProperty(key);
			}

			if (value == null) {
				value = sysProps.getProperty(key);
			}

			if (value == null) {
				throw new IllegalArgumentException("Missing placeholder value for: " + key);
			}

			String replacement = value.toString();

			replacement = escapeJsonString(replacement);

			m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		}

		m.appendTail(sb);

		return sb.toString();
	}

	public static String escapeJsonString(String value) {

		if (value == null) {
			return null;
		}

		return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t",
				"\\t");
	}

	default void stepIntoBreakpoint(EtlConfiguration etlConf, boolean b) {
		if (b) {
			if (etlConf != null)
				etlConf.logDebug("Steped into the breakpoint");
			else
				System.err.println("Steped into the breakpoint");
		}
	}

}
