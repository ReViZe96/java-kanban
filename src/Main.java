import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        TasksManager tasksManager = new TasksManager();

        Task awakening = new Task("Проснуться", "Необходимо проснуться в 8:00", TasksManager.idCounter,
                TaskStatus.NEW);
        Task sleeping = new Task("Заснуть", "Постараться заснуть раньше 1:00", ++TasksManager.idCounter,
                TaskStatus.NEW);
        tasksManager.addTask(awakening);
        tasksManager.addTask(sleeping);
        System.out.println("Задачи " + awakening + " и " + sleeping + " созданы!");

        SubTask makingCrutch = new SubTask("Создать основу приложения", "Покостылить",
                ++TasksManager.idCounter, TaskStatus.NEW);
        SubTask refactoring = new SubTask("Осуществить рефакторинг кода", "Сделать все красиво",
                ++TasksManager.idCounter, TaskStatus.NEW);
        tasksManager.addSubTask(makingCrutch);
        tasksManager.addSubTask(refactoring);
        System.out.println("Подзадачи " + makingCrutch + " и " + refactoring + " созданы!");

        ArrayList<SubTask> developingStages = new ArrayList<>();
        developingStages.add(makingCrutch);
        developingStages.add(refactoring);
        Epik developingOfTracker = new Epik("Разработать трекер задач", "Создать работающее приложение",
                ++TasksManager.idCounter, developingStages);
        tasksManager.addEpik(developingOfTracker);
        System.out.println("Эпик " + developingOfTracker + " создан!");


        SubTask pullUps = new SubTask("Подтягивания на перекладине", "Подтянуться 10 раз",
                ++TasksManager.idCounter, TaskStatus.NEW);
        tasksManager.addSubTask(pullUps);
        System.out.println("Подзадача " + pullUps + " создана!");


        ArrayList<SubTask> fitnessStages = new ArrayList<>();
        fitnessStages.add(pullUps);
        Epik fitness = new Epik("Заниматься спортом", "Хотя бы раз в день", ++TasksManager.idCounter,
                fitnessStages);
        tasksManager.addEpik(fitness);
        System.out.println("Эпик " + fitness + " создан!");


        System.out.println();
        System.out.println("Список всех задач:");
        Collection<Task> allTasks = tasksManager.getAllTasks();
        for (Task task : allTasks) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Список всех подзадач:");
        Collection<SubTask> allSubtasks = tasksManager.getAllSubTasks();
        for (SubTask subTask : allSubtasks) {
            System.out.println(subTask);
        }

        System.out.println();
        System.out.println("Список всех эпиков:");
        Collection<Epik> allEpiks = tasksManager.getAllEpiks();
        for (Epik epik : allEpiks) {
            System.out.println(epik);
        }

        System.out.println();
        System.out.println("Обновление статусов задач:");
        tasksManager.updateTask(new Task(awakening.getName(), awakening.getDescription(), awakening.getId(), TaskStatus.DONE));
        tasksManager.updateTask(new Task(sleeping.getName(), sleeping.getDescription(), sleeping.getId(), TaskStatus.IN_PROGRESS));

        System.out.println();
        System.out.println("Обновление статусов подзадач:");
        tasksManager.updateSubTask(new SubTask(makingCrutch.getName(), makingCrutch.getDescription(),
                makingCrutch.getId(), TaskStatus.IN_PROGRESS));
        tasksManager.updateSubTask(new SubTask(refactoring.getName(), refactoring.getDescription(), refactoring.getId(),
                TaskStatus.DONE));
        tasksManager.updateSubTask(new SubTask(pullUps.getName(), pullUps.getDescription(), pullUps.getId(), TaskStatus.DONE));

        System.out.println();
        System.out.println("Список всех задач после обновления:");
        Collection<Task> allTasksAfterUpdate = tasksManager.getAllTasks();
        for (Task task : allTasksAfterUpdate) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Список всех подзадач после обновления:");
        Collection<SubTask> allSubtasksAfterUpdate = tasksManager.getAllSubTasks();
        for (SubTask subTask : allSubtasksAfterUpdate) {
            System.out.println(subTask);
        }

        System.out.println();
        System.out.println("Список всех эпиков после обновления:");
        Collection<Epik> allEpiksAfterUpdate = tasksManager.getAllEpiks();
        for (Epik epik : allEpiksAfterUpdate) {
            System.out.println(epik);
        }

        tasksManager.removeTaskById(sleeping.getId());
        System.out.println();
        System.out.println("Задача " + sleeping + " удалена");

        System.out.println();
        System.out.println("Список всех задач после удаления " + sleeping + ":");
        Collection<Task> allTasksAfterDeleting = tasksManager.getAllTasks();
        for (Task task : allTasksAfterDeleting) {
            System.out.println(task);
        }

        tasksManager.removeEpikById(developingOfTracker.getId());
        System.out.println();
        System.out.println("Эпик " + developingOfTracker + " удален");

        System.out.println();
        System.out.println("Список всех эпиков после удаления " + developingOfTracker + ":");
        Collection<Epik> allEpiksAfterDeleting = tasksManager.getAllEpiks();
        for (Epik epik : allEpiksAfterDeleting) {
            System.out.println(epik);
        }
    }


}
