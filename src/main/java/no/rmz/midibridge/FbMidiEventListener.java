package no.rmz.midibridge;

import com.google.firebase.database.DataSnapshot;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FbMidiEventListener extends AbstractChildEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(FbMidiEventListener.class);
    private final Set<MidiReceiver> midiReceivers;

    public FbMidiEventListener() {
        this.midiReceivers = new HashSet<>();
    }

    public void addReceiver(final MidiReceiver midiReceiver) {
        midiReceivers.add(midiReceiver);
    }

    @Override
    public void onChildAdded(final DataSnapshot snapshot, final String previousChildName) {
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
            for (MidiReceiver midiReceiver : midiReceivers) {
                midiReceiver.put(midiEvent);
            }
            snapshot.getRef().removeValue();
        } catch (MidibridgeException e) {
            LOG.error("Couldn't transform req into FbPurchaseRequest", e);
        }
    }
}
