package org.openmrs.module.epts.etl.model.pojo.generic;
import java.sql.Connection;
import java.util.List;
import org.openmrs.module.epts.etl.conf.EtlConfigurationTableConf;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.inconsistenceresolver.model.InconsistenceInfo;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** Database persistence kept outside the in-memory result accumulator. */
final class EtlResultErrorDocumenter {
	private EtlResultErrorDocumenter() {}
	static <T extends EtlDatabaseObject> void document(List<EtlOperationItemResult<T>> toDocument, Connection srcConn) throws DBException {


		for (EtlOperationItemResult<T> r : toDocument) {
			if (r.hasInconsistences()) {
				for (InconsistenceInfo i : r.getInconsistenceInfo()) {
					i.save((TableConfiguration) r.getRecord().getRelatedConfiguration(), srcConn);
				}
			} else if (r.hasException()) {
				EtlConfigurationTableConf etlErr = r.getRecord().getRelatedConfiguration().getRelatedEtlConf()
				        .getEtlRecordErrorTabCof();

				if (!etlErr.isFullLoaded()) {
					etlErr.setTableAlias(etlErr.getTableName());
					etlErr.fullLoad(srcConn);
				}

				EtlDatabaseObject obj = etlErr.createRecordInstance();
				obj.setRelatedConfiguration(etlErr);

				obj.setFieldValue("record_id", r.getRecord().getObjectId().getSimpleValue());
				obj.setFieldValue("table_name", r.getRecord().generateTableName());
				obj.setFieldValue("origin_location_code",
				    r.getRecord().getRelatedConfiguration().getRelatedEtlConf().getOriginAppLocationCode());
				obj.setFieldValue("exception", r.getException().getClass().getName());
				obj.setFieldValue("exception_description", r.getException().getLocalizedMessage());
				obj.setFieldValue("table_name", r.getRecord().generateTableName());

				obj.save(etlErr, srcConn);
			}
		}

	}
}
