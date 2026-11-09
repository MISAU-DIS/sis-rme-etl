package org.openmrs.module.epts.etl.conf.destination;

/** Logical CSV destination records, independently of their connector. */
public class CsvDestinationConfiguration extends AbstractRecordDestinationConfiguration {

	private String delimiter = ",";
	private Boolean hasHeader = Boolean.TRUE;
	private String charset = "UTF-8";
	private String quote = "\"";
	private String escape = "\\";

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
}
