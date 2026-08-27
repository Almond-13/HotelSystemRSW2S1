package entity;

public class Room {
    private String roomNo;
    private RoomType roomType;
    private RoomStatus currentStatus;
    private String occupancyStatus;
    private Guest CGuest;

    public Room() {
        this.roomNo = "";
        this.roomType = null;
        this.currentStatus = RoomStatus.CLEAN;
        this.occupancyStatus = "Available";
    }

    public Room(String roomNo, RoomType roomType) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.currentStatus = RoomStatus.CLEAN;
        this.occupancyStatus = "Available";
    }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
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
        return "Room{" +
                "roomNo='" + roomNo + '\'' +
                ", roomType='" + roomType + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", occupancyStatus='" + occupancyStatus + '\'' +
                '}';
    }

    public boolean isValid() {
        if (roomNo == null || roomNo.trim().isEmpty()) {
            return false;
        }
        if (roomType == null) {
            return false;
        }
        if (currentStatus == null) {
            return false;
        }
        if (occupancyStatus == null || occupancyStatus.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
