package managers.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        Managers managers = new Managers();
        InMemoryTaskManager test = managers.getDefault();
        assertNotNull(test);
    }

    @Test
    void getDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
    }
}