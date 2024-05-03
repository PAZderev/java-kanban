package tasks;

import utils.enums.TaskStatus;
import utils.enums.TaskType;

public class SubTask extends Task {
    private final TaskType taskType = TaskType.SUBTASK;

    private int epicID;

    public SubTask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        if (this.getId() == epicID) {
            throw new IllegalArgumentException("EpicID не может равняться SubTaskID");
        }
        this.epicID = epicID;
    }

    public SubTask(SubTask subTask) { // Дополнительный конструктор для создания копий без обновления id
        super(subTask);
        this.epicID = subTask.getEpicID();

    }

    protected SubTask(int id, String name, TaskStatus status, String description, int epicID) {
        super(id, name, status, description);
        if (this.getId() == epicID) {
            throw new IllegalArgumentException("EpicID не может равняться SubTaskID");
        }
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public static SubTask fromString(String value) {
        String[] params = value.split(",");
        Task.increaseIdCounter();
        return new SubTask(Integer.parseInt(params[0]), params[2],
                TaskStatus.valueOf(params[3]), params[4], Integer.parseInt(params[5]));
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d", getId(), taskType, getName(), getStatus(), getDescription(), epicID);
    }
}
