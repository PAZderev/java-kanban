package tasks;

import utils.enums.TaskStatus;
import utils.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static managers.classes.FileBackedTaskManager.*;

public class Task {
    private final TaskType taskType = TaskType.TASK;
    private String name;
    private String description;
    private final int id;
    private static int idCounter;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        idCounter++;
        id = idCounter;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Task task) {
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
        this.id = task.id;
        this.startTime = task.startTime;
        this.duration = task.duration;
    }

    protected Task(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }


    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plus(duration);
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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public static Task fromString(String value) {
        String[] params = value.split(",");
        Task.increaseIdCounter();
        LocalDateTime time = params[TASK_START_TIME].equals("null") ? null : LocalDateTime.parse(params[TASK_START_TIME]);
        return new Task(Integer.parseInt(params[TASK_ID]), params[TASK_NAME], TaskStatus.valueOf(params[TASK_STATUS]), params[TASK_DESCRIPTION],
                Duration.ofSeconds(Long.parseLong(params[TASK_DURATION]) * 60), time);
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
        String time = getStartTime() == null ? "null" : getStartTime().toString();
        return String.format("%d,%s,%s,%s,%s,%d,%s,", id, taskType, name, status, description,
                ((duration.toSeconds() + duration.toSecondsPart()) / 60), time);
    }
}
