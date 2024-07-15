package managers.interfaces;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void resetHistory();

    List<Task> getHistory();

    void remove(int id);

}
