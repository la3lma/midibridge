package no.rmz.midibridge.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.List;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import no.rmz.midibridge.BufferedMidiReceiver;
import no.rmz.midibridge.FbMidiReadingEventGenerator;
import no.rmz.midibridge.IacDeviceUtilities;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.FirebaseDestination;
import no.rmz.midibridge.config.MidibridgeConfiguration;

public class MidiBridgeService extends Application<MidibridgeConfiguration> {

    public static void main(String[] args) throws Exception {
        args = new String[]{"server", "midibridge-config.yaml"};
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
    public void run(
            final MidibridgeConfiguration configuration,
            final Environment environment) {

        // These should be gotten from the the config file instead, and perhaps even
        // do most of the initializations in the initialize method instead of the
        // run method.

        final String configFile = configuration.getFirebaseDatabaseConfig().getConfigFile();
        final String databaseName = configuration.getFirebaseDatabaseConfig().getDatabaseName();

        final List<FirebaseDestination> firebaseDestinations = configuration.getFirebaseDestinations();
        if (firebaseDestinations.size() != 1) {
            throw new RuntimeException("Not exactly one firebase destination to listen for");
        }
        final FirebaseDestination dest = firebaseDestinations.get(0);

        final String pathToListenForEventsIn = dest.getPath();

        final String midiDeviceName = "toReason";

        final MidiReceiver mr;
        try {
            final MidiDevice midiDevice
                    = IacDeviceUtilities.getMidiReceivingDevice(midiDeviceName);
            mr = new BufferedMidiReceiver(midiDevice.getReceiver());

            // XXX Tie this into the lifecycle of dropwizard objects.
            FbMidiReadingEventGenerator midiReadingEventSource
                    = new FbMidiReadingEventGenerator(
                            databaseName,
                            configFile,
                            pathToListenForEventsIn,
                            mr);

        } catch (MidibridgeException | MidiUnavailableException e) {
            throw new RuntimeException(e);
        }

        final MidiEventResource resource = new MidiEventResource(mr);

        environment.jersey().register(resource);
    }
}
