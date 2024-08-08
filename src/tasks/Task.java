package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

public class Task implements Comparable, Comparator {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType taskType;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskType = TaskType.TASK;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.taskType = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
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

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Optional<LocalDateTime> startTime) {
        if (startTime.isPresent()) {
            this.startTime = startTime.get();
        }
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plus(this.duration);
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
            hash = this.id;
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
        if (startTime != null) {
            result += " Время начала - " + startTime + ".";
        } else {
            result += " Время начала не указана.";
        }
        if (duration != null) {
            result += " Продолжительность в секундах = " + duration.toSeconds() + ".";
        } else {
            result += " Продолжительность не указана.";
        }

        return result;
    }

    @Override
    public int compareTo(Object obj) {
        Task task = (Task) obj;
        return this.id - task.getId();

    }

    @Override
    public int compare(Object first, Object second) {
        Task firstTask = (Task) first;
        Task secondTask = (Task) second;

        LocalDateTime firstStartTime = firstTask.getStartTime();
        LocalDateTime secondStartTime = secondTask.getStartTime();
        if (firstStartTime != null && secondStartTime != null) {
            if (firstStartTime.isEqual(secondStartTime)) {
                return 0;
            } else if (firstStartTime.isBefore(secondStartTime)) {
                return 1;
            } else {
                return -1;
            }
        } else {
            throw new RuntimeException("У одной из сравниваемых задач отсутствует время начала!");
        }
    }

}
