package managers.classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskStatus;
import utils.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    public void initializeTaskManger() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager(File.createTempFile("test", ".txt").toPath());
    }

    @Test
    public void addTwoTask() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task1);
        Task task2 = new Task("Task2", "Desc2", TaskStatus.DONE);
        fileBackedTaskManager.createTask(task2);
        assertEquals(task1, fileBackedTaskManager.getTaskById(task1.getId()));
        assertEquals(fileBackedTaskManager.getTaskById(task2.getId()), task2);
    }

    @Test
    public void addEpic() {
        Epic epic1 = new Epic("Epic1", "Desc1", TaskStatus.DONE);
        fileBackedTaskManager.createEpic(epic1);
        assertEquals(epic1, fileBackedTaskManager.getEpicById(epic1.getId()));
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
    }

    @Test
    public void addTwoSubTasks() {
        Epic epic1 = new Epic("Epic1", "Desc1", TaskStatus.DONE);
        fileBackedTaskManager.createEpic(epic1);
        assertEquals(epic1, fileBackedTaskManager.getEpicById(epic1.getId()));
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
        SubTask subTask = new SubTask("Sub1", "Desc1", TaskStatus.DONE, epic1.getId());
        fileBackedTaskManager.createSubTask(subTask);
        assertEquals(subTask, fileBackedTaskManager.getSubTaskById(subTask.getId()));
        assertEquals(fileBackedTaskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.DONE); // Эпик должен сменить статус после добавления subTask
        assertNotNull(fileBackedTaskManager.getEpicById(epic1.getId()).getSubTasks());
        SubTask subTask1 = new SubTask("Sub2", "Desc2", TaskStatus.NEW, epic1.getId());
        fileBackedTaskManager.createSubTask(subTask1);
        assertEquals(fileBackedTaskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals(fileBackedTaskManager.getEpicById(epic1.getId()).getSubTasks().size(), 2);
    }

    @Test
    public void tryToCreateTasksWithEqualsIDs() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task1);
        int sizeAfterFirstAdd = fileBackedTaskManager.getTasks().size();
        Task task2 = new Task(task1);

        Task task3 = new Task("Task3", "Desc3", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task2);
        assertEquals(fileBackedTaskManager.getTasks().size(), sizeAfterFirstAdd);

    }

    @Test
    public void printTasks() {
        Task task1 = new Task("Name1", "Desc1", TaskStatus.NEW);
        fileBackedTaskManager.createTask(task1);
        System.out.println("TEST : printTasks // FILE MANAGER");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println("********************************************************");

    }

    @Test
    public void deleteTasksFromEpic() {
        Epic epic1 = new Epic("Epic1", "Desc1", TaskStatus.DONE);
        fileBackedTaskManager.createEpic(epic1);
        SubTask subTask = new SubTask("Sub1", "Desc1", TaskStatus.DONE, epic1.getId());
        fileBackedTaskManager.createSubTask(subTask);

        SubTask subTask1 = new SubTask("Sub2", "Desc2", TaskStatus.NEW, epic1.getId());
        fileBackedTaskManager.createSubTask(subTask1);
        System.out.println("TEST: updateEpic // FILE MANAGER, проверка статусов эпика реализована через assertEquals");
        System.out.println(fileBackedTaskManager.getSubTasksByEpic(fileBackedTaskManager.getEpicById(epic1.getId())));
        assertEquals(fileBackedTaskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.IN_PROGRESS);
        fileBackedTaskManager.removeSubTaskByID(subTask1.getId());
        System.out.println(fileBackedTaskManager.getSubTasksByEpic(fileBackedTaskManager.getEpicById(epic1.getId())));
        assertEquals(fileBackedTaskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.DONE);
        fileBackedTaskManager.removeSubTaskByID(subTask.getId());
        System.out.println(fileBackedTaskManager.getSubTasksByEpic(fileBackedTaskManager.getEpicById(epic1.getId())));
        assertEquals(fileBackedTaskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.NEW);
        try (BufferedReader bufferedReader = new BufferedReader
                (new FileReader(String.valueOf(fileBackedTaskManager.getMemoryFile()), StandardCharsets.UTF_8))) {
            System.out.println("Ожидаем в файле одну запись об эпике");
            while (bufferedReader.ready()) {
                System.out.println(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
        System.out.println("********************************************************");

    }

    @Test
    public void loadFromFileTest() {
        try (BufferedWriter bufferedWriter = new BufferedWriter
                (new FileWriter(String.valueOf(fileBackedTaskManager.getMemoryFile()), StandardCharsets.UTF_8))) {
            bufferedWriter.write(
                    "id,type,name,status,description,epic\n" +
                            "1,EPIC,Epic1,NEW,Desc1,\n" +
                            "2,TASK,Task1,IN_PROGRESS,Desc2,\n" +
                            "3,SUBTASK,SubTask1,IN_PROGRESS,Desc3,1"
            );
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(fileBackedTaskManager.getMemoryFile().toFile());
        assertEquals(TaskStatus.IN_PROGRESS, fileBackedTaskManager.getEpicById(1).getStatus());
        assertEquals(1, fileBackedTaskManager.getTasks().size());
        assertEquals(1, fileBackedTaskManager.getSubTasks().size());
        assertEquals(1, fileBackedTaskManager.getEpics().size());
        System.out.println("****************************************");
        System.out.println("TEST loadFromFileTest");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getSubTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println("****************************************");
    }


}

