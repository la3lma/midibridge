package no.rmz.midibridge;

import javax.sound.midi.ShortMessage;

public enum MidiCmd {
    NOTE_ON(ShortMessage.NOTE_ON),
    NOTE_OFF(ShortMessage.NOTE_OFF);

    private final int cmd;

    public int getCmd() {
        return cmd;
    }

    private MidiCmd(int cmd) {
        this.cmd = cmd;
    }
}
