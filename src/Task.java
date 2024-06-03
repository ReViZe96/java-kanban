public class Task {

    protected String name;
    protected String description;
    protected long id;
    protected TaskStatus status;

    protected Task (String name, String description, long id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    protected Task (String name, String description, long id, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return this.id;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        Task task = (Task) object;
        if (task.getId() == this.getId()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (this.id != 0) {
            hash = (int)this.id;
        }
        return hash;
    }

    @Override
    public String toString() {
        return name + " c с идентификатором = " + id;
    }

}
