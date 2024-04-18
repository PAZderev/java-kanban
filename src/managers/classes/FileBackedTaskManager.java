package managers.classes;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.enums.TaskStatus;
import utils.enums.TaskType;
import utils.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {


    private final Path memoryFile;
    private boolean loadingMode = false;

    FileBackedTaskManager(Path memoryFile) {
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
            fileWriter.write("id,type,name,status,description,epic");
            for (Task task : getTasks()) {
                fileWriter.write("\n" + task.toString());
            }
            for (SubTask subTask : getSubTasks()) {
                fileWriter.write("\n" + subTask.toString());
            }
            for (Epic epic : getEpics()) {
                fileWriter.write("\n" + epic.toString());
            }
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
                if (params.length < 5) { // Необходимо минимум 5 элементов для правильной десериализации
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
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        if (!loadingMode) {
            save();
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        if (!loadingMode) {
            save();
        }
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        if (!loadingMode) {
            save();
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
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


    /* Доп задание, спринт №7
    В ходе выполнения задания возник вопрос, метод loadFromFile должен возвращать экземпляр класса,
    который ссылается на тот же самый файл, переданный в метод или должен создавать новый файл?
     (если должен новый, то в какой папке? В "домашней" (в которой находимся по дефолту)?
     У меня реализован первый вариант.
     */
    public static void main(String[] args) throws IOException {
        FileBackedTaskManager fileBackedTaskManager =
                new FileBackedTaskManager(File.createTempFile("main", ".txt").toPath());
        fileBackedTaskManager.createTask(new Task("Task 1", "Desc 1", TaskStatus.NEW));
        fileBackedTaskManager.createEpic(new Epic("Epic 1", "Desc 2", TaskStatus.IN_PROGRESS));
        fileBackedTaskManager.createSubTask(new SubTask("Sub Task 1", "Desc 3",
                TaskStatus.IN_PROGRESS, 2));
        fileBackedTaskManager.createTask(new Task("Task 2", "Desc 4", TaskStatus.DONE));
        fileBackedTaskManager.createEpic(new Epic("Epic 2", "Desc 5", TaskStatus.DONE));
        fileBackedTaskManager.createSubTask(new SubTask("Sub Task 2", "Desc 6",
                TaskStatus.DONE, 5));
        System.out.println("Данные менеджера созданного вручную");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getSubTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        FileBackedTaskManager fileBackedTaskManagerFromFile =
                FileBackedTaskManager.loadFromFile(fileBackedTaskManager.getMemoryFile().toFile());
        System.out.println();
        System.out.println("Данные менеджера созданного из файла");
        System.out.println(fileBackedTaskManagerFromFile.getTasks());
        System.out.println(fileBackedTaskManagerFromFile.getSubTasks());
        System.out.println(fileBackedTaskManagerFromFile.getEpics());

    }
}
