package Entity;

public class Booking {
    private String BookingID;
    private Guest Guest;
    private String roomType;
    private int numberOfGuests;
    private String checkInDate;
    private String checkOutDate;
    private String Status;

    public Booking(){}
    public Booking(String BookingID, Guest Guest, String roomType, int numberOfGuests, String checkInDate, String checkOutDate, String Status){
        this.BookingID = BookingID;
        this.Guest=Guest;
        this.Status=Status;
        this.checkInDate=checkInDate;
        this.checkOutDate=checkOutDate;
        this.Status=Status;

    }


    public String getBookingID() {
        return BookingID;
    }

    public Guest getGuest() {
        return Guest;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public String getCheckInDate() {
        return checkInDate;
    }
    public String getcheckOutDate() {
        return checkOutDate;
    }
    public String getStatus() {
        return Status;
    }

}


