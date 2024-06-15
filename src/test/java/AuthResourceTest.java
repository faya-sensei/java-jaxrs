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
import wrappers.UserDTOWrapper;
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
        long postMethod = Arrays.stream(AuthResource.class.getDeclaredMethods())
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

            private static final UserEntityWrapper userEntity = UserFactory.createUserEntity("user", "password", UserRole.ADMIN);

            private static IRepository<UserEntity> userRepository;

            private static int targetId = -1;

            @BeforeAll
            @SuppressWarnings("unchecked")
            public static void setUp() throws Exception {
                Reflections reflections = new Reflections("org.faya.sensei.repositories");
                Set<Class<? extends IRepository>> classes = reflections.getSubTypesOf(IRepository.class);

                Class<? extends IRepository> userRepositoryClass = classes.stream()
                        .filter(cls -> {
                            Type[] genericInterfaces = cls.getGenericInterfaces();
                            for (Type genericInterface : genericInterfaces) {
                                if (genericInterface instanceof ParameterizedType parameterizedType) {
                                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
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

                Field userRepositoryField = userRepositoryClass.getDeclaredField("entityManager");
                userRepositoryField.setAccessible(true);
                userRepositoryField.set(userRepository, entityManagerFactory.createEntityManager());
            }

            @Test
            @Order(1)
            public void testPost() {
                targetId = userRepository.post(userEntity.userEntity());

                assertTrue(targetId > 0);
            }

            @Test
            @Order(2)
            public void testGet() {
                Optional<UserEntity> result = userRepository.get(targetId);

                assertTrue(result.isPresent());

                UserEntityWrapper userEntityWrapper = new UserEntityWrapper(result.get());

                assertTrue(userEntityWrapper.getId() > 0);
                assertEquals(userEntity.getName(), userEntityWrapper.getName());
                assertEquals(userEntity.getRole(), userEntityWrapper.getRole());
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        public class AuthServiceTest {

            private static final UserDTOWrapper userDTO = UserFactory.createUserDTO("user", "password");

            private static UserEntity userEntity;

            @Mock
            private IRepository<UserEntity> userRepository;

            private IAuthService authService;

            @BeforeEach
            public void prepare() throws Exception {
                Reflections reflections = new Reflections("org.faya.sensei.services");
                Set<Class<? extends IAuthService>> classes = reflections.getSubTypesOf(IAuthService.class);

                Class<? extends IAuthService> authServiceClass = classes.stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new ClassNotFoundException("No implementation found for IAuthService"));

                authService = authServiceClass.getDeclaredConstructor().newInstance();

                Field userRepositoryField = authServiceClass.getDeclaredField("userRepository");
                userRepositoryField.setAccessible(true);
                userRepositoryField.set(authService, userRepository);
            }

            @Test
            @Order(1)
            public void testRegister() {
                when(userRepository.post(any(UserEntity.class))).then(invocation -> {
                    UserEntityWrapper userEntityWrapper = new UserEntityWrapper(invocation.getArgument(0));
                    userEntityWrapper.setId(1);
                    userEntity = userEntityWrapper.userEntity();
                    return 1;
                });

                Optional<UserDTO> user = authService.create(userDTO.userDTO());

                verify(userRepository, times(1)).post(any(UserEntity.class));
                assertTrue(user.isPresent());
            }

            @Test
            @Order(2)
            public void testLogin() {
                when(userRepository.get(userDTO.getName())).thenReturn(Optional.of(userEntity));

                Optional<UserDTO> user = authService.login(userDTO.userDTO());

                verify(userRepository, times(1)).get(userDTO.getName());
                assertTrue(user.isPresent());
            }

            @Test
            @Order(3)
            public void testResolveToken() {
                Optional<String> token = authService.generateToken(1, Map.of("name", "user", "role", "USER"));

                assertTrue(token.isPresent());

                Optional<UserPrincipal> userPrincipal = authService.resolveToken(token.get());

                assertTrue(userPrincipal.isPresent());
            }
        }
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
                    .add("name", "user")
                    .add("password", "password")
                    .build();

            try (Client client = ClientBuilder.newClient()) {
                final WebTarget target = client.target(UriBuilder.fromUri(uri).path("/api/auth/register").build());

                try (Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(registerUser, MediaType.APPLICATION_JSON))) {
                    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                    try (JsonReader jsonReader = Json.createReader((InputStream) response.getEntity())) {
                        JsonObject jsonObject = jsonReader.readObject();

                        assertTrue(jsonObject.getInt("id") > 0);
                        assertEquals(registerUser.getJsonString("name").getString(), jsonObject.getJsonString("name").getString());
                        assertFalse(jsonObject.getJsonString("token").getString().isBlank());
                    }
                }
            }
        }

        @Test
        @Order(2)
        public void testLogin() {
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

                        assertTrue(jsonObject.getInt("id") > 0);
                        assertEquals(loginUser.getJsonString("name").getString(), jsonObject.getJsonString("name").getString());
                        assertFalse(jsonObject.getJsonString("token").getString().isBlank());
                    }
                }
            }
        }
    }
}
