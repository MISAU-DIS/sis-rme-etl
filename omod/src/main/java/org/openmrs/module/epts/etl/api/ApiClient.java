package org.openmrs.module.epts.etl.api;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ApiClient {
    // Responsável pela chamada HTTP genérica.

    private final String baseUrl;



    public ApiClient(String baseUrl){

        this.baseUrl = baseUrl;

    }



    public String get(String endpoint) throws Exception {


        URL url =
                new URL(
                        baseUrl + endpoint
                );


        HttpURLConnection connection =
                (HttpURLConnection)
                        url.openConnection();



        connection.setRequestMethod(
                "GET"
        );


        connection.setRequestProperty(
                "Accept",
                "application/json"
        );



        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );


        StringBuilder response =
                new StringBuilder();



        String line;


        while((line = reader.readLine()) != null){

            response.append(line);

        }


        reader.close();


        return response.toString();

    }

}