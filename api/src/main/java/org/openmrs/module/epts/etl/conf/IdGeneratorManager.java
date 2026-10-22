package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.base.EtlObject;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * This class represents a generator of Ids for specific engine
 */
public class IdGeneratorManager extends AbstractEtlDataConfiguration {

	private long lastGeneratedId;

	private long maxAllowedId;

	private TaskProcessor<? extends EtlDatabaseObject> processor;

	private DstConf dstConf;

	private List<? extends EtlObject> etlObjects;

	private IdGeneratorManager(DstConf dstConf, TaskProcessor<? extends EtlDatabaseObject> processor,
			List<? extends EtlObject> etlObjects) {
		this.dstConf = dstConf;
		this.processor = processor;
		this.etlObjects = etlObjects;
	}

	private IdGeneratorManager(DstConf dstConf, TaskProcessor<? extends EtlDatabaseObject> processor) {
		this.dstConf = dstConf;
		this.processor = processor;
		this.maxAllowedId = -1;
	}

	public List<? extends EtlObject> getEtlObjects() {
		return etlObjects;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IdGeneratorManager)) {
			return false;
		}

		IdGeneratorManager other = (IdGeneratorManager) obj;

		return this.dstConf == other.dstConf && this.processor == other.processor;
	}

	public DstConf getDstConf() {
		return dstConf;
	}

	private void setLastGeneratedId(long lastGeneratedId) {
		this.lastGeneratedId = lastGeneratedId;

		if (this.getEtlObjects() != null) {
			this.maxAllowedId = this.lastGeneratedId + getQtyRecords();
		} else {
			this.maxAllowedId = -1;
		}
	}

	public int getQtyRecords() {
		return utilities.arraySize(this.getEtlObjects());
	}

	public static void init(TaskProcessor<? extends EtlDatabaseObject> processor, DstConf dstConf,
			List<? extends EtlObject> etlObjects, Connection conn) throws DBException, ForbiddenOperationException {

		IdGeneratorManager newGenerator = new IdGeneratorManager(dstConf, processor, etlObjects);

		newGenerator.setLastGeneratedId(newGenerator.getDstConf().determineNextStartId(newGenerator, conn));

		processor.addIdGeneratorManager(newGenerator);
	}

	public static void init(TaskProcessor<? extends EtlDatabaseObject> processor, DstConf dstConf, Connection conn)
			throws DBException, ForbiddenOperationException {

		IdGeneratorManager newGenerator = new IdGeneratorManager(dstConf, processor);

		newGenerator.setLastGeneratedId(newGenerator.getDstConf().determineNextStartId(newGenerator, conn));

		processor.addIdGeneratorManager(newGenerator);
	}

	public synchronized void reset(long lastGeneratedId) throws ForbiddenOperationException {
	}

	private String getDstFullTableName() {
		return dstConf.getFullTableName();
	}

	public synchronized long retriveNextIdForRecord() throws ForbiddenOperationException {
		long id = ++lastGeneratedId;

		if (this.maxAllowedId > 0 && id > this.maxAllowedId) {
			throw new ForbiddenOperationException("Max allowed Id reached for IdGeneratorManager + '"
					+ getDstFullTableName() + "' [maxAllowed: " + this.maxAllowedId + ", curr: " + id);
		}

		return id;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.dstConf != null ? this.dstConf.getRelatedEtlConf() : null;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.dstConf;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {

	}

}
