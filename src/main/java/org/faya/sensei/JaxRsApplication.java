package org.faya.sensei;

import jakarta.ws.rs.core.Application;
import org.faya.sensei.resources.EndpointResource;
import org.faya.sensei.resources.StaticFileResource;

import java.util.Set;

public class JaxRsApplication extends Application {

    private final Set<Class<?>> classes = Set.of(
            StaticFileResource.class,
            EndpointResource.class
    );

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
