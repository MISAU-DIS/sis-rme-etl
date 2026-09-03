package org.openmrs.module.epts.etl.utilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/**
 * Canonical class loader for generated database-model POJOs. One instance must
 * be shared by every consumer of the same ETL configuration.
 */
public final class DataModelClassLoader extends URLClassLoader {

	private static final String GENERATED_POJO_PACKAGE = "org.openmrs.module.epts.etl.model.pojo.";

	private static final String GENERIC_POJO_PACKAGE = GENERATED_POJO_PACKAGE + "generic.";

	public DataModelClassLoader(File compiledClassesDirectory, File moduleRootDirectory, List<File> classPath) {
		super(toUrls(compiledClassesDirectory, moduleRootDirectory, classPath), EtlDatabaseObject.class.getClassLoader());
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (!isGeneratedPojo(name)) return super.loadClass(name, resolve);
		synchronized (getClassLoadingLock(name)) {
			Class<?> loaded = findLoadedClass(name);
			if (loaded == null) {
				try {
					loaded = findClass(name);
				} catch (ClassNotFoundException exception) {
					loaded = super.loadClass(name, false);
				}
			}
			if (resolve) resolveClass(loaded);
			return loaded;
		}
	}

	@SuppressWarnings("unchecked")
	public Class<EtlDatabaseObject> loadDatabaseObjectClass(String fullClassName) throws ClassNotFoundException {
		return (Class<EtlDatabaseObject>) loadClass(fullClassName);
	}

	private static boolean isGeneratedPojo(String className) {
		return className.startsWith(GENERATED_POJO_PACKAGE) && !className.startsWith(GENERIC_POJO_PACKAGE);
	}

	private static URL[] toUrls(File compiledClassesDirectory, File moduleRootDirectory, List<File> classPath) {
		Set<File> entries = new LinkedHashSet<>();
		add(entries, compiledClassesDirectory);
		add(entries, moduleRootDirectory);
		if (classPath != null) {
			for (File entry : classPath) add(entries, entry);
		}

		List<URL> urls = new ArrayList<>();
		for (File entry : entries) {
			try {
				urls.add(entry.toURI().toURL());
			} catch (MalformedURLException exception) {
				throw new IllegalArgumentException("Invalid data-model classpath entry: " + entry, exception);
			}
		}
		return urls.toArray(new URL[0]);
	}

	private static void add(Set<File> entries, File entry) {
		if (entry != null) entries.add(entry.getAbsoluteFile());
	}
}
