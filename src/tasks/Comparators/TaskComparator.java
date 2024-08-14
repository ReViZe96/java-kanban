package tasks.Comparators;

import tasks.Task;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task first, Task second) {
        LocalDateTime firstStartTime = first.getStartTime();
        LocalDateTime secondStartTime = second.getStartTime();
        if (firstStartTime != null && secondStartTime != null) {
            if (firstStartTime.isEqual(secondStartTime)) {
                return 0;
            } else if (firstStartTime.isAfter(secondStartTime)) {
                return 1;
            } else {
                return -1;
            }
        } else {
            throw new RuntimeException("У одной из сравниваемых задач отсутствует время начала!");
        }
    }
}
