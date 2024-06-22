package managers;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static List<Long> historyOfView = new ArrayList<>();
    private static TaskManager taskManager = Managers.getDefault();

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        historyOfView.add(task.getId());
    }

    @Override
    public void resetHistory() {
        historyOfView.clear();
    }

    @Override
    public List<Task> getHistory() {
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
            Task task = taskManager.getAllTypeTask().get(viewedTaskId);
            if (task != null) {
                last10ViewedTasks.add(task);
            }
        }
        return last10ViewedTasks;
    }

}
