package Tasks;

import TasksEnums.TaskStatus;
import TasksEnums.TaskType;
public class SubTask extends Task {
    private final TaskType taskType = TaskType.SUBTASK;

    private int epicID;
    public SubTask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public SubTask(SubTask subTask) { // Дополнительный конструктор для создания копий без обновления id
        super(subTask);
        this.epicID = subTask.getEpicID();
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return  "SubTask{" +
                "taskType=" + taskType +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicID=" + epicID +
                '}';
    }
}
