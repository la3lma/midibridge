package no.rmz.midibridge;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MidiReceiver {

    static final Logger LOG = LoggerFactory.getLogger(MidiReceiver.class);

    public void put(ShortMessage msg);

    default void put(final FbMidiEventBean midiEvent) {
        checkNotNull(midiEvent);
        final ShortMessage shortMidiMessage = asShortMessage(midiEvent);
        put(shortMidiMessage);
    }

    static ShortMessage asShortMessage(final FbMidiEventBean bean) {
        checkNotNull(bean);

        final MidiCmd cmd = MidiCmd.valueOf(bean.getCmd());
        final ShortMessage myMsg = new ShortMessage();
        try {
            switch (cmd) {
                case NOTE_ON:
                    myMsg.setMessage(ShortMessage.NOTE_ON, bean.getChan(), bean.getNote(), bean.getStrength());
                    return myMsg;
                case NOTE_OFF:
                    myMsg.setMessage(ShortMessage.NOTE_OFF, bean.getChan(), bean.getNote(), bean.getStrength());
                    return myMsg;
                default:
                    LOG.info("Received MIDI unknown type of MIDI message: " + bean.toString());
            }
        } catch (InvalidMidiDataException ex) {
            throw new IllegalStateException("couldn't make message", ex);
        }
        throw new IllegalArgumentException("Could not produce a valid Short MIDI message from input");
    }
}
