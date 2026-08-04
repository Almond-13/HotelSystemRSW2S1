/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package adt;

/**
 *
 * @author user
 */
public interface QueueInterface<T> {
    public void enqueue(T item);

    T dequeue();

    T peek();

    boolean isEmpty();

    void clear();

    T getFront();

}
