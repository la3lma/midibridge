package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import java.util.logging.Level;
import no.rmz.midibridge.MidiEventProducer;
import no.rmz.midibridge.MidiOverUdpReceivingService;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.UdpEndpoint;

public final class UdpEndpointManager extends AbstractEndpointManager<UdpEndpoint> {

    public UdpEndpointManager(final MidiEventProducerMap eventProducerManager) {
        super(eventProducerManager);
    }


    @Override
    public MidiEventProducerEntry newEntry(UdpEndpoint endpoint) throws MidibridgeException {
        return new UdpEntry(endpoint);
    }
    public static final class UdpEntry implements MidiEventProducerEntry {
        
        final UdpEndpoint endpoint;
        final MidiOverUdpReceivingService udpService;

        public UdpEntry(final UdpEndpoint endpoint) throws MidibridgeException {
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

        @Override
        public MidiEventProducer getMidiEventProducer() {
            return udpService;
        }
    }
}
