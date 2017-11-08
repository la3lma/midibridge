package no.rmz.midibridge;

import javax.sound.midi.ShortMessage;

public interface MidiReceiver {

    public void put(ShortMessage msg);
}
