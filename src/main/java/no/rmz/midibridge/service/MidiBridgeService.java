package no.rmz.midibridge.service;

import com.google.firebase.database.FirebaseDatabase;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import no.rmz.midibridge.FbMidiReadingEventGenerator;
import no.rmz.midibridge.MidiOverUdpReceivingService;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.MidiRoute;
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

    private final MidiDeviceManager midiDeviceManger = new MidiDeviceManager();
    private FirebaseEndpointManager firebaseEndpointManager;


    @Override
    public void run(
            final MidibridgeConfiguration configuration,
            final Environment environment) throws MidibridgeException {

        final FirebaseDatabase firebaseDatabase = configuration.getFirebaseDatabaseConfig().getFirebaseDatabase();

        firebaseEndpointManager = new FirebaseEndpointManager(firebaseDatabase);
        firebaseEndpointManager.addAll(configuration.getFirebaseDestinations());
        midiDeviceManger.addAll(configuration.getMidiDestinations());

        // Set up the firbase to MIDI routes.
        for (final MidiRoute route : configuration.getMidiRoutes()) {
            try {
                final FbMidiReadingEventGenerator midiReadingEventSource
                        = firebaseEndpointManager.get(route.getSource()).getGenerator();
                final MidiDeviceManager.Entry entryById = midiDeviceManger.getEntryById(route.getDestination());
                final MidiReceiver receiver = entryById.getReceiver();
                midiReadingEventSource.addMidiReceiver(receiver);
            } catch (MidibridgeException e) {
                throw new RuntimeException(e);
            }
        }

        // XXX The routing is still a bit of a mess.  Needs to be refactored for proper workitude.

        // Set up routing of incoming HTTP requests to MIDI.
        final MidiEventResource resource = new MidiEventResource(midiDeviceManger.getEntryById(configuration.getHttpMidiRoute()).getReceiver());

        final MidiOverUdpReceivingService udp
                = new MidiOverUdpReceivingService(6565,
                        midiDeviceManger.getEntryById(configuration.getHttpMidiRoute()).getReceiver());
        udp.start();

        environment.jersey().register(resource);
    }
}
