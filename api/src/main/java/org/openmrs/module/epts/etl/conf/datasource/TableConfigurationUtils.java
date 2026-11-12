package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.JdbcTableDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.JoinableEntity;
import org.openmrs.module.epts.etl.conf.interfaces.RelatedTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;

public class TableConfigurationUtils {

	public static List<EtlDataSource> retrieveAvaliableDataSourcesWithTable(TableConfiguration tab, Connection conn) {

		JdbcTablePrimaryDataSourceConfiguration jdbcPrimary = null;

		if (tab instanceof JdbcTablePrimaryDataSourceConfiguration) {
			jdbcPrimary = (JdbcTablePrimaryDataSourceConfiguration) tab;
		} else if (tab instanceof JdbcTableDestinationConfiguration) {
			jdbcPrimary = ((JdbcTableDestinationConfiguration) tab).getPrimaryDataSource().requireJdbc();
		} else if (tab instanceof TableDataSourceConfig) {
			jdbcPrimary = ((TableDataSourceConfig) tab).getRelatedPrimaryDataSource().requireJdbc();
		} else if (tab instanceof JoinableEntity) {
			return retrieveAvaliableDataSourcesWithTable(((JoinableEntity) tab).getMainExtractTable(), conn);
		} else if (tab instanceof RelatedTable) {
			return retrieveAvaliableDataSourcesWithTable(((RelatedTable) tab).getRelatedTabConf(), conn);
		}

		else if (tab.getParentConf() instanceof JdbcTablePrimaryDataSourceConfiguration) {
			jdbcPrimary = (JdbcTablePrimaryDataSourceConfiguration) tab.getParentConf();
		}

		if (jdbcPrimary != null) {
			return collectAllAvaliableDataSources(jdbcPrimary, conn);
		}

		return null;
	}

	private static List<EtlDataSource> collectAllAvaliableDataSources(
			JdbcTablePrimaryDataSourceConfiguration jdbcPrimary, Connection conn) {
		return jdbcPrimary.getParentConf().collectAllAvaliableDataSources(conn);
	}
}
