import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TasksManager {

    public HashMap<Long, Epik> allEpiks;
    public HashMap<Long, SubTask> allSubtasks;
    public HashMap<Long, Task> allTasks;

    public static int idCounter = 0;

    public TasksManager () {
        allEpiks = new HashMap<>();
        allSubtasks = new HashMap<>();
        allTasks = new HashMap<>();
    }

    /*
    public Epik epikSample = new Epik();
    public SubTask subTaskSample = new SubTask();
    public Task taskSample = new Task();

    public Object getTaskType (Object object) {
        Object taskType = new Object();
        if (epikSample.getClass().equals(object.getClass())) {
            taskType = epikSample.getClass();
        } else if (subTaskSample.getClass().equals(object.getClass())) {
            taskType = subTaskSample.getClass();
        } else if (taskSample.getClass().equals(object.getClass())) {
            taskType = taskSample.getClass();
        } else {
            System.out.println("Сущность не является задачей!");
        }
        return taskType;
    }
     */

    public Collection<Epik> getAllEpiks() {
        return allEpiks.values();
    }

    public Collection<SubTask> getAllSubTasks() {
        return allSubtasks.values();
    }

    public Collection<Task> getAllTasks() {
        return allTasks.values();
    }

    public void removeAllEpiks() {
        allEpiks.clear();
    }

    public void removeAllSubtasks() {
        allSubtasks.clear();
    }

    public void removeAllTasks() {
        allTasks.clear();
    }

    public Epik getEpikById(long id) {
        Epik epik;
        if (allEpiks.containsKey(id)) {
            epik = allEpiks.get(id);
        } else {
            throw new RuntimeException("Эпика с идентификатором " + id + " пока не существует");
        }
        return epik;
    }

    public SubTask getSubTaskById(long id) {
        SubTask subTask;
        if (allSubtasks.containsKey(id)) {
            subTask = allSubtasks.get(id);
        } else {
            throw new RuntimeException("Подзадачи с идентификатором " + id + " пока не существует");
        }
        return subTask;
    }

    public Task getTaskById(long id) {
        Task task;
        if (allTasks.containsKey(id)) {
            task = allTasks.get(id);
        } else {
            throw new RuntimeException("Задачи с идентификатором " + id + " пока не существует");
        }
        return task;
    }

    public void addEpik(Epik epik) {
        long epikId = epik.getId();
        if (!allEpiks.containsKey(epikId)) {
            allEpiks.put(epikId, epik);
        } else {
            throw new RuntimeException("Эпик " + epik + " уже существует");
        }
    }

    public void addSubTask(SubTask subTask) {
        long subTaskId = subTask.getId();
        if (!allSubtasks.containsKey(subTaskId)) {
            allSubtasks.put(subTaskId, subTask);
        } else {
            throw new RuntimeException("Подзадача " + subTask + " уже существует");
        }
    }

    public void addTask(Task task) {
        long taskId = task.getId();
        if (!allTasks.containsKey(taskId)) {
            allTasks.put(taskId, task);
        } else {
            throw new RuntimeException("Подзадача " + task + " уже существует");
        }
    }

    public void updateEpik(Epik epik) {
        long epikId = epik.getId();
        if (allEpiks.containsKey(epikId)) {
            allEpiks.put(epikId, epik);
        } else {
            throw new RuntimeException("Эпика " + epik + " пока не существует");
        }
    }

    public void updateSubTask(SubTask subTask) {
        long subTaskId = subTask.getId();
        if (allSubtasks.containsKey(subTaskId)) {
            allSubtasks.put(subTaskId, subTask);
        } else {
            throw new RuntimeException("Подзадачи " + subTask + " пока не существует");
        }
    }

    public void updateTask(Task task) {
        long taskId = task.getId();
        if (allTasks.containsKey(taskId)) {
            allTasks.put(taskId, task);
        } else {
            throw new RuntimeException("Задачи " + task + " пока не существует");
        }
    }

    public void removeEpikById (long id) {
        if (allEpiks.containsKey(id)) {
            allEpiks.remove(id);
        } else {
            throw new RuntimeException("Эпик с идентификатором " + id + " нельзя удалить, т.к. " +
                    "его пока не существует");
        }
    }

    public void removeSubTaskById (long id) {
        if (allSubtasks.containsKey(id)) {
            allSubtasks.remove(id);
        } else {
            throw new RuntimeException("Подзадачу с идентификатором " + id + " нельзя удалить, т.к. " +
                    "её пока не существует");
        }
    }

    public void removeTaskById (long id) {
        if (allTasks.containsKey(id)) {
            allTasks.remove(id);
        } else {
            throw new RuntimeException("Задачу с идентификатором " + id + " нельзя удалить, т.к. " +
                    "её пока не существует");
        }
    }

}
