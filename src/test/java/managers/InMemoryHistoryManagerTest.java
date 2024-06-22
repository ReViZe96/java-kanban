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

    @BeforeAll
    public static void init() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.addTask(task);
    }

    @Test
    public void shouldHistoryOfViewSaveViewsOfSameTask() {
        historyManager.resetHistory();
        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));

        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));
        Assertions.assertEquals(task, historyManager.getHistory().get(1));
    }
}
