package no.rmz.midibridge;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MidiReceiver {

    static final Logger LOG = LoggerFactory.getLogger(MidiReceiver.class);

    public void put(ShortMessage msg);

    default void put(final FbMidiEventBean midiEvent) throws MidibridgeException {
        checkNotNull(midiEvent);
        final ShortMessage shortMidiMessage = asShortMessage(midiEvent);
        put(shortMidiMessage);
    }

    static ShortMessage asShortMessage(final FbMidiEventBean bean) throws MidibridgeException {
        checkNotNull(bean);

        final MidiCmd cmd = MidiCmd.valueOf(bean.getCmd());
        final ShortMessage myMsg = new ShortMessage();
        try {
            switch (cmd) {
                case NOTE_ON:
                case NOTE_OFF:
                    final int midiCmd = cmd.getCmd();
                    myMsg.setMessage(midiCmd, bean.getChan(), bean.getNote(), bean.getVelocity());
                    return myMsg;
                default:
                    LOG.info("Received MIDI unknown type of MIDI message: " + bean.toString());
            }
        } catch (InvalidMidiDataException ex) {
            throw new MidibridgeException("couldn't make message", ex);
        }
        throw new MidibridgeException("Could not produce a valid Short MIDI message from input");
    }

    default void put(byte[] bytes) throws InvalidMidiDataException {

        final int firstByte = bytes[0] & 0xFF;

        final MidiCmd cmd = MidiCmd.findByMidiCmd(firstByte);

        if (cmd == null) {
            return;
        }

        if (bytes.length != (cmd.getNoOfArgs() + 1)) {
            return;
        }

        final int channel = 0; // XXX   Where should we -really- get this from?
        int arg1 = 0;
        int arg2 = 0;
        if (bytes.length > 0) {
            arg1 = bytes[1] & 0xFF;
        }

        if (bytes.length > 1) {
            arg2 = bytes[2] & 0xFF;
        }

        final ShortMessage myMsg
                = new ShortMessage(cmd.getCmd(), channel, arg1, arg2);
        put(myMsg);
    }
}
