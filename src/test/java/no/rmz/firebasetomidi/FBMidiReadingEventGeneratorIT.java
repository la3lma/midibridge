package no.rmz.firebasetomidi;

import java.util.concurrent.CountDownLatch;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import no.rmz.midibridge.BufferedMidiReceiver;
import no.rmz.midibridge.IacDeviceUtilities;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;
import org.junit.Test;

public class FBMidiReadingEventGeneratorIT {

    public FBMidiReadingEventGeneratorIT() {
    }

    @Test
    public void testSomeMethod() throws MidibridgeException, InterruptedException, MidiUnavailableException {

        // These should be gotten from the argv
        final String configFile = "fbmidibridge-1746b45f5da7.json";  // arg2
        final String databaseName = "fbmidibridge"; // arg1
        final String pathToListenForEventsIn = "testchannel"; // arg3
        final String midiDeviceName = "toReason"; // arg4

        final MidiDevice midiDevice
                = IacDeviceUtilities.getMidiReceivingDevice(midiDeviceName);

        final Receiver receiver = midiDevice.getReceiver();

        final BufferedMidiReceiver br = new BufferedMidiReceiver(receiver);

        final CountDownLatch latch = new CountDownLatch(1);
        final MidiReceiver latchingReceiver = (ShortMessage msg) -> {
            latch.countDown();
            br.put(msg);
        };

        /*

        final FbMidiReadingEventGenerator midiReadingEventSource;
        midiReadingEventSource = new FbMidiReadingEventGenerator(databaseName, configFile, pathToListenForEventsIn, latchingReceiver);

        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("Couldn't receive sample MIDI message through firebase.  So sad.");
        }
         */
    }
}
