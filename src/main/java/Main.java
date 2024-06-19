import java.util.ArrayList;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager tasksManager = new InMemoryTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistory();
        ArrayList<Task> last10viewedTasks;

        Task awakening = new Task("Проснуться", "Необходимо проснуться в 8:00");
        Task sleeping = new Task("Заснуть", "Постараться заснуть раньше 1:00");
        tasksManager.addTask(awakening);
        tasksManager.addTask(sleeping);
        System.out.println("Созданы задачи:");
        System.out.println(awakening);
        System.out.println(sleeping);

        Epic fitness = new Epic("Заниматься спортом", "Хотя бы раз в день");
        tasksManager.addEpic(fitness);
        System.out.println();
        System.out.println("Создан эпик:");
        System.out.println(fitness);
        SubTask pullUps = new SubTask("Подтягивания на перекладине", "Подтянуться 10 раз", fitness);
        SubTask pushUps = new SubTask("Отжиматься от пола", "Минимум 3 раза в день", fitness);
        SubTask benchPress = new SubTask("Жим гантели лёжа", "Два и более раза за день", fitness);
        tasksManager.addSubTask(pullUps);
        tasksManager.addSubTask(pushUps);
        tasksManager.addSubTask(benchPress);
        System.out.println();
        System.out.println("Созданы подзадачи:");
        System.out.println(pullUps);
        System.out.println(pushUps);
        System.out.println(benchPress);

        Epic developingOfTracker = new Epic("Разработать трекер задач", "Создать работающее приложение");
        tasksManager.addEpic(developingOfTracker);
        System.out.println();
        System.out.println("Создан эпик:");
        System.out.println(developingOfTracker);
        SubTask makingCrutch = new SubTask("Создать основу приложения", "Покостылить", developingOfTracker);
        SubTask refactoring = new SubTask("Осуществить рефакторинг кода", "Сделать все красиво", developingOfTracker);
        tasksManager.addSubTask(makingCrutch);
        tasksManager.addSubTask(refactoring);
        System.out.println();
        System.out.println("Созданы подзадачи:");
        System.out.println(makingCrutch);
        System.out.println(refactoring);

        Epic nothing = new Epic("Ничего не делать", "Только не забывать дышать");
        tasksManager.addEpic(nothing);
        System.out.println();
        System.out.println("Создан эпик:");
        System.out.println(nothing);
        SubTask breathing = new SubTask("Дышать", "Размеренно и спокойно", nothing);
        tasksManager.addSubTask(breathing);
        System.out.println();
        System.out.println("Создана подзадача:");
        System.out.println(breathing);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЗАДАЧ:");
        Collection<Task> allTasks = tasksManager.getAllTasks();
        printTasks(allTasks);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ПОДЗАДАЧ:");
        Collection<SubTask> allSubtasks = tasksManager.getAllSubTasks();
        printSubTasks(allSubtasks);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЭПИКОВ:");
        Collection<Epic> allEpics = tasksManager.getAllEpics();
        printEpics(allEpics);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);


        System.out.println();
        System.out.println("Обновление статусов задач...");
        awakening.setStatus(TaskStatus.DONE);
        tasksManager.updateTask(awakening);
        sleeping.setStatus(TaskStatus.IN_PROGRESS);
        tasksManager.updateTask(sleeping);

        System.out.println();
        System.out.println("Обновление статусов подзадач...");
        makingCrutch.setStatus(TaskStatus.IN_PROGRESS);
        tasksManager.updateSubTask(makingCrutch);
        refactoring.setStatus(TaskStatus.DONE);
        tasksManager.updateSubTask(refactoring);
        pullUps.setStatus(TaskStatus.DONE);
        tasksManager.updateSubTask(pullUps);
        breathing.setStatus(TaskStatus.DONE);
        tasksManager.updateSubTask(breathing);

        nothing.setDescription("Ну прям совсем ничего не делать!");
        tasksManager.updateEpic(nothing);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЗАДАЧ ПОСЛЕ ОБНОВЛЕНИЯ:");
        Collection<Task> allTasksAfterUpdate = tasksManager.getAllTasks();
        printTasks(allTasksAfterUpdate);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ПОДЗАДАЧ ПОСЛЕ ОБНОВЛЕНИЯ:");
        Collection<SubTask> allSubtasksAfterUpdate = tasksManager.getAllSubTasks();
        printSubTasks(allSubtasksAfterUpdate);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЭПИКОВ ПОСЛЕ ОБНОВЛЕНИЯ:");
        Collection<Epic> allEpicsAfterUpdate = tasksManager.getAllEpics();
        printEpics(allEpicsAfterUpdate);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);


        tasksManager.removeTaskById(sleeping.getId());
        System.out.println();
        System.out.println("Удалена задача:");
        System.out.println(sleeping.getName());

        tasksManager.removeSubTaskById(benchPress.getId());
        System.out.println();
        System.out.println("Удалена подзадача:");
        System.out.println(benchPress.getName());

        tasksManager.removeEpicById(developingOfTracker.getId());
        System.out.println();
        System.out.println("Удален эпик:");
        System.out.println(developingOfTracker.getName());

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЗАДАЧ ПОСЛЕ УДАЛЕНИЯ ЗАДАЧИ " + sleeping.getName() + ":");
        Collection<Task> allTasksAfterDeleting = tasksManager.getAllTasks();
        printTasks(allTasksAfterDeleting);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ПОДЗАДАЧ ПОСЛЕ УДАЛЕНИЯ ПОДЗАДАЧИ " + benchPress.getName() + ":");
        Collection<SubTask> allSubTasksAfterDeleting = tasksManager.getAllSubTasks();
        printSubTasks(allSubTasksAfterDeleting);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЭПИКОВ ПОСЛЕ УДАЛЕНИЯ ЭПИКА " + developingOfTracker.getName() + ":");
        Collection<Epic> allEpicsAfterDeleting = tasksManager.getAllEpics();
        printEpics(allEpicsAfterDeleting);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);


        long awakeningId = awakening.getId();
        System.out.println("Поиск задачи c идентификатором " + awakeningId + ":");
        Task taskFoundById = tasksManager.getTaskById(awakeningId);
        System.out.println("Найдена задача " + taskFoundById);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

        long pullUpsId = pullUps.getId();
        System.out.println("Поиск подзадачи c идентификатором " + pullUpsId + ":");
        SubTask subTaskFoundById = tasksManager.getSubTaskById(pullUpsId);
        System.out.println("Найдена задача " + subTaskFoundById);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

        long nothingId = nothing.getId();
        System.out.println("Поиск эпика c идентификатором " + nothingId + ":");
        Epic epicFoundById = tasksManager.getEpicById(nothingId);
        System.out.println("Найден эпик " + epicFoundById);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);


        System.out.println();
        System.out.println("Удаление всех задач, подзадач и эпиков!");
        tasksManager.removeAllTasks();
        tasksManager.removeAllSubtasks();
        tasksManager.removeAllEpics();
        System.out.println();

        System.out.println("Список всех задач после полного удаления:");
        Collection<Task> allTasksAfterFullDeleting = tasksManager.getAllTasks();
        printTasks(allTasksAfterFullDeleting);
        System.out.println();
        System.out.println("Список всех подзадач после полного удаления:");
        Collection<SubTask> allSubTasksAfterFullDeleting = tasksManager.getAllSubTasks();
        printSubTasks(allSubTasksAfterFullDeleting);
        System.out.println();
        System.out.println("Список всех эпиков после полного удаления:");
        Collection<Epic> allEpicsAfterFullDeleting = tasksManager.getAllEpics();
        printEpics(allEpicsAfterFullDeleting);

        System.out.println();
        System.out.println("СПИСОК ПОСЛЕДНИХ 10 ПРОСМОТРЕННЫХ ЗАДАЧ:");
        last10viewedTasks = historyManager.getHistory();
        printTasks(last10viewedTasks);

    }

    public static void printTasks(Collection<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    public static void printSubTasks(Collection<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            System.out.println(subTask);
        }
    }

    public static void printEpics(Collection<Epic> epics) {
        for (Epic epic : epics) {
            System.out.print(epic);
            System.out.println("Описание эпика: " + epic.getDescription());
            System.out.println();
        }
    }

}
