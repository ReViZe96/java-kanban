package server;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import managers.exceptions.NotFoundException;
import managers.interfaces.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SubtasksHttpHandler extends BaseHttpHandler {

    public SubtasksHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    public final SubtasksHttpHandler.LocalDateTimeTypeAdapter LOCAL_DATE_TIME_TYPE_ADAPTER = new SubtasksHttpHandler.LocalDateTimeTypeAdapter();
    public final SubtasksHttpHandler.DurationTypeAdapter DURATION_TYPE_ADAPTER = new SubtasksHttpHandler.DurationTypeAdapter();


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
                    deleteSubTask(exchange, Integer.parseInt(pathsParts[2]));
                    break;
                case "POST":
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    JsonElement jsonElement = JsonParser.parseString(body);
                    JsonObject subTask = jsonElement.getAsJsonObject();

                    if (subTask.has("id")) {
                        updateSubTask(exchange, body);
                    } else  {
                        createSubTask(exchange, body);
                    }
                    break;
                case "GET":
                    if (pathsParts.length == 3) {
                        getSubTaskById(exchange, Integer.parseInt(pathsParts[2]));
                    } else if (pathsParts.length == 2) {
                        getAllSubTasks(exchange);
                    }
                    break;
            }
        } catch (Exception e) {
            sendHasBadRequest(exchange);
        }
    }

    public void deleteSubTask(HttpExchange exchange, int subTaskId) throws IOException {
        try {
            super.taskManager.removeSubTaskById(subTaskId);
            sendText(exchange, "Подзадача с идентификатором " + subTaskId + " успешно удалена");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    public void createSubTask(HttpExchange exchange, String body) throws IOException {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_TYPE_ADAPTER)
                .registerTypeAdapter(Duration.class, DURATION_TYPE_ADAPTER)
                .create();
        SubTask subTask = gson.fromJson(body, SubTask.class);

        if (super.taskManager.isTasksIntersected(subTask)) {
            sendHasInteractions(exchange, "Создаваемая подзадача пересекается по времени с уже существующей!");
        } else {
            super.taskManager.addSubTask(subTask);
            sendText(exchange, "Подзадача создана!");
        }
    }

    public void updateSubTask(HttpExchange exchange, String body) throws IOException {

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_TYPE_ADAPTER)
                .registerTypeAdapter(Duration.class, DURATION_TYPE_ADAPTER)
                .create();
        SubTask subTask = gson.fromJson(body, SubTask.class);

        if (super.taskManager.isTasksIntersected(subTask)) {
            sendHasInteractions(exchange, "Обновляемая подзадача пересекается по времени с уже существующей!");
        } else {
            int subTaskId = subTask.getId();
            if (super.taskManager.getSubTaskById(subTaskId) != null) {
                super.taskManager.updateSubTask(subTask);
                sendText(exchange, "Подзадача с id =" + subTaskId + " обновлена!");
            } else {
                sendNotFound(exchange, "Подзадача с id =" + subTaskId + " не найдена!");
            }
        }
    }

    public void getSubTaskById(HttpExchange exchange, int subTaskId) throws IOException {

        SubTask subTask = super.taskManager.getSubTaskById(subTaskId);

        if (subTask != null) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_TYPE_ADAPTER)
                    .registerTypeAdapter(Duration.class, DURATION_TYPE_ADAPTER)
                    .create();
            sendText(exchange, gson.toJson(subTask));
        } else {
            sendNotFound(exchange, "Подзадача с id =" + subTaskId + " не найдена!");
        }
    }

    public void getAllSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> allSubTasks = super.taskManager.getAllSubTasks().stream().toList();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_TYPE_ADAPTER)
                .registerTypeAdapter(Duration.class, DURATION_TYPE_ADAPTER)
                .create();
        sendText(exchange, gson.toJson(allSubTasks));
    }


    class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SS");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localTime) throws IOException {
            jsonWriter.value(localTime.format(timeFormatter));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), timeFormatter);
        }
    }

    class DurationTypeAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value(String.valueOf(duration));
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            return Duration.parse(jsonReader.nextString());
        }
    }

}
