package boundary;

import control.VipRoomAllocationControl;
import dao.RoomDAO;
import entity.LoyaltyTier;
import entity.Room;
import entity.RoomType;
import entity.VipAllocationRequest;
import java.util.Scanner;

public class VIPUI {
    private Scanner input;
    private VipRoomAllocationControl control;

    public VIPUI(Scanner input, RoomDAO roomDAO) {
        this.input = input;
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
                    addVipRequest();
                    break;
                case 2:
                    allocateNextVip();
                    break;
                case 3:
                    displayNextVip();
                    break;
                case 4:
                    searchVipRequest();
                    break;
                case 5:
                    cancelVipRequest();
                    break;
                case 6:
                    displayWaitingReport();
                    break;
                case 7:
                    displayAllocatedReport();
                    break;
                case 8:
                    displayReadyRoomsReport();
                    break;
                case 9:
                    displayAllocationSummaryReport();
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
        System.out.println("1. Add VIP allocation request");
        System.out.println("2. Allocate room to highest priority VIP");
        System.out.println("3. View next VIP in queue");
        System.out.println("4. Search VIP request");
        System.out.println("5. Cancel waiting VIP request");
        System.out.println("6. Report: VIP waiting queue by priority");
        System.out.println("7. Report: Allocated VIP rooms");
        System.out.println("8. Report: Ready rooms");
        System.out.println("9. Report: VIP allocation summary");
        System.out.println("0. Back to Main Menu");
    }

    private void addVipRequest() {
        String confirmationNumber = readConfirmationNumber();
        String guestName = readRequiredString("Guest name: ");
        printTierThresholds();
        int points = readNonNegativeInt("Historical reward points: ");
        LoyaltyTier tier = LoyaltyTier.fromRewardPoints(points);
        System.out.println("Auto-assigned loyalty tier: " + tier);
        System.out.println("Highest eligible room type: " + tier.getHighestEligibleRoomType());
        RoomType roomType = readRoomType(tier);

        VipAllocationRequest request = control.addVipRequest(
                confirmationNumber, guestName, points, roomType);
        System.out.println("Added: " + request);
    }

    private void allocateNextVip() {
        VipAllocationRequest allocated = control.allocateNextVipRoom();
        if (allocated == null) {
            System.out.println("No waiting VIP or no available clean room.");
        } else {
            System.out.println("Allocated: " + allocated);
        }
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
        String confirmationNumber = readString("Confirmation number to search: ");
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

        System.out.println("No VIP request found for confirmation number " + confirmationNumber + ".");
    }

    private void cancelVipRequest() {
        String confirmationNumber = readString("Confirmation number to cancel: ");
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
        System.out.println("\nVIP Waiting Queue Report");
        VipAllocationRequest[] report = control.getWaitingReport();
        if (report.length == 0) {
            System.out.println("No waiting VIP requests.");
            return;
        }
        for (int i = 0; i < report.length; i++) {
            System.out.println((i + 1) + ". " + report[i]);
        }
    }

    private void displayAllocatedReport() {
        System.out.println("\nAllocated VIP Rooms Report");
        VipAllocationRequest[] report = control.getAllocatedReport();
        if (report.length == 0) {
            System.out.println("No VIP rooms allocated yet.");
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
        System.out.println("Tier is calculated from historical reward points.");
        System.out.println("Bronze   : 0 - 999");
        System.out.println("Silver   : 1000 - 2499");
        System.out.println("Gold     : 2500 - 4999");
        System.out.println("Platinum : 5000 and above");
        System.out.println("Current stay points are earned after checkout, not before this allocation.");
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

    private String readConfirmationNumber() {
        while (true) {
            String confirmationNumber = readString("Confirmation number (8 digits): ");
            if (!confirmationNumber.matches("\\d{8}")) {
                System.out.println("Confirmation number must be exactly 8 digits.");
                continue;
            }
            if (control.isConfirmationNumberUsed(confirmationNumber)) {
                System.out.println("Confirmation number already exists.");
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
        control.addVipRequest("10000001", "Tan Mei Ling", 4300, RoomType.SUITE);
        control.addVipRequest("10000002", "Jason Lim", 5200, RoomType.SUITE);
        control.addVipRequest("10000003", "Nur Aisyah", 2400, RoomType.DELUXE);
    }
}
