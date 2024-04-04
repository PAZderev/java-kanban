package managers.classes;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Task;
import utils.TaskLinkedList;
import utils.TaskNode;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    private final TaskLinkedList history;

    private final Map<Integer, TaskNode> idToTaskNode;

    public InMemoryHistoryManager() {
        this.history = new TaskLinkedList();
        this.idToTaskNode = new HashMap<>();
    }

    @Override
    public TaskLinkedList getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        history.removeByLink(idToTaskNode.get(task.getId())); // удаляем запись, если она есть
        TaskNode newNode = history.add(task);
        idToTaskNode.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        history.removeByLink(idToTaskNode.get(id));
        idToTaskNode.remove(id);
    }
}
