package tasks;

public class Task implements Comparable {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType taskType;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskType = TaskType.TASK;
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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return this.taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
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
            hash = (int) this.id;
        }
        return hash;
    }

    @Override
    public String toString() {
        String result;
        result = name + " с идентификатором = " + id;
        if (status != null) {
            result += " сейчас в статусе : ";
            switch (status) {
                case TaskStatus.NEW:
                    result += "НОВАЯ";
                    break;
                case TaskStatus.IN_PROGRESS:
                    result += "В РАБОТЕ";
                    break;
                case TaskStatus.DONE:
                    result += "ВЫПОЛНЕНА";
                    break;
            }
        } else {
            result += " по каким-то причинам не имеет статуса";
        }
        return result;
    }

    @Override
    public int compareTo(Object obj) {
        Task task = (Task) obj;
        return this.id - task.getId();

    }

}
