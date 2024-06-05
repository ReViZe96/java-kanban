import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TasksManager {

    public HashMap<Long, Epik> allEpiks;
    public HashMap<Long, SubTask> allSubtasks;
    public HashMap<Long, Task> allTasks;

    public static int idCounter = 1;

    public TasksManager() {
        allEpiks = new HashMap<>();
        allSubtasks = new HashMap<>();
        allTasks = new HashMap<>();
    }

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

    public void updateEpik(Epik updatedEpik) {
        long epikId = updatedEpik.getId();
        if (allEpiks.containsKey(epikId)) {
            Epik epik = allEpiks.get(epikId);
            epik.setSubtasks(updatedEpik.getSubtasks());
            allEpiks.put(epikId, epik);
        } else {
            throw new RuntimeException("Эпика " + updatedEpik + " пока не существует");
        }
    }

    public void updateSubTask(SubTask updatedSubTask) {
        long subTaskId = updatedSubTask.getId();
        if (allSubtasks.containsKey(subTaskId)) {
            SubTask subTask = getSubTaskById(subTaskId);
            Epik epik = subTask.getEpik();
            ArrayList<SubTask> subTasks = epik.getSubtasks();
            if (subTasks.contains(subTask)) {
                for (int i = 0; i < subTasks.size(); i++) {
                    if (subTasks.get(i).getId() == (updatedSubTask.getId())) {
                        subTasks.set(i, updatedSubTask);
                    }
                }
            } else {
                subTasks.add(updatedSubTask);
            }
            epik.setSubtasks(subTasks);
            allSubtasks.put(subTaskId, updatedSubTask);
        } else {
            throw new RuntimeException("Подзадачи " + updatedSubTask + " пока не существует");
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

    public void removeEpikById(long id) {
        if (allEpiks.containsKey(id)) {
            allEpiks.remove(id);
        } else {
            throw new RuntimeException("Эпик с идентификатором " + id + " нельзя удалить, т.к. " +
                    "его пока не существует");
        }
    }

    public void removeSubTaskById(long id) {
        if (allSubtasks.containsKey(id)) {
            allSubtasks.remove(id);
        } else {
            throw new RuntimeException("Подзадачу с идентификатором " + id + " нельзя удалить, т.к. " +
                    "её пока не существует");
        }
    }

    public void removeTaskById(long id) {
        if (allTasks.containsKey(id)) {
            allTasks.remove(id);
        } else {
            throw new RuntimeException("Задачу с идентификатором " + id + " нельзя удалить, т.к. " +
                    "её пока не существует");
        }
    }

}
