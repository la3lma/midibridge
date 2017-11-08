package no.rmz.midibridge.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import no.rmz.midibridge.FbMidiEventBean;

@Path("/midievent")
public class HelloWorldResource {

    public HelloWorldResource() {
    }

    @POST
    // @Timed
    public Response sayHello(final FbMidiEventBean event) {
        return Response.noContent().build();
    }
}
