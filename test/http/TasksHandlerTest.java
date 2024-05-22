package http;

import http.api.BaseHttpHandler;
import com.google.gson.Gson;
import managers.classes.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import utils.enums.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TasksHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = new BaseHttpHandler(new InMemoryTaskManager()).getGson();
    HttpClient httpClient;
    URI url;

    TasksHandlerTest() {

    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.clear();
        taskServer.startServer();
        httpClient = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks");
    }

    @AfterEach
    public void tearDown() throws IOException {
        taskServer.stopServer();
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("GET_TASKS_HTTP_TEST");

        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());

        HttpRequest request1 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW, Duration.ofSeconds(30), null);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder().uri(url)
                .GET().build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(response3.statusCode(), 200);
        System.out.println(response3.body());
        System.out.println("*********************************************************");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("ADD_TASK_HTTP_TEST");
        Task task = new Task("Task 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTasks().size());
        System.out.println("*********************************************************");

    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("GET_TASK_BY_ID_HTTP_TEST");
        Task task = new Task("Task 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int id = task.getId();
        URI getId1 = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest requestGet = HttpRequest.newBuilder().uri(getId1)
                .GET().build();
        HttpResponse<String> responseGet = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());
        System.out.println(responseGet.body());
        URI getId10 = URI.create("http://localhost:8080/tasks/10000");
        HttpRequest requestGetWrong = HttpRequest.newBuilder().uri(getId10)
                .GET().build();
        HttpResponse<String> responseGetWrong = httpClient.send(requestGetWrong, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGetWrong.statusCode());
        System.out.println("*********************************************************");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("DELETE_TASK_HTTP_TEST");
        Task task = new Task("Task 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int id = task.getId();
        URI getId1 = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(getId1)
                .DELETE().build();
        HttpResponse<String> responseDelete = httpClient.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode());
        System.out.println("*********************************************************");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("UPDATE_TASK_HTTP_TEST");
        Task task = new Task("Task 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        System.out.println(gson.toJson(task));
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTasks().size());
        int id = task.getId();
        task.setName("Task 2");
        URI temp = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(temp)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
        HttpResponse<String> responseUpdate = httpClient.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTasks().size());
        assertEquals("Task 2", taskManager.getTaskById(id).getName());
        System.out.println("*********************************************************");
    }

    @Test
    public void hasIntersections() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("hasIntersections_TASK_HTTP_TEST");
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());

        HttpRequest request1 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        HttpRequest request2 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(response2.statusCode(), 406);
        System.out.println("*********************************************************");
    }
}
