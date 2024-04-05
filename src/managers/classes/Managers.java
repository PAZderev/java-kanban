package managers.classes;

import managers.interfaces.HistoryManager;

public class Managers {
    private InMemoryTaskManager inMemoryTaskManager;

    public InMemoryTaskManager getDefault() {
        if (inMemoryTaskManager == null) {
            inMemoryTaskManager = new InMemoryTaskManager();
        }
        return inMemoryTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
