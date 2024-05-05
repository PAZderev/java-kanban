package tasks;

import utils.enums.TaskStatus;
import utils.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

import static managers.classes.FileBackedTaskManager.*;

public class SubTask extends Task {
    private final TaskType taskType = TaskType.SUBTASK;
    private int epicID;

    public SubTask(String name, String description, TaskStatus status,
                   Duration duration, LocalDateTime startTime, int epicID) {
        super(name, description, status, duration, startTime);
        if (this.getId() == epicID) {
            throw new IllegalArgumentException("EpicID не может равняться SubTaskID");
        }
        this.epicID = epicID;
    }

    public SubTask(SubTask subTask) { // Дополнительный конструктор для создания копий без обновления id
        super(subTask);
        this.epicID = subTask.getEpicID();

    }

    protected SubTask(int id, String name, TaskStatus status, String description,
                      Duration duration, LocalDateTime startTime, int epicID) {
        super(id, name, status, description, duration, startTime);
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
        LocalDateTime time = params[TASK_START_TIME].equals("null") ? null : LocalDateTime.parse(params[TASK_START_TIME]);
        return new SubTask(Integer.parseInt(params[TASK_ID]), params[TASK_NAME],
                TaskStatus.valueOf(params[TASK_STATUS]), params[TASK_DESCRIPTION], Duration.ofSeconds(60 * Long.parseLong(params[TASK_DURATION])),
                time, Integer.parseInt(params[TASK_EPIC]));
    }

    @Override
    public String toString() {
        String time = getStartTime() == null ? "null" : getStartTime().toString();
        return String.format("%d,%s,%s,%s,%s,%d,%s,%d", getId(), taskType, getName(), getStatus(), getDescription(),
                ((getDuration().toSeconds() + getDuration().toSecondsPart()) / 60), time, epicID);
    }
}
