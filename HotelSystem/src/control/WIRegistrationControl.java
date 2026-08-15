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

    // ========================================================================
    // Assign Room
    // ========================================================================
    public void AssignRoom() {
        System.out.println("\n============ Assign Room ============");

        // Ask whether to show all rooms or bookable rooms only.
        // Room status can always be checked here, even if no guest is
        // currently waiting to be assigned.
        System.out.println("\n1. Show All Rooms");
        System.out.println("2. Show Available Rooms Only");
        System.out.print("Enter your choice: ");
        String filterChoice = input.nextLine().trim();

        // Build the list of rooms that can actually be assigned (bookable only),
        // regardless of what the display filter shows.
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.isBookable()) {
                availableRooms.add(room);
            }
        }

        // Display Room Status (filtered display, single table)
        System.out.println("\n=================== Room Status ====================");
        System.out.printf("%-10s %-15s %-20s %-20s%n",
                "Room No",
                "Room Type",
                "Current Status",
                "Occupancy Status");

        System.out.println("--------------------------------------------------------");

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);

            // Filter "2" = Available Rooms Only: skip rooms that aren't bookable
            if (filterChoice.equals("2") && !room.isBookable()) {
                continue;
            }

            System.out.printf("%-10s %-15s %-20s %-20s%n",
                    room.getRoomNo(),
                    room.getRoomType(),
                    room.getCurrentStatus(),
                    room.getOccupancyStatus());
        }

        System.out.println("--------------------------------------------------------");

        if (availableRooms.isEmpty()) {
            System.out.println("\nNo available room for assignment.");
            System.out.print("Press Enter to return...");
            input.nextLine();
            return;
        }

        // Now that the room table has been shown, check whether there is
        // actually a guest waiting to be assigned a room. Viewing room
        // status should not require a guest in the queue, but assigning
        // one does.
        if (guestQueue.isEmpty()) {
            System.out.println("\nNo guest waiting for room assignment.");
            System.out.print("Press Enter to return...");
            input.nextLine();
            return;
        }

        // Get the first guest in the waiting queue
        Guest guest = guestQueue.peek();

        System.out.println("\n========== Guest Waiting ==========");
        System.out.println("Guest ID       : " + guest.getGuestID());
        System.out.println("Guest Name     : " + guest.getName());
        System.out.println("IC / Passport  : " + guest.getICPassportNo());
        System.out.println("Phone Number   : " + guest.getPhoneNumber());
        System.out.println("===================================");

        //Filter
        Room selectedRoom = null;

        while (true) {
            System.out.print("Enter Room Number (0 for Exit): ");
            String roomNo = input.nextLine().trim();

            if (roomNo.equals("0")) {
                return;
            }

            for (int i = 0; i < availableRooms.size(); i++) {
                Room room = availableRooms.get(i);

                if (room.getRoomNo().equals(roomNo)) {
                    selectedRoom = room;
                    break;
                }
            }

            if (selectedRoom != null) {
                break;
            }

            System.out.println("Invalid Room Number.");
            System.out.println("Please select an available room.");
        }

        // Create Booking
        LocalDate checkInDate = LocalDate.now();

        int nights = 0;

        while (true) {
            System.out.print("Enter number of nights (0 to cancel): ");
            String nightsInput = input.nextLine().trim();

            if (nightsInput.equals("0")) {
                System.out.println("Assignment cancelled.");
                return;
            }

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
                "Assigned");

        book.add(booking);

        // Room is now reserved for this guest but NOT occupied yet.
        // occupancyStatus turns "Unavailable" here on purpose, so no one
        // else can grab the same room before the guest actually checks in.
        selectedRoom.setOccupancyStatus("Unavailable");

        // Remove guest from waiting queue
        guestQueue.dequeue();

        System.out.println("\nRoom assigned successfully!");
        System.out.println("\n========== Assignment Summary ==========");
        System.out.println("Booking ID   : " + bookingID);
        System.out.println("Guest ID     : " + guest.getGuestID());
        System.out.println("Guest Name   : " + guest.getName());
        System.out.println("Room No      : " + selectedRoom.getRoomNo());
        System.out.println("Room Type    : " + selectedRoom.getRoomType());
        System.out.println("Check In     : " + booking.getCheckInDate());
        System.out.println("Check Out    : " + booking.getCheckOutDate());
        System.out.println("Status       : " + booking.getStatus());
        System.out.println("========================================");

        // Generate next Booking ID
        bookingID = "B" + String.format("%03d", bookingCount++);
    }

    // ========================================================================
    // Check-In
    // ========================================================================
    public void CheckIn() {
        System.out.println("\n============ Check In ============");

        boolean found = false;

        // Display all assigned bookings
        System.out.printf("%-12s %-12s %-20s %-12s %-15s%n",
                "Booking ID",
                "Guest ID",
                "Guest Name",
                "Room No",
                "Status");

        System.out.println("----------------------------------------------------------------");

        for (int i = 0; i < book.size(); i++) {
            Booking booking = book.get(i);

            if (booking.getStatus().equals("Assigned")) {

                System.out.printf("%-12s %-12s %-20s %-12s %-15s%n",
                        booking.getBookingID(),
                        booking.getGuest().getGuestID(),
                        booking.getGuest().getName(),
                        booking.getRoom().getRoomNo(),
                        booking.getStatus());

                found = true;
            }
        }

        if (!found) {
            System.out.println("No assigned guest available for check-in.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Search Guest ID
        Booking selectedBooking = null;

        while (true) {
            System.out.print("\nEnter Guest ID for Check In (0 for Exit): ");
            String guestID = input.nextLine().trim();

            if (guestID.equals("0")) {
                return;
            }

            for (int i = 0; i < book.size(); i++) {
                Booking booking = book.get(i);

                if (booking.getGuest().getGuestID().equalsIgnoreCase(guestID)
                        && booking.getStatus().equals("Assigned")) {

                    selectedBooking = booking;
                    break;
                }
            }

            if (selectedBooking != null) {
                break;
            }

            System.out.println("No assigned booking found for this Guest ID.");
        }

        Guest guest = selectedBooking.getGuest();
        Room room = selectedBooking.getRoom();

        // Check whether the room is available for check-in
        if (!room.getCurrentStatus().equals("Clean")) {
            System.out.println("\nThe assigned room is not ready for check-in yet.");
            System.out.println("Room Status : " + room.getCurrentStatus());

            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Confirm Check-In
        System.out.println("\n========== Guest Information ==========");
        System.out.println("Guest ID       : " + guest.getGuestID());
        System.out.println("Guest Name     : " + guest.getName());
        System.out.println("IC / Passport  : " + guest.getICPassportNo());
        System.out.println("Phone Number   : " + guest.getPhoneNumber());
        System.out.println("Room No        : " + room.getRoomNo());
        System.out.println("Room Type      : " + room.getRoomType());
        System.out.println("Check In       : " + selectedBooking.getCheckInDate());
        System.out.println("Check Out      : " + selectedBooking.getCheckOutDate());
        System.out.println("=======================================");

        System.out.print("\nConfirm Check In? (Y/N): ");
        String confirm = input.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Check In cancelled.");
            return;
        }

        // Update Booking Status
        selectedBooking.setStatus("Checked In");

        // Room stays "Unavailable" (it is occupied now, not free to assign),
        // so no further occupancyStatus change is needed here.

        System.out.println("\nCheck In Successful!");

        System.out.println("\n========== Check In Summary ==========");
        System.out.println("Booking ID   : " + selectedBooking.getBookingID());
        System.out.println("Guest ID     : " + guest.getGuestID());
        System.out.println("Guest Name   : " + guest.getName());
        System.out.println("Room No      : " + room.getRoomNo());
        System.out.println("Room Type    : " + room.getRoomType());
        System.out.println("Check In     : " + selectedBooking.getCheckInDate());
        System.out.println("Check Out    : " + selectedBooking.getCheckOutDate());
        System.out.println("Status       : " + selectedBooking.getStatus());
        System.out.println("======================================");
    }

    // ========================================================================
    // Check-Out
    // ========================================================================
    public void CheckOut() {
        System.out.println("\n============ Check Out ============");

        boolean found = false;

        System.out.printf("%-12s %-20s %-12s %-15s %-15s%n",
                "Booking ID",
                "Guest Name",
                "Room No",
                "Check In",
                "Check Out");

        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < book.size(); i++) {
            Booking booking = book.get(i);

            if (booking.getStatus().equals("Checked In")) {

                System.out.printf("%-12s %-20s %-12s %-15s %-15s%n",
                        booking.getBookingID(),
                        booking.getGuest().getName(),
                        booking.getRoom().getRoomNo(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate());

                found = true;
            }
        }

        if (!found) {
            System.out.println("No occupied room.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // whichever one they have on hand.
        Booking selectedBooking = null;

        while (true) {
            System.out.print("\nEnter Room Number for Check Out (0 for Exit): ");
            String keyword = input.nextLine().trim();

            if (keyword.equals("0")) {
                return;
            }

            for (int i = 0; i < book.size(); i++) {
                Booking booking = book.get(i);

                if (booking.getStatus().equals("Checked In")
                        && (booking.getRoom().getRoomNo().equalsIgnoreCase(keyword))) {

                    selectedBooking = booking;
                    break;
                }
            }

            if (selectedBooking != null) {
                break;
            }

            System.out.println("Invalid Room Number.");
            System.out.println(
                    "Please enter a Room Number that is currently Checked In.");
        }

        // Update Booking Status
        selectedBooking.setStatus("Checked Out");

        // Get Room
        Room room = selectedBooking.getRoom();

        // Send room to Housekeeping
        hkManager.recordCheckoutDirty(
                room.getRoomNo(),
                "Checkout");

        // Recompute occupancyStatus from the (now dirty) currentStatus
        room.updateOStatus();

        System.out.println("\n========== Check Out Summary ==========");
        System.out.println("Booking ID  : " + selectedBooking.getBookingID());
        System.out.println("Guest ID    : "
                + selectedBooking.getGuest().getGuestID());
        System.out.println("Guest Name  : "
                + selectedBooking.getGuest().getName());
        System.out.println("Room No     : " + room.getRoomNo());
        System.out.println("Room Type   : " + room.getRoomType());
        System.out.println("Check In    : " + selectedBooking.getCheckInDate());
        System.out.println("Check Out   : " + selectedBooking.getCheckOutDate());
        System.out.println("Status      : " + selectedBooking.getStatus());
        System.out.println("Room Status : " + room.getCurrentStatus());
        System.out.println("=======================================");
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