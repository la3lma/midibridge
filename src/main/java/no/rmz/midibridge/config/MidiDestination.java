package no.rmz.midibridge.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public final class MidiDestination {

    private String id;

    private String midiDeviceName;

    @JsonProperty
    public String getMidiDeviceName() {
        return midiDeviceName;
    }

    public void setMidiDeviceName(final String midiDeviceName) {
        this.midiDeviceName = Preconditions.checkNotNull(midiDeviceName);
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
