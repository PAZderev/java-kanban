package http;

import http.api.BaseHttpHandler;
import com.google.gson.Gson;
import managers.classes.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
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

public class EpicsHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = new BaseHttpHandler(new InMemoryTaskManager()).getGson();
    HttpClient httpClient;
    URI url;

    EpicsHandlerTest() {

    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.clear();
        taskServer.startServer();
        httpClient = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
    }

    @AfterEach
    public void tearDown() throws IOException {
        taskServer.stopServer();
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("GET_EPICS_HTTP_TEST");
        Epic epic1 = new Epic("Epic 1", "Desc 1", TaskStatus.IN_PROGRESS, Duration.ofSeconds(30), LocalDateTime.now());

        HttpRequest request1 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1))).build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        Epic epic2 = new Epic("Epic 2", "Desc 2", TaskStatus.NEW, Duration.ofSeconds(30), null);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2))).build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder().uri(url)
                .GET().build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());


        assertEquals(response3.statusCode(), 200);
        System.out.println(response3.body());
        System.out.println("*********************************************************");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        System.out.println("*********************************************************");
        System.out.println("ADD_EPIC_HTTP_TEST");
        Epic epic = new Epic("Epic 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getEpics().size());
        System.out.println("*********************************************************");
    }

    @Test
    public void getEpicById() throws IOException, InterruptedException {
        System.out.println("*******************************");
        System.out.println("GET_EPIC_BY_ID_HTTP_TEST");
        Epic epic = new Epic("Epic 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int id = epic.getId();
        URI getId1 = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest requestGet = HttpRequest.newBuilder().uri(getId1)
                .GET().build();
        HttpResponse<String> responseGet = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());


        System.out.println(responseGet.body());

        URI getId10 = URI.create("http://localhost:8080/epics/10000");
        HttpRequest requestGetWrong = HttpRequest.newBuilder().uri(getId10)
                .GET().build();
        HttpResponse<String> responseGetWrong = httpClient.send(requestGetWrong, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGetWrong.statusCode());
        System.out.println("*******************************");
    }

    @Test
    public void testDeleteEpicWithSubTask() throws IOException, InterruptedException {
        System.out.println("*******************************");
        System.out.println("DELETE_EPIC_WITH_SUBTASK_TEST");
        Epic epic = new Epic("Epic 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        SubTask subTask = new SubTask("Sub Task 1", "Desc 1", TaskStatus.IN_PROGRESS,
                Duration.ofSeconds(30), LocalDateTime.now(), epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int id = epic.getId();
        URI temp = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestSubTask = HttpRequest.newBuilder().uri(temp)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> responseSub = httpClient.send(requestSubTask, HttpResponse.BodyHandlers.ofString());
        URI getId1 = URI.create("http://localhost:8080/epics/" + id);
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubTasks().size());
        HttpRequest requestDelete = HttpRequest.newBuilder().uri(getId1)
                .DELETE().build();
        HttpResponse<String> responseGet = httpClient.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubTasks().size());
        System.out.println("*******************************");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        System.out.println("*******************************");
        System.out.println("UPDATE_EPIC_HTTP_TEST");
        Epic epic = new Epic("Epic 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getEpics().size());
        int id = epic.getId();
        epic.setName("Epic 2");
        URI temp = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(temp)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> responseUpdate = httpClient.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getEpics().size());
        assertEquals("Epic 2", taskManager.getEpicById(id).getName());
        System.out.println("*******************************");
    }

    @Test
    public void testGetEpicSubTasks() throws IOException, InterruptedException {
        System.out.println("*******************************");
        System.out.println("EPIC_TEST_GET_SUB_TASKS_HTTP");
        Epic epic = new Epic("Epic 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        SubTask subTask = new SubTask("Sub Task 1", "Desc 1", TaskStatus.IN_PROGRESS,
                Duration.ofSeconds(30), LocalDateTime.now(), epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int id = epic.getId();
        URI temp = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestSubTask = HttpRequest.newBuilder().uri(temp)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> responseSub = httpClient.send(requestSubTask, HttpResponse.BodyHandlers.ofString());
        URI getId1 = URI.create("http://localhost:8080/epics/" + id + "/subtasks");
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubTasks().size());
        HttpRequest requestGET = HttpRequest.newBuilder().uri(getId1)
                .GET().build();
        HttpResponse<String> responseGet = httpClient.send(requestGET, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        System.out.println(responseGet.body());
        System.out.println("*****************************************");
    }

    @Test
    public void testTryToGetEpicSubTasksButNotFound() throws IOException, InterruptedException {
        System.out.println("***********************************************");
        System.out.println("TryToGetEpicSubTasksButNotFound_TEST_HTTP");
        Epic epic = new Epic("Epic 1", "Desc 1", TaskStatus.NEW, Duration.ofSeconds(30), LocalDateTime.now());
        SubTask subTask = new SubTask("Sub Task 1", "Desc 1", TaskStatus.IN_PROGRESS,
                Duration.ofSeconds(30), LocalDateTime.now(), epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int id = epic.getId();
        URI temp = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestSubTask = HttpRequest.newBuilder().uri(temp)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
        HttpResponse<String> responseSub = httpClient.send(requestSubTask, HttpResponse.BodyHandlers.ofString());
        URI getId1 = URI.create("http://localhost:8080/epics/" + (id + 1000) + "/subtasks");
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubTasks().size());
        HttpRequest requestGET = HttpRequest.newBuilder().uri(getId1)
                .GET().build();
        HttpResponse<String> responseGet = httpClient.send(requestGET, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseGet.statusCode());
        System.out.println("*******************************************");
    }

}
