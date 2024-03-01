package tasks;

import tasksEnums.TaskStatus;
import tasksEnums.TaskType;

import java.util.HashMap;
import java.util.Set;

public class Epic extends Task {
    private final TaskType taskType = TaskType.EPIC;

    private final HashMap<Integer, TaskStatus> subTasks; // Подзадачи будем хранить в HashMap, где ключ - айдишник, а
    // значение - статус задачи. Статусы задачи понадобятся для определения статуса эпика в методе updateCounterAndStatus



    private final HashMap<TaskStatus, Integer> tasksCounterByStatus; // Словарь для подсчета подзадач в определенном статусе
    public Epic(String name, String description, TaskStatus status) {
        super(name, description, TaskStatus.NEW); // т.к. любой эпик сначала будет NEW
        subTasks = new HashMap<>();
        tasksCounterByStatus = new HashMap<>();
        tasksCounterByStatus.put(TaskStatus.NEW,0);
        tasksCounterByStatus.put(TaskStatus.DONE,0);
        tasksCounterByStatus.put(TaskStatus.IN_PROGRESS,0);
    }

    public Epic (Epic epic) { // Дополнительный конструктор для создания копий без обновления id
        super(epic);
        subTasks = new HashMap<>();
        tasksCounterByStatus = new HashMap<>();
        tasksCounterByStatus.put(TaskStatus.NEW,0);
        tasksCounterByStatus.put(TaskStatus.DONE,0);
        tasksCounterByStatus.put(TaskStatus.IN_PROGRESS,0);
    }

    public void addSubTask (SubTask subTask) { // Учитываем, что EpicID указан корректно
        subTasks.put(subTask.getId(),subTask.getStatus());
        updateCounterAndStatus(subTask.getStatus(),"+");
    }

    public void removeSubTask (SubTask subTask) {
        subTasks.remove(subTask.getId());
        updateCounterAndStatus(subTask.getStatus(),"-");
    }

    /*
    Метод получает на вход статус подзадачи, которую добавили/удалили и знак. Знак равен +, когда добавили и минус в обратном
    случае. Рассчитываем количество подзадач в статусах и на основе словаря tasksCounterByStatus определяем статус эпика.
     */
    private void updateCounterAndStatus (TaskStatus subTaskStatus, String sign) {
        int diff;
        if (sign.equals("+")) {
            diff = 1;
        } else diff = -1;
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


    public Set<Integer> getSubTasks() {
        return subTasks.keySet();
    }
    @Override
    public TaskStatus getStatus() {
        if (tasksCounterByStatus.get(TaskStatus.DONE) == subTasks.size() && !subTasks.isEmpty()) {
            this.setStatus(TaskStatus.DONE);
            return TaskStatus.DONE;
        }
        if (subTasks.isEmpty() || tasksCounterByStatus.get(TaskStatus.NEW) == subTasks.size()) {
            this.setStatus(TaskStatus.NEW);
            return TaskStatus.NEW;
        }
        this.setStatus(TaskStatus.IN_PROGRESS);
        return TaskStatus.IN_PROGRESS;
    }

    public HashMap<TaskStatus, Integer> getTasksCounterByStatus() {
        return tasksCounterByStatus;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "taskType=" + taskType +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subTasks=" + getSubTasks() +
                '}';
    }
}
