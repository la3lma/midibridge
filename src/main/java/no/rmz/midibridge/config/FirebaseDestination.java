package no.rmz.midibridge.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class FirebaseDestination {

    private String id;

    private String path;

    @JsonProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
