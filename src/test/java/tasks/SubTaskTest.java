package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SubTaskTest {

    @Test
    public void shouldSubTasksWithSameIdIsEquals() {
        SubTask firstSubTask = new SubTask("Первая подзадача", "1");
        firstSubTask.setId(3L);

        SubTask secondSubTask = new SubTask("Вторая подзадача", "2");
        secondSubTask.setId(3L);

        Assertions.assertEquals(firstSubTask, secondSubTask);
    }

    /*
    Тест public void shouldNotAddSubTaskToItselfsEpic() не нужен, т.к.
     у аргумента тестируемого метода void setSubtasks(ArrayList<tasks.SubTask> subTasks)
     присутствует параметризация и строгое ограничение типа
     */

}
