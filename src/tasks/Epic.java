package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        super.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, ArrayList<SubTask> subTasks) {
        super(name, description);
        super.taskType = TaskType.EPIC;
        this.subtasks = subTasks;
    }

    public ArrayList<SubTask> getSubtasks() {
        return this.subtasks;
    }

    public void setSubtasks(ArrayList<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        String result = super.toString();
        if (subtasks != null) {
            result += " Cписок подзадач эпика: \n";
            for (SubTask subTask : subtasks) {
                String status = null;
                if (subTask.getStatus() != null) {
                    switch (subTask.getStatus()) {
                        case TaskStatus.NEW:
                            status = "НОВАЯ";
                            break;
                        case TaskStatus.IN_PROGRESS:
                            status = "В РАБОТЕ";
                            break;
                        case TaskStatus.DONE:
                            status = "ВЫПОЛНЕНА";
                            break;
                    }
                } else {
                    status = " по каким-то причинам не имеет статуса!";
                }
                result += subTask.getName() + " в статусе: " + status + "\n";
            }
        }
        return result;
    }
}
