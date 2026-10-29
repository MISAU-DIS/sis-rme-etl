package org.openmrs.module.epts.etl.connector.file;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;

final class FileConnectorPathResolver {

	private FileConnectorPathResolver() {
	}

	static Path resolve(ConnectorResource resource) {
		String uri = resource.getEndpoint().getUri();
		if (uri == null || uri.trim().isEmpty()) {
			throw new IllegalArgumentException("FILE connector uri cannot be empty");
		}
		Path root = uri.startsWith("file:") ? Paths.get(URI.create(uri)) : Paths.get(uri);
		ConnectorResourceConfiguration configuration = resource.unwrap(ConnectorResourceConfiguration.class);
		Path resolved = root.resolve(configuration.getName()).normalize().toAbsolutePath();
		Path normalizedRoot = root.normalize().toAbsolutePath();
		if (!resolved.startsWith(normalizedRoot)) {
			throw new IllegalArgumentException("FILE resource must remain below connector uri: " + resource.getName());
		}
		return resolved;
	}
}
