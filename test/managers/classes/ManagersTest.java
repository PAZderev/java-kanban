package managers.classes;

import managers.interfaces.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        Managers managers = new Managers();
        TaskManager test = managers.getDefault();
        assertNotNull(test);
    }

    @Test
    void getDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
    }
}