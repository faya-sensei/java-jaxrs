package org.faya.sensei;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.faya.sensei.resources.HeartBeatResource;
import org.faya.sensei.resources.TodoResource;

import java.util.Set;

@ApplicationPath("api")
public class JaxRsApplication extends Application {

    private final Set<Class<?>> classes = Set.of(
            HeartBeatResource.class,
            TodoResource.class
    );

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
