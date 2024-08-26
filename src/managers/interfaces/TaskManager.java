package managers.interfaces;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.*;

public interface TaskManager {

    HashMap<Integer, Task> getAllTypeTask();

    Collection<Epic> getAllEpics();

    Collection<SubTask> getAllSubTasks();

    Collection<Task> getAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    void removeAllTasks();

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    Task getTaskById(int id);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    void addTask(Task task);

    void updateEpic(Epic updatedEpic);

    void updateSubTask(SubTask updatedSubTask);

    void updateTask(Task task);

    void removeEpicById(int id);

    void removeSubTaskById(int id);

    void removeTaskById(int id);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    boolean isTasksIntersected(Task task);

}
