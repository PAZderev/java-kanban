package http.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.interfaces.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager inMemoryTaskManager) {
        super(inMemoryTaskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                sendText(exchange, gson.toJson(taskManager.getInMemoryHistoryManager().getHistory()), 200);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }
}
