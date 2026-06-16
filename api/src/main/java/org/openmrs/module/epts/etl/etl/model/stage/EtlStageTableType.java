package org.openmrs.module.epts.etl.etl.model.stage;

public enum EtlStageTableType {
	
	SRC, 
	DST, 
	PROCESSED_RECORD;

	public boolean isSrc() {
		return this.equals(SRC);
	}

	public boolean isDst() {
		return this.equals(DST);
	}

	public boolean isProcessedRecord() {
		return this.equals(PROCESSED_RECORD);
	}

}
