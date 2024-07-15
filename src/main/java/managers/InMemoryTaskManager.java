package managers;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    public static int idCounter = 0;
    private static HashMap<Integer, Epic> allEpics = new HashMap<>();
    private static HashMap<Integer, SubTask> allSubtasks = new HashMap<>();
    private static HashMap<Integer, Task> allTasks = new HashMap<>();
    private static HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
    }

    public HashMap<Integer, Task> getAllTypeTask() {
        HashMap<Integer, Task> allTypesTasks = new HashMap<>();
        allTypesTasks.putAll(allEpics);
        allTypesTasks.putAll(allSubtasks);
        allTypesTasks.putAll(allTasks);
        return allTypesTasks;
    }

    @Override
    public Collection<Epic> getAllEpics() {
        for (Epic epic : allEpics.values()) {
            epic.setAmountOfView(epic.getAmountOfView() + 1);
            updateEpic(epic);
            historyManager.add(epic);
        }
        return allEpics.values();
    }

    @Override
    public Collection<SubTask> getAllSubTasks() {
        for (SubTask subTask : allSubtasks.values()) {
            subTask.setAmountOfView(subTask.getAmountOfView() + 1);
            updateSubTask(subTask);
            historyManager.add(subTask);
        }
        return allSubtasks.values();
    }

    @Override
    public Collection<Task> getAllTasks() {
        for (Task task : allTasks.values()) {
            task.setAmountOfView(task.getAmountOfView() + 1);
            updateTask(task);
            historyManager.add(task);
        }
        return allTasks.values();
    }

    @Override
    public void removeAllEpics() {
        List<Integer> epicIds = new ArrayList<>();
        Set<Map.Entry<Integer, Epic>> epicsEntrySet = allEpics.entrySet();
        for (Map.Entry<Integer, Epic> epicEntry : epicsEntrySet) {
            epicIds.add(epicEntry.getKey());
        }
        allEpics.clear();
        for (Integer epicId : epicIds) {
            historyManager.remove(epicId);
        }
    }

    @Override
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
        List<Integer> subTaskIds = new ArrayList<>();
        Set<Map.Entry<Integer, SubTask>> subTasksEntrySet = allSubtasks.entrySet();
        for (Map.Entry<Integer, SubTask> subTaskEntry : subTasksEntrySet) {
            subTaskIds.add(subTaskEntry.getKey());
        }
        allSubtasks.clear();
        for (Integer subTaskId : subTaskIds) {
            historyManager.remove(subTaskId);
        }
    }

    @Override
    public void removeAllTasks() {
        List<Integer> taskIds = new ArrayList<>();
        Set<Map.Entry<Integer, Task>> tasksEntrySet = allTasks.entrySet();
        for (Map.Entry<Integer, Task> taskEntry : tasksEntrySet) {
            taskIds.add(taskEntry.getKey());
        }
        allTasks.clear();
        for (Integer taskId : taskIds) {
            historyManager.remove(taskId);
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic;
        if (allEpics.containsKey(id)) {
            epic = allEpics.get(id);
            epic.setAmountOfView(epic.getAmountOfView() + 1);
            updateEpic(epic);
            historyManager.add(epic);
        } else {
            System.out.println("Эпика с идентификатором " + id + " пока не существует");
            return null;
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask;
        if (allSubtasks.containsKey(id)) {
            subTask = allSubtasks.get(id);
            subTask.setAmountOfView(subTask.getAmountOfView() + 1);
            updateSubTask(subTask);
            historyManager.add(subTask);
        } else {
            System.out.println("Подзадачи с идентификатором " + id + " пока не существует");
            return null;
        }
        return subTask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task;
        if (allTasks.containsKey(id)) {
            task = allTasks.get(id);
            task.setAmountOfView(task.getAmountOfView() + 1);
            updateTask(task);
            historyManager.add(task);
        } else {
            System.out.println("Задачи с идентификатором " + id + " пока не существует");
            return null;
        }
        return task;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++InMemoryTaskManager.idCounter);
        ArrayList<SubTask> subTasks = epic.getSubtasks();
        for (SubTask subTask : subTasks) {
            if (allSubtasks.containsValue(subTask)) {
                SubTask existSubTask = allSubtasks.get(subTask.getId());
                existSubTask.setEpic(epic);
                allSubtasks.put(subTask.getId(), subTask);
            } else {
                subTask.setId(++InMemoryTaskManager.idCounter);
                subTask.setStatus(TaskStatus.NEW);
                allSubtasks.put(subTask.getId(), subTask);
            }
        }
        epic.setStatus(calculateStatus(subTasks));
        allEpics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(++InMemoryTaskManager.idCounter);
        subTask.setStatus(TaskStatus.NEW);
        allSubtasks.put(subTask.getId(), subTask);
        Epic epic = subTask.getEpic();
        if (allEpics.containsValue(epic)) {
            ArrayList<SubTask> subTasks = epic.getSubtasks();
            subTasks.add(subTask);
            epic.setStatus(calculateStatus(subTasks));
        } else {
            epic.setId(++InMemoryTaskManager.idCounter);
            epic.setStatus(TaskStatus.NEW);
        }
        allEpics.put(epic.getId(), epic);
    }

    @Override
    public void addTask(Task task) {
        task.setId(++InMemoryTaskManager.idCounter);
        task.setStatus(TaskStatus.NEW);
        allTasks.put(task.getId(), task);

    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        int epicId = updatedEpic.getId();
        if (allEpics.containsKey(epicId)) {
            Epic epic = allEpics.get(epicId);
            ArrayList<SubTask> subTasks = updatedEpic.getSubtasks();
            epic.setSubtasks(subTasks);
            epic.setStatus(calculateStatus(subTasks));
            allEpics.put(epicId, epic);
        } else {
            System.out.println("Эпика " + updatedEpic + " пока не существует");
        }
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        int subTaskId = updatedSubTask.getId();
        if (allSubtasks.containsKey(subTaskId)) {
            SubTask subTask = allSubtasks.get(subTaskId);
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
            System.out.println("Подзадачи " + updatedSubTask + " пока не существует");
        }
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        if (allTasks.containsKey(taskId)) {
            allTasks.put(taskId, task);
        } else {
            System.out.println("Задачи " + task + " пока не существует");
        }
    }

    public void removeEpicById(int id) {
        if (allEpics.containsKey(id)) {
            allEpics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Эпик с идентификатором " + id + " нельзя удалить, т.к. " +
                    "его пока не существует");
        }
    }

    @Override
    public void removeSubTaskById(int id) {
        if (allSubtasks.containsKey(id)) {
            SubTask subTask = allSubtasks.get(id);
            Epic epic = subTask.getEpic();
            epic.getSubtasks().remove(subTask);
            epic.setStatus(calculateStatus(epic.getSubtasks()));
            allEpics.put(epic.getId(), epic);
            allSubtasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадачу с идентификатором " + id + " нельзя удалить, т.к. " +
                    "её пока не существует");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (allTasks.containsKey(id)) {
            allTasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задачу с идентификатором " + id + " нельзя удалить, т.к. " +
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
