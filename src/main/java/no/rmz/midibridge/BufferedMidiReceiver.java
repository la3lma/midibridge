package no.rmz.midibridge;

import com.google.common.base.Preconditions;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BufferedMidiReceiver implements MidiReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(BufferedMidiReceiver.class);

    private final Receiver recv;
    private final BlockingQueue<ShortMessage> queue;
    private AtomicBoolean shutdown;

    public BufferedMidiReceiver(final Receiver recv) {
        this.recv = Preconditions.checkNotNull(recv);
        this.queue = new LinkedBlockingQueue<>(100000);
        this.shutdown = new AtomicBoolean(false);
        final Runnable runnable = () -> {
            while (!this.shutdown.get()) {
                safelySendOldest();
            }
        };
        new Thread(runnable).start();
    }

    /**
     * When invoked, will stop the sending of MIDI events.
     */
    public void stop() {
        this.shutdown.set(true);
    }

    private void safelySendOldest() {
        try {
            sendOldest();
        } catch (InterruptedException e) {
            LOG.error("Interrupted sending of MIDI message", e);
        }
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

    @Override
    public String toString() {
        return "BufferedMidiReceiver{" + "recv=" + recv + ", queue=" + queue + '}';
    }
}
