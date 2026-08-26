// Author: Lim Jia Zheng
package entity;

public enum RoomType {
    STANDARD("Standard"),
    DELUXE("Deluxe"),
    SUITE("Suite"),
    EXECUTIVE("Executive"),
    PRESIDENTIAL("Presidential");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public static RoomType fromDisplayName(String value) {
        for (RoomType type : values()) {
            if (type.displayName.equalsIgnoreCase(value)) return type;
        }
        throw new IllegalArgumentException("Unknown room type: " + value);
    }

    @Override
    public String toString() { return displayName; }
}
