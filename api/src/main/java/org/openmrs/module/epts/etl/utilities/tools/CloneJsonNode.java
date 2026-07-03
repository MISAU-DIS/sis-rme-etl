package org.openmrs.module.epts.etl.utilities.tools;

import java.io.File;

import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CloneJsonNode {
	public static String replicateMapping(String mappingToReplicate, String[] replicas) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			String json = FileUtilities
					.realAllFileAsString(new File("/home/jpboane/prg/php/workspace/misau/bkps/mapping.json"));

			JsonNode root = mapper.readTree(json);
			JsonNode mapArray = root.get("map");

			if (mapArray == null || !mapArray.isArray()) {
				throw new RuntimeException("Invalid mapping structure: 'map' not found");
			}

			JsonNode originalMapping = null;

			for (JsonNode node : mapArray) {
				if (mappingToReplicate.equals(node.get("mapping").asText())) {
					originalMapping = node;
					break;
				}
			}

			if (originalMapping == null) {
				throw new RuntimeException("Mapping not found: " + mappingToReplicate);
			}

			ArrayNode resultArray = mapper.createArrayNode();

			// 🔹 2. Criar replicas
			for (String replica : replicas) {

				ObjectNode newNode = mapper.createObjectNode();

				newNode.put("mapping", replica);

				// copia profunda do array mapped
				newNode.set("mapped", originalMapping.get("mapped").deepCopy());

				resultArray.add(newNode);
			}

			// 🔹 3. Retornar como JSON string (sem colchetes externos, se quiser igual ao
			// exemplo)
			String fullJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultArray);

			// opcional: remover [ ] para ficar como no teu exemplo
			return fullJson.substring(1, fullJson.length() - 1).trim();

		} catch (Exception e) {
			throw new RuntimeException("Error replicating mapping", e);
		}
	}
}
