package org.openmrs.module.epts.etl.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.FieldsMappingIssues;

public class FieldsMappingException extends EtlExceptionImpl {

	private static final long serialVersionUID = 1505624913800886849L;

	public FieldsMappingException(String msg) {
		super(msg);
	}

	public FieldsMappingException(DstConf conf, FieldsMappingIssues mappingIssue) {
		super(generateIssueMsg(conf, mappingIssue));
	}

	static String generateIssueMsg(DstConf conf, FieldsMappingIssues mappingIssue) {

		String tableAlias = conf.getTableAlias();
		String configCode = conf.getParentConf().getConfigCode();
		String fullObjectName = configCode + " > " + tableAlias;

		List<String> messages = new ArrayList<>();

		if (mappingIssue == null) {
			return "Fields mapping issue detected for " + fullObjectName + System.lineSeparator()
					+ "- No detailed issue information was provided.";
		}

		if (!mappingIssue.getAvaliableInMultiDataSources().isEmpty()) {

			List<String> dstFields = mappingIssue.extractDstFieldInAvaliableInMultiDataSources();

			messages.add(buildFieldMessage(dstFields,
					"cannot be automatically mapped because the corresponding source field was found in multiple data sources.",
					"Configure the mapping manually or define the preferred data source order using the 'prefferredDataSource' array."));
		}

		if (!mappingIssue.getNotAvaliableInAnyDataSource().isEmpty()) {

			List<String> dstFields = mappingIssue.extractDstFieldInNotAvaliableInAnyDataSource();

			messages.add(buildFieldMessage(dstFields,
					"cannot be automatically mapped because no matching source field was found in any available data source.",
					"Configure the mapping manually."));
		}

		if (!mappingIssue.getNotAvaliableInSpecifiedDataSource().isEmpty()) {

			List<String> dstFields = mappingIssue.extractDstFieldInNotAvaliableInSpecifiedDataSource();

			messages.add(buildFieldMessage(dstFields,
					"cannot be mapped because the configured source field was not found in the specified data source.",
					"Check the source field name and the configured data source."));
		}

		if (messages.isEmpty()) {
			messages.add("- No field mapping issues were found, but FieldsMappingException was raised.");
		}

		return "Fields mapping issue detected for " + fullObjectName + System.lineSeparator()
				+ String.join(System.lineSeparator(), messages);
	}

	private static String buildFieldMessage(List<String> fields, String reason, String suggestion) {

		boolean single = fields != null && fields.size() == 1;

		String fieldLabel = single ? "Destination field" : "Destination fields";

		return "- " + fieldLabel + " " + fields + " " + reason + " " + suggestion;
	}
}