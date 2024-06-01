package factories;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.SeBootstrap;
import org.faya.sensei.JaxRsApplication;
import org.faya.sensei.entities.BoardEntity;
import org.faya.sensei.entities.StatusEntity;
import org.faya.sensei.entities.TaskEntity;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.services.*;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class ServerFactory {

    public static SeBootstrap.Instance createServer(EntityManagerFactory entityManagerFactory) {
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

                bind(UserRepository.class)
                        .to(new TypeLiteral<IRepository<UserEntity>>() { }.getType())
                        .in(Singleton.class);
                bind(BoardRepository.class)
                        .to(new TypeLiteral<IRepository<BoardEntity>>() { }.getType())
                        .in(Singleton.class);
                bind(StatusRepository.class)
                        .to(new TypeLiteral<IRepository<StatusEntity>>() { }.getType())
                        .in(Singleton.class);
                bind(TaskRepository.class)
                        .to(new TypeLiteral<IRepository<TaskEntity>>() { }.getType())
                        .in(Singleton.class);
            }
        });

        SeBootstrap.Configuration configuration = SeBootstrap.Configuration.builder().port(0).build();
        return SeBootstrap.start(resourceConfig, configuration).toCompletableFuture().join();
    }
}
