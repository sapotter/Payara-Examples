package fish.payara.examples.service;

import fish.payara.examples.domain.Librarian;
import fish.payara.examples.testcontainers.PayaraMicroContainer;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LibrarianServiceIT {

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
        String baseUri = appUrl + separator + "application/resources/librarians";
        baseTarget = client.target(baseUri);
    }

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    @Order(1)
    void testCreateAndRetrieveLibrarian() throws Exception {
        Librarian librarian = new Librarian();
        librarian.setName("Alice Johnson");

        Response createResponse = null;
        String respBody = null;

        for (int i = 0; i < 20; i++) {
            createResponse = baseTarget
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(librarian, MediaType.APPLICATION_JSON));
            if (createResponse.getStatus() != 404) {
                break;
            }
            Thread.sleep(1000);
        }

        if (createResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
            try {
                respBody = createResponse.readEntity(String.class);
            } catch (Exception e) {
                respBody = "<no body>";
            }
            String logs = payara.getLogs();
            String msg = String.format("POST failed, status=%d, body=%s, container logs:\n%s", createResponse.getStatus(), respBody, logs);
            assertEquals(Response.Status.CREATED.getStatusCode(), createResponse.getStatus(), msg);
        }

        String location = createResponse.getHeaderString("Location");
        assertNotNull(location, "Location header missing after create");

        Response getResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Librarian retrieved = getResponse.readEntity(Librarian.class);
        assertNotNull(retrieved);
        assertEquals("Alice Johnson", retrieved.getName());
    }

    @Test
    @Order(2)
    void testFindAllLibrarians() throws Exception {
        Response response = null;
        String respBody = null;

        for (int i = 0; i < 20; i++) {
            response = baseTarget.request(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != 404) {
                break;
            }
            Thread.sleep(500);
        }

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            try {
                respBody = response.readEntity(String.class);
            } catch (Exception e) {
                respBody = "<no body>";
            }
            String logs = payara.getLogs();
            String msg = String.format("GET all failed, status=%d, body=%s, logs:\n%s", response.getStatus(), respBody, logs);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), msg);
        }

        List<Librarian> librarians = response.readEntity(new GenericType<List<Librarian>>() {
        });
        assertNotNull(librarians);
        assertFalse(librarians.isEmpty(), "Expected at least one librarian");
    }

    @Test
    @Order(3)
    void testUpdateLibrarian() throws Exception {
        Librarian librarian = new Librarian();
        librarian.setName("Bob Williams");

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(librarian, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");
        assertNotNull(location);

        Librarian created = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(Librarian.class);

        created.setName("Bob Updated");

        Response updateResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(created, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), updateResponse.getStatus());

        Librarian updated = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(Librarian.class);

        assertEquals("Bob Updated", updated.getName());
    }

    @Test
    @Order(4)
    void testDeleteLibrarian() throws Exception {
        Librarian librarian = new Librarian();
        librarian.setName("Charlie Brown");

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(librarian, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");
        assertNotNull(location);

        Response deleteResponse = client.target(location)
                .request()
                .delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());

        Response getResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }
}
