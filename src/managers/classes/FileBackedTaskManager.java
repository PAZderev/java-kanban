package managers.classes;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskType;
import utils.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.Collection;


public class FileBackedTaskManager extends InMemoryTaskManager {
    public static final int MIN_PARAMS_LENGTH = 7;
    public static final int TASK_ID = 0;
    public static final int TASK_TYPE = 1;
    public static final int TASK_NAME = 2;
    public static final int TASK_STATUS = 3;
    public static final int TASK_DESCRIPTION = 4;
    public static final int TASK_DURATION = 5;
    public static final int TASK_START_TIME = 6;
    public static final int TASK_EPIC = 7;


    private final Path memoryFile;
    private boolean loadingMode = false;

    public FileBackedTaskManager(Path memoryFile) {
        super();
        if (!memoryFile.toFile().exists()) {
            try {
                Files.createFile(memoryFile);
            } catch (IOException e) {
                System.out.println("Произошла ошибка во время создания файла.");
            }
        }
        this.memoryFile = memoryFile;

    }

    public Path getMemoryFile() {
        return memoryFile;
    }

    public void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(memoryFile.toFile(), StandardCharsets.UTF_8))) {
            fileWriter.write("id,type,name,status,description,duration,startTime,epic");
            Stream.of(getTasks(), getSubTasks(), getEpics())
                    .flatMap(Collection::stream)  // Преобразуем Stream<Collection<Task>> в Stream<Task>
                    .forEach(task -> {
                        try {
                            fileWriter.write("\n" + task.toString());  // Записываем каждую задачу в файл
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при записи задачи в файл");
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка во время сохранения файла");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath());
        fileBackedTaskManager.changeLoadingMode();
        /* Необходимо, чтобы создание задач не обновляло файл
         во время работы метода loadFromFile */
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String headers = fileReader.readLine();
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                String[] params = line.split(",");
                if (params.length < MIN_PARAMS_LENGTH) { // Необходимо минимум 7 элементов для правильной десериализации
                    throw new ManagerSaveException("Ошибка во время чтения файла");
                }
                switch (TaskType.valueOf(params[1])) {
                    case SUBTASK:
                        fileBackedTaskManager.createSubTask(SubTask.fromString(line));
                        break;
                    case EPIC:
                        fileBackedTaskManager.createEpic(Epic.fromString(line));
                        break;
                    case TASK:
                        fileBackedTaskManager.createTask(Task.fromString(line));
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка во время чтения файла");
        }
        fileBackedTaskManager.changeLoadingMode();
        return fileBackedTaskManager;
    }

    private void changeLoadingMode() {
        this.loadingMode = !loadingMode;
    }

    @Override
    public void removeAllTasksByType(TaskType taskType) {
        super.removeAllTasksByType(taskType);
        save();
    }

    @Override
    public boolean createEpic(Epic epic) {
        boolean check = super.createEpic(epic);
        if (!loadingMode) {
            save();
        }
        return check;
    }

    @Override
    public boolean createTask(Task task) {
        boolean check = super.createTask(task);
        if (!loadingMode) {
            save();
        }
        return check;
    }

    @Override
    public boolean createSubTask(SubTask subTask) {
        boolean check = super.createSubTask(subTask);
        if (!loadingMode) {
            save();
        }
        return check;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean check = super.updateTask(task);
        save();
        return check;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        boolean check = super.updateSubTask(subTask);
        save();
        return check;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean check = super.updateEpic(epic);
        save();
        return check;
    }

    @Override
    public void removeTaskByID(int id) {
        super.removeTaskByID(id);
        save();
    }

    @Override
    public void removeEpicByID(int id) {
        super.removeEpicByID(id);
        save();
    }

    @Override
    public void removeSubTaskByID(int id) {
        super.removeSubTaskByID(id);
        save();
    }
}
