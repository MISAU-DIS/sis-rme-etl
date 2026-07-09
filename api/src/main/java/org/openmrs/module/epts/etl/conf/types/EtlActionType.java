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
	 * Moves the source record to the stage area only when it has been successfully
	 * processed.
	 *
	 * <p>
	 * If processing succeeds, the record is moved from the source table to the stage
	 * area. If processing fails, the record remains in the source table so it can be
	 * retried later.
	 * </p>
	 *
	 * <p>
	 * When the source configuration has processing state tracking enabled, failed
	 * records will have their processing status, processing date, processing error,
	 * and retry count updated before being left in the source table.
	 * </p>
	 */
	MOVE_TO_STAGE_AREA_ON_SUCCESS,
	
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
	
	
	public boolean moveToStageAreaOnSuccess() {
		return this.equals(MOVE_TO_STAGE_AREA_ON_SUCCESS);
	}

	public boolean includeTracking() {
		return moveToStageAreaOnSuccess() || moveToStageArea();
	}
	
}
