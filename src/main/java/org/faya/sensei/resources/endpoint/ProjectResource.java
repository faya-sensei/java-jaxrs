package org.faya.sensei.resources.endpoint;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.faya.sensei.payloads.ProjectDTO;
import org.faya.sensei.services.IService;

import java.util.Collection;
import java.util.Optional;

public class ProjectResource {

    @Inject
    private IService<ProjectDTO> projectService;

    public Response getAllProjects(@Context SecurityContext sc) {
        return Response.ok().build();
    }

    public Response getProject(@PathParam("id") final int id) {
        return Response.ok().build();
    }
}
