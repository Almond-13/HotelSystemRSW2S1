package boundary;

import control.WIRegistrationControl;
import control.VipRoomAllocationControl;
import dao.RoomDAO;
import entity.Booking;
import entity.LoyaltyTier;
import entity.LoyaltyTierMember;
import entity.Room;
import entity.RoomType;
import entity.VipAllocationRequest;
import java.util.Scanner;

public class VIPUI {
    private Scanner input;
    private VipRoomAllocationControl control;
    private WIRegistrationControl walkInControl;

    public VIPUI(Scanner input, RoomDAO roomDAO) {
        this(input, roomDAO, null);
    }

    public VIPUI(Scanner input, RoomDAO roomDAO, WIRegistrationControl walkInControl) {
        this.input = input;
        this.walkInControl = walkInControl;
        control = new VipRoomAllocationControl(roomDAO);
        seedVipRequests();
    }

    public void run() {
        int choice;
        do {
            printMenu();
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    registerLoyaltyTierMember();
                    break;
                case 2:
                    registerVipRoomRequest();
                    break;
                case 3:
                    allocateNextVip();
                    break;
                case 4:
                    displayNextVip();
                    break;
                case 5:
                    searchVipRequest();
                    break;
                case 6:
                    cancelVipRequest();
                    break;
                case 7:
                    displayWaitingReport();
                    break;
                case 8:
                    displayAllocatedReport();
                    break;
                case 9:
                    displayReadyRoomsReport();
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 0);
    }

    private void printMenu() {
        System.out.println("\n========== VIP Room Allocation ==========");
        System.out.println("1. Register Loyalty Tier Member");
        System.out.println("2. Register VIP Room Request");
        System.out.println("3. Allocate Room to Next VIP Guest");
        System.out.println("4. View Next VIP Guest");
        System.out.println("5. Search VIP Room Request");
        System.out.println("6. Cancel VIP Room Request");
        System.out.println("7. Report: VIP Waiting Queue");
        System.out.println("8. Report: VIP Allocated Rooms");
        System.out.println("9. View Ready Rooms");
        System.out.println("0. Back to Main Menu");
    }

    private void registerLoyaltyTierMember() {
        String guestName = readRequiredString("Guest name: ");
        String icPassportNo = readIcPassportNo();
        String phoneNumber = readPhoneNumber();
        printTierThresholds();
        int points = readNonNegativeInt("Historical loyalty points: ");

        LoyaltyTierMember member = control.registerLoyaltyTierMember(guestName, icPassportNo, phoneNumber, points);
        if (member == null) {
            System.out.println("Unable to register member. Member storage is full.");
            return;
        }

        System.out.println("Member registered: " + member);
    }

    private void registerVipRoomRequest() {
        if (control.getMemberCount() == 0) {
            System.out.println("No loyalty tier member found. Please register a member first.");
            return;
        }

        String confirmationNumber = readConfirmationNumber();
        String memberId = readRequiredString("Member ID: ");
        LoyaltyTierMember member = control.findMemberById(memberId);
        if (member == null) {
            System.out.println("Member ID not found. Please register the loyalty tier member first.");
            return;
        }

        System.out.println("Selected member: " + member);
        System.out.println("Highest eligible room type: " + member.getLoyaltyTier().getHighestEligibleRoomType());
        RoomType roomType = readRoomType(member.getLoyaltyTier());

        VipAllocationRequest request = control.addVipRequest(
                confirmationNumber, memberId, roomType);
        System.out.println("VIP room request registered: " + request);
    }

    private void allocateNextVip() {
        VipAllocationRequest allocated = control.allocateNextVipRoom();
        if (allocated == null) {
            System.out.println("No waiting VIP or no available clean room.");
        } else {
            System.out.println("Allocated: " + allocated);
            createWalkInBookingForVip(allocated);
        }
    }

    private void createWalkInBookingForVip(VipAllocationRequest allocated) {
        if (walkInControl == null) {
            System.out.println("Walk-in booking record was not created because Walk-in module is not linked.");
            return;
        }

        Booking booking = walkInControl.createVipAssignedBooking(
                allocated.getGuestProfile().getConfirmationNumber(),
                allocated.getGuestProfile().getGuestName(),
                allocated.getGuestProfile().getIcPassportNo(),
                allocated.getGuestProfile().getPhoneNumber(),
                allocated.getAllocatedRoom());

        if (booking == null) {
            System.out.println("Walk-in booking record was not created: " + walkInControl.getLastError());
            return;
        }

        System.out.println("Walk-in booking record created for check-in.");
        System.out.println("Booking ID: " + booking.getBookingID());
        System.out.println("Guest ID  : " + booking.getGuest().getGuestID());
    }

    private void displayNextVip() {
        VipAllocationRequest next = control.peekNextVip();
        if (next == null) {
            System.out.println("No VIP is waiting.");
        } else {
            System.out.println("Next VIP: " + next);
        }
    }

    private void searchVipRequest() {
        String confirmationNumber = readString("VIP Request ID to search: ");
        VipAllocationRequest waitingRequest = control.searchWaitingRequest(confirmationNumber);
        if (waitingRequest != null) {
            System.out.println("Found in waiting queue: " + waitingRequest);
            return;
        }

        VipAllocationRequest allocatedRequest = control.searchAllocatedRequest(confirmationNumber);
        if (allocatedRequest != null) {
            System.out.println("Found in allocated records: " + allocatedRequest);
            return;
        }

        System.out.println("No VIP request found for VIP Request ID " + confirmationNumber + ".");
    }

    private void cancelVipRequest() {
        String confirmationNumber = readString("VIP Request ID to cancel: ");
        VipAllocationRequest request = control.searchWaitingRequest(confirmationNumber);
        if (request == null) {
            System.out.println("Only waiting requests can be cancelled. No waiting request found.");
            return;
        }

        System.out.println("Selected request: " + request);
        String confirm = readString("Confirm cancellation? (Y/N): ");
        if (confirm.equalsIgnoreCase("Y")) {
            control.cancelWaitingRequest(confirmationNumber);
            System.out.println("VIP request cancelled.");
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    private void displayWaitingReport() {
        printReportHeader("VIP WAITING QUEUE REPORT");
        VipAllocationRequest[] report = filterAndSortRequestReport(control.getWaitingReport(), false);
        if (report.length == 0) {
            System.out.println("No waiting VIP requests match the selected filters.");
            printLine();
            return;
        }
        System.out.printf("%-5s %-10s %-20s %-12s %-10s %-15s %-10s%n",
                "No.", "Request", "Guest Name", "Tier", "Points", "Preferred Room", "Status");
        printLine();
        for (int i = 0; i < report.length; i++) {
            System.out.printf("%-5d %-10s %-20s %-12s %-10d %-15s %-10s%n",
                    i + 1,
                    report[i].getGuestProfile().getConfirmationNumber(),
                    report[i].getGuestProfile().getGuestName(),
                    report[i].getGuestProfile().getLoyaltyTier(),
                    report[i].getGuestProfile().getRewardPoints(),
                    report[i].getPreferredRoomType(),
                    "Waiting");
        }
        printLine();
        System.out.println("Total waiting VIP requests: " + report.length);
        printLine();
    }

    private void displayAllocatedReport() {
        printReportHeader("VIP ALLOCATED ROOMS REPORT");
        VipAllocationRequest[] report = filterAndSortRequestReport(control.getAllocatedReport(), true);
        if (report.length == 0) {
            System.out.println("No allocated VIP rooms match the selected filters.");
            printLine();
            return;
        }
        System.out.printf("%-5s %-10s %-20s %-12s %-10s %-12s %-12s%n",
                "No.", "Request", "Guest Name", "Tier", "Room No", "Room Type", "Status");
        printLine();
        for (int i = 0; i < report.length; i++) {
            Room room = report[i].getAllocatedRoom();
            System.out.printf("%-5d %-10s %-20s %-12s %-10s %-12s %-12s%n",
                    i + 1,
                    report[i].getGuestProfile().getConfirmationNumber(),
                    report[i].getGuestProfile().getGuestName(),
                    report[i].getGuestProfile().getLoyaltyTier(),
                    room == null ? "-" : room.getRoomNo(),
                    room == null ? "-" : room.getRoomType(),
                    "Allocated");
        }
        printLine();
        System.out.println("Total allocated VIP rooms: " + report.length);
        printLine();
    }

    private void displayMemberReport() {
        System.out.println("\nLoyalty Tier Member Report");
        LoyaltyTierMember[] report = control.getMemberReport();
        if (report.length == 0) {
            System.out.println("No loyalty tier members registered.");
            return;
        }
        for (int i = 0; i < report.length; i++) {
            System.out.println((i + 1) + ". " + report[i]);
        }
    }

    private void displayReadyRoomsReport() {
        System.out.println("\nReady Rooms Report");
        Room[] report = control.getReadyRoomsReport();
        if (report.length == 0) {
            System.out.println("No rooms are ready.");
            return;
        }
        for (int i = 0; i < report.length; i++) {
            System.out.println((i + 1) + ". Room "
                    + report[i].getRoomNo() + " | "
                    + report[i].getRoomType() + " | "
                    + report[i].getCurrentStatus() + " | "
                    + report[i].getOccupancyStatus());
        }
    }

    private void displayAllocationSummaryReport() {
        System.out.println("\nVIP Allocation Summary Report");
        System.out.println("Total waiting requests  : " + control.getWaitingCount());
        System.out.println("Total allocated requests: " + control.getAllocatedCount());
        System.out.println();
        System.out.println("Waiting requests by loyalty tier");
        System.out.println("Platinum : " + control.countWaitingByTier(LoyaltyTier.PLATINUM));
        System.out.println("Gold     : " + control.countWaitingByTier(LoyaltyTier.GOLD));
        System.out.println("Silver   : " + control.countWaitingByTier(LoyaltyTier.SILVER));
        System.out.println("Bronze   : " + control.countWaitingByTier(LoyaltyTier.BRONZE));
    }

    private void printReportHeader(String title) {
        printLine();
        System.out.println(centerText(title, 86));
        printLine();
    }

    private void printLine() {
        System.out.println("--------------------------------------------------------------------------------------");
    }

    private String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        String result = "";
        for (int i = 0; i < padding; i++) {
            result += " ";
        }
        return result + text;
    }

    private VipAllocationRequest[] filterAndSortRequestReport(VipAllocationRequest[] source, boolean allocatedReport) {
        LoyaltyTier tierFilter = readTierFilter();
        RoomType roomTypeFilter = readRoomTypeFilter();
        String keyword = readString("Search VIP Request ID / Guest Name / Room No (press Enter for all): ");
        int sortChoice = readRequestReportSortChoice(allocatedReport);

        int matchCount = 0;
        for (int i = 0; i < source.length; i++) {
            if (matchesRequestReportFilter(source[i], tierFilter, roomTypeFilter, keyword, allocatedReport)) {
                matchCount++;
            }
        }

        VipAllocationRequest[] filtered = new VipAllocationRequest[matchCount];
        int index = 0;
        for (int i = 0; i < source.length; i++) {
            if (matchesRequestReportFilter(source[i], tierFilter, roomTypeFilter, keyword, allocatedReport)) {
                filtered[index++] = source[i];
            }
        }

        sortRequestReport(filtered, sortChoice);
        printRequestReportCriteria(tierFilter, roomTypeFilter, keyword, sortChoice, allocatedReport);
        return filtered;
    }

    private LoyaltyTier readTierFilter() {
        System.out.println("\nSelect Loyalty Tier Filter");
        System.out.println("0. All");
        System.out.println("1. Bronze");
        System.out.println("2. Silver");
        System.out.println("3. Gold");
        System.out.println("4. Platinum");
        while (true) {
            int choice = readInt("Tier filter: ");
            switch (choice) {
                case 0:
                    return null;
                case 1:
                    return LoyaltyTier.BRONZE;
                case 2:
                    return LoyaltyTier.SILVER;
                case 3:
                    return LoyaltyTier.GOLD;
                case 4:
                    return LoyaltyTier.PLATINUM;
                default:
                    System.out.println("Invalid tier filter. Please enter 0 to 4.");
                    break;
            }
        }
    }

    private RoomType readRoomTypeFilter() {
        System.out.println("\nSelect Room Type Filter");
        System.out.println("0. All");
        System.out.println("1. Standard");
        System.out.println("2. Deluxe");
        System.out.println("3. Suite");
        System.out.println("4. Executive");
        System.out.println("5. Presidential");
        while (true) {
            int choice = readInt("Room type filter: ");
            switch (choice) {
                case 0:
                    return null;
                case 1:
                    return RoomType.STANDARD;
                case 2:
                    return RoomType.DELUXE;
                case 3:
                    return RoomType.SUITE;
                case 4:
                    return RoomType.EXECUTIVE;
                case 5:
                    return RoomType.PRESIDENTIAL;
                default:
                    System.out.println("Invalid room type filter. Please enter 0 to 5.");
                    break;
            }
        }
    }

    private int readRequestReportSortChoice(boolean allocatedReport) {
        System.out.println("\nSelect Sorting Method");
        System.out.println("1. VIP Request ID");
        System.out.println("2. Guest Name");
        System.out.println("3. Loyalty Tier Priority");
        System.out.println(allocatedReport ? "4. Allocated Room No" : "4. Preferred Room Type");
        while (true) {
            int choice = readInt("Sort by: ");
            if (choice >= 1 && choice <= 4) {
                return choice;
            }
            System.out.println("Invalid sorting method. Please enter 1 to 4.");
        }
    }

    private boolean matchesRequestReportFilter(VipAllocationRequest request, LoyaltyTier tierFilter,
            RoomType roomTypeFilter, String keyword, boolean allocatedReport) {
        boolean tierMatch = tierFilter == null || request.getGuestProfile().getLoyaltyTier() == tierFilter;
        RoomType reportRoomType = getReportRoomType(request, allocatedReport);
        boolean roomTypeMatch = roomTypeFilter == null || reportRoomType == roomTypeFilter;
        boolean keywordMatch = keyword == null || keyword.trim().isEmpty()
                || request.getGuestProfile().getConfirmationNumber().equalsIgnoreCase(keyword.trim())
                || request.getGuestProfile().getGuestName().toLowerCase().contains(keyword.trim().toLowerCase())
                || (request.getAllocatedRoom() != null
                        && request.getAllocatedRoom().getRoomNo().equalsIgnoreCase(keyword.trim()));
        return tierMatch && roomTypeMatch && keywordMatch;
    }

    private RoomType getReportRoomType(VipAllocationRequest request, boolean allocatedReport) {
        if (allocatedReport && request.getAllocatedRoom() != null) {
            return request.getAllocatedRoom().getRoomType();
        }
        return request.getPreferredRoomType();
    }

    private void sortRequestReport(VipAllocationRequest[] report, int sortChoice) {
        for (int i = 0; i < report.length - 1; i++) {
            int selectedIndex = i;
            for (int j = i + 1; j < report.length; j++) {
                if (compareRequestReport(report[j], report[selectedIndex], sortChoice) < 0) {
                    selectedIndex = j;
                }
            }
            VipAllocationRequest temp = report[i];
            report[i] = report[selectedIndex];
            report[selectedIndex] = temp;
        }
    }

    private int compareRequestReport(VipAllocationRequest first, VipAllocationRequest second, int sortChoice) {
        switch (sortChoice) {
            case 2:
                return first.getGuestProfile().getGuestName()
                        .compareToIgnoreCase(second.getGuestProfile().getGuestName());
            case 3:
                return second.getGuestProfile().getLoyaltyTier().getPriority()
                        - first.getGuestProfile().getLoyaltyTier().getPriority();
            case 4:
                return compareRequestRoom(first, second);
            case 1:
            default:
                return first.getGuestProfile().getConfirmationNumber()
                        .compareToIgnoreCase(second.getGuestProfile().getConfirmationNumber());
        }
    }

    private int compareRequestRoom(VipAllocationRequest first, VipAllocationRequest second) {
        String firstRoom = first.getAllocatedRoom() == null
                ? String.valueOf(getRoomTypeRank(first.getPreferredRoomType()))
                : first.getAllocatedRoom().getRoomNo();
        String secondRoom = second.getAllocatedRoom() == null
                ? String.valueOf(getRoomTypeRank(second.getPreferredRoomType()))
                : second.getAllocatedRoom().getRoomNo();
        return firstRoom.compareToIgnoreCase(secondRoom);
    }

    private void printRequestReportCriteria(LoyaltyTier tierFilter, RoomType roomTypeFilter, String keyword,
            int sortChoice, boolean allocatedReport) {
        System.out.println("\nReport Criteria");
        System.out.println("Tier Filter     : " + (tierFilter == null ? "All" : tierFilter));
        System.out.println("Room Type Filter: " + (roomTypeFilter == null ? "All" : roomTypeFilter));
        System.out.println("Search Keyword  : " + (keyword == null || keyword.trim().isEmpty() ? "All" : keyword));
        System.out.println("Sorted By       : " + getRequestSortName(sortChoice, allocatedReport));
        System.out.println();
    }

    private String getRequestSortName(int sortChoice, boolean allocatedReport) {
        switch (sortChoice) {
            case 2:
                return "Guest Name";
            case 3:
                return "Loyalty Tier Priority";
            case 4:
                return allocatedReport ? "Allocated Room No" : "Preferred Room Type";
            case 1:
            default:
                return "VIP Request ID";
        }
    }

    private RoomType readRoomType(LoyaltyTier tier) {
        System.out.println("Eligible room type options");
        System.out.println("1 Standard");
        if (isEligibleRoomType(tier, RoomType.DELUXE)) {
            System.out.println("2 Deluxe");
        }
        if (isEligibleRoomType(tier, RoomType.SUITE)) {
            System.out.println("3 Suite");
        }
        if (isEligibleRoomType(tier, RoomType.EXECUTIVE)) {
            System.out.println("4 Executive");
        }
        if (isEligibleRoomType(tier, RoomType.PRESIDENTIAL)) {
            System.out.println("5 Presidential");
        }
        while (true) {
            int choice = readInt("Select room type: ");
            switch (choice) {
                case 5:
                    if (isEligibleRoomType(tier, RoomType.PRESIDENTIAL)) {
                        return RoomType.PRESIDENTIAL;
                    }
                    break;
                case 4:
                    if (isEligibleRoomType(tier, RoomType.EXECUTIVE)) {
                        return RoomType.EXECUTIVE;
                    }
                    break;
                case 3:
                    if (isEligibleRoomType(tier, RoomType.SUITE)) {
                        return RoomType.SUITE;
                    }
                    break;
                case 2:
                    if (isEligibleRoomType(tier, RoomType.DELUXE)) {
                        return RoomType.DELUXE;
                    }
                    break;
                case 1:
                    return RoomType.STANDARD;
                default:
                    break;
            }
            System.out.println("Invalid room type for " + tier
                    + ". Please select up to " + tier.getHighestEligibleRoomType() + ".");
        }
    }

    private boolean isEligibleRoomType(LoyaltyTier tier, RoomType roomType) {
        return getRoomTypeRank(roomType) <= getRoomTypeRank(tier.getHighestEligibleRoomType());
    }

    private int getRoomTypeRank(RoomType roomType) {
        switch (roomType) {
            case PRESIDENTIAL:
                return 5;
            case EXECUTIVE:
                return 4;
            case SUITE:
                return 3;
            case DELUXE:
                return 2;
            case STANDARD:
            default:
                return 1;
        }
    }

    private void printTierThresholds() {
        System.out.println("Tier is calculated from historical loyalty points.");
        System.out.println("Bronze   : 0 - 999");
        System.out.println("Silver   : 1000 - 2499");
        System.out.println("Gold     : 2500 - 4999");
        System.out.println("Platinum : 5000 and above");
        System.out.println("Current stay loyalty points are earned after checkout, not before this allocation.");
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numbers only.");
            }
        }
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private String readRequiredString(String prompt) {
        while (true) {
            String value = readString(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    private String readPhoneNumber() {
        while (true) {
            String phoneNumber = readRequiredString("Phone number: ");
            if (phoneNumber.matches("\\d{10,12}")) {
                return phoneNumber;
            }
            System.out.println("Phone number must contain 10 to 12 digits.");
        }
    }

    private String readIcPassportNo() {
        while (true) {
            String icPassportNo = readRequiredString("IC / Passport No: ");
            if (icPassportNo.length() >= 8 && icPassportNo.length() <= 12) {
                return icPassportNo;
            }
            System.out.println("IC / Passport No. must be 8-12 characters.");
        }
    }

    private String readConfirmationNumber() {
        while (true) {
            String confirmationNumber = readString("VIP Request ID (e.g. VIP001): ").toUpperCase();
            if (!confirmationNumber.matches("VIP\\d{3}")) {
                System.out.println("VIP Request ID must follow the format VIP001.");
                continue;
            }
            if (control.isConfirmationNumberUsed(confirmationNumber)) {
                System.out.println("VIP Request ID already exists.");
                continue;
            }
            return confirmationNumber;
        }
    }

    private int readNonNegativeInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println("Value cannot be negative.");
        }
    }

    private void seedVipRequests() {
        control.registerLoyaltyTierMember("Tan Mei Ling", "A12345678", "0123456789", 4300);
        control.registerLoyaltyTierMember("Jason Lim", "B23456789", "0134567891", 5200);
        control.registerLoyaltyTierMember("Nur Aisyah", "C34567890", "0145678912", 2400);
        control.addVipRequest("VIP001", "M001", RoomType.SUITE);
        control.addVipRequest("VIP002", "M002", RoomType.SUITE);
        control.addVipRequest("VIP003", "M003", RoomType.DELUXE);
    }
}
