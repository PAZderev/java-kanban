package managers.classes;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskType;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, SubTask> subTasks;
    private final Map<Integer, Epic> epics;
    private final NavigableMap<Task, Integer> prioritizedTasks;


    HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeMap<>((task1, task2) -> task1.getStartTime().compareTo(task2.getStartTime()));
    }

    @Override
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    @Override
    public Collection<SubTask> getSubTasks() {
        return subTasks.values();
    }

    @Override
    public Collection<Epic> getEpics() {
        return epics.values();
    }

    @Override
    public Collection<Task> getPrioritizedTasks() {
        return prioritizedTasks.keySet();
    }

    public HistoryManager getInMemoryHistoryManager() {
        return historyManager;
    }

    @Override
    public void removeAllTasksByType(TaskType taskType) {
        switch (taskType) {
            case TASK:
                tasks.keySet().stream()
                        .forEach(id -> historyManager.remove(id));
                tasks.clear();
                break;
            case EPIC:
                epics.keySet().stream()
                        .forEach(id -> historyManager.remove(id));
                epics.clear();
                subTasks.keySet().stream()
                        .forEach(id -> historyManager.remove(id));
                subTasks.clear();
                break;
            case SUBTASK:
                getSubTasks().stream()
                        .forEach(subTask -> {
                            Epic epic = getEpicById(subTask.getEpicID());
                            if (epic != null) {
                                epic.removeSubTask(subTask);
                            }
                            historyManager.remove(subTask.getId());
                        });
                subTasks.clear();
                break;
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void createEpic(Epic epic) {
        Epic newEpic = new Epic(epic);
        epics.put(epic.getId(), newEpic);
    }

    @Override
    public void createTask(Task task) {
        Task newTask = new Task(task);
        if (checkTime(task)) {
            tasks.put(task.getId(), newTask);
        }

    }

    @Override
    public void createSubTask(SubTask subTask) {
        SubTask newSubTask = new SubTask(subTask);
        if (checkTime(newSubTask)) {
            subTasks.put(newSubTask.getId(), newSubTask);
            if (getEpicById(newSubTask.getEpicID()) != null) {
                getEpicById(newSubTask.getEpicID()).addSubTask(newSubTask); // Добавили subTask в эпик
            }
        }

    }

    @Override
    public void updateTask(Task task) {
        Task updatedTask = new Task(task);
        if (checkTime(updatedTask)) {
            tasks.put(task.getId(), updatedTask);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask updatedSubTask = new SubTask(subTask);
        if (checkTime(updatedSubTask)) {
            // Логика для обновления статуса эпика
            int oldEpicID = subTasks.get(subTask.getId()).getEpicID();
            Epic oldEpic = getEpicById(oldEpicID);
            SubTask oldSubTask = subTasks.get(subTask.getId());
            if (oldEpic != null) oldEpic.removeSubTask(oldSubTask);
            Epic newEpic = getEpicById(subTask.getEpicID());
            if (newEpic != null) newEpic.addSubTask(subTask);
            // статус эпика обновится, даже если сам EpicID не поменялся

            subTasks.put(subTask.getId(), updatedSubTask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {


        List<SubTask> oldSubTasksTemp = getSubTasksByEpic(getEpicById(epic.getId()));
        HashSet<SubTask> newSubTasks = new HashSet<>(getSubTasksByEpic(epic));
        HashSet<SubTask> oldSubTasks = new HashSet<>(oldSubTasksTemp);
        oldSubTasks.removeAll(newSubTasks);
        oldSubTasks.stream() // Если каких-то subTasks не стало в новом эпике, то удаляем их
                .forEach(subTask -> removeSubTaskByID(subTask.getId()));

        /* Предполагаем, что пользователь не может обновить эпик с новыми SubTasks,
         которых еще не существует в нашей базе, поэтому состояние эпика считаем уже правильно определённым
         */
        Epic newEpic = new Epic(epic);
        newSubTasks.stream()
                .forEach(newEpic::addSubTask);
        epics.put(epic.getId(), newEpic);
    }

    private boolean checkTime(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }
        return updatePriority(task);
    }

    private boolean updatePriority(Task task) {
        if (prioritizedTasks.get(task) != null) {
            if (!prioritizedTasks.get(task).equals(task.getId())) { // Если с таким временем существует другая задача
                return false;
            }
        }
        prioritizedTasks.remove(task); // Если задача существует, но это та же самая

        Task lower = prioritizedTasks.lowerKey(task);
        Task higher = prioritizedTasks.higherKey(task);
        if ((lower == null || lower.getEndTime().isBefore(task.getStartTime())) &&
                (higher == null || higher.getStartTime().isAfter(task.getEndTime()))) {
            prioritizedTasks.put(task, task.getId());
            return true;
        }

        return false;
    }

    @Override
    public void removeTaskByID(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void removeEpicByID(int id) {
        getSubTasksByEpic(getEpicById(id)).stream()
                .forEach(subTask -> removeSubTaskByID(subTask.getId()));
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubTaskByID(int id) {
        Epic epic = getEpicById(getSubTaskById(id).getEpicID());
        epic.removeSubTask(getSubTaskById(id));
        historyManager.remove(id);
        prioritizedTasks.remove(subTasks.get(id));
        subTasks.remove(id);
    }

    @Override
    public List<SubTask> getSubTasksByEpic(Epic epic) {
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        epic.getSubTasksStatuses().stream()
                .forEach(id -> subTasksByEpic.add(getSubTaskById(id)));
        return subTasksByEpic;
    }
}
