import factories.ServerFactory;
import factories.UserFactory;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.entities.UserRole;
import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.payloads.UserPrincipal;
import org.faya.sensei.repositories.IRepository;
import org.faya.sensei.resources.endpoints.AuthResource;
import org.faya.sensei.services.IAuthService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;
import wrappers.UserEntityWrapper;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthResourceTest {

    @Test
    public void testAnnotations() {
        final long postMethod = Arrays.stream(AuthResource.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(POST.class))
                .count();

        assertTrue(postMethod >= 2, "At least two method under claas should be annotated with @POST");
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    public class UnitTest {

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class UserRepositoryTest {

            private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

            private static IRepository<UserEntity> userRepository;

            private static UserEntityWrapper cacheUserEntity;

            private static int targetId = -1;

            @BeforeAll
            @SuppressWarnings("unchecked")
            public static void setUp() throws Exception {
                final Reflections reflections = new Reflections("org.faya.sensei.repositories");
                final Set<Class<? extends IRepository>> classes = reflections.getSubTypesOf(IRepository.class);

                final Class<? extends IRepository> userRepositoryClass = classes.stream()
                        .filter(cls -> {
                            final Type[] genericInterfaces = cls.getGenericInterfaces();
                            for (final Type genericInterface : genericInterfaces) {
                                if (genericInterface instanceof ParameterizedType parameterizedType) {
                                    final Type[] typeArguments = parameterizedType.getActualTypeArguments();
                                    if (typeArguments.length == 1 && typeArguments[0].equals(UserEntity.class))
                                        return true;
                                }
                            }

                            return false;
                        })
                        .findFirst()
                        .orElseThrow(() ->
                                new ClassNotFoundException("No implementation found for IRepository<UserEntity>"));

                userRepository = userRepositoryClass.getDeclaredConstructor().newInstance();
                cacheUserEntity = UserFactory.createUserEntity("user", "password", UserRole.ADMIN).build();

                final Field userRepositoryField = userRepositoryClass.getDeclaredField("entityManager");
                userRepositoryField.setAccessible(true);
                userRepositoryField.set(userRepository, entityManagerFactory.createEntityManager());
            }

            @Test
            @Order(1)
            public void testPost() {
                targetId = userRepository.post(cacheUserEntity.entity());

                assertTrue(targetId > 0);
            }

            @Test
            @Order(2)
            public void testGetById() {
                final Optional<UserEntity> actualUserEntity = userRepository.get(targetId);

                assertTrue(actualUserEntity.isPresent());
                actualUserEntity.ifPresent(entity -> {
                    final UserEntityWrapper actualUserEntityWrapper = new UserEntityWrapper(entity);

                    assertTrue(actualUserEntityWrapper.getId() > 0);
                    assertEquals(cacheUserEntity.getName(), actualUserEntityWrapper.getName());
                    assertEquals(cacheUserEntity.getRole(), actualUserEntityWrapper.getRole());
                });
            }

            @Test
            @Order(3)
            public void testGetByKey() {
                final Optional<UserEntity> actualUserEntity = userRepository.get(cacheUserEntity.getName());

                assertTrue(actualUserEntity.isPresent());
                actualUserEntity.ifPresent(entity -> {
                    final UserEntityWrapper actualUserEntityWrapper = new UserEntityWrapper(entity);

                    assertTrue(actualUserEntityWrapper.getId() > 0);
                    assertEquals(cacheUserEntity.getName(), actualUserEntityWrapper.getName());
                    assertEquals(cacheUserEntity.getRole(), actualUserEntityWrapper.getRole());
                });
            }

            @Test
            @Order(4)
            public void testPut() {
                cacheUserEntity = UserFactory.createUserEntity(cacheUserEntity).setName("updated name").build();

                final Optional<UserEntity> actualUserEntity = userRepository.put(targetId,
                        UserFactory.createUserEntity().setName(cacheUserEntity.getName()).toEntity());

                assertTrue(actualUserEntity.isPresent());
                actualUserEntity.ifPresent(entity -> {
                    final UserEntityWrapper actualUserEntityWrapper = new UserEntityWrapper(entity);

                    assertTrue(actualUserEntityWrapper.getId() > 0);
                    assertEquals(cacheUserEntity.getName(), actualUserEntityWrapper.getName());
                    assertEquals(cacheUserEntity.getRole(), actualUserEntityWrapper.getRole());
                });
            }

            @Test
            @Order(5)
            public void testDelete() {
                final Optional<UserEntity> actualUserEntity = userRepository.delete(targetId);

                assertTrue(actualUserEntity.isPresent());
                actualUserEntity.ifPresent(entity -> {
                    final UserEntityWrapper actualUserEntityWrapper = new UserEntityWrapper(entity);

                    assertTrue(actualUserEntityWrapper.getId() > 0);
                    assertEquals(cacheUserEntity.getName(), actualUserEntityWrapper.getName());
                    assertEquals(cacheUserEntity.getRole(), actualUserEntityWrapper.getRole());
                });
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class AuthServiceTest {

            private static UserEntityWrapper cacheUserEntity;

            @Mock
            private IRepository<UserEntity> userRepository;

            private IAuthService authService;

            @BeforeEach
            public void prepare() throws Exception {
                final Reflections reflections = new Reflections("org.faya.sensei.services");
                final Set<Class<? extends IAuthService>> classes = reflections.getSubTypesOf(IAuthService.class);

                final Class<? extends IAuthService> authServiceClass = classes.stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new ClassNotFoundException("No implementation found for IAuthService"));

                authService = authServiceClass.getDeclaredConstructor().newInstance();

                final Field userRepositoryField = authServiceClass.getDeclaredField("userRepository");
                userRepositoryField.setAccessible(true);
                userRepositoryField.set(authService, userRepository);
            }

            @Test
            @Order(1)
            public void testRegister() {
                final UserDTO userDTO = UserFactory.createUserDTO("user", "password").toDTO();

                when(userRepository.post(any(UserEntity.class))).then(invocation -> {
                    cacheUserEntity = new UserEntityWrapper(invocation.getArgument(0));
                    cacheUserEntity.setId(1);
                    return 1;
                });

                final Optional<UserDTO> actualUserDTO = authService.create(userDTO);

                verify(userRepository, times(1)).post(any(UserEntity.class));
                assertTrue(actualUserDTO.isPresent());
            }

            @Test
            @Order(2)
            public void testLogin() {
                final UserDTO userDTO = UserFactory.createUserDTO("user", "password").toDTO();

                when(userRepository.get(cacheUserEntity.getName())).thenReturn(Optional.of(cacheUserEntity.entity()));

                final Optional<UserDTO> actualUserDTO = authService.login(userDTO);

                verify(userRepository, times(1)).get(cacheUserEntity.getName());
                assertTrue(actualUserDTO.isPresent());
            }

            @Test
            @Order(3)
            public void testUpdate() {
                final int userId = 1;

                cacheUserEntity = UserFactory.createUserEntity(cacheUserEntity).setName("updated name").build();

                final UserDTO userDTO = UserFactory.createUserDTO().setName(cacheUserEntity.getName()).toDTO();

                when(userRepository.put(eq(userId), any(UserEntity.class))).thenReturn(Optional.of(cacheUserEntity.entity()));

                final Optional<UserDTO> actualUserDTO = authService.update(userId, userDTO);

                verify(userRepository, times(1)).put(eq(userId), any(UserEntity.class));
                assertTrue(actualUserDTO.isPresent());
            }

            @Test
            @Order(4)
            public void testResolveToken() {
                final Optional<String> actualToken = authService.generateToken(1, Map.of("name", "user", "role", "USER"));

                assertTrue(actualToken.isPresent());

                final Optional<UserPrincipal> actualUserPrincipal = authService.resolveToken(actualToken.get());

                assertTrue(actualUserPrincipal.isPresent());
            }
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class IntegrationTest {

        private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

        private static final SeBootstrap.Instance instance = ServerFactory.createServer(entityManagerFactory);

        private static final URI uri = instance.configuration().baseUri();

        private static String cacheToken;

        @Test
        @Order(1)
        public void testRegister() {
            final var registerUserBody = Json.createObjectBuilder(Map.of("name", "user", "password", "password")).build();

            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth/register").build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(registerUserBody, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readObject();

                        final String actualToken = actualJsonObject.getString("token");
                        final String actualName = actualJsonObject.getJsonString("name").getString();

                        assertFalse(actualToken.isBlank());
                        assertEquals(registerUserBody.getJsonString("name").getString(), actualName);
                    }
                }
            }
        }

        @Test
        @Order(2)
        public void testLogin() {
            final var loginUserBody = Json.createObjectBuilder(Map.of("name", "user", "password", "password")).build();

            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth/login").build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(loginUserBody, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readObject();

                        final String actualToken = actualJsonObject.getString("token");
                        final String actualName = actualJsonObject.getJsonString("name").getString();

                        assertFalse(actualToken.isBlank());
                        assertEquals(loginUserBody.getJsonString("name").getString(), actualName);

                        cacheToken = actualToken;
                    }
                }
            }
        }

        @Test
        @Order(3)
        public void testVerify() {
            try (final Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth").build());

                try (final Response response = target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", cacheToken))
                        .get()) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (final JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        final JsonObject actualJsonObject = jsonReader.readObject();

                        final String actualToken = actualJsonObject.getString("token");

                        assertFalse(actualToken.isBlank());
                    }
                }
            }
        }
    }
}
