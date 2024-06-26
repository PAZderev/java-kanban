package managers.interfaces;


import tasks.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);

    void remove(int id);

    void clearHistory();
}
