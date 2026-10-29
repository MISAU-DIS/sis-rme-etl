package org.openmrs.module.epts.etl.connector.file;

/** Unchecked failure while reading records through a file connector. */
public class FileConnectorReadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileConnectorReadException(String message, Throwable cause) {
		super(message, cause);
	}
}
