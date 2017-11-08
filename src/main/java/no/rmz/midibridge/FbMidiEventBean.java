package no.rmz.midibridge;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class FbMidiEventBean {

    private String cmd;
    private int chan;
    private int note;
    private int velocity;

    public FbMidiEventBean() {
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setChan(int chan) {
        this.chan = chan;
    }

    public void setNote(int note) {
        this.note = note;
    }


    @JsonProperty
    public String getCmd() {
        return cmd;
    }

    @JsonProperty
    public int getChan() {
        return chan;
    }

    @JsonProperty
    public int getNote() {
        return note;
    }

    @JsonProperty
    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    @Override
    public String toString() {
        return "FbMidiEventBean{" + "cmd=" + cmd + ", chan=" + chan + ", note=" + note + ", velocity=" + velocity + '}';
    }
}
