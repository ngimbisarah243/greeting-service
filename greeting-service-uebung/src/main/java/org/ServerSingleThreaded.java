package org;

import org.model.Greeting;
import org.repository.GreetingRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ServerSingleThreaded {
    // Datenbank-Konfiguration
    private static final String DB_URL = "jdbc:postgresql://db:5432/greeting_db";
    private static final String DB_USER = "app";
    private static final String DB_PASSWORD = "app";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL-Driver geladen");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL-Driver nicht gefunden!");
            e.printStackTrace();
            // Abbruch, wenn Treiber nicht da ist
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8081)) {
            System.out.println("Server läuft auf Port 8081...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleConnect(clientSocket);
                clientSocket.close();
            }
        }
    }

    private static void handleConnect(Socket clientSocket) throws IOException {
        try (var inputStream = clientSocket.getInputStream();
             var outputStream = clientSocket.getOutputStream();
             var scanner = new Scanner(inputStream);
             var writer = new PrintWriter(outputStream, true, StandardCharsets.UTF_8)) {

            // HTTP-Request lesen (nur erste Zeile benötigt)
            String requestLine = scanner.nextLine();
            System.out.println("Request: " + requestLine);

            // Pfad extrahieren
            String[] parts = requestLine.split(" ");
            if (parts.length >= 2 && "GET".equals(parts[0])) {
                String path = parts[1]; // z.B. /greeting oder /greeting/1
                GreetingRepository repo = new GreetingRepository(DB_URL, DB_USER, DB_PASSWORD);

                if ("/greeting".equals(path)) {
                    // Alle Greetings anzeigen
                    var allGreetings = repo.findAll();
                    StringBuilder html = new StringBuilder();
                    html.append("<h1>Alle Greetings</h1>");
                    html.append("<table border='1'><tr><th>ID</th><th>Text</th></tr>");
                    for (Greeting g : allGreetings) {
                        html.append("<tr><td>")
                                .append(g.getId())
                                .append("</td><td>")
                                .append(g.getGreetingText())
                                .append("</td></tr>");
                    }
                    html.append("</table>");
                    respond(writer, 200, html.toString());

                } else if (path.startsWith("/greeting/")) {
                    // Einzelnen Greeting anzeigen
                    String idStr = path.substring("/greeting/".length());
                    try {
                        int id = Integer.parseInt(idStr);
                        Greeting greeting = repo.findById(id);
                        if (greeting != null) {
                            respond(writer, 200, "<h1>" + greeting.getGreetingText() + "</h1>");
                        } else {
                            respond(writer, 404, "Greeting not found");
                        }
                    } catch (NumberFormatException e) {
                        respond(writer, 400, "Bad Request");
                    }
                } else {
                    respond(writer, 404, "Unknown path");
                }
            } else {
                respond(writer, 400, "Invalid Request");
            }
        }
    }

    private static void respond(PrintWriter writer, int status, String message) {
        String statusText = (status == 200) ? "OK" : (status == 404) ? "Not Found" : "Bad Request";
        writer.println("HTTP/1.1 " + status + " " + statusText);
        writer.println("Content-Type: text/html; charset=UTF-8");
        writer.println("Connection: close");
        writer.println();
        writer.println("<html><body><h1>" + message + "</h1></body></html>");
    }
}
