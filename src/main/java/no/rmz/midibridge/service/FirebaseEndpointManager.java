package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.FirebaseDestination;

public final class FirebaseEndpointManager {

    private final Map<String, String> idToEndpointMap;

    public FirebaseEndpointManager() {
        this.idToEndpointMap = new HashMap<>();
    }

    void addAll(final Collection<FirebaseDestination> firebaseDestinations) {
        Preconditions.checkNotNull(firebaseDestinations);
        for (final FirebaseDestination dest : firebaseDestinations) {
            add(dest);
        }
    }

    public void add(final FirebaseDestination dest) {
        Preconditions.checkNotNull(dest);
        idToEndpointMap.put(dest.getId(), dest.getPath());
    }

    public String get(String id) throws MidibridgeException {
        if (idToEndpointMap.containsKey(id)) {
            return idToEndpointMap.get(id);
        } else {
            throw new MidibridgeException("Unknown firebase endpoint named: " + id);
        }
    }
}
