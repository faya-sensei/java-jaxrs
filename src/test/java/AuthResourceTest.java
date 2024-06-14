import factories.ServerFactory;
import factories.UserFactory;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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
import org.faya.sensei.payloads.UserPrincipal;
import org.faya.sensei.repositories.IRepository;
import org.faya.sensei.services.AuthService;
import org.faya.sensei.services.IAuthService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;
import wrappers.UserDTOWrapper;
import wrappers.UserEntityWrapper;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthResourceTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    public class UnitTest {

        private final UserEntityWrapper userEntity = UserFactory.createUserEntity("user", "password", UserRole.ADMIN);
        private final UserDTOWrapper userDTO = UserFactory.createUserDTO("user", "password");

        @Mock
        private IRepository<UserEntity> userRepository;

        private IAuthService authService;

        public void setUp() throws Exception {
            Reflections reflections = new Reflections("org.faya.sensei.services");
            Set<Class<? extends IAuthService>> classes = reflections.getSubTypesOf(IAuthService.class);

            Class<? extends IAuthService> authServiceClass = classes.stream().findFirst()
                    .orElseThrow(() -> new ClassNotFoundException("No implementation found for IAuthService"));

            authService = authServiceClass.getDeclaredConstructor().newInstance();

            Field userRepositoryField = authServiceClass.getDeclaredField("userRepository");
            userRepositoryField.setAccessible(true);
            userRepositoryField.set(authService, userRepository);
        }

        @Test
        public void testAuthService_Register_Success() {
            when(userRepository.post(any(UserEntity.class))).thenReturn(1);

            Optional<String> token = authService.register(userDTO.userDTO());

            verify(userRepository, times(1)).post(any(UserEntity.class));
            assertTrue(token.isPresent());
        }

        @Test
        public void testAuthService_Register_Failure() {
            when(userRepository.post(any(UserEntity.class))).thenReturn(0);

            Optional<String> token = authService.register(userDTO.userDTO());

            verify(userRepository, times(1)).post(any(UserEntity.class));
            assertFalse(token.isPresent());
        }

        @Test
        public void testAuthService_Login_Success() {
            when(userRepository.get(userDTO.getName())).thenReturn(Optional.of(userEntity.userEntity()));

            Optional<String> token = authService.login(userDTO.userDTO());

            verify(userRepository, times(1)).get(userDTO.getName());
            assertTrue(token.isPresent());
        }

        @Test
        public void testAuthService_Login_Failure() {
            when(userRepository.get(userDTO.getName())).thenReturn(Optional.empty());

            Optional<String> token = authService.login(userDTO.userDTO());

            verify(userRepository, times(1)).get(userDTO.getName());
            assertFalse(token.isPresent());
        }

        @Test
        public void testAuthService_ParseToken_Success() {
            Optional<String> token = authService.generateToken("1", "user");

            assertTrue(token.isPresent());

            Optional<UserPrincipal> userPrincipal = authService.parseToken(token.get());

            assertTrue(userPrincipal.isPresent());
        }

        @Test
        public void testAuthService_ParseToken_Failure() {
            String invalidToken = "invalid.token";

            Optional<UserPrincipal> userPrincipal = authService.parseToken(invalidToken);

            assertFalse(userPrincipal.isPresent());
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

                        assertFalse(jsonObject.getJsonString("token").getString().isEmpty());
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

                        assertFalse(jsonObject.getJsonString("token").getString().isEmpty());
                    }
                }
            }
        }
    }
}
