package managers.classes;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    final static int MAX_HISTORY_SIZE = 10;
    private List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() < MAX_HISTORY_SIZE) {

            history.add(task);
        }
        else {
            history.removeFirst();
            history.add(task);
        }
    }
}
