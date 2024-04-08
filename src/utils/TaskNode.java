package utils;

import tasks.Task;

public class TaskNode {
    Task value;
    TaskNode prev;
    TaskNode next;

    public TaskNode(TaskNode prev, Task value, TaskNode next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }
}
