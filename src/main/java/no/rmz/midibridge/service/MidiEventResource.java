package no.rmz.midibridge.service;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import no.rmz.midibridge.FbMidiEventBean;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;

@Path("/midievent")
public class MidiEventResource {

    private MidiReceiver mr;

    public MidiEventResource() {
    }

    MidiEventResource(MidiReceiver mr) {
        this.mr = checkNotNull(mr);
    }

    @POST
    // @Timed
    public Response sayHello(final FbMidiEventBean event) throws MidibridgeException {
        this.mr.put(event);
        return Response.noContent().build();
    }
}
