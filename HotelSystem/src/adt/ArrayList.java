package adt;

public class ArrayList<T> implements ListInterface<T> {

    private T[] list;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 10;


    @SuppressWarnings("unchecked")
    public ArrayList() {

        list = (T[]) new Object[DEFAULT_CAPACITY];
        numberOfEntries = 0;

    }


    @Override
    public void add(T newEntry) {

        ensureCapacity();

        list[numberOfEntries] = newEntry;
        numberOfEntries++;

    }


    @Override
    public void add(int index, T newEntry) {

        ensureCapacity();

        for(int i = numberOfEntries; i > index; i--) {

            list[i] = list[i-1];

        }

        list[index] = newEntry;
        numberOfEntries++;

    }


    @Override
    public T remove(int index) {

        T removed = list[index];

        for(int i=index; i<numberOfEntries-1; i++) {

            list[i] = list[i+1];

        }

        list[numberOfEntries-1] = null;

        numberOfEntries--;

        return removed;

    }


    @Override
    public void clear() {

        for(int i=0; i<numberOfEntries; i++) {

            list[i] = null;

        }

        numberOfEntries = 0;

    }


    @Override
    public void replace(int index, T newEntry) {

        list[index] = newEntry;

    }


    @Override
    public T get(int index) {

        return list[index];

    }


    @Override
    public boolean contains(T entry) {

        for(int i=0; i<numberOfEntries; i++) {

            if(list[i].equals(entry)) {

                return true;

            }

        }

        return false;

    }


    @Override
    public int size() {

        return numberOfEntries;

    }


    @Override
    public boolean isEmpty() {

        return numberOfEntries == 0;

    }


    @SuppressWarnings("unchecked")
    private void ensureCapacity() {

        if(numberOfEntries == list.length) {

            T[] oldList = list;

            list = (T[]) new Object[list.length * 2];

            for(int i=0; i<oldList.length; i++) {

                list[i] = oldList[i];

            }

        }

    }

}