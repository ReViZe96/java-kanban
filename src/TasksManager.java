import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TasksManager {

    protected HashMap<Long, Epic> allEpics;
    protected HashMap<Long, SubTask> allSubtasks;
    protected HashMap<Long, Task> allTasks;

    public static int idCounter = 0;

    public TasksManager() {
        allEpics = new HashMap<>();
        allSubtasks = new HashMap<>();
        allTasks = new HashMap<>();
    }

    public Collection<Epic> getAllEpics() {
        return allEpics.values();
    }

    public Collection<SubTask> getAllSubTasks() {
        return allSubtasks.values();
    }

    public Collection<Task> getAllTasks() {
        return allTasks.values();
    }

    public void removeAllEpics() {
        allEpics.clear();
    }

    public void removeAllSubtasks() {
        for (SubTask subTask : allSubtasks.values()) {
            for (Epic epic : allEpics.values()) {
                if (subTask.getEpic().equals(epic)) {
                    ArrayList<SubTask> subTasks = epic.getSubtasks();
                    subTasks.remove(subTask);
                    epic.setStatus(calculateStatus(subTasks));
                    allEpics.put(epic.getId(), epic);
                }
            }
        }
        allSubtasks.clear();
    }

    public void removeAllTasks() {
        allTasks.clear();
    }

    public Epic getEpicById(long id) {
        Epic epic;
        if (allEpics.containsKey(id)) {
            epic = allEpics.get(id);
        } else {
            throw new RuntimeException("Эпика с идентификатором " + id + " пока не существует");
        }
        return epic;
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

    public void addEpic(Epic epic) {
        epic.setId(++TasksManager.idCounter);
        ArrayList<SubTask> subTasks = epic.getSubtasks();
        for (SubTask subTask : subTasks) {
            if (allSubtasks.containsValue(subTask)) {
                SubTask existSubTask = allSubtasks.get(subTask.getId());
                existSubTask.setEpic(epic);
                allSubtasks.put(subTask.getId(), subTask);
            } else {
                subTask.setId(++TasksManager.idCounter);
                subTask.setStatus(TaskStatus.NEW);
                allSubtasks.put(subTask.getId(), subTask);
            }
        }
        epic.setStatus(calculateStatus(subTasks));
        allEpics.put(epic.getId(), epic);
    }

    public void addSubTask(SubTask subTask) {
        subTask.setId(++TasksManager.idCounter);
        subTask.setStatus(TaskStatus.NEW);
        allSubtasks.put(subTask.getId(), subTask);
        Epic epic = subTask.getEpic();
        if (allEpics.containsValue(epic)) {
            ArrayList<SubTask> subTasks = epic.getSubtasks();
            subTasks.add(subTask);
            epic.setStatus(calculateStatus(subTasks));
        } else {
            epic.setId(++TasksManager.idCounter);
            epic.setStatus(TaskStatus.NEW);
        }
        allEpics.put(epic.getId(), epic);
    }

    public void addTask(Task task) {
        task.setId(++TasksManager.idCounter);
        task.setStatus(TaskStatus.NEW);
        allTasks.put(task.getId(), task);

    }

    public void updateEpic(Epic updatedEpic) {
        long epicId = updatedEpic.getId();
        if (allEpics.containsKey(epicId)) {
            Epic epic = allEpics.get(epicId);
            ArrayList<SubTask> subTasks = updatedEpic.getSubtasks();
            epic.setSubtasks(subTasks);
            epic.setStatus(calculateStatus(subTasks));
            allEpics.put(epicId, epic);
        } else {
            throw new RuntimeException("Эпика " + updatedEpic + " пока не существует");
        }
    }

    public void updateSubTask(SubTask updatedSubTask) {
        long subTaskId = updatedSubTask.getId();
        if (allSubtasks.containsKey(subTaskId)) {
            SubTask subTask = getSubTaskById(subTaskId);
            Epic epic = subTask.getEpic();
            ArrayList<SubTask> subTasks = epic.getSubtasks();
            if (subTasks.contains(subTask)) {
                for (int i = 0; i < subTasks.size(); i++) {
                    if (subTasks.get(i).getId() == (updatedSubTask.getId())) {
                        subTasks.set(i, updatedSubTask);
                    }
                }
            }
            epic.setSubtasks(subTasks);
            epic.setStatus(calculateStatus(subTasks));
            allSubtasks.put(subTaskId, updatedSubTask);
            allEpics.put(epic.getId(), epic);
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

    public void removeEpicById(long id) {
        if (allEpics.containsKey(id)) {
            allEpics.remove(id);
        } else {
            throw new RuntimeException("Эпик с идентификатором " + id + " нельзя удалить, т.к. " +
                    "его пока не существует");
        }
    }

    public void removeSubTaskById(long id) {
        if (allSubtasks.containsKey(id)) {
            SubTask subTask = allSubtasks.get(id);
            Epic epic = subTask.getEpic();
            epic.getSubtasks().remove(subTask);
            epic.setStatus(calculateStatus(epic.getSubtasks()));
            allEpics.put(epic.getId(), epic);
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

    private TaskStatus calculateStatus(ArrayList<SubTask> subTasks) {
        TaskStatus EpicStatus;
        int newSubtaskCount = 0;
        int doneSubtaskCount = 0;
        int amountOfSubtasks = subTasks.size();
        if (amountOfSubtasks <= 0) {
            EpicStatus = TaskStatus.NEW;
        } else {
            for (SubTask subTask : subTasks) {
                switch (subTask.getStatus()) {
                    case TaskStatus.NEW:
                        newSubtaskCount++;
                        break;
                    case TaskStatus.DONE:
                        doneSubtaskCount++;
                        break;
                }
            }
            if (newSubtaskCount == amountOfSubtasks) {
                EpicStatus = TaskStatus.NEW;
            } else if (doneSubtaskCount == amountOfSubtasks) {
                EpicStatus = TaskStatus.DONE;
            } else {
                EpicStatus = TaskStatus.IN_PROGRESS;
            }
        }
        return EpicStatus;
    }

}
