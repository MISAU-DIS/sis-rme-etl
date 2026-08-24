package org.openmrs.module.epts.etl.conf;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EtlTemplateConfiguration {

	public static final CommonUtilities utilities = CommonUtilities.getInstance();

	private static final Map<String, List<EtlTemplateConfiguration>> CACHE = new HashMap<>();

	private String name;

	private Set<String> parameters;

	private JsonNode template;

	@JsonProperty("extends")
	private EtlTemplateInfo extendsTemplate;

	private EtlConfiguration relatedEtlConf;

	public EtlConfiguration getRelatedEtlConf() {
		return relatedEtlConf;
	}

	public void setRelatedEtlConf(EtlConfiguration relatedEtlConf) {
		this.relatedEtlConf = relatedEtlConf;
	}

	public EtlTemplateInfo getExtendsTemplate() {
		return extendsTemplate;
	}

	public void setExtendsTemplate(EtlTemplateInfo extendsTemplate) {
		this.extendsTemplate = extendsTemplate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getParameters() {
		return parameters;
	}

	public void setParameters(Set<String> parameters) {
		this.parameters = parameters;
	}

	public JsonNode getTemplate() {
		return template;
	}

	public void setTemplate(JsonNode template) {
		this.template = template;
	}

	public Boolean isExtension() {
		return this.getExtendsTemplate() != null;
	}

	public <T extends EtlDataConfiguration> T parseToEtlDataConfiguration(Class<T> clazz,
			EtlTemplateInfo templateInfo) {
		String json = null;

		Map<String, Object> inputParams = templateInfo.getParameters();

		try {
			validateAllowedParanms(templateInfo);
			validateMissingParanms(templateInfo);

			if (this.template == null && this.extendsTemplate == null) {
				throw new EtlExceptionImpl("Missing template content on " + this.getTemplate());
			}

			json = EtlDataConfiguration.resolvePlaceholders(this.template != null ? this.template.toString() : "{}",
					this.getParameters(), inputParams, true);

			EtlDataConfiguration parentFromTemplate = null;
			EtlTemplateInfo extendsTemplateInfo = null;

			if (this.isExtension()) {
				EtlTemplateConfiguration baseTemplate = EtlTemplateConfiguration.findTemplate(this.getRelatedEtlConf(),
						this.getExtendsTemplate().getName());

				extendsTemplateInfo = this.getExtendsTemplate()
						.cloneAndEnsureParametersAndOverridePlaceholdersReplacement(inputParams);

				extendsTemplateInfo.setChildTemplate(templateInfo);

				templateInfo.setParentTemplate(extendsTemplateInfo);

				baseTemplate.setRelatedEtlConf(getRelatedEtlConf());

				parentFromTemplate = baseTemplate.parseToEtlDataConfiguration(clazz, extendsTemplateInfo);
			}

			T etlDataConf = new ObjectMapperProvider().getContext(clazz).readValue(json, clazz);

			if (parentFromTemplate != null) {
				etlDataConf.copyFromTemplate(parentFromTemplate, this.getName(), extendsTemplateInfo);
			}

			this.ensureOverride(etlDataConf, templateInfo);

			return etlDataConf;
		} catch (IOException | IllegalArgumentException e) {
			throw new EtlExceptionImpl("Error happened loading template " + this.name, e);
		}
	}

	private void ensureOverride(EtlDataConfiguration toOverride, EtlTemplateInfo templateInfo) {
		if (templateInfo.hasOverride()) {
			for (TemplateOverride override : templateInfo.getOverride()) {
				override.setParent(templateInfo);
				override.apply(toOverride);
			}
		}
	}

	void validateMissingParanms(EtlTemplateInfo templateInfo) {
		Map<String, Object> inputParams = templateInfo.getParameters();

		Set<String> allowedParams = this.getParameters();

		if (allowedParams == null) {
			allowedParams = new HashSet<>();
		}

		List<String> missingParams = allowedParams.stream().filter(p -> !inputParams.containsKey(p))
				.collect(java.util.stream.Collectors.toList());

		if (!missingParams.isEmpty()) {
			String childMsg = templateInfo.getChildTemplate() != null
					? " Within extends template: " + templateInfo.getChildTemplate().getName()
					: "";

			throw new EtlExceptionImpl("The following parameters are missing in template (" + this.getName() + "): "
					+ missingParams + childMsg);
		}
	}

	void validateAllowedParanms(EtlTemplateInfo templateInfo) {
		Map<String, Object> inputParams = templateInfo.getParameters();

		if (inputParams == null)
			return;

		Set<String> allowedSet = this.getParameters() != null ? new HashSet<>(this.getParameters())
				: Collections.emptySet();

		List<String> unknownParams = inputParams.keySet().stream().filter(key -> !allowedSet.contains(key))
				.collect(java.util.stream.Collectors.toList());

		if (!unknownParams.isEmpty()) {
			String childMsg = templateInfo.getChildTemplate() != null
					? " Within extends template: " + templateInfo.getChildTemplate().getName()
					: "";

			throw new EtlExceptionImpl("The following parameters are not allowed for template (" + this.getName()
					+ "): " + unknownParams + childMsg);
		}
	}

	public static EtlTemplateConfiguration findTemplate(EtlConfiguration relatedEtlConf, String templateName) {

		String templatesFileLocation = relatedEtlConf.getTemplatesDir();

		if (templatesFileLocation == null || templatesFileLocation.isBlank()) {
			throw new EtlExceptionImpl("Templates file path is not defined.");
		}

		if (templateName == null || templateName.isBlank()) {
			throw new EtlExceptionImpl("Missing template name in one or more template invocation.");
		}

		String cacheKey = relatedEtlConf.getEtlConfDir() + "|" + templatesFileLocation;

		List<EtlTemplateConfiguration> templates = CACHE.computeIfAbsent(cacheKey, path -> {
			try {
				ObjectMapper mapper = new ObjectMapperProvider().getContext(EtlTemplateConfiguration.class);
				List<EtlTemplateConfiguration> result = new ArrayList<>();

				for (File file : resolveFiles(relatedEtlConf.getEtlConfDir(), templatesFileLocation)) {
					JsonNode content = mapper.readTree(file);

					if (content.isArray()) {
						result.addAll(mapper.convertValue(content, mapper.getTypeFactory()
								.constructCollectionType(List.class, EtlTemplateConfiguration.class)));
					} else {
						result.add(mapper.treeToValue(content, EtlTemplateConfiguration.class));
					}
				}

				validateUniqueTemplateNames(result, templatesFileLocation);

				return result;
			} catch (IOException e) {
				throw new EtlExceptionImpl("Error reading templates path: " + templatesFileLocation, e);
			}
		});

		return templates.stream().filter(t -> templateName.equals(t.getName())).findFirst()
				.orElseThrow(() -> new EtlExceptionImpl(
						"Template not found: " + templateName + " in path: " + templatesFileLocation));
	}

	private static void validateUniqueTemplateNames(List<EtlTemplateConfiguration> templates, String path) {
		Set<String> names = new HashSet<>();
		Set<String> duplicatedNames = new LinkedHashSet<>();

		for (EtlTemplateConfiguration template : templates) {
			if (!names.add(template.getName())) {
				duplicatedNames.add(template.getName());
			}
		}

		if (!duplicatedNames.isEmpty()) {
			throw new EtlExceptionImpl(
					"Duplicated template names " + duplicatedNames + " in templates path: " + path);
		}
	}

	private static List<File> resolveFiles(String confRootDir, String path) throws IOException {
		if (confRootDir == null || confRootDir.isBlank()) {
			throw new EtlExceptionImpl("The confRootDir was not specified!");
		}

		Path rootPath = Paths.get(confRootDir).normalize();

		if (path.contains("*")) {
			int lastSeparator = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
			String parentPart = lastSeparator >= 0 ? path.substring(0, lastSeparator) : "";
			String pattern = lastSeparator >= 0 ? path.substring(lastSeparator + 1) : path;
			Path parentDir = parentPart.isEmpty() ? rootPath : rootPath.resolve(parentPart).normalize();

			validatePathWithinRoot(rootPath, parentDir, path);

			if (!Files.exists(parentDir) || !Files.isDirectory(parentDir)) {
				throw new EtlExceptionImpl("Templates directory not found: " + parentDir);
			}

			List<File> files = new ArrayList<>();
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(parentDir, pattern)) {
				for (Path item : stream) {
					if (Files.isRegularFile(item)) {
						files.add(item.toFile());
					}
				}
			}
			files.sort(Comparator.comparing(File::getName));
			return files;
		}

		Path resolvedPath = rootPath.resolve(path).normalize();
		validatePathWithinRoot(rootPath, resolvedPath, path);
		File file = resolvedPath.toFile();

		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
			if (files == null) {
				return new ArrayList<>();
			}
			List<File> result = new ArrayList<>(Arrays.asList(files));
			result.sort(Comparator.comparing(File::getName));
			return result;
		}

		if (file.exists() && file.isFile()) {
			return Collections.singletonList(file);
		}

		throw new EtlExceptionImpl("Templates path not found: " + resolvedPath);
	}

	private static void validatePathWithinRoot(Path rootPath, Path resolvedPath, String path) {
		if (!resolvedPath.startsWith(rootPath)) {
			throw new EtlExceptionImpl("Invalid templates path outside configuration root: " + path);
		}
	}

	@Override
	public String toString() {
		return name + "("
				+ (this.getParameters() != null && !this.getParameters().isEmpty() ? this.getParameters().toString()
						: "")
				+ ")";
	}
}
