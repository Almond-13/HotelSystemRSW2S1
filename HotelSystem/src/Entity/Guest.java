package entity;

public class Guest {
    private String GuestID;
    private String Name;
    private String ICPassportNo;
    private String PhoneNumber;
    private String Email;

    public Guest(){}
    public Guest(String GuestID,String Name, String ICPassportNo, String PhoneNumber, String Email){
        this.GuestID = GuestID;
        this.Name=Name;
        this.ICPassportNo= ICPassportNo;
        this.PhoneNumber=PhoneNumber;
        this.Email = Email;
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

    public String getEmail(){
        return Email;
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
    public void setEmail(String Email){
        this.Email = Email;
    }
    @Override
    public String toString() {
        return "Guest ID: " + GuestID +"\nName: " + Name +"\nPhone: " + PhoneNumber;
}
}