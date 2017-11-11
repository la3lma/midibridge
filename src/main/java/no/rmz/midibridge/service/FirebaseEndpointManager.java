package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Collection;
import no.rmz.midibridge.FbMidiReadingEventGenerator;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.FirebaseDestination;

public final class FirebaseEndpointManager {

    private final MidiEventProducerMap epm;

    public final static class Entry {

        final FbMidiReadingEventGenerator generator;

        public Entry(final String path, FirebaseDatabase db) throws MidibridgeException {
            this.generator = new FbMidiReadingEventGenerator(db, path);
        }

        public FbMidiReadingEventGenerator getGenerator() {
            return generator;
        }
    }

    final FirebaseDatabase db;

    public FirebaseEndpointManager(
            final FirebaseDatabase db,
            final MidiEventProducerMap eventProducerManager) {
        this.db = checkNotNull(db);
        this.epm = checkNotNull(eventProducerManager);
    }

    void addAll(final Collection<FirebaseDestination> firebaseDestinations) throws MidibridgeException {
        Preconditions.checkNotNull(firebaseDestinations);
        for (final FirebaseDestination dest : firebaseDestinations) {
            add(dest);
        }
    }
    public void add(final FirebaseDestination dest) throws MidibridgeException {
        Preconditions.checkNotNull(dest);

        final Entry entry = new Entry(dest.getPath(), db);
        epm.put(dest.getId(), entry.getGenerator());
    }
}
