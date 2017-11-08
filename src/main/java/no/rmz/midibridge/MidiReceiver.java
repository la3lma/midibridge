package no.rmz.midibridge;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MidiReceiver {

    static final Logger LOG = LoggerFactory.getLogger(MidiReceiver.class);

    public void put(ShortMessage msg);

    default void put(FbMidiEventBean midiEvent) {
        checkNotNull(midiEvent);
        final ShortMessage shortMidiMessage = asShortMessage(midiEvent);
        put(shortMidiMessage);
    }

    static ShortMessage asShortMessage(final FbMidiEventBean bean) {
        checkNotNull(bean);

        final MidiCmd cmd = MidiCmd.valueOf(bean.getCmd());
        final ShortMessage myMsg = new ShortMessage();
        switch (cmd) {
            case NOTE_ON:
                try {
                    myMsg.setMessage(ShortMessage.NOTE_ON, bean.getChan(), bean.getNote(), bean.getStrength());
                } catch (InvalidMidiDataException ex) {
                    throw new IllegalStateException(" couldn't make message", ex);
                }
                return myMsg;

            case NOTE_OFF:
                try {
                    myMsg.setMessage(ShortMessage.NOTE_OFF, bean.getChan(), bean.getNote(), bean.getStrength());
                } catch (InvalidMidiDataException ex) {
                    throw new IllegalStateException(" couldn't make message", ex);
                }
                return myMsg;

            default:
                LOG.info("Received MIDI unknown type of MIDI message: " + bean.toString());
        }
        throw new IllegalArgumentException("Could not produce a valid MIDI message from input");
    }
}
