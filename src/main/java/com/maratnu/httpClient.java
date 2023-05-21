package com.maratnu;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class httpClient {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        int countPersons = 0;
        int countRequests = 0;
        String url = "https://swapi.dev/api/people";
        ResultsPage page;
        HttpClient client = HttpClient.newBuilder().build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String,String> homeworlds = new HashMap<>();
        Planet planet;
        String planetName;

        do {
            // prepare the get request
            HttpRequest requestGet = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // send the get request
            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            countRequests++;
            if (responseGet.statusCode() != HttpURLConnection.HTTP_OK){
                return;
            }
            page = mapper.readValue(responseGet.body(), ResultsPage.class);
            for (Person person: page.getPersons()) {
                planetName = "unknown";
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
    private static Planet GetPlanet(String url) throws IOException, InterruptedException, URISyntaxException {
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
        return mapper.readValue(responseGet.body(), Planet.class);
    }
}
