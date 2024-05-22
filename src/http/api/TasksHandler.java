package http.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.interfaces.TaskManager;
import tasks.Task;
import utils.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final String INIT_PATH = "/tasks";

    public TasksHandler(TaskManager inMemoryTaskManager) {
        super(inMemoryTaskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                if (path.equals(INIT_PATH)) {
                    sendText(exchange, gson.toJson(taskManager.getTasks()), 200);
                } else {
                    int id = Integer.parseInt(path.split("/")[ID_POS]);
                    try {
                        Task task = taskManager.getTaskById(id);
                        sendText(exchange, gson.toJson(task), 200);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
                break;
            case "POST":
                if (path.equals(INIT_PATH)) {
                    handleCreateTask(exchange);
                } else {
                    handleUpdateTask(exchange);
                }
                break;
            case "DELETE":
                int id = Integer.parseInt(path.split("/")[ID_POS]);
                handleDeleteTask(exchange, id);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Task.class);
        if (taskManager.createTask(task)) {
            sendText(exchange, "Task created", 201);
        } else {
            sendHasInteractions(exchange);
        }
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Task.class);
        if (taskManager.updateTask(task)) {
            sendText(exchange, "Task updated", 201);
        } else {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange, int id) throws IOException {
        if (taskManager.getTaskById(id) != null) {
            taskManager.removeTaskByID(id);
            sendText(exchange, "Task removed", 200);
        } else {
            sendNotFound(exchange);
        }
    }
}
