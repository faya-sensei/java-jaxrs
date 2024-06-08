package org.faya.sensei.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

import java.io.*;
import java.net.URLConnection;
import java.util.Map;

@Path("/")
public class StaticFileResource {

    private static final String BASE_DIR = "/static";
    private static final int BUFFER_SIZE = 4096;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("{path:.*}")
    public Response serveFile(@PathParam("path") String path) {
        String filePath = BASE_DIR + "/" + (path == null || path.isEmpty() ? "index.html" : path);
        InputStream fileStream = getClass().getResourceAsStream(filePath);

        if (fileStream != null) {
            String mimeType = URLConnection.guessContentTypeFromName(filePath);
            String mediaType = mimeType != null ? mimeType : "application/octet-stream";

            StreamingOutput streamingOutput = output -> {
                try (InputStream inputStream = "text/html".equals(mediaType)
                        ? new GlobalVariableStream(fileStream, Map.of("URI", uriInfo.getBaseUri().toString()))
                        : fileStream) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
            };

            return Response.ok(streamingOutput, mediaType).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public static class GlobalVariableStream extends FilterInputStream {

        private final Map<String, String> properties;
        private StringBuilder buffer = new StringBuilder();
        private int bufferIndex = 0;

        public GlobalVariableStream(InputStream inputStream, Map<String, String> properties) {
            super(inputStream);
            this.properties = properties;
        }

        @Override
        public int read() throws IOException {
            if (bufferIndex < buffer.length()) return buffer.charAt(bufferIndex++);

            int firstChar = super.read();
            if (firstChar == '_') {
                buffer.setLength(0);
                bufferIndex = 0;

                int secondChar = super.read();
                if (secondChar == '_') {
                    int tempChar;
                    while ((tempChar = super.read()) != '_' && tempChar != -1)
                        buffer.append((char) tempChar);

                    if (tempChar != -1) {
                        if (super.read() != '_') {
                            buffer = new StringBuilder("__" + buffer.toString() + "_");
                        } else {
                            String value = properties.get(buffer.toString());
                            buffer = new StringBuilder(value != null ? value : "__" + buffer.toString() + "__");
                        }
                    } else {
                        buffer = new StringBuilder("__" + buffer.toString());
                    }

                    return buffer.charAt(bufferIndex++);
                } else {
                    buffer.append((char) secondChar);
                    return firstChar;
                }
            }

            return firstChar;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int bytesRead = 0;

            while (bytesRead < len) {
                int byteRead = read();
                if (byteRead == -1) {
                    if (bytesRead == 0) {
                        return -1;
                    } else {
                        break;
                    }
                }
                b[off + bytesRead] = (byte) byteRead;
                bytesRead++;
            }

            return bytesRead;
        }
    }
}
