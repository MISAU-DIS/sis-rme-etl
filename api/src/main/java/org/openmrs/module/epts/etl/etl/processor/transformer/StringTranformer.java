package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Field transformer that evaluates string manipulation expressions using standard {@link String}
 * methods.
 * <p>
 * The transformer expects expressions that follow the format: <pre>
 * (value).methodName(arg1, arg2, ...)
 * </pre> where:
 * <ul>
 * <li><b>value</b> – the source string value</li>
 * <li><b>methodName</b> – the name of a method defined in {@link String}</li>
 * <li><b>arg1, arg2...</b> – optional arguments passed to the method</li>
 * </ul>
 * </p>
 * <p>
 * Before evaluation, any dynamic parameters in the expression are resolved using
 * {@link EtlFieldTransformer#tryToReplaceParametersOnSrcValue}.
 * </p>
 * <p>
 * The transformer uses Java reflection to invoke the specified method on the {@link String} class.
 * </p>
 * <p>
 * Example expressions:
 * </p>
 * <pre>
 * (John).toUpperCase()
 * (hello world).substring(0,5)
 * (abc123).replace(123,XYZ)
 * </pre>
 * <p>
 * If the expression cannot be parsed or the method invocation fails, an
 * {@link EtlTransformationException} is raised.
 * </p>
 */
public class StringTranformer extends AbstractEtlFieldTransformer {
	
	protected static final Map<String, StringTranformer> INSTANCES = new ConcurrentHashMap<>();
	
	private String transformationString;
	
	private StringTranformerElements transformerElements;
	
	public StringTranformer(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget, TransformableField field,
	    Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		super(parameters, relatedEtlTransformTarget, field);
		
		if (utilities.listHasNoElement(parameters)) {
			throw new EtlExceptionImpl("You must specify the string to transforn for STRING_TRANSFORMER");
		}
		
		this.transformationString = (String) parameters.get(0);
		
		loadTransformerElements(conn);
	}
	
	private void loadTransformerElements(Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		
		String expr = this.transformationString;
		
		if (expr == null || !expr.startsWith("(")) {
			throw new EtlExceptionImpl("Invalid string expression: " + expr);
		}
		
		int firstClose = expr.indexOf(")");
		
		if (firstClose == -1) {
			throw new EtlExceptionImpl("Invalid expression: " + expr);
		}
		
		String initialValue = expr.substring(1, firstClose);
		
		String remaining = expr.substring(firstClose + 1);
		
		this.transformerElements = StringTranformerElements.buildChain(initialValue, remaining,
		    (DstConf) getRelatedEtlTransformTarget(), conn);
	}
	
	public static String buildCacheKey(String transformationString) {
		return transformationString;
	}
	
	public static StringTranformer getInstance(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
	        TransformableField field, Connection conn) {
		String key = buildCacheKey(relatedEtlTransformTarget, field, parameters);
		
		return INSTANCES.computeIfAbsent(key, k -> {
			try {
				return new StringTranformer(parameters, relatedEtlTransformTarget, field, conn);
			}
			catch (FieldAvaliableInMultipleDataSources | DBException e) {
				throw new EtlExceptionImpl(e);
			}
		});
	}
	
	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
	        Connection srcConn, Connection dstConn) throws DBException, EtlTransformationException {
		
		if (additionalSrcObjects == null || additionalSrcObjects.isEmpty()) {
			throw new EtlTransformationException("StringTransformer requires at least one source object.", null, srcObject,
			        ActionOnEtlIssue.ABORT_PROCESS);
		}
		
		try {
			
			Object result = this.transformerElements.evaluate(additionalSrcObjects, srcConn);
			
			FieldTransformingInfo transformingInfo = new FieldTransformingInfo(field, result, null);
			
			transformingInfo.setLoadedWithDefaultValue(true);
			
			return transformingInfo;
			
		}
		catch (Exception e) {
			
			throw new EtlTransformationException("Failed to evaluate string expression: " + field.getValueToTransform(), e,
			        srcObject, ActionOnEtlIssue.ABORT_PROCESS);
		}
	}
	
}
