package managers.classes;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    public abstract void initializeTaskManager(); // Абстрактный метод для инициализации taskManager

    @Test
    public void addTwoTasks() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Desc2", TaskStatus.DONE, Duration.ofSeconds(30), null);
        taskManager.createTask(task2);
        assertEquals(task1, taskManager.getTaskById(task1.getId()));
        assertEquals(task2, taskManager.getTaskById(task2.getId()));
    }

    @Test
    public void addEpic() {
        Epic epic1 = new Epic("Epic1", "Desc1", TaskStatus.DONE, Duration.ofSeconds(30), null);
        taskManager.createEpic(epic1);
        assertEquals(epic1, taskManager.getEpicById(epic1.getId()));
        assertEquals(TaskStatus.NEW, epic1.getStatus()); // Обратите внимание на статус
    }

    @Test
    public void addTwoSubTasks() {
        Epic epic1 = new Epic("Epic1", "Desc1", TaskStatus.DONE, Duration.ofSeconds(30), null);
        taskManager.createEpic(epic1);
        assertEquals(epic1, taskManager.getEpicById(epic1.getId()));
        assertEquals(epic1.getStatus(), TaskStatus.NEW);
        SubTask subTask = new SubTask("Sub1", "Desc1", TaskStatus.DONE, Duration.ofSeconds(30), null, epic1.getId());
        taskManager.createSubTask(subTask);
        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()));
        assertEquals(taskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.DONE); // Эпик должен сменить статус после добавления subTask
        assertNotNull(taskManager.getEpicById(epic1.getId()).getSubTasksStatuses());
        SubTask subTask1 = new SubTask("Sub2", "Desc2", TaskStatus.NEW, Duration.ofSeconds(30), null, epic1.getId());
        taskManager.createSubTask(subTask1);
        assertEquals(taskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals(taskManager.getEpicById(epic1.getId()).getSubTasksStatuses().size(), 2);
    }

    @Test
    public void deleteTasksFromEpic() {
        Epic epic1 = new Epic("Epic1", "Desc1", TaskStatus.DONE, Duration.ofSeconds(30), null);
        taskManager.createEpic(epic1);
        SubTask subTask = new SubTask("Sub1", "Desc1", TaskStatus.DONE, Duration.ofSeconds(30), null, epic1.getId());
        taskManager.createSubTask(subTask);
        SubTask subTask1 = new SubTask("Sub2", "Desc2", TaskStatus.NEW, Duration.ofSeconds(30), null, epic1.getId());
        taskManager.createSubTask(subTask1);
        System.out.println("TEST: deleteTasksFromEpic, проверка статусов эпика реализована через assertEquals");
        System.out.println(taskManager.getSubTasksByEpic(taskManager.getEpicById(epic1.getId())));
        assertEquals(taskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.IN_PROGRESS);
        taskManager.removeSubTaskByID(subTask1.getId());
        System.out.println(taskManager.getSubTasksByEpic(taskManager.getEpicById(epic1.getId())));
        assertEquals(taskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.DONE);
        taskManager.removeSubTaskByID(subTask.getId());
        System.out.println(taskManager.getSubTasksByEpic(taskManager.getEpicById(epic1.getId())));
        assertEquals(taskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.NEW);
        System.out.println("********************************************************");
    }

    @Test
    public void tryToCreateTasksWithEqualsIDs() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        taskManager.createTask(task1);
        int sizeAfterFirstAdd = taskManager.getTasks().size();
        Task task2 = new Task(task1);

        Task task3 = new Task("Task3", "Desc3", TaskStatus.NEW, Duration.ofSeconds(30), null);
        taskManager.createTask(task2);
        assertEquals(taskManager.getTasks().size(), sizeAfterFirstAdd);

    }

    @Test
    public void printTasks() {
        Task task1 = new Task("Name1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(30), null);
        taskManager.createTask(task1);
        System.out.println("TEST : printTasks");
        System.out.println(taskManager.getTasks());
        System.out.println("********************************************************");

    }

    @Test
    public void checkIntervals() {
        LocalDateTime firstMoment = LocalDateTime.now();
        Task task1 = new Task("Name1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(300), firstMoment);
        taskManager.createTask(task1);
        int sizeAfterFirstAdd = taskManager.getTasks().size();
        Task task2 = new Task("Name2", "Desc2", TaskStatus.NEW,
                Duration.ofSeconds(10), firstMoment.plus(Duration.ofSeconds(10)));
        assertEquals(sizeAfterFirstAdd, taskManager.getTasks().size());
        Task task3 = new Task("Name3", "Desc3", TaskStatus.NEW,
                Duration.ofSeconds(30), firstMoment.plus(task1.getDuration()));
        assertEquals(sizeAfterFirstAdd, taskManager.getTasks().size());
        Task task4 = new Task("Name4", "Desc4", TaskStatus.NEW,
                Duration.ofSeconds(30), firstMoment.minus(Duration.ofSeconds(30)));
        assertEquals(sizeAfterFirstAdd, taskManager.getTasks().size());
    }

    @Test
    public void checkIntervalsWithEqualsStartTime() {
        LocalDateTime firstMoment = LocalDateTime.now();
        Task task1 = new Task("Name1", "Desc1", TaskStatus.NEW, Duration.ofSeconds(300), firstMoment);
        taskManager.createTask(task1);
        int sizeAfterFirstAdd = taskManager.getTasks().size();
        Task task2 = new Task("Name2", "Desc2", TaskStatus.NEW,
                Duration.ofSeconds(10), firstMoment);
        assertEquals(sizeAfterFirstAdd, taskManager.getTasks().size());
    }

    @Test
    public void epicIntervals() {
        Epic epic1 = new Epic("Epic1", "desc1", TaskStatus.IN_PROGRESS, Duration.ofSeconds(90), LocalDateTime.now());
        taskManager.createEpic(epic1);
        assertEquals(taskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.NEW);
        taskManager.createSubTask(new SubTask("Sub1", "desc1", TaskStatus.DONE,
                Duration.ofSeconds(10), LocalDateTime.of(2024, 1, 1, 0, 0, 0), epic1.getId()));
        taskManager.createSubTask(new SubTask("Sub2", "Desc2", TaskStatus.NEW,
                Duration.ofSeconds(20), LocalDateTime.of(2024, 2, 1, 0, 0, 0), epic1.getId()));
        taskManager.createSubTask(new SubTask("Sub3", "desc3", TaskStatus.NEW,
                Duration.ofSeconds(5), LocalDateTime.of(2024, 1, 1, 0, 0, 1), epic1.getId()));
        assertEquals(taskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals(taskManager.getEpicById(epic1.getId()).getEndTime(),
                LocalDateTime.of(2024, 2, 1, 0, 0, 20));
        assertEquals(taskManager.getEpicById(epic1.getId()).getStartTime(),
                LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        assertEquals(taskManager.getEpicById(epic1.getId()).getDuration(), Duration.ofSeconds(30));
    }

}
