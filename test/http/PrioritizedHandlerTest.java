package http;

import http.api.BaseHttpHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.classes.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public class PrioritizedHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = new BaseHttpHandler(new InMemoryTaskManager()).getGson();
    HttpClient httpClient;
    URI url;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.clear();
        taskServer.startServer();
        httpClient = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/prioritized");
    }

    @AfterEach
    public void tearDown() throws IOException {
        taskServer.stopServer();
    }

    @Test
    void addAndGetPriorityList() throws IOException, InterruptedException {
        System.out.println("********************************************************");
        System.out.println("ADD_AND_GET_PRIORITY_LIST_TEST_HTTP");
        Task task = new Task("Task1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        URI taskURI = URI.create("http://localhost:8080/tasks");
        HttpRequest requestTask = HttpRequest.newBuilder().uri(taskURI)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        HttpResponse<String> responseTask = httpClient.send(requestTask, HttpResponse.BodyHandlers.ofString());
        URI subURI = URI.create("http://localhost:8080/subtasks");
        SubTask subTask = new SubTask("Epic1", "Desc1", TaskStatus.NEW,
                Duration.ofSeconds(30), LocalDateTime.now().plus(Duration.ofSeconds(900)), -1);
        HttpRequest requestSub = HttpRequest.newBuilder().uri(subURI)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> responseSub = httpClient.send(requestSub, HttpResponse.BodyHandlers.ofString());
        HttpRequest requestPriority = HttpRequest.newBuilder().uri(url)
                .GET().build();
        HttpResponse<String> response = httpClient.send(requestPriority, HttpResponse.BodyHandlers.ofString());
        Type taskListMap = new TypeToken<Collection<Task>>() {
        }.getType();
        Collection<Task> priorityList = gson.fromJson(response.body(), taskListMap);
        System.out.println("priorityList: (Ожидаем первым TASK, вторым SUBTASK)");
        System.out.println(priorityList);
        System.out.println("********************************************************");


    }
}
