package adt;

public class ArrayQueue<T> implements QueueInterface<T> {
    private static final int DEFAULT_CAPACITY = 10;

    private T[] queue;
    private int frontIndex;
    private int backIndex;
    private int size;

    public ArrayQueue() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        @SuppressWarnings("unchecked")
        T[] tempQueue = (T[]) new Object[initialCapacity];
        queue = tempQueue;
        frontIndex = 0;
        backIndex = 0;
        size = 0;
    }

    @Override
    public void enqueue(T newEntry) {
        if (size == queue.length) {
            ensureCapacity();
        }
        queue[backIndex] = newEntry;
        backIndex = (backIndex + 1) % queue.length;
        size++;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T front = queue[frontIndex];
        queue[frontIndex] = null;
        frontIndex = (frontIndex + 1) % queue.length;
        size--;
        return front;
    }
    
    @Override
    public T peek() {
        return getFront();
    }

    @Override
    public T getFront() {
        if (isEmpty()) {
            return null;
        }
        return queue[frontIndex];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        while (!isEmpty()) {
            dequeue();
        }
        frontIndex = 0;
        backIndex = 0;
    }

    private void ensureCapacity() {
        int newLength = queue.length * 2;
        @SuppressWarnings("unchecked")
        T[] largerQueue = (T[]) new Object[newLength];

        for (int index = 0; index < size; index++) {
            largerQueue[index] = queue[(frontIndex + index) % queue.length];
        }

        queue = largerQueue;
        frontIndex = 0;
        backIndex = size;
    }
}