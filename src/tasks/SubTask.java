package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private Epic epic;

    public SubTask(String name, String description) {
        super(name, description);
        super.taskType = TaskType.SUBTASK;
    }

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        super.taskType = TaskType.SUBTASK;
        this.epic = epic;
    }

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        super.taskType = TaskType.SUBTASK;
    }

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, Epic epic) {
        super(name, description, duration, startTime);
        super.taskType = TaskType.SUBTASK;
        this.epic = epic;
    }

    public Epic getEpic() {
        return this.epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

}
