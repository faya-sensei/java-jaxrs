package org.faya.sensei.resources;

import jakarta.ws.rs.core.Response;

public class HeartBeatResource {

    public Response getHeartbeat() {
        return Response.ok().build();
    }
}
