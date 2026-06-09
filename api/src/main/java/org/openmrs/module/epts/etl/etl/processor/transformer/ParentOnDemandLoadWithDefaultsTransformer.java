package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Specialized version of {@link ParentOnDemandLoadTransformer} that enables the use of default
 * field values when creating parent records on demand.
 * <p>
 * This transformer behaves like {@link ParentOnDemandLoadTransformer}, but automatically configures
 * the on-demand parent creation process to use default values for destination fields that were not
 * explicitly mapped.
 * </p>
 * <p>
 * Internally, this transformer sets the unmapped field behavior to {@code USE_DEFAULT}, allowing
 * values from the global {@code defaultFieldValues} configuration to be applied when required.
 * </p>
 * <p>
 * This is useful when creating placeholder or default parent records in the destination database,
 * especially when some required destination fields are not provided in the transformer
 * configuration.
 * </p>
 *
 * @see ParentOnDemandLoadTransformer
 */
public class ParentOnDemandLoadWithDefaultsTransformer extends ParentOnDemandLoadTransformer {
	
	protected static final Map<String, ParentOnDemandLoadWithDefaultsTransformer> INSTANCES = new ConcurrentHashMap<>();
	
	public ParentOnDemandLoadWithDefaultsTransformer(OnDemandInfo onDemandInfo, Connection conn)
	        throws FieldAvaliableInMultipleDataSources, DBException {
		
		super(onDemandInfo, conn);
	}
	
	public static ParentOnDemandLoadWithDefaultsTransformer getInstance(List<Object> parameters,
	        EtlTransformTarget relatedEtlTransformTarget, TransformableField field, Connection conn) {
		
		List<Object> effectiveParameters = new ArrayList<>();
		
		if (parameters != null) {
			
			for (Object parameter : parameters) {
				String value = parameter.toString().trim();
				
				if (value.startsWith("unmapped_field_behavior:")) {
					throw new EtlConfException("The parameter 'unmapped_field_behavior' cannot be specified on "
					        + "ParentOnDemandLoadWithDefaultsTransformer. "
					        + "This transformer always uses 'unmapped_field_behavior:USE_DEFAULT'.");
				}
				
				effectiveParameters.add(parameter);
			}
		}
		
		effectiveParameters.add("unmapped_field_behavior:USE_DEFAULT");
		
		String key = buildCacheKey(relatedEtlTransformTarget, field, effectiveParameters);
		
		return INSTANCES.computeIfAbsent(key, k -> {
			try {
				return new ParentOnDemandLoadWithDefaultsTransformer(
				        OnDemandInfo.create(effectiveParameters, (DstConf) relatedEtlTransformTarget, field, conn), conn);
			}
			catch (DBException e) {
				throw new EtlExceptionImpl(e);
			}
		});
	}
}
