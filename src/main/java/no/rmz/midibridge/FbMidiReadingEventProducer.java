package no.rmz.midibridge;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FbMidiReadingEventProducer implements MidiEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(FbMidiReadingEventProducer.class);

    private final FbMidiEventListener listener;

    @Override
    public void addMidiReceiver(MidiReceiver receiver) {
        checkNotNull(receiver);
        listener.addReceiver(receiver);
    }

    public FbMidiReadingEventProducer(
            final FirebaseDatabase firebaseDatabase,
            final String eventpath) throws MidibridgeException {
        checkNotNull(firebaseDatabase);

        checkNotNull(eventpath);
        this.listener = new FbMidiEventListener();

        final DatabaseReference pathToMidiMessages = firebaseDatabase.getReference(eventpath);
        pathToMidiMessages.addChildEventListener(listener);
    }
}
