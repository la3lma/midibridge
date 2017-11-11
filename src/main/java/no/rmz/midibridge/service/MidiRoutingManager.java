package no.rmz.midibridge.service;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.database.FirebaseDatabase;
import no.rmz.midibridge.MidiEventProducer;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.MidiRoute;
import no.rmz.midibridge.config.MidibridgeConfiguration;

public final class MidiRoutingManager {

    private final MidiEventProducerMap eventProducerManager;
    private final MidiDeviceManager midiDeviceManger;
    private final FirebaseEndpointManager firebaseEndpointManager;
    private final UdpEndpointManager udpEndpointManager;
    private final HttpEndpointManager httpEndpointManager;
    private final MidiEventResource resource;

    public MidiRoutingManager(final MidibridgeConfiguration configuration) throws MidibridgeException {
        checkNotNull(configuration);
        this.eventProducerManager = new MidiEventProducerMap();
        this.midiDeviceManger = new MidiDeviceManager();
        this.udpEndpointManager = new UdpEndpointManager(eventProducerManager);
        this.httpEndpointManager = new HttpEndpointManager(eventProducerManager);
        final FirebaseDatabase firebaseDatabase = configuration.getFirebaseDatabaseConfig().getFirebaseDatabase();
        firebaseEndpointManager = new FirebaseEndpointManager(firebaseDatabase, eventProducerManager);
        firebaseEndpointManager.addAll(configuration.getFirebaseDestinations());
        httpEndpointManager.addAll(configuration.getHttpDestinations());
        udpEndpointManager.addAll(configuration.getUdpDestinations());
        midiDeviceManger.addAll(configuration.getMidiDestinations());

        setUpRoutes(configuration);
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
