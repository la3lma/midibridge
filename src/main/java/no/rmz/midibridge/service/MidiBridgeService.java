package no.rmz.midibridge.service;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.database.FirebaseDatabase;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.HashMap;
import java.util.Map;
import no.rmz.midibridge.MidiEventProducer;
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

    // XXX This stuff is getting _really_ ripe for both
    //    a) Some proper unit tests.
    //    b) Some radical refactoring.
    private MidiEventProducerMap eventProducerManager;
    private MidiDeviceManager midiDeviceManger;
    private FirebaseEndpointManager firebaseEndpointManager;
    private UdpEndpointManager udpEndpointManager;
    private HttpEndpointManager httpEndpointManager;

    public MidiBridgeService() {
        this.eventProducerManager = new MidiEventProducerMap();
        this.midiDeviceManger = new MidiDeviceManager(eventProducerManager);
        this.udpEndpointManager = new UdpEndpointManager(eventProducerManager);
        this.httpEndpointManager = new HttpEndpointManager(eventProducerManager);

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

    private MidiEventResource configMidiRouting(final MidibridgeConfiguration configuration) throws RuntimeException, MidibridgeException {

        final FirebaseDatabase firebaseDatabase = configuration.getFirebaseDatabaseConfig().getFirebaseDatabase();
        firebaseEndpointManager = new FirebaseEndpointManager(firebaseDatabase, eventProducerManager);
        firebaseEndpointManager.addAll(configuration.getFirebaseDestinations());
        httpEndpointManager.addAll(configuration.getHttpDestinations());
        udpEndpointManager.addAll(configuration.getUdpDestinations());

        midiDeviceManger.addAll(configuration.getMidiDestinations());
        // Set up the firbase to MIDI routes.
        for (final MidiRoute route : configuration.getMidiRoutes()) {
            try {
                final String source = route.getSource();
                final MidiEventProducer producer = eventProducerManager.get(source);
                if (producer == null) {
                    throw new MidibridgeException("Unknown midi event producer " + source);
                }
                final String destination = route.getDestination();
                final MidiReceiver receiver = midiDeviceManger.getEntryById(destination).getReceiver();
                if (receiver == null) {
                    throw new MidibridgeException("Unknown midi receiver " + source);
                }

                producer.addMidiReceiver(receiver);
            } catch (MidibridgeException e) {
                throw new RuntimeException(e);
            }
        }

//        final MidiReceiver defaultReceiver = midiDeviceManger.getEntryById(configuration.getHttpMidiRoute()).getReceiver();
        final MidiEventResource resource = new MidiEventResource(httpEndpointManager);
//        final MidiEventProducer udp = udpEndpointManager.get("udpMidi").getUdpService();
//        udp.addMidiReceiver(defaultReceiver);

        return resource;
    }

    public static class MidiEventProducerMap {

        private final Map<String, MidiEventProducer> map;

        public MidiEventProducerMap() {
            this.map = new HashMap<>();
        }

        public void put(final String id, final MidiEventProducer entry) throws MidibridgeException {
            checkNotNull(id);
            checkNotNull(entry);
            if (map.containsKey(id)) {
                throw new MidibridgeException("Duplicate midi event producer: " + id);
            } else {
                map.put(id, entry);
            }
        }

        public MidiEventProducer get(final String id) {
            checkNotNull(id);
            return map.get(id);
        }
    }
}
