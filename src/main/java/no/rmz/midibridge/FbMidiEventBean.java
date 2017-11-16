package no.rmz.midibridge;

import com.fasterxml.jackson.annotation.JsonProperty;

/*

Command	Meaning	#               parameters	param 1         param 2
0x80	Note-off	        2               key             velocity
0x90	Note-on	                2               key             veolcity
0xA0	Aftertouch	        2               key             touch
0xB0	Continuous controller	2               controller      controller value
0xC0	Patch change	        2               instrument      #
0xD0	Channel Pressure	1               pressure
0xE0	Pitch bend	        2               lsb (7 bits)    msb (7 bits)
                                                  " 14 bits  of bend value"
0xF0	(non-musical commands)
 */
public final class FbMidiEventBean {

    private String cmd;
    private int chan;
    private int note;
    private int velocity;
    private int controller;
    private int pressure;
    private int touch;
    private int instrument;
    private int bendValue;
    private int patch;
    public FbMidiEventBean() {
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    @JsonProperty
    public int getBendValue() {
        return bendValue;
    }

    public void setBendValue(int bendValue) {
        this.bendValue = bendValue;
    }

    @JsonProperty
    public int getController() {
        return controller;
    }

    public void setController(int controller) {
        this.controller = controller;
    }

    @JsonProperty
    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    @JsonProperty
    public int getTouch() {
        return touch;
    }

    @JsonProperty
    public void setTouch(int touch) {
        this.touch = touch;
    }

    @JsonProperty
    public int getInstrumentNumber() {
        return instrument;
    }

    public void setInstrumentNumber(int instrumentNumber) {
        this.instrument = instrumentNumber;
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

    public int getInstrument() {
        return instrument;
    }

    @JsonProperty
    public int getPatch() {
        return patch;
    }
}
