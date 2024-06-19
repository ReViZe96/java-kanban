import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    public static ArrayList<Long> historyOfView = new ArrayList<>();

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {

    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Long> last10ViewedTaskIds = new ArrayList<>();
        if (historyOfView.size() <= 10) {
            for (int i = historyOfView.size() - 1; i >= 0; i--) {
                last10ViewedTaskIds.add(historyOfView.get(i));
            }
        } else {
            for (int i = historyOfView.size() - 1; i >= historyOfView.size() - 10; i--) {
                last10ViewedTaskIds.add(historyOfView.get(i));
            }
        }
        ArrayList<Task> last10ViewedTasks = new ArrayList<>();
        for (Long viewedTaskId : last10ViewedTaskIds) {
            Task task = InMemoryTaskManager.allEpics.get(viewedTaskId);
            if (task != null) {
                last10ViewedTasks.add(task);
            } else {
                task = InMemoryTaskManager.allSubtasks.get(viewedTaskId);
                if (task != null) {
                    last10ViewedTasks.add(task);
                } else {
                    task = InMemoryTaskManager.allTasks.get(viewedTaskId);
                    if (task != null) {
                        last10ViewedTasks.add(task);
                    }
                }
            }
        }
        return last10ViewedTasks;
    }

}
