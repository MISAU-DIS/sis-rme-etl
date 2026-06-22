package org.openmrs.module.epts.etl.conf;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EtlFragmentInclude extends AbstractEtlDataConfiguration {

	private String target;

	private String srcPath;

	private EtlDataConfiguration parent;

	public EtlDataConfiguration getParent() {
		return parent;
	}

	public void setParent(EtlDataConfiguration parent) {
		this.parent = parent;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.parent;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {

	}

	public void include(EtlDataConfiguration dc) {

		if (dc == null) {
			throw new EtlConfException("Target EtlDataConfiguration cannot be null.");
		}

		if (target == null || target.isBlank()) {
			throw new EtlConfException("Include target cannot be null or empty.");
		}

		if (srcPath == null || srcPath.isBlank()) {
			throw new EtlConfException("Include srcPath cannot be null or empty.");
		}

		Field targetField = findField(dc.getClass(), target);

		if (targetField == null) {
			throw new EtlConfException("Field '" + target + "' not found in class " + dc.getClass().getName());
		}

		targetField.setAccessible(true);

		try {
			if (List.class.isAssignableFrom(targetField.getType())) {
				includeList(dc, targetField);
			} else {
				includeSingle(dc, targetField);
			}
		} catch (Exception e) {
			throw new EtlConfException("Error applying include for target '" + target + "' from '" + srcPath + "'", e);
		}
	}

	private void includeSingle(EtlDataConfiguration dc, Field targetField) throws Exception {

		File file = resolveSingleFile(srcPath);

		ObjectMapper mapper = new ObjectMapperProvider().getContext(targetField.getType());

		Object value = mapper.readValue(file, targetField.getType());

		targetField.set(dc, value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void includeList(EtlDataConfiguration dc, Field targetField) throws Exception {

		Class<?> itemType = resolveListItemType(targetField);

		List<File> files = resolveFiles(dc.getRelatedEtlConf().getEtlConfDir(), srcPath);

		ObjectMapper mapper = new ObjectMapperProvider().getContext(itemType);

		Object currentValue = targetField.get(dc);

		List targetList;

		if (currentValue == null) {
			targetList = new ArrayList<>();
			targetField.set(dc, targetList);
		} else if (currentValue instanceof List<?>) {
			targetList = (List) currentValue;
		} else {
			throw new EtlExceptionImpl("Field '" + targetField.getName() + "' is not a List.");
		}

		for (File file : files) {
			Object item = mapper.readValue(file, itemType);
			targetList.add(item);
		}
	}

	private Class<?> resolveListItemType(Field field) {

		Type genericType = field.getGenericType();

		if (!(genericType instanceof ParameterizedType)) {
			throw new EtlExceptionImpl("Cannot determine List item type for field '" + field.getName() + "'.");
		}

		Type itemType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

		if (itemType instanceof Class<?>) {
			return (Class<?>) itemType;
		}

		if (itemType instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) itemType).getRawType();
		}

		throw new EtlExceptionImpl("Unsupported List item type for field '" + field.getName() + "': " + itemType);
	}

	private File resolveSingleFile(String path) {

		File file = new File(path);

		if (!file.exists() || !file.isFile()) {
			throw new EtlExceptionImpl("Include file not found: " + path);
		}

		return file;
	}

	private List<File> resolveFiles(String confRootDir, String path) throws IOException {

		if (!utilities.stringHasValue(confRootDir)) {
			throw new EtlConfException("The confRootDir was not specified!");
		}

		if (!utilities.stringHasValue(path)) {
			throw new EtlConfException("The include path was not specified!");
		}

		Path rootPath = Paths.get(confRootDir).normalize();

		// Resolve o path relativamente ao directório raiz da configuração
		Path resolvedPath = rootPath.resolve(path).normalize();

		// wildcard: includes/sync/child/sync*.json
		if (path.contains("*")) {

			Path parentDir = resolvedPath.getParent();

			if (parentDir == null) {
				throw new EtlConfException("Unable to determine parent directory for wildcard path: " + path);
			}

			String pattern = resolvedPath.getFileName().toString();

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(parentDir, pattern)) {

				List<File> files = new ArrayList<>();

				for (Path item : stream) {
					if (Files.isRegularFile(item)) {
						files.add(item.toFile());
					}
				}

				files.sort(Comparator.comparing(File::getName));

				return files;
			}
		}

		File file = resolvedPath.toFile();

		// directório
		if (file.exists() && file.isDirectory()) {

			File[] files = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

			if (files == null) {
				return new ArrayList<>();
			}

			List<File> result = new ArrayList<>(Arrays.asList(files));

			result.sort(Comparator.comparing(File::getName));

			return result;
		}

		// ficheiro único
		if (file.exists() && file.isFile()) {
			return List.of(file);
		}

		throw new EtlExceptionImpl("Include path not found: " + resolvedPath);
	}

	private Field findField(Class<?> clazz, String fieldName) {

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

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.parent.getRelatedEtlConf();
	}

}
