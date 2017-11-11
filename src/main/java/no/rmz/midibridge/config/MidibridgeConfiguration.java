package no.rmz.midibridge.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import no.rmz.midibridge.service.HttpEndpointConfig;

public final class MidibridgeConfiguration extends Configuration {

    private FirebaseDatabaseConfig firebaseDatabaseConfig;

    private List<FirebaseDestination> firebaseDestinations;

    private List<MidiDestination> midiDestinations;

    private List<MidiRoute> midiRoutes;

    private List<UdpEndpoint> udpDestinations;

    private String httpMidiRoute;
    private Collection<HttpEndpointConfig> httpDestinations;

    public FirebaseDatabaseConfig getFirebaseDatabaseConfig() {
        return firebaseDatabaseConfig;
    }

    public void setFirebaseDatabaseConfig(final FirebaseDatabaseConfig firebaseDatabaseConfig) {
        this.firebaseDatabaseConfig = firebaseDatabaseConfig;
    }

    @JsonProperty
    public List<UdpEndpoint> getUdpDestinations() {
        return Collections.unmodifiableList(udpDestinations);
    }

    public void setUdpDestinations(List<UdpEndpoint> udpEndpoints) {
        this.udpDestinations = udpEndpoints;
    }

    @JsonProperty
    public List<MidiDestination> getMidiDestinations() {
        return Collections.unmodifiableList(midiDestinations);
    }

    public void setMidiDestinations(final List<MidiDestination> midiDestinations) {
        this.midiDestinations = midiDestinations;
    }

    @JsonProperty
    public List<MidiRoute> getMidiRoutes() {
        return Collections.unmodifiableList(midiRoutes);
    }

    public void setMidiRoutes(final List<MidiRoute> midiRoutes) {
        this.midiRoutes = midiRoutes;
    }

    public List<FirebaseDestination> getFirebaseDestinations() {
        return firebaseDestinations;
    }

    public void setFirebaseDestinations(final List<FirebaseDestination> firebaseDestinations) {
        this.firebaseDestinations = firebaseDestinations;
    }

    public String getHttpMidiRoute() {
        return httpMidiRoute;
    }

    @JsonProperty
    public void setHttpMidiRoute(String httpMidiRoute) {
        this.httpMidiRoute = httpMidiRoute;
    }

    @JsonProperty
    public Collection<HttpEndpointConfig> getHttpDestinations() {
        return this.httpDestinations;
    }

    public void setHttpDestinations(Collection<HttpEndpointConfig> httpDestinations) {
        this.httpDestinations = httpDestinations;
    }

    @Override
    public String toString() {
        return "MidibridgeConfiguration{" + "firebaseDatabaseConfig=" + firebaseDatabaseConfig + ", firebaseDestinations=" + firebaseDestinations + ", midiDestinations=" + midiDestinations + ", midiRoutes=" + midiRoutes + ", udpDestinations=" + udpDestinations + ", httpMidiRoute=" + httpMidiRoute + '}';
    }

}
