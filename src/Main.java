import managers.classes.InMemoryTaskManager;
import managers.interfaces.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasksEnums.TaskStatus;


public class Main {

    public static void main(String[] args) {







//        System.out.println("Update Part");
//        System.out.println(taskManager.getTaskById(task.getId()));
//        task.setName("UpdateName"); // Меняем имя для второго таска.
//        // Лучше не делать явную смену имени у переменной,
//        // а создавать новый объект копию, т.к. явную смену managers.managers.interfaces.TaskManager не поддерживает.
//        // Но т.к. task не привязан ни к какому методу, то здесь все будет ок.
//        taskManager.updateTask(task);
//        System.out.println(taskManager.getTaskById(task.getId()));
//        System.out.println("*************************************************************************");
//        // Создадим копию subtask, т.к. теперь явная смена нарушит работу методов.
//        System.out.println("Original: "  + taskManager.getSubTaskById(7));
//        SubTask subTask7 = new SubTask(taskManager.getSubTaskById(7)); // В классе реализован конструктор, который сохраняет поля
//        System.out.println("Copy: " + subTask7);
//        // UPDATE задач возможен только через копии объектов, что логично, т.к. если будем править напрямую задачи,
//        // то мы исправим задачи уже в хранилище taskManagera, а он об этом не узнает.
//        System.out.println(taskManager.getEpicById(subTask7.getEpicID()));
//        subTask7.setStatus(TaskStatus.DONE);
//        taskManager.updateSubTask(subTask7);
//        System.out.println(subTask7);
//        System.out.println(taskManager.getEpicById(subTask7.getEpicID())); // Ожидаем, что эпик сменит статус на DONE
//        System.out.println("*************************************************************************");
//        epic1 = new Epic(taskManager.getEpicById(epic1.getId()));
//        System.out.println(taskManager.getEpicById(epic1.getId()));
//        epic1.getSubTasks().clear();
//        epic1.setName("UpdatedEpic");
//        taskManager.updateEpic(epic1);
//        System.out.println(taskManager.getEpicById(epic1.getId())); // Очистили эпик № 2 и поменяли название, используя копию.
//        System.out.println("*************************************************************************");
//        System.out.println("Delete part");
//        System.out.println(taskManager.getEpics());
//        taskManager.removeSubTaskByID(4);
//        System.out.println(taskManager.getEpics()); // Убрали в первом эпике одну из двух подзадач. Ожидаем смену статуса на NEW
//        System.out.println("*************************************************************************");
//        System.out.println(taskManager.getEpics());
//        taskManager.removeEpicByID(3);
//        System.out.println(taskManager.getEpics()); // Удалили первый эпик
//        System.out.println("*************************************************************************");
//        System.out.println(taskManager.getTasks());
//        taskManager.removeTaskByID(1);
//        System.out.println(taskManager.getTasks()); // Удалили первый таск
//        System.out.println("*******f******************************************************************");
        
    }
}
