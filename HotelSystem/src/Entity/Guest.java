package entity;

public class Guest {
    private String GuestID;
    private String Name;
    private String ICPassportNo;
    private String PhoneNumber;
    //Add Guest Type(Normal/VIP)

    public Guest(){}
    public Guest(String GuestID,String Name, String ICPassportNo, String PhoneNumber){
        this.GuestID = GuestID;
        this.Name=Name;
        this.ICPassportNo= ICPassportNo;
        this.PhoneNumber=PhoneNumber;
    }

    //Getter
    public String getGuestID(){
        return GuestID;
    }

    public String getName(){
        return Name;
    }

    public String getICPassportNo(){
        return ICPassportNo;
    }
    
    public String getPhoneNumber(){
        return PhoneNumber;
    }

    //Setter
    public void setGuestID(String GuestID){
        this.GuestID = GuestID;
    }
    public void setName(String Name){
        this.Name = Name;
    }
    public void setPhoneNumber(String PhoneNumber){
        this.PhoneNumber = PhoneNumber;
    }
    public void setICPassportNo(String ICPassportNo){
        this.ICPassportNo = ICPassportNo;
    }
    @Override
    public String toString() {
        return "Guest ID: " + GuestID +"\nName: " + Name +"\nPhone: " + PhoneNumber;
}
}