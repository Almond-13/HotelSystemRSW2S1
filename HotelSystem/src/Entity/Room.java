package Entity;

public class Room {

    private String roomNo;
    private String roomType;
    private String currentStatus;

    public Room(String roomNo, String roomType) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.currentStatus = "Dirty";
    }

    public String getRoomNo() { return roomNo; }
    public String getRoomType() { return roomType; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    @Override
    public String toString() {
        return "Room " + roomNo + " (" + roomType + ") - " + currentStatus;
    }
}