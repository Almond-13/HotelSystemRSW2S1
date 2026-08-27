package entity;

public class Room {
    private String roomNo;
    private RoomType roomType;
    private RoomStatus currentStatus;
    private String occupancyStatus;

<<<<<<< HEAD
    public Room() {
        this.roomNo = "";
        this.roomType = "";
        this.currentStatus = "Clean";
        this.occupancyStatus = "Available";
    }

    public Room(String roomNo, String roomType) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.currentStatus = "Clean";
        this.occupancyStatus = "Available";
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getOccupancyStatus() {
        return occupancyStatus;
    }

    public void setOccupancyStatus(String occupancyStatus) {
        this.occupancyStatus = occupancyStatus;
    }

    public boolean isBookable() {
        return occupancyStatus.equals("Available") && currentStatus.equals("Clean");
    }

    public void updateOStatus() {
    if (occupancyStatus.equals("Occupied")) {
        return;
    }
    if (currentStatus.equals("Clean")) {
        occupancyStatus = "Available";
    } else {
        occupancyStatus = "Unavailable";
    }
    }
=======
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
>>>>>>> VIP-Room

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
        if (roomType == null || roomType.trim().isEmpty()) {
            return false;
        }
        if (currentStatus == null || currentStatus.trim().isEmpty()) {
            return false;
        }
        if (occupancyStatus == null || occupancyStatus.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
