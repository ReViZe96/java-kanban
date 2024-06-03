public class SubTask extends Task {

    protected Epik epik;

    protected SubTask (String name, String description, long id, Epik epik) {
        super(name, description, id);
        this.epik = epik;
    }

    protected SubTask (String name, String description, long id, TaskStatus status) {
        super(name, description, id, status);
    }

    public Epik getEpik() {
        return this.epik;
    }

    public void setEpik(Epik epik) {
        this.epik = epik;
    }

}
