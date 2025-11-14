package fish.payara.examples.rest;

import fish.payara.examples.domain.Book;
import fish.payara.examples.service.BookService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path("books")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class BookResource {

    @Inject
    private BookService bookService;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAllBooks() {
        List<Book> books = bookService.findAll();
        return Response.ok(books).build();
    }

    @GET
    @Path("{isbn}")
    public Response getBook(@PathParam("isbn") String isbn) {
        Book book = bookService.find(isbn);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(book).build();
    }

    @POST
    public Response createBook(Book book) {
        bookService.create(book);
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(book.getIsbn())
                .build();
        return Response.created(uri).entity(book).build();
    }

    @PUT
    @Path("{isbn}")
    public Response updateBook(@PathParam("isbn") String isbn, Book updatedBook) {
        Book existing = bookService.find(isbn);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        updatedBook.setIsbn(isbn);
        Book result = bookService.edit(updatedBook);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("{isbn}")
    public Response deleteBook(@PathParam("isbn") String isbn) {
        Book book = bookService.find(isbn);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        bookService.remove(book);
        return Response.noContent().build();
    }
}
