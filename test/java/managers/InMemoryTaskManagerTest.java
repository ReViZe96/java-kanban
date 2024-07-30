package managers;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;


public class InMemoryTaskManagerTest {

    public static TaskManager taskManager;

    @BeforeAll
    public static void init() {
        taskManager = Managers.getDefault();
    }

    @AfterAll
    public static void removeAllTasks() {
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
    }

    @Test
    public void shouldAddAndGetByIdEpic() {
        Epic addedEpic = new Epic("Добавляемый эпик", "added");
        taskManager.addEpic(addedEpic);

        Epic foundedEpic = taskManager.getEpicById(addedEpic.getId());

        Assertions.assertEquals(addedEpic, foundedEpic);
    }

    @Test
    public void shouldAddAndGetByIdSubTask() {
        Epic epic = new Epic("Эпик добавляемой подзадачи", "Эпик");
        SubTask addedSubTask = new SubTask("Добавляемая подзадача", "added", epic);
        taskManager.addSubTask(addedSubTask);

        SubTask foundedSubTask = taskManager.getSubTaskById(addedSubTask.getId());

        Assertions.assertEquals(addedSubTask, foundedSubTask);
    }

    @Test
    public void shouldAddAndGetByIdTask() {
        Task addedTask = new Task("Добавляемая задача", "added");
        taskManager.addTask(addedTask);

        Task foundedTask = taskManager.getTaskById(addedTask.getId());

        Assertions.assertEquals(addedTask, foundedTask);
    }

    @Test
    public void shouldTasksWithSameIdIsNotConflicted() {
        Task taskWithGeneratedId = new Task("Первая задача", "Id будет сгенерирован в процессе добавления");
        taskManager.addTask(taskWithGeneratedId);
        int id = taskWithGeneratedId.getId();
        Assertions.assertEquals(taskWithGeneratedId, taskManager.getTaskById(id));

        Task taskWithAddedId = new Task("Вторая задача", "Id будет задан вручную");
        taskWithAddedId.setId(id);
        taskManager.updateTask(taskWithAddedId);
        Assertions.assertEquals(taskWithAddedId, taskManager.getTaskById(id));
    }

    @Test
    public void shouldTaskIsNotChangedWhenAdd() {
        Task beforeAddTask = new Task("Добавляемая задача", "added");
        beforeAddTask.setId(1);
        beforeAddTask.setStatus(TaskStatus.NEW);
        taskManager.addTask(beforeAddTask);

        Task afterAddTask = taskManager.getTaskById(beforeAddTask.getId());

        Assertions.assertEquals(beforeAddTask.getId(), afterAddTask.getId());
        Assertions.assertEquals(beforeAddTask.getName(), afterAddTask.getName());
        Assertions.assertEquals(beforeAddTask.getStatus(), afterAddTask.getStatus());
        Assertions.assertEquals(beforeAddTask.getDescription(), afterAddTask.getDescription());
    }
}
