package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.exceptions.NotFoundException;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHttpHandler extends BaseHttpHandler {

    public EpicsHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] pathsParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();

        if (pathsParts.length < 2 || pathsParts.length > 4) {
            sendHasBadRequest(exchange);
        }

        try {
            switch (method) {
                case "DELETE":
                    deleteEpic(exchange, Integer.parseInt(pathsParts[2]));
                    break;
                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    createEpic(exchange, body);
                    break;
                case "GET":
                    if (pathsParts.length == 4) {
                        getEpicSubtasks(exchange, Integer.parseInt(pathsParts[2]));
                    } else if (pathsParts.length == 3) {
                        getEpicById(exchange, Integer.parseInt(pathsParts[2]));
                    } else if (pathsParts.length == 2) {
                        getAllEpics(exchange);
                    }
                    break;
            }
        } catch (Exception e) {
            sendHasBadRequest(exchange);
        }
    }

    public void deleteEpic(HttpExchange exchange, int epicId) throws IOException {
        try {
            super.taskManager.removeEpicById(epicId);
            sendText(exchange, "Эпик с идентификатором " + epicId + " успешно удален");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    public void createEpic(HttpExchange exchange, String body) throws IOException {
        Gson gson = createGson();
        Epic epic = gson.fromJson(body, Epic.class);
        super.taskManager.addEpic(epic);
        sendText(exchange, "Эпик создан!");
    }

    public void getEpicSubtasks(HttpExchange exchange, int epicId) throws IOException {
        Epic epic = super.taskManager.getEpicById(epicId);
        if (epic != null) {
            List<SubTask> epicSubtasks = epic.getSubtasks();
            Gson gson = createGson();
            sendText(exchange, gson.toJson(epicSubtasks));
        } else {
            sendNotFound(exchange, "Эпик с id =" + epicId + " не найден!");
        }
    }

    public void getEpicById(HttpExchange exchange, int epicId) throws IOException {
        Epic epic = super.taskManager.getEpicById(epicId);
        if (epic != null) {
            Gson gson = createGson();
            sendText(exchange, gson.toJson(epic));
        } else {
            sendNotFound(exchange, "Эпик с id =" + epicId + " не найден!");
        }
    }

    public void getAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> allEpics = super.taskManager.getAllEpics().stream().toList();
        Gson gson = createGson();
        sendText(exchange, gson.toJson(allEpics));
    }

}
