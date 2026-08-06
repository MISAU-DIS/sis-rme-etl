package org.openmrs.module.epts.etl.monitor;

import org.junit.Test;
import org.openmrs.module.epts.etl.monitor.collector.MonitorCollector;

public class MonitorCollectorTest {

    @Test
    public void testCollectorCreation() {

        MonitorCollector collector = new MonitorCollector();

        collector.collect(null);

        System.out.println("Collector executed");

    }
}