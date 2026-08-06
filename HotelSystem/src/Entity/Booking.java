package entity;

public class Booking {
    private String BookingID;
    private Guest Guest;
    private Room room;
    private String checkInDate;
    private String checkOutDate;
    private String Status;

    public Booking(){}
    public Booking(String BookingID, Guest Guest, Room room, String checkInDate, String checkOutDate, String Status){
        this.BookingID = BookingID;
        this.Guest=Guest;
        this.room=room;
        this.Status=Status;
        this.checkInDate=checkInDate;
        this.checkOutDate=checkOutDate;
        this.Status=Status;

    }

    //Getter
    public String getBookingID() {
        return BookingID;
    }
    public Guest getGuest() {
        return Guest;
    }
    public Room getRoom() {
        return room;
    }
    public String getCheckInDate() {
        return checkInDate;
    }
    public String getCheckOutDate() {
        return checkOutDate;
    }
    public String getStatus() {
        return Status;
    }

    //Setter
    public void setStatus(String Status){
        this.Status=Status;
    }

}


