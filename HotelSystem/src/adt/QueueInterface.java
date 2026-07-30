package adt;

public interface QueueInterface<T> {
    public void enqueue(T item);

    T dequeue();

    T peek();

    boolean isEmpty();

    void clear();
    
    T getFront();
}
