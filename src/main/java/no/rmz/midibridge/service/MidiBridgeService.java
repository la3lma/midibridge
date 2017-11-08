package no.rmz.midibridge.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import no.rmz.midibridge.BufferedMidiReceiver;
import no.rmz.midibridge.FBMidiReadingEventGenerator;
import no.rmz.midibridge.IacDeviceUtilities;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;

public class MidiBridgeService extends Application<MidibridgeConfiguration> {

    public static void main(String[] args) throws Exception {
        args = new String[]{"server"};
        new MidiBridgeService().run(args);
    }

    @Override
    public String getName() {
        return "midibridge";
    }

    @Override
    public void initialize(Bootstrap<MidibridgeConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(MidibridgeConfiguration configuration,
            Environment environment) {
        final HelloWorldResource resource = new HelloWorldResource();

        // These should be gotten from the argv
        final String configFile = "fbmidibridge-1746b45f5da7.json";  // arg2
        final String databaseName = "fbmidibridge"; // arg1
        final String pathToListenForEventsIn = "testchannel"; // arg3
        final String midiDeviceName = "toReason"; // arg4

        try {
            final MidiDevice midiDevice
                    = IacDeviceUtilities.getMidiReceivingDevice(midiDeviceName);
            final MidiReceiver br = new BufferedMidiReceiver(midiDevice.getReceiver());

            FBMidiReadingEventGenerator midiReadingEventSource
                    = new FBMidiReadingEventGenerator(databaseName, configFile, pathToListenForEventsIn, br);

        } catch (MidibridgeException e) {
            // mm.
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(MidiBridgeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        environment.jersey().register(resource);
    }
}
