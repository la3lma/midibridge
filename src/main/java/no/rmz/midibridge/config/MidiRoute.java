package no.rmz.midibridge.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class MidiRoute {

    private String source;

    private String destination;

    @JsonProperty
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "MidiRoute{" + "source=" + source + ", destination=" + destination + '}';
    }
}
