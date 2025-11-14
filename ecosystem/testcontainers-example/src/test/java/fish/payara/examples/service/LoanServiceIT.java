package fish.payara.examples.service;

import fish.payara.examples.domain.Loan;
import fish.payara.examples.domain.Patron;
import fish.payara.examples.domain.Book;
import fish.payara.examples.testcontainers.PayaraMicroContainer;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoanServiceIT {

    private static final String PAYARA_MICRO_VERSION = "6.2025.10";
    private static final int EXPOSED_PORT = 8080;

    @Container
    private static final PayaraMicroContainer payara = new PayaraMicroContainer(
            DockerImageName.parse("payara/micro:" + PAYARA_MICRO_VERSION))
            .withExposedPorts(EXPOSED_PORT)
            .withDeploymentPath("target/testcontainers-example-1.0.0.war");

    private Client client;
    private WebTarget baseTarget;

    @BeforeEach
    void setUp() {
        client = ClientBuilder.newClient();
        String appUrl = payara.getApplicationUrl();
        String separator = appUrl.endsWith("/") ? "" : "/";
        String baseUri = appUrl + separator + "application/resources/loans";
        baseTarget = client.target(baseUri);
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    @Order(1)
    void testCreateAndRetrieveLoan() throws Exception {
        Book book = new Book();
        book.setTitle("The Shining");
        book.setAuthor("Stephen King");
        book.setIsbn("978-0-385-12167-5");
        book.setPages(447);
        Patron patron = new Patron();
        patron.setName("Jack Torrance");
        patron.setAddress("Evergreen Terrace");
        patron.setEmail("jack.torrance@mail.com");
        patron.setPatronID("P042");
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setPatron(patron);

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(loan, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.CREATED.getStatusCode(), createResponse.getStatus());
        String location = createResponse.getHeaderString("Location");
        assertNotNull(location);

        Loan retrieved = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(Loan.class);

        assertNotNull(retrieved);
        assertEquals("978-0-385-12167-5", retrieved.getBook().getIsbn());
        assertEquals("P042", retrieved.getPatron().getPatronID());
    }

    @Test
    @Order(2)
    void testFindAllLoans() {
        Response response = baseTarget.request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Loan> loans = response.readEntity(new GenericType<>() {
        });
        assertNotNull(loans);
    }

    @Test
    @Order(3)
    void testUpdateLoan() {
        Book book = new Book();
        book.setTitle("The Shining");
        book.setAuthor("Stephen King");
        book.setIsbn("978-0-385-12167-5");
        book.setPages(447);
        Patron patron = new Patron();
        patron.setName("Jack Torrance");
        patron.setAddress("Evergreen Terrace");
        patron.setEmail("jack.torrance@mail.com");
        patron.setPatronID("P042");

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setPatron(patron);

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(loan, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");

        patron.setPatronID("P666");

        loan.setPatron(patron);
        Response updateResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(loan, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), updateResponse.getStatus());
    }

    @Test
    @Order(4)
    void testDeleteLoan() {
        Book book = new Book();
        book.setTitle("The Shining");
        book.setAuthor("Stephen King");
        book.setIsbn("978-0-385-12167-5");
        book.setPages(447);
        Patron patron = new Patron();
        patron.setName("Jack Torrance");
        patron.setAddress("Evergreen Terrace");
        patron.setEmail("jack.torrance@mail.com");
        patron.setPatronID("P042");

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setPatron(patron);

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(loan, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");
        Response deleteResponse = client.target(location).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
}
