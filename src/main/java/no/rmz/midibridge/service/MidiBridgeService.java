package no.rmz.midibridge.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import no.rmz.midibridge.MidibridgeException;
import no.rmz.midibridge.config.MidibridgeConfiguration;

public class MidiBridgeService extends Application<MidibridgeConfiguration> {

    public static void main(String[] args) throws Exception {
        // XXX Setting args to make it simpler to debug, not what we should do when
        //     building standalone jars.
        args = new String[]{"server", "midibridge-config.yaml"};
        new MidiBridgeService().run(args);
    }

    public MidiBridgeService() {

    }

    @Override
    public String getName() {
        return "midibridge";
    }

    @Override
    public void initialize(Bootstrap<MidibridgeConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(
            final MidibridgeConfiguration configuration,
            final Environment environment) {

        final MidiRoutingManager midiRoutingManager;
        try {
            midiRoutingManager = new MidiRoutingManager(configuration);
        } catch (MidibridgeException ex) {
            throw new RuntimeException(ex); // Ghetto!
        }
        final MidiEventResource resource = midiRoutingManager.getResource();

        environment.jersey().register(resource);
    }
}
