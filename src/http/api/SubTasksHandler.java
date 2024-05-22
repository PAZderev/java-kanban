package http.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.interfaces.TaskManager;
import tasks.SubTask;
import utils.exceptions.NotFoundEpicException;
import utils.exceptions.NotFoundException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SubTasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final String INIT_PATH = "/subtasks";

    public SubTasksHandler(TaskManager inMemoryTaskManager) {
        super(inMemoryTaskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                if (path.equals(INIT_PATH)) {
                    try {
                        String a = gson.toJson(taskManager.getSubTasks());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    sendText(exchange, gson.toJson(taskManager.getSubTasks()), 200);
                } else {
                    int id = Integer.parseInt(path.split("/")[ID_POS]);
                    try {
                        SubTask task = taskManager.getSubTaskById(id);
                        sendText(exchange, gson.toJson(task), 200);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange);
                    }
                }
                break;
            case "POST":
                if (path.equals(INIT_PATH)) {
                    handleCreateSubTask(exchange);
                } else {
                    handleUpdateSubTask(exchange);
                }
                break;
            case "DELETE":
                int id = Integer.parseInt(path.split("/")[ID_POS]);
                handleDeleteSubTask(exchange, id);
                break;
            default:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private void handleCreateSubTask(HttpExchange exchange) throws IOException {
        SubTask subTask = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), SubTask.class);
        if (taskManager.createSubTask(subTask)) {
            sendText(exchange, "SubTask created", 201);
        } else {
            sendHasInteractions(exchange);
        }
    }

    private void handleUpdateSubTask(HttpExchange exchange) throws IOException {
        SubTask subTask = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), SubTask.class);
        if (taskManager.updateSubTask(subTask)) {
            sendText(exchange, "SubTask updated", 201);
        } else {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteSubTask(HttpExchange exchange, int id) throws IOException {
        try {
            taskManager.getSubTaskById(id);
            taskManager.removeSubTaskByID(id);
            sendText(exchange, "SubTask removed", 200);
        } catch (NotFoundEpicException e) {
            sendText(exchange, "SubTask removed", 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }
}
