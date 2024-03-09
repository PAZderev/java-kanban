package managers.interfaces;

import java.util.ArrayList;
import tasks.Task;
public interface HistoryManager {
    ArrayList<Task> getHistory();
    void add(Task task);
}
