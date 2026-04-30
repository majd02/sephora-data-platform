package com.sephora.data.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeltaOperation<T> {
    private String action;
    private String timestamp;
    private Map<String, Object> fields = new HashMap<>(); // On l'initialise ici

    public DeltaOperation() {}

    public DeltaOperation(String action, String timestamp, Map<String, Object> fields) {
        this.action = action;
        this.timestamp = timestamp;
        this.fields = fields;
    }

    @JsonAnySetter
    public void addField(String key, Object value) {
        this.fields.put(key, value);
    }

    // Getters et Setters standards
    public String getAction() { return action; }
    public String getTimestamp() { return timestamp; }
    public Map<String, Object> getFields() { return fields; }
}