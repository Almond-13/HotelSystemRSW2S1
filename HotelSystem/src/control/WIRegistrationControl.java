package control;

import java.util.Scanner;
import adt.ArrayList;
import adt.CircularArrayQueue;
import dao.RoomDAO;
import entity.*;

public class WIRegistrationControl {

    private CircularArrayQueue<Guest> guestQueue;
    private ArrayList<Booking> book;
    private Scanner input;
    private String lastError;

    // For Booking Part
    private int bookingCount = 1;
    String bookingID = "B" + bookingCount++;
    private int guestCounter = 1;

    public WIRegistrationControl(RoomDAO roomDAO) {
        guestQueue = new CircularArrayQueue<>();
        input = new Scanner(System.in);
        rooms = roomDAO.getRooms();
        book = new ArrayList<>();
        lastError = "";
    }

    public String getLastError() {
        return lastError;
    }

    private void setError(String message) {
        this.lastError = message;
    }

    private void clearError() {
        this.lastError = "";
    }

    // ===================================================================
    // Register Walk-In Guest
    // ===================================================================
    public void RGuest() {

        System.out.println("\n===== Register Guest =====");

        // Guest ID is the Unique ID, so need to generate from System.
        String guestID = String.format("G%03d", guestCounter++);

        // Input all the infomation of Guest and use nextLine to avoid next question.
        String name;
        while (true) {
            System.out.print("Guest Name: ");
            name = input.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Guest Name cannot be empty.");
            } else {
                break;
            }
        }

        String icPassportNo;
        while (true) {
            System.out.print("IC / Passport No: ");
            icPassportNo = input.nextLine().trim();

            if (icPassportNo.isEmpty()) {
                System.out.println("IC / Passport No. cannot be empty.");
            } else if (!icPassportNo.matches("\\d+")) {
                System.out.println("IC / Passposrt No. must contain digits only.");
            } else if (icPassportNo.length() < 8 || icPassportNo.length() > 12) {
                System.out.println("IC / Passport No. must be 8-12 element.");
            } else {
                break;
            }
        }

        String phoneNumber;
        while (true) {
            System.out.print("Phone Number: ");
            phoneNumber = input.nextLine().trim();

            if (phoneNumber.isEmpty()) {
                System.out.println("Phone Number cannot be empty.");
            } else if (!phoneNumber.matches("\\d+")) {
                System.out.println("Phone Number must contain digits only.");
            } else if (phoneNumber.length() < 10 || phoneNumber.length() > 12) {
                System.out.println("Phone Number must be 10 to 12 digits.");
            } else {
                break;
            }
        }

        // Store all the info just now into Queues
        Guest guest = new Guest(
                guestID,
                name,
                icPassportNo,
                phoneNumber);

        guestQueue.enqueue(guest);

        System.out.println("\nGuest registered successfully!");
        System.out.println("Guest ID: " + guestID);
        System.out.println("Guest has been added to the waiting queue.");
    }

    // Call Fixed Data (which is Room No and Room Type)
    private ArrayList<Room> rooms;

    // =====================================================================
    // Check-In
    // =====================================================================
    public void CheckIn() {
        // Show the All the Room
        System.out.println("\n=================== All Room Status ====================");
        System.out.printf("%-10s %-15s %-20s %-20s%n",
                "Room No",
                "Room Type",
                "Current Status",
                "Occupancy Status");

        System.out.println("--------------------------------------------------------");
        boolean found = false;
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            // Show the room detail from txt file.
            System.out.printf("%-10s %-15s %-20s %-20s%n",
                    room.getRoomNo(),
                    room.getRoomType(),
                    room.getCurrentStatus(),
                    room.getOccupancyStatus());
            if (room.isBookable()) {
                availableRooms.add(room);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No Available Room Now.");
            return;
        }
        if (guestQueue.isEmpty()) {
            System.out.println("No customer waiting for check in.");
            return;
        }
        // Guest Information
        Guest guest = guestQueue.peek();
        System.out.println("\nCurrent Guest:");
        System.out.println(guest);
        // Enter for Return to SubMenu
        System.out.println("\nEnter your Room Number (Enter to return): ");
        Room selectedRoom = null;

        while (true) {
            String selectedRoomNo = input.nextLine().trim();

            // Enter = return
            if (selectedRoomNo.isEmpty()) {
                return;
            }

            // Select the Room by RoomNo.
            for (int i = 0; i < availableRooms.size(); i++) {
                Room room = availableRooms.get(i);
                if (room.getRoomNo().equals(selectedRoomNo)) {
                    selectedRoom = room;
                    break;
                }
            }
            // If Press Enter.. it will exit
            if (selectedRoom != null) {
                break;
            }
            // Invalid room number
            System.out.println("Invalid Room Number.");
            System.out.print("Please enter a valid Room Number: ");
        }
        // Update Room Status
        selectedRoom.setOccupancyStatus("Occupied");
        selectedRoom.setCGuest(guest);

        Booking booking = new Booking(
                bookingID,
                guest,
                selectedRoom,
                "2026-08-07",
                "2026-08-10",
                "Checked In");
        book.add(booking);
        System.out.println(
                "Check In Successful!");
        System.out.println(
                "Booking ID: " + bookingID);

        // Remove guest from queue after successful check in
        guestQueue.dequeue();
        System.out.println("\n========== Check In Summary ==========");
        System.out.println("Booking ID   : " + bookingID);
        System.out.println("Guest Name   : " + guest.getName());
        System.out.println("IC/Passport  : " + guest.getICPassportNo());
        System.out.println("Room No      : " + selectedRoom.getRoomNo());
        System.out.println("Room Type    : " + selectedRoom.getRoomType());
        System.out.println("Check In     : " + booking.getCheckInDate());
        System.out.println("Status       : " + booking.getStatus());
        System.out.println("======================================");
    }

    // ========================================================================
    // Check-Out
    // ========================================================================
    public void CheckOut() {
        System.out.println("\n============ Check Out ============");
        boolean found = false;
        for (int i = 0; i < book.size(); i++) {
            Booking booking = book.get(i);

            // Output all the detail of Room that were Booked
            if (booking.getStatus().equals("Checked In")) {
                System.out.printf(
                        "%-10s %-15s %-15s %-15s%n",
                        booking.getRoom().getRoomNo(),
                        booking.getGuest().getName(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No occupied room.");
            return;
        }

        System.out.print("\nEnter Room Number for Check Out (0 for Exit..): ");
        String roomNo;
        Booking selectedBooking = null;

        while (true) {
            roomNo = input.nextLine().trim();
            if (roomNo.equals("0")) {
                return;
            }
            for (int i = 0; i < book.size(); i++) {
                Booking booking = book.get(i);

                if (booking.getRoom().getRoomNo().equals(roomNo)
                        && booking.getStatus().equals("Checked In")) {

                    selectedBooking = booking;
                    break;
                }
            }
            if (selectedBooking != null) {
                break;
            }

            // Invalid Room Number
            System.out.println("Invalid Room Number.");
            System.out.println("Please enter a Room Number that is currently Checked In.");
            System.out.print("Enter Room Number for Check Out: ");
        }
        Booking booking = selectedBooking;
        booking.setStatus("Checked Out");
        Room room = booking.getRoom();
        room.setCurrentStatus("Dirty");
        room.updateOStatus();

        System.out.println("\n========== Check Out Summary ==========");
        System.out.println("Booking ID  : " + booking.getBookingID());
        System.out.println("Guest Name  : " + booking.getGuest().getName());
        System.out.println("Room No     : " + room.getRoomNo());
        System.out.println("Room Type   : " + room.getRoomType());
        System.out.println("Check In    : " + booking.getCheckInDate());
        System.out.println("Check Out   : " + booking.getCheckOutDate());
        System.out.println("Status      : " + booking.getStatus());
        System.out.println("======================================");
    }

    // ======================================================================
    // Report
    // ======================================================================
    public void SReport() {
        System.out.println("\n============================== SUMMARY REPORT ==============================");

        if (book.isEmpty()) {
            System.out.println("No guest or booking records available.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        System.out.printf(
                "%-10s %-12s %-18s %-18s %-15s %-12s %-12s %-15s%n",
                "Guest ID",
                "Booking ID",
                "Guest Name",
                "IC / Passport",
                "Phone Number",
                "Room No",
                "Room Type",
                "Status");

        System.out.println(
                "-------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < book.size(); i++) {
            Booking booking = book.get(i);
            Guest guest = booking.getGuest();
            Room room = booking.getRoom();

            System.out.printf(
                    "%-10s %-12s %-18s %-18s %-15s %-12s %-12s %-15s%n",
                    guest.getGuestID(),
                    booking.getBookingID(),
                    guest.getName(),
                    guest.getICPassportNo(),
                    guest.getPhoneNumber(),
                    room.getRoomNo(),
                    room.getRoomType(),
                    booking.getStatus());

            System.out.printf(
                    "    Check In Date : %-15s | Check Out Date : %-15s%n",
                    booking.getCheckInDate(),
                    booking.getCheckOutDate());

            System.out.println(
                    "-------------------------------------------------------------------------------------------------------------");
        }

        System.out.print("\nPress Enter to return...");
        input.nextLine();
    }
}
