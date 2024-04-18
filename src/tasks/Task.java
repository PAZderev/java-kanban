package tasks;

import utils.enums.TaskStatus;
import utils.enums.TaskType;

import java.util.Objects;

public class Task {
    private final TaskType taskType = TaskType.TASK;
    private String name;
    private String description;
    private final int id;
    private static int idCounter;
    private TaskStatus status;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        idCounter++;
        id = idCounter;
    }

    public Task(Task task) {
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
        this.id = task.id;
    }

    protected Task(int id, String name, TaskStatus status, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public static void increaseIdCounter() {
        idCounter++;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public static Task fromString(String value) {
        String[] params = value.split(",");
        Task.increaseIdCounter();
        return new Task(Integer.parseInt(params[0]), params[2], TaskStatus.valueOf(params[3]), params[4]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,", id, taskType, name, status, description);
    }
}
