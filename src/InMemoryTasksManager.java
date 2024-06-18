import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class InMemoryTasksManager implements TaskManager {

    protected HashMap<Long, Epic> allEpics;
    protected HashMap<Long, SubTask> allSubtasks;
    protected HashMap<Long, Task> allTasks;
    protected HistoryManager historyManager;

    public static int idCounter = 0;

    public InMemoryTasksManager() {
        allEpics = new HashMap<>();
        allSubtasks = new HashMap<>();
        allTasks = new HashMap<>();
        historyManager = new InMemoryHistoryManager();
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return allEpics.values();
    }

    @Override
    public Collection<SubTask> getAllSubTasks() {
        return allSubtasks.values();
    }

    @Override
    public Collection<Task> getAllTasks() {
        return allTasks.values();
    }

    @Override
    public void removeAllEpics() {
        allEpics.clear();
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
        allSubtasks.clear();
    }

    @Override
    public void removeAllTasks() {
        allTasks.clear();
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic;
        if (allEpics.containsKey(id)) {
            epic = allEpics.get(id);
            epic.setAmountOfView(epic.getAmountOfView() + 1);
            updateEpic(epic);
            ArrayList<Long> historyOfView = historyManager.getHistoryOfView();
            historyOfView.add(id);
            historyManager.setHistoryOfView(historyOfView);
        } else {
            System.out.println("Эпика с идентификатором " + id + " пока не существует");
            return null;
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        SubTask subTask;
        if (allSubtasks.containsKey(id)) {
            subTask = allSubtasks.get(id);
            subTask.setAmountOfView(subTask.getAmountOfView() + 1);
            updateSubTask(subTask);
            ArrayList<Long> historyOfView = historyManager.getHistoryOfView();
            historyOfView.add(id);
            historyManager.setHistoryOfView(historyOfView);
        } else {
            System.out.println("Подзадачи с идентификатором " + id + " пока не существует");
            return null;
        }
        return subTask;
    }

    @Override
    public Task getTaskById(long id) {
        Task task;
        if (allTasks.containsKey(id)) {
            task = allTasks.get(id);
            task.setAmountOfView(task.getAmountOfView() + 1);
            updateTask(task);
            ArrayList<Long> historyOfView = historyManager.getHistoryOfView();
            historyOfView.add(id);
            historyManager.setHistoryOfView(historyOfView);
        } else {
            System.out.println("Задачи с идентификатором " + id + " пока не существует");
            return null;
        }
        return task;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++InMemoryTasksManager.idCounter);
        ArrayList<SubTask> subTasks = epic.getSubtasks();
        for (SubTask subTask : subTasks) {
            if (allSubtasks.containsValue(subTask)) {
                SubTask existSubTask = allSubtasks.get(subTask.getId());
                existSubTask.setEpic(epic);
                allSubtasks.put(subTask.getId(), subTask);
            } else {
                subTask.setId(++InMemoryTasksManager.idCounter);
                subTask.setStatus(TaskStatus.NEW);
                allSubtasks.put(subTask.getId(), subTask);
            }
        }
        epic.setStatus(calculateStatus(subTasks));
        allEpics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(++InMemoryTasksManager.idCounter);
        subTask.setStatus(TaskStatus.NEW);
        allSubtasks.put(subTask.getId(), subTask);
        Epic epic = subTask.getEpic();
        if (allEpics.containsValue(epic)) {
            ArrayList<SubTask> subTasks = epic.getSubtasks();
            subTasks.add(subTask);
            epic.setStatus(calculateStatus(subTasks));
        } else {
            epic.setId(++InMemoryTasksManager.idCounter);
            epic.setStatus(TaskStatus.NEW);
        }
        allEpics.put(epic.getId(), epic);
    }

    @Override
    public void addTask(Task task) {
        task.setId(++InMemoryTasksManager.idCounter);
        task.setStatus(TaskStatus.NEW);
        allTasks.put(task.getId(), task);

    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        long epicId = updatedEpic.getId();
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
            System.out.println("Подзадачи " + updatedSubTask + " пока не существует");
        }
    }

    @Override
    public void updateTask(Task task) {
        long taskId = task.getId();
        if (allTasks.containsKey(taskId)) {
            allTasks.put(taskId, task);
        } else {
            System.out.println("Задачи " + task + " пока не существует");
        }
    }

    public void removeEpicById(long id) {
        if (allEpics.containsKey(id)) {
            allEpics.remove(id);
        } else {
            System.out.println("Эпик с идентификатором " + id + " нельзя удалить, т.к. " +
                    "его пока не существует");
        }
    }

    @Override
    public void removeSubTaskById(long id) {
        if (allSubtasks.containsKey(id)) {
            SubTask subTask = allSubtasks.get(id);
            Epic epic = subTask.getEpic();
            epic.getSubtasks().remove(subTask);
            epic.setStatus(calculateStatus(epic.getSubtasks()));
            allEpics.put(epic.getId(), epic);
            allSubtasks.remove(id);
        } else {
            System.out.println("Подзадачу с идентификатором " + id + " нельзя удалить, т.к. " +
                    "её пока не существует");
        }
    }

    @Override
    public void removeTaskById(long id) {
        if (allTasks.containsKey(id)) {
            allTasks.remove(id);
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

}
