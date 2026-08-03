package adt;

public class LinkedStack<T> implements StackInterface<T> {

    private class Node {
        T data;
        Node next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node topNode;
    private int numberOfEntries;

    public LinkedStack() {
        numberOfEntries = 0;
        topNode = null;
    }

    @Override
    public void push(T newEntry) {
        Node newNode = new Node(newEntry);
        newNode.next = topNode;
        topNode = newNode;
        numberOfEntries++;
    }

    @Override
    public T pop() {
        T topData = peek();
        if (!isEmpty()) {
            topNode = topNode.next;
            numberOfEntries--;
        }
        return topData;
    }

    @Override
    public T peek() {
        T topData = null;
        if (!isEmpty()) {
            topData = topNode.data;
        }
        return topData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Top -> ");
        Node currentNode = topNode;
        while (currentNode != null) {
            sb.append(currentNode.data).append(" -> ");
            currentNode = currentNode.next;
        }
        return sb.toString();
    }

    @Override
    public int size() {
        return numberOfEntries;
    }
    @Override
    public boolean isEmpty() {
        return topNode == null;
    }

    @Override
    public void clear() {
        topNode = null;
        numberOfEntries = 0;
    }
}
