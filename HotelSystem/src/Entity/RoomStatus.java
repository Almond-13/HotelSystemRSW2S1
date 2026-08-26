// Author: Lim Jia Zheng
package entity;

public enum RoomStatus {
    DIRTY("Dirty"),
    CLEANING_IN_PROGRESS("Cleaning In Progress"),
    INSPECTED("Inspected"),
    CLEAN("Clean");

    private final String displayName;

    RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    public static RoomStatus fromDisplayName(String value) {
        for (RoomStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(value)) return status;
        }
        throw new IllegalArgumentException("Unknown room status: " + value);
    }

    @Override
    public String toString() { return displayName; }
}
