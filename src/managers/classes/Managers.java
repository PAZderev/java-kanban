package managers.classes;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;

public class Managers {
    private TaskManager inMemoryTaskManager;
    private static HistoryManager inMemoryHistoryManager;
    public TaskManager getDefault() {
        if (inMemoryTaskManager == null) {
            inMemoryTaskManager = new InMemoryTaskManager();
        }
        return inMemoryTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (inMemoryHistoryManager == null) {
            inMemoryHistoryManager = new InMemoryHistoryManager();
        }
        return inMemoryHistoryManager;
    }
}
