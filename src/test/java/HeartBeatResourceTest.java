import factories.ServerFactory;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.faya.sensei.resources.endpoints.HeartBeatResource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeartBeatResourceTest {

    @Test
    public void testAnnotations() {
        final Optional<Method> getMethod = Arrays.stream(HeartBeatResource.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GET.class))
                .findFirst();

        assertTrue(getMethod.isPresent(), "One method under claas should be annotated with @GET");
    }

    @Nested
    public class IntegrationTest {

        private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

        private static final SeBootstrap.Instance instance = ServerFactory.createServer(entityManagerFactory);

        private static final URI uri = instance.configuration().baseUri();

        @Test
        public void testHeartBeat() {
            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/heartbeat").build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON).get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readObject();

                        final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // 2011-12-03T10:15:30
                                .appendOptional(DateTimeFormatter.ISO_DATE_TIME) // 2011-12-03T10:15:30+01:00
                                .appendOptional(DateTimeFormatter.ISO_INSTANT)  // 2011-12-03T10:15:30Z
                                .toFormatter();
                        final LocalDateTime actualTime = LocalDateTime.parse(
                                actualJsonObject.getJsonString("time").getString(),
                                dateTimeFormatter
                        );
                        final String actualStatus = actualJsonObject.getJsonString("status").getString();

                        final Duration epsilon = Duration.ofSeconds(1L);

                        assertTrue(Duration.between(actualTime, LocalDateTime.now()).compareTo(epsilon) <= 0);
                        assertEquals("alive", actualStatus);
                    }
                }
            }
        }
    }
}
