package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class Epic extends Task {

    private ArrayList<SubTask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        super.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, ArrayList<SubTask> subTasks) {
        super(name, description);
        super.taskType = TaskType.EPIC;
        this.subtasks = subTasks;
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        super.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime, ArrayList<SubTask> subTasks) {
        super(name, description, duration, startTime);
        super.taskType = TaskType.EPIC;
        this.subtasks = subTasks;
    }

    public ArrayList<SubTask> getSubtasks() {
        return this.subtasks;
    }

    public void setSubtasks(ArrayList<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    public void setEndTime(Optional<LocalDateTime> endTime) {
        if (endTime.isPresent()) {
            this.endTime = endTime.get();
        }
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
