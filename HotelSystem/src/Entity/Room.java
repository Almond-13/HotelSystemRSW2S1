package entity;

public class Room {

    private String roomNo;
    private String roomType;
    private String currentStatus;
    private String occupancyStatus;

    public Room(String roomNo, String roomType){
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.currentStatus = "Clean"; 
        this.occupancyStatus="Available";
    }

    public String getRoomNo() { return roomNo; }
    public String getRoomType() { return roomType; }
    public String getCurrentStatus() { return currentStatus; }
    public String getOccupancyStatus() { return occupancyStatus; }
  
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public void setOccupancyStatus(String OccupancyStatus) { this.occupancyStatus = OccupancyStatus; }

    public boolean isBookable() {
    return occupancyStatus.equals("Available")
        && currentStatus.equals("Clean");}

    @Override
    public String toString() {
        return "Room :" + roomNo + " (" + roomType + ") - " + currentStatus + "\nOccupancy Status" + occupancyStatus;
    }
}