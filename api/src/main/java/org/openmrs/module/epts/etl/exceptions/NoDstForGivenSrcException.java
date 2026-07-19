package org.openmrs.module.epts.etl.exceptions;

import java.util.List;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public class NoDstForGivenSrcException extends EtlTransformationException {

	private static final long serialVersionUID = 1L;

	public NoDstForGivenSrcException(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> srcObjects) {
		super("No dst was found for given src objects: " + srcObjects.get(0).getRelatedConfiguration().getAlias() + ":["
				+ retrieveObjectIds(srcObjects) + "]" + "\nConfigured Dst: " + loadConfiguredDst(etlItemConf));
	}

	private static String loadConfiguredDst(EtlItemConfiguration etlItemConf) {
		String msg = "";
		for (DstConf dstConf : etlItemConf.getDstConf()) {
			msg += (msg.isEmpty() ? "" : "\n") + dstConf;
		}

		return msg;
	}

	private static String retrieveObjectIds(List<EtlDatabaseObject> srcObjects) {
		String str = "";

		for (EtlDatabaseObject o : srcObjects) {
			str += "\n" + o.getObjectId().asSimpleValue();
		}

		return str;

	}
}
