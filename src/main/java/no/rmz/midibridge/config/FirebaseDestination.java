package no.rmz.midibridge.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.rmz.midibridge.service.NamedEntity;

public final class FirebaseDestination implements NamedEntity {

    private String id;

    private String path;

    @JsonProperty
    @Override
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

    @Override
    public String toString() {
        return "FirebaseDestination{" + "id=" + id + ", path=" + path + '}';
    }
}
