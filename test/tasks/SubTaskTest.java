package tasks;

import org.junit.jupiter.api.Test;
import utils.enums.TaskStatus;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    public void subTaskEquals() {
        SubTask subTask = new SubTask("Name1", "Desc1", TaskStatus.DONE, Duration.ofSeconds(30), null, 1);
        SubTask subTask1 = new SubTask(subTask);
        assertEquals(subTask, subTask1);
    }

    @Test
    public void makeSubTasksEpicItSelf() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SubTask("Name1", "Desc1", TaskStatus.DONE, Duration.ofSeconds(30), null, Task.getIdCounter() + 1); // SubTask будет иметь ид = текущее кол-во задач + 1
        });
    }
}