package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.exceptions.NotFoundException;
import managers.interfaces.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHttpHandler extends BaseHttpHandler {

    public TasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] pathsParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();

        if (pathsParts.length < 2 || pathsParts.length > 3) {
            sendHasBadRequest(exchange);
        }

        try {
            switch (method) {
                case "DELETE":
                    deleteTask(exchange, Integer.parseInt(pathsParts[2]));
                    break;
                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    if (body.contains("id")) {
                        updateTask(exchange, body);
                    } else {
                        createTask(exchange, body);
                    }
                    break;
                case "GET":
                    if (pathsParts.length == 3) {
                        getTaskById(exchange, Integer.parseInt(pathsParts[2]));
                    } else if (pathsParts.length == 2) {
                        getAllTasks(exchange);
                    }
                    break;
            }
        } catch (Exception e) {
            sendHasBadRequest(exchange);
        }
    }

    public void deleteTask(HttpExchange exchange, int taskId) throws IOException {
        try {
            super.taskManager.removeTaskById(taskId);
            sendText(exchange, "Задача с идентификатором " + taskId + " успешно удалена");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    public void createTask(HttpExchange exchange, String body) throws IOException {
        Gson gson = createGson();
        Task task = gson.fromJson(body, Task.class);
        if (super.taskManager.isTasksIntersected(task)) {
            sendHasInteractions(exchange, "Создаваемая задача пересекается по времени с уже существующей!");
        } else {
            super.taskManager.addTask(task);
            sendText(exchange, "Задача создана!");
        }
    }

    public void updateTask(HttpExchange exchange, String body) throws IOException {
        Gson gson = createGson();
        Task task = gson.fromJson(body, Task.class);
        if (super.taskManager.isTasksIntersected(task)) {
            sendHasInteractions(exchange, "Обновляемая задача пересекается по времени с уже существующей!");
        } else {
            int taskId = task.getId();
            if (super.taskManager.getTaskById(taskId) != null) {
                super.taskManager.updateTask(task);
                sendText(exchange, "Задача с id =" + taskId + " обновлена!");
            } else {
                sendNotFound(exchange, "Задача с id =" + taskId + " не найдена!");
            }
        }
    }

    public void getTaskById(HttpExchange exchange, int taskId) throws IOException {
        Task task = super.taskManager.getTaskById(taskId);
        if (task != null) {
            Gson gson = createGson();
            sendText(exchange, gson.toJson(task));
        } else {
            sendNotFound(exchange, "Задача с id =" + taskId + " не найдена!");
        }
    }

    public void getAllTasks(HttpExchange exchange) throws IOException {
        List<Task> allTasks = super.taskManager.getAllTasks().stream().toList();
        Gson gson = createGson();
        sendText(exchange, gson.toJson(allTasks));
    }

}