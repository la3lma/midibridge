package no.rmz.midibridge.service;

import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import no.rmz.midibridge.BufferedMidiReceiver;
import no.rmz.midibridge.FbMidiReadingEventGenerator;
import no.rmz.midibridge.IacDeviceUtilities;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.FirebaseDestination;
import no.rmz.midibridge.config.MidiDestination;
import no.rmz.midibridge.config.MidiRoute;
import no.rmz.midibridge.config.MidibridgeConfiguration;

public class MidiBridgeService extends Application<MidibridgeConfiguration> {

    public static void main(String[] args) throws Exception {
        args = new String[]{"server", "midibridge-config.yaml"};
        new MidiBridgeService().run(args);
    }

    @Override
    public String getName() {
        return "midibridge";
    }

    @Override
    public void initialize(Bootstrap<MidibridgeConfiguration> bootstrap) {
        // nothing to do yet
    }

    public final static class MidiDeviceManager {

        public final static class Entry {

            private final String id;
            private final MidiDestination dest;
            private final MidiReceiver receiver;

            public Entry(final MidiDestination dest) throws MidibridgeException {
                this.dest = checkNotNull(dest);
                this.id = dest.getId();

                final MidiDevice midiDevice
                        = IacDeviceUtilities.getMidiReceivingDevice(dest.getMidiDeviceName());
                try {
                    this.receiver = new BufferedMidiReceiver(midiDevice.getReceiver());
                } catch (MidiUnavailableException ex) {
                    throw new MidibridgeException(ex);
                }
            }

            public String getId() {
                return id;
            }

            public MidiDestination getDest() {
                return dest;
            }

            public MidiReceiver getReceiver() {
                return receiver;
            }

        }

        private final Map<String, Entry> devices;

        public MidiDeviceManager() {
            this.devices = new HashMap<>();
        }

        public void addDestination(final MidiDestination dest) throws MidibridgeException {
            checkNotNull(dest);
            final Entry entry = new Entry(dest);
            devices.put(entry.getId(), entry);
        }

        public void addAll(final List<MidiDestination> midiDestinations) throws MidibridgeException {
            checkNotNull(midiDestinations);
            for (MidiDestination d : midiDestinations) {
                addDestination(d);
            }
        }

        public Entry getEntryById(String id) {
            checkNotNull(id);
            return devices.get(id);
        }
    }

    private final MidiDeviceManager midiDeviceManger = new MidiDeviceManager();

    @Override
    public void run(
            final MidibridgeConfiguration configuration,
            final Environment environment) {


        final String configFile = configuration.getFirebaseDatabaseConfig().getConfigFile();
        final String databaseName = configuration.getFirebaseDatabaseConfig().getDatabaseName();

        final List<FirebaseDestination> firebaseDestinations = configuration.getFirebaseDestinations();
        if (firebaseDestinations.size() != 1) {
            throw new RuntimeException("Not exactly one firebase destination to listen for");
        }
        final FirebaseDestination dest = firebaseDestinations.get(0);

        final String pathToListenForEventsIn = dest.getPath();

        final List<MidiDestination> midiDestinations = configuration.getMidiDestinations();
        if (midiDestinations.size() != 1) {
            throw new RuntimeException("Not exactly one midi destination to send to");
        }
        try {
            midiDeviceManger.addAll(midiDestinations);
        } catch (MidibridgeException ex) {
            throw new RuntimeException("We're screwed", ex);
        }

        MidiDeviceManager.Entry entry = midiDeviceManger.getEntryById("toReason");


        final MidiDestination midiDestination = midiDestinations.get(0);
        final String midiDeviceName = midiDestination.getMidiDeviceName();

        final List<MidiRoute> midiRoutes = configuration.getMidiRoutes();

        try {
            // XXX Tie this into the lifecycle of dropwizard objects.
            final FbMidiReadingEventGenerator midiReadingEventSource
                    = new FbMidiReadingEventGenerator(
                            databaseName,
                            configFile,
                            pathToListenForEventsIn,
                            entry.getReceiver());

        } catch (MidibridgeException e) {
            throw new RuntimeException(e);
        }

        final MidiEventResource resource = new MidiEventResource(entry.getReceiver());

        environment.jersey().register(resource);
    }
}
