import java.util.Collection;

public class Main {

    public static void main(String[] args) {

        TasksManager tasksManager = new TasksManager();

        Task awakening = new Task("Проснуться", "Необходимо проснуться в 8:00", TasksManager.idCounter,
                TaskStatus.NEW);
        Task sleeping = new Task("Заснуть", "Постараться заснуть раньше 1:00", ++TasksManager.idCounter,
                TaskStatus.NEW);
        tasksManager.addTask(awakening);
        tasksManager.addTask(sleeping);
        System.out.println("Созданы задачи:");
        System.out.println(awakening);
        System.out.println(sleeping);


        Epik fitness = new Epik("Заниматься спортом", "Хотя бы раз в день", ++TasksManager.idCounter);

        SubTask pullUps = new SubTask("Подтягивания на перекладине", "Подтянуться 10 раз",
                ++TasksManager.idCounter, TaskStatus.NEW, fitness);
        SubTask pushUps = new SubTask("Отжиматься от пола", "Минимум 3 раза в день",
                ++TasksManager.idCounter, TaskStatus.NEW, fitness);
        SubTask benchPress = new SubTask("Жим гантели лёжа", "Два и более раза за день",
                ++TasksManager.idCounter, TaskStatus.NEW, fitness);
        tasksManager.addSubTask(pullUps);
        tasksManager.addSubTask(pushUps);
        tasksManager.addSubTask(benchPress);
        System.out.println();
        System.out.println("Созданы подзадачи:");
        System.out.println(pullUps);
        System.out.println(pushUps);
        System.out.println(benchPress);

        fitness.addSubTask(pullUps);
        fitness.addSubTask(pushUps);
        fitness.addSubTask(benchPress);
        tasksManager.addEpik(fitness);
        System.out.println();
        System.out.println("Создан эпик:");
        System.out.println(fitness);


        Epik developingOfTracker = new Epik("Разработать трекер задач", "Создать работающее приложение",
                ++TasksManager.idCounter);

        SubTask makingCrutch = new SubTask("Создать основу приложения", "Покостылить",
                ++TasksManager.idCounter, TaskStatus.NEW, developingOfTracker);
        SubTask refactoring = new SubTask("Осуществить рефакторинг кода", "Сделать все красиво",
                ++TasksManager.idCounter, TaskStatus.NEW, developingOfTracker);
        tasksManager.addSubTask(makingCrutch);
        tasksManager.addSubTask(refactoring);
        System.out.println();
        System.out.println("Созданы подзадачи:");
        System.out.println(makingCrutch);
        System.out.println(refactoring);

        developingOfTracker.addSubTask(makingCrutch);
        developingOfTracker.addSubTask(refactoring);
        tasksManager.addEpik(developingOfTracker);
        System.out.println();
        System.out.println("Создан эпик:");
        System.out.println(developingOfTracker);


        Epik nothing = new Epik("Ничего не делать", "Только не забывать дышать", ++TasksManager.idCounter);

        SubTask breathing = new SubTask("Дышать", "Размеренно и спокойно", ++TasksManager.idCounter,
                TaskStatus.NEW, nothing);
        tasksManager.addSubTask(breathing);
        System.out.println();
        System.out.println("Создана подзадача:");
        System.out.println(breathing);

        nothing.addSubTask(breathing);
        tasksManager.addEpik(nothing);
        System.out.println();
        System.out.println("Создан эпик:");
        System.out.println(nothing);


        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЗАДАЧ:");
        Collection<Task> allTasks = tasksManager.getAllTasks();
        printTasks(allTasks);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ПОДЗАДАЧ:");
        Collection<SubTask> allSubtasks = tasksManager.getAllSubTasks();
        printSubTasks(allSubtasks);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЭПИКОВ:");
        Collection<Epik> allEpiks = tasksManager.getAllEpiks();
        printEpiks(allEpiks);


        System.out.println();
        System.out.println("Обновление статусов задач...");
        tasksManager.updateTask(new Task(awakening.getName(), awakening.getDescription(), awakening.getId(), TaskStatus.DONE));
        tasksManager.updateTask(new Task(sleeping.getName(), sleeping.getDescription(), sleeping.getId(), TaskStatus.IN_PROGRESS));

        System.out.println();
        System.out.println("Обновление статусов подзадач...");
        tasksManager.updateSubTask(new SubTask(makingCrutch.getName(), makingCrutch.getDescription(),
                makingCrutch.getId(), TaskStatus.IN_PROGRESS, makingCrutch.getEpik()));
        tasksManager.updateSubTask(new SubTask(refactoring.getName(), refactoring.getDescription(), refactoring.getId(),
                TaskStatus.DONE, refactoring.getEpik()));
        tasksManager.updateSubTask(new SubTask(pullUps.getName(), pullUps.getDescription(), pullUps.getId(), TaskStatus.DONE,
                pullUps.getEpik()));
        tasksManager.updateSubTask(new SubTask(breathing.getName(), breathing.getDescription(), breathing.getId(),
                TaskStatus.DONE, breathing.getEpik()));
        tasksManager.updateEpik(new Epik(nothing.getName(), "Ну прям совсем ничего не делать!", nothing.getId(),
                nothing.getSubtasks()));

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЗАДАЧ ПОСЛЕ ОБНОВЛЕНИЯ:");
        Collection<Task> allTasksAfterUpdate = tasksManager.getAllTasks();
        printTasks(allTasksAfterUpdate);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ПОДЗАДАЧ ПОСЛЕ ОБНОВЛЕНИЯ:");
        Collection<SubTask> allSubtasksAfterUpdate = tasksManager.getAllSubTasks();
        printSubTasks(allSubtasksAfterUpdate);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЭПИКОВ ПОСЛЕ ОБНОВЛЕНИЯ:");
        Collection<Epik> allEpiksAfterUpdate = tasksManager.getAllEpiks();
        printEpiks(allEpiksAfterUpdate);


        tasksManager.removeTaskById(sleeping.getId());
        System.out.println();
        System.out.println("Удалена задача:");
        System.out.println(sleeping.getName());

        fitness.removeSubtasks(benchPress);
        tasksManager.removeSubTaskById(benchPress.getId());
        System.out.println();
        System.out.println("Удалена подзадача:");
        System.out.println(benchPress.getName());

        tasksManager.removeEpikById(developingOfTracker.getId());
        System.out.println();
        System.out.println("Удален эпик:");
        System.out.println(developingOfTracker.getName());

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЗАДАЧ ПОСЛЕ УДАЛЕНИЯ ЗАДАЧИ " + sleeping.getName() + ":");
        Collection<Task> allTasksAfterDeleting = tasksManager.getAllTasks();
        printTasks(allTasksAfterDeleting);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ПОДЗАДАЧ ПОСЛЕ УДАЛЕНИЯ ПОДЗАДАЧИ " + benchPress.getName() + ":");
        Collection<SubTask> allSubTasksAfterDeleting = tasksManager.getAllSubTasks();
        printSubTasks(allSubTasksAfterDeleting);

        System.out.println();
        System.out.println("СПИСОК ВСЕХ ЭПИКОВ ПОСЛЕ УДАЛЕНИЯ ЭПИКА " + developingOfTracker.getName() + ":");
        Collection<Epik> allEpiksAfterDeleting = tasksManager.getAllEpiks();
        printEpiks(allEpiksAfterDeleting);


        long awakeningId = awakening.getId();
        System.out.println("Поиск задачи c идентификатором " + awakeningId + ":");
        Task taskFoundById = tasksManager.getTaskById(awakeningId);
        System.out.println("Найдена задача " + taskFoundById);

        long pullUpsId = pullUps.getId();
        System.out.println("Поиск подзадачи c идентификатором " + pullUpsId + ":");
        SubTask subTaskFoundById = tasksManager.getSubTaskById(pullUpsId);
        System.out.println("Найдена задача " + subTaskFoundById);

        long nothingId = nothing.getId();
        System.out.println("Поиск эпика c идентификатором " + nothingId + ":");
        Epik epikFoundById = tasksManager.getEpikById(nothingId);
        System.out.println("Найден эпик " + epikFoundById);


        System.out.println();
        System.out.println("Удаление всех задач, подзадач и эпиков!");
        tasksManager.removeAllTasks();
        tasksManager.removeAllSubtasks();
        tasksManager.removeAllEpiks();
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
        Collection<Epik> allEpiksAfterFullDeleting = tasksManager.getAllEpiks();
        printEpiks(allEpiksAfterFullDeleting);
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

    public static void printEpiks(Collection<Epik> epiks) {
        for (Epik epik : epiks) {
            System.out.print(epik);
            System.out.println("Описание эпика: " + epik.getDescription());
            System.out.println();
        }
    }

}
