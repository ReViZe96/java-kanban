package managers;

import managers.exceptions.ManagerSaveException;
import managers.services.CSVFormat;
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

    public void save() {
        Map<Task, TaskType> tasksOfAllType = new TreeMap<>();

        Set<Map.Entry<Integer, Task>> allTaskEntrySet = InMemoryTaskManager.allTasks.entrySet();
        Set<Map.Entry<Integer, Epic>> allEpicsEntrySet = InMemoryTaskManager.allEpics.entrySet();
        Set<Map.Entry<Integer, SubTask>> allSubTasksEntrySet = InMemoryTaskManager.allSubtasks.entrySet();

        for (Map.Entry<Integer, Task> allTaskEntry : allTaskEntrySet) {
            tasksOfAllType.put(allTaskEntry.getValue(), allTaskEntry.getValue().getType());
        }
        for (Map.Entry<Integer, Epic> allEpicsEntry : allEpicsEntrySet) {
            tasksOfAllType.put(allEpicsEntry.getValue(), allEpicsEntry.getValue().getType());
        }
        for (Map.Entry<Integer, SubTask> allSubTasksEntry : allSubTasksEntrySet) {
            tasksOfAllType.put(allSubTasksEntry.getValue(), allSubTasksEntry.getValue().getType());
        }

        String[] tasksInfo = new String[tasksOfAllType.size()];
        int indexOfTask = 0;
        Set<Map.Entry<Task, TaskType>> tasksOfAllTypeEntrySet = tasksOfAllType.entrySet();
        for (Map.Entry<Task, TaskType> taskEntry : tasksOfAllTypeEntrySet) {
            tasksInfo[indexOfTask] = CSVFormat.toString(taskEntry.getKey(), taskEntry.getValue());
            indexOfTask++;
        }

        try (FileWriter fileWriter = new FileWriter(tasksFile); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            String header = "id,type,name,status,description,startTime,duration,epic,subtasks\n";
            bufferedWriter.write(header);
            for (String taskInfo : tasksInfo) {
                bufferedWriter.write(taskInfo + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить информацию о задачах в файл " + tasksFile);
        }
    }

}
