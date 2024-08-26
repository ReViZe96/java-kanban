package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EpicsHttpHandlerTest {
    private static TaskManager taskManager = Managers.getDefault();
    private static HttpServer server;
    private static Gson gson;

    private static final String URL = "http://localhost:8080/epics/";

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

    public ArrayList<SubTask> createSubtasksList() {
        ArrayList<SubTask> subtasks = new ArrayList<>();

        SubTask firstSubTask = new SubTask("Подзадача 1", "Первая Подзадача", Duration.ofSeconds(10),
                LocalDateTime.of(2010, 1, 1, 1, 1));
        subtasks.add(firstSubTask);
        SubTask secondSubTask = new SubTask("Подзадача 2", "Вторая Подзадача", Duration.ofSeconds(20),
                LocalDateTime.of(2020, 2, 2, 2, 2));
        subtasks.add(secondSubTask);
        SubTask thirdSubTask = new SubTask("Подзадача 3", "Третья Подзадача", Duration.ofSeconds(30),
                LocalDateTime.of(2030, 3, 3, 3, 3));
        subtasks.add(thirdSubTask);

        return subtasks;
    }


    @Test
    public void getAllEpicsTest() throws IOException, InterruptedException {
        List<Epic> allEpics = new ArrayList<>();
        Epic firstEpic = new Epic("Эпик 1", "Первый эпик", Duration.ofSeconds(10),
                LocalDateTime.of(2010, 1, 1, 1, 1), createSubtasksList());
        allEpics.add(firstEpic);
        taskManager.addEpic(firstEpic);
        Epic secondEpic = new Epic("Эпик 2", "Второй эпик", Duration.ofSeconds(20),
                LocalDateTime.of(2020, 2, 2, 2, 2));
        allEpics.add(secondEpic);
        taskManager.addEpic(secondEpic);
        Epic thirdEpic = new Epic("Эпик 3", "Третий эпик", Duration.ofSeconds(30),
                LocalDateTime.of(2030, 3, 3, 3, 3));
        allEpics.add(thirdEpic);
        taskManager.addEpic(thirdEpic);
        String expectedGetAllResponse = gson.toJson(allEpics);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> actualGetAllResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, actualGetAllResponse.statusCode());
        Assertions.assertEquals(expectedGetAllResponse, actualGetAllResponse.body());
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Просто эпик", Duration.ofSeconds(40),
                LocalDateTime.of(2040, 4, 4, 4, 4), createSubtasksList());
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL + epicId);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).GET().build();
        HttpResponse<String> actualSuccessGetByIdResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Epic epicFromResponse = gson.fromJson(actualSuccessGetByIdResponse.body(), Epic.class);
        Assertions.assertEquals(200, actualSuccessGetByIdResponse.statusCode());
        Assertions.assertEquals(epic, epicFromResponse);


        URI notFoundUrl = URI.create(URL + "200");
        HttpRequest notFoundRequest = HttpRequest.newBuilder().uri(notFoundUrl).GET().build();
        HttpResponse<String> actualNotFoundGetByIdResponse = client.send(notFoundRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, actualNotFoundGetByIdResponse.statusCode());
    }

    @Test
    public void createEpicTest() throws IOException, InterruptedException {
        Epic sendedEpic = new Epic("Эпик", "Создаваемый эпик", Duration.ofSeconds(50),
                LocalDateTime.of(2050, 5, 5, 5, 5), createSubtasksList());
        LocalDateTime endTypeBeforeCalculating = LocalDateTime.of(2100, 12, 12, 12, 12);
        sendedEpic.setEndTime(Optional.of(endTypeBeforeCalculating));
        String sendedEpicJson = gson.toJson(sendedEpic).replace("\"id\": 0,", "");

        Assertions.assertTrue(taskManager.getAllEpics().size() == 0);

        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(sendedEpicJson)).build();
        HttpResponse<String> actualSuccessCreateResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Epic addedEpic = taskManager.getAllEpics().stream().toList().get(0);
        Assertions.assertEquals(201, actualSuccessCreateResponse.statusCode());
        Assertions.assertTrue(taskManager.getAllEpics().size() == 1);
        Assertions.assertEquals(sendedEpic.getName(), addedEpic.getName());
        Assertions.assertEquals(sendedEpic.getDescription(), addedEpic.getDescription());
        Assertions.assertEquals(sendedEpic.getType(), addedEpic.getType());
        Assertions.assertEquals(sendedEpic.getStatus(), addedEpic.getStatus());
        Assertions.assertNotEquals(sendedEpic.getStartTime(), addedEpic.getSubtasks().get(0).getStartTime());
        Assertions.assertNotEquals(sendedEpic.getEndTime(), addedEpic.getEndTime());
    }

    @Test
    public void getEpicSubEpicsTest() throws IOException, InterruptedException {
        Assertions.assertEquals(taskManager.getAllEpics().size(), 0);
        Assertions.assertEquals(taskManager.getAllSubTasks().size(), 0);

        Epic epic = new Epic("Эпик", "Просто эпик", Duration.ofSeconds(40),
                LocalDateTime.of(2040, 4, 4, 4, 4), createSubtasksList());
        taskManager.addEpic(epic);

        Assertions.assertEquals(taskManager.getAllEpics().size(), 1);
        Assertions.assertEquals(taskManager.getAllSubTasks().size(), 3);

        ArrayList<SubTask> epicSubtasks = taskManager.getAllEpics().stream().toList().get(0).getSubtasks();
        String expectedEpicSubtasks = gson.toJson(epicSubtasks);

        int epicId = epic.getId();
        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL + epicId + "/subtasks");
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).GET().build();
        HttpResponse<String> actualSuccessEpicSubtasksResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, actualSuccessEpicSubtasksResponse.statusCode());
        Assertions.assertEquals(expectedEpicSubtasks, actualSuccessEpicSubtasksResponse.body());
    }

    @Test
    public void deleteEpicTest() throws IOException, InterruptedException {
        Assertions.assertTrue(taskManager.getAllEpics().size() == 0);

        Epic epic = new Epic("Эпик", "Удаляемый эпик", Duration.ofSeconds(90),
                LocalDateTime.of(2090, 9, 9, 9, 9), createSubtasksList());
        int notExistEpicId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI notFoundUrl = URI.create(URL + notExistEpicId);
        HttpRequest notFoundRequest = HttpRequest.newBuilder().uri(notFoundUrl).DELETE().build();
        HttpResponse<String> actualNotFoundDeleteResponse = client.send(notFoundRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, actualNotFoundDeleteResponse.statusCode());


        taskManager.addEpic(epic);
        Assertions.assertTrue(taskManager.getAllEpics().size() == 1);
        int existEpicId = taskManager.getAllEpics().stream().toList().get(0).getId();

        URI rightUrl = URI.create(URL + existEpicId);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).DELETE().build();

        HttpResponse<String> actualSuccessDeleteResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, actualSuccessDeleteResponse.statusCode());
        Assertions.assertEquals(taskManager.getAllEpics().size(), 0);
        Assertions.assertEquals(taskManager.getEpicById(existEpicId), null);
    }

}
