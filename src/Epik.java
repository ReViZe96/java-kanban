import java.util.ArrayList;

public class Epik extends Task {

    protected ArrayList<SubTask> subtasks;

    protected Epik (String name, String description, long id, ArrayList<SubTask> subTasks) {
        super (name, description, id);
        this.subtasks = subTasks;
        this.status = calculateStatus(subTasks);
    }

    private TaskStatus calculateStatus(ArrayList<SubTask> subTasks) {
        TaskStatus epikStatus = null;
        int newSubtaskCount = 0;
        int doneSubtaskCount = 0;
        int amountOfSubtasks = subTasks.size();
        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case TaskStatus.NEW :
                    newSubtaskCount++;
                case TaskStatus.DONE:
                    doneSubtaskCount++;
            }
        }
        if (newSubtaskCount == amountOfSubtasks) {
            epikStatus = TaskStatus.NEW;
        } else if (doneSubtaskCount == amountOfSubtasks) {
            epikStatus = TaskStatus.DONE;
        } else {
            epikStatus = TaskStatus.IN_PROGRESS;
        }
        return epikStatus;
    }

    public ArrayList<SubTask> getSubtasks() {
        return this.subtasks;
    }

    public void setSubtasks (ArrayList<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    public void removeSubtasks (SubTask subTask) {
        if (!subtasks.isEmpty()) {
            if (subtasks.contains(subTask)) {
                subtasks.remove(subTask);
            } else {
                System.out.println("Эпик не содержит подзадачу " + subTask + "Нечего удалять!");
            }
        } else {
            System.out.println("Эпик пока не содержит ни одной подзадачи");
        }
    }

    public void addSubTask(SubTask subTask) {
        if (!subtasks.contains(subTask)) {
            subtasks.add(subTask);
        } else {
            System.out.println("Эпик уже содержит указанную подзадачу " + subTask);
        }
    }
}
