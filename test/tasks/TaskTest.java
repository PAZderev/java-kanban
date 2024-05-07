package tasks;

import org.junit.jupiter.api.Test;
import utils.enums.TaskStatus;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testEquals() {
        Task task1 = new Task("Name1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        Task task2 = new Task(task1);
        assertEquals(task1, task2);
    }
}