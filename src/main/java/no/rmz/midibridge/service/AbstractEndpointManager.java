package no.rmz.midibridge.service;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.logging.Logger;
import no.rmz.midibridge.MidibridgeException;

public abstract class AbstractEndpointManager<T extends NamedEntity> {

    protected static final Logger LOG = Logger.getLogger(AbstractEndpointManager.class.getName());
    protected final MidiEventProducerMap epm;

    public AbstractEndpointManager(final MidiEventProducerMap epm) {
        this.epm = checkNotNull(epm);
    }

    public void addAll(final Collection<T> endpoints) throws MidibridgeException {
        Preconditions.checkNotNull(endpoints);
        for (final T dest : endpoints) {
            add(dest);
        }
    }

    public void add(final T endpoint) throws MidibridgeException {
        Preconditions.checkNotNull(endpoint);
        final MidiEventProducerEntry entry = newEntry(endpoint);
        epm.put(endpoint.getId(), entry.getMidiEventProducer());
    }

    abstract MidiEventProducerEntry newEntry(T t) throws MidibridgeException;
}
