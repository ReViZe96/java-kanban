package server;

import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.interfaces.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final static int PORT = 8080;

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        start(taskManager);
    }

    public static HttpServer start(TaskManager taskManager) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHttpHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHttpHandler(taskManager));
        server.createContext("/epics", new EpicsHttpHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));
        server.createContext("/history", new HistoryHttpHandler());
        server.start();
        return server;
    }

    public static void stop(HttpServer server) {
        server.stop(1);
    }

}
