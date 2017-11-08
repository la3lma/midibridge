package no.rmz.midibridge.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.FirebaseDatabase;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import no.rmz.midibridge.FbMidiReadingEventGenerator;
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

    private final MidiDeviceManager midiDeviceManger = new MidiDeviceManager();
    private FirebaseEndpointManager firebaseEndpointManager;

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

    @Override
    public void run(
            final MidibridgeConfiguration configuration,
            final Environment environment) throws MidibridgeException {


        final String configFile = configuration.getFirebaseDatabaseConfig().getConfigFile();
        final String databaseName = configuration.getFirebaseDatabaseConfig().getDatabaseName();

        final FirebaseDatabase firebaseDatabase;
        try {
            firebaseDatabase = getDatabaseInstance(configFile, databaseName);
        } catch (MidibridgeException ex) {
            throw new RuntimeException("We're screwed");
        }

        firebaseEndpointManager = new FirebaseEndpointManager(firebaseDatabase);

        final List<FirebaseDestination> firebaseDestinations = configuration.getFirebaseDestinations();
        if (firebaseDestinations.size() != 1) {
            throw new RuntimeException("Not exactly one firebase destination to listen for");
        }
        final FirebaseDestination dest = firebaseDestinations.get(0);
        firebaseEndpointManager.addAll(firebaseDestinations);


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

        final MidiDeviceManager.Entry entry = midiDeviceManger.getEntryById("toReason");

        final MidiDestination midiDestination = midiDestinations.get(0);
        final String midiDeviceName = midiDestination.getMidiDeviceName();

        final List<MidiRoute> midiRoutes = configuration.getMidiRoutes();

        try {
            final FbMidiReadingEventGenerator midiReadingEventSource
                    = firebaseEndpointManager.get("testchannel").getGenerator();
            midiReadingEventSource.addMidiReceiver(entry.getReceiver());
        } catch (MidibridgeException e) {
            throw new RuntimeException(e);
        }

        final MidiEventResource resource = new MidiEventResource(entry.getReceiver());

        environment.jersey().register(resource);
    }
}
