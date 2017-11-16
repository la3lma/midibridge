package no.rmz.midibridge;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MidiReceiver {

    Logger LOG = LoggerFactory.getLogger(MidiReceiver.class);

    void put(ShortMessage msg);

    default void put(final FbMidiEventBean midiEvent) throws MidibridgeException {
        checkNotNull(midiEvent);
        final ShortMessage shortMidiMessage = asShortMessage(midiEvent);
        put(shortMidiMessage);
    }

    static ShortMessage asShortMessage(final FbMidiEventBean bean) throws MidibridgeException {
        checkNotNull(bean);

        final MidiCmd cmd = MidiCmd.valueOf(bean.getCmd());

        if (cmd != null) {
            return beanToShortMessage(cmd, bean);
        } else {
            LOG.info("Received MIDI unknown type of MIDI message: " + bean.toString());
            throw new MidibridgeException("Could not produce a valid Short MIDI message from input");
        }
    }

    /*
     Command    Meaning #               parameters      param 1         param 2
     0x80       Note-off                2               key             velocity
     0x90       Note-on                 2               key             veolcity
     0xA0       Aftertouch              2               key             touch
     0xB0       Continuous controller   2               controller      controller value
     0xC0       Patch change            2               instrument      #
     0xD0       Channel Pressure        1               pressure
     0xE0       Pitch bend              2               lsb (7 bits)    msb (7 bits)
                                                       " 14 bits  of bend value"
     0xF0       (non-musical commands)
     */


    static int getArg1(int midiCmd, FbMidiEventBean bean) {
        if ((midiCmd == ShortMessage.NOTE_ON)
                || (midiCmd == ShortMessage.NOTE_OFF)
                || (midiCmd == ShortMessage.POLY_PRESSURE)) {
            return bean.getNote();
        } else if (midiCmd == ShortMessage.CONTROL_CHANGE) {
            return bean.getController();
        } else if (midiCmd == ShortMessage.PROGRAM_CHANGE) {
            return bean.getInstrument();
        } else if (midiCmd == ShortMessage.CHANNEL_PRESSURE) {
            return bean.getPressure();
        } else if (midiCmd == ShortMessage.PITCH_BEND) {
            return getMsbOf14ValidBits(bean.getBendValue());
        } else {
            return 0;
        }
    }

    static int getLeastSignificantSevenBits(int x) {
        return x & ((int) (1L << 7) - 1);
    }

    static int getLsbOf14ValidBits(int x) {
        return getLeastSignificantSevenBits(x);
    }

    // XXX  THis is not correct!  Don't know how to make it right yet.
    static int getMsbOf14ValidBits(int x) {
        return getLeastSignificantSevenBits(x << 7);
    }

    static int getArg2(int midiCmd, FbMidiEventBean bean) {
        if ((midiCmd == ShortMessage.NOTE_ON)
                || (midiCmd == ShortMessage.NOTE_OFF)) {
            return bean.getVelocity();
        } else if (midiCmd == ShortMessage.POLY_PRESSURE) {
            return bean.getTouch();
        } else if (midiCmd == ShortMessage.CONTROL_CHANGE) {
            return bean.getController();
        } else if (midiCmd == ShortMessage.PROGRAM_CHANGE) {
            return bean.getPatch();
        } else if (midiCmd == ShortMessage.PITCH_BEND) {
            return getLsbOf14ValidBits(bean.getBendValue());
        } else {
            return 0;
        }
    }

    static ShortMessage beanToShortMessage(final MidiCmd cmd, final FbMidiEventBean bean) throws MidibridgeException {
        final int midiCmd = cmd.getCmd();
        try {
            final ShortMessage myMsg = new ShortMessage();
            // XXX This isn'r right :-) It's only for a few of the commands that
            //     arg1 is not and arg2 is velocity.   This requires *drumroll* another
            //     layer of abstraction .-)
            myMsg.setMessage(midiCmd, bean.getChan(), getArg1(midiCmd, bean), getArg2(midiCmd, bean));
            return myMsg;
        } catch (InvalidMidiDataException ex) {
            throw new MidibridgeException("couldn't make message", ex);
        }
    }

    default void put(byte[] bytes) throws InvalidMidiDataException {

        if (bytes.length < 2) {
            return;
        }

        final int midiChannelByte = bytes[0] & 0xFF;
        final int midiCommandByte = bytes[1] & 0xFF;

        if (midiChannelByte > 16) {  // XXX Or >=?
            return;
        }

        final MidiCmd cmd = MidiCmd.findByMidiCmd(midiCommandByte);

        if (cmd == null) {
            return;
        }

        if (bytes.length != (cmd.getNoOfArgs() + 2)) {
            return;
        }

        final int channel = midiChannelByte;

        int arg1 = 0;
        int arg2 = 0;
        if (bytes.length > 0) {
            arg1 = bytes[2] & 0xFF;
        }

        if (bytes.length > 1) {
            arg2 = bytes[3] & 0xFF;
        }

        final ShortMessage myMsg
                = new ShortMessage(midiCommandByte, channel, arg1, arg2);
        put(myMsg);
    }
}
