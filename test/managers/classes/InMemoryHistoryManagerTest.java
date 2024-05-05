package managers.classes;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


import tasks.Epic;
import tasks.Task;
import utils.enums.TaskStatus;

import java.time.Duration;
import java.util.List;

class InMemoryHistoryManagerTest {

    static TaskManager inMemoryTaskManager;

    @BeforeEach
    public void initializeTaskManger() {
        Managers managers = new Managers();
        inMemoryTaskManager = managers.getDefault();
        inMemoryTaskManager.getInMemoryHistoryManager().clearHistory();
    }

    @Test
    void emptyHistoryTest() {
        assertEquals(inMemoryTaskManager.getInMemoryHistoryManager().getHistory().size(), 0);
    }

    @Test
    void doubleCallTaskTest() {
        Task task = new Task("Task1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task.getId());
        assertEquals(inMemoryTaskManager.getInMemoryHistoryManager().getHistory().size(), 1);
    }

    @Test
    void removeFromHistoryTest() {
        Task task = new Task("Task1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.removeTaskByID(task.getId());
        assertEquals(inMemoryTaskManager.getInMemoryHistoryManager().getHistory().size(), 0);
    }

    @Test
    void addAndGetHistory() {
        Task task = new Task("Task1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        inMemoryTaskManager.createTask(task);
        Epic epic = new Epic("Epic1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        inMemoryTaskManager.createEpic(epic);
        for (int i = 0; i < 5; i++) {
            inMemoryTaskManager.getEpicById(epic.getId());
            inMemoryTaskManager.getTaskById(task.getId());
        }
        System.out.println("TEST : addAndGetHistory, ожидаем две задачи в истории, эпик первый");
        List<Task> history = inMemoryTaskManager.getInMemoryHistoryManager().getHistory();
        System.out.println(history);
        inMemoryTaskManager.getEpicById(epic.getId());
        System.out.println("Ожидаем две задачи в истории, эпик второй");
        System.out.println(inMemoryTaskManager.getInMemoryHistoryManager().getHistory());
        System.out.println("********************************************************");


    }


}