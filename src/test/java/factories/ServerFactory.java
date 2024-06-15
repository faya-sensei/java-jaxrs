package factories;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.SeBootstrap;
import org.faya.sensei.JaxRsApplication;
import org.faya.sensei.middlewares.JWTAuthFilter;
import org.faya.sensei.repositories.IRepository;
import org.faya.sensei.services.IAuthService;
import org.faya.sensei.services.IService;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerFactory {

    public static SeBootstrap.Instance createServer(EntityManagerFactory entityManagerFactory) {
        ResourceConfig resourceConfig = ResourceConfig.forApplication(new JaxRsApplication());
        resourceConfig.register(JWTAuthFilter.class);
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
                    } else {
                        bind(implementationClass).to(interfaceClass).in(Singleton.class);
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

        SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().port(0).build();

        return SeBootstrap.start(resourceConfig, configuration).toCompletableFuture().join();
    }
}
