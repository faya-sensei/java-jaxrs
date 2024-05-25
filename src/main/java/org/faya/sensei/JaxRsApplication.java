package org.faya.sensei;

import jakarta.ws.rs.core.Application;
import org.faya.sensei.resources.HeartBeatResource;

import java.util.Set;

public class JaxRsApplication extends Application {

    private final Set<Class<?>> classes;

    public JaxRsApplication() {
        classes = Set.of(
                HeartBeatResource.class
        );
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
