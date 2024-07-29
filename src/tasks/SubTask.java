package tasks;

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

    public Epic getEpic() {
        return this.epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

}
