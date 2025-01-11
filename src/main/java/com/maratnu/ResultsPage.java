package com.maratnu;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultsPage {
    @JsonProperty("next")
    private String nextPageUrl;
    @JsonProperty("results")
    private Resource[] resources;

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public Resource[] getResources() {
        return resources;
    }

}

