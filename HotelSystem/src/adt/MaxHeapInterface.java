/*========================================== 
| Author          | Change                |
| Lim Jia Zheng   | Setup RESORT VIP      |
============================================
*/ 
package adt;

public interface MaxHeapInterface<T> {
    void add(T newEntry);

    T removeMax();

    T peekMax();

    boolean isEmpty();

    int getNumberOfEntries();

    void clear();
}
