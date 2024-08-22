package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedHttpHandlerTest {

    private static TaskManager taskManager = Managers.getDefault();
    private static HttpServer server;
    private static Gson gson;

    private static final String URL = "http://localhost:8080/prioritized";

    @BeforeEach
    public void startServerAndClearData() throws IOException {
        server = HttpTaskServer.start(taskManager);
        gson = BaseHttpHandler.createGson();
    }

    @AfterEach
    public void stopServer() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        HttpTaskServer.stop(server);
    }


    @Test
    public void getprioritizedTest() throws IOException, InterruptedException {
        //priority (by startTime) = 2
        Task firstTask = new Task("Задача 1", "Первая задача", Duration.ofSeconds(10),
                LocalDateTime.of(2010, 1, 1, 1, 1));
        taskManager.addTask(firstTask);
        int firstTaskId = firstTask.getId();
        String firstTaskJson = gson.toJson(taskManager.getTaskById(firstTaskId));
        //priority (by startTime) = 1
        Task secondTask = new Task("Задача 2", "Вторая задача", Duration.ofSeconds(20),
                LocalDateTime.of(2000, 2, 2, 2, 2));
        taskManager.addTask(secondTask);
        int secondTaskId = secondTask.getId();
        String secondTaskJson = gson.toJson(taskManager.getTaskById(secondTaskId));
        //priority (by startTime) = 3
        Task thirdTask = new Task("Задача 3", "Третья задача", Duration.ofSeconds(30),
                LocalDateTime.of(2040, 3, 3, 3, 3));
        taskManager.addTask(thirdTask);
        int thirdTaskId = thirdTask.getId();
        String thirdTaskJson = gson.toJson(taskManager.getTaskById(thirdTaskId));

        String expectedHistoryResponse = String.join(",\n", secondTaskJson, firstTaskJson,
                thirdTaskJson);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest prioritizedRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(prioritizedRequest, HttpResponse.BodyHandlers.ofString());
        String actualPrioritizedResponse = response.body()
                .replace("[\n", "")
                .replace("\n]", "")
                .replace("\n  ", "\n").trim();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(expectedHistoryResponse, actualPrioritizedResponse);

    }

}
