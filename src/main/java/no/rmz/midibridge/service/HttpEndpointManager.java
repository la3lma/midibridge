package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sound.midi.ShortMessage;
import no.rmz.midibridge.MidiEventProducer;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;

final class HttpEndpointManager {

    private final MidiEventProducerMap epm;

    final MidiReceiver getReceiverForPath(final String endpoint) {
        checkNotNull(endpoint);
        return idToEndpointMap.get(endpoint);
    }

    public final static class Entry implements MidiEventProducer, MidiReceiver {

        private String id;
        private String path;

        public Entry(final String id, final String path) {
            this.id = checkNotNull(id);
            this.path = checkNotNull(path);
        }

        public String getId() {
            return id;
        }

        public String getPath() {
            return path;
        }

        private final Set<MidiReceiver> receivers = new HashSet<>();

        @Override
        public void addMidiReceiver(final MidiReceiver receiver) {
            receivers.add(receiver);
        }

        @Override
        public void put(final ShortMessage msg) {
            checkNotNull(msg);
            for (final MidiReceiver r : receivers) {
                r.put(msg);
            }
        }
    }

    private final Map<String, Entry> idToEndpointMap;

    public HttpEndpointManager(final MidiEventProducerMap eventProducerManager) {
        this.idToEndpointMap = new HashMap<>();
        this.epm = checkNotNull(eventProducerManager);
    }

    void addAll(final Collection<HttpEndpointConfig> dst) throws MidibridgeException {
        Preconditions.checkNotNull(dst);
        for (final HttpEndpointConfig dest : dst) {
            add(dest);
        }
    }

    public void add(final HttpEndpointConfig dest) throws MidibridgeException {
        Preconditions.checkNotNull(dest);
        if (idToEndpointMap.containsKey(dest.getId())) {
            throw new MidibridgeException("Multiple declarations of firebase destination with id = " + dest.getId());
        }

        final Entry entry = new Entry(dest.getId(), dest.getPath());
        idToEndpointMap.put(dest.getId(), entry);
        epm.put(dest.getId(), entry);
    }

    public Entry get(String id) throws MidibridgeException {
        if (idToEndpointMap.containsKey(id)) {
            return idToEndpointMap.get(id);
        } else {
            throw new MidibridgeException("Unknown firebase endpoint named: " + id);
        }
    }
}
