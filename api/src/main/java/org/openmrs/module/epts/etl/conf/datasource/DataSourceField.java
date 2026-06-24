package org.openmrs.module.epts.etl.conf.datasource;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.model.Field;

public class DataSourceField extends Field {

	private static final long serialVersionUID = -7824136202167355998L;

	private EtlDataSource parent;

	public DataSourceField() {
	}

	public static DataSourceField fastCreate(String name, Object value) {
		DataSourceField ds = new DataSourceField();

		ds.setValue(value);
		ds.setName(name);

		return ds;
	}

	public EtlDataSource getParent() {
		return parent;
	}

	public void setParent(EtlDataSource parent) {
		this.parent = parent;
	}

	@Override
	public void copyFrom(Field f) {
		super.copyFrom(f);

		if (f instanceof DataSourceField) {
			DataSourceField fDs = (DataSourceField) f;
			this.setExtension(fDs.getExtension());
		}
	}

	public static List<DataSourceField> cloneAll(List<DataSourceField> toCloneFrom, TransformableDataSource toCloneTo) {
		if (toCloneFrom == null)
			return null;

		List<DataSourceField> clonedItems = new ArrayList<>(toCloneFrom.size());

		for (DataSourceField dsF : toCloneFrom) {
			DataSourceField clonedItem = new DataSourceField();

			clonedItem.copyFrom(dsF);
			clonedItem.setParent(toCloneTo);

			clonedItems.add(clonedItem);
		}

		return clonedItems;
	}

}
