package fish.payara.examples.resource;

import fish.payara.examples.domain.Patron;
import fish.payara.examples.service.PatronService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path("patrons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatronResource {

    @Inject
    private PatronService patronService;

    @GET
    public Response getAllPatrons() {
        List<Patron> patrons = patronService.findAll();
        return Response.ok(patrons).build();
    }

    @GET
    @Path("{id}")
    public Response getPatronById(@PathParam("id") String id) {
        Patron patron = patronService.find(id);
        if (patron == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(patron).build();
    }

    @POST
    public Response createPatron(Patron patron, @Context UriInfo uriInfo) {
        patronService.create(patron);
        URI uri = uriInfo.getAbsolutePathBuilder().path(patron.getPatronID()).build();
        return Response.created(uri).entity(patron).build();
    }

    @PUT
    @Path("{id}")
    public Response updatePatron(@PathParam("id") String id, Patron patron) {
        Patron existing = patronService.find(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        patron.setPatronID(id);
        patronService.edit(patron);
        return Response.ok(patron).build();
    }

    @DELETE
    @Path("{id}")
    public Response deletePatron(@PathParam("id") String id) {
        Patron patron = patronService.find(id);
        if (patron == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        patronService.remove(patron);
        return Response.noContent().build();
    }
}
