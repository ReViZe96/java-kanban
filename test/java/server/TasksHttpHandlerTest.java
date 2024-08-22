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
import java.util.ArrayList;
import java.util.List;

public class TasksHttpHandlerTest {

    private static TaskManager taskManager = Managers.getDefault();
    private static HttpServer server;
    private static Gson gson;

    private static final String URL = "http://localhost:8080/tasks/";

    @BeforeEach
    public void startServerAndClearData() throws IOException {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        server = HttpTaskServer.start(taskManager);
        gson = BaseHttpHandler.createGson();
    }

    @AfterEach
    public void stopServer() {
        HttpTaskServer.stop(server);
    }


    @Test
    public void getAllTasksTest() throws IOException, InterruptedException {
        List<Task> allTasks = new ArrayList<>();
        Task firstTask = new Task("Задача 1", "Первая задача", Duration.ofSeconds(10),
                LocalDateTime.of(2010, 1, 1, 1, 1));
        allTasks.add(firstTask);
        taskManager.addTask(firstTask);
        Task secondTask = new Task("Задача 2", "Вторая задача", Duration.ofSeconds(20),
                LocalDateTime.of(2020, 2, 2, 2, 2));
        allTasks.add(secondTask);
        taskManager.addTask(secondTask);
        Task thirdTask = new Task("Задача 3", "Третья задача", Duration.ofSeconds(30),
                LocalDateTime.of(2030, 3, 3, 3, 3));
        allTasks.add(thirdTask);
        taskManager.addTask(thirdTask);
        String expectedGetAllResponse = gson.toJson(allTasks);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> actualGetAllResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, actualGetAllResponse.statusCode());
        Assertions.assertEquals(expectedGetAllResponse, actualGetAllResponse.body());
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Просто задача", Duration.ofSeconds(40),
                LocalDateTime.of(2040, 4, 4, 4, 4));
        taskManager.addTask(task);
        int taskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL + taskId);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).GET().build();
        HttpResponse<String> actualSuccessGetByIdResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Task taskFromResponse = gson.fromJson(actualSuccessGetByIdResponse.body(), Task.class);
        Assertions.assertEquals(200, actualSuccessGetByIdResponse.statusCode());
        Assertions.assertEquals(task, taskFromResponse);


        URI notFoundUrl = URI.create(URL + "2");
        HttpRequest notFoundRequest = HttpRequest.newBuilder().uri(notFoundUrl).GET().build();
        HttpResponse<String> actualNotFoundGetByIdResponse = client.send(notFoundRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, actualNotFoundGetByIdResponse.statusCode());
    }

    @Test
    public void createTaskTest() throws IOException, InterruptedException {
        Task sendedTask = new Task("Задача", "Создаваемая задача", Duration.ofSeconds(50),
                LocalDateTime.of(2050, 5, 5, 5, 5));
        String sendedTaskJson = gson.toJson(sendedTask).replace("\"id\": 0,", "");

        Assertions.assertTrue(taskManager.getAllTasks().size() == 0);

        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(sendedTaskJson)).build();
        HttpResponse<String> actualSuccessCreateResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Task addedTask = taskManager.getAllTasks().stream().toList().get(0);
        Assertions.assertEquals(201, actualSuccessCreateResponse.statusCode());
        Assertions.assertTrue(taskManager.getAllTasks().size() == 1);
        Assertions.assertEquals(sendedTask.getName(), addedTask.getName());
        Assertions.assertEquals(sendedTask.getDescription(), addedTask.getDescription());
        Assertions.assertEquals(sendedTask.getType(), addedTask.getType());
        Assertions.assertEquals(sendedTask.getStatus(), addedTask.getStatus());
        Assertions.assertEquals(sendedTask.getStartTime(), addedTask.getStartTime());
        Assertions.assertEquals(sendedTask.getDuration(), addedTask.getDuration());


        URI url = URI.create(URL);
        HttpRequest intersectedRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(sendedTaskJson)).build();
        HttpResponse<String> actualIntersectedCreateResponse = client.send(intersectedRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, actualIntersectedCreateResponse.statusCode());
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Обновляемая задача", Duration.ofSeconds(60),
                LocalDateTime.of(2060, 6, 6, 6, 6));

        Assertions.assertEquals(taskManager.getAllTasks().size(), 0);
        taskManager.addTask(task);
        Assertions.assertEquals(taskManager.getAllTasks().size(), 1);
        int updatedTaskId = taskManager.getAllTasks().stream().toList().get(0).getId();

        String updatedName = "Новое имя";
        String updatedDescription = "Новое описание";
        LocalDateTime updatedStartTime = LocalDateTime.of(2070, 7, 7, 7, 7);
        Duration updatedDuration = Duration.ofSeconds(70);
        Task sendedTask = new Task(updatedName, updatedDescription, updatedDuration, updatedStartTime);
        sendedTask.setId(updatedTaskId);
        String sendedTaskJson = gson.toJson(sendedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(sendedTaskJson)).build();
        HttpResponse<String> actualSuccessUpdateResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Task updatedTask = taskManager.getAllTasks().stream().toList().get(0);
        Assertions.assertEquals(taskManager.getAllTasks().size(), 1);
        Assertions.assertEquals(201, actualSuccessUpdateResponse.statusCode());
        Assertions.assertTrue(taskManager.getAllTasks().size() == 1);
        Assertions.assertEquals(sendedTask.getId(), updatedTask.getId());
        Assertions.assertEquals(updatedName, updatedTask.getName());
        Assertions.assertEquals(updatedDescription, updatedTask.getDescription());
        Assertions.assertEquals(updatedStartTime, updatedTask.getStartTime());
        Assertions.assertEquals(updatedDuration, updatedTask.getDuration());


        HttpRequest intersectedRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(sendedTaskJson)).build();
        HttpResponse<String> actualIntersectedUpdateResponse = client.send(intersectedRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, actualIntersectedUpdateResponse.statusCode());


        Task notExistTask = new Task("Задача", "Несуществующая задача", Duration.ofSeconds(80),
                LocalDateTime.of(2080, 8, 8, 8, 8));
        notExistTask.setId(1000);
        String notExistTaskJson = gson.toJson(notExistTask);
        HttpRequest notFoundRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(notExistTaskJson)).build();
        HttpResponse<String> actualNotFoundUpdateResponse = client.send(notFoundRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, actualNotFoundUpdateResponse.statusCode());
    }

    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        Assertions.assertTrue(taskManager.getAllTasks().size() == 0);

        Task task = new Task("Задача", "Удаляемая задача", Duration.ofSeconds(90),
                LocalDateTime.of(2090, 9, 9, 9, 9));
        int notExistTaskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI notFoundUrl = URI.create(URL + notExistTaskId);
        HttpRequest notFoundRequest = HttpRequest.newBuilder().uri(notFoundUrl).DELETE().build();
        HttpResponse<String> actualNotFoundDeleteResponse = client.send(notFoundRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, actualNotFoundDeleteResponse.statusCode());


        taskManager.addTask(task);
        Assertions.assertTrue(taskManager.getAllTasks().size() == 1);
        int existTaskId = taskManager.getAllTasks().stream().toList().get(0).getId();

        URI rightUrl = URI.create(URL + existTaskId);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).DELETE().build();

        HttpResponse<String> actualSuccessDeleteResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, actualSuccessDeleteResponse.statusCode());
        Assertions.assertEquals(taskManager.getAllTasks().size(), 0);
        Assertions.assertEquals(taskManager.getTaskById(existTaskId), null);
    }

}
