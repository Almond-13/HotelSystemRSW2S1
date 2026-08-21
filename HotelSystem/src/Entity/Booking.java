package entity;

public class Booking {
    private String bookingID;
    private Guest guest;
    private Room room;
    private String checkInDate;
    private String checkOutDate;
    private String status;

    public Booking() {
        this.bookingID = "";
        this.guest = null;
        this.room = null;
        this.checkInDate = "";
        this.checkOutDate = "";
        this.status = "";
    }

    public Booking(String bookingID, Guest guest, Room room, String checkInDate, String checkOutDate, String status) {
        this.bookingID = bookingID;
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingID='" + bookingID + '\'' +
                ", guest=" + guest +
                ", room=" + room +
                ", checkInDate='" + checkInDate + '\'' +
                ", checkOutDate='" + checkOutDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public boolean isValid() {
        if (bookingID == null || bookingID.trim().isEmpty()) {
            return false;
        }
        if (guest == null) {
            return false;
        }
        if (room == null) {
            return false;
        }
        if (checkInDate == null || checkInDate.trim().isEmpty()) {
            return false;
        }
        if (checkOutDate == null || checkOutDate.trim().isEmpty()) {
            return false;
        }
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
