package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import no.rmz.midibridge.MidiEventProducer;
import no.rmz.midibridge.MidibridgeException;

public final class MidiEventProducerMap {

    private final Map<String, MidiEventProducer> map;

    public MidiEventProducerMap() {
        this.map = new HashMap<>();
    }

    public void put(final String id, final MidiEventProducer entry) throws MidibridgeException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(entry);
        if (map.containsKey(id)) {
            throw new MidibridgeException("Duplicate midi event producer: " + id);
        } else {
            map.put(id, entry);
        }
    }

    public MidiEventProducer get(final String id) throws MidibridgeException {
        Preconditions.checkNotNull(id);
        if (map.containsKey(id)) {
            return map.get(id);
        } else {
            throw new MidibridgeException("Attempt to get unknown midi event producer: " + id);
        }
    }
}
