package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void shouldTasksWithSameIdIsEquals() {
        Task firstTask = new Task("Первая задача", "1");
        firstTask.setId(1);

        Task secondTask = new Task("Вторая задача", "2");
        secondTask.setId(1);

        Assertions.assertEquals(firstTask, secondTask);
    }

}
