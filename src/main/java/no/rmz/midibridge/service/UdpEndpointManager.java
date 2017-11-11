package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.rmz.midibridge.MidiOverUdpReceivingService;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.UdpEndpoint;

public final class UdpEndpointManager {

    private static final Logger LOG = Logger.getLogger(UdpEndpointManager.class.getName());
    private final MidiBridgeService.MidiEventProducerMap epm;

    public UdpEndpointManager(MidiBridgeService.MidiEventProducerMap eventProducerManager) {
        this.epm = checkNotNull(eventProducerManager);
        this.idToEndpointMap = new HashMap<>();
    }

    public static final class Entry {

        final UdpEndpoint endpoint;
        final MidiOverUdpReceivingService udpService;

        public Entry(final UdpEndpoint endpoint) throws MidibridgeException {
            this.endpoint = Preconditions.checkNotNull(endpoint);
            this.udpService = new MidiOverUdpReceivingService(endpoint.getPort());
            new Thread(() -> {
                try {
                    udpService.start();
                } catch (MidibridgeException ex) {
                    LOG.log(Level.SEVERE, "udp service for enpoint " + endpoint + " failed", ex);
                }
            }).start();
        }


        public MidiOverUdpReceivingService getUdpService() {
            return udpService;
        }

        public UdpEndpoint getEndpoint() {
            return endpoint;
        }
    }
    private final Map<String, Entry> idToEndpointMap;

    void addAll(final Collection<UdpEndpoint> endpoints) throws MidibridgeException {
        Preconditions.checkNotNull(endpoints);
        for (final UdpEndpoint dest : endpoints) {
            add(dest);
        }
    }

    public void add(final UdpEndpoint endpoint) throws MidibridgeException {
        Preconditions.checkNotNull(endpoint);
        if (idToEndpointMap.containsKey(endpoint.getId())) {
            throw new MidibridgeException("Multiple declarations of firebase destination with id = " + endpoint.getId());
        }
        final Entry entry = new Entry(endpoint);
        idToEndpointMap.put(endpoint.getId(), entry);
        epm.put(endpoint.getId(), entry.getUdpService());
    }

    public Entry get(String id) throws MidibridgeException {
        if (idToEndpointMap.containsKey(id)) {
            return idToEndpointMap.get(id);
        } else {
            throw new MidibridgeException("Unknown firebase endpoint named: " + id);
        }
    }
}
