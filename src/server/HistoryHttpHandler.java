package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.Managers;
import managers.interfaces.HistoryManager;

import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHttpHandler extends BaseHttpHandler {

    protected HistoryManager historyManager;

    public HistoryHttpHandler() {
        this.historyManager = Managers.getDefaultHistory();
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathsParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        if (pathsParts.length != 2 && !(method.equals("GET"))) {
            sendHasBadRequest(exchange);
        }
        try {
            getHistory(exchange);
        } catch (Exception e) {
            sendHasBadRequest(exchange);
        }
    }

    public void getHistory(HttpExchange exchange) throws IOException {
        List<Task> historyOfView = historyManager.getHistory();
        Gson gson = createGson();
        sendText(exchange, gson.toJson(historyOfView));
    }

}
