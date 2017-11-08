package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import no.rmz.midibridge.FbMidiReadingEventGenerator;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.FirebaseDestination;

public final class FirebaseEndpointManager {

    public final static class Entry {

        final FbMidiReadingEventGenerator generator;

        public Entry(final String path, FirebaseDatabase db) throws MidibridgeException {
            this.generator = new FbMidiReadingEventGenerator(db, path);
        }

        public FbMidiReadingEventGenerator getGenerator() {
            return generator;
        }

    }

    private final Map<String, Entry> idToEndpointMap;
    final FirebaseDatabase db;

    public FirebaseEndpointManager(final FirebaseDatabase db) {
        this.idToEndpointMap = new HashMap<>();
        this.db = checkNotNull(db);
    }

    void addAll(final Collection<FirebaseDestination> firebaseDestinations) throws MidibridgeException {
        Preconditions.checkNotNull(firebaseDestinations);
        for (final FirebaseDestination dest : firebaseDestinations) {
            add(dest);
        }
    }

    public void add(final FirebaseDestination dest) throws MidibridgeException {
        Preconditions.checkNotNull(dest);
        if (idToEndpointMap.containsKey(dest.getId())) {
            throw new MidibridgeException("Multiple declarations of firebase destination with id = " + dest.getId());
        }

        final Entry entry = new Entry(dest.getPath(), db);
        idToEndpointMap.put(dest.getId(), entry);
    }

    public Entry get(String id) throws MidibridgeException {
        if (idToEndpointMap.containsKey(id)) {
            return idToEndpointMap.get(id);
        } else {
            throw new MidibridgeException("Unknown firebase endpoint named: " + id);
        }
    }
}
