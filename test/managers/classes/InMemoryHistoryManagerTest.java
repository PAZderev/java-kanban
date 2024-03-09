package managers.classes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasksEnums.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    static InMemoryTaskManager inMemoryTaskManager;
    @BeforeAll
    public static void initializeTaskManger() {
        Managers managers = new Managers();
        inMemoryTaskManager = managers.getDefault();
    }
    @Test
    void addAndGetHistory() {
        Task task = new Task("Task1", "Desc1", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Epic epic = new Epic("Epic1","Desc1",TaskStatus.NEW);
        inMemoryTaskManager.createEpic(epic);
        for (int i = 0; i < 5;i++) {
            inMemoryTaskManager.getTaskById(task.getId());
            inMemoryTaskManager.getEpicById(epic.getId());
        }
        System.out.println("TEST : addAndGetHistory");
        ArrayList<Task> history = inMemoryTaskManager.getInMemoryHistoryManager().getHistory();
        System.out.println(history);
        inMemoryTaskManager.getEpicById(epic.getId());
        System.out.println(inMemoryTaskManager.getInMemoryHistoryManager().getHistory());
        System.out.println("********************************************************");

        // Видно, что истории не равны, когда размер превышает 10, удаляется первый элемент.
    }



}