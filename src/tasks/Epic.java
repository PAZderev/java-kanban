package tasks;

import utils.enums.TaskStatus;
import utils.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static managers.classes.FileBackedTaskManager.*;

public class Epic extends Task {
    private final TaskType taskType = TaskType.EPIC;
    private Map<Integer, TaskStatus> subTasksStatuses; // Подзадачи будем хранить в HashMap, где ключ - айдишник
    private HashMap<TaskStatus, Integer> tasksCounterByStatus; // Словарь для подсчета подзадач в определенном статусе
    private SortedMap<Task, Integer> subTasksOrderedByStartTime; // Словарь для быстрого расчета времени Epic`а
    private SortedMap<Task, Integer> subTasksOrderedByEndTime;
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, TaskStatus.NEW, Duration.ofSeconds(0), LocalDateTime.now());
        initializeMaps(startTime);
    }

    public Epic(Epic epic) { // Дополнительный конструктор для создания копий без обновления id
        super(epic);
        initializeMaps(epic.getStartTime());
    }

    protected Epic(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, status, description, duration, startTime);
        initializeMaps(startTime);
    }

    // Так как в каждом конструкторе идёт инициализация Map, то вынесем это в отдельный метод.
    private void initializeMaps(LocalDateTime startTime) {
        subTasksStatuses = new HashMap<>();
        tasksCounterByStatus = new HashMap<>();
        tasksCounterByStatus.put(TaskStatus.NEW, 0);
        tasksCounterByStatus.put(TaskStatus.DONE, 0);
        tasksCounterByStatus.put(TaskStatus.IN_PROGRESS, 0);
        subTasksOrderedByStartTime = new TreeMap<>((task1, task2) -> task1.getStartTime().compareTo(task2.getStartTime()));
        subTasksOrderedByEndTime = new TreeMap<>((task1, task2) -> task2.getEndTime().compareTo(task1.getEndTime()));
        endTime = startTime;
    }

    public void addSubTask(SubTask subTask) {
        subTasksStatuses.put(subTask.getId(), subTask.getStatus());
        updateTimingWithAdd(subTask);
        updateCounterAndStatus(subTask.getStatus(), "+");
    }

    public void removeSubTask(SubTask subTask) {
        subTasksStatuses.remove(subTask.getId());
        updateTimingWithRemove(subTask);
        updateCounterAndStatus(subTask.getStatus(), "-");
    }

    private void updateTimingWithAdd(SubTask subTask) {

        if (subTask.getStartTime() != null) {
            subTasksOrderedByStartTime.put(subTask, subTask.getId());
            subTasksOrderedByEndTime.put(subTask, subTask.getId());
        }
        setDuration(getDuration().plus(subTask.getDuration()));
        updateTimeBorders();
    }

    private void updateTimingWithRemove(SubTask subTask) {
        subTasksOrderedByStartTime.remove(subTask);
        subTasksOrderedByEndTime.remove(subTask);
        setDuration(getDuration().minus(subTask.getDuration()));
        updateTimeBorders();
    }

    private void updateTimeBorders() {
        setStartTime(subTasksOrderedByStartTime.isEmpty() ?
                LocalDateTime.now() : subTasksOrderedByStartTime.firstKey().getStartTime());
        endTime = subTasksOrderedByEndTime.isEmpty() ?
                getStartTime() : subTasksOrderedByEndTime.firstKey().getEndTime();
    }


    /*
    Метод получает на вход статус подзадачи, которую добавили/удалили и знак. Знак равен +, когда добавили и минус в обратном
    случае. Рассчитываем количество подзадач в статусах и на основе словаря tasksCounterByStatus определяем статус эпика.
     */
    private void updateCounterAndStatus(TaskStatus subTaskStatus, String sign) {
        int diff = sign.equals("+") ? 1 : -1;
        int count;
        switch (subTaskStatus) {
            case NEW:
                count = tasksCounterByStatus.get(TaskStatus.NEW);
                tasksCounterByStatus.put(TaskStatus.NEW, count + diff);
                break;
            case IN_PROGRESS:
                count = tasksCounterByStatus.get(TaskStatus.IN_PROGRESS);
                tasksCounterByStatus.put(TaskStatus.IN_PROGRESS, count + diff);
                break;
            case DONE:
                count = tasksCounterByStatus.get(TaskStatus.DONE);
                tasksCounterByStatus.put(TaskStatus.DONE, count + diff);
                break;
        }
        this.getStatus(); // обновили статус эпика
    }


    public Set<Integer> getSubTasksStatuses() {
        return subTasksStatuses.keySet();
    }

    @Override
    public TaskStatus getStatus() {
        if (tasksCounterByStatus.get(TaskStatus.DONE) == subTasksStatuses.size() && !subTasksStatuses.isEmpty()) {
            this.setStatus(TaskStatus.DONE);
            return TaskStatus.DONE;
        }
        if (subTasksStatuses.isEmpty() || tasksCounterByStatus.get(TaskStatus.NEW) == subTasksStatuses.size()) {
            this.setStatus(TaskStatus.NEW);
            return TaskStatus.NEW;
        }
        this.setStatus(TaskStatus.IN_PROGRESS);
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public static Epic fromString(String value) {
        String[] params = value.split(",");
        Task.increaseIdCounter();
        LocalDateTime time = params[TASK_START_TIME].equals("null") ? null : LocalDateTime.parse(params[TASK_START_TIME]);
        return new Epic(Integer.parseInt(params[TASK_ID]), params[TASK_NAME], TaskStatus.valueOf(params[TASK_STATUS]), params[TASK_DESCRIPTION],
                Duration.ofSeconds(0), time);
    }

    public HashMap<TaskStatus, Integer> getTasksCounterByStatus() {
        return tasksCounterByStatus;
    }

    @Override
    public String toString() {
        String time = getStartTime() == null ? "null" : getStartTime().toString();
        return String.format("%d,%s,%s,%s,%s,%d,%s,", getId(), taskType, getName(), getStatus(), getDescription(),
                ((getDuration().toSeconds() + getDuration().toSecondsPart()) / 60), time);
    }
}
