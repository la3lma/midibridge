package no.rmz.midibridge;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FBMidiReadingEventGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(FBMidiReadingEventGenerator.class);

    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference midiInputMessages;

    public final static class FbMidiEventListener extends AbstractChildEventListener {

        private final MidiReceiver midiReceiver;

        public FbMidiEventListener(MidiReceiver midiReceiver) {
            this.midiReceiver = checkNotNull(midiReceiver);
        }


        @Override
        public void onChildAdded(final DataSnapshot snapshot, final String previousChildName) {
            LOG.info("onChildAdded");
            if (snapshot == null) {
                LOG.error("dataSnapshot can't be null");
                return;
            }
            if (!snapshot.exists()) {
                LOG.error("dataSnapshot must exist");
                return;
            }
            try {
                final FbMidiEventBean midiEvent = snapshot.getValue(FbMidiEventBean.class);
                LOG.info("just read midi event bean  from Firebase " + midiEvent);

                midiReceiver.put(midiEvent);

                snapshot.getRef().removeValue();
            } catch (Exception e) {
                LOG.error("Couldn't transform req into FbPurchaseRequest", e);
            }
        }
    }

    public FBMidiReadingEventGenerator(
            final String databaseName,
            final String configFile,
            final String eventpath,
            final MidiReceiver midiReceiver) throws MidibridgeException {
        checkNotNull(configFile);
        checkNotNull(databaseName);
        checkNotNull(eventpath);

        this.firebaseDatabase = getDatabaseInstance(configFile, databaseName);
        final ChildEventListener listener = new FbMidiEventListener(midiReceiver);
        this.midiInputMessages = firebaseDatabase.getReference(eventpath);
        this.midiInputMessages.addChildEventListener(listener);
    }

    private static FirebaseDatabase getDatabaseInstance(final String configFile, final String databaseName) throws MidibridgeException {
        try (final FileInputStream serviceAccount = new FileInputStream(configFile)) {
            final FirebaseOptions options = new FirebaseOptions.Builder().setCredential(FirebaseCredentials.fromCertificate(serviceAccount)).setDatabaseUrl("https://" + databaseName + ".firebaseio.com/").build();
            try {
                FirebaseApp.getInstance();
            } catch (Exception e) {
                FirebaseApp.initializeApp(options);
            }
            return FirebaseDatabase.getInstance();
        } catch (IOException ex) {
            throw new MidibridgeException(ex);
        }
    }
}
