import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InMemoryHistoryManagerTest {

    public static TaskManager taskManager;
    public static HistoryManager historyManager;

    @BeforeAll
    public static void init() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldHistoryOfViewSaveViewsOfSameTask() {
        Task task = new Task("Задача", "Будет просмотрена не раз");
        taskManager.addTask(task);
        Assertions.assertEquals(0, historyManager.getHistory().size());

        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));

        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(task, historyManager.getHistory().get(0));
        Assertions.assertEquals(task, historyManager.getHistory().get(1));
    }
}
