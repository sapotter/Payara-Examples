package fish.payara.examples.rest;

import fish.payara.examples.domain.Librarian;
import fish.payara.examples.service.LibrarianService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path("librarians")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class LibrarianResource {

    @Inject
    private LibrarianService librarianService;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAllLibrarians() {
        List<Librarian> librarians = librarianService.findAll();
        return Response.ok(librarians).build();
    }

    @GET
    @Path("{librarianID}")
    public Response getLibrarian(@PathParam("librarianID") String librarianID) {
        Librarian librarian = librarianService.find(librarianID);
        if (librarian == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(librarian).build();
    }

    @POST
    public Response createLibrarian(Librarian librarian) {
        librarianService.create(librarian);
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(librarian.getLibrarianID())
                .build();
        return Response.created(uri).entity(librarian).build();
    }

    @PUT
    @Path("{librarianID}")
    public Response updateLibrarian(@PathParam("librarianID") String librarianID, Librarian updatedLibrarian) {
        Librarian existing = librarianService.find(librarianID);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        updatedLibrarian.setLibrarianID(librarianID);
        Librarian result = librarianService.edit(updatedLibrarian);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("{librarianID}")
    public Response deleteLibrarian(@PathParam("librarianID") String librarianID) {
        Librarian librarian = librarianService.find(librarianID);
        if (librarian == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        librarianService.remove(librarian);
        return Response.noContent().build();
    }
}
