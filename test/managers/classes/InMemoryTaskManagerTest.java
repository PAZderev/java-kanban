package managers.classes;

import org.junit.jupiter.api.BeforeEach;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    public void initializeTaskManager() {
        Managers managers = new Managers();
        this.taskManager = (InMemoryTaskManager) managers.getDefault();
    }
}