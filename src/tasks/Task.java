package tasks;

import tasksEnums.TaskStatus;
import tasksEnums.TaskType;

import java.util.Objects;

public class Task {
    private final TaskType taskType = TaskType.TASK;
    private String name;
    private String description;
    private final int id;
    private static int idCounter;
    private TaskStatus status;
    public Task (String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        idCounter++;
        id = idCounter;
    }

    public Task(Task task) { // Дополнительный конструктор для создания копий без обновления id
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
        this.id = task.id;
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
        return "Task{" +
                "taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
