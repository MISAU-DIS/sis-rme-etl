package org.openmrs.module.epts.etl.conf.types;

import org.openmrs.module.epts.etl.utilities.CommonUtilities;

public enum ConflictResolutionType {
	// @formatter:off
	
	
	NONE,
	
	/**
	 * Represents the applied resolution
	 */
	KEPT_EXISTING,
	
	PATCHED_EXISTING,
	
	/**
	 * Represents the applied resolution
	 */
	UPDATED_EXISTING,
	
	/**
	 * Represents the resolution to apply on conflict
	 */
	KEEP_EXISTING,
	
	/**
	 * Represents the resolution to apply on conflict
	 */
	UPDATE_EXISTING,
	
	/**
	 * Updates only a selected set of fields on an existing destination record when
	 * a conflict is detected.
	 *
	 * <p>
	 * This conflict resolution type is used when the destination record already exists
	 * and only specific fields should be updated instead of replacing or updating the
	 * entire record.
	 * </p>
	 *
	 * <p>
	 * The fields to be updated must be explicitly defined in the destination
	 * configuration, typically using the {@code patchFields} property.
	 * Fields not listed in {@code patchFields} remain unchanged in the destination
	 * record.
	 * </p>
	 *
	 * <p>
	 * This strategy is useful when the ETL process must enrich or correct selected
	 * attributes of an existing record without affecting other values that may have
	 * been previously migrated, manually edited, or maintained by another process.
	 * </p>
	 */
	PATCH_EXISTING,
	
	/**
	 * Represents the resolution to apply on conflict
	 */
	MAKE_YOUR_DECISION,
	
	REJECT;
	
	static final ConflictResolutionType[] ALLOWED_FOR_TABLE_CONF = {KEEP_EXISTING, UPDATE_EXISTING, PATCH_EXISTING, MAKE_YOUR_DECISION, REJECT};
	
	// @formatter:on
	public boolean keepExisting() {
		return this.equals(KEEP_EXISTING);
	}

	public boolean updateExisting() {
		return this.equals(UPDATE_EXISTING);
	}

	public boolean patchExisting() {
		return this.equals(PATCH_EXISTING);
	}

	public boolean makeYourDecision() {
		return this.equals(MAKE_YOUR_DECISION);
	}

	public boolean keptExisting() {
		return this.equals(KEPT_EXISTING);
	}

	public boolean none() {
		return this.equals(NONE);
	}

	public boolean isReject() {
		return this.equals(REJECT);
	}

	public boolean isAllowedForConfiguration() {
		return CommonUtilities.getInstance().existOnArray(ALLOWED_FOR_TABLE_CONF, this);
	}
}
