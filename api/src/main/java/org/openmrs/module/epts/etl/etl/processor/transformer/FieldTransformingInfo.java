package org.openmrs.module.epts.etl.etl.processor.transformer;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

public class FieldTransformingInfo {

	private static CommonUtilities utilities = CommonUtilities.getInstance();

	private EtlDataSource transformationDatasource;

	private TransformableField srcField;

	private Object transformedValue;

	private boolean loadedWithDefaultValue;

	public FieldTransformingInfo(TransformableField srcField, Object transformedValue,
			EtlDataSource transformationDatasource) {

		this.srcField = srcField;
		this.transformationDatasource = transformationDatasource;
		this.transformedValue = transformedValue;

		tryToParseValue();
	}

	void tryToParseValue() {
		if (!this.srcField.hasDataType()) {
			return;
		}

		if (!this.srcField.hasTypeClass()) {
			this.srcField.determineTypeClass();
		}

		if (this.transformedValue != null) {
			this.transformedValue = utilities.parseValue(transformedValue, srcField.getTypeClass());
		}
	}

	public EtlDataSource getTransformationDatasource() {
		return transformationDatasource;
	}

	public void setTransformationDatasource(EtlDataSource transformationDatasource) {
		this.transformationDatasource = transformationDatasource;
	}

	public TransformableField getSrcField() {
		return srcField;
	}

	public void setSrcField(TransformableField srcField) {
		this.srcField = srcField;
	}

	public Object getTransformedValue() {
		return transformedValue;
	}

	public void setTransformedValue(Object transformedValue) {
		this.transformedValue = transformedValue;

		tryToParseValue();
	}

	public boolean loadedWithDefaultValue() {
		return loadedWithDefaultValue;
	}

	public boolean isLoadedWithDefaultValue() {
		return loadedWithDefaultValue;
	}

	public void setLoadedWithDefaultValue(boolean loadedWithDefaultValue) {
		this.loadedWithDefaultValue = loadedWithDefaultValue;
	}

	public boolean isLoadedWithDstValue(DBConnectionInfo dstConnInfo) {

		boolean isDstDs = false;

		if (this.getTransformationDatasource() != null) {
			if (this.getTransformationDatasource() instanceof DstConf) {
				isDstDs = true;
			} else {
				if (this.getTransformationDatasource() instanceof TableConfiguration) {
					TableConfiguration conf = (TableConfiguration) this.getTransformationDatasource();

					if (conf.hasSchema() && utilities.isStringIn(conf.getSchema(), dstConnInfo.getSchema())
							&& DBUtilities.isSameDatabaseServer(dstConnInfo, conf.getRelatedConnInfo())) {

						isDstDs = true;
					}
				}
			}
		}

		return this.loadedWithDefaultValue || isDstDs;

	}

	public boolean skipRelationshipResolution(DBConnectionInfo dstConnInfo) {
		return isLoadedWithDefaultValue() || isLoadedWithDstValue(dstConnInfo)
				|| srcField.relationshipResolutionStrategy().skip();
	}

	@Override
	public String toString() {
		return this.srcField.getSrcField() + " = " + this.getTransformedValue() + ". Ds: "
				+ this.transformationDatasource;
	}

}
