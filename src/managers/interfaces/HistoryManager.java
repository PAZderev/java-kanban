package managers.interfaces;


import tasks.Task;
import utils.TaskLinkedList;

public interface HistoryManager {
    TaskLinkedList getHistory();

    void add(Task task);

    void remove(int id);
}
