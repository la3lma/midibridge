package no.rmz.midibridge.service;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sound.midi.ShortMessage;
import no.rmz.midibridge.MidiEventProducer;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;

public final class HttpEndpointManager extends AbstractEndpointManager<HttpEndpointConfig> {

    private final Map<String, MidiReceiver> pathToEndpointMap;
    public HttpEndpointManager(final MidiEventProducerMap epm) {
        super(epm);
        this.pathToEndpointMap = new HashMap<>();
    }


    @Override
    MidiEventProducerEntry newEntry(HttpEndpointConfig dest) throws MidibridgeException {
        checkNotNull(dest);
        return new Entry();
    }

    @Override
    public MidiEventProducerEntry add(final HttpEndpointConfig dest) throws MidibridgeException {

        final MidiEventProducerEntry entry = super.add(dest);

        if (pathToEndpointMap.containsKey(dest.getPath())) {
            throw new MidibridgeException("Duplicate declaration for http destination " + dest.getPath());
        } else {
            pathToEndpointMap.put(dest.getPath(), (MidiReceiver) entry);
        }

        return entry;
    }

    public final MidiReceiver getReceiverForPath(final String endpoint) {
        checkNotNull(endpoint);
        return pathToEndpointMap.get(endpoint);
    }
    public final static class Entry implements MidiEventProducer, MidiReceiver, MidiEventProducerEntry {
        
        private final Set<MidiReceiver> receivers = new HashSet<>();
        
        @Override
        public void addMidiReceiver(final MidiReceiver receiver) {
            receivers.add(receiver);
        }
        
        @Override
        public void put(final ShortMessage msg) {
            checkNotNull(msg);
            receivers.forEach((r) -> {
                r.put(msg);
            });
        }
        
        @Override
        public MidiEventProducer getMidiEventProducer() {
            return this;
        }
    }
}
