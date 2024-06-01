import factories.*;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.resources.TodoResource;
import org.junit.jupiter.api.*;
import wrappers.BoardEntityWrapper;
import wrappers.StatusEntityWrapper;
import wrappers.TaskEntityWrapper;
import wrappers.UserEntityWrapper;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TodoResourceTest {

    @Test
    public void testAnnotations() {
        assertTrue(
                TodoResource.class.isAnnotationPresent(Path.class),
                "Class should be annotated with @Path"
        );

        Path pathAnnotation = TodoResource.class.getAnnotation(Path.class);

        assertEquals("/todos", pathAnnotation.value(), "@Path value should be /todos");
    }

    @Test
    public void testMethodAnnotations() {
        Optional<Method> getMethod = Arrays.stream(TodoResource.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GET.class))
                .findFirst();

        assertTrue(getMethod.isPresent(), "One method under claas should be annotated with @GET");

        Optional<Method> postMethod = Arrays.stream(TodoResource.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(POST.class))
                .findFirst();

        assertTrue(postMethod.isPresent(), "One method under claas should be annotated with @POST");

        Optional<Method> putMethod = Arrays.stream(TodoResource.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PUT.class))
                .findFirst();

        assertTrue(putMethod.isPresent(), "One method under claas should be annotated with @PUT");

        Optional<Method> deleteMethod = Arrays.stream(TodoResource.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(DELETE.class))
                .findFirst();

        assertTrue(deleteMethod.isPresent(), "One method under claas should be annotated with @DELETE");
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class IntegrationTest {

        private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

        private static final UserEntityWrapper user = UserFactory.createUserEntity("test");

        private static final BoardEntityWrapper board = BoardFactory.createBoardEntity(List.of(user.userEntity()));

        private static final List<StatusEntityWrapper> statuses = List.of(
                StatusFactory.createStatusEntity("Todo", board.boardEntity()),
                StatusFactory.createStatusEntity("Done", board.boardEntity())
        );

        private static final List<TaskEntityWrapper> tasks = List.of(
                TaskFactory.createTaskEntity(
                        "Test task 1",
                        "Task 1 Test Description.",
                        LocalDateTime.now().plusMinutes(10),
                        board.boardEntity(),
                        statuses.getFirst().statusEntity(),
                        user.userEntity()
                ),
                TaskFactory.createTaskEntity(
                        "Test task 2",
                        "Task 2 Test Description.",
                        LocalDateTime.now().plusMinutes(20),
                        board.boardEntity(),
                        statuses.getLast().statusEntity(),
                        user.userEntity()
                )
        );

        private static final SeBootstrap.Instance instance = ServerFactory.createServer(entityManagerFactory);

        private static final URI uri = instance.configuration().baseUri();

        @BeforeAll
        public static void setUp() {
            final EntityManager entityManager = entityManagerFactory.createEntityManager();
            final EntityTransaction transaction = entityManager.getTransaction();

            try {
                transaction.begin();
                entityManager.persist(user);
                entityManager.persist(board);
                statuses.forEach(entityManager::persist);
                tasks.forEach(entityManager::persist);
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive())
                    transaction.rollback();
            }

            entityManager.close();
        }

        @Test
        @Order(1)
        public void testGetAllTasks() throws Exception {
            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/todos").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonArray jsonArray = jsonReader.readArray();

                        for (int i = 0; i < tasks.size(); i++) {
                            JsonObject jsonObject = jsonArray.getJsonObject(i);
                            TaskEntityWrapper task = tasks.get(i);

                            assertEquals(task.getTitle(), jsonObject.getJsonString("title").getString());
                            assertEquals(task.getDescription(), jsonObject.getJsonString("description").getString());
                            assertEquals(task.getStatus().getName(), jsonObject.getJsonString("statusName").getString());
                        }
                    }
                }
            }
        }

        @Test
        @Order(2)
        public void testGetTask() throws Exception {
            final TaskEntityWrapper task = tasks.getFirst();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/todos/%d".formatted(task.getId())).build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        assertEquals(task.getTitle(), jsonObject.getJsonString("title").getString());
                        assertEquals(task.getDescription(), jsonObject.getJsonString("description").getString());
                        assertEquals(task.getStatus().getName(), jsonObject.getJsonString("statusName").getString());
                    }
                }
            }
        }

        @Test
        @Order(3)
        public void testSaveTask() {
            final JsonObject task = Json.createObjectBuilder()
                    .add("title", "New Test task")
                    .add("description", "New Task Test Description.")
                    .add("startDate", LocalDateTime.now().toString())
                    .add("endDate", LocalDateTime.now().plusMinutes(5).toString())
                    .add("statusName", statuses.getFirst().getName())
                    .add("boardId", 1)
                    .add("assignerId", 1)
                    .build();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/todos").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(task, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        assertEquals(task.getString("title"), jsonObject.getJsonString("title").getString());
                        assertEquals(task.getString("description"), jsonObject.getJsonString("description").getString());
                        assertEquals(task.getString("statusName"), jsonObject.getJsonString("statusName").getString());
                    }
                }
            }
        }

        @Test
        @Order(4)
        public void testUpdateTask() throws Exception {
            final TaskEntityWrapper task = tasks.getFirst();
            final JsonObject updateTask = Json.createObjectBuilder()
                    .add("title", "New Test task")
                    .add("description", "New Task Test Description.")
                    .add("startDate", LocalDateTime.now().toString())
                    .add("endDate", LocalDateTime.now().plusMinutes(5).toString())
                    .add("statusName", statuses.getFirst().getName())
                    .add("boardId", 1)
                    .add("assignerId", 1)
                    .build();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/todos/%d".formatted(task.getId())).build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(updateTask, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        assertEquals(task.getTitle(), jsonObject.getJsonString("title").getString());
                        assertEquals(task.getDescription(), jsonObject.getJsonString("description").getString());
                        assertEquals(task.getStatus().getName(), jsonObject.getJsonString("statusName").getString());
                    }
                }
            }
        }

        @Test
        @Order(5)
        public void testRemoveTask() throws Exception {
            final TaskEntityWrapper task = tasks.getFirst();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/todos/%d".formatted(task.getId())).build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).delete()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        assertEquals(task.getTitle(), jsonObject.getJsonString("title").getString());
                        assertEquals(task.getDescription(), jsonObject.getJsonString("description").getString());
                        assertEquals(task.getStatus().getName(), jsonObject.getJsonString("statusName").getString());
                    }
                }
            }
        }
    }
}
