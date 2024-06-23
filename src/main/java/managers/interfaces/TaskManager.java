package managers.interfaces;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    HashMap<Long, Task> getAllTypeTask();

    Collection<Epic> getAllEpics();

    Collection<SubTask> getAllSubTasks();

    Collection<Task> getAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    void removeAllTasks();

    Epic getEpicById(long id);

    SubTask getSubTaskById(long id);

    Task getTaskById(long id);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    void addTask(Task task);

    void updateEpic(Epic updatedEpic);

    void updateSubTask(SubTask updatedSubTask);

    void updateTask(Task task);

    void removeEpicById(long id);

    void removeSubTaskById(long id);

    void removeTaskById(long id);

    List<Task> getHistory();

}
