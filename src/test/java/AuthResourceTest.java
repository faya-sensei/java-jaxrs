import factories.ServerFactory;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.repositories.IRepository;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.net.URI;

import static factories.UserFactory.createInMemoryUserRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AuthResourceTest {

    @Nested
    public class UnitTest {
        private static final IRepository<UserEntity> userRepository = createInMemoryUserRepository();


    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class IntegrationTest {

        private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

        private static final SeBootstrap.Instance instance = ServerFactory.createServer(entityManagerFactory);

        private static final URI uri = instance.configuration().baseUri();

        @Test
        @Order(1)
        public void testRegister() {
            final JsonObject registerUser = Json.createObjectBuilder()
                    .add("name", "User")
                    .add("password", "Password")
                    .build();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth/register").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(registerUser, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        assertFalse(jsonObject.getJsonString("token").getString().isEmpty());
                    }
                }
            }
        }

        @Test
        @Order(2)
        public void testLogin() {
            final JsonObject loginUser = Json.createObjectBuilder()
                    .add("name", "User")
                    .add("password", "Password")
                    .build();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth/login").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(loginUser, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        assertFalse(jsonObject.getJsonString("token").getString().isEmpty());
                    }
                }
            }
        }
    }
}
