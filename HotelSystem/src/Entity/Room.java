package entity;

public class Room {
    private String roomNo;
    private String roomType;
    private String currentStatus;
    private String occupancyStatus;
    private Guest currentGuest;

    public Room() {
        this.roomNo = "";
        this.roomType = "";
        this.currentStatus = "Clean";
        this.occupancyStatus = "Vacant";
        this.currentGuest = null;
    }

    public Room(String roomNo, String roomType) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.currentStatus = "Clean";
        this.occupancyStatus = "Vacant";
        this.currentGuest = null;
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

    public Guest getCurrentGuest() {
        return currentGuest;
    }

    public void setCGuest(Guest guest) {
        this.currentGuest = guest;
    }

    public boolean isBookable() {
        return occupancyStatus.equals("Vacant") && currentStatus.equals("Clean");
    }

    public void updateOStatus() {
        if (occupancyStatus.equals("Occupied")) {
            currentStatus = "Occupied";
        } else if (currentStatus.equals("Dirty")) {
            currentStatus = "Dirty";
        }
    }

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
