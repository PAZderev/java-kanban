package managers.classes;

import managers.interfaces.HistoryManager;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasksEnums.TaskType;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {


    //
    private final Map<Integer, Task> tasks;
    private final Map<Integer, SubTask> subTasks;
    private final Map<Integer, Epic> epics;

    HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
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

    public HistoryManager getInMemoryHistoryManager() {
        return historyManager;
    }

    @Override
    public void removeAllTasksByType(TaskType taskType) {
        switch (taskType) {
            case TASK:
                for (int id : tasks.keySet()) { // Удаляем задачи из истории
                    historyManager.remove(id);
                }
                tasks.clear();
                break;
            case EPIC:
                for (int id : epics.keySet()) {
                    historyManager.remove(id);
                }
                epics.clear();
                for (int id : subTasks.keySet()) {
                    historyManager.remove(id);
                }
                subTasks.clear(); // Удаляем subTasks, т.к. эпиков больше не существует
                break;
            case SUBTASK:
                for (SubTask subTask : getSubTasks()) { // Обновляем эпики, т.к. удалили subTasks
                    Epic epic = getEpicById(subTask.getEpicID());
                    if (epic != null) epic.removeSubTask(subTask);
                }
                for (int id : subTasks.keySet()) {
                    historyManager.remove(id);
                }
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
        // Проверка на то, что пользователь отправил задачу с верным ID (с последним созданным в системе)
        if (epic.getId() != Task.getIdCounter()) return;
        Epic newEpic = new Epic(epic);
        epics.put(epic.getId(), newEpic);
        // Предполагается, что невозможно создать subTask с epicID, который еще не существует, поэтому проверки
        // на автоматическое добавление существующих subTask нет
    }

    @Override
    public void createTask(Task task) {
        if (task.getId() != Task.getIdCounter()) return;
        Task newTask = new Task(task);
        tasks.put(task.getId(), newTask);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (subTask.getId() != Task.getIdCounter()) return;
        SubTask newSubTask = new SubTask(subTask);
        if (getEpicById(newSubTask.getEpicID()) != null) {
            getEpicById(newSubTask.getEpicID()).addSubTask(newSubTask); // Добавили subTask в эпик
        }
        subTasks.put(newSubTask.getId(), newSubTask);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), new Task(task));
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        // Логика для обновления статуса эпика
        int oldEpicID = subTasks.get(subTask.getId()).getEpicID();
        Epic oldEpic = getEpicById(oldEpicID);
        SubTask oldSubTask = subTasks.get(subTask.getId());
        if (oldEpic != null) oldEpic.removeSubTask(oldSubTask);
        Epic newEpic = getEpicById(subTask.getEpicID());
        if (newEpic != null) newEpic.addSubTask(subTask);
        // статус эпика обновится, даже если сам EpicID не поменялся
        subTasks.put(subTask.getId(), new SubTask(subTask));
    }

    @Override
    public void updateEpic(Epic epic) {


        List<SubTask> oldSubTasksTemp = getSubTasksByEpic(getEpicById(epic.getId()));
        HashSet<SubTask> newSubTasks = new HashSet<>(getSubTasksByEpic(epic));
        HashSet<SubTask> oldSubTasks = new HashSet<>(oldSubTasksTemp);
        oldSubTasks.removeAll(newSubTasks);
        for (SubTask subTask : oldSubTasks) { // Если каких-то subTasks не стало в новом эпике, то удаляем их
            removeSubTaskByID(subTask.getId());
        }
        /* Предполагаем, что пользователь не может обновить эпик с новыми SubTasks,
         которых еще не существует в нашей базе, поэтому состояние эпика считаем уже правильно определённым
         */
        Epic newEpic = new Epic(epic);
        for (SubTask subTask : newSubTasks) {
            newEpic.addSubTask(subTask);
        }
        epics.put(epic.getId(), newEpic);
    }

    @Override
    public void removeTaskByID(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpicByID(int id) {
        for (SubTask subTask : getSubTasksByEpic(getEpicById(id))) {
            removeSubTaskByID(subTask.getId());
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubTaskByID(int id) {
        Epic epic = getEpicById(getSubTaskById(id).getEpicID());
        epic.removeSubTask(getSubTaskById(id));
        historyManager.remove(id);
        subTasks.remove(id);
    }

    @Override
    public List<SubTask> getSubTasksByEpic(Epic epic) {
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        for (int id : epic.getSubTasks()) {
            subTasksByEpic.add(getSubTaskById(id));
        }
        return subTasksByEpic;
    }
}
