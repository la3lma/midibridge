package no.rmz.midibridge;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FBMidiReadingEventGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(FBMidiReadingEventGenerator.class);
    private String configFile;
    private String databaseName;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference midiInputMessages;
    private final String eventpath;

    public static ShortMessage asShortMessage(final FbMidiEventBean bean) {
        checkNotNull(bean);

        final MidiCmd cmd = MidiCmd.valueOf(bean.getCmd());
        final ShortMessage myMsg = new ShortMessage();
        switch (cmd) {
            case NOTE_ON:
                try {
                    myMsg.setMessage(ShortMessage.NOTE_ON, bean.getChan(), bean.getNote(), bean.getStrength());
                } catch (InvalidMidiDataException ex) {
                    throw new IllegalStateException(" couldn't make message", ex);
                }
                return myMsg;

            case NOTE_OFF:
                try {
                    myMsg.setMessage(ShortMessage.NOTE_OFF, bean.getChan(), bean.getNote(), bean.getStrength());
                } catch (InvalidMidiDataException ex) {
                    throw new IllegalStateException(" couldn't make message", ex);
                }
                return myMsg;

            default:
                LOG.info("Received MIDI unknown type of MIDI message: " + bean.toString());
        }
        throw new IllegalArgumentException("Could not produce a valid MIDI message from input");
    }


    public FBMidiReadingEventGenerator(
            final String databaseName,
            final String configFile,
            final String eventpath,
            final MidiReceiver midiReceiver) {
        this.configFile = Preconditions.checkNotNull(configFile);
        this.databaseName = Preconditions.checkNotNull(databaseName);
        this.eventpath = Preconditions.checkNotNull(eventpath);

        try (final FileInputStream serviceAccount = new FileInputStream(configFile)) {
            final FirebaseOptions options = new FirebaseOptions.Builder().setCredential(FirebaseCredentials.fromCertificate(serviceAccount)).setDatabaseUrl("https://" + databaseName + ".firebaseio.com/").build();
            try {
                FirebaseApp.getInstance();
            } catch (Exception e) {
                FirebaseApp.initializeApp(options);
            }
            this.firebaseDatabase = FirebaseDatabase.getInstance();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.midiInputMessages = firebaseDatabase.getReference(eventpath);

        this.midiInputMessages.addChildEventListener(new AbstractChildEventListener() {
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

                    final ShortMessage shortMidiMessage = asShortMessage(midiEvent);

                    midiReceiver.put(shortMidiMessage);

                    snapshot.getRef().removeValue();
                } catch (Exception e) {
                    LOG.error("Couldn't transform req into FbPurchaseRequest", e);
                }
            }
        });
    }
}
