package no.rmz.midibridge.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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

        // These should be gotten from the argv
        final String configFile = "fbmidibridge-1746b45f5da7.json";  // arg2
        final String databaseName = "fbmidibridge"; // arg1
        final String pathToListenForEventsIn = "testchannel"; // arg3
        final String midiDeviceName = "toReason"; // arg4

        final MidiReceiver mr;
        try {
            final MidiDevice midiDevice
                    = IacDeviceUtilities.getMidiReceivingDevice(midiDeviceName);
            mr = new BufferedMidiReceiver(midiDevice.getReceiver());

            // XXX Tie this into the lifecycle of dropwizard objects.
            FBMidiReadingEventGenerator midiReadingEventSource
                    = new FBMidiReadingEventGenerator(databaseName, configFile, pathToListenForEventsIn, mr);

        } catch (MidibridgeException | MidiUnavailableException e) {
            throw new RuntimeException(e);
        }

        final MidiEventResource resource = new MidiEventResource(mr);

        environment.jersey().register(resource);
    }
}
