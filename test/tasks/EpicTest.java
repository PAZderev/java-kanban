package tasks;

import org.junit.jupiter.api.Test;
import utils.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void epicEquals() {
        Epic epic1 = new Epic("Name1", "desc1", TaskStatus.DONE, Duration.ofSeconds(90), LocalDateTime.now());
        Epic epic2 = new Epic(epic1);
        assertEquals(epic2, epic1);
    }

    @Test
    public void epicAllTasksNew() {
        Epic epic1 = new Epic("Epic1", "desc1", TaskStatus.DONE, Duration.ofSeconds(90), LocalDateTime.now());
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
        epic1.addSubTask(new SubTask("Sub1", "desc1", TaskStatus.NEW,
                Duration.ofSeconds(10), LocalDateTime.of(2024, 1, 1, 0, 0, 0), epic1.getId()));
        epic1.addSubTask(new SubTask("Sub2", "Desc2", TaskStatus.NEW,
                Duration.ofSeconds(20), LocalDateTime.of(2024, 2, 1, 0, 0, 0), epic1.getId()));
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2024, 2, 1, 0, 0, 20));
        assertEquals(epic1.getStartTime(), LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        assertEquals(epic1.getDuration(), Duration.ofSeconds(30));
    }

    @Test
    public void epicAllTasksDone() {
        Epic epic1 = new Epic("Epic1", "desc1", TaskStatus.DONE, Duration.ofSeconds(90), LocalDateTime.now());
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
        epic1.addSubTask(new SubTask("Sub1", "desc1", TaskStatus.DONE,
                Duration.ofSeconds(10), LocalDateTime.of(2024, 1, 1, 0, 0, 0), epic1.getId()));
        epic1.addSubTask(new SubTask("Sub2", "Desc2", TaskStatus.DONE,
                Duration.ofSeconds(20), LocalDateTime.of(2024, 2, 1, 0, 0, 0), epic1.getId()));
        assertEquals(epic1.getStatus(), TaskStatus.DONE);
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2024, 2, 1, 0, 0, 20));
        assertEquals(epic1.getStartTime(), LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        assertEquals(epic1.getDuration(), Duration.ofSeconds(30));
    }

    @Test
    public void epicTasksDoneAndNew() {
        Epic epic1 = new Epic("Epic1", "desc1", TaskStatus.DONE, Duration.ofSeconds(90), LocalDateTime.now());
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
        epic1.addSubTask(new SubTask("Sub1", "desc1", TaskStatus.NEW,
                Duration.ofSeconds(10), LocalDateTime.of(2024, 1, 1, 0, 0, 0), epic1.getId()));
        epic1.addSubTask(new SubTask("Sub2", "Desc2", TaskStatus.DONE,
                Duration.ofSeconds(20), LocalDateTime.of(2024, 2, 1, 0, 0, 0), epic1.getId()));
        assertEquals(epic1.getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2024, 2, 1, 0, 0, 20));
        assertEquals(epic1.getStartTime(), LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        assertEquals(epic1.getDuration(), Duration.ofSeconds(30));
    }

    @Test
    public void epicTasksInProgress() {
        Epic epic1 = new Epic("Epic1", "desc1", TaskStatus.IN_PROGRESS, Duration.ofSeconds(90), LocalDateTime.now());
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
        epic1.addSubTask(new SubTask("Sub1", "desc1", TaskStatus.IN_PROGRESS,
                Duration.ofSeconds(10), LocalDateTime.of(2024, 1, 1, 0, 0, 0), epic1.getId()));
        epic1.addSubTask(new SubTask("Sub2", "Desc2", TaskStatus.IN_PROGRESS,
                Duration.ofSeconds(20), LocalDateTime.of(2024, 2, 1, 0, 0, 0), epic1.getId()));
        assertEquals(epic1.getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals(epic1.getEndTime(), LocalDateTime.of(2024, 2, 1, 0, 0, 20));
        assertEquals(epic1.getStartTime(), LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        assertEquals(epic1.getDuration(), Duration.ofSeconds(30));
    }
}