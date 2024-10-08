package managers;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.*;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class FileBackedTaskManagerTest {

    public static FileBackedTaskManager fileBackedTaskManager;
    public static TaskManager inMemoryTaskManager = Managers.getDefault();

    public final String testSaveFile = "resources/testTasksFileSave.csv";
    public final String testLoadFile = "resources/testTasksFileLoad.csv";


    @BeforeEach
    public void resetIdCounter() {
        InMemoryTaskManager.idCounter = 0;
    }

    @AfterEach
    public void cleanTestFileAndRemoveAllTasks() throws IOException {
        File file = new File(testSaveFile);
        try (FileWriter clearFileWriter = new FileWriter(file, false);
             PrintWriter clearPrintWriter = new PrintWriter(clearFileWriter, false)) {
            clearPrintWriter.flush();
        }

        inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.removeAllTasks();
        inMemoryTaskManager.removeAllEpics();
        inMemoryTaskManager.removeAllSubtasks();

    }

    @Test
    public void loadEmptyFileTest() {
        fileBackedTaskManager = Managers.loadFromFile(new File(testSaveFile));

        Assertions.assertTrue(fileBackedTaskManager.getAllTasks().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllEpics().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllSubTasks().isEmpty());
    }

    @Test
    public void saveInFileSomeTasksTest() throws IOException {
        fileBackedTaskManager = Managers.loadFromFile(new File(testSaveFile));

        Assertions.assertTrue(fileBackedTaskManager.getAllTasks().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllEpics().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllSubTasks().isEmpty());

        Task task = new Task("task1", "descriptionOfTask1", Duration.ofSeconds(1200),
                LocalDateTime.of(1999, 1, 1, 1, 0, 0));
        fileBackedTaskManager.addTask(task);

        ArrayList<SubTask> subtasks = new ArrayList<>();

        SubTask firstSubTask = new SubTask("subtask1", "descriptionOfSubtask1", Duration.ofSeconds(100),
                LocalDateTime.of(2000, 2, 2, 2, 0, 0));
        fileBackedTaskManager.addSubTask(firstSubTask);
        subtasks.add(firstSubTask);

        SubTask secondSubTask = new SubTask("subtask2", "descriptionOfSubtask2", Duration.ofSeconds(200),
                LocalDateTime.of(2001, 3, 3, 3, 0, 0));
        subtasks.add(secondSubTask);
        fileBackedTaskManager.addSubTask(secondSubTask);

        SubTask thirdSubTask = new SubTask("subtask3", "descriptionOfSubtask3", Duration.ofSeconds(300),
                LocalDateTime.of(2002, 4, 4, 4, 0, 0));
        subtasks.add(thirdSubTask);
        fileBackedTaskManager.addSubTask(thirdSubTask);

        Epic epic = new Epic("epic1", "descriptionOfEpic1");
        epic.setSubtasks(subtasks);
        fileBackedTaskManager.addEpic(epic);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(testSaveFile));
        StringBuilder fileContentBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            fileContentBuilder.append(bufferedReader.readLine() + ";");
        }
        String fileContent = fileContentBuilder.deleteCharAt(fileContentBuilder.length() - 1).toString();

        String[] fileLines = fileContent.split(";");
        Assertions.assertEquals("1,TASK,task1,NEW,descriptionOfTask1,1999-01-01T01:00,1200,,", fileLines[1]); //task1
        Assertions.assertEquals("2,SUBTASK,subtask1,NEW,descriptionOfSubtask1,2000-02-02T02:00,100,5,", fileLines[2]); //subtask1
        Assertions.assertEquals("3,SUBTASK,subtask2,NEW,descriptionOfSubtask2,2001-03-03T03:00,200,5,", fileLines[3]); //subtask2
        Assertions.assertEquals("4,SUBTASK,subtask3,NEW,descriptionOfSubtask3,2002-04-04T04:00,300,5,", fileLines[4]); //subtask3
        Assertions.assertEquals("5,EPIC,epic1,NEW,descriptionOfEpic1,2000-02-02T02:00,600,,2 3 4", fileLines[5]); //epic1

    }

    @Test
    public void loadFromFileWithSomeTasks() {

        Task task = new Task("task1", "descriptionOfTask1", Duration.ofSeconds(1200),
                LocalDateTime.of(1999, 1, 1, 1, 0, 0));
        task.setId(1);
        task.setStatus(TaskStatus.NEW);

        Epic epic = new Epic("epic1", "descriptionOfEpic1");
        ArrayList<SubTask> subtasks = new ArrayList<>();

        SubTask firstSubTask = new SubTask("subtask1", "descriptionOfSubtask1", Duration.ofSeconds(100),
                LocalDateTime.of(2000, 2, 2, 2, 0, 0),
                epic);
        firstSubTask.setId(2);
        firstSubTask.setStatus(TaskStatus.NEW);
        subtasks.add(firstSubTask);

        SubTask secondSubTask = new SubTask("subtask2", "descriptionOfSubtask2", Duration.ofSeconds(200),
                LocalDateTime.of(2001, 3, 3, 3, 0, 0),
                epic);
        secondSubTask.setId(3);
        secondSubTask.setStatus(TaskStatus.NEW);
        subtasks.add(secondSubTask);

        SubTask thirdSubTask = new SubTask("subtask3", "descriptionOfSubtask3", Duration.ofSeconds(300),
                LocalDateTime.of(2002, 4, 4, 4, 0, 0),
                epic);
        thirdSubTask.setId(4);
        thirdSubTask.setStatus(TaskStatus.NEW);
        subtasks.add(thirdSubTask);

        epic.setSubtasks(subtasks);
        epic.setId(5);
        epic.setStatus(TaskStatus.NEW);

        fileBackedTaskManager = Managers.loadFromFile(new File(testLoadFile));
        HashMap<Integer, Task> allTasks = fileBackedTaskManager.getAllTypeTask();

        Assertions.assertTrue(allTasks.containsValue(task));
        Assertions.assertTrue(allTasks.containsValue(firstSubTask));
        Assertions.assertTrue(allTasks.containsValue(secondSubTask));
        Assertions.assertTrue(allTasks.containsValue(thirdSubTask));
        Assertions.assertTrue(allTasks.containsValue(epic));

    }

}
