package managers.classes;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskType;
import utils.exceptions.NotFoundEpicException;
import utils.exceptions.NotFoundException;

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
                            try {
                                Epic epic = getEpicById(subTask.getEpicID());
                                epic.removeSubTask(subTask);
                            } catch (NotFoundEpicException ignored) {

                            }
                            historyManager.remove(subTask.getId());
                        });
                subTasks.clear();
                break;
        }
    }

    @Override
    public Task getTaskById(int id) throws NotFoundException {
        if (!tasks.containsKey(id)) {
            throw new NotFoundException("Task with id " + id + " not found");
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            throw new NotFoundException("Subtask with id " + id + " not found");
        }
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            throw new NotFoundEpicException("Epic with id " + id + " not found");
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public boolean createEpic(Epic epic) {
        Epic newEpic = new Epic(epic);
        epics.put(epic.getId(), newEpic);
        return true;
    }

    @Override
    public boolean createTask(Task task) {
        Task newTask = new Task(task);
        boolean check = checkTime(task);
        if (check) {
            tasks.put(task.getId(), newTask);
        }
        return check;
    }

    @Override
    public boolean createSubTask(SubTask subTask) {
        SubTask newSubTask = new SubTask(subTask);
        boolean check = checkTime(newSubTask);
        if (check) {
            subTasks.put(newSubTask.getId(), newSubTask);
            try {
                Epic epic = getEpicById(newSubTask.getEpicID());
                epic.addSubTask(newSubTask); // Добавили subTask в эпик
            } catch (NotFoundEpicException e) {
                return true;
            }
        }
        return check;
    }

    @Override
    public boolean updateTask(Task task) {
        Task updatedTask = new Task(task);
        boolean check = checkTime(updatedTask);
        if (check) {
            tasks.put(task.getId(), updatedTask);
        }
        return check;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        SubTask updatedSubTask = new SubTask(subTask);
        boolean check = checkTime(updatedSubTask);
        if (check) {
            subTasks.put(subTask.getId(), updatedSubTask);
            try {
                // Логика для обновления статуса эпика
                int oldEpicID = subTasks.get(subTask.getId()).getEpicID();
                Epic oldEpic = getEpicById(oldEpicID);
                SubTask oldSubTask = subTasks.get(subTask.getId());
                oldEpic.removeSubTask(oldSubTask);
                Epic newEpic = getEpicById(subTask.getEpicID());
                newEpic.addSubTask(subTask);
                // статус эпика обновится, даже если сам EpicID не поменялся
            } catch (NotFoundEpicException e) {
                return true;
            }

        }
        return check;
    }

    @Override
    public boolean updateEpic(Epic epic) {


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
        return true;
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
        try {
            Epic epic = getEpicById(getSubTaskById(id).getEpicID());
            epic.removeSubTask(getSubTaskById(id));
        } catch (NotFoundEpicException ignored) {

        }
        historyManager.remove(id);
        prioritizedTasks.remove(subTasks.get(id));
        subTasks.remove(id);
    }

    @Override
    public void clear() {
        removeAllTasksByType(TaskType.TASK);
        removeAllTasksByType(TaskType.SUBTASK);
        removeAllTasksByType(TaskType.EPIC);
        historyManager.clearHistory();
        prioritizedTasks.clear();
    }

    @Override
    public List<SubTask> getSubTasksByEpic(Epic epic) {
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        epic.getSubTasksStatuses().stream()
                .forEach(id -> subTasksByEpic.add(getSubTaskById(id)));
        return subTasksByEpic;
    }
}
