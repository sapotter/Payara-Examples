package fish.payara.examples.resource;

import fish.payara.examples.domain.Loan;
import fish.payara.examples.service.LoanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path("loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    @Inject
    private LoanService loanService;

    @GET
    public Response getAllLoans() {
        List<Loan> loans = loanService.findAll();
        return Response.ok(loans).build();
    }

    @GET
    @Path("{id}")
    public Response getLoanById(@PathParam("id") Integer id) {
        Loan loan = loanService.find(id);
        if (loan == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(loan).build();
    }

    @POST
    public Response createLoan(Loan loan, @Context UriInfo uriInfo) {
        loanService.create(loan);
        URI uri = uriInfo.getAbsolutePathBuilder().path(loan.getLoanID().toString()).build();
        return Response.created(uri).entity(loan).build();
    }

    @PUT
    @Path("{id}")
    public Response updateLoan(@PathParam("id") Integer id, Loan loan) {
        Loan existing = loanService.find(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        loan.setLoanID(id);
        loanService.edit(loan);
        return Response.ok(loan).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteLoan(@PathParam("id") Integer id) {
        Loan loan = loanService.find(id);
        if (loan == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        loanService.remove(loan);
        return Response.noContent().build();
    }
}
