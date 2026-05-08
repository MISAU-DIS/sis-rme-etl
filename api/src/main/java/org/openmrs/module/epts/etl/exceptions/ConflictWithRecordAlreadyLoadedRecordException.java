package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.model.base.EtlObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class ConflictWithRecordAlreadyLoadedRecordException extends DBException {
	
	private static final long serialVersionUID = 1L;
	
	private EtlObject currentRecord;
	
	private EtlObject existingRecord;
	
	public ConflictWithRecordAlreadyLoadedRecordException(EtlObject currentRecord, EtlObject existingRecord, DBException e) {
		super("Found conflictant record with current record: \nCurrentRecord: " + currentRecord + "\nExisting Record: "
		        + existingRecord, e);
		
		this.currentRecord = currentRecord;
	}
	
	public EtlObject getRecord() {
		return currentRecord;
	}
	
	public EtlObject getExistingRecord() {
		return existingRecord;
	}
	
	@Override
	public boolean isDuplicatePrimaryOrUniqueKeyException() throws DBException {
		return true;
	}
	
	@Override
	public boolean isIntegrityConstraintViolationException() throws DBException {
		return false;
	}
}
