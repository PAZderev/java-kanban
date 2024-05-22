package http;

import http.api.BaseHttpHandler;
import com.google.gson.Gson;
import managers.classes.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.SubTask;
import utils.enums.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SubTasksHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = new BaseHttpHandler(new InMemoryTaskManager()).getGson();
    HttpClient httpClient;
    URI url;

    SubTasksHandlerTest() {

    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.clear();
        taskServer.startServer();
        httpClient = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
    }

    @AfterEach
    public void tearDown() throws IOException {
        taskServer.stopServer();
    }

    @Test
    public void getSubTasks() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("GET_SUBTASKS_HTTP_TEST");
        SubTask subTask1 = new SubTask("SubTask 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now(), -1);
        subTask1.setEpicID(subTask1.getId() + 1);

        HttpRequest request1 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask1))).build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        SubTask subTask2 = new SubTask("SubTask 2", "Desc 2", TaskStatus.NEW, Duration.ofSeconds(30), null, -1);
        subTask2.setEpicID(subTask2.getId() + 1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask2))).build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder().uri(url)
                .GET().build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());


        assertEquals(response3.statusCode(), 200);
        System.out.println(response3.body());
        System.out.println("*********************************************************");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        System.out.println("****************************************************");
        System.out.println("ADD_SUBTASK_TEST_HTTP");
        SubTask subTask = new SubTask("SubTask 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now(), -1);
        subTask.setEpicID(subTask.getId() + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getSubTasks().size());
        System.out.println("****************************************************");
    }

    @Test
    public void getSubTaskById() throws IOException, InterruptedException {
        System.out.println("****************************************************");
        System.out.println("GET_SUBTASK_BY_ID_TEST_HTTP");
        SubTask subTask = new SubTask("Task 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now(), -1);
        subTask.setEpicID(subTask.getId() + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int id = subTask.getId();
        URI getId1 = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest requestGet = HttpRequest.newBuilder().uri(getId1)
                .GET().build();
        HttpResponse<String> responseGet = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());
        System.out.println(responseGet.body());
        URI getId10 = URI.create("http://localhost:8080/subtasks/10000");
        HttpRequest requestGetWrong = HttpRequest.newBuilder().uri(getId10)
                .GET().build();
        HttpResponse<String> responseGetWrong = httpClient.send(requestGetWrong, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGetWrong.statusCode());
        System.out.println("****************************************************");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        System.out.println("********************************************");
        System.out.println("DELETE_SUB_TASK_TEST_HTTP");
        SubTask subTask = new SubTask("SubTask 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now(), -1);
        subTask.setEpicID(subTask.getId() + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int id = subTask.getId();
        System.out.println(taskManager.getSubTasks().size());
        URI getId1 = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(getId1)
                .DELETE().build();
        HttpResponse<String> responseDelete = httpClient.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseDelete.statusCode());
        System.out.println("****************************************************");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        System.out.println("****************************************************");
        System.out.println("UPDATE_SUBTASK_HTTP_TEST");
        SubTask subTask = new SubTask("SubTask 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now(), -1);
        subTask.setEpicID(subTask.getId() + 1);
        System.out.println(gson.toJson(subTask));
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getSubTasks().size());
        int id = subTask.getId();
        subTask.setEpicID(subTask.getEpicID() + 1);
        URI temp = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(temp)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> responseUpdate = httpClient.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getSubTasks().size());
        assertEquals(id + 2, taskManager.getSubTaskById(id).getEpicID());
        System.out.println("****************************************************");
    }

    @Test
    public void hasIntersections() throws IOException, InterruptedException {
        System.out.println("****************************************");
        System.out.println("HAS_INTERSECTIONS_TEST_HTTP_SUBTASK");
        SubTask subTask1 = new SubTask("SubTask 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30),
                LocalDateTime.now(), -1);
        subTask1.setEpicID(subTask1.getId() + 1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask1))).build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        SubTask subTask2 = new SubTask("SubTask 2", "Desc 2", TaskStatus.NEW, Duration.ofSeconds(30),
                LocalDateTime.now(), subTask1.getId() + 2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask2))).build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(response2.statusCode(), 406);
        System.out.println("*****************************************");
    }
}

