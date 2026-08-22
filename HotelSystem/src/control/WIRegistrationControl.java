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
        input = new Scanner(System.in);
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

        // Generate Guest ID

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
        System.out.println("Guest has been added to the waiting queue.");
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

        // Display Guesst
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
        // Search Guest
        while (true) {
            System.out.print("\nEnter Guest ID / Name / IC-Passport / Phone to Search (Enter for Exit): ");
            String keyword = input.nextLine().trim();
            if (keyword.isEmpty()) {
                return;
            }

            // search
            CircularArrayQueue<Guest> tempQueue = new CircularArrayQueue<>();
            while (!guestQueue.isEmpty()) {
                Guest guest = guestQueue.dequeue();
                if (selectedGuest == null
                        && matchGuest(guest, keyword)) {
                    selectedGuest = guest;
                }
                tempQueue.enqueue(guest);
            }

            // Restore Queue
            while (!tempQueue.isEmpty()) {
                guestQueue.enqueue(tempQueue.dequeue());
            }

            // Then search Booking
            if (selectedGuest == null) {
                for (int i = 0; i < book.size(); i++) {
                    Booking booking = book.get(i);
                    Guest guest = booking.getGuest();
                    if (matchGuest(guest, keyword)) {
                        selectedGuest = guest;
                        break;
                    }
                }
            }

            if (selectedGuest != null) {
                break;
            }

            System.out.println("No matching guest found.");
            System.out.println("Please enter a valid keyword.");
        }

        // Display Current Information
        System.out.println("\n========== Current Guest Information ==========");
        System.out.println("Guest ID       : " + selectedGuest.getGuestID());
        System.out.println("Guest Name     : " + selectedGuest.getName());
        System.out.println("IC / Passport  : "
                + selectedGuest.getICPassportNo());
        System.out.println("Phone Number   : "
                + selectedGuest.getPhoneNumber());
        System.out.println("===============================================");

        // Update Name
        String name;
        while (true) {
            System.out.print(
                    "\nEnter New Guest Name (0 for No Change): ");
            name = input.nextLine().trim();

            if (name.equals("0")) {
                name = selectedGuest.getName();
                break;

            } else if (name.isEmpty()) {
                System.out.println(
                        "Guest Name cannot be empty.");
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
            } else if (icPassportNo.length() < 8
                    || icPassportNo.length() > 12) {
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

    // Check wether the guest is Match
    private boolean matchGuest(Guest guest, String keyword) {

        return guest.getGuestID().equalsIgnoreCase(keyword)
                || guest.getName()
                        .toLowerCase()
                        .contains(keyword.toLowerCase())
                || guest.getICPassportNo()
                        .contains(keyword)
                || guest.getPhoneNumber()
                        .contains(keyword);
    }

    // Cancel Walk-In Registration
    public void CancelGuest() {

        System.out.println("\n===== Cancel Walk-In Registration =====");
        if (guestQueue.isEmpty()) {
            System.out.println("No guest is currently waiting for room assignment.");
            System.out.print("\nPress Enter to return...");
            input.nextLine();
            return;
        }

        // Display Waiting Guests
        System.out.println(
                "\n========== Guests Waiting for Room Assignment ==========");
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
            System.out.print("\nEnter Guest ID / Name / IC-Passport / Phone (0 for Exit): ");
            String keyword = input.nextLine().trim();
            if (keyword.equals("0")) {
                return;
            }
            CircularArrayQueue<Guest> tempQueue = new CircularArrayQueue<>();
            while (!guestQueue.isEmpty()) {
                Guest guest = guestQueue.dequeue();
                if (selectedGuest == null
                        && matchGuest(guest, keyword)) {
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
        System.out.println("Guest " + selectedGuest.getGuestID() + " has been removed from the waiting queue.");

        System.out.print("\nPress Enter to return...");
        input.nextLine();
    }

    // ===================================================================
    // Assign Room
    // ===================================================================
    public void AssignRoom() {

        System.out.println("\n============ Assign Room ============");
        System.out.println("1. Show All Rooms");
        System.out.println("2. Show Available Rooms Only");

        System.out.print(
                "Enter your choice (0 for Exit): ");
        String filterChoice = input.nextLine().trim();

        if (filterChoice.equals("0")) {
            return;
        }

        while (!filterChoice.equals("1")
                && !filterChoice.equals("2")) {
            System.out.print("Invalid choice. Enter 1 or 2 (0 for Exit): ");
            filterChoice = input.nextLine().trim();
            if (filterChoice.equals("0")) {
                return;
            }
        }

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
            if (filterChoice.equals("2")
                    && !room.isBookable()) {
                continue;
            }

            System.out.printf(
                    "%-10s %-15s %-20s %-20s%n",
                    room.getRoomNo(),
                    room.getRoomType(),
                    room.getCurrentStatus(),
                    room.getOccupancyStatus());
        }
        System.out.println("--------------------------------------------------------");

        if (availableRooms.isEmpty()) {
            System.out.println(
                    "\nNo available room for assignment.");
            System.out.print(
                    "Press Enter to return...");
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
    // Summary Report
    // ===================================================================
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