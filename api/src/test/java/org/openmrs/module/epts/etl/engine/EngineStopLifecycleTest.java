package org.openmrs.module.epts.etl.engine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.TableOperationProgressInfo;

public class EngineStopLifecycleTest {

	@Test
	public void stopMustNotBecomeTerminalBeforeActiveExecutionCompletes() {
		Engine<EtlDatabaseObject> engine = newEngine();

		engine.beginExecutionLifecycle();
		engine.setOperationStatus(EtlOperationStatus.RUNNING);
		engine.requestStop();

		assertTrue(engine.stopRequested());
		assertTrue(engine.isStopping());
		assertFalse("STOPPED would allow the controller to interrupt the final flush", engine.isStopped());

		// The processing strategy returns only after workers, commit and auxiliary flush.
		engine.completeExecutionLifecycle();

		assertTrue(engine.isStopped());
	}

	@Test
	public void inactiveEngineMayStopImmediately() {
		Engine<EtlDatabaseObject> engine = newEngine();
		engine.setOperationStatus(EtlOperationStatus.RUNNING);

		engine.requestStop();

		assertTrue(engine.isStopped());
	}

	private Engine<EtlDatabaseObject> newEngine() {
		return new Engine<EtlDatabaseObject>(null, null, new TableOperationProgressInfo() {
			@Override
			public String getOperationId() {
				return "stop-lifecycle-test";
			}
		}) {
			@Override
			public void changeStatusToStopping() {
				setOperationStatus(EtlOperationStatus.STOPPING);
			}

			@Override
			public void changeStatusToStopped() {
				setOperationStatus(EtlOperationStatus.STOPPED);
			}
		};
	}
}
