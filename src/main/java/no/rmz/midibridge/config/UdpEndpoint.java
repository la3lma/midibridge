package no.rmz.midibridge.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.rmz.midibridge.service.NamedEntity;

public final class UdpEndpoint implements NamedEntity {

    private String id;
    private int port;

    @JsonProperty
    public int getPort() {
        return port;
    }

    public void setSource(int port) {
        this.port = port;
    }

    @JsonProperty
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UdpEndpoint{" + "id=" + id + ", port=" + port + '}';
    }
}
