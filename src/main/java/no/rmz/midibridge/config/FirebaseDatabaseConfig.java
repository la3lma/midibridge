package no.rmz.midibridge.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import no.rmz.midibridge.MidibridgeException;

public class FirebaseDatabaseConfig {

    private String configFile;

    private String databaseName;

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public String toString() {
        return "FirebaseDatabaseConfig{" + "configFile=" + configFile + ", databaseName=" + databaseName + '}';
    }

    private FirebaseDatabase firebaseDatabase;

    private static FirebaseDatabase newDatabaseInstance(final String configFile, final String databaseName) throws MidibridgeException {
        try (final FileInputStream serviceAccount = new FileInputStream(configFile)) {
            final FirebaseOptions options = new FirebaseOptions.Builder().setCredential(FirebaseCredentials.fromCertificate(serviceAccount)).setDatabaseUrl("https://" + databaseName + ".firebaseio.com/").build();
            intializeFirebaseApp(options);
            return FirebaseDatabase.getInstance();
        } catch (IOException ex) {
            throw new MidibridgeException(ex);
        }
    }

    private static void intializeFirebaseApp(final FirebaseOptions options) {
        try {
            FirebaseApp.getInstance();
        } catch (Exception e) {
            FirebaseApp.initializeApp(options);
        }
    }

    public FirebaseDatabase getFirebaseDatabase() {
        if (firebaseDatabase == null) {
            try {
                firebaseDatabase = newDatabaseInstance(configFile, databaseName);
            } catch (MidibridgeException ex) {
                throw new RuntimeException("We're screwed");
            }
        }
        return firebaseDatabase;
    }

}
