import factories.*;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.faya.sensei.entities.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import wrappers.ProjectEntityWrapper;
import wrappers.StatusEntityWrapper;
import wrappers.TaskEntityWrapper;
import wrappers.UserEntityWrapper;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProjectResourceTest {

    @Nested
    public class IntegrationTest {

        private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

        private static final UserEntityWrapper userEntityWrapper = UserFactory.createUserEntity("test", "test", UserRole.USER);

        private static final List<ProjectEntityWrapper> projectEntityWrapper = List.of(
                ProjectFactory.createProjectEntity("test", List.of(userEntityWrapper.userEntity()))
        );

        private static final List<StatusEntityWrapper> StatusEntityWrappers = List.of(
                StatusFactory.createStatusEntity("Todo", projectEntityWrapper.getFirst().projectEntity()),
                StatusFactory.createStatusEntity("Done", projectEntityWrapper.getFirst().projectEntity())
        );

        private static final List<TaskEntityWrapper> taskEntityWrappers = List.of(
                TaskFactory.createTaskEntity(
                        "Test task 1",
                        "Task 1 Test Description.",
                        LocalDateTime.now().plusMinutes(10),
                        StatusEntityWrappers.getFirst().statusEntity(),
                        projectEntityWrapper.getFirst().projectEntity(),
                        userEntityWrapper.userEntity()
                ),
                TaskFactory.createTaskEntity(
                        "Test task 2",
                        "Task 2 Test Description.",
                        LocalDateTime.now().plusMinutes(20),
                        StatusEntityWrappers.getLast().statusEntity(),
                        projectEntityWrapper.getFirst().projectEntity(),
                        userEntityWrapper.userEntity()
                )
        );

        private static final SeBootstrap.Instance instance = ServerFactory.createServer(entityManagerFactory);

        private static final URI uri = instance.configuration().baseUri();

        private static String cacheToken;

        @BeforeAll
        public static void setUp() {
            final EntityManager entityManager = entityManagerFactory.createEntityManager();
            final EntityTransaction transaction = entityManager.getTransaction();

            try {
                transaction.begin();
                entityManager.persist(userEntityWrapper.userEntity());
                projectEntityWrapper.forEach(project -> entityManager.persist(project.projectEntity()));
                StatusEntityWrappers.forEach(status -> entityManager.persist(status.statusEntity()));
                taskEntityWrappers.forEach(task -> entityManager.persist(task.taskEntity()));
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive())
                    transaction.rollback();
            }

            entityManager.close();

            final JsonObject loginUser = Json.createObjectBuilder()
                    .add("name", "user")
                    .add("password", "password")
                    .build();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth/login").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(loginUser, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        cacheToken = jsonObject.getJsonString("token").getString();
                        assertFalse(cacheToken.isBlank());
                    }
                }
            }
        }

        @Test
        @Order(2)
        public void testGetAllProjects() {
            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/project").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + cacheToken)
                        .get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonArray jsonArray = jsonReader.readArray();

                        JsonObject jsonObject = jsonArray.getFirst().asJsonObject();
                        assertEquals(projectEntityWrapper.getFirst().getName(), jsonObject.getJsonString("name").getString());
                    }
                }
            }
        }

        @Test
        @Order(3)
        public void testGetProject() {
            final ProjectEntityWrapper targetProject = projectEntityWrapper.getFirst();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/project/%d".formatted(targetProject.getId())).build());

                try (Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + cacheToken)
                        .get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonArray jsonArray = jsonReader.readArray();

                        JsonObject jsonObject = jsonArray.getFirst().asJsonObject();
                        assertEquals(projectEntityWrapper.getFirst().getName(), jsonObject.getJsonString("name").getString());
                    }
                }
            }
        }
    }
}
