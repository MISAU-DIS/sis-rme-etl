package org.openmrs.module.epts.etl.utilities;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** Formats generated Java source using an Eclipse formatter profile. */
public final class EclipseJavaSourceFormatter {

	private EclipseJavaSourceFormatter() {
	}

	public static String format(String source, File profileFile) throws IOException {
		if (profileFile == null)
			return source;
		if (!profileFile.isFile()) {
			throw new IOException("Eclipse Java formatter configuration file does not exist: " + profileFile);
		}

		Map<String, String> options = loadOptions(profileFile);
		options.put("org.eclipse.jdt.core.compiler.source", "11");
		options.put("org.eclipse.jdt.core.compiler.compliance", "11");
		options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "11");

		CodeFormatter formatter = new DefaultCodeFormatter(options);
		TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, source, 0, source.length(), 0, null);
		if (edit == null)
			throw new IOException("Eclipse JDT could not format the generated Java source");

		Document document = new Document(source);
		try {
			edit.apply(document);
			return document.get();
		} catch (MalformedTreeException | BadLocationException exception) {
			throw new IOException("Could not apply the Eclipse Java formatter", exception);
		}
	}

	private static Map<String, String> loadOptions(File profileFile) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);

			NodeList settings = factory.newDocumentBuilder().parse(profileFile).getElementsByTagName("setting");
			if (settings.getLength() == 0) {
				throw new IOException("No Eclipse formatter settings were found in " + profileFile);
			}

			Map<String, String> options = new HashMap<>();
			for (int index = 0; index < settings.getLength(); index++) {
				Element setting = (Element) settings.item(index);
				options.put(setting.getAttribute("id"), setting.getAttribute("value"));
			}
			return options;
		} catch (ParserConfigurationException | SAXException exception) {
			throw new IOException("Could not read Eclipse Java formatter profile " + profileFile, exception);
		}
	}
}
