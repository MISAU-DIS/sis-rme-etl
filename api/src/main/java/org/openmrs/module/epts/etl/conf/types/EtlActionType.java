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
	 * Moves the source record to the stage area only when all of its destination
	 * records have been successfully loaded.
	 *
	 * <p>
	 * A source record with no destination records is not considered successfully
	 * loaded and therefore remains in the source table. Use
	 * {@link #MOVE_TO_STAGE_AREA_ON_NO_ERROR} when such a record should also be moved.
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
	 * Moves the source record to the stage area when its processing produced no
	 * error.
	 *
	 * <p>
	 * This includes records whose destination records were successfully loaded and
	 * records for which there was nothing to load. Records that failed or were only
	 * partially loaded remain in the source table so they can be retried later.
	 * </p>
	 *
	 * <p>
	 * When source processing-state tracking is enabled, records that remain in the
	 * source table have their processing status, date, error and retry count updated.
	 * </p>
	 */
	MOVE_TO_STAGE_AREA_ON_NO_ERROR,

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

	public boolean moveToStageAreaOnNoError() {
		return this.equals(MOVE_TO_STAGE_AREA_ON_NO_ERROR);
	}

	public boolean includeTracking() {
		return moveToStageAreaOnSuccess() || moveToStageAreaOnNoError() || moveToStageArea();
	}
	
}
