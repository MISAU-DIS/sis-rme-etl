package org.openmrs.module.epts.etl.model.pojo.generic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.exceptions.EtlException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.inconsistenceresolver.model.InconsistenceInfo;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Task-confined accumulator: one category per stable record key, latest update
 * wins. Getters return detached classification snapshots (empty, never null).
 * Domain records and diagnostic objects remain shared. Do not share across
 * writer threads.
 */
public class EtlOperationResultHeader<T extends EtlDatabaseObject> {

	private static CommonUtilities utilities = CommonUtilities.getInstance();

	private final Map<ResultRecordKey, EtlOperationResultItemType> categoryByRecord = new HashMap<>();
	private final Map<EtlDatabaseObject, ResultRecordKey> identityByInstance = new IdentityHashMap<>();
	private final EnumMap<EtlOperationResultItemType, LinkedHashMap<ResultRecordKey, EtlOperationItemResult<T>>> recordsByType = new EnumMap<>(
			EtlOperationResultItemType.class);

	private final List<EtlDatabaseObject> processedRecords = new ArrayList<>();

	private Exception fatalException;

	private IntervalExtremeRecord interval;

	public EtlOperationResultHeader(IntervalExtremeRecord interval) {
		this.interval = interval;
	}

	public EtlOperationResultHeader(IntervalExtremeRecord interval, Exception fatalException) {
		this.interval = interval;
		this.fatalException = fatalException;
	}

	public EtlOperationResultHeader(List<T> recordsWithNoError) {
		addSuccessfulRecords(recordsWithNoError);
	}

	public List<EtlDatabaseObject> getProcessedRecords() {
		return Collections.unmodifiableList(new ArrayList<>(processedRecords));
	}

	public void setProcessedRecords(List<EtlDatabaseObject> processedRecords) {
		this.processedRecords.clear();
		addProcessedRecords(processedRecords);
	}

	public List<EtlOperationItemResult<T>> getRecordsWithUnexpectedErrors() {
		return snapshot(EtlOperationResultItemType.UNEXPECTED_ERRORS);
	}

	public void setRecordsWithUnexpectedErrors(List<EtlOperationItemResult<T>> recordsWithUnexpectedErrors) {
		replaceCategory(EtlOperationResultItemType.UNEXPECTED_ERRORS, recordsWithUnexpectedErrors);
	}

	public List<EtlOperationItemResult<T>> getRecordsWithResolvedInconsistences() {
		return snapshot(EtlOperationResultItemType.RESOLVED_INCONSISTENCES);
	}

	public void setRecordsWithResolvedInconsistence(List<EtlOperationItemResult<T>> recordsWithResolvedInconsistence) {
		replaceCategory(EtlOperationResultItemType.RESOLVED_INCONSISTENCES, recordsWithResolvedInconsistence);
	}

	public List<EtlOperationItemResult<T>> getRecordsWithUnresolvedInconsistences() {
		return snapshot(EtlOperationResultItemType.UNRESOLVED_INCONSISTENCES);
	}

	public void setRecordsWithUnresolvedInconsistence(
			List<EtlOperationItemResult<T>> recordsWithUnresolvedInconsistence) {
		replaceCategory(EtlOperationResultItemType.UNRESOLVED_INCONSISTENCES, recordsWithUnresolvedInconsistence);
	}

	public List<EtlOperationItemResult<T>> getRecordsWithRecursiveRelashionship() {
		return snapshot(EtlOperationResultItemType.RECURSIVE_RELATIONSHIPS);
	}

	public void setRecordsWithRecursiveRelashionship(
			List<EtlOperationItemResult<T>> recordsWithRecursiveRelashionship) {
		replaceCategory(EtlOperationResultItemType.RECURSIVE_RELATIONSHIPS, recordsWithRecursiveRelashionship);
	}

	public List<EtlOperationItemResult<T>> getRecordsWithNoError() {
		return snapshot(EtlOperationResultItemType.NO_ERROR);
	}

	public void setRecordsWithNoError(List<EtlOperationItemResult<T>> recordsWithNoError) {
		replaceCategory(EtlOperationResultItemType.NO_ERROR, recordsWithNoError);
	}

	public Exception getFatalException() {
		return fatalException;
	}

	public void setFatalException(Exception fatalException) {
		this.fatalException = fatalException;
	}

	public IntervalExtremeRecord getInterval() {
		return interval;
	}

	public void setInterval(IntervalExtremeRecord interval) {
		this.interval = interval;
	}

	public boolean hasFatalException() {
		return getFatalException() != null;
	}

	public boolean hasRecordsWithUnexpectedErrors() {
		return !bucket(EtlOperationResultItemType.UNEXPECTED_ERRORS).isEmpty();
	}

	public boolean hasRecordsWithRecursiveRelashionships() {
		return !bucket(EtlOperationResultItemType.RECURSIVE_RELATIONSHIPS).isEmpty();
	}

	public boolean hasRecordsWithResolvedInconsistences() {
		return !bucket(EtlOperationResultItemType.RESOLVED_INCONSISTENCES).isEmpty();
	}

	public boolean hasRecordsWithUnresolvedInconsistences() {
		return !bucket(EtlOperationResultItemType.UNRESOLVED_INCONSISTENCES).isEmpty();
	}

	public boolean hasRecordsWithNoError() {
		return !bucket(EtlOperationResultItemType.NO_ERROR).isEmpty();
	}

	public boolean hasFatalError() throws DBException {

		if (getFatalException() != null) {
			return true;
		} else {

			return hasRecordsWithUnexpectedErrors();
		}
	}

	/**
	 * Incoming classifications win; the receiver retains its interval and
	 * processed-input scope. Its first fatal exception is preserved, or the
	 * incoming one adopted.
	 */
	public void addAllFromOtherResult(EtlOperationResultHeader<T> otherResult) {
		if (otherResult == null || otherResult == this)
			return;
		for (EtlOperationResultItemType type : EtlOperationResultItemType.values()) {
			for (Map.Entry<ResultRecordKey, EtlOperationItemResult<T>> entry : otherResult.bucket(type).entrySet()) {
				EtlOperationItemResult<T> item = entry.getValue();
				ResultRecordKey key = identityByInstance.get(item.getRecord());
				if (key == null)
					key = entry.getKey();
				put(key, item, type);
			}
		}
		if (fatalException == null)
			fatalException = otherResult.fatalException;
	}

	public void addSuccessfulRecords(Collection<? extends T> records) {
		if (records != null)
			for (T record : records)
				addToRecordsWithNoError(record);
	}

	public void addProcessedRecords(Collection<? extends EtlDatabaseObject> records) {
		if (records != null)
			processedRecords.addAll(records);
	}

	public void addAllToRecordsWithNoError(List<EtlOperationItemResult<T>> records) {
		addAll(records, EtlOperationResultItemType.NO_ERROR);
	}

	private void addToRecordsWithNoError(EtlOperationItemResult<T> record) {
		add(record, EtlOperationResultItemType.NO_ERROR);
	}

	private void addToRecordsWithUnresolvedErrors(EtlOperationItemResult<T> records) {
		add(records, EtlOperationResultItemType.UNEXPECTED_ERRORS);
	}

	public void addAllToRecordsWithResolvedInconsistences(EtlOperationItemResult<T> records) {
		add(records, EtlOperationResultItemType.RESOLVED_INCONSISTENCES);
	}

	public void addAllToRecordsWithUnresolvedInconsistences(EtlOperationItemResult<T> records) {
		add(records, EtlOperationResultItemType.UNRESOLVED_INCONSISTENCES);
	}

	public void addAllToRecordsWithRecursiveRelashionship(EtlOperationItemResult<T> records) {
		add(records, EtlOperationResultItemType.RECURSIVE_RELATIONSHIPS);
	}

	public void addToRecordsWithNoError(T record) {
		addToRecordsWithNoError(new EtlOperationItemResult<T>(record));
	}

	public void addToRecordsWithUnresolvedErrors(T record) {
		addToRecordsWithUnresolvedErrors(new EtlOperationItemResult<T>(record));
	}

	public void addToRecordsWithUnresolvedErrors(T records, EtlException e) {
		add(records, e);
	}

	public void addToRecordsWithRecursiveRelashionship(T record) {
		addAllToRecordsWithRecursiveRelashionship(new EtlOperationItemResult<T>(record));
	}

	public void addToRecordsWithResolvedErrors(T record, InconsistenceInfo i) {
		addOrUpdate(new EtlOperationItemResult<T>(record, i));
	}

	private void addAll(List<EtlOperationItemResult<T>> toAdd, EtlOperationResultItemType type) {
		if (utilities.listHasElement(toAdd)) {

			for (EtlOperationItemResult<T> rec : toAdd) {
				add(rec, type);
			}
		}
	}

	private void add(EtlOperationItemResult<T> record, EtlOperationResultItemType type) {
		ResultRecordKey key = identityByInstance.get(record.getRecord());
		if (key == null)
			key = ResultRecordKey.of(record.getRecord());
		put(key, record, type);
	}

	private void put(ResultRecordKey key, EtlOperationItemResult<T> record, EtlOperationResultItemType type) {
		EtlOperationItemResult<T> stored = record.copyWithType(type);
		EtlOperationResultItemType previous = categoryByRecord.put(key, type);
		if (previous != null)
			bucket(previous).remove(key);
		bucket(type).put(key, stored);
		identityByInstance.put(record.getRecord(), key);
	}

	private LinkedHashMap<ResultRecordKey, EtlOperationItemResult<T>> bucket(EtlOperationResultItemType type) {
		return recordsByType.computeIfAbsent(type, ignored -> new LinkedHashMap<>());
	}

	private List<EtlOperationItemResult<T>> snapshot(EtlOperationResultItemType type) {
		List<EtlOperationItemResult<T>> result = new ArrayList<>(bucket(type).size());
		for (EtlOperationItemResult<T> item : bucket(type).values())
			result.add(item.copyWithType(type));
		return Collections.unmodifiableList(result);
	}

	private void replaceCategory(EtlOperationResultItemType type, List<EtlOperationItemResult<T>> records) {
		for (ResultRecordKey key : bucket(type).keySet())
			categoryByRecord.remove(key);
		bucket(type).clear();
		addAll(records, type);
	}

	private void add(T record, EtlException e) {
		add(new EtlOperationItemResult<T>(record, e), EtlOperationResultItemType.UNEXPECTED_ERRORS);
	}

	public void addOrUpdate(EtlOperationItemResult<T> resultItem) {
		if (!resultItem.hasType())
			throw new ForbiddenOperationException("No type defined for item");

		add(resultItem, resultItem.getType());
	}

	public void documentErrors(Connection srcConn, Connection dstConn) throws DBException {
		EtlResultErrorDocumenter.document(getAllRecordsWithErros(), srcConn);
	}

	public void printStackErrorOfFatalErrors() {
		if (getFatalException() != null) {
			getFatalException().printStackTrace();
		} else {

			for (EtlOperationItemResult<T> r : getRecordsWithUnexpectedErrors()) {
				if (r.hasException()) {
					r.getException().printStackTrace();
				}
			}
		}
	}

	public void throwDefaultExcetions(Engine<T> engine) throws Exception {
		if (hasFatalException()) {

			String msg = "Error happened on etl '" + engine.getEngineId();

			if (getFatalException() instanceof EtlException) {
				EtlException e = (EtlException) getFatalException();

				if (e.getEtlObject() != null) {
					msg += " while processing object: " + e.getEtlObject();
				}
			}

			throw new EtlExceptionImpl(msg, getFatalException());
		}

		for (EtlOperationItemResult<T> o : getRecordsWithUnexpectedErrors()) {
			if (o.getException() != null) {
				try {
					throw o.getException().getException();
				} catch (Throwable e) {
					throw new RuntimeException(o.getException().getException());
				}
			}
		}

		throw new ForbiddenOperationException("No exception found");
	}

	/**
	 * Legacy input progress: processed inputs minus unresolved and unexpected
	 * results.
	 */
	public int countAllSuccessfulyProcessedRecords() {
		int allRecords = processedRecords.size();

		int recordsWithUnresolvedInconsistences = bucket(EtlOperationResultItemType.UNRESOLVED_INCONSISTENCES).size();
		int recordsWithUnexpectedErrors = bucket(EtlOperationResultItemType.UNEXPECTED_ERRORS).size();

		return allRecords - recordsWithUnresolvedInconsistences - recordsWithUnexpectedErrors;
	}

	/**
	 * Classified output successes, unlike the legacy input progress count above.
	 */
	public List<T> getAllSuccessfulyProcessedRecords() {
		List<T> success = new ArrayList<>(bucket(EtlOperationResultItemType.NO_ERROR).size()
				+ bucket(EtlOperationResultItemType.RESOLVED_INCONSISTENCES).size());
		for (EtlOperationItemResult<T> item : bucket(EtlOperationResultItemType.NO_ERROR).values())
			success.add(item.getRecord());
		for (EtlOperationItemResult<T> item : bucket(EtlOperationResultItemType.RESOLVED_INCONSISTENCES).values())
			success.add(item.getRecord());
		return success;
	}

	public static <T extends EtlDatabaseObject> boolean hasAtLeastOneFatalError(
			List<EtlOperationResultHeader<T>> results) throws DBException {

		for (EtlOperationResultHeader<T> result : results) {
			if (result.hasFatalError()) {
				return true;
			}
		}

		return false;

	}

	public static <T extends EtlDatabaseObject> EtlOperationResultHeader<T> getDefaultResultWithFatalError(
			List<EtlOperationResultHeader<T>> results) throws DBException {
		for (EtlOperationResultHeader<T> result : results) {
			if (result.hasFatalError()) {
				return result;
			}
		}

		return null;
	}

	public static <T extends EtlDatabaseObject> boolean hasAtLeastOneRecordsWithRecursiveRelashionships(
			List<EtlOperationResultHeader<T>> results) {

		for (EtlOperationResultHeader<T> result : results) {
			if (result.hasRecordsWithRecursiveRelashionships()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasRecordsWithErrors() {
		return hasRecordsWithResolvedInconsistences() || hasRecordsWithUnresolvedInconsistences()
				|| hasRecordsWithUnexpectedErrors();
	}

	public List<EtlOperationItemResult<T>> getAllRecordsWithErros() {

		List<EtlOperationItemResult<T>> recordsWithErros = new ArrayList<>();

		if (hasRecordsWithUnexpectedErrors()) {
			recordsWithErros.addAll(getRecordsWithUnexpectedErrors());
		}

		if (hasRecordsWithResolvedInconsistences()) {
			recordsWithErros.addAll(getRecordsWithResolvedInconsistences());
		}

		if (hasRecordsWithUnresolvedInconsistences()) {
			recordsWithErros.addAll(getRecordsWithUnresolvedInconsistences());
		}

		return recordsWithErros;
	}

	public List<T> getRecordsWithErrorsAsEtlDatabaseObject() {
		if (hasRecordsWithErrors())
			return EtlOperationItemResult.parseToEtlDatabaseObject(getAllRecordsWithErros());

		return null;
	}

}
