package http;

import http.api.BaseHttpHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.classes.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import utils.enums.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class HistoryHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = new BaseHttpHandler(new InMemoryTaskManager()).getGson();
    HttpClient httpClient;
    URI url;

    HistoryHandlerTest() {

    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.clear();
        taskServer.startServer();
        httpClient = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/history");
    }

    @AfterEach
    public void tearDown() throws IOException {
        taskServer.stopServer();
    }

    @Test
    void addAndGetHistory() throws IOException, InterruptedException {
        System.out.println("********************************************************");
        System.out.println("ADD_AND_GET_HISTORY_HTTP_TEST");
        Task task = new Task("Task1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        URI taskURI = URI.create("http://localhost:8080/tasks");
        HttpRequest requestTask = HttpRequest.newBuilder().uri(taskURI)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        HttpResponse<String> responseTask = httpClient.send(requestTask, HttpResponse.BodyHandlers.ofString());
        URI epicURI = URI.create("http://localhost:8080/epics");
        Epic epic = new Epic("Epic1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(epicURI)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> responseEpic = httpClient.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        for (int i = 0; i < 5; i++) {
            taskManager.getEpicById(epic.getId());
            taskManager.getTaskById(task.getId());
        }
        System.out.println("TEST : addAndGetHistory_HTTP, ожидаем две задачи в истории, эпик первый");
        HttpRequest requestHistory = HttpRequest.newBuilder().uri(url)
                .GET().build();
        HttpResponse<String> response = httpClient.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskListType);
        System.out.println(history);
        taskManager.getEpicById(epic.getId());
        response = httpClient.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        history = gson.fromJson(response.body(), taskListType);
        System.out.println("Ожидаем две задачи в истории, эпик второй");
        System.out.println(history);
        System.out.println("********************************************************");


    }
}
