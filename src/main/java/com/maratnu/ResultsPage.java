package com.maratnu;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultsPage {
    @JsonProperty("next")
    private String nextPageUrl;
    @JsonProperty("results")
    private Person[] persons;

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public Person[] getPersons() {
        return persons;
    }

}

