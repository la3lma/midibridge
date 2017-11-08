package no.rmz.midibridge;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BufferedMidiReceiver implements MidiReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(BufferedMidiReceiver.class);

    private final Receiver recv;
    private final BlockingQueue<ShortMessage> queue;

    public BufferedMidiReceiver(final Receiver recv) {
        this.recv = Preconditions.checkNotNull(recv);
        this.queue = new LinkedBlockingQueue<>(100000);
        new Thread(() -> {
            while (true) {
                try {
                    sendOldest();
                } catch (InterruptedException e) {
                    LOG.error("Interrupted sending of MIDI message", e);
                }
            }
        }).start();
    }

    private void sendOldest() throws InterruptedException {
        final ShortMessage msg = queue.take();
        final long timeStamp = -1;
        LOG.info("Sending short midi message: " + msg);
        recv.send(msg, timeStamp);
    }

    @Override
    public void put(final ShortMessage msg) {
        queue.add(msg);
    }

    private static ShortMessage asShortMessage(final FbMidiEventBean bean) {
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

    @Override
    public void put(final FbMidiEventBean midiEvent) {
        checkNotNull(midiEvent);
        final ShortMessage shortMidiMessage = asShortMessage(midiEvent);
        put(shortMidiMessage);
    }
}
