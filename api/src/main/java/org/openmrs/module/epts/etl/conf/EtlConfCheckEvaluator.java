package org.openmrs.module.epts.etl.conf;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.types.EtlConfCheckType;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Evaluates configuration-level checks that can be used inside ETL conditional
 * expressions.
 *
 * <p>
 * This evaluator allows conditions to be based not only on source object
 * values, but also on the structure or state of ETL configuration elements such
 * as source configurations, destination configurations, or auxiliary data
 * sources.
 * </p>
 *
 * <p>
 * Typical use cases include checking whether a data source exposes a given
 * field, counting available fields, or verifying whether a configuration-backed
 * data source contains records.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * ETL_CONF_CHECK(conf:patient_src_ds, operation:HAS_FIELD, field:location_id)
 * </pre>
 *
 * <p>
 * This expression evaluates to {@code true} when the data source identified by
 * {@code patient_src_ds} contains the field {@code location_id}.
 * </p>
 */
public class EtlConfCheckEvaluator {

	public Object evaluate(EtlConfCheckExpression expression) throws EtlTransformationException, DBException {

		EtlDataConfiguration conf = expression.getRelatedConfiguration();

		if (conf == null) {

			if (expression.getOperation() == EtlConfCheckType.EXISTS) {
				return false;
			}

			if (expression.getOperation() == EtlConfCheckType.DOES_NOT_EXIST) {
				return true;
			}

			throw new EtlConfException("ETL configuration check failed. Etl Confuiguration '" + expression.getConfName()
					+ "' was not found.");
		}

		if (!(conf instanceof EtlDataSource))
			throw new EtlConfException(
					"Unsupported Etl Configuration for ETL_CONF_CHECK " + conf.getClass().getCanonicalName());

		EtlDataSource ds = (EtlDataSource) conf;

		switch (expression.getOperation()) {

		case HAS_FIELD:
			return ds.containsField(expression.getField());

		case COUNT_FIELDS:
			return ds.getFields().size();

		default:
			throw new EtlConfException("Unsupported ETL configuration check type: " + expression.getOperation());
		}
	}
}