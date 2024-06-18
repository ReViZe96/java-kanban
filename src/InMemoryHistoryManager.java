import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    protected ArrayList<Long> historyOfView;
    protected TaskManager taskManager;

    public InMemoryHistoryManager() {
        historyOfView = new ArrayList<>();
        taskManager = new InMemoryTasksManager();
    }

    @Override
    public ArrayList<Long> getHistoryOfView() {
        return this.historyOfView;
    }

    @Override
    public void setHistoryOfView(ArrayList<Long> historyOfView) {
        this.historyOfView = historyOfView;
    }

    @Override
    public void add(Task task) {

    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Long> last10ViewedTaskIds = new ArrayList<>();
        if (historyOfView.size() <= 10) {
            last10ViewedTaskIds.addAll(historyOfView);
        } else {
            for (int i = historyOfView.size() - 1; i >= historyOfView.size() - 10; i--) {
                last10ViewedTaskIds.add(historyOfView.get(i));
            }
        }
        ArrayList<Task> last10ViewedTasks = new ArrayList<>();
        for (Long viewedTaskId : last10ViewedTaskIds) {
            Task task = taskManager.getTaskById(viewedTaskId);
            if (task != null) {
                last10ViewedTasks.add(task);
            }
        }
        return last10ViewedTasks;
    }

}
