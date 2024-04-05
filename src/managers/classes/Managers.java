package managers.classes;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;

public class Managers {
    private TaskManager inMemoryTaskManager;

    public TaskManager getDefault() {
        if (inMemoryTaskManager == null) {
            inMemoryTaskManager = new InMemoryTaskManager();
        }
        return inMemoryTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
