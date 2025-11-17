package fish.payara.examples.service;

import fish.payara.examples.domain.Patron;
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
class PatronServiceIT {

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
        String baseUri = appUrl + separator + "application/resources/patrons";
        baseTarget = client.target(baseUri);
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    @Order(1)
    void testCreateAndRetrievePatron() {
        Patron patron = new Patron();
        patron.setName("John Doe");
        patron.setEmail("john@example.com");

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(patron, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.CREATED.getStatusCode(), createResponse.getStatus());
        String location = createResponse.getHeaderString("Location");
        assertNotNull(location);

        Patron retrieved = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(Patron.class);

        assertNotNull(retrieved);
        assertEquals("John Doe", retrieved.getName());
    }

    @Test
    @Order(2)
    void testFindAllPatrons() {
        Response response = baseTarget.request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Patron> patrons = response.readEntity(new GenericType<>() {
        });
        assertNotNull(patrons);
    }

    @Test
    @Order(3)
    void testUpdatePatron() {
        Patron patron = new Patron();
        patron.setName("Jane Doe");
        patron.setEmail("jane@example.com");

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(patron, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");

        patron.setEmail("jane.updated@example.com");
        Response updateResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(patron, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), updateResponse.getStatus());
    }

    @Test
    @Order(4)
    void testDeletePatron() {
        Patron patron = new Patron();
        patron.setName("Mark Twain");
        patron.setEmail("mark@example.com");

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(patron, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");
        Response deleteResponse = client.target(location).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
}
