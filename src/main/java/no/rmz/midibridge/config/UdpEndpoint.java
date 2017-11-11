package no.rmz.midibridge.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UdpEndpoint {

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
