package org.openmrs.module.epts.etl.model.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Allow the load of additional data to a {@link VO} when it is loaded from
 * database
 */
public interface VOLoaderHelper {

	/**
	 * Load the additional data to the vo object passed by parameter before the load
	 * of object from database
	 * 
	 * @param vo the vo object to load data to
	 */
	void beforeLoad(ResultSet rs, VO vo);

	/**
	 * Loads contextual data that depends on columns from the current result-set.
	 * Implementations that do not need that context retain the original behavior.
	 */
	void afterLoad(ResultSet rs, VO vo) throws SQLException;
}
