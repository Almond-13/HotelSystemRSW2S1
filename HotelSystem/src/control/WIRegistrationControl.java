package control;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import adt.ArrayList;
import adt.CircularArrayQueue;
import dao.RoomDAO;
import entity.*;

public class WIRegistrationControl {

    private CircularArrayQueue<Guest> guestQueue;
    private ArrayList<Booking> book;
    private ArrayList<Room> rooms;

    private Scanner input;
    private String lastError;

    // For Booking Part
    private int bookingCount = 1;
    private String bookingID = "B" + String.format("%03d", bookingCount++);

    // For Guest ID
    private int guestCounter = 1;

    private HousekeepingManager hkManager;

    public WIRegistrationControl(RoomDAO roomDAO, HousekeepingManager hkManager) {

        guestQueue = new CircularArrayQueue<>();
        this.input = input;
        rooms = roomDAO.getRooms();
        book = new ArrayList<>();
        lastError = "";
        guestCounter = 1;
        this.hkManager = hkManager;
    }

    public WIRegistrationControl(Scanner input, RoomDAO roomDAO, HousekeepingManager hkManager) {
        this.input = input;
        guestQueue = new CircularArrayQueue<>();
        rooms = roomDAO.getRooms();
        book = new ArrayList<>();
        lastError = "";
        guestCounter = 1;
        this.hkManager = hkManager;
    }

    public String getLastError() {
        return lastError;
    }

    // ===================================================================
    // Register Walk-In Guest
    // ===================================================================
    public void RGuest() {

        System.out.println("\n===== Register Guest =====");
        // Guest Name
        String name;

        while (true) {

            System.out.print("Guest Name (0 for Return): ");
            name = input.nextLine().trim();
            if (name.equals("0")) {
                return;
            } else if (name.isEmpty()) {
                System.out.println("Guest Name cannot be empty.");
            } else {
                break;
            }
        }
        // Generate Guest ID
        String guestID = String.format("G%03d", guestCounter++);

        // IC / Passport
        String icPassportNo;

        while (true) {

            System.out.print("IC / Passport No: ");
            icPassportNo = input.nextLine().trim();

            if (icPassportNo.isEmpty()) {

                System.out.println("IC / Passport No. cannot be empty.");

            } else if (icPassportNo.length() < 8
                    || icPassportNo.length() > 12) {

                System.out.println(
                        "IC / Passport No. must be 8-12 characters.");

            } else {
                break;
            }
        }

        // Phone Number
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

        // Create Guest
        Guest guest = new Guest(
                guestID,
                name,
                icPassportNo,
                phoneNumber);

        // Store Guest ONLY in Queue
        guestQueue.enqueue(guest);

        System.out.println("\nGuest registered successfully!");
        System.out.println("Guest ID: " + guestID);
    }

    // ===================================================================
    // Update Guest Information
    // ===================================================================
    public void UpdateGuestInfo() {

        System.out.println("\n===== Update Guest Information =====");
        boolean hasGuest = !guestQueue.isEmpty() || !book.isEmpty();

        if (!hasGuest) {
            System.out.println("No guest information available.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Display Guest
        System.out.printf(
                "%-10s %-20s %-20s %-15s%n",
                "Guest ID",
                "Guest Name",
                "IC / Passport",
                "Phone Number");

        System.out.println("----------------------------------------------------------------");
        CircularArrayQueue<Guest> tempDisplayQueue = new CircularArrayQueue<>();
        while (!guestQueue.isEmpty()) {
            Guest guest = guestQueue.dequeue();
            System.out.printf(
                    "%-10s %-20s %-20s %-15s%n",
                    guest.getGuestID(),
                    guest.getName(),
                    guest.getICPassportNo(),
                    guest.getPhoneNumber());
            tempDisplayQueue.enqueue(guest);
        }
        // Restore Queue
        while (!tempDisplayQueue.isEmpty()) {
            guestQueue.enqueue(tempDisplayQueue.dequeue());
        }
        // Display Guests from Booking
        for (int i = 0; i < book.size(); i++) {
            Booking booking = book.get(i);
            Guest guest = booking.getGuest();
            System.out.printf(
                    "%-10s %-20s %-20s %-15s%n",
                    guest.getGuestID(),
                    guest.getName(),
                    guest.getICPassportNo(),
                    guest.getPhoneNumber());
        }

        System.out.println("----------------------------------------------------------------");

        Guest selectedGuest = null;
        String guestID = null;
        // Search Guest
        while (true) {
            System.out.print("\nEnter Guest ID to Update (Enter for Exit): ");
            guestID = input.nextLine().trim();
            if (guestID.isEmpty()) {
                return;
            }

            // Take out Guest for edit
            CircularArrayQueue<Guest> tempQueue = new CircularArrayQueue<>();
            while (!guestQueue.isEmpty()) {
                Guest guest = guestQueue.dequeue();
                if (guest.getGuestID().equalsIgnoreCase(guestID)) {
                    selectedGuest = guest;
                }
                tempQueue.enqueue(guest); // Put the guest in to temporaly Queue
            }

            // Restore Queue
            while (!tempQueue.isEmpty()) {
                guestQueue.enqueue(tempQueue.dequeue());
            }

            // Find the guest if they have booked or assigned.
            if (selectedGuest == null) {
                for (int i = 0; i < book.size(); i++) {
                    Booking booking = book.get(i);
                    Guest guest = booking.getGuest(); // find guest in booking entity
                    if (guest.getGuestID().equalsIgnoreCase(guestID)) {
                        selectedGuest = guest;
                        break;
                    }
                }
            }
            if (selectedGuest != null) {
                break;
            }
            System.out.println("No matching guest found.");
        }

        // Display Current Information
        System.out.println("\n========== Current Guest Information ==========");
        System.out.println("Guest ID       : " + selectedGuest.getGuestID());
        System.out.println("Guest Name     : " + selectedGuest.getName());
        System.out.println("IC / Passport  : " + selectedGuest.getICPassportNo());
        System.out.println("Phone Number   : " + selectedGuest.getPhoneNumber());
        System.out.println("===============================================");

        // Update Name
        String name;
        while (true) {
            System.out.print("\nEnter New Guest Name (0 for No Change): ");
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
            System.out.print("Enter New IC / Passport No (0 for No Change): ");
            icPassportNo = input.nextLine().trim();

            if (icPassportNo.equals("0")) {
                icPassportNo = selectedGuest.getICPassportNo();
                break;
            } else if (icPassportNo.isEmpty()) {
                System.out.println("IC / Passport No. cannot be empty.");
            } else if (icPassportNo.length() < 8 || icPassportNo.length() > 12) {
                System.out.println("IC / Passport No. must be 8-12 characters.");
            } else {
                break;
            }
        }

        // Update Phone
        String phoneNumber;
        while (true) {
            System.out.print("Enter New Phone Number (0 for No Change): ");
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

        // Update Guest Object
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

    // ==============================================================
    // Cancel Walk-In Registration
    // ==============================================================
    public void CancelGuest() {

        System.out.println("\n===== Cancel Walk-In Registration =====");
        if (guestQueue.isEmpty()) {
            System.out.println("No guest is currently waiting for room assignment.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Display Waiting Guests
        System.out.println("\n========== Guests Waiting for Room Assignment ==========");
        System.out.printf(
                "%-10s %-20s %-20s %-15s%n",
                "Guest ID",
                "Guest Name",
                "IC / Passport",
                "Phone Number");
        System.out.println(
                "---------------------------------------------------------------");

        CircularArrayQueue<Guest> tempDisplayQueue = new CircularArrayQueue<>();

        while (!guestQueue.isEmpty()) {
            Guest guest = guestQueue.dequeue();
            System.out.printf(
                    "%-10s %-20s %-20s %-15s%n",
                    guest.getGuestID(),
                    guest.getName(),
                    guest.getICPassportNo(),
                    guest.getPhoneNumber());

            tempDisplayQueue.enqueue(guest);
        }

        // Restore Queue
        while (!tempDisplayQueue.isEmpty()) {
            guestQueue.enqueue(tempDisplayQueue.dequeue());
        }
        System.out.println("---------------------------------------------------------------");

        // Search Guest
        Guest selectedGuest = null;
        while (true) {
            System.out.print("\nEnter Guest ID (Enter for Exit): ");
            String CguestID = input.nextLine().trim();
            if (CguestID.isEmpty()) {
                return;
            }
            CircularArrayQueue<Guest> tempQueue = new CircularArrayQueue<>();
            while (!guestQueue.isEmpty()) {
                Guest guest = guestQueue.dequeue();
                if (guest.getGuestID().equalsIgnoreCase(CguestID)) {
                    selectedGuest = guest;
                }
                tempQueue.enqueue(guest);
            }
            // Restore Queue
            while (!tempQueue.isEmpty()) {
                guestQueue.enqueue(tempQueue.dequeue());
            }
            if (selectedGuest == null) {
                System.out.println(
                        "No matching guest found.");
                System.out.println(
                        "Please enter a valid Guest ID, Name, IC / Passport, or Phone Number.");
                continue;
            }
            break;
        }

        // Display Selected Guest
        System.out.println("\n========== Guest Information ==========");
        System.out.println("Guest ID       : " + selectedGuest.getGuestID());
        System.out.println("Guest Name     : " + selectedGuest.getName());
        System.out.println("IC / Passport  : " + selectedGuest.getICPassportNo());
        System.out.println("Phone Number   : " + selectedGuest.getPhoneNumber());
        System.out.println("Status         : Waiting for Room Assignment");
        System.out.println("=======================================");

        // Confirmation
        while (true) {
            System.out.print("\nConfirm Cancel Registration? (Y/N): ");
            String confirm = input.nextLine().trim();

            if (confirm.equalsIgnoreCase("Y")) {
                break;
            } else if (confirm.equalsIgnoreCase("N")) {
                System.out.println("Cancellation cancelled.");
                return;
            } else {
                System.out.println("Invalid input. Please enter Y or N.");
            }
        }

        // Remove selected Guest from Queue
        CircularArrayQueue<Guest> tempQueue = new CircularArrayQueue<>();
        while (!guestQueue.isEmpty()) {
            Guest guest = guestQueue.dequeue();
            if (!guest.getGuestID()
                    .equalsIgnoreCase(selectedGuest.getGuestID())) {
                tempQueue.enqueue(guest);
            }
        }

        // Restore Queue
        while (!tempQueue.isEmpty()) {
            guestQueue.enqueue(tempQueue.dequeue());
        }

        System.out.println("\nWalk-In Registration cancelled successfully.");
        System.out.println("Guest " + selectedGuest.getGuestID() + " has been removed.");

        System.out.print("\nPress Enter to return...");
        input.nextLine();
    }

    // ===================================================================
    // Assign Room
    // ===================================================================
    public void AssignRoom() {

        System.out.println("\n============ Assign Room ============");
        // Available Rooms
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.isBookable()) {
                availableRooms.add(room);
            }
        }
        // Display Room Status
        System.out.println("\n=================== Room Status ====================");
        System.out.printf(
                "%-10s %-15s %-20s %-20s%n",
                "Room No",
                "Room Type",
                "Current Status",
                "Occupancy Status");
        System.out.println(
                "--------------------------------------------------------");
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            System.out.printf(
                    "%-10s %-15s %-20s %-20s%n",
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

        if (guestQueue.isEmpty()) {
            System.out.println("\nNo guest waiting for room assignment.");
            System.out.print("Press Enter to return...");
            input.nextLine();
            return;
        }

        // Get First Guest
        Guest guest = guestQueue.peek();
        System.out.println("\n========== Guest Waiting ==========");
        System.out.println("Guest ID       : " + guest.getGuestID());
        System.out.println("Guest Name     : " + guest.getName());
        System.out.println("IC / Passport  : " + guest.getICPassportNo());
        System.out.println("Phone Number   : " + guest.getPhoneNumber());
        System.out.println("===================================");

        // Select Room
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
        Booking booking = new Booking(
                bookingID,
                guest,
                selectedRoom,
                "",
                "",
                "Assigned");

        book.add(booking);

        // Room becomes unavailable
        selectedRoom.setOccupancyStatus("Unavailable");
        // Remove Guest from Queue
        guestQueue.dequeue();

        System.out.println("\nRoom assigned successfully!");
        System.out.println("\n========== Assignment Summary ==========");
        System.out.println("Booking ID   : " + bookingID);
        System.out.println("Guest ID     : " + guest.getGuestID());
        System.out.println("Guest Name   : " + guest.getName());
        System.out.println("Room No      : " + selectedRoom.getRoomNo());
        System.out.println("Room Type    : " + selectedRoom.getRoomType());
        System.out.println("Status       : " + booking.getStatus());
        System.out.println("========================================");

        // Generate Next Booking ID
        bookingID = "B" + String.format(
                "%03d",
                bookingCount++);
    }

    // ===================================================================
    // Check-In
    // ===================================================================
    public void CheckIn() {

        System.out.println(
                "\n============ Check In ============");

        boolean found = false;

        System.out.printf(
                "%-12s %-12s %-20s %-12s %-15s%n",
                "Booking ID",
                "Guest ID",
                "Guest Name",
                "Room No",
                "Status");

        System.out.println("----------------------------------------------------------------");
        for (int i = 0; i < book.size(); i++) {
            Booking booking = book.get(i);
            if (booking.getStatus().equals("Assigned")) {
                System.out.printf(
                        "%-12s %-12s %-20s %-12s %-15s%n",
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
                if (booking.getGuest()
                        .getGuestID()
                        .equalsIgnoreCase(guestID)
                        && booking.getStatus()
                                .equals("Assigned")) {
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

        // Check Room Status
        if (!room.getCurrentStatus()
                .equals("Clean")) {
            System.out.println("\nThe assigned room is not ready for check-in yet.");
            System.out.println("Room Status : " + room.getCurrentStatus());
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Guest Information
        System.out.println("\n========== Guest Information ==========");
        System.out.println("Guest ID       : " + guest.getGuestID());
        System.out.println("Guest Name     : " + guest.getName());
        System.out.println("IC / Passport  : " + guest.getICPassportNo());
        System.out.println("Phone Number   : " + guest.getPhoneNumber());
        System.out.println("Room No        : " + room.getRoomNo());
        System.out.println("Room Type      : " + room.getRoomType());
        System.out.println("Status         : " + selectedBooking.getStatus());
        System.out.println("Check In       : " + selectedBooking.getCheckInDate());
        System.out.println("Check Out      : " + selectedBooking.getCheckOutDate());
        System.out.println("=======================================");
        System.out.print("\nConfirm Check In? (Y/N): ");
        String confirm = input.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Check In cancelled.");
            return;
        }

        // Generate Check-In Time
        LocalDateTime checkInDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String checkInStr = checkInDate.format(formatter);
        // Create Updated Booking
        Booking checkedInBooking = new Booking(
                selectedBooking.getBookingID(),
                selectedBooking.getGuest(),
                selectedBooking.getRoom(),
                checkInStr,
                "",
                "Checked In");

        // Replace Assigned Booking
        for (int i = 0; i < book.size(); i++) {
            if (book.get(i) == selectedBooking) {
                book.remove(i);
                book.add(checkedInBooking);
                break;
            }
        }
        System.out.println("\nCheck In Successful!");
        System.out.println("\n========== Check In Summary ==========");
        System.out.println("Booking ID   : " + checkedInBooking.getBookingID());
        System.out.println("Guest ID       : " + guest.getGuestID());
        System.out.println("Guest Name     : " + guest.getName());
        System.out.println("IC / Passport  : " + guest.getICPassportNo());
        System.out.println("Phone Number   : " + guest.getPhoneNumber());
        System.out.println("Room No        : " + room.getRoomNo());
        System.out.println("Room Type      : " + room.getRoomType());
        System.out.println("Status         : " + checkedInBooking.getStatus());
        System.out.println("Check In       : " + checkedInBooking.getCheckInDate());
        System.out.println("Check Out      : " + checkedInBooking.getCheckOutDate());
        System.out.println("======================================");
    }

    // ===================================================================
    // Check-Out
    // ===================================================================
    public void CheckOut() {

        System.out.println("\n============ Check Out ============");
        boolean found = false;
        System.out.printf(
                "%-12s %-20s %-12s %-15s %-15s%n",
                "Booking ID",
                "Guest Name",
                "Room No",
                "Check In",
                "Check Out");
        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < book.size(); i++) {

            Booking booking = book.get(i);
            if (booking.getStatus()
                    .equals("Checked In")) {

                System.out.printf(
                        "%-12s %-20s %-12s %-15s %-15s%n",
                        booking.getBookingID(),
                        booking.getGuest().getName(),
                        booking.getRoom().getRoomNo(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate());
                found = true;
            }
        }

        if (!found) {
            System.out.println(
                    "No occupied room.");
            System.out.print(
                    "\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Select Booking
        Booking selectedBooking = null;
        while (true) {
            System.out.print(
                    "\nEnter Room Number for Check Out (0 for Exit): ");
            String keyword = input.nextLine().trim();
            if (keyword.equals("0")) {
                return;
            }
            for (int i = 0; i < book.size(); i++) {
                Booking booking = book.get(i);
                if (booking.getStatus()
                        .equals("Checked In")
                        && booking.getRoom()
                                .getRoomNo()
                                .equalsIgnoreCase(keyword)) {
                    selectedBooking = booking;
                    break;
                }
            }

            if (selectedBooking != null) {
                break;
            }
            System.out.println("Invalid Room Number.");
            System.out.println("Please enter a Room Number that is currently Checked In.");
        }
        // Generate Check-Out Time
        LocalDateTime checkOutDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String checkOutStr = checkOutDate.format(formatter);

        // Create Checked-Out Booking
        Booking checkedOutBooking = new Booking(
                selectedBooking.getBookingID(),
                selectedBooking.getGuest(),
                selectedBooking.getRoom(),
                selectedBooking.getCheckInDate(),
                checkOutStr,
                "Checked Out");

        // Replace Booking
        for (int i = 0; i < book.size(); i++) {
            if (book.get(i) == selectedBooking) {
                book.remove(i);
                book.add(checkedOutBooking);
                break;
            }
        }
        // Get Room
        Room room = checkedOutBooking.getRoom();
        // Send Room to Housekeeping
        hkManager.recordCheckoutDirty(
                room.getRoomNo(),
                "Checkout");

        // Update Occupancy Status
        room.updateOStatus();
        System.out.println("\n========== Check Out Summary ==========");
        System.out.println("Booking ID  : " + checkedOutBooking.getBookingID());
        System.out.println("Guest ID    : " + checkedOutBooking.getGuest().getGuestID());
        System.out.println("Guest Name  : " + checkedOutBooking.getGuest().getName());
        System.out.println("Room No     : " + room.getRoomNo());
        System.out.println("Room Type   : " + room.getRoomType());
        System.out.println("Check In    : " + checkedOutBooking.getCheckInDate());
        System.out.println("Check Out   : " + checkedOutBooking.getCheckOutDate());
        System.out.println("Status      : " + checkedOutBooking.getStatus());
        System.out.println("Room Status : " + room.getCurrentStatus());
        System.out.println("=======================================");
    }

    // ===================================================================
    // Report 1 - Walk-In Booking Report
    // ===================================================================
    public void SReport() {

        System.out.println("\n================ WALK-IN BOOKING SUMMARY REPORT ================");

        if (book.isEmpty()) {
            System.out.println("No guest or booking records available.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }
        // Filter Booking Status
        System.out.println("\nSelect Booking Status Filter");
        System.out.println("1. All");
        System.out.println("2. Assigned");
        System.out.println("3. Checked In");
        System.out.println("4. Checked Out");

        System.out.print("Enter choice: ");
        String statusChoice = input.nextLine().trim();

        while (!statusChoice.equals("1")
                && !statusChoice.equals("2")
                && !statusChoice.equals("3")
                && !statusChoice.equals("4")) {

            System.out.print("Invalid choice. Enter 1-4: ");
            statusChoice = input.nextLine().trim();
        }

        String statusFilter = "All";
        if (statusChoice.equals("2")) {
            statusFilter = "Assigned";
        } else if (statusChoice.equals("3")) {
            statusFilter = "Checked In";
        } else if (statusChoice.equals("4")) {
            statusFilter = "Checked Out";
        }

        // Filter Room Type
        System.out.println("\nSelect Room Type Filter");
        System.out.println("1. All");
        System.out.println("2. Standard");
        System.out.println("3. Deluxe");
        System.out.println("4. Suite");

        System.out.print("Enter choice: ");
        String roomTypeChoice = input.nextLine().trim();

        while (!roomTypeChoice.equals("1")
                && !roomTypeChoice.equals("2")
                && !roomTypeChoice.equals("3")
                && !roomTypeChoice.equals("4")) {

            System.out.print("Invalid choice. Enter 1-4: ");
            roomTypeChoice = input.nextLine().trim();
        }

        String roomTypeFilter = "All";
        if (roomTypeChoice.equals("2")) {
            roomTypeFilter = "Standard";
        } else if (roomTypeChoice.equals("3")) {
            roomTypeFilter = "Deluxe";
        } else if (roomTypeChoice.equals("4")) {
            roomTypeFilter = "Suite";
        }

        // Searching
        System.out.print("\nSearch Guest ID / Guest Name / Room No " + "(Enter for All): ");

        String keyword = input.nextLine().trim();

        // Apply Multiple Criteria Filters + Searching
        ArrayList<Booking> filteredBookings = new ArrayList<>();

        for (int i = 0; i < book.size(); i++) {
            Booking booking = book.get(i);
            boolean statusMatch = statusFilter.equals("All")
                    || booking.getStatus().equalsIgnoreCase(statusFilter);

            boolean roomTypeMatch = roomTypeFilter.equals("All")
                    || booking.getRoom().getRoomType().toString().equalsIgnoreCase(roomTypeFilter);

            boolean searchMatch = keyword.isEmpty()
                    || booking.getBookingID().equalsIgnoreCase(keyword)
                    || booking.getGuest().getGuestID().equalsIgnoreCase(keyword)
                    || booking.getGuest().getName().toLowerCase().contains(keyword.toLowerCase())
                    || booking.getRoom().getRoomNo().equalsIgnoreCase(keyword);

            if (statusMatch && roomTypeMatch && searchMatch) {
                filteredBookings.add(booking);
            }
        }

        // Display Result
        if (filteredBookings.isEmpty()) {

            System.out.println("\nNo matching booking records found.");
            System.out.println("\nApplied Criteria:");
            System.out.println("Status    : " + statusFilter);
            System.out.println("Room Type : " + roomTypeFilter);
            System.out.println("Search    : " + (keyword.isEmpty() ? "All" : keyword));

            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Sorting
        System.out.println("\nSelect Sorting Method");
        System.out.println("1. Booking ID");
        System.out.println("2. Guest Name");
        System.out.println("3. Room Number");

        System.out.print("Enter choice: ");
        String sortChoice = input.nextLine().trim();

        while (!sortChoice.equals("1")
                && !sortChoice.equals("2")
                && !sortChoice.equals("3")) {

            System.out.print("Invalid choice. Enter 1-3: ");
            sortChoice = input.nextLine().trim();
        }

        if (sortChoice.equals("1")) {
            filteredBookings = sortBookingsByBookingID(filteredBookings);
        } else if (sortChoice.equals("2")) {
            filteredBookings = sortBookingsByGuestName(filteredBookings);
        } else {
            filteredBookings = sortBookingsByRoomNo(filteredBookings);
        }

        // Report
        System.out.println("\n==================== FILTERED BOOKING RECORDS ====================");
        System.out.println("Status Filter    : " + statusFilter);
        System.out.println("Room Type Filter : " + roomTypeFilter);
        System.out.println("Search Keyword   : "
                + (keyword.isEmpty() ? "All" : keyword));

        String sortingMethod;
        if (sortChoice.equals("1")) {
            sortingMethod = "Booking ID";
        } else if (sortChoice.equals("2")) {
            sortingMethod = "Guest Name";
        } else {
            sortingMethod = "Room Number";
        }
        System.out.println("Sorted By        : " + sortingMethod);
        System.out.println("--------------------------------------------------------------------------");

        System.out.printf(
                "%-10s %-12s %-18s %-15s %-12s %-12s %-15s%n",
                "Guest ID",
                "Booking ID",
                "Guest Name",
                "Room No",
                "Room Type",
                "Status",
                "Check In");

        System.out.println(
                "--------------------------------------------------------------------------");

        // Display Filtering Report
        for (int i = 0; i < filteredBookings.size(); i++) {

            Booking booking = filteredBookings.get(i);
            System.out.printf(
                    "%-10s %-12s %-18s %-15s %-12s %-15s %-15s%n",
                    booking.getGuest().getGuestID(),
                    booking.getBookingID(),
                    booking.getGuest().getName(),
                    booking.getRoom().getRoomNo(),
                    booking.getRoom().getRoomType(),
                    booking.getStatus(),
                    booking.getCheckInDate());
        }
        System.out.print("\nPress Enter to return...");
        input.nextLine();
    }

    // ===================================================================
    // Sorting - Booking ID
    // ===================================================================
    private ArrayList<Booking> sortBookingsByBookingID(
            ArrayList<Booking> source) {

        ArrayList<Booking> remaining = new ArrayList<>();
        ArrayList<Booking> sorted = new ArrayList<>();

        // Copy records
        for (int i = 0; i < source.size(); i++) {
            remaining.add(source.get(i));
        }

        // Selection Sort
        while (!remaining.isEmpty()) {
            int minIndex = 0;
            for (int i = 1; i < remaining.size(); i++) {
                if (remaining.get(i)
                        .getBookingID()
                        .compareToIgnoreCase(
                                remaining.get(minIndex)
                                        .getBookingID()) < 0) {
                    minIndex = i;
                }
            }
            sorted.add(remaining.get(minIndex));
            remaining.remove(minIndex);
        }

        return sorted;
    }

    // Sorting - Guest Name
    private ArrayList<Booking> sortBookingsByGuestName(
            ArrayList<Booking> source) {
        ArrayList<Booking> remaining = new ArrayList<>();
        ArrayList<Booking> sorted = new ArrayList<>();

        for (int i = 0; i < source.size(); i++) {
            remaining.add(source.get(i));
        }

        // Selection Sort
        while (!remaining.isEmpty()) {
            int minIndex = 0;
            for (int i = 1; i < remaining.size(); i++) {
                String currentName = remaining.get(i)
                        .getGuest()
                        .getName();

                String minName = remaining.get(minIndex)
                        .getGuest()
                        .getName();

                if (currentName.compareToIgnoreCase(minName) < 0) {
                    minIndex = i;
                }
            }

            sorted.add(remaining.get(minIndex));
            remaining.remove(minIndex);
        }

        return sorted;
    }

    // Sorting - Room Number
    private ArrayList<Booking> sortBookingsByRoomNo(
            ArrayList<Booking> source) {

        ArrayList<Booking> remaining = new ArrayList<>();
        ArrayList<Booking> sorted = new ArrayList<>();

        for (int i = 0; i < source.size(); i++) {
            remaining.add(source.get(i));
        }

        // Selection Sort
        while (!remaining.isEmpty()) {

            int minIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                String currentRoom = remaining.get(i)
                        .getRoom()
                        .getRoomNo();
                String minRoom = remaining.get(minIndex)
                        .getRoom()
                        .getRoomNo();
                if (currentRoom.compareToIgnoreCase(minRoom) < 0) {
                    minIndex = i;
                }
            }
            sorted.add(remaining.get(minIndex));
            remaining.remove(minIndex);
        }
        return sorted;
    }

    // ===================================================================
    // Report 2 - Room Occupancy & Guest Activity Report
    // ===================================================================
    public void RoomReport() {

        System.out.println("\n================ ROOM OCCUPANCY & GUEST ACTIVITY REPORT ================");

        if (book.isEmpty()) {
            System.out.println("No booking records available.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // R2 Filter Room Type
        System.out.println("\nSelect Room Type Filter");
        System.out.println("1. All");
        System.out.println("2. Standard");
        System.out.println("3. Deluxe");
        System.out.println("4. Suite");

        System.out.print("Enter choice: ");
        String roomTypeChoice = input.nextLine().trim();

        while (!roomTypeChoice.equals("1")
                && !roomTypeChoice.equals("2")
                && !roomTypeChoice.equals("3")
                && !roomTypeChoice.equals("4")) {

            System.out.print("Invalid choice. Enter 1-4: ");
            roomTypeChoice = input.nextLine().trim();
        }

        String roomTypeFilter = "All";

        if (roomTypeChoice.equals("2")) {
            roomTypeFilter = "Standard";
        } else if (roomTypeChoice.equals("3")) {
            roomTypeFilter = "Deluxe";
        } else if (roomTypeChoice.equals("4")) {
            roomTypeFilter = "Suite";
        }

        // R2 Filter Room Status
        System.out.println("\nSelect Guest Activity Status");
        System.out.println("1. All");
        System.out.println("2. Assigned");
        System.out.println("3. Checked In");
        System.out.println("4. Checked Out");

        System.out.print("Enter choice: ");
        String statusChoice = input.nextLine().trim();

        while (!statusChoice.equals("1")
                && !statusChoice.equals("2")
                && !statusChoice.equals("3")
                && !statusChoice.equals("4")) {

            System.out.print("Invalid choice. Enter 1-4: ");
            statusChoice = input.nextLine().trim();
        }

        String statusFilter = "All";
        if (statusChoice.equals("2")) {
            statusFilter = "Assigned";
        } else if (statusChoice.equals("3")) {
            statusFilter = "Checked In";
        } else if (statusChoice.equals("4")) {
            statusFilter = "Checked Out";
        }

        // Searching===========================================
        System.out.print("\nSearch Guest ID / Guest Name / Room Number " + "(Enter for All): ");

        String keyword = input.nextLine().trim();
        // Apply Multiple Criteria=============================
        ArrayList<Booking> filteredBookings = new ArrayList<>();
        for (int i = 0; i < book.size(); i++) {
            Booking booking = book.get(i);
            boolean roomTypeMatch = roomTypeFilter.equals("All") || booking.getRoom()
                    .getRoomType()
                    .toString().equalsIgnoreCase(roomTypeFilter);
            boolean statusMatch = statusFilter.equals("All") || booking.getStatus()
                    .equalsIgnoreCase(statusFilter);
            boolean searchMatch = keyword.isEmpty() || booking.getGuest()
                    .getGuestID()
                    .equalsIgnoreCase(keyword)
                    || booking.getGuest()
                            .getName()
                            .toLowerCase()
                            .contains(keyword.toLowerCase())
                    || booking.getRoom()
                            .getRoomNo()
                            .equalsIgnoreCase(keyword);

            if (roomTypeMatch
                    && statusMatch
                    && searchMatch) {

                filteredBookings.add(booking);
            }
        }

        // No Matching Result
        if (filteredBookings.isEmpty()) {

            System.out.println("\nNo matching room activity found.");
            System.out.println("\nApplied Criteria:");
            System.out.println("Room Type : " + roomTypeFilter);
            System.out.println("Status    : " + statusFilter);
            System.out.println("Search    : "
                    + (keyword.isEmpty() ? "All" : keyword));

            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Sorting
        System.out.println("\nSelect Sorting Method");
        System.out.println("1. Room Number");
        System.out.println("2. Room Type");
        System.out.println("3. Guest Name");

        System.out.print("Enter choice: ");
        String sortChoice = input.nextLine().trim();

        while (!sortChoice.equals("1")
                && !sortChoice.equals("2")
                && !sortChoice.equals("3")) {

            System.out.print("Invalid choice. Enter 1-3: ");
            sortChoice = input.nextLine().trim();
        }

        if (sortChoice.equals("1")) {
            filteredBookings = sortBookingsByRoomNo(filteredBookings);
        } else if (sortChoice.equals("2")) {
            filteredBookings = sortBookingsByRoomType(filteredBookings);
        } else {
            filteredBookings = sortBookingsByGuestName(filteredBookings);
        }

        // Display Report
        System.out.println("\n==================== ROOM ACTIVITY RECORDS ====================");

        System.out.println("Room Type Filter : " + roomTypeFilter);
        System.out.println("Status Filter    : " + statusFilter);
        System.out.println("Search Keyword   : " + (keyword.isEmpty() ? "All" : keyword));

        String sortingMethod;
        if (sortChoice.equals("1")) {
            sortingMethod = "Room Number";
        } else if (sortChoice.equals("2")) {
            sortingMethod = "Room Type";
        } else {
            sortingMethod = "Guest Name";
        }

        System.out.println("Sorted By        : " + sortingMethod);
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(
                "%-10s %-15s %-12s %-20s %-15s %-18s%n",
                "Room No",
                "Room Type",
                "Guest ID",
                "Guest Name",
                "Status",
                "Check In");

        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < filteredBookings.size(); i++) {
            Booking booking = filteredBookings.get(i);
            System.out.printf(
                    "%-10s %-15s %-12s %-20s %-15s %-18s%n",
                    booking.getRoom().getRoomNo(),
                    booking.getRoom().getRoomType(),
                    booking.getGuest().getGuestID(),
                    booking.getGuest().getName(),
                    booking.getStatus(),
                    booking.getCheckInDate());
        }

        System.out.println("--------------------------------------------------------------------------");

        System.out.print("\nPress Enter to return...");
        input.nextLine();
    }

    // Sorting - Room Type
    private ArrayList<Booking> sortBookingsByRoomType(
            ArrayList<Booking> source) {

        ArrayList<Booking> remaining = new ArrayList<>();
        ArrayList<Booking> sorted = new ArrayList<>();

        for (int i = 0; i < source.size(); i++) {
            remaining.add(source.get(i));
        }

        // Selection Sort
        while (!remaining.isEmpty()) {

            int minIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                String currentType = remaining.get(i)
                        .getRoom()
                    .getRoomType().toString();
                String minType = remaining.get(minIndex)
                        .getRoom()
                    .getRoomType().toString();
                if (currentType.compareToIgnoreCase(minType) < 0) {
                    minIndex = i;
                }
            }

            sorted.add(remaining.get(minIndex));
            remaining.remove(minIndex);
        }

        return sorted;
    }
}