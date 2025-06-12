package org.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.model.Greeting;
import org.repository.GreetingRepository;

import javax.naming.Context;
import java.io.IOException;
import java.io.OutputStream;

public class GreetingController implements HttpHandler {
    private final GreetingRepository repository;

    public GreetingController(GreetingRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.matches("/greeting/\\d+")) {
            int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            Greeting greeting = repository.findById(id);
            String response;
            if (greeting != null) {
                response = "<html><body><h1>" + greeting.getGreetingText() + "</h1></body></html>";
                exchange.sendResponseHeaders(200, response.getBytes().length);
            } else {
                response = "Greeting not found";
                exchange.sendResponseHeaders(404, response.getBytes().length);
            }
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            String response = "Invalid endpoint";
            exchange.sendResponseHeaders(400, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
