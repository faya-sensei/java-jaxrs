import com.sun.net.httpserver.HttpServer;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.faya.sensei.App;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatIntegrationTest {

    @Test
    public void testHeartBeat() throws IOException {
        final HttpServer server = App.startServer(0);

        try (Client client = ClientBuilder.newClient()) {
            final WebTarget target = client.target("http://localhost:%d/api/heartbeat".formatted(server.getAddress().getPort()));

            JsonObject response = target.request(MediaType.APPLICATION_JSON).get(JsonObject.class);

            assertEquals("alive", response.getJsonString("status").getString());

            DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                    .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // 2011-12-03T10:15:30
                    .appendOptional(DateTimeFormatter.ISO_DATE_TIME) // 2011-12-03T10:15:30+01:00
                    .appendOptional(DateTimeFormatter.ISO_INSTANT)  // 2011-12-03T10:15:30Z
                    .toFormatter();

            LocalDateTime time = LocalDateTime.parse(response.getJsonString("time").getString(), dateTimeFormatter);

            assertNotEquals(Duration.between(LocalDateTime.now(), time).compareTo(Duration.ofSeconds(10L)), 0);
        }
    }
}
