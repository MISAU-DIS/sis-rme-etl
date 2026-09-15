package org.openmrs.module.epts.etl.model.pojo.generic;

import static org.junit.Assert.*;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.inconsistenceresolver.model.InconsistenceInfo;

public class EtlOperationResultHeaderTest {
	private final AtomicInteger equalsCalls = new AtomicInteger();

	private EtlOperationResultHeader<EtlDatabaseObject> header() {
		return new EtlOperationResultHeader<>((IntervalExtremeRecord) null);
	}

	private EtlDatabaseObject record(Oid oid, EtlDatabaseObjectConfiguration scope) {
		return (EtlDatabaseObject) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] {EtlDatabaseObject.class}, (proxy, method, args) -> {
					switch (method.getName()) {
						case "getObjectId": return oid;
						case "getRelatedConfiguration": return scope;
						case "equals": equalsCalls.incrementAndGet(); return proxy == args[0];
						case "hashCode": throw new AssertionError("Domain hashCode must not be used");
						default: return null;
					}
				});
	}

	private EtlDatabaseObject record(int id) { return record(Oid.fastCreate("id", id), null); }

	@Test public void distinctInstancesDeduplicateAndMoveBetweenAllCategories() {
		EtlOperationResultHeader<EtlDatabaseObject> result = header();
		for (EtlOperationResultItemType type : EtlOperationResultItemType.values()) {
			EtlOperationItemResult<EtlDatabaseObject> item = new EtlOperationItemResult<>(record(1));
			item.setType(type);
			result.addOrUpdate(item);
			assertEquals(1, total(result));
		}
		assertEquals(1, result.getRecordsWithUnresolvedInconsistences().size());
		assertEquals(0, equalsCalls.get());
	}

	private int total(EtlOperationResultHeader<EtlDatabaseObject> result) {
		return result.getRecordsWithNoError().size() + result.getAllRecordsWithErros().size()
				+ result.getRecordsWithRecursiveRelashionship().size();
	}

	@Test public void snapshotsAndMergeDoNotShareClassification() {
		EtlOperationResultHeader<EtlDatabaseObject> source = header(), target = header();
		EtlOperationItemResult<EtlDatabaseObject> item = new EtlOperationItemResult<>(record(1));
		source.addOrUpdate(item);
		item.setType(EtlOperationResultItemType.UNEXPECTED_ERRORS);
		target.addAllFromOtherResult(source);
		source.getRecordsWithNoError().get(0).setType(EtlOperationResultItemType.UNEXPECTED_ERRORS);
		assertEquals(EtlOperationResultItemType.NO_ERROR, source.getRecordsWithNoError().get(0).getType());
		target.addOrUpdate(item);
		assertTrue(source.hasRecordsWithNoError());
		assertFalse(target.hasRecordsWithNoError());
		target.addAllFromOtherResult(target);
		assertEquals(1, total(target));
		assertThrows(UnsupportedOperationException.class, () -> source.getRecordsWithNoError().clear());
	}

	@Test public void keySurvivesIdMutationAndMerge() {
		Oid oid = Oid.fastCreate("id", 1);
		EtlDatabaseObject record = record(oid, null);
		EtlOperationResultHeader<EtlDatabaseObject> source = header(), target = header();
		source.addToRecordsWithNoError(record);
		oid.getFields().get(0).setValue(2);
		source.addToRecordsWithUnresolvedErrors(record);
		target.addAllFromOtherResult(source);
		target.addToRecordsWithNoError(record(1));
		assertEquals(1, total(source));
		assertEquals(1, total(target));
		assertFalse(target.hasRecordsWithUnexpectedErrors());
	}

	@Test public void incompleteKeysUseInstanceIdentity() {
		EtlOperationResultHeader<EtlDatabaseObject> result = header();
		EtlDatabaseObject a = record(null, null), b = record(null, null);
		result.addSuccessfulRecords(Arrays.asList(a, b, a));
		assertEquals(2, total(result));
	}

	@Test public void compositeKeysIgnoreFieldOrderAndNumericWrapper() {
		EtlOperationResultHeader<EtlDatabaseObject> result = header();
		result.addToRecordsWithNoError(record(Oid.fastCreate("id", 1, "site", "a"), null));
		result.addToRecordsWithNoError(record(Oid.fastCreate("site", "a", "id", 1L), null));
		result.addToRecordsWithNoError(record(Oid.fastCreate("site", "b", "id", 1), null));
		assertEquals(2, total(result));
	}

	@Test public void configurationScopesDoNotCollide() {
		EtlOperationResultHeader<EtlDatabaseObject> result = header();
		for (int i = 0; i < 2; i++) {
			EtlDatabaseObjectConfiguration scope = (EtlDatabaseObjectConfiguration) Proxy.newProxyInstance(
					getClass().getClassLoader(), new Class<?>[] {EtlDatabaseObjectConfiguration.class},
					(proxy, method, args) -> null);
			result.addToRecordsWithNoError(record(Oid.fastCreate("id", 1), scope));
		}
		assertEquals(2, total(result));
	}

	@Test public void replacementAndReinsertionKeepOrderAndExclusiveMembership() {
		EtlOperationResultHeader<EtlDatabaseObject> result = header();
		EtlDatabaseObject a = record(1), b = record(2);
		result.addSuccessfulRecords(Arrays.asList(a, b, a));
		assertSame(b, result.getRecordsWithNoError().get(0).getRecord());
		result.addToRecordsWithUnresolvedErrors(a);
		result.setRecordsWithNoError(Arrays.asList(new EtlOperationItemResult<>(a)));
		assertEquals(1, total(result));
		result.setRecordsWithNoError(null);
		assertEquals(0, total(result));
	}

	@Test public void mergeKeepsInputScopeAndFirstFatalException() {
		EtlOperationResultHeader<EtlDatabaseObject> a = header(), b = header();
		Exception first = new Exception("first");
		a.addProcessedRecords(Arrays.asList(record(1), record(2)));
		b.setFatalException(first);
		b.addToRecordsWithUnresolvedErrors(record(1));
		a.addAllFromOtherResult(b);
		a.addAllFromOtherResult(b);
		b.setFatalException(new Exception("second"));
		a.addAllFromOtherResult(b);
		assertSame(first, a.getFatalException());
		assertEquals(1, a.countAllSuccessfulyProcessedRecords());
		assertEquals(2, a.getProcessedRecords().size());
	}

	@Test public void singleInconsistenceConstructorClassifiesImmediately() {
		EtlOperationItemResult<EtlDatabaseObject> item = new EtlOperationItemResult<>(record(1), new InconsistenceInfo() {
			@Override public Object getDefaultParentId() { return null; }
		});
		assertEquals(EtlOperationResultItemType.UNRESOLVED_INCONSISTENCES, item.getType());
	}

	@Test public void largeBatchNeverUsesDomainEquality() {
		EtlOperationResultHeader<EtlDatabaseObject> result = header();
		java.util.List<EtlOperationItemResult<EtlDatabaseObject>> batch = new java.util.ArrayList<>();
		for (int i = 0; i < 100000; i++) batch.add(new EtlOperationItemResult<>(record(i)));
		result.addAllToRecordsWithNoError(batch);
		assertEquals(100000, result.getRecordsWithNoError().size());
		assertEquals(0, equalsCalls.get());
	}

	@Test public void documentsTheActualInconsistences() throws Exception {
		AtomicInteger saves = new AtomicInteger();
		EtlOperationResultHeader<EtlDatabaseObject> result = header();
		result.addOrUpdate(new EtlOperationItemResult<>(record(1), new InconsistenceInfo() {
			@Override public Object getDefaultParentId() { return null; }
			@Override public void save(org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration conf,
					java.sql.Connection conn) { saves.incrementAndGet(); }
		}));
		result.documentErrors(null, null);
		assertEquals(1, saves.get());
	}

	/** Optional diagnostic, without timing thresholds: -Detl.result.benchmark=true. */
	@Test public void benchmarkBulkInsertionAndUpdate() {
		org.junit.Assume.assumeTrue(Boolean.getBoolean("etl.result.benchmark"));
		com.sun.management.ThreadMXBean bean = (com.sun.management.ThreadMXBean)
				java.lang.management.ManagementFactory.getThreadMXBean();
		for (int n : new int[] {10000, 100000, 1000000}) {
			java.util.List<EtlOperationItemResult<EtlDatabaseObject>> batch = new java.util.ArrayList<>(n);
			for (int i = 0; i < n; i++) batch.add(new EtlOperationItemResult<>(record(i)));
			EtlOperationResultHeader<EtlDatabaseObject> result = header();
			long allocated = bean.getThreadAllocatedBytes(Thread.currentThread().getId());
			long start = System.nanoTime();
			result.addAllToRecordsWithNoError(batch);
			long insert = System.nanoTime() - start;
			long bytes = bean.getThreadAllocatedBytes(Thread.currentThread().getId()) - allocated;
			start = System.nanoTime();
			result.addAllToRecordsWithNoError(batch);
			long update = System.nanoTime() - start;
			System.out.printf("RESULT_BENCH n=%d insert_ms=%.1f update_ms=%.1f insert_allocated_MiB=%.1f%n",
					n, insert / 1e6, update / 1e6, bytes / 1048576.0);
			assertEquals(n, result.getRecordsWithNoError().size());
		}
		assertEquals(0, equalsCalls.get());
	}
}
