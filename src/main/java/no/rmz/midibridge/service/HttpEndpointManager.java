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

    public final MidiReceiver getReceiverForPath(final String endpoint) {
        checkNotNull(endpoint);
        return pathToEndpointMap.get(endpoint);
    }

    public final static class Entry implements MidiEventProducer, MidiReceiver {

        private final String id;
        private final String path;

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

    private final Map<String, Entry> pathToEndpointMap;

    public HttpEndpointManager(final MidiEventProducerMap eventProducerManager) {
        this.pathToEndpointMap = new HashMap<>();
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


        final Entry entry = new Entry(dest.getId(), dest.getPath());
        epm.put(dest.getId(), entry);

        if (pathToEndpointMap.containsKey(dest.getPath())) {
            throw new MidibridgeException("Duplicate declaration for http destination " + dest.getPath());
        } else {
            pathToEndpointMap.put(dest.getPath(), entry);
        }
    }
}
