package server;

import managers.interfaces.TaskManager;

public class PrioritiziedHttpHandler extends BaseHttpHandler {

    public PrioritiziedHttpHandler(TaskManager taskManager) {
        super(taskManager);
    }
}
