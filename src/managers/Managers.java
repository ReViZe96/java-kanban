package managers;

import managers.exceptions.ManagerLoadException;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import managers.services.CSVFormat;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import java.io.*;
import java.util.*;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        file = file.toPath().toAbsolutePath().toFile();

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        Map<Task, TaskType> taskWithType = new HashMap<>();

        Map<String, List<String>> loadedTasksInfo = getLoadedTasksInfo(file);

        try (FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while (bufferedReader.ready()) {
                String currentLine = bufferedReader.readLine();
                if (!currentLine.equals("id,type,name,status,description,startTime,duration,epic,subtasks")) {
                    taskWithType.putAll(CSVFormat.fromString(currentLine, loadedTasksInfo));
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Не удалось прочитать информацию о задачах из файла " + file);
        }

        if (!taskWithType.isEmpty()) {
            Set<Map.Entry<Task, TaskType>> taskWithTypeEntrySet = taskWithType.entrySet();
            for (Map.Entry<Task, TaskType> taskWithTypeEntry : taskWithTypeEntrySet) {
                if (TaskType.TASK.equals(taskWithTypeEntry.getValue())) {
                    getDefault().addTask(taskWithTypeEntry.getKey());
                } else if (TaskType.EPIC.equals(taskWithTypeEntry.getValue())) {
                    Epic epic = (Epic) taskWithTypeEntry.getKey();

                    boolean isSubTasksExist = true;
                    for (SubTask subTask : epic.getSubtasks()) {
                        if (getDefault().getSubTaskById(subTask.getId()) == null) {
                            isSubTasksExist = false;
                            break;
                        }
                    }

                    if ((getDefault().getEpicById(epic.getId()) == null)) {
                        if (!isSubTasksExist) {
                            Epic epicWithoutTasks = new Epic(epic.getName(), epic.getDescription());
                            epicWithoutTasks.setId(epic.getId());
                            getDefault().addEpic(epicWithoutTasks);

                            for (SubTask subTask : epic.getSubtasks()) {
                                getDefault().addSubTask(subTask);
                            }
                            getDefault().updateEpic(epic);
                        } else {
                            getDefault().addEpic(epic);
                        }
                    }
                } else {
                    SubTask subTask = (SubTask) taskWithTypeEntry.getKey();
                    if (getDefault().getSubTaskById(subTask.getId()) == null) {
                        if (getDefault().getEpicById(subTask.getEpic().getId()) == null) {
                            SubTask subTaskWithoutEpic = new SubTask(subTask.getName(), subTask.getDescription());
                            subTask.setId(subTask.getId());
                            getDefault().addSubTask(subTaskWithoutEpic);
                        }
                    }
                }
            }
        }
        return fileBackedTaskManager;
    }

    private static Map<String, List<String>> getLoadedTasksInfo(File file) {

        Map<String, List<String>> loadedTaskInfo = new HashMap<>();

        try (FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while (bufferedReader.ready()) {
                String currentLine = bufferedReader.readLine();
                if (!currentLine.equals("id,type,name,status,description,startTime,duration,epic,subtasks")) {
                    List<String> taskFields = new ArrayList<>();
                    String[] fieldInfo = currentLine.split(",");
                    taskFields.add(fieldInfo[2]); //name
                    taskFields.add(fieldInfo[3]); //status
                    taskFields.add(fieldInfo[4]); //description
                    taskFields.add(fieldInfo[5]); //startTime
                    taskFields.add(fieldInfo[6]); //duration
                    loadedTaskInfo.put(fieldInfo[0], taskFields);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Не удалось прочитать информацию о задачах из файла " + file);
        }

        return loadedTaskInfo;
    }

}
