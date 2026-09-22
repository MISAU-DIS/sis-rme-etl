package org.openmrs.module.epts.etl.monitor;

import org.openmrs.module.epts.etl.monitor.model.EtlMonitorSnapshot;
import org.openmrs.module.epts.etl.monitor.model.TaskMonitorInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**

 * Represents the runtime monitoring state of a single ETL operation.
 *
 * <p>
 * This class is intentionally a simple state holder. It does not execute
 * the ETL and it does not calculate the progress by itself.
 * </p>
 *
 * <p>
 * The actual ETL execution is performed by {@code ProcessController}
 * and its engines. Monitoring components collect the current execution
 * information and update this object.
 * </p>
 *
 * <p>
 * One instance of this class represents one ETL operation.
 * </p>

 */
public class EtlMonitor {


    /**
     * Unique identifier of the ETL operation.
     */
    private final String operationId;

    /**
     * Time at which this monitoring instance was created.
     */
    private final Date startTime;

    /**
     * Latest aggregated snapshot of the ETL operation.
     *
     * volatile allows the monitoring/UI thread to see the latest value
     * written by the collector thread.
     */
    private volatile EtlMonitorSnapshot snapshot;

    /**
     * Current task information.
     *
     * The list is replaced atomically whenever a new task snapshot
     * is received.
     */
    private volatile List<TaskMonitorInfo> tasks =
            Collections.emptyList();

    /**
     * Creates a monitor for a new ETL operation.
     *
     * @param operationId unique operation identifier
     */
    public EtlMonitor(String operationId) {
        this.operationId = operationId;
        this.startTime = new Date();
    }

    /**
     * Returns the identifier of the monitored operation.
     *
     * @return operation identifier
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * Returns the time when monitoring started.
     *
     * @return monitoring start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Returns the latest ETL execution snapshot.
     *
     * @return latest snapshot, or {@code null} if no snapshot
     *         has been collected yet
     */
    public EtlMonitorSnapshot getSnapshot() {
        return snapshot;
    }

    /**
     * Updates the latest ETL execution snapshot.
     *
     * <p>
     * The monitor itself does not calculate this information.
     * A collector is responsible for creating the snapshot.
     * </p>
     *
     * @param snapshot new execution snapshot
     */
    public void updateSnapshot(EtlMonitorSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * Returns the current task information.
     *
     * <p>
     * The returned list is immutable.
     * </p>
     *
     * @return current task information
     */
    public List<TaskMonitorInfo> getTasks() {
        return tasks;
    }

    /**
     * Updates the current task information.
     *
     * <p>
     * A defensive copy is created so that changes to the original
     * list supplied by the collector do not affect the monitor.
     * The resulting list is immutable.
     * </p>
     *
     * @param tasks new task information
     */
    public synchronized void updateTasks(
            List<TaskMonitorInfo> tasks) {

        if (tasks == null || tasks.isEmpty()) {
            this.tasks = Collections.emptyList();
            return;
        }

        this.tasks = Collections.unmodifiableList(
                new ArrayList<TaskMonitorInfo>(tasks)
        );
    }


}
