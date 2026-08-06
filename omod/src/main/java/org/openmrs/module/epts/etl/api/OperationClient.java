package org.openmrs.module.epts.etl.api;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.openmrs.module.epts.etl.monitor.dto.MonitorResponseDTO;



public class OperationClient {

//    Aqui fica o cliente da API.

    private final ApiClient apiClient;


    private final ObjectMapper mapper;



    public OperationClient(String apiUrl){


        this.apiClient =
                new ApiClient(apiUrl);


        this.mapper =
                new ObjectMapper();

    }





    public MonitorResponseDTO getMonitor()
            throws Exception {



        String json =
                apiClient.get(
                        "/operations/monitor"
                );



        return mapper.readValue(
                json,
                MonitorResponseDTO.class
        );

    }

}