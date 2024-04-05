package tasks;

import org.junit.jupiter.api.Test;
import tasksEnums.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testEquals() {
        Task task1 = new Task("Name1", "Desc1", TaskStatus.NEW);
        Task task2 = new Task(task1);
        assertEquals(task1, task2);
    }
}