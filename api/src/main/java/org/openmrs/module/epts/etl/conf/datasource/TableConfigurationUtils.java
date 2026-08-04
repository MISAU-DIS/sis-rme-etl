package org.openmrs.module.epts.etl.conf.datasource;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.JoinableEntity;
import org.openmrs.module.epts.etl.conf.interfaces.RelatedTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;

public class TableConfigurationUtils {

	public static List<EtlDataSource> retrieveAvaliableDataSourcesWithTable(TableConfiguration tab, Connection conn) {

		SrcConf srcConf = null;

		if (tab instanceof SrcConf) {
			srcConf = (SrcConf) tab;
		} else if (tab instanceof DstConf) {
			srcConf = ((DstConf) tab).getSrcConf();
		} else if (tab instanceof TableDataSourceConfig) {
			srcConf = ((TableDataSourceConfig) tab).getRelatedSrcConf();
		} else if (tab instanceof JoinableEntity) {
			return retrieveAvaliableDataSourcesWithTable(((JoinableEntity) tab).getMainExtractTable(), conn);
		} else if (tab instanceof RelatedTable) {
			return retrieveAvaliableDataSourcesWithTable(((RelatedTable) tab).getRelatedTabConf(), conn);
		}

		else if (tab.getParentConf() instanceof SrcConf) {
			srcConf = (SrcConf) tab.getParentConf();
		}

		if (srcConf != null) {
			return collectAllAvaliableDataSources(srcConf, conn);
		}

		return null;
	}

	private static List<EtlDataSource> collectAllAvaliableDataSources(SrcConf srcConf, Connection conn) {
		return srcConf.getParentConf().collectAllAvaliableDataSources(conn);
	}
}
