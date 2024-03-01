import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasksEnums.TaskStatus;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        // Тестовые данные
        // Создаем таск
        Task task = new Task("Task1","Task1Desc",TaskStatus.NEW);
        taskManager.createTask(task);

        // Создаем таск
        task = new Task("Task2","Task2Desc",TaskStatus.DONE);
        taskManager.createTask(task);

        // Создаем эпик, даем статус не NEW, но должен автоматически поменять на NEW
        Epic epic = new Epic("Epic1","Epic1Desc",TaskStatus.IN_PROGRESS);
        taskManager.createEpic(epic);


        System.out.println(taskManager.getEpicById(epic.getId()).getStatus()); // Ожидается NEW
        System.out.println("*************************************************************************");

        // создаем подзадачи, привязываем к эпику
        SubTask subTask = new SubTask("Sub1","Sub1Desc",TaskStatus.DONE,3);
        taskManager.createSubTask(subTask);
        // одна подзадача в статусе DONE, ожидаем DONE
        System.out.println(taskManager.getEpicById(epic.getId()).getStatus()); // Ожидается DONE
        System.out.println("*************************************************************************");

        // привязываем вторую подзадачу в статусе NEW, ожидаем эпик в прогрессе
        subTask = new SubTask("Sub2","Sub2Desc",TaskStatus.NEW,3);
        taskManager.createSubTask(subTask);


        System.out.println(taskManager.getEpicById(epic.getId()).getStatus()); // Ожидается IN_PROGRESS
        System.out.println("*************************************************************************");

        // создали второй эпик, ожидаем NEW
        Epic epic1 = new Epic("Epic2","Epic2Desc",TaskStatus.DONE);
        taskManager.createEpic(epic1);

        // привязали задачу в прогрессе к эпику №2, ожидаем статус в прогрессе
        subTask = new SubTask("Sub3","Sub3Desc",TaskStatus.IN_PROGRESS,6);
        taskManager.createSubTask(subTask);

        System.out.println(taskManager.getEpicById(epic1.getId()).getStatus()); // Ожидается IN_PROGRESS

        System.out.println("Print ALL Part");


        System.out.println("*************************************************************************");

        System.out.println(taskManager.getTasks());
        System.out.println("*************************************************************************");

        System.out.println(taskManager.getEpics());
        System.out.println("*************************************************************************");

        System.out.println(taskManager.getSubTasks());
        System.out.println("*************************************************************************");

        System.out.println(taskManager.getSubTasksByEpic(taskManager.getEpicById(epic.getId()))); // Принтим сабтаск первого эпика
        System.out.println("*************************************************************************");

        System.out.println("Update Part");
        System.out.println(taskManager.getTaskById(task.getId()));
        task.setName("UpdateName"); // Меняем имя для второго таска.
        // Лучше не делать явную смену имени у переменной,
        // а создавать новый объект копию, т.к. явную смену TaskManager не поддерживает.
        // Но т.к. task не привязан ни к какому методу, то здесь все будет ок.
        taskManager.updateTask(task);
        System.out.println(taskManager.getTaskById(task.getId()));
        System.out.println("*************************************************************************");
        // Создадим копию subtask, т.к. теперь явная смена нарушит работу методов.
        System.out.println("Original: "  + taskManager.getSubTaskById(7));
        SubTask subTask7 = new SubTask(taskManager.getSubTaskById(7)); // В классе реализован конструктор, который сохраняет поля
        System.out.println("Copy: " + subTask7);
        // UPDATE задач возможен только через копии объектов, что логично, т.к. если будем править напрямую задачи,
        // то мы исправим задачи уже в хранилище taskManagera, а он об этом не узнает.
        System.out.println(taskManager.getEpicById(subTask7.getEpicID()));
        subTask7.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask7);
        System.out.println(subTask7);
        System.out.println(taskManager.getEpicById(subTask7.getEpicID())); // Ожидаем, что эпик сменит статус на DONE
        System.out.println("*************************************************************************");
        epic1 = new Epic(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        epic1.getSubTasks().clear();
        epic1.setName("UpdatedEpic");
        taskManager.updateEpic(epic1);
        System.out.println(taskManager.getEpicById(epic1.getId())); // Очистили эпик № 2 и поменяли название, используя копию.
        System.out.println("*************************************************************************");
        System.out.println("Delete part");
        System.out.println(taskManager.getEpics());
        taskManager.removeSubTaskByID(4);
        System.out.println(taskManager.getEpics()); // Убрали в первом эпике одну из двух подзадач. Ожидаем смену статуса на NEW
        System.out.println("*************************************************************************");
        System.out.println(taskManager.getEpics());
        taskManager.removeEpicByID(3);
        System.out.println(taskManager.getEpics()); // Удалили первый эпик
        System.out.println("*************************************************************************");
        System.out.println(taskManager.getTasks());
        taskManager.removeTaskByID(1);
        System.out.println(taskManager.getTasks()); // Удалили первый таск
        System.out.println("*******f******************************************************************");
        
    }
}
