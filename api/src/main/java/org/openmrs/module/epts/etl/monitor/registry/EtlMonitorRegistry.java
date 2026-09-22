
package org.openmrs.module.epts.etl.monitor.registry;

import org.openmrs.module.epts.etl.monitor.EtlMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry of active ETL monitors.
 *
 * <p>
 * The registry keeps the monitoring state of ETL operations in memory.
 * Each ETL operation is identified by its unique operation ID.
 * </p>
 *
 * <p>
 * The registry is shared by the ETL execution layer and the OpenMRS
 * monitoring layer. This allows both sides to work with the same
 * {@link EtlMonitor} instance instead of maintaining separate copies
 * of the execution state.
 * </p>
 *
 * <p>
 * A {@link ConcurrentHashMap} is used because ETL execution and
 * monitoring may happen in different threads.
 * </p>
 */
public class EtlMonitorRegistry {

    /*
     * Single registry instance used by the ETL application.
     */
    private static final EtlMonitorRegistry INSTANCE =
            new EtlMonitorRegistry();

    /*
     * Stores monitors indexed by their operation ID.
     *
     * Example:
     *
     *     operationId -> EtlMonitor
     */
    private final Map<String, EtlMonitor> monitors =
            new ConcurrentHashMap<String, EtlMonitor>();

    /*
     * Private constructor prevents external instantiation.
     */
    private EtlMonitorRegistry() {
    }

    /**
     * Returns the shared registry instance.
     *
     * @return registry singleton
     */
    public static EtlMonitorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the number of registered monitors.
     *
     * @return number of monitors
     */
    public int size() {
        return monitors.size();
    }

    /**
     * Checks whether a monitor exists for the given operation.
     *
     * @param operationId ETL operation identifier
     * @return true if a monitor exists
     */
    public boolean exists(String operationId) {

        return operationId != null
                && monitors.containsKey(operationId);
    }

    /**
     * Registers an ETL monitor.
     *
     * <p>
     * If a monitor with the same operation ID already exists,
     * it will be replaced by the new monitor.
     * </p>
     *
     * @param monitor monitor to register
     */
    public void register(EtlMonitor monitor) {

        if (monitor == null
                || monitor.getOperationId() == null) {
            return;
        }

        monitors.put(
                monitor.getOperationId(),
                monitor);
    }

    /**
     * Removes a monitor from the registry.
     *
     * @param operationId ETL operation identifier
     */
    public void remove(String operationId) {

        if (operationId != null) {
            monitors.remove(operationId);
        }
    }

    /**
     * Finds a monitor by operation ID.
     *
     * @param operationId ETL operation identifier
     * @return monitor or null when it does not exist
     */
    public EtlMonitor find(String operationId) {

        if (operationId == null) {
            return null;
        }

        return monitors.get(operationId);
    }

    /**
     * Returns all registered monitors.
     *
     * <p>
     * A new list is returned so callers cannot modify the internal
     * registry collection.
     * </p>
     *
     * @return list containing all registered monitors
     */
    public List<EtlMonitor> findAll() {

        return new ArrayList<EtlMonitor>(
                monitors.values());
    }

    /**
     * Finds the most recently registered monitor.
     *
     * <p>
     * This method is useful when the OpenMRS UI wants to monitor
     * the current ETL execution without already knowing its
     * operation ID.
     * </p>
     *
     * <p>
     * The registry is primarily keyed by operation ID, so this
     * method determines the current monitor using the monitor
     * start time.
     * </p>
     *
     * @return most recently started monitor, or null if none exists
     */
    public EtlMonitor findCurrent() {

        EtlMonitor current = null;

        for (EtlMonitor monitor : monitors.values()) {

            if (monitor == null) {
                continue;
            }

            if (current == null
                    || monitor.getStartTime()
                    .after(current.getStartTime())) {

                current = monitor;
            }
        }

        return current;
    }
}
