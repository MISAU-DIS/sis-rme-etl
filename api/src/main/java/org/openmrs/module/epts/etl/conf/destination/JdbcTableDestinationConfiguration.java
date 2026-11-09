package org.openmrs.module.epts.etl.conf.destination;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.connector.ConnectorWriteMode;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** JDBC table destination variant backed by the current {@link DstConf}. */
public class JdbcTableDestinationConfiguration extends DstConf implements DestinationDataConfiguration {

	@JsonIgnore
	private transient EtlDestinationConfiguration parentDestination;

	public JdbcTableDestinationConfiguration() {
	}

	public JdbcTableDestinationConfiguration(String tableName) {
		super(tableName);
	}

	@Override
	public EtlDestinationConfiguration getParentDestination() {
		return parentDestination;
	}

	@Override
	public void setParentDestination(EtlDestinationConfiguration parentDestination) {
		this.parentDestination = parentDestination;
		pullConfigurationFromParentDestination();
	}

	@Override
	public Set<ConnectorWriteMode> getSupportedWriteModes() {
		return EnumSet.allOf(ConnectorWriteMode.class);
	}

	/** A JDBC destination is intrinsically a database destination. */
	@Override
	public EtlDstType getDstType() {
		EtlDstType configured = super.getDstType();
		return configured == null ? EtlDstType.db : configured;
	}

	@Override
	public void fullLoad() throws DBException {
		pullConfigurationFromParentDestination();
		super.fullLoad();
	}

	@Override
	public void fullLoad(Connection conn) throws DBException {
		pullConfigurationFromParentDestination();
		super.fullLoad(conn);
	}

	/** Imports the logical rules owned by the connector-neutral destination. */
	void pullConfigurationFromParentDestination() {
		if (parentDestination == null) {
			return;
		}

		if (parentDestination.getFields() != null) {
			setFields(copy(parentDestination.getFields()));
		}
		if (parentDestination.getPrimaryKey() != null) {
			setPrimaryKey(parentDestination.getPrimaryKey());
		}

		setJoinFields(copy(parentDestination.getJoinFields()));
		setGeneratedJoinFields(copyNested(parentDestination.getGeneratedJoinFields()));
		setMapping(copy(parentDestination.getMapping()));
		setAllMapping(copy(parentDestination.getAllMapping()));
		setExcludedFields(copy(parentDestination.getExcludedFields()));
		setIgnorableFields(copy(parentDestination.getIgnorableFields()));
		setPatchFields(copy(parentDestination.getPatchFields()));
		setPreferredDataSource(copy(parentDestination.getPreferredDataSource()));
		setUnmappedFieldBehavior(parentDestination.getUnmappedFieldBehavior());
		setTransformer(parentDestination.getTransformer());
		setTransformerInstance(parentDestination.getTransformerInstance());
		setIncludeAllFieldsFromDataSource(parentDestination.getIncludeAllFieldsFromDataSource());
		setSrcObjectCondition(parentDestination.getApplyCondition());
		setOnMultipleDataSourceForSameMapping(parentDestination.getOnMultipleDataSourceForSameMapping());
		setOnMultipleDataSourceWithSameName(parentDestination.getOnMultipleDataSourceWithSameName());
		setUseAsDataSource(parentDestination.getUseAsDataSource());
		setIgnoreNoDstIssue(parentDestination.getIgnoreNoDstIssue());
		setMappingResolutionStrategy(parentDestination.getMappingResolutionStrategy());
		setOnMissingRequiredSrcObject(parentDestination.getOnMissingRequiredSrcObject());
		setAllowDuplicateDestinationMappings(parentDestination.getAllowDuplicateDestinationMappings());
		setIdentifiedRecordAction(parentDestination.getIdentifiedRecordAction());
		setIncompleteMappingBehavior(parentDestination.getIncompleteMappingBehavior());
		setDisabled(parentDestination.getDisabled());
	}

	private <T> List<T> copy(List<T> values) {
		return values == null ? null : new ArrayList<>(values);
	}

	private List<List<FieldsMapping>> copyNested(List<List<FieldsMapping>> values) {
		if (values == null) {
			return null;
		}
		List<List<FieldsMapping>> copied = new ArrayList<>();
		for (List<FieldsMapping> value : values) {
			copied.add(copy(value));
		}
		return copied;
	}
}
