package no.rmz.midibridge;

public interface MidiEventProducer {

    /**
     * Add a receiver that will receive the midi events produced by this
     * producer.
     *
     * @param receiver
     */
    void addMidiReceiver(MidiReceiver receiver);

}
