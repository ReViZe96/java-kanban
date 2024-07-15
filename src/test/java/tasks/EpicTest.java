package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EpicTest {

    @Test
    public void shouldEpicsWithSameIdIsEquals() {
        Epic firstEpic = new Epic("Первый эпик", "1");
        firstEpic.setId(2);

        Epic secondEpic = new Epic("Второй эпик", "2");
        secondEpic.setId(2);

        Assertions.assertEquals(firstEpic, secondEpic);
    }


    /*
    Тест public void shouldNotAddEpicToItselfsSubtasks() не нужен, т.к.
     тестируемый метод void setEpic(tasks.Epic epic)
     принимает в качестве аргумента только объекты типа tasks.Epic
     */

}
