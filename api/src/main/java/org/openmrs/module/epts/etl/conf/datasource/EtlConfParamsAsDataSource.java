package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.exceptions.DatabaseResourceDoesNotExists;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectLoaderHelper;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public final class EtlConfParamsAsDataSource extends AbstractEtlDataConfiguration implements EtlDataSource {
	private final Object LOCK = new Object();

	private List<Field> fields;
	private Boolean fullLoaded;
	private EtlConfiguration relatedEtlConf;

	public EtlConfParamsAsDataSource(EtlConfiguration relatedEtlConf) {
		if (relatedEtlConf == null) {
			throw new ForbiddenOperationException("Empty relatedEtlConf was provided to an EtlConfParamsAsDataSource");
		}

		this.fullLoaded = false;
		this.relatedEtlConf = relatedEtlConf;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return relatedEtlConf;
	}

	@Override
	public Boolean isFullLoaded() {
		return this.fullLoaded;
	}

	@Override
	public void fullLoad() throws DBException {
		if (this.isFullLoaded()) {
			return;
		}

		synchronized (LOCK) {
			if (this.isFullLoaded()) {
				return;
			}

			if (this.getRelatedEtlConf().hasConfiguredParams()) {
				this.fields = new ArrayList<>();

				for (Entry<String, String> p : this.getRelatedEtlConf().getParams().entrySet()) {
					this.fields.add(Field.fastCreateWithValue("@" + p.getKey(), p.getValue()));
				}

			}

			this.fullLoaded = true;
		}

	}

	@Override
	public void fullLoad(Connection conn) throws DBException {
		this.fullLoad();
	}

	@Override
	public TableConfiguration findFullConfiguredConfInAllRelatedTable(String fullTableName,
			List<Integer> alreadyCheckedObjects) {

		return null;
	}

	@Override
	public String generateClassName() {
		return null;
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return this.getRelatedEtlConf();
	}

	@Override
	public String getObjectName() {
		return this.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Field> List<T> getFields() {
		return (List<T>) this.fields;
	}

	@Override
	public UniqueKeyInfo getPrimaryKey() {
		return null;
	}

	@Override
	public String getSharePkWith() {
		return null;
	}

	@Override
	public Boolean hasPK() {
		return false;
	}

	@Override
	public Boolean hasPK(Connection conn) throws DBException {
		return false;
	}

	@Override
	public Boolean isMetadata() {
		return false;
	}

	@Override
	public DBConnectionInfo getRelatedConnInfo() {
		return this.getRelatedEtlConf().getSrcConnInfo();
	}

	@Override
	public void setEtlRecordClass(Class<? extends EtlDatabaseObject> syncRecordClass) {
	}

	@Override
	public Boolean isDestinationInstallationType() {
		return false;
	}

	@Override
	public void generateRecordClass(DBConnectionInfo connInfo, Boolean fullClass) {
	}

	@Override
	public List<ParentTable> getParentRefInfo() {
		return null;
	}

	@Override
	public List<ChildTable> getChildRefInfo() {
		return null;
	}

	@Override
	public DatabaseObjectLoaderHelper getLoadHealper() {
		return null;
	}

	@Override
	public String getAlias() {
		return this.getName();
	}

	@Override
	public String generateSelectFromQuery() {
		return null;
	}

	@Override
	public List<String> getExcludedFieldsFromObjectDesc() {
		return null;
	}

	@Override
	public Boolean isMustLoadChildrenInfo() {
		return false;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}

	@Override
	public String getName() {
		return "etl_params_src_ds";
	}

	@Override
	public PreparedQuery getDefaultPreparedQuery() {
		throw new ForbiddenOperationException("Forbiden Method");
	}

	@Override
	public void setDefaultPreparedQuery(PreparedQuery defaultPreparedQuery) {
		throw new ForbiddenOperationException("Forbiden Method");
	}

	@Override
	public String getQuery() {
		return null;
	}

	@Override
	public void setParentConf(EtlDataConfiguration relatedParent) {
		this.relatedEtlConf = (EtlConfiguration) relatedParent;
	}

	@Override
	public void tryToLoadSchemaInfo(EtlDatabaseObject schemaInfoSrc, Connection conn)
			throws DBException, ForbiddenOperationException, DatabaseResourceDoesNotExists {
	}

	public Object getParamValue(String token) {
		if (!token.startsWith("@")) {
			token = "@" + token;
		}

		for (Field f : this.fields) {
			if (f.getName().equals(token)) {
				return f.getValue();
			}
		}

		throw new ForbiddenOperationException("Parameter " + token + " not found");
	}

}
