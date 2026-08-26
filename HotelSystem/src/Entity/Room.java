package entity;

public class Room {

    private String roomNo;
    private RoomType roomType;
    private RoomStatus currentStatus;
    private String occupancyStatus;
    private Guest CGuest; //For Guest Check Out (due to Guest Check-In will dequeue)

    public Room(String roomNo, RoomType roomType){
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.currentStatus = RoomStatus.CLEAN;
        this.occupancyStatus="Available";
    }

    public String getRoomNo() { return roomNo; }
    public RoomType getRoomType() { return roomType; }
    public RoomStatus getCurrentStatus() { return currentStatus; }
    public String getOccupancyStatus() { return occupancyStatus; }
    public Guest getCGuest() {return CGuest;}

    public void setCGuest(Guest CGuest) {this.CGuest = CGuest;}
    public void setCurrentStatus(RoomStatus currentStatus) { this.currentStatus = currentStatus; }
    public void setOccupancyStatus(String OccupancyStatus) { this.occupancyStatus = OccupancyStatus; }
    public void updateOStatus() {
        if (currentStatus == RoomStatus.CLEAN) {
            occupancyStatus = "Available";
        } else {
            occupancyStatus = "Unavailable";
        }  
    }

    public boolean isBookable() {
    return occupancyStatus.equals("Available")
        && currentStatus == RoomStatus.CLEAN;}

    @Override
    public String toString() {
        return "Room :" + roomNo + " (" + roomType + ") - " + currentStatus + "\nOccupancy Status" + occupancyStatus;
    }
}