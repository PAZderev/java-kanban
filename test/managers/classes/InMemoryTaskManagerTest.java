package managers.classes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasksEnums.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager;
    @BeforeEach
    public void initializeTaskManger() {
        Managers managers = new Managers();
        inMemoryTaskManager = managers.getDefault();
    }

    @Test
    public void addTwoTask() {
        Task task1 = new Task("Task1","Desc1", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task1);
        Task task2 = new Task("Task2","Desc2", TaskStatus.DONE);
        inMemoryTaskManager.createTask(task2);
        assertEquals(task1, inMemoryTaskManager.getTaskById(task1.getId()));
        assertEquals(inMemoryTaskManager.getTaskById(task2.getId()),task2);
    }
    @Test
    public void addEpic() {
        Epic epic1 = new Epic("Epic1","Desc1",TaskStatus.DONE);
        inMemoryTaskManager.createEpic(epic1);
        assertEquals(epic1,inMemoryTaskManager.getEpicById(epic1.getId()));
        assertEquals(epic1.getStatus(),TaskStatus.NEW);
    }
    @Test
    public void addTwoSubTasks() {
        Epic epic1 = new Epic("Epic1","Desc1",TaskStatus.DONE);
        inMemoryTaskManager.createEpic(epic1);
        assertEquals(epic1,inMemoryTaskManager.getEpicById(epic1.getId()));
        assertEquals(epic1.getStatus(),TaskStatus.NEW);
        SubTask subTask = new SubTask("Sub1","Desc1",TaskStatus.DONE,epic1.getId());
        inMemoryTaskManager.createSubTask(subTask);
        assertEquals(subTask,inMemoryTaskManager.getSubTaskById(subTask.getId()));
        assertEquals(inMemoryTaskManager.getEpicById(epic1.getId()).getStatus(),TaskStatus.DONE); // Эпик должен сменить статус после добавления subTask
        assertNotNull(inMemoryTaskManager.getEpicById(epic1.getId()).getSubTasks());
        SubTask subTask1 = new SubTask("Sub2","Desc2",TaskStatus.NEW,epic1.getId());
        inMemoryTaskManager.createSubTask(subTask1);
        assertEquals(inMemoryTaskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.IN_PROGRESS);
        assertEquals(inMemoryTaskManager.getEpicById(epic1.getId()).getSubTasks().size(),2);
    }



    // Тест из ТЗ: проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    /*
    Вообще, моя программа не предполагает, что пользователь может задавать ID вручную, ID оно на то и ID,
     чтобы идентифицировать объекты. Но у меня есть конструктор, который не увеличивает ID,
      созданный для внутренней логики программы, чтобы taskManager не хранил в себе ссылки на подаваемые ему объекты,
       а создавал свои идентичные. Поэтому я проверю, что пользователь не может добавить задачу, если ее ID
        меньше ID последней созданной задачи
     */
    @Test
    public void tryToCreateTasksWithEqualsIDs() {
        Task task1 = new Task("Task1","Desc1",TaskStatus.NEW);
        inMemoryTaskManager.createTask(task1);
        int sizeAfterFirstAdd = inMemoryTaskManager.getTasks().size();
        Task task2 = new Task(task1);

        Task task3 = new Task("Task3","Desc3",TaskStatus.NEW);
        inMemoryTaskManager.createTask(task2);
        assertEquals(inMemoryTaskManager.getTasks().size(),sizeAfterFirstAdd);

    }

    @Test
    public void printTasks() {
        Task task1 = new Task("Name1","Desc1",TaskStatus.NEW);
        inMemoryTaskManager.createTask(task1);
        System.out.println("TEST : printTasks");
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println("********************************************************");

    }

    @Test
    public void deleteTasksFromEpic() {
        Epic epic1 = new Epic("Epic1","Desc1",TaskStatus.DONE);
        inMemoryTaskManager.createEpic(epic1);
        SubTask subTask = new SubTask("Sub1","Desc1",TaskStatus.DONE,epic1.getId());
        inMemoryTaskManager.createSubTask(subTask);

        SubTask subTask1 = new SubTask("Sub2","Desc2",TaskStatus.NEW,epic1.getId());
        inMemoryTaskManager.createSubTask(subTask1);
        System.out.println("TEST: updateEpic, проверка статусов эпика реализована через assertEquals");
        System.out.println(inMemoryTaskManager.getSubTasksByEpic(inMemoryTaskManager.getEpicById(epic1.getId())));
        assertEquals(inMemoryTaskManager.getEpicById(epic1.getId()).getStatus(), TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.removeSubTaskByID(subTask1.getId());
        System.out.println(inMemoryTaskManager.getSubTasksByEpic(inMemoryTaskManager.getEpicById(epic1.getId())));
        assertEquals(inMemoryTaskManager.getEpicById(epic1.getId()).getStatus(),TaskStatus.DONE);
        inMemoryTaskManager.removeSubTaskByID(subTask.getId());
        System.out.println(inMemoryTaskManager.getSubTasksByEpic(inMemoryTaskManager.getEpicById(epic1.getId())));
        assertEquals(inMemoryTaskManager.getEpicById(epic1.getId()).getStatus(),TaskStatus.NEW);
        System.out.println("********************************************************");
    }



}