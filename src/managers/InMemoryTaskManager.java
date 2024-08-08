package managers;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    public static int idCounter = 0;
    protected static HashMap<Integer, Epic> allEpics = new HashMap<>();
    protected static HashMap<Integer, SubTask> allSubtasks = new HashMap<>();
    protected static HashMap<Integer, Task> allTasks = new HashMap<>();
    protected static HistoryManager historyManager = Managers.getDefaultHistory();

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
            historyManager.add(epic);
        }
        return allEpics.values();
    }

    @Override
    public Collection<SubTask> getAllSubTasks() {
        for (SubTask subTask : allSubtasks.values()) {
            historyManager.add(subTask);
        }
        return allSubtasks.values();
    }

    @Override
    public Collection<Task> getAllTasks() {
        for (Task task : allTasks.values()) {
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
                    epic.setStartTime(calculateEpicStartTime(subTasks));
                    epic.setDuration(calculateEpicDuration(subTasks));
                    epic.setEndTime(calculateEpicEndTime(subTasks));
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
                if (subTask != null) {
                    subTask.setId(++InMemoryTaskManager.idCounter);
                    subTask.setStatus(TaskStatus.NEW);
                    allSubtasks.put(subTask.getId(), subTask);
                }
            }
        }
        epic.setStatus(calculateStatus(subTasks));
        epic.setStartTime(calculateEpicStartTime(subTasks));
        epic.setDuration(calculateEpicDuration(subTasks));
        epic.setEndTime(calculateEpicEndTime(subTasks));
        allEpics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(++InMemoryTaskManager.idCounter);
        subTask.setStatus(TaskStatus.NEW);

        boolean isTaskIntersected = false;
        if (subTask.getStartTime() != null) {
            isTaskIntersected = getPrioritizedTasks().stream().anyMatch(sortedTask ->
                    InMemoryTaskManager.isTasksIntersected(sortedTask, subTask));
        }

        if (!isTaskIntersected) {
            allSubtasks.put(subTask.getId(), subTask);
            Epic epic = subTask.getEpic();
            if (allEpics.containsValue(epic)) {
                ArrayList<SubTask> subTasks = epic.getSubtasks();
                subTasks.add(subTask);
                epic.setStatus(calculateStatus(subTasks));
                epic.setStartTime(calculateEpicStartTime(subTasks));
                epic.setDuration(calculateEpicDuration(subTasks));
                epic.setEndTime(calculateEpicEndTime(subTasks));

            } else {
                if (epic != null) {
                    epic.setId(++InMemoryTaskManager.idCounter);
                    epic.setStatus(TaskStatus.NEW);
                    allEpics.put(epic.getId(), epic);
                }
            }
        } else {
            System.out.println("Подзадача " + subTask.getName() + " не будет создана, т. к. пересекается по времени с " +
                    "уже существующими задачами!");
        }
    }

    @Override
    public void addTask(Task task) {
        task.setId(++InMemoryTaskManager.idCounter);
        task.setStatus(TaskStatus.NEW);
        boolean isTaskIntersected = false;

        if (task.getStartTime() != null) {
            isTaskIntersected = getPrioritizedTasks().stream().anyMatch(sortedTask ->
                    InMemoryTaskManager.isTasksIntersected(sortedTask, task));
        }

        if (!isTaskIntersected) {
            allTasks.put(task.getId(), task);
        } else {
            System.out.println("Задача " + task.getName() + " не будет создана, т. к. пересекается по времени с " +
                    "уже существующими задачами!");
        }

    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        int epicId = updatedEpic.getId();
        if (allEpics.containsKey(epicId)) {
            Epic epic = allEpics.get(epicId);
            ArrayList<SubTask> subTasks = updatedEpic.getSubtasks();
            epic.setSubtasks(subTasks);
            epic.setStatus(calculateStatus(subTasks));
            epic.setStartTime(calculateEpicStartTime(subTasks));
            epic.setDuration(calculateEpicDuration(subTasks));
            epic.setEndTime(calculateEpicEndTime(subTasks));

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

            boolean isTaskIntersected = false;
            if (updatedSubTask.getStartTime() != null && (!subTask.getStartTime().equals(updatedSubTask.getStartTime()))) {
                isTaskIntersected = getPrioritizedTasks().stream().anyMatch(sortedTask ->
                        InMemoryTaskManager.isTasksIntersected(sortedTask, updatedSubTask));
            }

            if (!isTaskIntersected) {
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
                epic.setStartTime(calculateEpicStartTime(subTasks));
                epic.setDuration(calculateEpicDuration(subTasks));
                epic.setEndTime(calculateEpicEndTime(subTasks));
                allSubtasks.put(subTaskId, updatedSubTask);
                allEpics.put(epic.getId(), epic);
            } else {
                System.out.println("Подзадача " + updatedSubTask.getName() + " не будет обновлена, т. к. пересекается " +
                        "по времени с уже существующими задачами!");
            }
        } else {
            System.out.println("Подзадачи " + updatedSubTask + " пока не существует");
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        int taskId = updatedTask.getId();
        if (allTasks.containsKey(taskId)) {
            Task task = allTasks.get(taskId);

            boolean isTaskIntersected = false;
            if (updatedTask.getStartTime() != null && (!task.getStartTime().equals(updatedTask.getStartTime()))) {
                isTaskIntersected = getPrioritizedTasks().stream().anyMatch(sortedTask ->
                        InMemoryTaskManager.isTasksIntersected(sortedTask, updatedTask));
            }

            if (!isTaskIntersected) {
                allTasks.put(taskId, task);
            } else {
                System.out.println("Задача " + task.getName() + " не будет обновлена, т. к. пересекается " +
                        "по времени с уже существующими задачами!");
            }
        } else {
            System.out.println("Задачи " + updatedTask + " пока не существует");
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
        TaskStatus epicStatus;
        int newSubtaskCount = 0;
        int doneSubtaskCount = 0;
        int amountOfSubtasks = subTasks.size();
        if (amountOfSubtasks <= 0) {
            epicStatus = TaskStatus.NEW;
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
                epicStatus = TaskStatus.NEW;
            } else if (doneSubtaskCount == amountOfSubtasks) {
                epicStatus = TaskStatus.DONE;
            } else {
                epicStatus = TaskStatus.IN_PROGRESS;
            }
        }
        return epicStatus;
    }

    private Optional<LocalDateTime> calculateEpicStartTime(ArrayList<SubTask> subTasks) {
        return subTasks.stream()
                .filter(subTask -> subTask.getStartTime() != null)
                .map(SubTask::getStartTime)
                .min(Comparator.comparing(epoch -> epoch.toEpochSecond(ZoneOffset.UTC)));
    }

    private Duration calculateEpicDuration(ArrayList<SubTask> subTasks) {
        long durationInSeconds = subTasks.stream()
                .filter(subTask -> subTask.getDuration() != null)
                .map(SubTask::getDuration)
                .map(Duration::toSeconds)
                .reduce(0L, Long::sum);
        return Duration.ofSeconds(durationInSeconds);
    }

    private Optional<LocalDateTime> calculateEpicEndTime(ArrayList<SubTask> subTasks) {
        return subTasks.stream()
                .filter(subTask -> subTask.getStartTime() != null)
                .map(SubTask::getEndTime)
                .max(Comparator.comparing(epoch -> epoch.toEpochSecond(ZoneOffset.UTC)));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> tasksSortedyStartTime = new TreeSet<>();
        tasksSortedyStartTime.addAll(allTasks.values().stream().filter(task -> task.getStartTime() != null)
                .collect(Collectors.toSet()));
        tasksSortedyStartTime.addAll(allSubtasks.values().stream().filter(task -> task.getStartTime() != null)
                .collect(Collectors.toSet()));
        return tasksSortedyStartTime;

    }

    private static boolean isTasksIntersected(Task first, Task second) {
        boolean isIntersected = false;
        if (first.getEndTime().isAfter(second.getStartTime())) {
            isIntersected = true;
        }
        return isIntersected;
    }
}
