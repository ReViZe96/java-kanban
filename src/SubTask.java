public class SubTask extends Task {

    private Epic epic;

    protected SubTask(String name, String description) {
        super(name, description);
    }

    protected SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return this.epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

}
