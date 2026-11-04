package org.openmrs.module.epts.etl.conf.datasource;

import java.util.List;

import org.openmrs.module.epts.etl.model.Field;

/** Logical CSV records, independently of the connector transporting them. */
public class CsvPrimaryDataSourceConfiguration extends AbstractRecordPrimaryDataSourceConfiguration {

	private String delimiter = ",";

	private Boolean hasHeader = Boolean.TRUE;

	private String charset = "UTF-8";

	private String quote = "\"";

	private String escape = "\\";

	private List<Field> fields;

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Boolean getHasHeader() {
		return hasHeader;
	}

	public void setHasHeader(Boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getEscape() {
		return escape;
	}

	public void setEscape(String escape) {
		this.escape = escape;
	}

	@Override
	public List<Field> getFields() {
		return fields;
	}

	@Override
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	@Override
	protected String getSourceTypeName() {
		return "csvDataSource";
	}
}
