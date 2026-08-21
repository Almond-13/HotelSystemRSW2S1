package entity;

import adt.LinkedStack;

public class HousekeepingLog {
    private Room room;
    private LinkedStack<StatusRecord> history;
    private int rollbackCount;

    public HousekeepingLog() {
        this.room = new Room();
        this.history = new LinkedStack<>();
        this.rollbackCount = 0;
        this.history.push(new StatusRecord(room.getCurrentStatus(), "SYSTEM"));
    }

    public HousekeepingLog(Room room) {
        this.room = room;
        this.history = new LinkedStack<>();
        this.rollbackCount = 0;
        this.history.push(new StatusRecord(room.getCurrentStatus(), "SYSTEM"));
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LinkedStack<StatusRecord> getHistory() {
        return history;
    }

    public int getRollbackCount() {
        return rollbackCount;
    }

    public void incrementRollbackCount() {
        rollbackCount++;
    }

    public void resetRollbackCount() {
        rollbackCount = 0;
    }

    @Override
    public String toString() {
        return "HousekeepingLog{" +
                "room=" + room +
                ", rollbackCount=" + rollbackCount +
                '}';
    }
}
