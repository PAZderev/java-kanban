package managers.interfaces;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskType;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getTasks();

    Collection<SubTask> getSubTasks();

    Collection<Epic> getEpics();

    Collection<Task> getPrioritizedTasks();

    HistoryManager getInMemoryHistoryManager();

    void removeAllTasksByType(TaskType taskType);

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    boolean createEpic(Epic epic);

    boolean createTask(Task task);

    boolean createSubTask(SubTask subTask);

    boolean updateTask(Task task);

    boolean updateSubTask(SubTask subTask);

    boolean updateEpic(Epic epic);

    void removeTaskByID(int id);

    void removeEpicByID(int id);

    void removeSubTaskByID(int id);

    void clear();

    List<SubTask> getSubTasksByEpic(Epic epic);
}
