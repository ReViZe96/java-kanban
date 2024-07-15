package managers.utils;

public class Node<Task> {
    public Task data;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Node<Task> prev, Task data, Node<Task> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Task getData() {
        return this.data;
    }

    public void setNext(Node<Task> next) {
        this.next = next;
    }

    public void setPrev(Node<Task> prev) {
        this.prev = prev;
    }
}
