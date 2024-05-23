package http.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.interfaces.TaskManager;
import tasks.Epic;
import utils.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private static final int GET_SUBTASK_PATH_LENGTH = 4;
    private static final String INIT_PATH = "/epics";

    public EpicsHandler(TaskManager inMemoryTaskManager) {
        super(inMemoryTaskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                if (path.equals(INIT_PATH)) {
                    sendText(exchange, gson.toJson(taskManager.getEpics()), 200);
                } else {
                    int id = Integer.parseInt(path.split("/")[ID_POS]);
                    if (path.split("/").length == GET_SUBTASK_PATH_LENGTH) {
                        handleGetSubTasks(exchange, id);
                    } else {
                        try {
                            Epic temp = taskManager.getEpicById(id);
                            sendText(exchange, gson.toJson(temp), 200);
                        } catch (NotFoundException e) {
                            sendNotFound(exchange);
                        }
                    }
                }
                break;
            case "POST":
                handleCreateEpic(exchange);
                break;
            case "DELETE":
                int id = Integer.parseInt(path.split("/")[ID_POS]);
                handleDeleteEpic(exchange, id);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        Epic epic = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Epic.class);
        if (taskManager.createEpic(epic)) {
            sendText(exchange, "Epic created", 201);
        } else {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.getEpicById(id);
            taskManager.removeEpicByID(id);
            sendText(exchange, "Epic removed", 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetSubTasks(HttpExchange exchange, int id) throws IOException {
        try {
            Epic temp = taskManager.getEpicById(id);
            sendText(exchange, gson.toJson(taskManager.getSubTasksByEpic(temp)), 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }
}
