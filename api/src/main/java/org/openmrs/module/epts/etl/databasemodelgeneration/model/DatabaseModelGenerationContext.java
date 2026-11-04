package org.openmrs.module.epts.etl.databasemodelgeneration.model;

import java.sql.Connection;

import org.openmrs.module.epts.etl.conf.DataModelConfiguration;

/**
 * Explicit context of one side of data-model generation. Model identity and
 * POJO package are deliberately independent from the physical JDBC connection.
 */
public final class DatabaseModelGenerationContext {
	private final Side side;
	private final String dataModelId;
	private final String pojoPackageName;
	private final Connection connection;

	private DatabaseModelGenerationContext(Side side, String dataModelId, String pojoPackageName,
			Connection connection) {
		this.side = side;
		this.dataModelId = dataModelId;
		this.pojoPackageName = pojoPackageName;
		this.connection = connection;
	}

	public static DatabaseModelGenerationContext source(DataModelConfiguration dataModel, Connection connection) {
		return new DatabaseModelGenerationContext(Side.SRC, dataModel.getSrcDataModelId(),
				dataModel.getSrcPojoPackageName(), connection);
	}

	public static DatabaseModelGenerationContext destination(DataModelConfiguration dataModel, Connection connection) {
		return new DatabaseModelGenerationContext(Side.DST, dataModel.getDstDataModelId(),
				dataModel.getDstPojoPackageName(), connection);
	}

	public static DatabaseModelGenerationContext legacy(Side side, String dataModelId, String pojoPackageName,
			Connection connection) {
		return new DatabaseModelGenerationContext(side, dataModelId, pojoPackageName, connection);
	}

	public Side getSide() {
		return side;
	}

	public String getDataModelId() {
		return dataModelId;
	}

	public String getPojoPackageName() {
		return pojoPackageName;
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public String toString() {
		return "DatabaseModelGenerationContext [side=" + side + ", dataModelId=" + dataModelId + ", pojoPackageName="
				+ pojoPackageName + "]";
	}

	public enum Side {
		SRC, DST
	}
}
