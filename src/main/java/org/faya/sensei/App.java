package org.faya.sensei;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.SeBootstrap;
import org.faya.sensei.repositories.IRepository;
import org.faya.sensei.services.AuthService;
import org.faya.sensei.services.IAuthService;
import org.faya.sensei.services.IService;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.h2.tools.Server;
import org.reflections.Reflections;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class App {

    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("java-jaxrs-database");

    private static final int START_DATABASE_SERVER = 0x01;
    private static final int START_DATABASE_MIGRATION  = 0x02;

    public static void main(String[] args) throws InterruptedException, SQLException {
        Map<String, String> ServerProperties = new HashMap<>();
        int operations = 0;

        for (String arg : args) {
            switch (arg) {
                case "--database" -> operations |= START_DATABASE_SERVER;
                case "--migration" -> operations |= START_DATABASE_MIGRATION;
                case "--help" -> {
                    System.out.println("Usage: java -jar java-jaxrs.jar [options]");
                    System.out.println("Options:");
                    System.out.println("  --database              Start the H2 database TCP server");
                    System.out.println("  --help                  Show help document");
                    System.out.println("  --migration             Run migration script at resources/migration.sql");
                    System.out.println("  --protocol=<protocol>   Specify the server protocol (default: http)");
                    System.out.println("  --host=<host>           Specify the server host (default: localhost)");
                    System.out.println("  --port=<port>           Specify the server port (default: 8080)");
                    System.exit(0);
                }
                default -> {
                    if (arg.startsWith("--")) {
                        String[] keyValue = arg.substring(2).split("=", 2);
                        if (keyValue.length == 2)
                            ServerProperties.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }

        if ((operations & START_DATABASE_SERVER) == START_DATABASE_SERVER) {
            final Server databaseServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
            LOGGER.log(Level.INFO, "Database server instance running at {0}.", databaseServer.getURL());
        }

        if ((operations & START_DATABASE_MIGRATION) == START_DATABASE_MIGRATION) {
            boolean result = startMigration("migration.sql");
            LOGGER.log(Level.INFO, "Database migrations executed {0}.", result ? "success" : "failed");
        }

        final SeBootstrap.Instance server = startServer(ServerProperties);
        LOGGER.log(Level.INFO, "Server instance running at {0}.", server.configuration().baseUri());

        Thread.currentThread().join();
    }

    public static boolean startMigration(String scriptPath) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream(Objects.requireNonNull(scriptPath));
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            transaction.begin();

            String sql = reader.lines().collect(Collectors.joining("\n"));
            entityManager.createNativeQuery(sql).executeUpdate();

            transaction.commit();

            return true;
        } catch (Exception e) {
            transaction.rollback();
            return false;
        }
    }

    public static SeBootstrap.Instance startServer(Map<String, String> properties) {
        ResourceConfig resourceConfig = ResourceConfig.forApplication(new JaxRsApplication());
        resourceConfig.register(new AbstractBinder() {

            @Override
            protected void configure() {
                bind(entityManagerFactory)
                        .to(EntityManagerFactory.class)
                        .in(Singleton.class);
                bindFactory(entityManagerFactory::createEntityManager)
                        .to(EntityManager.class)
                        .in(Singleton.class);

                bindImplementations("org.faya.sensei.repositories", IRepository.class);
                bindImplementations("org.faya.sensei.services", IAuthService.class);
                bindImplementations("org.faya.sensei.services", IService.class);

                bind(AuthService.class).to(AuthService.class).in(Singleton.class);
            }

            private <T> void bindImplementations(String basePackage, Class<T> interfaceClass) {
                Reflections reflections = new Reflections(basePackage);
                Set<Class<? extends T>> implementations = reflections.getSubTypesOf(interfaceClass).stream()
                        .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                        .collect(Collectors.toSet());

                for (Class<? extends T> implementationClass : implementations) {
                    Type implementationInterface = getGenericInterface(implementationClass, interfaceClass);
                    if (implementationInterface != null) {
                        bind(implementationClass).to(implementationInterface).in(Singleton.class);
                    }
                }
            }

            private <T> Type getGenericInterface(Class<?> clazz, Class<T> interfaceClass) {
                for (Type type : clazz.getGenericInterfaces()) {
                    if (type instanceof ParameterizedType paramType &&
                            interfaceClass.equals(paramType.getRawType()))
                        return type;
                }

                return null;
            }
        });

        SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder()
                .from((name, type) -> {
                    String value = properties.entrySet().stream()
                            .filter(entry -> entry.getKey().equalsIgnoreCase(name.substring(name.lastIndexOf('.') + 1)))
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse(null);

                    return switch (type.getSimpleName()) {
                        case "Boolean" -> Optional.ofNullable(value).map(v -> type.cast(Boolean.parseBoolean(v)));
                        case "Integer" -> Optional.ofNullable(value).map(v -> type.cast(Integer.parseInt(v)));
                        default -> Optional.ofNullable(value).map(type::cast);
                    };
                })
                .build();

        CompletionStage<SeBootstrap.Instance> handler = SeBootstrap.start(resourceConfig, configuration);

        return handler.toCompletableFuture().join();
    }
}
