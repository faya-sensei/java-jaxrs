import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.core.Response;
import org.faya.sensei.resources.HeartBeatResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class HeartBeatUnitTest {

    @InjectMocks
    public HeartBeatResource heartBeatResource;

    @BeforeEach
    public void setUp() {
        try {
            MockitoAnnotations.openMocks(this).close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHeartBeat() {
        Response response = heartBeatResource.getHeartbeat();

        try (JsonReader jsonReader = Json.createReader(new StringReader(response.getEntity().toString()))) {
            JsonObject jsonObject = jsonReader.readObject();

            assertEquals("alive", jsonObject.getJsonString("status").getString());

            DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                    .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // 2011-12-03T10:15:30
                    .appendOptional(DateTimeFormatter.ISO_DATE_TIME) // 2011-12-03T10:15:30+01:00
                    .appendOptional(DateTimeFormatter.ISO_INSTANT)  // 2011-12-03T10:15:30Z
                    .toFormatter();

            LocalDateTime time = LocalDateTime.parse(jsonObject.getJsonString("time").getString(), dateTimeFormatter);

            assertNotEquals(Duration.between(LocalDateTime.now(), time).compareTo(Duration.ofSeconds(10L)), 0);
        }
    }
}