package org.openmrs.module.epts.etl.conf.types;

/**
 * The ETL action type
 */
public enum EtlActionType {

	// @formatter:off
	/**
	 * This action creates new dstRecord on ETL operation
	 */
	CREATE,
	
	/**
	 * This action deletes the dstRecord on ETL operation
	 */
	DELETE,
	
	/**
	 * This action update the dstRecord on ETL operation
	 */
	UPDATE,
	
	/**
	 * Moves the current source record to the processing stage area.
	 * <p>
	 * The processing stage area is a temporary storage used to keep
	 * records that have already been consumed from the source system
	 * and are awaiting processing, reprocessing or final disposition.
	 * <p>
	 * Depending on the ETL configuration, the original record may be
	 * removed from the source after being successfully transferred to
	 * the processing stage area.
	 */
	MOVE_TO_STAGE_AREA,

	/**
	 * Undefined action.
	 */
	UNDEFINED;
	
	public boolean isCreate() {
		return this.equals(CREATE);
	}
	
	public boolean isDelete() {
		return this.equals(DELETE);
	}
	
	public boolean isUpdate() {
		return this.equals(UPDATE);
	}
	
	public boolean isUndefined() {
		return this.equals(UNDEFINED);
	}
	
	public boolean moveToStageArea() {
		return this.equals(MOVE_TO_STAGE_AREA);
	}
	
}
