package managers;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Task;

public class InMemoryHistoryManagerTest {

    public static TaskManager taskManager;
    public static HistoryManager historyManager;
    public static Task task = new Task("Задача", "Будет просмотрена не раз");
    public static Task awakening = new Task("Проснуться", "Необходимо проснуться в 8:00");
    public static Task sleeping = new Task("Заснуть", "Постараться заснуть раньше 1:00");


    @BeforeAll
    public static void init() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.addTask(task);
        taskManager.addTask(awakening);
        taskManager.addTask(sleeping);
    }

    @Test
    public void shouldHistoryOfViewSaveLastViewOfSameTask() {
        historyManager.resetHistory();
        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    public void shouldHistoryOfViewHasRightSequenceOfTasks() {
        historyManager.resetHistory();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(awakening.getId());
        taskManager.getTaskById(sleeping.getId());
        Assertions.assertEquals(3, historyManager.getHistory().size());
        Assertions.assertEquals(sleeping, historyManager.getHistory().get(0));
        Assertions.assertEquals(awakening, historyManager.getHistory().get(1));
        Assertions.assertEquals(task, historyManager.getHistory().get(2));
    }

    @Test
    public void shouldHistoryOfViewNotContainRemovedTasks() {
        historyManager.resetHistory();
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(awakening.getId());
        taskManager.getTaskById(sleeping.getId());
        Assertions.assertEquals(3, historyManager.getHistory().size());
        Assertions.assertEquals(sleeping, historyManager.getHistory().get(0));
        Assertions.assertEquals(awakening, historyManager.getHistory().get(1));
        Assertions.assertEquals(task, historyManager.getHistory().get(2));

        taskManager.removeTaskById(sleeping.getId());
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(awakening, historyManager.getHistory().get(0));
        Assertions.assertEquals(task, historyManager.getHistory().get(1));

        taskManager.removeAllTasks();
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }
}
