package managers;

import managers.exceptions.ManagerLoadException;
import managers.exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File tasksFile;

    public FileBackedTaskManager(File tasksFile) {
        this.tasksFile = tasksFile;
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        super.updateSubTask(updatedSubTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    private void save() {
        Map<Task, TaskType> tasksOfAllType = new TreeMap<>();

        Set<Map.Entry<Integer, Task>> allTaskEntrySet = InMemoryTaskManager.allTasks.entrySet();
        Set<Map.Entry<Integer, Epic>> allEpicsEntrySet = InMemoryTaskManager.allEpics.entrySet();
        Set<Map.Entry<Integer, SubTask>> allSubTasksEntrySet = InMemoryTaskManager.allSubtasks.entrySet();

        for (Map.Entry<Integer, Task> allTaskEntry : allTaskEntrySet) {
            tasksOfAllType.put(allTaskEntry.getValue(), TaskType.TASK);
        }
        for (Map.Entry<Integer, Epic> allEpicsEntry : allEpicsEntrySet) {
            tasksOfAllType.put(allEpicsEntry.getValue(), TaskType.EPIC);
        }
        for (Map.Entry<Integer, SubTask> allSubTasksEntry : allSubTasksEntrySet) {
            tasksOfAllType.put(allSubTasksEntry.getValue(), TaskType.SUBTASK);
        }

        String[] tasksInfo = new String[tasksOfAllType.size()];
        int indexOfTask = 0;
        Set<Map.Entry<Task, TaskType>> tasksOfAllTypeEntrySet = tasksOfAllType.entrySet();
        for (Map.Entry<Task, TaskType> taskEntry : tasksOfAllTypeEntrySet) {
            tasksInfo[indexOfTask] = toString(taskEntry.getKey(), taskEntry.getValue());
            indexOfTask++;
        }

        try (FileWriter fileWriter = new FileWriter(tasksFile); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            String header = "id,type,name,status,description,epic,subtasks\n";
            bufferedWriter.write(header);
            for (String taskInfo : tasksInfo) {
                bufferedWriter.write(taskInfo + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить информацию о задачах в файл " + tasksFile);
        }
    }

    private String toString(Task task, TaskType taskType) {
        String epicId = "";
        String subtasksId = "";
        if (TaskType.SUBTASK.equals(taskType)) {
            SubTask subTask = (SubTask) task;
            if (subTask.getEpic() != null) {
                epicId = "" + subTask.getEpic().getId();
            }
        }

        if (TaskType.EPIC.equals(taskType)) {
            Epic epic = (Epic) task;
            ArrayList<SubTask> subtasks = epic.getSubtasks();
            for (SubTask subTask : subtasks) {
                if (subTask.getEpic() != null) {
                    subtasksId += subTask.getId() + " ";
                }
            }
            subtasksId = subtasksId.trim();
        }

        return String.join(",", "" + task.getId(), taskType.toString(), task.getName(),
                task.getStatus().toString(), task.getDescription(), epicId, subtasksId);
    }

    public Map<Task, TaskType> fromString(String value, Map<String, List<String>> loadedTasksInfo) {

        Map<Task, TaskType> taskWithType = new HashMap<>();
        Task task;

        Set<Map.Entry<String, List<String>>> loadedTaskInfoEntrySet = loadedTasksInfo.entrySet();
        List<String> loadedTasksId = new ArrayList<>();
        for (Map.Entry<String, List<String>> loadedTaskInfo : loadedTaskInfoEntrySet) {
            loadedTasksId.add(loadedTaskInfo.getKey());
        }

        String[] tasksField = value.split(",");

        if (TaskType.TASK.toString().equals(tasksField[1])) {
            task = new Task(tasksField[2], tasksField[4]);
            task.setId(Integer.parseInt(tasksField[0]));
            task.setStatus(parseTaskType(tasksField[2], tasksField[3]));
            taskWithType.put(task, TaskType.TASK);

        } else if (TaskType.EPIC.toString().equals(tasksField[1])) {
            ArrayList<SubTask> subTasks = new ArrayList<>();
            String[] subTaskIds = tasksField[6].split(" ");
            for (String subTaskId : subTaskIds) {
                SubTask subTask;
                if (!loadedTasksId.contains(subTaskId)) {
                    subTask = getSubTaskById(Integer.parseInt(subTaskId));
                } else {
                    List<String> subTaskInfo = loadedTasksInfo.get(subTaskId);
                    subTask = new SubTask(subTaskInfo.get(0), subTaskInfo.get(2));
                    subTask.setId(Integer.parseInt(subTaskId));
                    subTask.setStatus(parseTaskType(subTaskInfo.get(0), subTaskInfo.get(1)));
                }
                subTasks.add(subTask);
            }
            task = new Epic(tasksField[2], tasksField[4], subTasks);
            task.setId(Integer.parseInt(tasksField[0]));
            task.setStatus(parseTaskType(tasksField[2], tasksField[3]));
            taskWithType.put(task, TaskType.EPIC);
        } else if (TaskType.SUBTASK.toString().equals(tasksField[1])) {
            Epic epic;
            if (!loadedTasksId.contains(tasksField[5])) {
                epic = getEpicById(Integer.parseInt(tasksField[5]));
            } else {
                List<String> epicTaskInfo = loadedTasksInfo.get(tasksField[5]);
                epic = new Epic(epicTaskInfo.get(0), epicTaskInfo.get(2));
                epic.setId(Integer.parseInt(tasksField[5]));
                epic.setStatus(parseTaskType(epicTaskInfo.get(0), epicTaskInfo.get(1)));
            }
            task = new SubTask(tasksField[2], tasksField[4], epic);
            task.setId(Integer.parseInt(tasksField[0]));
            task.setStatus(parseTaskType(tasksField[2], tasksField[3]));
            taskWithType.put(task, TaskType.SUBTASK);
        } else {
            throw new ManagerLoadException("Ошибка загрузки задач. У задачи " + tasksField[2] +
                    " указан несуществующий в системе тип - " + tasksField[1]);
        }
        return taskWithType;
    }

    private TaskStatus parseTaskType(String taskName, String taskType) {
        if (TaskStatus.NEW.toString().equals(taskType)) {
            return TaskStatus.NEW;
        } else if (TaskStatus.DONE.toString().equals(taskType)) {
            return TaskStatus.DONE;
        } else if (TaskStatus.IN_PROGRESS.toString().equals(taskType)) {
            return TaskStatus.IN_PROGRESS;
        } else {
            throw new ManagerLoadException("Ошибка загрузки задач. У задачи " + taskName +
                    " указан несуществующий в системе статус - " + taskType);
        }
    }

}
