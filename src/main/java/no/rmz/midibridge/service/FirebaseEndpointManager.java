package no.rmz.midibridge.service;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.database.FirebaseDatabase;
import no.rmz.midibridge.FbMidiReadingEventProducer;
import no.rmz.midibridge.MidiEventProducer;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.FirebaseDestination;

public final class FirebaseEndpointManager extends AbstractEndpointManager<FirebaseDestination> {


    public final static class Entry implements MidiEventProducerEntry {

        private final FbMidiReadingEventProducer generator;

        public Entry(final String path, FirebaseDatabase db) throws MidibridgeException {
            this.generator = new FbMidiReadingEventProducer(db, path);
        }

        @Override
        public MidiEventProducer getMidiEventProducer() {
            return generator;
        }
    }

    final FirebaseDatabase db;

    public FirebaseEndpointManager(
            final FirebaseDatabase db,
            final MidiEventProducerMap epm) {
        super(epm);
        this.db = checkNotNull(db);
    }

    @Override
    MidiEventProducerEntry newEntry(FirebaseDestination dest) throws MidibridgeException {
        return new Entry(dest.getPath(), db);
    }
}
