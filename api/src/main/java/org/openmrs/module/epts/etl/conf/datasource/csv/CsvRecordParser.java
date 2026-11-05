package org.openmrs.module.epts.etl.conf.datasource.csv;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.datasource.CsvPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;

/** Parses CSV records according to one logical CSV datasource definition. */
public final class CsvRecordParser {

	private final CsvPrimaryDataSourceConfiguration configuration;

	public CsvRecordParser(CsvPrimaryDataSourceConfiguration configuration) {
		this.configuration = configuration;
	}

	public List<EtlDatabaseObject> parse(Path path) throws IOException {
		List<List<String>> rows = readRows(path);
		List<Field> fields = configuration.getFields();
		Map<String, Integer> columns = resolveColumns(rows, fields, path);
		int firstRecord = configuration.getHasHeader() == null || configuration.getHasHeader() ? 1 : 0;
		List<EtlDatabaseObject> records = new ArrayList<>();

		for (int rowIndex = firstRecord; rowIndex < rows.size(); rowIndex++) {
			List<String> row = rows.get(rowIndex);

			if (isBlank(row)) {
				continue;
			}
			if (row.size() != columns.size()) {
				throw new EtlConfException("CSV record " + (rowIndex - firstRecord + 1) + " of " + path
						+ " has " + row.size() + " columns; expected " + columns.size());
			}

			EtlDatabaseObject record = configuration.createRecordInstance();

			for (Field field : fields) {
				int column = columns.get(field.getName());
				String rawValue = column < row.size() ? row.get(column) : null;
				Object value = rawValue == null || rawValue.isEmpty() ? null : rawValue;

				if (value == null && !field.allowNull()) {
					throw new EtlConfException("Required CSV field '" + field.getName() + "' is empty at record "
							+ (rowIndex - firstRecord + 1) + " of " + path);
				}

				try {
					record.setFieldValue(field.getName(), value);
				} catch (RuntimeException exception) {
					throw new EtlConfException("Invalid CSV value for field '" + field.getName() + "' at record "
							+ (rowIndex - firstRecord + 1) + " of " + path + ": " + rawValue, exception);
				}
			}

			records.add(record);
		}

		return records;
	}

	public long count(Path path) throws IOException {
		List<List<String>> rows = readRows(path);
		int firstRecord = configuration.getHasHeader() == null || configuration.getHasHeader() ? 1 : 0;
		long count = 0;

		for (int index = firstRecord; index < rows.size(); index++) {
			if (!isBlank(rows.get(index))) {
				count++;
			}
		}

		return count;
	}

	private Map<String, Integer> resolveColumns(List<List<String>> rows, List<Field> fields, Path path) {
		if (fields == null || fields.isEmpty()) {
			throw new EtlConfException("source.csvDataSource.fields is required");
		}

		Map<String, Integer> columns = new HashMap<>();

		if (configuration.getHasHeader() == null || configuration.getHasHeader()) {
			if (rows.isEmpty()) {
				return columns;
			}

			Set<String> knownFields = new HashSet<>();
			for (Field field : fields) {
				knownFields.add(field.getName());
			}

			List<String> header = rows.get(0);
			for (int index = 0; index < header.size(); index++) {
				String name = stripBom(header.get(index)).trim();

				if (!knownFields.contains(name)) {
					throw new EtlConfException("Unknown CSV column '" + name + "' in " + path);
				}
				if (columns.put(name, index) != null) {
					throw new EtlConfException("Duplicated CSV column '" + name + "' in " + path);
				}
			}

			for (Field field : fields) {
				if (!columns.containsKey(field.getName())) {
					throw new EtlConfException("CSV column '" + field.getName() + "' was not found in " + path);
				}
			}
		} else {
			for (int index = 0; index < fields.size(); index++) {
				columns.put(fields.get(index).getName(), index);
			}
		}

		return columns;
	}

	private List<List<String>> readRows(Path path) throws IOException {
		char delimiter = requiredCharacter(configuration.getDelimiter(), "delimiter");
		char quote = requiredCharacter(configuration.getQuote(), "quote");
		Character escape = optionalCharacter(configuration.getEscape(), "escape");
		Charset charset;

		try {
			charset = Charset.forName(configuration.getCharset());
		} catch (RuntimeException exception) {
			throw new EtlConfException("Unsupported CSV charset '" + configuration.getCharset() + "'", exception);
		}

		try (Reader reader = Files.newBufferedReader(path, charset)) {
			List<List<String>> rows = new ArrayList<>();
			List<String> row = new ArrayList<>();
			StringBuilder value = new StringBuilder();
			boolean quoted = false;
			int current;

			while ((current = reader.read()) >= 0) {
				char character = (char) current;

				if (quoted) {
					if (character == quote) {
						reader.mark(1);
						int next = reader.read();
						if (next == quote) {
							value.append(quote);
						} else {
							quoted = false;
							if (next >= 0) {
								reader.reset();
							}
						}
					} else if (escape != null && character == escape) {
						int next = reader.read();
						if (next < 0) {
							value.append(character);
						} else {
							value.append((char) next);
						}
					} else {
						value.append(character);
					}
				} else if (character == quote && value.length() == 0) {
					quoted = true;
				} else if (character == delimiter) {
					row.add(value.toString());
					value.setLength(0);
				} else if (character == '\n' || character == '\r') {
					if (character == '\r') {
						reader.mark(1);
						int next = reader.read();
						if (next != '\n' && next >= 0) {
							reader.reset();
						}
					}
					row.add(value.toString());
					rows.add(row);
					row = new ArrayList<>();
					value.setLength(0);
				} else {
					value.append(character);
				}
			}

			if (quoted) {
				throw new EtlConfException("Unclosed quoted CSV value in " + path);
			}
			if (value.length() > 0 || !row.isEmpty()) {
				row.add(value.toString());
				rows.add(row);
			}

			return rows;
		}
	}

	private char requiredCharacter(String value, String property) {
		Character character = optionalCharacter(value, property);
		if (character == null) {
			throw new EtlConfException("CSV " + property + " must contain exactly one character");
		}
		return character;
	}

	private Character optionalCharacter(String value, String property) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		if (value.length() != 1) {
			throw new EtlConfException("CSV " + property + " must contain exactly one character");
		}
		return value.charAt(0);
	}

	private boolean isBlank(List<String> row) {
		return row.size() == 1 && row.get(0).trim().isEmpty();
	}

	private String stripBom(String value) {
		return value != null && value.startsWith("\uFEFF") ? value.substring(1) : value;
	}
}
