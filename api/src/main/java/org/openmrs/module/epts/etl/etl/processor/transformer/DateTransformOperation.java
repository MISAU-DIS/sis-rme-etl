package org.openmrs.module.epts.etl.etl.processor.transformer;

public enum DateTransformOperation {
	PARSE,
	FORMAT,
	ADD,
	SUBTRACT,
	NOW,
	START_OF_DAY,
	END_OF_DAY
}
