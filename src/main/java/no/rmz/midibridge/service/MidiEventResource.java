package no.rmz.midibridge.service;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import no.rmz.midibridge.FbMidiEventBean;
import no.rmz.midibridge.MidiReceiver;
import no.rmz.midibridge.MidibridgeException;

@Path("/midievent/{endpoint}")
public class MidiEventResource {

    private final HttpEndpointManager httpEndpointManager;

    MidiEventResource(final HttpEndpointManager httpEndpointManager) {
        this.httpEndpointManager = checkNotNull(httpEndpointManager);
    }

    @POST
    // @Timed
    public Response sayHello(final @PathParam("endpoint") String endpoint, final FbMidiEventBean event) throws MidibridgeException {
        final MidiReceiver mr;
        mr = this.httpEndpointManager.getReceiverForPath(endpoint);
        if (mr == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mr.put(event);
        return Response.noContent().build();
    }
}
