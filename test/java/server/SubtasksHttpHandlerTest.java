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
import java.util.Optional;

public class SubtasksHttpHandlerTest {

    private static TaskManager taskManager = Managers.getDefault();
    private static HttpServer server;
    private static Gson gson;

    private static final String URL = "http://localhost:8080/subtasks/";

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

    public ArrayList<Epic> createEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        Epic firstEpic = new Epic("Эпик 1", "Первый эпик", Duration.ofSeconds(10),
                LocalDateTime.of(2010, 1, 1, 1, 1));
        firstEpic.setEndTime(Optional.of(LocalDateTime.of(2200, 6, 6, 6, 6)));
        epics.add(firstEpic);
        Epic secondEpic = new Epic("Эпик 2", "Второй эпик", Duration.ofSeconds(20),
                LocalDateTime.of(2020, 2, 2, 2, 2));
        secondEpic.setEndTime(Optional.of(LocalDateTime.of(2300, 7, 7, 7, 7)));
        epics.add(secondEpic);
        Epic thirdEpic = new Epic("Эпик 3", "Третий эпик", Duration.ofSeconds(30),
                LocalDateTime.of(2030, 3, 3, 3, 3));
        thirdEpic.setEndTime(Optional.of(LocalDateTime.of(2400, 8, 8, 8, 8)));
        epics.add(thirdEpic);

        return  epics;
    }


    @Test
    public void getAllSubtasksTest() throws IOException, InterruptedException {
        ArrayList<SubTask> allSubTasks = new ArrayList<>();
        SubTask firstSubTask = new SubTask("Подзадача 1", "Первая Подзадача", Duration.ofSeconds(10),
                LocalDateTime.of(2010, 1, 1, 1, 1), createEpics().get(0));
        allSubTasks.add(firstSubTask);
        taskManager.addSubTask(firstSubTask);
        SubTask secondSubTask = new SubTask("Подзадача 2", "Вторая Подзадача", Duration.ofSeconds(20),
                LocalDateTime.of(2020, 2, 2, 2, 2), createEpics().get(1));
        allSubTasks.add(secondSubTask);
        taskManager.addSubTask(secondSubTask);
        SubTask thirdSubTask = new SubTask("Подзадача 3", "Третья Подзадача", Duration.ofSeconds(30),
                LocalDateTime.of(2030, 3, 3, 3, 3), createEpics().get(2));
        allSubTasks.add(thirdSubTask);
        taskManager.addSubTask(thirdSubTask);
        String expectedGetAllResponse = gson.toJson(allSubTasks);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> actualGetAllResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, actualGetAllResponse.statusCode());
        Assertions.assertEquals(expectedGetAllResponse, actualGetAllResponse.body());
    }

    @Test
    public void getSubtaskByIdTest() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("Подзадача", "Просто Подзадача", Duration.ofSeconds(40),
                LocalDateTime.of(2040, 4, 4, 4, 4), createEpics().get(0));
        taskManager.addSubTask(subTask);
        int subTaskId = subTask.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL + subTaskId);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).GET().build();
        HttpResponse<String> actualSuccessGetByIdResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskFromResponse = gson.fromJson(actualSuccessGetByIdResponse.body(), SubTask.class);
        Assertions.assertEquals(200, actualSuccessGetByIdResponse.statusCode());
        Assertions.assertEquals(subTask, subTaskFromResponse);


        URI notFoundUrl = URI.create(URL + "20");
        HttpRequest notFoundRequest = HttpRequest.newBuilder().uri(notFoundUrl).GET().build();
        HttpResponse<String> actualNotFoundGetByIdResponse = client.send(notFoundRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, actualNotFoundGetByIdResponse.statusCode());
    }

    @Test
    public void createSubtaskTest() throws IOException, InterruptedException {
        SubTask sendedSubTask = new SubTask("Подзадача", "Создаваемая Подзадача", Duration.ofSeconds(50),
                LocalDateTime.of(2050, 5, 5, 5, 5), createEpics().get(1));
        String sendedSubTaskJson = gson.toJson(sendedSubTask).replace("\"id\": 0,", "");

        Assertions.assertTrue(taskManager.getAllSubTasks().size() == 0);

        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(sendedSubTaskJson)).build();
        HttpResponse<String> actualSuccessCreateResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        SubTask addedSubTask = taskManager.getAllSubTasks().stream().toList().get(0);
        Assertions.assertEquals(201, actualSuccessCreateResponse.statusCode());
        Assertions.assertTrue(taskManager.getAllSubTasks().size() == 1);
        Assertions.assertEquals(sendedSubTask.getName(), addedSubTask.getName());
        Assertions.assertEquals(sendedSubTask.getDescription(), addedSubTask.getDescription());
        Assertions.assertEquals(sendedSubTask.getType(), addedSubTask.getType());
        Assertions.assertEquals(sendedSubTask.getStatus(), addedSubTask.getStatus());
        Assertions.assertEquals(sendedSubTask.getStartTime(), addedSubTask.getStartTime());
        Assertions.assertEquals(sendedSubTask.getDuration(), addedSubTask.getDuration());


        URI url = URI.create(URL);
        HttpRequest intersectedRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(sendedSubTaskJson)).build();
        HttpResponse<String> actualIntersectedCreateResponse = client.send(intersectedRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, actualIntersectedCreateResponse.statusCode());
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("Подзадача", "Обновляемая Подзадача", Duration.ofSeconds(60),
                LocalDateTime.of(2060, 6, 6, 6, 6), createEpics().get(0));

        Assertions.assertEquals(taskManager.getAllSubTasks().size(), 0);
        taskManager.addSubTask(subTask);
        Assertions.assertEquals(taskManager.getAllSubTasks().size(), 1);
        int updatedSubTaskId = taskManager.getAllSubTasks().stream().toList().get(0).getId();

        String updatedName = "Новое имя";
        String updatedDescription = "Новое описание";
        LocalDateTime updatedStartTime = LocalDateTime.of(2070, 7, 7, 7, 7);
        Duration updatedDuration = Duration.ofSeconds(70);
        Epic updatedEpic = createEpics().get(2);
        SubTask sendedSubTask = new SubTask(updatedName, updatedDescription, updatedDuration, updatedStartTime, updatedEpic);
        sendedSubTask.setId(updatedSubTaskId);
        String sendedSubTaskJson = gson.toJson(sendedSubTask);

        HttpClient client = HttpClient.newHttpClient();
        URI rightUrl = URI.create(URL);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(sendedSubTaskJson)).build();
        HttpResponse<String> actualSuccessUpdateResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        SubTask updatedSubTask = taskManager.getAllSubTasks().stream().toList().get(0);
        Assertions.assertEquals(taskManager.getAllSubTasks().size(), 1);
        Assertions.assertEquals(201, actualSuccessUpdateResponse.statusCode());
        Assertions.assertTrue(taskManager.getAllSubTasks().size() == 1);
        Assertions.assertEquals(sendedSubTask.getId(), updatedSubTask.getId());
        Assertions.assertEquals(updatedName, updatedSubTask.getName());
        Assertions.assertEquals(updatedDescription, updatedSubTask.getDescription());
        Assertions.assertEquals(updatedStartTime, updatedSubTask.getStartTime());
        Assertions.assertEquals(updatedDuration, updatedSubTask.getDuration());
        Assertions.assertEquals(updatedEpic, updatedSubTask.getEpic());


        HttpRequest intersectedRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(sendedSubTaskJson)).build();
        HttpResponse<String> actualIntersectedUpdateResponse = client.send(intersectedRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, actualIntersectedUpdateResponse.statusCode());


        SubTask notExistSubTask = new SubTask("Подзадача", "Несуществующая Подзадача", Duration.ofSeconds(80),
                LocalDateTime.of(2080, 8, 8, 8, 8));
        notExistSubTask.setId(1000);
        String notExistSubTaskJson = gson.toJson(notExistSubTask);
        HttpRequest notFoundRequest = HttpRequest.newBuilder().uri(rightUrl).POST(HttpRequest.BodyPublishers.ofString(notExistSubTaskJson)).build();
        HttpResponse<String> actualNotFoundUpdateResponse = client.send(notFoundRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, actualNotFoundUpdateResponse.statusCode());
    }

    @Test
    public void deleteSubtaskTest() throws IOException, InterruptedException {
        Assertions.assertTrue(taskManager.getAllSubTasks().size() == 0);

        SubTask subTask = new SubTask("Подзадача", "Удаляемая Подзадача", Duration.ofSeconds(90),
                LocalDateTime.of(2090, 9, 9, 9, 9), createEpics().get(1));
        int notExistSubTaskId = subTask.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI notFoundUrl = URI.create(URL + notExistSubTaskId);
        HttpRequest notFoundRequest = HttpRequest.newBuilder().uri(notFoundUrl).DELETE().build();
        HttpResponse<String> actualNotFoundDeleteResponse = client.send(notFoundRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, actualNotFoundDeleteResponse.statusCode());


        taskManager.addSubTask(subTask);
        Assertions.assertTrue(taskManager.getAllSubTasks().size() == 1);
        int existSubTaskId = taskManager.getAllSubTasks().stream().toList().get(0).getId();

        URI rightUrl = URI.create(URL + existSubTaskId);
        HttpRequest rightRequest = HttpRequest.newBuilder().uri(rightUrl).DELETE().build();

        HttpResponse<String> actualSuccessDeleteResponse = client.send(rightRequest, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, actualSuccessDeleteResponse.statusCode());
        Assertions.assertEquals(taskManager.getAllSubTasks().size(), 0);
        Assertions.assertEquals(taskManager.getSubTaskById(existSubTaskId), null);
    }

}
