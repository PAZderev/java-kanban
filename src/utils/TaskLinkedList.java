package utils;

import tasks.Task;

import java.util.ArrayList;

import java.util.List;

public class TaskLinkedList {
    private TaskNode head;
    private TaskNode tail;
    private int size;

    public TaskLinkedList() {

    }

    public TaskNode add(Task task) {
        if (task == null) {
            return null;
        }
        TaskNode last = tail;
        TaskNode newNode = new TaskNode(tail, task, null);
        tail = newNode;
        if (last == null) {
            head = newNode;
        } else {
            last.next = newNode;
        }
        size++;
        return newNode;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void removeByLink(TaskNode taskNode) {
        if (taskNode == null) {
            return;
        }

        TaskNode prev = taskNode.prev;
        TaskNode next = taskNode.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            taskNode.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            taskNode.next = null;
        }

        taskNode.value = null;
        size--;
    }

    public List<Task> linkedListToList() {
        List<Task> linkedListToList = new ArrayList<>();
        TaskNode currentNode = head;
        while (currentNode != null) {
            linkedListToList.add(currentNode.value);
            currentNode = currentNode.next;
        }
        return linkedListToList;
    }

    @Override
    public String toString() {
        return linkedListToList().toString();
    }
}
