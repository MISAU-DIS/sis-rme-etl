package org.openmrs.module.epts.etl.conf.physical;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

/** Computes a stable SHA-256 fingerprint of a physical metadata snapshot. */
public final class PhysicalTableMetadataFingerprint {

	private PhysicalTableMetadataFingerprint() {
	}

	public static String sha256(PhysicalTableMetadata metadata) throws IOException {
		try {
			byte[] serialized = new ObjectMapperProvider().getContext(PhysicalTableMetadata.class)
					.writeValueAsBytes(metadata);
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(serialized);
			StringBuilder value = new StringBuilder(digest.length * 2);
			for (byte element : digest) value.append(String.format("%02x", element & 0xff));
			return value.toString();
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is not available", exception);
		}
	}
}
