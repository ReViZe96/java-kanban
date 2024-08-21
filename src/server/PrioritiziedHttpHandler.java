package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import managers.interfaces.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

public class PrioritiziedHttpHandler extends BaseHttpHandler {

    public PrioritiziedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }

    public final PrioritiziedHttpHandler.LocalDateTimeTypeAdapter LOCAL_DATE_TIME_TYPE_ADAPTER = new PrioritiziedHttpHandler.LocalDateTimeTypeAdapter();
    public final PrioritiziedHttpHandler.DurationTypeAdapter DURATION_TYPE_ADAPTER = new PrioritiziedHttpHandler.DurationTypeAdapter();

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

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_TYPE_ADAPTER)
                .registerTypeAdapter(Duration.class, DURATION_TYPE_ADAPTER)
                .create();
        sendText(exchange, gson.toJson(historyOfView));
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
