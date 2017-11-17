package no.rmz.midibridge.service;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.database.FirebaseDatabase;
import no.rmz.midibridge.MidiEventProducer;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.FirebaseDatabaseConfig;
import no.rmz.midibridge.config.MidiRoute;
import no.rmz.midibridge.config.MidibridgeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MidiRoutingManager {

    private final static Logger LOG = LoggerFactory.getLogger(MidiReceiver.class);

    private final MidiEventProducerMap eventProducerManager;
    private final MidiDeviceManager midiDeviceManger;
    private final MidiEventResource resource;

    public MidiRoutingManager(final MidibridgeConfiguration configuration) throws MidibridgeException {

        // Parse config & start services
        checkNotNull(configuration);
        this.eventProducerManager = new MidiEventProducerMap();
        this.midiDeviceManger = new MidiDeviceManager();
        UdpEndpointManager udpEndpointManager = new UdpEndpointManager(eventProducerManager);
        HttpEndpointManager httpEndpointManager = new HttpEndpointManager(eventProducerManager);
        final FirebaseDatabaseConfig firebaseDatabaseConfig = configuration.getFirebaseDatabaseConfig();

        if (firebaseDatabaseConfig != null) {
            LOG.info("Configuring firebase endpoint(s)");
            final FirebaseDatabase firebaseDatabase = firebaseDatabaseConfig.getFirebaseDatabase();
            final FirebaseEndpointManager firebaseEndpointManager
                    = new FirebaseEndpointManager(firebaseDatabase, eventProducerManager);
            firebaseEndpointManager.addAll(configuration.getFirebaseDestinations());
        }
        if (configuration.getHttpDestinations() != null) {
            LOG.info("Configuring HTTP endpoint(s)");
            httpEndpointManager.addAll(configuration.getHttpDestinations());
        }
        if (configuration.getUdpDestinations() != null) {
            LOG.info("Configuring UDP endpoint(s)");
            udpEndpointManager.addAll(configuration.getUdpDestinations());
        }
        if (configuration.getUdpDestinations() != null) {
            midiDeviceManger.addAll(configuration.getMidiDestinations());
        }

        // Set up routes.
        setUpRoutes(configuration);

        // Create resource to handle HTTP requests, to be picked up
        // by dropwizard and be injected into Jersey.
        this.resource = new MidiEventResource(httpEndpointManager);
    }

    private void setUpRoutes(final MidibridgeConfiguration configuration) throws MidibridgeException {
        for (final MidiRoute route : configuration.getMidiRoutes()) {
            final String source = route.getSource();
            final String destination = route.getDestination();
            setUpRoute(source, destination);
        }
    }

    private void setUpRoute(final String source, final String destination) throws MidibridgeException {
        final MidiEventProducer producer = eventProducerManager.get(source);
        final MidiReceiver receiver = midiDeviceManger.getEntryById(destination).getReceiver();
        producer.addMidiReceiver(receiver);
    }

    public MidiEventResource getResource() {
        return resource;
    }
}
