package managers.classes;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import utils.enums.TaskStatus;

import java.util.List;

class InMemoryHistoryManagerTest {
    /*
    По поводу предлагаемых тестов в 6 спринте: В моей реализации ID статически увеличивается, поэтому у меня не может
    быть ситуаций, когда старый удаленный id где-то сохранился в менеджере.
    Сеттеры в классах задач не влияют на работу менеджера, т.к. статус эпика и сабтаски вручную изменить нельзя, а только
    они могут оказать влияние на работу. (обновление их статусов реализовано через методы Update)
     */
    static TaskManager inMemoryTaskManager;

    @BeforeAll
    public static void initializeTaskManger() {
        Managers managers = new Managers();
        inMemoryTaskManager = managers.getDefault();
    }

    @Test
    void addAndGetHistory() {
        Task task = new Task("Task1", "Desc1", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        Epic epic = new Epic("Epic1", "Desc1", TaskStatus.NEW);
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