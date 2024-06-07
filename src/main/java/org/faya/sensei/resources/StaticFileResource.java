package org.faya.sensei.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.net.URLConnection;

@Path("/")
public class StaticFileResource {

    private static final String BASE_DIR = "/static";

    @GET
    @Path("{path:.*}")
    public Response serveFile(@PathParam("path") String path) {
        if (path == null || path.isEmpty()) path = "index.html";

        String filePath = BASE_DIR + "/" + path;

        InputStream fileStream = getClass().getResourceAsStream(filePath);
        if (fileStream != null) {
            String mediaType = getMediaType(filePath);
            return Response.ok(fileStream, mediaType).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private String getMediaType(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null ? mimeType : "application/octet-stream";
    }
}
