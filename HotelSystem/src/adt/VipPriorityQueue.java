// Author: Lim Jia Zheng
package adt;

import entity.VipAllocationRequest;

public class VipPriorityQueue implements MaxHeapInterface<VipAllocationRequest> {
    private static final int DEFAULT_CAPACITY = 16;
    private VipAllocationRequest[] heap;
    private int numberOfEntries;

    public VipPriorityQueue() {
        heap = new VipAllocationRequest[DEFAULT_CAPACITY + 1];
        numberOfEntries = 0;
    }

    @Override
    public void add(VipAllocationRequest newEntry) {
        if (newEntry == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        ensureCapacity();
        heap[++numberOfEntries] = newEntry;
        reheapUp(numberOfEntries);
    }

    @Override
    public VipAllocationRequest removeMax() {
        if (isEmpty()) {
            return null;
        }
        VipAllocationRequest root = heap[1];
        heap[1] = heap[numberOfEntries];
        heap[numberOfEntries] = null;
        numberOfEntries--;
        reheapDown(1);
        return root;
    }

    @Override
    public VipAllocationRequest peekMax() {
        return isEmpty() ? null : heap[1];
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    @Override
    public void clear() {
        heap = new VipAllocationRequest[DEFAULT_CAPACITY + 1];
        numberOfEntries = 0;
    }

    public VipAllocationRequest[] toPriorityOrderArray() {
        VipPriorityQueue copy = new VipPriorityQueue();
        for (int i = 1; i <= numberOfEntries; i++) {
            copy.add(heap[i]);
        }

        VipAllocationRequest[] ordered = new VipAllocationRequest[numberOfEntries];
        for (int i = 0; i < ordered.length; i++) {
            ordered[i] = copy.removeMax();
        }
        return ordered;
    }

    private void reheapUp(int currentIndex) {
        int parentIndex = currentIndex / 2;
        while (parentIndex > 0 && heap[currentIndex].hasHigherPriorityThan(heap[parentIndex])) {
            swap(currentIndex, parentIndex);
            currentIndex = parentIndex;
            parentIndex = currentIndex / 2;
        }
    }

    private void reheapDown(int currentIndex) {
        while (currentIndex * 2 <= numberOfEntries) {
            int leftChildIndex = currentIndex * 2;
            int rightChildIndex = leftChildIndex + 1;
            int largerChildIndex = leftChildIndex;

            if (rightChildIndex <= numberOfEntries
                    && heap[rightChildIndex].hasHigherPriorityThan(heap[leftChildIndex])) {
                largerChildIndex = rightChildIndex;
            }

            if (heap[largerChildIndex].hasHigherPriorityThan(heap[currentIndex])) {
                swap(currentIndex, largerChildIndex);
                currentIndex = largerChildIndex;
            } else {
                break;
            }
        }
    }

    private void ensureCapacity() {
        if (numberOfEntries + 1 < heap.length) {
            return;
        }
        VipAllocationRequest[] largerHeap = new VipAllocationRequest[heap.length * 2];
        for (int i = 1; i <= numberOfEntries; i++) {
            largerHeap[i] = heap[i];
        }
        heap = largerHeap;
    }

    private void swap(int firstIndex, int secondIndex) {
        VipAllocationRequest temp = heap[firstIndex];
        heap[firstIndex] = heap[secondIndex];
        heap[secondIndex] = temp;
    }
}
