package managers.services;

import managers.Managers;
import managers.exceptions.ManagerLoadException;
import managers.interfaces.TaskManager;
import tasks.*;

import java.util.*;

public class CSVFormat {

    private static TaskManager taskManager = Managers.getDefault();

    public static String toString(Task task, TaskType taskType) {
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

    public static Map<Task, TaskType> fromString(String value, Map<String, List<String>> loadedTasksInfo) {

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
                    subTask = taskManager.getSubTaskById(Integer.parseInt(subTaskId));
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
                epic = taskManager.getEpicById(Integer.parseInt(tasksField[5]));
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

    public static TaskStatus parseTaskType(String taskName, String taskType) {
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
