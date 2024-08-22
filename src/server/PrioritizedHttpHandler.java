package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.interfaces.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.TreeSet;

public class PrioritizedHttpHandler extends BaseHttpHandler {

    public PrioritizedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathsParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        if (pathsParts.length != 2 && !(method.equals("GET"))) {
            sendHasBadRequest(exchange);
        }
        try {
            getPrioritizedTasks(exchange);
        } catch (Exception e) {
            sendHasBadRequest(exchange);
        }
    }

    public void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        TreeSet<Task> historyOfView = super.taskManager.getPrioritizedTasks();
        Gson gson = createGson();
        sendText(exchange, gson.toJson(historyOfView));
    }

}
