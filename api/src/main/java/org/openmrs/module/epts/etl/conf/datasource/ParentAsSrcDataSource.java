package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.ParentTableImpl;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlSrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class ParentAsSrcDataSource extends ParentTableImpl implements EtlAdditionalDataSource, EtlSrcConf {

	private PrimaryDataSourceConfiguration relatedPrimaryDataSource;

	public ParentAsSrcDataSource() {
	}

	@Override
	public String getName() {
		return getTableName();
	}

	@Override
	public PrimaryDataSourceConfiguration getRelatedPrimaryDataSource() {
		return this.relatedPrimaryDataSource;
	}

	@Override
	public void setRelatedPrimaryDataSource(PrimaryDataSourceConfiguration primaryDataSource) {
		this.relatedPrimaryDataSource = primaryDataSource;
	}

	@Override
	public EtlDatabaseObject loadRelatedSrcObject(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject dstObject,
			List<EtlDatabaseObject> avaliableSrcObjects, Connection srcConn)
			throws DBException {


		for (EtlDatabaseObject obj : avaliableSrcObjects) {
			if (obj.getRelatedConfiguration().getObjectName().equals(this.getChildTableConf().getName())) {
				return obj.getSharedPkObj();
			}
		}

		return null;
	}

	@Override
	public Boolean isRequired() {
		return true_();
	}

	public static ParentAsSrcDataSource generateFromSrcConfSharedPkParent(JdbcTablePrimaryDataSourceConfiguration mainSrcConf, ParentTable parent,
			Connection conn) throws ForbiddenOperationException, DBException {
		ParentAsSrcDataSource ds = new ParentAsSrcDataSource();

		ds.setChildTableConf(parent.getChildTableConf());

		ds.clone(parent, null, null, conn);

		ds.relatedPrimaryDataSource = mainSrcConf;
		if (mainSrcConf.getParentSource() != null) {
			ds.setResolvedResource(mainSrcConf.getParentSource().getResolvedResource());
		}

		ds.setRefCode(parent.getRefCode());
		ds.setRefMapping(parent.getRefMapping());

		ds.setConditionalFields(parent.getConditionalFields());
		ds.setDefaultValueDueInconsistency(parent.getDefaultValueDueInconsistency());
		ds.setSetNullDueInconsistency(parent.isSetNullDueInconsistency());

		return ds;
	}

	@Override
	public String getQuery() {
		return null;
	}

	@Override
	public String getCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
