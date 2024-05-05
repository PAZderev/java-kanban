package managers.classes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.enums.TaskStatus;
import utils.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    @BeforeEach
    public void initializeTaskManager() {
        try {
            this.taskManager = new FileBackedTaskManager(File.createTempFile("test", ".txt").toPath());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при инициализации FileBackedTaskManager");
        }
    }

    @Override
    @Test
    public void deleteTasksFromEpic() {
        super.deleteTasksFromEpic();
        try (BufferedReader bufferedReader = new BufferedReader
                (new FileReader(String.valueOf(taskManager.getMemoryFile()), StandardCharsets.UTF_8))) {
            System.out.println("Ожидаем в файле одну запись об эпике");
            while (bufferedReader.ready()) {
                System.out.println(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
        System.out.println("********************************************************");

    }

    @Test
    public void loadFromFileTest() {
        try (BufferedWriter bufferedWriter = new BufferedWriter
                (new FileWriter(String.valueOf(taskManager.getMemoryFile()), StandardCharsets.UTF_8))) {
            bufferedWriter.write(
                    "id,type,name,status,description,duration,startTime,epic\n" +
                            "1,EPIC,Epic1,NEW,Desc1,60,null,\n" +
                            "2,TASK,Task1,IN_PROGRESS,Desc2,60,null,\n" +
                            "3,SUBTASK,SubTask1,IN_PROGRESS,Desc3,60,null,1"
            );
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
        taskManager = FileBackedTaskManager.loadFromFile(taskManager.getMemoryFile().toFile());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
        assertEquals(1, taskManager.getTasks().size());
        assertEquals(1, taskManager.getSubTasks().size());
        assertEquals(1, taskManager.getEpics().size());
        System.out.println("****************************************");
        System.out.println("TEST loadFromFileTest");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getEpics()); // Дата будет не null, такова логика эпика
        System.out.println("****************************************");
    }

    @Test
    void testSaveThrowsException() {
        Path testPath = Paths.get("path/to/nonexistent/directory/file.txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(testPath);
        assertThrows(ManagerSaveException.class, manager::save);
    }


}

