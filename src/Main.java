import managers.classes.Managers;
import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskStatus;


public class Main {

    public static void main(String[] args) {
        // Доп. задание спринт №6
        Managers manager = new Managers();
        TaskManager inMemoryTaskManager = manager.getDefault();
        HistoryManager inMemoryHistoryManager = inMemoryTaskManager.getInMemoryHistoryManager();
        // Создание задач
        inMemoryTaskManager.createTask(new Task("Task 1", "Desc of Task 1", TaskStatus.NEW));
        inMemoryTaskManager.createTask(new Task("Task 2", "Desc of Task 2", TaskStatus.DONE));

        Epic epic1 = new Epic("Epic 1", "Desc of Epic 1", TaskStatus.DONE);
        inMemoryTaskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Sub Task1", "Desc of Sub Task1", TaskStatus.NEW, epic1.getId());
        inMemoryTaskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Sub Task2", "Desc of Sub Task2", TaskStatus.DONE, epic1.getId());
        inMemoryTaskManager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("Sub Task3", "Desc of Sub Task3", TaskStatus.DONE, epic1.getId());
        inMemoryTaskManager.createSubTask(subTask3);
        Epic epic2 = new Epic("Epic 2", "Desc of Epic 2", TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.createEpic(epic2);

        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getTaskById(2);
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getEpicById(epic2.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getSubTaskById(subTask1.getId());
        inMemoryTaskManager.getSubTaskById(subTask2.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getSubTaskById(subTask3.getId());
        System.out.println("Ожидаем Task1, Task2, Epic2, SubTask1, SubTask2, Epic1, SubTask3");
        System.out.println(inMemoryHistoryManager.getHistory());
        System.out.println("Ожидаем Task2, Epic2, SubTask1, SubTask2, Epic1, SubTask3");
        inMemoryTaskManager.removeTaskByID(1);
        System.out.println(inMemoryHistoryManager.getHistory());
        inMemoryTaskManager.removeEpicByID(epic1.getId());
        System.out.println("Ожидаем Task2,Epic2");
        System.out.println(inMemoryHistoryManager.getHistory());


    }
}
