package managers;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class TaskManagerTest<T extends TaskManager> {

    private InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("resources/testTasksFileSave.csv"));
    private T taskManager = (T) Managers.getDefault();

    @BeforeEach
    public void resetIdCounter() {
        InMemoryTaskManager.idCounter = 0;
    }

    //Перед каждым тестом создается: 2 задачи и 6 подзадач, распределенных между 3 эпиками
    @BeforeEach
    public void addTestData() {
        Task awakening = new Task("Проснуться", "Необходимо проснуться в 8:00",
                Duration.ofSeconds(120), LocalDateTime.of(2024, 8, 7, 8, 0, 0));
        Task sleeping = new Task("Заснуть", "Постараться заснуть раньше 1:00",
                Duration.ofSeconds(360), LocalDateTime.of(2024, 8, 8, 1, 0, 0));
        taskManager.addTask(awakening);
        taskManager.addTask(sleeping);

        Epic fitness = new Epic("Заниматься спортом", "Хотя бы раз в день");
        taskManager.addEpic(fitness);

        SubTask pullUps = new SubTask("Подтягивания на перекладине", "Подтянуться 10 раз",
                Duration.ofSeconds(180), LocalDateTime.of(2024, 8, 10, 10, 0, 0),
                fitness);
        SubTask pushUps = new SubTask("Отжиматься от пола", "Минимум 3 раза в день",
                Duration.ofSeconds(900), LocalDateTime.of(2024, 8, 10, 12, 0, 0),
                fitness);
        SubTask benchPress = new SubTask("Жим гантели лёжа", "Два и более раза за день",
                Duration.ofSeconds(1000), LocalDateTime.of(2024, 8, 10, 18, 0, 0),
                fitness);
        taskManager.addSubTask(pullUps);
        taskManager.addSubTask(pushUps);
        taskManager.addSubTask(benchPress);

        Epic developingOfTracker = new Epic("Разработать трекер задач", "Создать работающее приложение");
        taskManager.addEpic(developingOfTracker);

        SubTask makingCrutch = new SubTask("Создать основу приложения", "Покостылить",
                Duration.ofDays(31), LocalDateTime.of(2024, 9, 1, 9, 0, 0),
                developingOfTracker);
        SubTask refactoring = new SubTask("Осуществить рефакторинг кода", "Сделать все красиво",
                Duration.ofDays(62), LocalDateTime.of(2024, 10, 14, 9, 0, 0),
                developingOfTracker);
        taskManager.addSubTask(makingCrutch);
        taskManager.addSubTask(refactoring);

        Epic nothing = new Epic("Ничего не делать", "Только не забывать дышать");
        taskManager.addEpic(nothing);

        SubTask breathing = new SubTask("Дышать", "Размеренно и спокойно",
                Duration.ofDays(31 * 12 * 70), LocalDateTime.of(2026, 5, 10, 15, 17, 0),
                nothing);
        taskManager.addSubTask(breathing);

    }

    @AfterEach
    public void removeAllTasks() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
    }


    //TaskManager
    @Test
    public void shouldGetAllTypeTask() {
        HashMap<Integer, Task> allTypeTask = taskManager.getAllTypeTask();
        Assertions.assertEquals(11, allTypeTask.values().size());
    }

    @Test
    public void shouldGetAllEpics() {
        Collection<Epic> allEpics = taskManager.getAllEpics();
        Assertions.assertEquals(3, allEpics.size());
    }

    @Test
    public void shouldGetAllSubTasks() {
        Collection<SubTask> allSubtasks = taskManager.getAllSubTasks();
        Assertions.assertEquals(6, allSubtasks.size());
    }

    @Test
    public void shouldGetAllTasks() {
        Collection<Task> allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(2, allTasks.size());
    }

    @Test
    public void shouldRemoveAllEpics() {
        Collection<Epic> allEpicsBefore = taskManager.getAllEpics();
        Assertions.assertEquals(3, allEpicsBefore.size());
        taskManager.removeAllEpics();
        Collection<Epic> allEpicsAfter = taskManager.getAllEpics();
        Assertions.assertTrue(allEpicsAfter.isEmpty());
    }

    @Test
    public void shouldRemoveAllSubtasks() {
        Collection<SubTask> allSubTasksBefore = taskManager.getAllSubTasks();
        Assertions.assertEquals(6, allSubTasksBefore.size());
        taskManager.removeAllSubtasks();
        Collection<SubTask> allSubTasksAfter = taskManager.getAllSubTasks();
        Assertions.assertTrue(allSubTasksAfter.isEmpty());
    }

    @Test
    public void shouldRemoveAllTasks() {
        Collection<Task> allTasksBefore = taskManager.getAllTasks();
        Assertions.assertEquals(2, allTasksBefore.size());
        taskManager.removeAllTasks();
        Collection<Task> allTasksAfter = taskManager.getAllTasks();
        Assertions.assertTrue(allTasksAfter.isEmpty());
    }

    @Test
    public void shouldGetEpicById() {
        Epic fitness = taskManager.getEpicById(3);
        List<SubTask> fitnesSubtasks = new ArrayList<>();
        fitnesSubtasks.addAll(fitness.getSubtasks());

        Assertions.assertEquals("Заниматься спортом", fitness.getName());
        Assertions.assertEquals("Хотя бы раз в день", fitness.getDescription());
        Assertions.assertEquals(3, fitnesSubtasks.size());
        Assertions.assertEquals("Подтягивания на перекладине", fitnesSubtasks.get(0).getName());
        Assertions.assertEquals("Отжиматься от пола", fitnesSubtasks.get(1).getName());
        Assertions.assertEquals("Жим гантели лёжа", fitnesSubtasks.get(2).getName());
    }

    @Test
    public void shouldGetSubTaskById() {
        SubTask makingCrutch = taskManager.getSubTaskById(8);
        Assertions.assertEquals("Создать основу приложения", makingCrutch.getName());
        Assertions.assertEquals("Покостылить", makingCrutch.getDescription());
        Assertions.assertNotNull(makingCrutch.getEpic());
        Assertions.assertEquals("Разработать трекер задач", makingCrutch.getEpic().getName());
    }

    @Test
    public void shouldGetTaskById() {
        Task sleeping = taskManager.getTaskById(2);
        Assertions.assertEquals("Заснуть", sleeping.getName());
        Assertions.assertEquals("Постараться заснуть раньше 1:00", sleeping.getDescription());
        Assertions.assertEquals(Duration.ofSeconds(360), sleeping.getDuration());
        Assertions.assertEquals(LocalDateTime.of(2024, 8, 8, 1, 0, 0),
                sleeping.getStartTime());
    }

    @Test
    public void shouldAddEpic() {
        Epic epicOutOfSystem = new Epic("Новый эпик", "Добавляемый эпик");
        ArrayList<SubTask> newEpicSubtasks = new ArrayList<>();
        SubTask firstSubtaskOut = new SubTask("Новая подзадача эпика 1", "Первая подзадача", Duration.ofSeconds(20),
                LocalDateTime.of(2024, 8, 9, 18, 30), epicOutOfSystem);
        firstSubtaskOut.setStatus(TaskStatus.NEW);
        newEpicSubtasks.add(firstSubtaskOut);
        SubTask secondSubTaskOut = new SubTask("Новая подзадача эпика 2", "Вторая подзадача",
                Duration.ofSeconds(40), LocalDateTime.of(2024, 8, 9, 18, 41),
                epicOutOfSystem);
        secondSubTaskOut.setStatus(TaskStatus.NEW);
        newEpicSubtasks.add(secondSubTaskOut);
        epicOutOfSystem.setSubtasks(newEpicSubtasks);

        taskManager.addEpic(epicOutOfSystem);
        int idAfterUpdated = epicOutOfSystem.getId();

        Epic epicFromSystem = taskManager.getEpicById(idAfterUpdated);

        ArrayList<SubTask> epicFromSystemSubtasks = epicFromSystem.getSubtasks();

        Assertions.assertEquals(epicOutOfSystem.getName(), epicFromSystem.getName());
        Assertions.assertEquals(epicOutOfSystem.getDescription(), epicFromSystem.getDescription());
        Assertions.assertEquals(TaskStatus.NEW, epicFromSystem.getStatus());
        Assertions.assertEquals(epicOutOfSystem.getSubtasks().size(), epicFromSystem.getSubtasks().size());
        Assertions.assertEquals(firstSubtaskOut, epicFromSystemSubtasks.get(0));
        Assertions.assertEquals(secondSubTaskOut, epicFromSystemSubtasks.get(1));
        Assertions.assertEquals(firstSubtaskOut.getDuration().plus(secondSubTaskOut.getDuration()), epicFromSystem.getDuration());
        Assertions.assertEquals(firstSubtaskOut.getStartTime(), epicFromSystem.getStartTime());

    }

    @Test
    public void shouldAddSubTask() {
        Epic epicOutOfSystem = new Epic("Новый эпик", "Добавляемый эпик");
        epicOutOfSystem.setId(10);
        SubTask subtaskOutOfSystem = new SubTask("Новая подзадача", "Подзадача", Duration.ofSeconds(120),
                LocalDateTime.of(2224, 8, 9, 18, 54), epicOutOfSystem);
        subtaskOutOfSystem.setStatus(TaskStatus.NEW);
        taskManager.addSubTask(subtaskOutOfSystem);
        int idAfterUpdated = subtaskOutOfSystem.getId();

        SubTask subtaskFromSystem = taskManager.getSubTaskById(idAfterUpdated);
        Assertions.assertEquals(subtaskOutOfSystem.getName(), subtaskFromSystem.getName());
        Assertions.assertEquals(subtaskOutOfSystem.getDescription(), subtaskFromSystem.getDescription());
        Assertions.assertEquals(TaskStatus.NEW, subtaskFromSystem.getStatus());
        Assertions.assertEquals(subtaskOutOfSystem.getEpic(), subtaskFromSystem.getEpic());
        Assertions.assertEquals(subtaskOutOfSystem.getDuration(), subtaskFromSystem.getDuration());
        Assertions.assertEquals(subtaskOutOfSystem.getStartTime(), subtaskFromSystem.getStartTime());
        Assertions.assertEquals(subtaskOutOfSystem.getStartTime().plus(subtaskOutOfSystem.getDuration()),
                subtaskFromSystem.getEndTime());

    }

    @Test
    public void shouldAddTask() {
        Task taskOutOfSystem = new Task("Новая задача", "Добавляемая задача", Duration.ofSeconds(220),
                LocalDateTime.of(2324, 8, 9, 19, 9));

        taskManager.addTask(taskOutOfSystem);
        int idAfterUpdated = taskOutOfSystem.getId();

        Task taskFromSystem = taskManager.getTaskById(idAfterUpdated);

        Assertions.assertEquals(taskOutOfSystem.getName(), taskFromSystem.getName());
        Assertions.assertEquals(taskOutOfSystem.getDescription(), taskFromSystem.getDescription());
        Assertions.assertEquals(TaskStatus.NEW, taskFromSystem.getStatus());
        Assertions.assertEquals(taskOutOfSystem.getDuration(), taskFromSystem.getDuration());
        Assertions.assertEquals(taskOutOfSystem.getStartTime(), taskFromSystem.getStartTime());
        Assertions.assertEquals(taskOutOfSystem.getStartTime().plus(taskOutOfSystem.getDuration()), taskFromSystem.getEndTime());

    }

    @Test
    public void shouldUpdateEpic() {
        Epic nothing = taskManager.getEpicById(10);
        //эпик содержит только одну подзадачу
        Duration expectedNothingDuration = nothing.getSubtasks().get(0).getDuration();
        LocalDateTime expectedNothingStartTime = nothing.getSubtasks().get(0).getStartTime();
        Assertions.assertEquals("Ничего не делать", nothing.getName());
        Assertions.assertEquals("Только не забывать дышать", nothing.getDescription());
        Assertions.assertEquals(1, nothing.getSubtasks().size());
        Assertions.assertEquals("Дышать", nothing.getSubtasks().get(0).getName());
        Assertions.assertEquals("Размеренно и спокойно", nothing.getSubtasks().get(0).getDescription());
        Assertions.assertEquals(expectedNothingDuration, nothing.getDuration());
        Assertions.assertEquals(expectedNothingStartTime, nothing.getStartTime());
        Assertions.assertEquals(expectedNothingStartTime.plus(expectedNothingDuration), nothing.getEndTime());

        SubTask eating = new SubTask("Кушать", "Только после того, как поспал",
                Duration.ofHours(1), LocalDateTime.of(2096, 6, 11, 16, 18));
        eating.setStatus(TaskStatus.NEW);
        ArrayList<SubTask> modifiedSubtasksList = nothing.getSubtasks();
        modifiedSubtasksList.add(eating);
        Epic modifiedNothingOutOfSystem = new Epic(nothing.getName(), "Новое описание", modifiedSubtasksList);
        modifiedNothingOutOfSystem.setId(nothing.getId());
        Assertions.assertEquals(modifiedNothingOutOfSystem.getId(), nothing.getId());

        taskManager.updateEpic(modifiedNothingOutOfSystem);
        int idAfterUpdated = modifiedNothingOutOfSystem.getId();

        Epic modifiedNothingFromSystem = taskManager.getEpicById(idAfterUpdated);
        Assertions.assertEquals(nothing.getName(), modifiedNothingFromSystem.getName());
        Assertions.assertEquals("Новое описание", modifiedNothingFromSystem.getDescription());
        Assertions.assertEquals(modifiedSubtasksList.size(), modifiedNothingFromSystem.getSubtasks().size());
        Assertions.assertEquals(modifiedSubtasksList.get(0).getDuration().plus(modifiedSubtasksList.get(1).getDuration()),
                modifiedNothingFromSystem.getDuration());
        Assertions.assertEquals(modifiedSubtasksList.get(0).getStartTime(), modifiedNothingFromSystem.getStartTime());

    }

    @Test
    public void shouldUpdateSubTask() {
        SubTask refactoring = taskManager.getSubTaskById(9);
        Epic developingOfTracker = taskManager.getEpicById(7);
        LocalDateTime expectedRefactoringStartTime = LocalDateTime.of(2024, 10, 14, 9, 0);
        Duration expectedRefactoringDuration = Duration.ofDays(62);
        Assertions.assertEquals("Осуществить рефакторинг кода", refactoring.getName());
        Assertions.assertEquals("Сделать все красиво", refactoring.getDescription());
        Assertions.assertEquals(developingOfTracker, refactoring.getEpic());
        Assertions.assertEquals(expectedRefactoringDuration, refactoring.getDuration());
        Assertions.assertEquals(expectedRefactoringStartTime, refactoring.getStartTime());
        Assertions.assertEquals(expectedRefactoringStartTime.plus(expectedRefactoringDuration), refactoring.getEndTime());

        Duration expectedUpdatedRefactoringDuration = Duration.ofDays(30);
        LocalDateTime expectedUpdatedRefactorngStartTime = LocalDateTime.of(2125, 1, 11, 9, 0);
        SubTask modifiedRefactoringOutOfSystem = new SubTask("Осуществить рефакторинг результатов рефакторинга",
                refactoring.getDescription(), expectedUpdatedRefactoringDuration, expectedUpdatedRefactorngStartTime,
                refactoring.getEpic());
        modifiedRefactoringOutOfSystem.setId(refactoring.getId());
        modifiedRefactoringOutOfSystem.setStatus(TaskStatus.NEW);
        Assertions.assertEquals(modifiedRefactoringOutOfSystem.getId(), refactoring.getId());

        taskManager.updateSubTask(modifiedRefactoringOutOfSystem);

        SubTask modifiedRefactorFromSystem = taskManager.getSubTaskById(refactoring.getId());
        Assertions.assertEquals(modifiedRefactoringOutOfSystem.getName(), modifiedRefactorFromSystem.getName());
        Assertions.assertEquals(modifiedRefactoringOutOfSystem.getDescription(), modifiedRefactorFromSystem.getDescription());
        Assertions.assertEquals(modifiedRefactoringOutOfSystem.getEpic(), modifiedRefactorFromSystem.getEpic());
        Assertions.assertEquals(modifiedRefactoringOutOfSystem.getDuration(), modifiedRefactorFromSystem.getDuration());
        Assertions.assertEquals(modifiedRefactoringOutOfSystem.getStartTime(), modifiedRefactorFromSystem.getStartTime());
        Assertions.assertEquals(expectedUpdatedRefactorngStartTime.plus(expectedUpdatedRefactoringDuration),
                modifiedRefactorFromSystem.getEndTime());
        Assertions.assertNotEquals(refactoring.getEndTime(), modifiedRefactorFromSystem.getEndTime());

    }

    @Test
    public void shouldUpdateTask() {
        Task awakening = taskManager.getTaskById(1);
        Duration expectedAwakeningDuration = Duration.ofSeconds(120);
        LocalDateTime expectedAwakeningStartTime = LocalDateTime.of(2024, 8, 7, 8, 0);
        Assertions.assertEquals("Проснуться", awakening.getName());
        Assertions.assertEquals("Необходимо проснуться в 8:00", awakening.getDescription());
        Assertions.assertEquals(expectedAwakeningDuration, awakening.getDuration());
        Assertions.assertEquals(expectedAwakeningStartTime, awakening.getStartTime());
        Assertions.assertEquals(expectedAwakeningStartTime.plus(expectedAwakeningDuration), awakening.getEndTime());
        Assertions.assertEquals(expectedAwakeningStartTime.plus(expectedAwakeningDuration), awakening.getEndTime());

        Duration expectedUpdatedAwakeningDuration = Duration.ofSeconds(10);
        LocalDateTime expectedUpdateAwakeningStartTime = LocalDateTime.of(2424, 8, 7, 7, 58);
        Task modifiedAwakeningOutOfSystem = new Task("Проспать", "Выключить будильник на 8:00",
                expectedUpdatedAwakeningDuration, expectedUpdateAwakeningStartTime);
        modifiedAwakeningOutOfSystem.setId(awakening.getId());
        modifiedAwakeningOutOfSystem.setStatus(TaskStatus.NEW);
        Assertions.assertEquals(modifiedAwakeningOutOfSystem.getId(), awakening.getId());

        taskManager.updateTask(modifiedAwakeningOutOfSystem);

        Task modifiedAwakeningFromSystem = taskManager.getTaskById(awakening.getId());
        Assertions.assertEquals("Проспать", modifiedAwakeningFromSystem.getName());
        Assertions.assertEquals("Выключить будильник на 8:00", modifiedAwakeningFromSystem.getDescription());
        Assertions.assertEquals(expectedUpdatedAwakeningDuration, modifiedAwakeningFromSystem.getDuration());
        Assertions.assertEquals(expectedUpdateAwakeningStartTime, modifiedAwakeningFromSystem.getStartTime());
        Assertions.assertEquals(expectedUpdateAwakeningStartTime.plus(expectedUpdatedAwakeningDuration),
                modifiedAwakeningFromSystem.getEndTime());
        Assertions.assertNotEquals(awakening.getEndTime(), modifiedAwakeningFromSystem.getEndTime());

    }

    @Test
    public void shouldRemoveEpicById() {
        Epic developingOfTracker = taskManager.getEpicById(7);
        Collection<Epic> allEpicsBefore = taskManager.getAllEpics();
        Assertions.assertTrue(allEpicsBefore.contains(developingOfTracker));

        taskManager.removeEpicById(7);

        Collection<Epic> allEpicsAfter = taskManager.getAllEpics();
        Assertions.assertFalse(allEpicsAfter.contains(developingOfTracker));

    }

    @Test
    public void shouldRemoveSubTaskById() {
        SubTask benchPress = taskManager.getSubTaskById(6);
        Collection<SubTask> allSubTasksBefore = taskManager.getAllSubTasks();
        Assertions.assertTrue(allSubTasksBefore.contains(benchPress));

        taskManager.removeSubTaskById(6);

        Collection<SubTask> allSubTasksAfter = taskManager.getAllSubTasks();
        Assertions.assertFalse(allSubTasksAfter.contains(benchPress));

    }

    @Test
    public void shouldRemoveTaskById() {
        Task sleeping = taskManager.getTaskById(2);
        Collection<Task> allTasksBefore = taskManager.getAllTasks();
        Assertions.assertTrue(allTasksBefore.contains(sleeping));

        taskManager.removeTaskById(2);

        Collection<Task> allTasksAfter = taskManager.getAllTasks();
        Assertions.assertFalse(allTasksAfter.contains(sleeping));

    }

    @Test
    public void shouldGetPrioritizedTasks() {

        Task awakening = taskManager.getTaskById(1);
        LocalDateTime awakeningStartTime = awakening.getStartTime();
        SubTask breathing = taskManager.getSubTaskById(11);
        LocalDateTime breathingStartTiime = breathing.getStartTime();

        TreeSet<Task> tasksSortedByStartTime = taskManager.getPrioritizedTasks();

        Assertions.assertEquals(8, tasksSortedByStartTime.size());
        Assertions.assertEquals(awakening, tasksSortedByStartTime.first());
        Assertions.assertEquals(breathing, tasksSortedByStartTime.last());
        Assertions.assertTrue(awakeningStartTime.isBefore(breathingStartTiime));

    }

    //InMemoryTaskManager
    @Test
    public void shouldCalculateStatus () {
        ArrayList<SubTask> subtasks = new ArrayList<>();
        SubTask firstSubtask = new SubTask("firstSubtask", "first");
        firstSubtask.setStatus(TaskStatus.NEW);
        SubTask secondSubtask = new SubTask("secondSubtask", "second");
        secondSubtask.setStatus(TaskStatus.IN_PROGRESS);
        SubTask thirdSubtask = new SubTask("thirdSubtask", "third");
        thirdSubtask.setStatus(TaskStatus.DONE);

        subtasks.add(firstSubtask);
        subtasks.add(secondSubtask);
        subtasks.add(thirdSubtask);

        TaskStatus taskStatus = inMemoryTaskManager.calculateStatus(subtasks);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskStatus);

    }

    @Test
    public void shouldCalculateEpicStartTime () {
        ArrayList<SubTask> subtasks = new ArrayList<>(taskManager.getAllSubTasks());
        //pullUps           --->    startTime = 10-08-2024 10:00
        //pushUps           --->    startTime = 10-08-2024 12:00
        //benchPress        --->    startTime = 10-08-2024 18:00
        //makingCrutch      --->    startTime = 1-09-2024
        //refactoring       --->    startTime = 14-10-2024
        //breathing         --->    startTime = 10-05-2026
        LocalDateTime startTime = inMemoryTaskManager.calculateEpicStartTime(subtasks).get();
        Assertions.assertEquals(LocalDateTime.of(2024, 8, 10, 10, 0), startTime);
    }

    @Test
    public void shouldCalculateEpicDuration () {
        ArrayList<SubTask> subtasks = new ArrayList<>(taskManager.getAllSubTasks());
        //pullUps           --->    duration = 180
        //pushUps           --->    duration = 900
        //benchPress        --->    duration = 1000
        //makingCrutch      --->    duration = 2678400
        //refactoring       --->    duration = 5356800
        //breathing         --->    duration = 2249856000
        //TOTAL             --->    duration = 2257893280
        Duration duration = inMemoryTaskManager.calculateEpicDuration(subtasks);
        Assertions.assertEquals(Duration.ofSeconds(2257893280L), duration);
    }

    @Test
    public void shouldCalculateEpicEndTime () {
        ArrayList<SubTask> subtasks = new ArrayList<>(taskManager.getAllSubTasks());
        //pullUps           --->    startTime = 10-08-2024 10:00    duration = 180
        //pushUps           --->    startTime = 10-08-2024 12:00    duration = 900
        //benchPress        --->    startTime = 10-08-2024 18:00    duration = 1000
        //makingCrutch      --->    startTime = 1-09-2024           duration = 2678400
        //refactoring       --->    startTime = 14-10-2024          duration = 5356800
        //breathing         --->    startTime = 10-05-2026          duration = 2249856000   ---> endTime = 25-08-2097 15:17
        LocalDateTime endTime = inMemoryTaskManager.calculateEpicEndTime(subtasks).get();
        Assertions.assertEquals(LocalDateTime.of(2097, 8,25, 15, 17), endTime);
    }

    @Test
    public void isTasksIntersectedTest () {
        Task firstTask = new Task("firstTask", "firstDescription", Duration.ofSeconds(100),
                LocalDateTime.of(2000, 1,1,1,1));
        Task secondTask = new Task("secondTask", "secondDescription", Duration.ofSeconds(200),
                LocalDateTime.of(2000, 1,1,1,1));
        Task thirdTask = new Task("thirdTask", "thirdDescription", Duration.ofSeconds(300),
                LocalDateTime.of(2000, 1,1,2,1));
        Task fourthTask = new Task("fourthTask", "fourthDescription", Duration.ofSeconds(400),
                LocalDateTime.of(2000, 1,1,2,1));

        boolean isIntersectedOne = InMemoryTaskManager.isTasksIntersected(firstTask, secondTask);
        boolean isIntersectedSecond = InMemoryTaskManager.isTasksIntersected(thirdTask, fourthTask);
        boolean isIntersectedThird = InMemoryTaskManager.isTasksIntersected(firstTask, thirdTask);
        boolean isIntersectedFourth = InMemoryTaskManager.isTasksIntersected(secondTask, fourthTask);

        Assertions.assertTrue(isIntersectedOne);
        Assertions.assertTrue(isIntersectedSecond);
        Assertions.assertFalse(isIntersectedThird);
        Assertions.assertFalse(isIntersectedFourth);

    }

    //FileBackedTaskManager
    @Test
    public void shouldSave() throws IOException {
        fileBackedTaskManager.removeTaskById(1);

        BufferedReader bufferedReader = new BufferedReader(new FileReader("resources/testTasksFileSave.csv"));
        StringBuilder fileContentBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            fileContentBuilder.append(bufferedReader.readLine() + ";");
        }
        String fileContent = fileContentBuilder.deleteCharAt(fileContentBuilder.length() - 1).toString();

        String[] fileLines = fileContent.split(";");
        Assertions.assertEquals("2,TASK,Заснуть,NEW,Постараться заснуть раньше 1:00,2024-08-08T01:00,360,,", fileLines[1]);
        Assertions.assertEquals("3,EPIC,Заниматься спортом,NEW,Хотя бы раз в день,2024-08-10T10:00,2080,,4 5 6", fileLines[2]);
        Assertions.assertEquals("4,SUBTASK,Подтягивания на перекладине,NEW,Подтянуться 10 раз,2024-08-10T10:00,180,3,", fileLines[3]);
        Assertions.assertEquals("5,SUBTASK,Отжиматься от пола,NEW,Минимум 3 раза в день,2024-08-10T12:00,900,3,", fileLines[4]);
        Assertions.assertEquals("6,SUBTASK,Жим гантели лёжа,NEW,Два и более раза за день,2024-08-10T18:00,1000,3,", fileLines[5]);
        Assertions.assertEquals("7,EPIC,Разработать трекер задач,NEW,Создать работающее приложение,2024-09-01T09:00,8035200,,8 9", fileLines[6]);
        Assertions.assertEquals("8,SUBTASK,Создать основу приложения,NEW,Покостылить,2024-09-01T09:00,2678400,7,", fileLines[7]);
        Assertions.assertEquals("9,SUBTASK,Осуществить рефакторинг кода,NEW,Сделать все красиво,2024-10-14T09:00,5356800,7,", fileLines[8]);
        Assertions.assertEquals("10,EPIC,Ничего не делать,NEW,Только не забывать дышать,2026-05-10T15:17,2249856000,,11", fileLines[9]);
        Assertions.assertEquals("11,SUBTASK,Дышать,NEW,Размеренно и спокойно,2026-05-10T15:17,2249856000,10,", fileLines[10]);

    }
}
