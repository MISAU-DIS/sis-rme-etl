package org.openmrs.module.epts.etl.connector.file;

/** Unchecked failure while persisting records through a file connector. */
public class FileConnectorWriteException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FileConnectorWriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
