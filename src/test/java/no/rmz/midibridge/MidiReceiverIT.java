package no.rmz.midibridge;

import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

public class MidiReceiverIT {

    public MidiReceiverIT() {
    }

    @Test
    public void testgetLeastSignificantSevenBits() {
        final int v = MidiReceiver.getLeastSignificantSevenBits(0b11111111);
        assertEquals(0b1111111, v);
    }

    // XXX This thing is failing, don't quite know why (bit-fiddling),
    //     make it work!
    @Ignore
    @Test
    public void testgetMostSignificantSevenBits() {
        final int v = MidiReceiver.getMsbOf14ValidBits(0b11111111);
        assertEquals(0b1, v);
    }
}
