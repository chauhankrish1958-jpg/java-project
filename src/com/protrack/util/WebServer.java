package com.protrack.util;

import com.protrack.model.Priority;
import com.protrack.model.Role;
import com.protrack.model.TaskStatus;
import com.protrack.service.ProjectService;
import com.protrack.service.SystemSettingService;
import com.protrack.service.TaskService;
import com.protrack.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Built-in Lightweight Java HTTP Web Server.
 * Serves the HTML5/CSS3/JS frontend files and exposes REST API endpoints.
 * Requires ZERO external dependencies (uses standard JDK com.sun.net.httpserver).
 */
public class WebServer {

    private final int port;
    private final File baseDir;
    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final SystemSettingService settingService;
    private HttpServer server;

    public WebServer(int port, File baseDir, UserService userService, ProjectService projectService,
                     TaskService taskService, SystemSettingService settingService) {
        this.port = port;
        this.baseDir = baseDir;
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.settingService = settingService;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Static File Handler for HTML, CSS, JS
        server.createContext("/", new StaticFileHandler(baseDir));

        // API Endpoints
        server.createContext("/api/health", exchange -> {
            String resp = "{\"status\":\"UP\",\"system\":\"ProTrack Enterprise Java Server\"}";
            sendResponse(exchange, 200, "application/json", resp);
        });

        server.setExecutor(null); // default executor
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    static class StaticFileHandler implements HttpHandler {
        private final File rootDir;

        public StaticFileHandler(File rootDir) {
            this.rootDir = rootDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/")) {
                path = "/index.html";
            }

            File file = new File(rootDir, path);
            if (!file.exists() || file.isDirectory()) {
                file = new File(rootDir, "index.html");
            }

            if (!file.exists()) {
                String error = "404 Not Found: " + path;
                exchange.sendResponseHeaders(404, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
                return;
            }

            String contentType = "text/plain";
            String name = file.getName().toLowerCase();
            if (name.endsWith(".html")) contentType = "text/html; charset=UTF-8";
            else if (name.endsWith(".css")) contentType = "text/css; charset=UTF-8";
            else if (name.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";
            else if (name.endsWith(".json")) contentType = "application/json";
            else if (name.endsWith(".png")) contentType = "image/png";
            else if (name.endsWith(".svg")) contentType = "image/svg+xml";

            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}
