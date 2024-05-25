package org.faya.sensei;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import jakarta.ws.rs.ext.RuntimeDelegate;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        final HttpServer server = startServer(0);

        System.out.println(
                "Server started at " + server.getAddress().toString() + "\n" +
                           "CTRL + C to stop the application...\n");

        Thread.currentThread().join();
    }

    public static HttpServer startServer(int port) throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));

        HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(new JaxRsApplication(), HttpHandler.class);

        server.createContext("/api", handler);

        server.start();

        return server;
    }
}
