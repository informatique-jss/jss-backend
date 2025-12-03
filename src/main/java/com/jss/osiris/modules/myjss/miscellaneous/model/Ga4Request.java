package com.jss.osiris.modules.myjss.miscellaneous.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ga4Request {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("events")
    private List<Ga4Event> events;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Ga4Event> getEvents() {
        return events;
    }

    public void setEvents(List<Ga4Event> events) {
        this.events = events;
    }
}
