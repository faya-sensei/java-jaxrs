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
        Optional<Method> GetMethod = Arrays.stream(HeartBeatResource.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GET.class))
                .findFirst();

        assertTrue(GetMethod.isPresent(), "One method under claas should be annotated with @GET");
    }

    @Nested
    public class IntegrationTest {

        private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

        private static final SeBootstrap.Instance instance = ServerFactory.createServer(entityManagerFactory);

        private static final URI uri = instance.configuration().baseUri();

        @Test
        public void testHeartBeat() {
            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/heartbeat").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        assertEquals("alive", jsonObject.getJsonString("status").getString());

                        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // 2011-12-03T10:15:30
                                .appendOptional(DateTimeFormatter.ISO_DATE_TIME) // 2011-12-03T10:15:30+01:00
                                .appendOptional(DateTimeFormatter.ISO_INSTANT)  // 2011-12-03T10:15:30Z
                                .toFormatter();

                        LocalDateTime time = LocalDateTime.parse(jsonObject.getJsonString("time").getString(), dateTimeFormatter);
                        Duration duration = Duration.between(time, LocalDateTime.now());

                        assertTrue(duration.compareTo(Duration.ofSeconds(10L)) <= 0);
                    }
                }
            }
        }
    }
}
