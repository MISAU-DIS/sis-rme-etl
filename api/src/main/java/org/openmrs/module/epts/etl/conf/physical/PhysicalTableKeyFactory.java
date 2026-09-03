package org.openmrs.module.epts.etl.conf.physical;

import java.sql.Connection;
import java.sql.SQLException;

import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;

/** Creates the same stable metadata key in discovery and generation flows. */
public final class PhysicalTableKeyFactory {

	private PhysicalTableKeyFactory() {
	}

	public static PhysicalTableKey create(TableConfiguration table, String configuredLogicalDatabaseId,
			Connection connection) throws SQLException {
		String logicalDatabaseId = configuredLogicalDatabaseId;
		if (!CommonUtilities.getInstance().stringHasValue(logicalDatabaseId)) {
			logicalDatabaseId = CommonUtilities.getInstance().stringHasValue(table.getCatalog(connection))
					? table.getCatalog(connection) : table.getSchema();
		}
		String dialect = connection.getMetaData().getDatabaseProductName().toLowerCase()
				.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
		return new PhysicalTableKey(logicalDatabaseId, dialect, table.getCatalog(connection), table.getSchema(),
				table.getTableName());
	}
}
