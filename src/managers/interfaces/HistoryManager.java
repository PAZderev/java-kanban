package managers.interfaces;

import java.util.List;
import tasks.Task;
public interface HistoryManager {
    List<Task> getHistory();
    void add(Task task);
}
