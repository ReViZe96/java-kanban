package managers;

import managers.exceptions.NotFoundException;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.*;
import tasks.Comparators.TaskComparator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    public static int idCounter = 0;
    protected static HashMap<Integer, Epic> allEpics = new HashMap<>();
    protected static HashMap<Integer, SubTask> allSubtasks = new HashMap<>();
    protected static HashMap<Integer, Task> allTasks = new HashMap<>();
    protected static HistoryManager historyManager = Managers.getDefaultHistory();
    protected static TreeSet<Task> tasksSortedByStartTime = new TreeSet<>(new TaskComparator());

    public InMemoryTaskManager() {
    }

    @Override
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
                if (subTask.getEpic() != null && subTask.getEpic().equals(epic)) {
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
            if (tasksSortedByStartTime.contains(subTaskEntry.getValue())) {
                tasksSortedByStartTime.remove(subTaskEntry.getValue());
            }
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
            if (tasksSortedByStartTime.contains(taskEntry.getValue())) {
                tasksSortedByStartTime.remove(taskEntry.getValue());
            }
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
            throw new NotFoundException("Задачи с идентификатором " + id + " пока не существует");
        }
        return task;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++InMemoryTaskManager.idCounter);
        allEpics.put(epic.getId(), epic);

        ArrayList<SubTask> epicSubTasks = epic.getSubtasks();
        if (!epicSubTasks.isEmpty()) {
            for (SubTask epicSubTask : epicSubTasks) {
                if (allSubtasks.containsValue(epicSubTask)) {
                    SubTask existSubTask = allSubtasks.get(epicSubTask.getId());
                    existSubTask.setEpic(epic);
                } else {
                    epicSubTask.setId(++InMemoryTaskManager.idCounter);
                    epicSubTask.setStatus(TaskStatus.NEW);
                    allSubtasks.put(epicSubTask.getId(), epicSubTask);
                }
            }
        }

        Optional<LocalDateTime> epicStartTime = calculateEpicStartTime(epicSubTasks);
        Duration epicDuration = calculateEpicDuration(epicSubTasks);
        Optional<LocalDateTime> epicEndTime = calculateEpicEndTime(epicSubTasks);
        epic.setStatus(calculateStatus(epicSubTasks));
        epic.setStartTime(epicStartTime.isPresent() ? epicStartTime : Optional.of(LocalDateTime.of(1970, 1, 1, 0, 0)));
        epic.setDuration(epicDuration != null ? epicDuration : Duration.ofSeconds(0L));
        epic.setEndTime(epicEndTime.isPresent() ? epicEndTime : Optional.of(LocalDateTime.of(1970, 1, 1, 0, 0)));
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(++InMemoryTaskManager.idCounter);

        if (subTask.getStartTime() != null && !isTasksIntersected(subTask)) {
            tasksSortedByStartTime.add(subTask);
        }

        allSubtasks.put(subTask.getId(), subTask);
        Epic subTaskEpic = subTask.getEpic();
        if (subTaskEpic != null) {
            if (allEpics.containsValue(subTaskEpic)) {
                Epic existEpic = allEpics.get(subTaskEpic.getId());
                ArrayList<SubTask> subTasks = existEpic.getSubtasks();
                subTasks.add(subTask);
                existEpic.setStatus(calculateStatus(subTasks));
                existEpic.setStartTime(calculateEpicStartTime(subTasks));
                existEpic.setDuration(calculateEpicDuration(subTasks));
                existEpic.setEndTime(calculateEpicEndTime(subTasks));
            } else {
                subTaskEpic.setId(++InMemoryTaskManager.idCounter);
                subTaskEpic.setStatus(TaskStatus.NEW);
                allEpics.put(subTaskEpic.getId(), subTaskEpic);
            }
        }
    }

    @Override
    public void addTask(Task task) {
        task.setId(++InMemoryTaskManager.idCounter);

        if (task.getStartTime() != null && !isTasksIntersected(task)) {
            tasksSortedByStartTime.add(task);
        }
        allTasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        int epicId = updatedEpic.getId();
        if (allEpics.containsKey(epicId)) {
            Epic existEpic = allEpics.get(epicId);

            ArrayList<SubTask> updatedEpicSubTasks = updatedEpic.getSubtasks();
            if (!updatedEpicSubTasks.isEmpty()) {
                for (SubTask updatedEpicSubtask : updatedEpicSubTasks) {
                    if (allSubtasks.containsKey(updatedEpicSubtask.getId())) {
                        SubTask epicSubTask = allSubtasks.get(updatedEpicSubtask.getId());
                        epicSubTask.setEpic(existEpic);
                        allSubtasks.remove(updatedEpicSubtask.getId());
                        allSubtasks.put(epicSubTask.getId(), epicSubTask);
                    } else {
                        allSubtasks.put(updatedEpicSubtask.getId(), updatedEpicSubtask);
                    }
                }
            }
            existEpic.setName(updatedEpic.getName());
            existEpic.setDescription(updatedEpic.getDescription());
            existEpic.setSubtasks(updatedEpicSubTasks);
            existEpic.setStatus(calculateStatus(updatedEpicSubTasks));
            existEpic.setStartTime(calculateEpicStartTime(updatedEpicSubTasks));
            existEpic.setDuration(calculateEpicDuration(updatedEpicSubTasks));
            existEpic.setEndTime(calculateEpicEndTime(updatedEpicSubTasks));
            allEpics.put(epicId, existEpic);

        } else {
            System.out.println("Эпика " + updatedEpic + " пока не существует");
        }
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        int subTaskId = updatedSubTask.getId();
        if (allSubtasks.containsKey(subTaskId)) {
            SubTask existSubTask = allSubtasks.get(subTaskId);

            if (updatedSubTask.getStartTime() != null && (!existSubTask.getStartTime().equals(updatedSubTask.getStartTime())) &&
                    !isTasksIntersected(updatedSubTask)) {
                tasksSortedByStartTime.remove(existSubTask);
                tasksSortedByStartTime.add(updatedSubTask);
            }

            Epic subTasksEpic = existSubTask.getEpic();
            if (subTasksEpic != null) {
                ArrayList<SubTask> epicSubTasks = subTasksEpic.getSubtasks();
                for (int i = 0; i < epicSubTasks.size(); i++) {
                    if (epicSubTasks.get(i).getId() == (updatedSubTask.getId())) {
                        epicSubTasks.set(i, updatedSubTask);
                    }
                }

                subTasksEpic.setSubtasks(epicSubTasks);
                subTasksEpic.setStatus(calculateStatus(epicSubTasks));
                subTasksEpic.setStartTime(calculateEpicStartTime(epicSubTasks));
                subTasksEpic.setDuration(calculateEpicDuration(epicSubTasks));
                subTasksEpic.setEndTime(calculateEpicEndTime(epicSubTasks));
                allEpics.put(subTasksEpic.getId(), subTasksEpic);
            }

            allSubtasks.put(subTaskId, updatedSubTask);

        } else {
            System.out.println("Подзадачи " + updatedSubTask + " пока не существует");
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        int taskId = updatedTask.getId();
        if (allTasks.containsKey(taskId)) {
            Task task = allTasks.get(taskId);

            if (updatedTask.getStartTime() != null && (!task.getStartTime().equals(updatedTask.getStartTime())) &&
                    !isTasksIntersected(updatedTask)) {
                tasksSortedByStartTime.remove(task);
                tasksSortedByStartTime.add(updatedTask);
            }

            allTasks.put(taskId, updatedTask);

        } else {
            System.out.println("Задачи " + updatedTask + " пока не существует");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (allEpics.containsKey(id)) {
            Epic epic = allEpics.get(id);
            ArrayList<SubTask> epicSubtasks = epic.getSubtasks();
            for (SubTask epicSubTask : epicSubtasks) {
                allSubtasks.remove(epicSubTask.getId());
            }
            allEpics.remove(id);
            historyManager.remove(id);
        } else {
            throw new NotFoundException("Эпик с идентификатором " + id + " нельзя удалить, т.к. " +
                    "его пока не существует");
        }
    }

    @Override
    public void removeSubTaskById(int id) {
        if (allSubtasks.containsKey(id)) {
            SubTask subTask = allSubtasks.get(id);
            Epic epic = subTask.getEpic();
            if (epic != null) {
                epic.getSubtasks().remove(subTask);
                epic.setStatus(calculateStatus(epic.getSubtasks()));
                allEpics.put(epic.getId(), epic);
            }
            allSubtasks.remove(id);
            tasksSortedByStartTime.remove(subTask);
            historyManager.remove(id);
        } else {
            throw new NotFoundException("Подзадачу с идентификатором " + id + " нельзя удалить, т.к. " +
                    "её пока не существует");
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (allTasks.containsKey(id)) {
            Task task = allTasks.get(id);
            allTasks.remove(id);
            tasksSortedByStartTime.remove(task);
            historyManager.remove(id);
        } else {
            throw new NotFoundException("Задачу с идентификатором " + id + " нельзя удалить, т.к. " +
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
                .filter(subTask -> subTask.getStartTime() != null && subTask.getDuration() != null)
                .map(SubTask::getEndTime)
                .max(Comparator.comparing(epoch -> epoch.toEpochSecond(ZoneOffset.UTC)));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return tasksSortedByStartTime;
    }

    @Override
    public boolean isTasksIntersected(Task task) {
        TreeSet<Task> sortedTasks = getPrioritizedTasks();
        if (!sortedTasks.isEmpty()) {
            return getPrioritizedTasks().stream().anyMatch(sortedTask ->
                    (sortedTask.getEndTime().isAfter(task.getStartTime()) && task.getEndTime().isAfter(sortedTask.getEndTime()))
                            || (task.getStartTime().isEqual(sortedTask.getStartTime()) && task.getEndTime().isEqual(sortedTask.getEndTime())));

        } else {
            return false;
        }
    }
}
