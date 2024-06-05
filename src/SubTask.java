public class SubTask extends Task {

    protected Epik epik;

    protected SubTask(String name, String description, long id, TaskStatus status) {
        super(name, description, id, status);
    }

    protected SubTask(String name, String description, long id, TaskStatus status, Epik epik) {
        super(name, description, id, status);
        this.epik = epik;
    }

    public Epik getEpik() {
        return this.epik;
    }

    public void setEpik(Epik epik) {
        this.epik = epik;
    }

}
