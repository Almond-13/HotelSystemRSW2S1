package control;

import java.util.Scanner;
import adt.ArrayList;
import adt.CircularArrayQueue;
import dao.RoomDAO;
import entity.*;

public class WIRegistrationControl {

    private CircularArrayQueue<Guest> guestQueue;
    private Scanner input;

    public WIRegistrationControl() {
        guestQueue = new CircularArrayQueue<>();
        input = new Scanner(System.in);
        roomDAO = new RoomDAO();
        rooms = roomDAO.getRooms();

    }

    public void RGuest() {

        System.out.println("\n===== Register Guest =====");

        //Guest ID is the Unique ID, so need to generate from System.
        System.out.print("Guest ID: ");
        String guestID = input.next();
        input.nextLine(); 

        //Input all the infomation of Guest and use nextLine to avoid next question.
        System.out.print("Guest Name: ");
        String name = input.nextLine();

        System.out.print("IC / Passport No: ");
        String icPassportNo = input.nextLine();

        System.out.print("Phone Number: ");
        String phoneNumber = input.nextLine();

        System.out.print("Email: ");
        String email = input.nextLine();

        //Store all the info just now into Queues
        Guest guest = new Guest(
                guestID,
                name,
                icPassportNo,
                phoneNumber,
                email
        );
        
        guestQueue.enqueue(guest);

        System.out.println("\nGuest registered successfully!");
        System.out.println("Guest has been added to the waiting queue.");
    }

    //Call Fixed Data (which is Room No and Room Type)
    private RoomDAO roomDAO;
    private ArrayList<Room> rooms;
    
    public void CheckIn() {
        if(guestQueue.isEmpty()){
        System.out.println("No customer waiting for check in.");
        return;
    }
    //Check whether contain registration guest.
        Guest guest = guestQueue.peek();
        System.out.println("\nCurrent Guest:");
        System.out.println(guest);
        // Show the All the Room
        System.out.println("\n================ Available Rooms ================");
        System.out.printf("%-10s %-15s %-20s %-20s%n",
                "Room No",
                "Room Type",
                "Current Status",
                "Occupancy Status");

        System.out.println("--------------------------------------------------");
        boolean found = false;
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.isBookable()) {
                System.out.printf("%-10s %-15s %-20s %-20s%n",
                        room.getRoomNo(),
                        room.getRoomType(),
                        room.getCurrentStatus(),
                        room.getOccupancyStatus()
                );
                found = true;
            }
        }
        if(!found) {

            System.out.println("No Available Room Now.");
            return;
        }
        //Enter for Return to SubMenu
        System.out.println("\nPress Enter to return...");
        String selectedRoomNo = input.nextLine();
            Room selectedRoom = null;


        //Select the Room by RoomNo.
        for(int i = 0; i < availableRooms.size(); i++) {
            Room room = availableRooms.get(i);
            if(room.getRoomNo().equals(selectedRoomNo)) {
                selectedRoom = room;
                break;

            }

        }
        if(selectedRoom == null) {
            System.out.println("Invalid Room Selection.");
            return;

        }
        // Update Room Status
        selectedRoom.setOccupancyStatus("Occupied");

        // Remove guest from queue after successful check in
        guestQueue.dequeue();
        System.out.println("\n===== Check In Successful =====");
        System.out.println("Guest: " + guest.getName());
        System.out.println("Room: " + selectedRoom.getRoomNo());
        }
    

    public void CheckOut() {

    }
}