package com.maratnu;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class httpClient {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        int countPersons = 0;
        int countRequests = 0;
        String url = "https://swapi.tech/api/people";
        ResultsPage page;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String,String> homeworlds = new HashMap<>();
        Planet planet;
        String planetName;

        do {
            // send the get request
            page = GetPage(url);
            countRequests++;

            for (Resource resource: page.getResources()) {
                planetName = "unknown";
                
                // person doesn't contain homeworld information, just a reference to a full person resource
                Person person = GetPerson(resource.getUrl());
                countRequests++;

                // accumulate homeworlds db
                if (person.getHomeworld() != null) {
                    planetName = homeworlds.get(person.getHomeworld());
                    if (planetName == null) {
                        planet = GetPlanet(person.getHomeworld());
                        countRequests++;
                        if (planet != null) {
                            planetName = planet.getName();
                            homeworlds.put(person.getHomeworld(), planetName);
                        }
                    }
                }
                System.out.println("name : " + person.getName() + " , homeworld : " + planetName);
                countPersons++;
            }
            url = page.getNextPageUrl();

        } while (url != null);
        System.out.println("persons : " + countPersons + " , requests : " + countRequests);

    }
    
    // todo - return object of requested type
    private static String GetResource(String url, String requestType) throws IOException, InterruptedException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        // send the get request
        HttpResponse<String> responseGet = HttpClient.newBuilder()
                .build()
                .send(requestGet, HttpResponse.BodyHandlers.ofString());
        if (responseGet.statusCode() != HttpURLConnection.HTTP_OK){
            return null;
        }

        //read JSON like DOM Parser
        JsonNode rootNode = mapper.readTree(responseGet.body());
        //JsonNode results = rootNode.path("results");
        JsonNode result = rootNode.path("result");
        JsonNode properties = result.path("properties");
        
        if (requestType.equals("page")) {
            return rootNode.toString();
        }
        else {
            return properties.toString();
        }
    }
    private static ResultsPage GetPage(String url) throws IOException, InterruptedException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String responseGet = GetResource(url,"page");
        if (responseGet == null){
            return null;
        }
        return mapper.readValue(responseGet, ResultsPage.class);
    }

    private static Planet GetPlanet(String url) throws IOException, InterruptedException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String responseGet = GetResource(url,"planet");
        if (responseGet == null){
            return null;
        }
        return mapper.readValue(responseGet, Planet.class);
    }

    private static Person GetPerson(String url) throws IOException, InterruptedException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String responseGet = GetResource(url,"person");
        if (responseGet == null){
            return null;
        }
        return mapper.readValue(responseGet, Person.class);
    }
}
