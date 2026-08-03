package entity;

import adt.LinkedStack;

public class HousekeepingLog {

    private Room room;
    private LinkedStack<StatusRecord> history;
    private int rollbackCount;

    public HousekeepingLog(Room room) {
        this.room = room;
        this.history = new LinkedStack<>();
        this.rollbackCount = 0;
        history.push(new StatusRecord(room.getCurrentStatus(), null));
    }

    public Room getRoom() { return room; }
    public LinkedStack<StatusRecord> getHistory() { return history; }
    public int getRollbackCount() { return rollbackCount; }
    public void incrementRollbackCount() { rollbackCount++; }
}