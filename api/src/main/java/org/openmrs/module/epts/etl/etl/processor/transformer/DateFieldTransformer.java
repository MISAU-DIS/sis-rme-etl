package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.interfaces.EtlTranformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.EtlInconsistencyBehavior;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class DateFieldTransformer extends AbstractEtlFieldTransformer {
	
	private static final Map<String, DateFieldTransformer> INSTANCES = new ConcurrentHashMap<>();
	
	private DateTransformOperation operation;
	
	private DateAndTimeFormat inputFormat;
	
	private DateAndTimeFormat outputFormat;
	
	private int amount;
	
	private DateTimeUnit unit;
	
	private EtlInconsistencyBehavior onInvalid;
	
	private List<String> rawParameterDefinitions;
	
	public DateFieldTransformer(List<Object> parameters, EtlTranformTarget relatedEtlTargedConf, TransformableField field,
	    Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		
		super(parameters, relatedEtlTargedConf, field);
		
		this.onInvalid = EtlInconsistencyBehavior.ABORT_PROCESS;
		
		this.rawParameterDefinitions = parameters != null ? parameters.stream().map(Object::toString).toList()
		        : Collections.emptyList();
		
		if (utilities.listHasElement(this.rawParameterDefinitions)) {
			
			for (String fieldData : this.rawParameterDefinitions) {
				String[] mapping = fieldData.split(":", 2);
				
				if (mapping.length != 2) {
					throw new EtlExceptionImpl("Wrong format for conditional parameters within the tranformer "
					        + getTransformerDsc() + "\n" + "Each object param must be specified as paramName:paramValue");
				}
				
				String paramName = mapping[0];
				String paramValue = mapping[1];
				
				if (!utilities.stringHasValue(paramValue)) {
					throw new EtlExceptionImpl("The paramValue for parameter " + paramName
					        + " has no value on transformer:  " + getTransformerDsc());
				}
				
				if (paramName.equals("input")) {
					if (isTransformerExpression(paramValue)) {
						this.input = FieldsMapping.fastCreate(field.getDstField(), field.getDstField(), false, conn);
						this.input.setTransformer(paramValue);
						this.input.tryToLoadTransformer(relatedEtlTransformTarget, conn);
						
					} else {
						this.input = FieldsMapping.fastCreate(paramValue, paramValue, relatedEtlTransformTarget, conn);
					}
				} else if (paramName.equals("operation")) {
					this.operation = DateTransformOperation.valueOf(paramValue);
				} else if (paramName.equals("on_invalid")) {
					try {
						this.onInvalid = EtlInconsistencyBehavior.valueOf(paramValue);
					}
					catch (Exception e) {
						throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
						        + " on transformer:  " + getTransformerDsc());
					}
					
				} else if (paramName.equals("output_format")) {
					try {
						this.outputFormat = DateAndTimeFormat.resolve(paramValue);
					}
					catch (Exception e) {
						throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
						        + " on transformer:  " + getTransformerDsc());
					}
					
				} else if (paramName.equals("input_format")) {
					try {
						this.inputFormat = DateAndTimeFormat.resolve(paramValue);
					}
					catch (Exception e) {
						throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
						        + " on transformer:  " + getTransformerDsc());
					}
					
				} else if (paramName.equals("amount")) {
					try {
						this.amount = Integer.parseInt(paramValue);
					}
					catch (Exception e) {
						throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
						        + " on transformer:  " + getTransformerDsc());
					}
				} else if (paramName.equals("unit")) {
					try {
						this.unit = DateTimeUnit.valueOf(paramValue);
					}
					catch (Exception e) {
						throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
						        + " on transformer:  " + getTransformerDsc());
					}
				} else {
					throw new ForbiddenOperationException(
					        "Unsupported parameter " + paramName + " on transformer:  " + getTransformerDsc());
				}
			}
		}
		
		if (this.operation == null) {
			throw new EtlExceptionImpl("Missing operation parameter");
		}
	}
	
	public static DateFieldTransformer getInstance(List<Object> parameters, EtlTranformTarget relatedEtlTargedConf,
	        TransformableField field, Connection conn) {
		
		String key = buildCacheKey(relatedEtlTargedConf, field, parameters);
		
		DateFieldTransformer existing = INSTANCES.get(key);
		
		if (existing != null) {
			return existing;
		}
		
		DateFieldTransformer transformer;
		try {
			transformer = new DateFieldTransformer(parameters, relatedEtlTargedConf, field, conn);
		}
		catch (FieldAvaliableInMultipleDataSources | DBException e) {
			throw new EtlConfException(e);
		}
		
		INSTANCES.putIfAbsent(key, transformer);
		
		return INSTANCES.get(key);
	}
	
	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
	        Connection srcConn, Connection dstConn) throws DBException, EtlTransformationException {
		
		Object valueToTransform = null;
		
		FieldTransformingInfo transformingInfo = null;
		
		if (this.hasInput()) {
			transformingInfo = this.input.getTransformerInstance().transform(processor, srcObject, transformedRecord,
			    additionalSrcObjects, this.input, srcConn, dstConn);
			
			try {
				valueToTransform = transformingInfo.getTransformedValue();
			}
			catch (Exception e) {
				throw e;
			}
			
		} else {
			valueToTransform = field.getValueToTransform();
		}
		
		if (valueToTransform == null) {
			return null;
		}
		
		Object readyValueToTranform = EtlFieldTransformer.tryToReplaceParametersOnSrcValue(additionalSrcObjects,
		    valueToTransform);
		
		Object transformedValue = evaluate(srcObject, readyValueToTranform);
		
		return new FieldTransformingInfo(field, transformedValue, null);
	}
	
	private Object evaluate(EtlDatabaseObject srcObject, Object readyValueToTransform) {
		
		try {
			
			switch (operation) {
				
				case NOW:
					return utilities.getCurrentDate();
				
				case PARSE:
					return parseDate(readyValueToTransform.toString());
				
				case FORMAT:
					return formatDate(readyValueToTransform);
				
				case ADD:
					return adjustDate(readyValueToTransform, amount);
				
				case SUBTRACT:
					return adjustDate(readyValueToTransform, -amount);
				
				case START_OF_DAY:
					return getStartOfDay(readyValueToTransform);
				
				case END_OF_DAY:
					return getEndOfDay(readyValueToTransform);
				
				default:
					
					throw new ForbiddenOperationException("Unsupported operation: " + operation);
				
			}
			
		}
		catch (Exception e) {
			
			if (onInvalid != null && onInvalid.setToNull()) {
				return null;
			}
			
			throw new EtlTransformationException("Error Happened evaluating Date Operation", e, srcObject,
			        getGeneralBehaviourOnEtlException());
		}
	}
	
	private Date parseDate(String value) throws ParseException {
		
		if (inputFormat == null) {
			throw new EtlExceptionImpl("input_format required");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(inputFormat.getFormat());
		
		return sdf.parse(value);
	}
	
	private String formatDate(Object value) throws ParseException {
		
		if (outputFormat == null) {
			throw new EtlExceptionImpl("output_format required");
		}
		
		Date date;
		
		if (value instanceof Date) {
			date = (Date) value;
		} else {
			
			if (inputFormat == null) {
				throw new EtlExceptionImpl("input_format required");
			}
			
			date = parseDate(value.toString());
		}
		
		return new SimpleDateFormat(outputFormat.getFormat()).format(date);
	}
	
	private Date adjustDate(Object value, int amount) throws ParseException {
		
		Date date;
		
		if (value instanceof Date) {
			date = (Date) value;
		} else {
			date = parseDate(value.toString());
		}
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		
		int a = (int) Math.round(amount);
		
		switch (unit) {
			
			case DAYS:
				cal.add(Calendar.DAY_OF_MONTH, a);
				break;
			
			case MONTHS:
				cal.add(Calendar.MONTH, a);
				break;
			
			case YEARS:
				cal.add(Calendar.YEAR, a);
				break;
			
			case HOURS:
				cal.add(Calendar.HOUR, a);
				break;
			
			case MINUTES:
				cal.add(Calendar.MINUTE, a);
				break;
			
			case SECONDS:
				cal.add(Calendar.SECOND, a);
				break;
			
			default:
				throw new ForbiddenOperationException("Unsupported unit");
		}
		
		return cal.getTime();
	}
	
	private Date getStartOfDay(Object value) throws ParseException {
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(value instanceof Date ? (Date) value : parseDate(value.toString()));
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		cal.set(Calendar.MINUTE, 0);
		
		cal.set(Calendar.SECOND, 0);
		
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	private Date getEndOfDay(Object value) throws ParseException {
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(value instanceof Date ? (Date) value : parseDate(value.toString()));
		
		cal.set(Calendar.HOUR_OF_DAY, 23);
		
		cal.set(Calendar.MINUTE, 59);
		
		cal.set(Calendar.SECOND, 59);
		
		cal.set(Calendar.MILLISECOND, 999);
		
		return cal.getTime();
	}
}
