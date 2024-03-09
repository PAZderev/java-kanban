package tasks;

import org.junit.jupiter.api.Test;
import tasksEnums.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void subTaskEquals() {
        Epic epic1 = new Epic("Name1","desc1",TaskStatus.DONE);
        Epic epic2 = new Epic(epic1);
        assertEquals(epic2,epic1);
    }
    /* Из ТЗ: проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи.
    Данный тест бессмысленен, т.к. метод добавления subTask ожидает тип subTask,
    в случае передачи другого типа программа не скомпилируется
     */
}