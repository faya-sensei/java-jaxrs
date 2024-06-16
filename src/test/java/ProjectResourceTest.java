import factories.*;
import jakarta.json.Json;
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
import org.faya.sensei.entities.ProjectEntity;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.entities.UserRole;
import org.faya.sensei.repositories.IRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;
import wrappers.ProjectEntityWrapper;
import wrappers.StatusEntityWrapper;
import wrappers.TaskEntityWrapper;
import wrappers.UserEntityWrapper;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectResourceTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    public class UnitTest {

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class ProjectRepository {

            private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

            private static IRepository<ProjectEntity> projectRepository;

            private static ProjectEntityWrapper projectEntity;

            private static UserEntityWrapper userEntity;

            private static int targetId = -1;

            @BeforeAll
            @SuppressWarnings("unchecked")
            public static void setUp() throws Exception {
                final Reflections reflections = new Reflections("org.faya.sensei.repositories");
                final Set<Class<? extends IRepository>> classes = reflections.getSubTypesOf(IRepository.class);

                final Class<? extends IRepository> projectRepositoryClass = classes.stream()
                        .filter(cls -> {
                            Type[] genericInterfaces = cls.getGenericInterfaces();
                            for (Type genericInterface : genericInterfaces) {
                                if (genericInterface instanceof ParameterizedType parameterizedType) {
                                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                                    if (typeArguments.length == 1 && typeArguments[0].equals(ProjectEntity.class))
                                        return true;
                                }
                            }

                            return false;
                        })
                        .findFirst()
                        .orElseThrow(() ->
                                new ClassNotFoundException("No implementation found for IRepository<ProjectEntity>"));

                projectRepository = projectRepositoryClass.getDeclaredConstructor().newInstance();
                userEntity = UserFactory.createUserEntity("user", "password", UserRole.ADMIN);
                projectEntity = ProjectFactory.createProjectEntity("project", List.of(userEntity.userEntity()));

                final Field userRepositoryField = projectRepositoryClass.getDeclaredField("entityManager");
                userRepositoryField.setAccessible(true);
                userRepositoryField.set(projectRepository, entityManagerFactory.createEntityManager());
            }

            @Test
            @Order(1)
            public void testPost() {
                targetId = projectRepository.post(projectEntity.projectEntity());

                assertTrue(targetId > 0);
            }

            @Test
            @Order(2)
            public void testGetByUser() {
                Collection<ProjectEntity> actualProjectEntities = projectRepository.getBy("users.name", "user");

                assertFalse(actualProjectEntities.isEmpty());
                for (ProjectEntity actualProject : actualProjectEntities) {
                    assertEquals(projectEntity.projectEntity().getName(), actualProject.getName());
                }
            }
        }
    }

    @Nested
    public class IntegrationTest {

        private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

        private static final SeBootstrap.Instance instance = ServerFactory.createServer(entityManagerFactory);

        private static final URI uri = instance.configuration().baseUri();

        private static UserEntityWrapper userEntityWrapper;

        private static List<ProjectEntityWrapper> projectEntityWrappers;

        private static List<StatusEntityWrapper> StatusEntityWrappers;

        private static List<TaskEntityWrapper> taskEntityWrappers;

        private static String cacheToken;

        @BeforeAll
        public static void setUp() {
            final JsonObject registerUser = Json.createObjectBuilder()
                    .add("name", "user")
                    .add("password", "password")
                    .build();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth/register").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(registerUser, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final EntityManager entityManager = entityManagerFactory.createEntityManager();
                        final EntityTransaction transaction = entityManager.getTransaction();
                        final JsonObject jsonObject = jsonReader.readObject();

                        cacheToken = jsonObject.getJsonString("token").getString();

                        try {
                            transaction.begin();

                            userEntityWrapper = new UserEntityWrapper(entityManager.find(UserEntity.class, jsonObject.getInt("id")));

                            projectEntityWrappers = List.of(
                                    ProjectFactory.createProjectEntity("Project 1", List.of(userEntityWrapper.userEntity()))
                            );

                            StatusEntityWrappers = List.of(
                                    StatusFactory.createStatusEntity("Todo", projectEntityWrappers.getFirst().projectEntity()),
                                    StatusFactory.createStatusEntity("Done", projectEntityWrappers.getFirst().projectEntity())
                            );

                            taskEntityWrappers = List.of(
                                    TaskFactory.createTaskEntity(
                                            "Test task 1",
                                            "Task 1 Test Description.",
                                            LocalDateTime.now().plusMinutes(10),
                                            StatusEntityWrappers.getFirst().statusEntity(),
                                            projectEntityWrappers.getFirst().projectEntity(),
                                            userEntityWrapper.userEntity()
                                    ),
                                    TaskFactory.createTaskEntity(
                                            "Test task 2",
                                            "Task 2 Test Description.",
                                            LocalDateTime.now().plusMinutes(20),
                                            StatusEntityWrappers.getLast().statusEntity(),
                                            projectEntityWrappers.getFirst().projectEntity(),
                                            userEntityWrapper.userEntity()
                                    )

                            );

                            projectEntityWrappers.forEach(project -> entityManager.persist(project.projectEntity()));
                            StatusEntityWrappers.forEach(status -> entityManager.persist(status.statusEntity()));
                            taskEntityWrappers.forEach(task -> entityManager.persist(task.taskEntity()));

                            transaction.commit();
                        } catch (Exception e) {
                            if (transaction.isActive())
                                transaction.rollback();
                        }

                        entityManager.close();
                    }
                }
            }
        }

        @Test
        @Order(1)
        public void testSaveProject() {
            final JsonObject creationTask = Json.createObjectBuilder().add("name", "New Project").build();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/project").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + cacheToken)
                        .post(Entity.entity(creationTask, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject jsonObject = jsonReader.readObject();

                        assertEquals(creationTask.getJsonString("name").getString(), jsonObject.getJsonString("name").getString());
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
                        final JsonObject jsonObject = jsonReader.readArray().getFirst().asJsonObject();

                        assertEquals(projectEntityWrappers.getFirst().getName(), jsonObject.getJsonString("name").getString());
                    }
                }
            }
        }

        @Test
        @Order(3)
        public void testGetProject() {
            final ProjectEntityWrapper targetProject = projectEntityWrappers.getFirst();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/project/%d".formatted(targetProject.getId())).build());

                try (Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + cacheToken)
                        .get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject jsonObject = jsonReader.readObject();

                        assertEquals(projectEntityWrappers.getFirst().getName(), jsonObject.getJsonString("name").getString());
                    }
                }
            }
        }
    }
}
