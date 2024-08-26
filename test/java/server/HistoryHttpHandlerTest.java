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

public class HistoryHttpHandlerTest {

    private static TaskManager taskManager = Managers.getDefault();
    private static HttpServer server;
    private static Gson gson;

    private static final String URL = "http://localhost:8080/history";

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
    public void getHistoryTest() throws IOException, InterruptedException {
        //id = 1
        Task firstTask = new Task("Задача 1", "Первая задача", Duration.ofSeconds(10),
                LocalDateTime.of(2010, 1, 1, 1, 1));
        taskManager.addTask(firstTask);
        //id = 2
        Task secondTask = new Task("Задача 2", "Вторая задача", Duration.ofSeconds(20),
                LocalDateTime.of(2020, 2, 2, 2, 2));
        taskManager.addTask(secondTask);
        //id = 3
        Task thirdTask = new Task("Задача 3", "Третья задача", Duration.ofSeconds(30),
                LocalDateTime.of(2030, 3, 3, 3, 3));
        taskManager.addTask(thirdTask);


        HttpClient client = HttpClient.newHttpClient();
        //1
        URI getTasksUrl1 = URI.create("http://localhost:8080/tasks/" + firstTask.getId());
        HttpRequest task1Request = HttpRequest.newBuilder().uri(getTasksUrl1).GET().build();
        HttpResponse<String> task1Response = client.send(task1Request, HttpResponse.BodyHandlers.ofString());
        String task1ResponseBody = task1Response.body();
        //3
        URI getTasksUrl3 = URI.create("http://localhost:8080/tasks/" + thirdTask.getId());
        HttpRequest task3Request = HttpRequest.newBuilder().uri(getTasksUrl3).GET().build();
        HttpResponse<String> task3Response = client.send(task3Request, HttpResponse.BodyHandlers.ofString());
        String task3ResponseBody = task3Response.body();
        //2
        URI getTasksUrl2 = URI.create("http://localhost:8080/tasks/" + secondTask.getId());
        HttpRequest task2Request = HttpRequest.newBuilder().uri(getTasksUrl2).GET().build();
        HttpResponse<String> task2Response = client.send(task2Request, HttpResponse.BodyHandlers.ofString());
        String task2ResponseBody = task2Response.body();

        String expectedHistoryResponse = String.join(",\n", task1ResponseBody, task3ResponseBody, task2ResponseBody);

        URI url = URI.create(URL);
        HttpRequest historyRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(historyRequest, HttpResponse.BodyHandlers.ofString());
        String actualHistoryResponse = response.body()
                .replace("[\n", "")
                .replace("\n]", "")
                .replace("\n  ", "\n").trim();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(expectedHistoryResponse, actualHistoryResponse);

    }

}
