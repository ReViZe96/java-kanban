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

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TasksHttpHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHttpHandler(taskManager));
        server.createContext("/epics", new EpicsHttpHandler(taskManager));
        server.createContext("/history", new HistoryHttpHandler(taskManager));
        server.createContext("/prioritized", new PrioritiziedHttpHandler(taskManager));

        server.start();


    }
}
