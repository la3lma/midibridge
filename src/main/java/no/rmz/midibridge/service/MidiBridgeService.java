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
        // XXX Setting args to make it simpler to debug, not what we should do when
        //     building standalone jars.
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
            final Environment environment) throws MidibridgeException {

        final MidiEventResource resource = configMidiRouting(configuration);

        environment.jersey().register(resource);
    }

    private final MidiDeviceManager midiDeviceManger = new MidiDeviceManager();
    private FirebaseEndpointManager firebaseEndpointManager;
    private UdpEndpointManager udpEndpointManager = new UdpEndpointManager();

    private MidiEventResource configMidiRouting(final MidibridgeConfiguration configuration) throws RuntimeException, MidibridgeException {
        final FirebaseDatabase firebaseDatabase = configuration.getFirebaseDatabaseConfig().getFirebaseDatabase();
        firebaseEndpointManager = new FirebaseEndpointManager(firebaseDatabase);
        firebaseEndpointManager.addAll(configuration.getFirebaseDestinations());
        midiDeviceManger.addAll(configuration.getMidiDestinations());

        udpEndpointManager.addAll(configuration.getUdpDestinations());
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
        final MidiReceiver defaultReceiver = midiDeviceManger.getEntryById(configuration.getHttpMidiRoute()).getReceiver();
        final MidiEventResource resource = new MidiEventResource(defaultReceiver);
        final MidiOverUdpReceivingService udp = udpEndpointManager.get("udpMidi").getUdpService();
        udp.addMidiReceiver(defaultReceiver);

        return resource;
    }
}
