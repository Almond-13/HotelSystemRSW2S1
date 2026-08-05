package adt;

public interface ListInterface<T> {

    void add(T newEntry);

    void add(int index, T newEntry);

    T remove(int index);

    void clear();

    void replace(int index, T newEntry);

    T get(int index);

    boolean contains(T entry);

    int size();

    boolean isEmpty();

}