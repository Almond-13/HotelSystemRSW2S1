package control;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import adt.ArrayList;
import adt.CircularArrayQueue;
import dao.GuestDAO;
import dao.RoomDAO;
import entity.*;

public class WIRegistrationControl {

    private CircularArrayQueue<Guest> guestQueue;
    private ArrayList<Booking> book;
    private ArrayList<Room> rooms; // Call Fixed Data (which is Room No and Room Type)
    private ArrayList<Guest> guests;
    private Scanner input;
    private GuestDAO guestDAO;
    // For Booking Part (Booking ID)
    private int bookingCount = 1;
    String bookingID = "B" + String.format("%03d", bookingCount++);
    private int guestCounter = 1;
    private HousekeepingManager hkManager;

     public WIRegistrationControl(RoomDAO roomDAO, HousekeepingManager hkManager) {
        guestQueue = new CircularArrayQueue<>();
        input = new Scanner(System.in);
        rooms = roomDAO.getRooms();
        guestDAO = new GuestDAO();
        book = new ArrayList<>();
        guests = new ArrayList<>();
        guestDAO.loadGuests(guests);
        guestCounter = guests.size() + 1;
        this.hkManager = hkManager;
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
        guests.add(guest);
        guestDAO.saveGuests(guests);

        System.out.println("\nGuest registered successfully!");
        System.out.println("Guest ID: " + guestID);
        System.out.println("Guest has been added to the waiting queue.");
    }

    // ======================================================================
    // Update/Search Information Of Guest (Customer)
    // ======================================================================
    public void UpdateGuestInfo() {
        System.out.println("\n===== Update Guest Information =====");

        if (guests.isEmpty()) {
            System.out.println("No guest information available.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Display all guests
        System.out.printf("%-10s %-20s %-20s %-15s%n",
                "Guest ID",
                "Guest Name",
                "IC / Passport",
                "Phone Number");

        System.out.println("----------------------------------------------------------------");

        for (int i = 0; i < guests.size(); i++) {
            Guest guest = guests.get(i);

            System.out.printf("%-10s %-20s %-20s %-15s%n",
                    guest.getGuestID(),
                    guest.getName(),
                    guest.getICPassportNo(),
                    guest.getPhoneNumber());
        }

        System.out.println("----------------------------------------------------------------");

        // Select Guest ID
        // Select Guest
        Guest selectedGuest = null;

        while (true) {
            System.out.print("\nEnter Guest ID / Name / IC-Passport / Phone to Search (0 for Exit): ");
            String keyword = input.nextLine().trim();

            // 0 = Exit
            if (keyword.equals("0")) {
                return;
            }

            // Search Guest (matches ID, Name, IC/Passport, or Phone)
            ArrayList<Guest> matches = new ArrayList<>();
            for (int i = 0; i < guests.size(); i++) {
                Guest guest = guests.get(i);

                if (guest.getGuestID().equalsIgnoreCase(keyword)
                        || guest.getName().toLowerCase().contains(keyword.toLowerCase())
                        || guest.getICPassportNo().contains(keyword)
                        || guest.getPhoneNumber().contains(keyword)) {
                    matches.add(guest);
                }
            }

            if (matches.isEmpty()) {
                System.out.println("No matching guest found.");
                System.out.println("Please enter a valid keyword.");
                continue;
            }

            if (matches.size() == 1) {
                selectedGuest = matches.get(0);
                break;
            }

            // Multiple matches found, let user pick
            System.out.println("\nMultiple guests found:");
            System.out.printf("%-10s %-20s %-20s %-15s%n",
                    "Guest ID", "Guest Name", "IC / Passport", "Phone Number");
            for (int i = 0; i < matches.size(); i++) {
                Guest g = matches.get(i);
                System.out.printf("%-10s %-20s %-20s %-15s%n",
                        g.getGuestID(), g.getName(), g.getICPassportNo(), g.getPhoneNumber());
            }

            System.out.print("\nEnter exact Guest ID to select: ");
            String exactID = input.nextLine().trim();
            for (int i = 0; i < matches.size(); i++) {
                if (matches.get(i).getGuestID().equalsIgnoreCase(exactID)) {
                    selectedGuest = matches.get(i);
                    break;
                }
            }

            if (selectedGuest != null) {
                break;
            }
            System.out.println("Invalid Guest ID.");
        }

        // Display current information
        System.out.println("\n========== Current Guest Information ==========");
        System.out.println("Guest ID       : " + selectedGuest.getGuestID());
        System.out.println("Guest Name     : " + selectedGuest.getName());
        System.out.println("IC / Passport  : " + selectedGuest.getICPassportNo());
        System.out.println("Phone Number   : " + selectedGuest.getPhoneNumber());
        System.out.println("===============================================");

        // Update Name
        String name;

        while (true) {
            System.out.print("\nEnter New Guest Name (0 for Next): ");
            name = input.nextLine().trim();

            if (name.equals("0")) {
                name = selectedGuest.getName();
                break;
            } else if (name.isEmpty()) {
                System.out.println("Guest Name cannot be empty.");
            } else {
                break;
            }
        }

        // Update IC / Passport
        String icPassportNo;

        while (true) {
            System.out.print("Enter New IC / Passport No (0 for Next): ");
            icPassportNo = input.nextLine().trim();

            if (icPassportNo.equals("0")) {
                icPassportNo = selectedGuest.getICPassportNo();
                break;
            } else if (icPassportNo.isEmpty()) {
                System.out.println("IC / Passport No. cannot be empty.");
            } else if (!icPassportNo.matches("\\d+")) {
                System.out.println("IC / Passport No. must contain digits only.");
            } else if (icPassportNo.length() < 8 || icPassportNo.length() > 12) {
                System.out.println("IC / Passport No. must be 8-12 digits.");
            } else {
                break;
            }
        }

        // Update Phone Number
        String phoneNumber;

        while (true) {
            System.out.print("Enter New Phone Number (0 for Next): ");
            phoneNumber = input.nextLine().trim();

            if (phoneNumber.equals("0")) {
                phoneNumber = selectedGuest.getPhoneNumber();
                break;
            } else if (phoneNumber.isEmpty()) {
                System.out.println("Phone Number cannot be empty.");
            } else if (!phoneNumber.matches("\\d+")) {
                System.out.println("Phone Number must contain digits only.");
            } else if (phoneNumber.length() < 10 || phoneNumber.length() > 12) {
                System.out.println("Phone Number must be 10 to 12 digits.");
            } else {
                break;
            }
        }

        // Update Guest Information
        selectedGuest.setName(name);
        selectedGuest.setICPassportNo(icPassportNo);
        selectedGuest.setPhoneNumber(phoneNumber);

        System.out.println("\nGuest information updated successfully!");

        System.out.println("\n========== Updated Guest Information ==========");
        System.out.println("Guest ID       : " + selectedGuest.getGuestID());
        System.out.println("Guest Name     : " + selectedGuest.getName());
        System.out.println("IC / Passport  : " + selectedGuest.getICPassportNo());
        System.out.println("Phone Number   : " + selectedGuest.getPhoneNumber());
        System.out.println("===============================================");

        System.out.print("\nPress Enter to return...");
        input.nextLine();
    }

    // =====================================================================
    // Check-In
    // =====================================================================
    public void CheckIn() {
        System.out.println("\n===== Check In =====");
        System.out.println("1. Show All Rooms");
        System.out.println("2. Show Available Rooms Only");
        System.out.print("Enter your choice: ");

        String filterChoice = input.nextLine().trim();
        // Show Room Status (with Filter)
        System.out.println("\n=================== Room Status ====================");
        System.out.printf("%-10s %-15s %-20s %-20s%n",
                "Room No",
                "Room Type",
                "Current Status",
                "Occupancy Status");

        System.out.println("--------------------------------------------------------\n");
        boolean found = false;
        ArrayList<Room> availableRooms = new ArrayList<>();

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);

            // Filter: only add to availableRooms list if bookable
            if (room.isBookable()) {
                availableRooms.add(room);
                found = true;
            }

            // Filter: decide whether to print this row
            if (filterChoice.equals("2") && !room.isBookable()) {
                continue; // skip non-available rooms when filter is "2"
            }

            System.out.printf("%-10s %-15s %-20s %-20s%n",
                    room.getRoomNo(),
                    room.getRoomType(),
                    room.getCurrentStatus(),
                    room.getOccupancyStatus());
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
        LocalDate checkInDate = LocalDate.now();

        // Check Out
        int nights = 0;
        while (true) {
            System.out.print("Enter number of nights: ");
            String nightsInput = input.nextLine().trim();
            try {
                nights = Integer.parseInt(nightsInput);
                if (nights > 0) {
                    break;
                }
                System.out.println("Number of nights must be at least 1.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        LocalDate checkOutDate = checkInDate.plusDays(nights);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String checkInStr = checkInDate.format(formatter);
        String checkOutStr = checkOutDate.format(formatter);

        Booking booking = new Booking(
                bookingID,
                guest,
                selectedRoom,
                checkInStr,
                checkOutStr,
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
        hkManager.recordCheckoutDirty(room.getRoomNo(), "Checkout");
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
