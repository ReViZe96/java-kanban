package managers;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import managers.utils.Node;
import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    protected Node<Task> head = new Node<>(null, null, null);
    protected Node<Task> tail = new Node<>(null, null, null);

    private static Map<Integer, Node<Task>> historyOfView = new HashMap<>();
    private static TaskManager taskManager = Managers.getDefault();

    public InMemoryHistoryManager() {
        head.setNext(tail);
        tail.setPrev(head);
    }

    @Override
    public void add(Task task) {
        if (historyOfView.containsKey(task.getId())) {
            Node<Task> node = historyOfView.get(task.getId());
            removeNode(node);
        }

        Node<Task> newNode = linkLast(task);
        historyOfView.put(task.getId(), newNode);
    }

    @Override
    public void resetHistory() {
        historyOfView.clear();
    }

    @Override
    public List<Task> getHistory() {
        Set<Map.Entry<Integer, Node<Task>>> historyEntrySet = historyOfView.entrySet();
        ArrayList<Task> viewedTasks = new ArrayList<>();
        for (Map.Entry<Integer, Node<Task>> historyEntry : historyEntrySet) {
            viewedTasks.add(historyEntry.getValue().data);
        }
        Collections.reverse(viewedTasks);
        return viewedTasks;
    }

    @Override
    public void remove(int id) {
        if (historyOfView.containsKey(id)) {
            historyOfView.remove(id);
        }
    }

    public Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = this.tail;
        final Node<Task> newNode = new Node<>(null, task, oldTail);
        this.tail = newNode;
        if (oldTail == null) {
            this.head = newNode;
        } else {
            oldTail.prev = newNode;
        }
        return newNode;
    }

    public void removeNode(Node<Task> node) {
        Task removedTask = node.getData();
        Integer removedTasksId = removedTask.getId();
        historyOfView.remove(removedTasksId);
    }
}

