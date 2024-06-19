import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {

    @Test
    public void shouldReturnInMemoryTaskManagerObject() {
        Assertions.assertNotNull(Managers.getDefault());
        Assertions.assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    public void shouldReturnInMemoryHistoryManagerObject() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }
}
