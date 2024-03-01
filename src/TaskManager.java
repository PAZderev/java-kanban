import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasksEnums.TaskType;

import java.util.*;

public class TaskManager {

    //
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public Collection<SubTask> getSubTasks() {
        return subTasks.values();
    }

    public Collection<Epic> getEpics() {
        return epics.values();
    }

    public void removeAllTasksByType(TaskType taskType) {
        switch (taskType) {
            case TASK:
                tasks.clear();
                break;
            case EPIC:
                epics.clear();
                subTasks.clear(); // Удаляем subTasks, т.к. эпиков больше не существует
                break;
            case SUBTASK:
                for (SubTask subTask : getSubTasks()) { // Обновляем эпики, т.к. удалили subTasks
                    Epic epic = getEpicById(subTask.getEpicID());
                    if (epic != null) epic.removeSubTask(subTask);
                }
                subTasks.clear();
                break;
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }
    public Epic getEpicById(int id) {
        return epics.get(id);
    }
    public void createEpic (Epic epic) {
        Epic newEpic = new Epic(epic);
        epics.put(epic.getId(),newEpic);
        // Предполагается, что невозможно создать subTask с epicID, который еще не существует, поэтому проверки на это нет
    }
    public void createTask(Task task) {
        Task newTask = new Task(task);
        tasks.put(task.getId(), newTask);
    }
    public void createSubTask(SubTask subTask) {
        SubTask newSubTask = new SubTask(subTask);
        if (getEpicById(newSubTask.getEpicID()) != null) {
            getEpicById(newSubTask.getEpicID()).addSubTask(newSubTask); // Добавили subTask в эпик
        }
        subTasks.put(newSubTask.getId(), newSubTask);
    }
    public void updateTask(Task task) {
        createTask(task);
    }
    public void updateSubTask(SubTask subTask) {
        // Логика для обновления статуса эпика
        int oldEpicID = subTasks.get(subTask.getId()).getEpicID();
        Epic oldEpic = getEpicById(oldEpicID);
        SubTask oldSubTask = subTasks.get(subTask.getId());
        if (oldEpic != null) oldEpic.removeSubTask(subTasks.get(subTask.getId()));
        Epic newEpic = getEpicById(subTask.getEpicID());
        if (newEpic != null) newEpic.addSubTask(subTask);
        // статус эпика обновится, даже если сам EpicID не поменялся
        subTasks.put(subTask.getId(), subTask);
    }

    public void updateEpic(Epic epic) {


        ArrayList<SubTask> oldSubTasksTemp = getSubTasksByEpic(epic);
        HashSet<SubTask> newSubTasks = new HashSet<>(getSubTasksByEpic(epic));
        HashSet<SubTask> oldSubTasks = new HashSet<>(oldSubTasksTemp);
        oldSubTasks.removeAll(newSubTasks);
        for (SubTask subTask : oldSubTasks) { // Если каких-то subTasks не стало в новом эпике, то обнуляем их epicID
            subTask.setEpicID(0);
        }
        /* Предполагаем, что пользователь не может обновить эпик с новыми SubTasks,
         которых еще не существует в нашей базе, поэтому состояние эпика считаем уже правильно определённым
         */
        epics.put(epic.getId(), epic);

    }
    public void removeTaskByID(int id) {
        tasks.remove(id);
    }

    public void removeEpicByID(int id) {
        for (SubTask subTask : getSubTasksByEpic(getEpicById(id))) {
            removeSubTaskByID(subTask.getId());
        }
        epics.remove(id);
    }
    public void removeSubTaskByID(int id) {
        Epic epic = getEpicById(getSubTaskById(id).getEpicID());
        epic.removeSubTask(getSubTaskById(id));
        subTasks.remove(id);
    }
    public ArrayList<SubTask> getSubTasksByEpic(Epic epic) {
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        for (int id : epic.getSubTasks()) {
            subTasksByEpic.add(getSubTaskById(id));
        }
        return  subTasksByEpic;
    }
}
