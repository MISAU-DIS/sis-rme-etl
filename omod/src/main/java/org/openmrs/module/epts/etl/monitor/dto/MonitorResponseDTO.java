package org.openmrs.module.epts.etl.monitor.dto;

import java.util.List;

public class MonitorResponseDTO {

        private List<MonitorInfoDTO> monitors;

        public MonitorResponseDTO() {
        }

        public List<MonitorInfoDTO> getMonitors() {
                return monitors;
        }

        public void setMonitors(List<MonitorInfoDTO> monitors) {
                this.monitors = monitors;
        }
}