package managers.interfaces;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasksEnums.TaskType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getTasks();

    Collection<SubTask> getSubTasks();

    Collection<Epic> getEpics();
    HistoryManager getInMemoryHistoryManager();

    void removeAllTasksByType(TaskType taskType);

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void createEpic(Epic epic);

    void createTask(Task task);

    void createSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void removeTaskByID(int id);

    void removeEpicByID(int id);

    void removeSubTaskByID(int id);

    List<SubTask> getSubTasksByEpic(Epic epic);
}
