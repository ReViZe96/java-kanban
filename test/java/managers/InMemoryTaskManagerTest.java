package managers;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;


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

    @Test
    public void shouldCalculateEpicStatus() {
        ArrayList<SubTask> newEpicSubtasks = new ArrayList<>();
        SubTask firstNewSubtask = new SubTask("firstNew", "new1");
        taskManager.addSubTask(firstNewSubtask);
        newEpicSubtasks.add(firstNewSubtask);
        SubTask secondNewSubtask = new SubTask("secondNew", "new2");
        taskManager.addSubTask(secondNewSubtask);
        newEpicSubtasks.add(secondNewSubtask);
        SubTask thirdNewSubtask = new SubTask("thirdNew", "new3");
        taskManager.addSubTask(thirdNewSubtask);
        newEpicSubtasks.add(thirdNewSubtask);
        Epic newEpic = new Epic("newEpic", "epicNew", newEpicSubtasks);
        taskManager.addEpic(newEpic);
        Assertions.assertEquals(TaskStatus.NEW, newEpic.getStatus());

        ArrayList<SubTask> doneEpicSubtasks = new ArrayList<>();
        SubTask firstDoneSubtask = new SubTask("firstDone", "done1");
        taskManager.addSubTask(firstDoneSubtask);
        firstDoneSubtask.setStatus(TaskStatus.DONE);
        doneEpicSubtasks.add(firstDoneSubtask);
        SubTask secondDoneSubtask = new SubTask("secondDone", "done2");
        taskManager.addSubTask(secondDoneSubtask);
        secondDoneSubtask.setStatus(TaskStatus.DONE);
        doneEpicSubtasks.add(secondDoneSubtask);
        SubTask thirdDoneSubtask = new SubTask("thirdDone", "done3");
        taskManager.addSubTask(thirdDoneSubtask);
        thirdDoneSubtask.setStatus(TaskStatus.DONE);
        doneEpicSubtasks.add(thirdDoneSubtask);
        Epic doneEpic = new Epic("doneEpic", "epicDone", doneEpicSubtasks);
        taskManager.addEpic(doneEpic);
        Assertions.assertEquals(TaskStatus.DONE, doneEpic.getStatus());

        ArrayList<SubTask> newAndDoneEpicSubtasks = new ArrayList<>();
        SubTask fourthNewSubtask = new SubTask("fourthNew", "new4");
        taskManager.addSubTask(fourthNewSubtask);
        newAndDoneEpicSubtasks.add(fourthNewSubtask);
        SubTask fourthDoneSubtask = new SubTask("fourthDone", "done4");
        taskManager.addSubTask(fourthDoneSubtask);
        fourthDoneSubtask.setStatus(TaskStatus.DONE);
        newAndDoneEpicSubtasks.add(fourthDoneSubtask);
        SubTask fifthDoneSubtask = new SubTask("fifthDone", "done5");
        taskManager.addSubTask(fifthDoneSubtask);
        fifthDoneSubtask.setStatus(TaskStatus.DONE);
        newAndDoneEpicSubtasks.add(fifthDoneSubtask);
        Epic newAndDoneEpic = new Epic("newAndDoneEpic", "epicNewAndDone", newAndDoneEpicSubtasks);
        taskManager.addEpic(newAndDoneEpic);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, newAndDoneEpic.getStatus());

        ArrayList<SubTask> inProgressEpicSubtasks = new ArrayList<>();
        SubTask firstInProgressSubtask = new SubTask("firstInProgress", "inProgress1");
        taskManager.addSubTask(firstInProgressSubtask);
        firstInProgressSubtask.setStatus(TaskStatus.IN_PROGRESS);
        inProgressEpicSubtasks.add(firstInProgressSubtask);
        SubTask secondInProgressSubtask = new SubTask("secondInProgress", "inProgress2");
        taskManager.addSubTask(secondInProgressSubtask);
        secondInProgressSubtask.setStatus(TaskStatus.IN_PROGRESS);
        inProgressEpicSubtasks.add(secondInProgressSubtask);
        SubTask thirdInProgressSubtask = new SubTask("thirdInProgress", "inProgress3");
        taskManager.addSubTask(thirdInProgressSubtask);
        thirdInProgressSubtask.setStatus(TaskStatus.IN_PROGRESS);
        inProgressEpicSubtasks.add(thirdInProgressSubtask);
        Epic inProgressEpic = new Epic("inProgressEpic", "epicInProgress", inProgressEpicSubtasks);
        taskManager.addEpic(inProgressEpic);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, inProgressEpic.getStatus());
    }
}
