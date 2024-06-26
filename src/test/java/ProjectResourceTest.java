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
import jakarta.ws.rs.sse.SseEventSource;
import org.faya.sensei.entities.*;
import org.faya.sensei.payloads.ProjectDTO;
import org.faya.sensei.payloads.TaskDTO;
import org.faya.sensei.repositories.IRepository;
import org.faya.sensei.resources.endpoints.ProjectResource;
import org.faya.sensei.resources.endpoints.TaskResource;
import org.faya.sensei.services.IService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;
import wrappers.*;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectResourceTest {

    @Test
    public void testAnnotations() {
        assertAll(
                () -> {
                    final String projectResourceClassName = ProjectResource.class.getSimpleName();

                    final Optional<Method> getMethod = Arrays.stream(ProjectResource.class.getDeclaredMethods())
                            .filter(method -> method.isAnnotationPresent(GET.class))
                            .findFirst();

                    assertTrue(
                            getMethod.isPresent(),
                            "One method under %s claas should be annotated with @GET".formatted(projectResourceClassName)
                    );

                    final Optional<Method> postMethod = Arrays.stream(ProjectResource.class.getDeclaredMethods())
                            .filter(method -> method.isAnnotationPresent(POST.class))
                            .findFirst();

                    assertTrue(
                            postMethod.isPresent(),
                            "One method under %s claas should be annotated with @POST".formatted(projectResourceClassName)
                    );
                },
                () -> {
                    final String taskResourceClassName = TaskResource.class.getSimpleName();

                    final Optional<Method> postMethod = Arrays.stream(TaskResource.class.getDeclaredMethods())
                            .filter(method -> method.isAnnotationPresent(POST.class))
                            .findFirst();

                    assertTrue(
                            postMethod.isPresent(),
                            "One method under %s claas should be annotated with @POST".formatted(taskResourceClassName)
                    );

                    final Optional<Method> putMethod = Arrays.stream(TaskResource.class.getDeclaredMethods())
                            .filter(method -> method.isAnnotationPresent(PUT.class))
                            .findFirst();

                    assertTrue(
                            putMethod.isPresent(),
                            "One method under %s claas should be annotated with @PUT".formatted(taskResourceClassName)
                    );

                    final Optional<Method> deleteMethod = Arrays.stream(TaskResource.class.getDeclaredMethods())
                            .filter(method -> method.isAnnotationPresent(DELETE.class))
                            .findFirst();

                    assertTrue(
                            deleteMethod.isPresent(),
                            "One method under %s claas should be annotated with @DELETE".formatted(taskResourceClassName)
                    );
                }
        );
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    public class UnitTest {

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class ProjectRepositoryTest {

            private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

            private static IRepository<ProjectEntity> projectRepository;

            private static ProjectEntityWrapper cacheProjectEntity;

            private static int targetId = -1;

            @BeforeAll
            @SuppressWarnings("unchecked")
            public static void setUp() throws Exception {
                final Reflections reflections = new Reflections("org.faya.sensei.repositories");
                final Set<Class<? extends IRepository>> classes = reflections.getSubTypesOf(IRepository.class);

                final Class<? extends IRepository> projectRepositoryClass = classes.stream()
                        .filter(cls -> {
                            final Type[] genericInterfaces = cls.getGenericInterfaces();
                            for (final Type genericInterface : genericInterfaces) {
                                if (genericInterface instanceof ParameterizedType parameterizedType) {
                                    final Type[] typeArguments = parameterizedType.getActualTypeArguments();
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
                UserEntity userEntity = UserFactory.createUserEntity("user", "password", UserRole.ADMIN).toEntity();
                cacheProjectEntity = ProjectFactory.createProjectEntity("project", List.of(userEntity)).build();

                final Field userRepositoryField = projectRepositoryClass.getDeclaredField("entityManager");
                userRepositoryField.setAccessible(true);
                userRepositoryField.set(projectRepository, entityManagerFactory.createEntityManager());
            }

            @Test
            @Order(1)
            public void testPost() {
                targetId = projectRepository.post(cacheProjectEntity.entity());

                assertTrue(targetId > 0);
            }

            @Test
            @Order(2)
            public void testGetById() {
                final Optional<ProjectEntity> actualProjectEntity = projectRepository.get(targetId);

                assertTrue(actualProjectEntity.isPresent());
                actualProjectEntity.ifPresent(entity -> {
                    final ProjectEntityWrapper actualProjectEntityWrapper = new ProjectEntityWrapper(entity);

                    assertTrue(actualProjectEntityWrapper.getId() > 0);
                    assertEquals(cacheProjectEntity.getName(), actualProjectEntityWrapper.getName());
                });
            }

            @Test
            @Order(3)
            public void testGetByUser() {
                final Collection<ProjectEntity> actualProjects = projectRepository.getBy("users.name", "user");

                assertFalse(actualProjects.isEmpty());
                assertArrayEquals(actualProjects.toArray(), List.of(cacheProjectEntity.entity()).toArray());
            }

            @Test
            @Order(4)
            public void testPut() {
                cacheProjectEntity = ProjectFactory.createProjectEntity(cacheProjectEntity).setName("updated project").build();

                final Optional<ProjectEntity> actualProjectEntity = projectRepository.put(targetId, cacheProjectEntity.entity());

                assertTrue(actualProjectEntity.isPresent());
                actualProjectEntity.ifPresent(entity -> {
                    final ProjectEntityWrapper actualProjectEntityWrapper = new ProjectEntityWrapper(entity);

                    assertTrue(actualProjectEntityWrapper.getId() > 0);
                    assertEquals(cacheProjectEntity.getName(), actualProjectEntityWrapper.getName());
                });
            }

            @Test
            @Order(5)
            public void testDelete() {
                final Optional<ProjectEntity> actualProjectEntity = projectRepository.delete(targetId);

                assertTrue(actualProjectEntity.isPresent());
                actualProjectEntity.ifPresent(entity -> {
                    final ProjectEntityWrapper actualProjectEntityWrapper = new ProjectEntityWrapper(entity);

                    assertTrue(actualProjectEntityWrapper.getId() > 0);
                    assertEquals(cacheProjectEntity.getName(), actualProjectEntityWrapper.getName());
                });
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class ProjectServiceTest {

            private static ProjectEntityWrapper cacheProjectEntity;

            @Mock
            private IRepository<ProjectEntity> projectRepository;

            private IService<ProjectDTO> projectService;

            @BeforeEach
            @SuppressWarnings("unchecked")
            public void prepare() throws Exception {
                final Reflections reflections = new Reflections("org.faya.sensei.services");
                final Set<Class<? extends IService>> classes = reflections.getSubTypesOf(IService.class);

                final Class<? extends IService> projectServiceClass = classes.stream()
                        .filter(cls -> {
                            final Type[] genericInterfaces = cls.getGenericInterfaces();
                            for (final Type genericInterface : genericInterfaces) {
                                if (genericInterface instanceof ParameterizedType parameterizedType) {
                                    final Type[] typeArguments = parameterizedType.getActualTypeArguments();
                                    if (typeArguments.length == 1 && typeArguments[0].equals(ProjectDTO.class))
                                        return true;
                                }
                            }

                            return false;
                        })
                        .findFirst()
                        .orElseThrow(() ->
                                new ClassNotFoundException("No implementation found for IService<ProjectDTO>"));

                projectService = projectServiceClass.getDeclaredConstructor().newInstance();

                final Field projectRepositoryField = projectServiceClass.getDeclaredField("projectRepository");
                projectRepositoryField.setAccessible(true);
                projectRepositoryField.set(projectService, projectRepository);
            }

            @Test
            @Order(1)
            public void testCreate() {
                final ProjectDTO projectDTO = ProjectFactory.createProjectDTO("project", List.of(1)).toDTO();

                when(projectRepository.post(any(ProjectEntity.class))).then(invocation -> {
                    cacheProjectEntity = new ProjectEntityWrapper(invocation.getArgument(0));
                    cacheProjectEntity.setId(1);
                    return 1;
                });

                final Optional<ProjectDTO> actualProjectDTO = projectService.create(projectDTO);

                verify(projectRepository, times(1)).post(any(ProjectEntity.class));
                assertTrue(actualProjectDTO.isPresent());
                actualProjectDTO.ifPresent(dto -> {
                    final ProjectDTOWrapper actualProjectDTOWrapper = new ProjectDTOWrapper(dto);

                    assertTrue(actualProjectDTOWrapper.getId() > 0);
                    assertEquals(cacheProjectEntity.getName(), actualProjectDTOWrapper.getName());
                });
            }

            @Test
            @Order(2)
            public void testGet() {
                final int projectId = 1;

                final ProjectEntityWrapper projectEntity = ProjectFactory.createProjectEntity(projectId, "project", List.of()).build();

                when(projectRepository.get(projectId)).thenReturn(Optional.of(projectEntity.entity()));

                final Optional<ProjectDTO> actualProjectDTO = projectService.get(projectId);

                verify(projectRepository, times(1)).get(projectId);
                assertTrue(actualProjectDTO.isPresent());
                actualProjectDTO.ifPresent(dto -> {
                    final ProjectDTOWrapper actualProjectDTOWrapper = new ProjectDTOWrapper(dto);

                    assertTrue(actualProjectDTOWrapper.getId() > 0);
                    assertEquals(projectEntity.getName(), actualProjectDTOWrapper.getName());
                });
            }

            @Test
            @Order(4)
            public void testUpdate() {
                final int projectId = 1;

                cacheProjectEntity = ProjectFactory.createProjectEntity(cacheProjectEntity).setName("updated project").build();

                final ProjectDTO projectDTO = ProjectFactory.createProjectDTO(cacheProjectEntity.getName(), List.of(1)).toDTO();

                when(projectRepository.put(eq(projectId), any(ProjectEntity.class))).thenReturn(Optional.of(cacheProjectEntity.entity()));

                final Optional<ProjectDTO> actualProjectDTO = projectService.update(projectId, projectDTO);

                verify(projectRepository, times(1)).put(eq(projectId), any(ProjectEntity.class));
                assertTrue(actualProjectDTO.isPresent());
                actualProjectDTO.ifPresent(dto -> {
                    final ProjectDTOWrapper actualProjectDTOWrapper = new ProjectDTOWrapper(dto);

                    assertTrue(actualProjectDTOWrapper.getId() > 0);
                    assertEquals(cacheProjectEntity.getName(), actualProjectDTOWrapper.getName());
                });
            }

            @Test
            @Order(5)
            public void testRemove() {
                final int projectId = 1;

                when(projectRepository.delete(projectId)).thenReturn(Optional.of(cacheProjectEntity.entity()));

                final boolean actualResult = projectService.remove(projectId);

                verify(projectRepository, times(1)).delete(projectId);
                assertTrue(actualResult);
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class TaskRepositoryTest {

            private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

            private static IRepository<TaskEntity> taskRepository;

            private static TaskEntityWrapper cacheTaskEntity;

            private static int targetId = -1;

            @BeforeAll
            @SuppressWarnings("unchecked")
            public static void setUp() throws Exception {
                final Reflections reflections = new Reflections("org.faya.sensei.repositories");
                final Set<Class<? extends IRepository>> classes = reflections.getSubTypesOf(IRepository.class);

                final Class<? extends IRepository> taskRepositoryClass = classes.stream()
                        .filter(cls -> {
                            final Type[] genericInterfaces = cls.getGenericInterfaces();
                            for (final Type genericInterface : genericInterfaces) {
                                if (genericInterface instanceof ParameterizedType parameterizedType) {
                                    final Type[] typeArguments = parameterizedType.getActualTypeArguments();
                                    if (typeArguments.length == 1 && typeArguments[0].equals(TaskEntity.class))
                                        return true;
                                }
                            }

                            return false;
                        })
                        .findFirst()
                        .orElseThrow(() ->
                                new ClassNotFoundException("No implementation found for IRepository<TaskEntity>"));

                taskRepository = taskRepositoryClass.getDeclaredConstructor().newInstance();
                UserEntity userEntity = UserFactory.createUserEntity("user", "password", UserRole.ADMIN).toEntity();
                ProjectEntity projectEntity = ProjectFactory.createProjectEntity("project", List.of(userEntity)).toEntity();
                StatusEntity statusEntity = StatusFactory.createStatusEntity("todo", projectEntity).toEntity();
                cacheTaskEntity = TaskFactory.createTaskEntity()
                        .setTitle("task")
                        .setDescription("task description")
                        .setEndDate(LocalDateTime.now().plusMinutes(10))
                        .setStatus(statusEntity)
                        .setProject(projectEntity)
                        .setAssigner(userEntity)
                        .build();

                final Field userRepositoryField = taskRepositoryClass.getDeclaredField("entityManager");
                userRepositoryField.setAccessible(true);
                userRepositoryField.set(taskRepository, entityManagerFactory.createEntityManager());
            }

            @Test
            @Order(1)
            public void testPost() {
                targetId = taskRepository.post(cacheTaskEntity.entity());

                assertTrue(targetId > 0);
            }

            @Test
            @Order(2)
            public void testGetById() {
                final Optional<TaskEntity> actualTaskEntity = taskRepository.get(targetId);

                assertTrue(actualTaskEntity.isPresent());
                actualTaskEntity.ifPresent(entity -> {
                    final TaskEntityWrapper actualTaskEntityWrapper = new TaskEntityWrapper(entity);

                    assertEquals(cacheTaskEntity.getTitle(), actualTaskEntityWrapper.getTitle());
                    assertEquals(cacheTaskEntity.getDescription(), actualTaskEntityWrapper.getDescription());
                    assertEquals(cacheTaskEntity.getStatus(), actualTaskEntityWrapper.getStatus());
                });
            }

            @Test
            @Order(4)
            public void testPut() {
                cacheTaskEntity = TaskFactory.createTaskEntity(cacheTaskEntity).setTitle("updated task").build();

                final Optional<TaskEntity> actualTaskEntity = taskRepository.put(targetId,
                        TaskFactory.createTaskEntity().setTitle("updated task").toEntity());

                assertTrue(actualTaskEntity.isPresent());
                actualTaskEntity.ifPresent(entity -> {
                    final TaskEntityWrapper actualTaskEntityWrapper = new TaskEntityWrapper(entity);

                    assertEquals(cacheTaskEntity.getTitle(), actualTaskEntityWrapper.getTitle());
                    assertEquals(cacheTaskEntity.getDescription(), actualTaskEntityWrapper.getDescription());
                    assertEquals(cacheTaskEntity.getStatus(), actualTaskEntityWrapper.getStatus());
                });
            }

            @Test
            @Order(5)
            public void testDelete() {
                final Optional<TaskEntity> actualTaskEntity = taskRepository.delete(targetId);

                assertTrue(actualTaskEntity.isPresent());
                actualTaskEntity.ifPresent(entity -> {
                    final TaskEntityWrapper actualTaskEntityWrapper = new TaskEntityWrapper(entity);

                    assertEquals(cacheTaskEntity.getTitle(), actualTaskEntityWrapper.getTitle());
                    assertEquals(cacheTaskEntity.getDescription(), actualTaskEntityWrapper.getDescription());
                    assertEquals(cacheTaskEntity.getStatus(), actualTaskEntityWrapper.getStatus());
                });
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class TaskServiceTest {

            private static TaskEntityWrapper cacheTaskEntity;

            @Mock
            private IRepository<UserEntity> userRepository;

            @Mock
            private IRepository<ProjectEntity> projectRepository;

            @Mock
            private IRepository<StatusEntity> statusRepository;

            @Mock
            private IRepository<TaskEntity> taskRepository;

            private IService<TaskDTO> taskService;

            @BeforeEach
            @SuppressWarnings("unchecked")
            public void prepare() throws Exception {
                final Reflections reflections = new Reflections("org.faya.sensei.services");
                final Set<Class<? extends IService>> classes = reflections.getSubTypesOf(IService.class);

                final Class<? extends IService> taskServiceClass = classes.stream()
                        .filter(cls -> {
                            final Type[] genericInterfaces = cls.getGenericInterfaces();
                            for (final Type genericInterface : genericInterfaces) {
                                if (genericInterface instanceof ParameterizedType parameterizedType) {
                                    final Type[] typeArguments = parameterizedType.getActualTypeArguments();
                                    if (typeArguments.length == 1 && typeArguments[0].equals(TaskDTO.class))
                                        return true;
                                }
                            }

                            return false;
                        })
                        .findFirst()
                        .orElseThrow(() ->
                                new ClassNotFoundException("No implementation found for IService<TaskDTO>"));

                taskService = taskServiceClass.getDeclaredConstructor().newInstance();

                final Field userRepositoryField = taskServiceClass.getDeclaredField("userRepository");
                userRepositoryField.setAccessible(true);
                userRepositoryField.set(taskService, userRepository);

                final Field projectRepositoryField = taskServiceClass.getDeclaredField("projectRepository");
                projectRepositoryField.setAccessible(true);
                projectRepositoryField.set(taskService, projectRepository);

                final Field statusRepositoryField = taskServiceClass.getDeclaredField("statusRepository");
                statusRepositoryField.setAccessible(true);
                statusRepositoryField.set(taskService, statusRepository);

                final Field taskRepositoryField = taskServiceClass.getDeclaredField("taskRepository");
                taskRepositoryField.setAccessible(true);
                taskRepositoryField.set(taskService, taskRepository);
            }

            @Test
            @Order(1)
            public void testCreate() {
                final UserEntity userEntity = UserFactory.createUserEntity(1, "user", "hashed-password", UserRole.USER).toEntity();
                final ProjectEntity projectEntity = ProjectFactory.createProjectEntity(1, "project", List.of(userEntity)).toEntity();
                final StatusEntity statusEntity = StatusFactory.createStatusEntity(1, "todo", projectEntity).toEntity();

                final TaskDTOWrapper taskDTO = TaskFactory.createTaskDTO()
                        .setTitle("task")
                        .setDescription("task description")
                        .setEndDate(LocalDateTime.now().plusMinutes(10))
                        .setStatus("todo")
                        .setProjectId(1)
                        .setAssignerId(1)
                        .build();

                when(projectRepository.get(taskDTO.getProjectId())).thenReturn(Optional.of(projectEntity));
                when(userRepository.get(taskDTO.getAssignerId())).thenReturn(Optional.of(userEntity));
                when(statusRepository.get(taskDTO.getStatus())).thenReturn(Optional.of(statusEntity));
                when(taskRepository.post(any(TaskEntity.class))).then(invocation -> {
                    cacheTaskEntity = new TaskEntityWrapper(invocation.getArgument(0));
                    cacheTaskEntity.setId(1);
                    return 1;
                });

                final Optional<TaskDTO> actualTaskDTO = taskService.create(taskDTO.dto());

                verify(projectRepository, times(1)).get(taskDTO.getProjectId());
                verify(statusRepository, times(1)).get(taskDTO.getStatus());
                verify(userRepository, times(1)).get(taskDTO.getAssignerId());
                verify(taskRepository, times(1)).post(any(TaskEntity.class));
                assertTrue(actualTaskDTO.isPresent());
            }

            @Test
            @Order(2)
            public void testUpdate() {
                final int taskId = 1;

                final UserEntity userEntity = UserFactory.createUserEntity(2, "sensei", "hashed-password", UserRole.USER).toEntity();
                final StatusEntity statusEntity = StatusFactory.createStatusEntity(2, "done", cacheTaskEntity.getProject().entity()).toEntity();

                cacheTaskEntity = TaskFactory.createTaskEntity(cacheTaskEntity)
                        .setTitle("updated task")
                        .setStatus(statusEntity)
                        .setAssigner(userEntity)
                        .build();

                final TaskDTO taskDTO = TaskFactory.createTaskDTO()
                        .setTitle("updated task")
                        .setStatus("done")
                        .setAssignerId(2)
                        .toDTO();

                when(userRepository.get(cacheTaskEntity.getAssigner().getId())).thenReturn(Optional.of(userEntity));
                when(statusRepository.get(cacheTaskEntity.getStatus().getName())).thenReturn(Optional.of(statusEntity));
                when(taskRepository.put(eq(taskId), any(TaskEntity.class))).thenReturn(Optional.of(cacheTaskEntity.entity()));

                final Optional<TaskDTO> actualTaskDTO = taskService.update(taskId, taskDTO);

                verify(userRepository, times(1)).get(cacheTaskEntity.getAssigner().getId());
                verify(statusRepository, times(1)).get(cacheTaskEntity.getStatus().getName());
                verify(taskRepository, times(1)).put(eq(taskId), any(TaskEntity.class));
                assertTrue(actualTaskDTO.isPresent());
            }

            @Test
            @Order(3)
            public void testRemove() {
                final int taskId = 1;

                when(taskRepository.delete(taskId)).thenReturn(Optional.of(cacheTaskEntity.entity()));

                final boolean actualResult = taskService.remove(taskId);

                verify(taskRepository, times(1)).delete(taskId);
                assertTrue(actualResult);
            }
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class IntegrationTest {

        private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

        private static final SeBootstrap.Instance instance = ServerFactory.createServer(entityManagerFactory);

        private static final URI uri = instance.configuration().baseUri();

        private static List<ProjectEntityWrapper> projectEntities;

        private static List<StatusEntityWrapper> StatusEntities;

        private static List<TaskEntityWrapper> taskEntities;

        private static String cacheToken;

        @BeforeAll
        public static void setUp() {
            final JsonObject registerUserBody = Json.createObjectBuilder(Map.of("name", "user", "password", "password")).build();

            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth/register").build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(registerUserBody, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final EntityManager entityManager = entityManagerFactory.createEntityManager();
                        final EntityTransaction transaction = entityManager.getTransaction();
                        final JsonObject jsonObject = jsonReader.readObject();

                        transaction.begin();

                        cacheToken = jsonObject.getJsonString("token").getString();

                        final UserEntityWrapper userEntity = new UserEntityWrapper(entityManager.find(UserEntity.class, jsonObject.getInt("id")));

                        projectEntities = List.of(
                                ProjectFactory.createProjectEntity("project", List.of(userEntity.entity())).build()
                        );
                        StatusEntities = List.of(
                                StatusFactory.createStatusEntity("todo", projectEntities.getFirst().entity()).build(),
                                StatusFactory.createStatusEntity("done", projectEntities.getFirst().entity()).build()
                        );
                        taskEntities = List.of(
                                TaskFactory.createTaskEntity()
                                        .setTitle("test task 1")
                                        .setDescription("task 1 test description.")
                                        .setStartDate(LocalDateTime.now())
                                        .setEndDate(LocalDateTime.now().plusMinutes(10))
                                        .setStatus(StatusEntities.getFirst().entity())
                                        .setProject(projectEntities.getFirst().entity())
                                        .setAssigner(userEntity.entity())
                                        .build(),
                                TaskFactory.createTaskEntity()
                                        .setTitle("test task 2")
                                        .setDescription("task 2 test description.")
                                        .setStartDate(LocalDateTime.now())
                                        .setEndDate(LocalDateTime.now().plusMinutes(20))
                                        .setStatus(StatusEntities.getLast().entity())
                                        .setProject(projectEntities.getFirst().entity())
                                        .setAssigner(userEntity.entity())
                                        .build()
                        );

                        projectEntities.forEach(project -> entityManager.persist(project.entity()));
                        StatusEntities.forEach(status -> entityManager.persist(status.entity()));
                        taskEntities.forEach(task -> entityManager.persist(task.entity()));

                        transaction.commit();

                        entityManager.close();
                    }
                }
            }
        }

        @Test
        @Order(1)
        public void testSaveProject() {
            final JsonObject creationTaskBody = Json.createObjectBuilder()
                    .add("name", "new project")
                    .build();

            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/project").build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", cacheToken))
                        .post(Entity.entity(creationTaskBody, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readObject();

                        final String actualName = actualJsonObject.getJsonString("name").getString();

                        assertEquals(creationTaskBody.getJsonString("name").getString(), actualName);
                    }
                }
            }
        }

        @Test
        @Order(2)
        public void testGetAllProjects() {
            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/project").build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", cacheToken))
                        .get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readArray().getFirst().asJsonObject();

                        final String actualName = actualJsonObject.getJsonString("name").getString();

                        assertEquals(projectEntities.getFirst().getName(), actualName);
                    }
                }
            }
        }

        @Test
        @Order(3)
        public void testGetProject() {
            final ProjectEntityWrapper targetProjectEntity = projectEntities.getFirst();

            try (final Client client = ClientBuilder.newClient()) {
                final String path = "/api/project/%d".formatted(targetProjectEntity.getId());
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path(path).build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", cacheToken))
                        .get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readObject();

                        final String actualName = actualJsonObject.getJsonString("name").getString();

                        assertEquals(projectEntities.getFirst().getName(), actualName);

                        final JsonArray actualTaskJsonArray = actualJsonObject.getJsonArray("tasks");

                        for (int i = 0; i < taskEntities.size(); i++) {
                            final JsonObject actualTaskJsonObject = actualTaskJsonArray.getJsonObject(i);
                            final TaskEntityWrapper expectedTask = taskEntities.get(i);

                            final String actualTitle = actualTaskJsonObject.getJsonString("title").getString();
                            final String actualDescription = actualTaskJsonObject.getJsonString("description").getString();
                            final String actualStatus = actualTaskJsonObject.getJsonString("status").getString();
                            final int actualProjectId = actualTaskJsonObject.getInt("projectId");
                            final int actualAssignerId = actualTaskJsonObject.getInt("assignerId");

                            assertEquals(expectedTask.getTitle(), actualTitle);
                            assertEquals(expectedTask.getDescription(), actualDescription);
                            assertEquals(expectedTask.getStatus().getName(), actualStatus);
                            assertEquals(expectedTask.getProject().getId(), actualProjectId);
                            assertEquals(expectedTask.getAssigner().getId(), actualAssignerId);
                        }
                    }
                }
            }
        }

        @Test
        @Order(4)
        public void testGetTask() {
            final LinkedBlockingDeque<String> events = new LinkedBlockingDeque<>();

            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/project/tasks").build());
                final SseEventSource eventSource = SseEventSource.target(target).build();
                eventSource.register(inboundSseEvent -> {
                    final String eventData = inboundSseEvent.readData();
                    events.offer(eventData);
                });
                eventSource.open();

                assertTrue(eventSource.isOpen());

                final JsonObject creationTaskBody = Json.createObjectBuilder()
                        .add("title", "new sse task")
                        .add("description", "new sse task description.")
                        .add("startDate", LocalDateTime.now().toString())
                        .add("endDate", LocalDateTime.now().plusMinutes(5).toString())
                        .add("status", StatusEntities.getFirst().getName())
                        .add("projectId", 1)
                        .add("assignerId", 1)
                        .build();

                final WebTarget createTarget = client.target(UriBuilder.fromUri(uri).path("/api/project/tasks").build());
                try (final Response response = createTarget.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", cacheToken))
                        .post(Entity.entity(creationTaskBody, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
                }

                String createEvent = events.poll(1, TimeUnit.SECONDS);
                assertNotNull(createEvent);
                assertTrue(createEvent.contains("\"title\":\"new sse task\""));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        @Order(5)
        public void testSaveTask() {
            final JsonObject creationTaskBody = Json.createObjectBuilder()
                    .add("title", "new test task")
                    .add("description", "new test task description.")
                    .add("startDate", LocalDateTime.now().toString())
                    .add("endDate", LocalDateTime.now().plusMinutes(5).toString())
                    .add("status", StatusEntities.getFirst().getName())
                    .add("projectId", 1)
                    .add("assignerId", 1)
                    .build();

            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/project/tasks").build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", cacheToken))
                        .post(Entity.entity(creationTaskBody, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readObject();

                        final String actualTitle = actualJsonObject.getJsonString("title").getString();
                        final String actualDescription = actualJsonObject.getJsonString("description").getString();
                        final String actualStatus = actualJsonObject.getJsonString("status").getString();
                        final int actualProjectId = actualJsonObject.getInt("projectId");
                        final int actualAssignerId = actualJsonObject.getInt("assignerId");

                        assertEquals(creationTaskBody.getString("title"), actualTitle);
                        assertEquals(creationTaskBody.getString("description"), actualDescription);
                        assertEquals(creationTaskBody.getString("status"), actualStatus);
                        assertEquals(creationTaskBody.getInt("projectId"), actualProjectId);
                        assertEquals(creationTaskBody.getInt("assignerId"), actualAssignerId);
                    }
                }
            }
        }

        @Test
        @Order(6)
        public void testUpdateTask() {
            final TaskEntityWrapper targetTaskEntity = taskEntities.getFirst();
            final JsonObject updateTaskBody = Json.createObjectBuilder()
                    .add("title", "updated test task")
                    .add("endDate", LocalDateTime.now().plusMinutes(10).toString())
                    .add("status", StatusEntities.getLast().getName())
                    .build();

            try (final Client client = ClientBuilder.newClient()) {
                final String path = "/api/project/tasks/%d".formatted(targetTaskEntity.getId());
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path(path).build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", cacheToken))
                        .put(Entity.entity(updateTaskBody, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readObject();

                        final String actualTitle = actualJsonObject.getJsonString("title").getString();
                        final String actualDescription = actualJsonObject.getJsonString("description").getString();
                        final String actualStatus = actualJsonObject.getJsonString("status").getString();
                        final int actualProjectId = actualJsonObject.getInt("projectId");
                        final int actualAssignerId = actualJsonObject.getInt("assignerId");

                        assertEquals(updateTaskBody.getString("title"), actualTitle);
                        assertEquals(targetTaskEntity.getDescription(), actualDescription);
                        assertEquals(updateTaskBody.getString("status"), actualStatus);
                        assertEquals(targetTaskEntity.getProject().getId(), actualProjectId);
                        assertEquals(targetTaskEntity.getAssigner().getId(), actualAssignerId);
                    }
                }
            }
        }
    }
}
