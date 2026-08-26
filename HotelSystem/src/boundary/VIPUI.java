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
                    displayWaitingReport();
                    break;
                case 5:
                    displayAllocatedReport();
                    break;
                case 6:
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
        System.out.println("1. Add VIP allocation request");
        System.out.println("2. Allocate room to highest priority VIP");
        System.out.println("3. View next VIP in queue");
        System.out.println("4. Report: VIP waiting queue by priority");
        System.out.println("5. Report: Allocated VIP rooms");
        System.out.println("6. Report: Ready rooms");
        System.out.println("0. Back to Main Menu");
    }

    private void addVipRequest() {
        String confirmationNumber = readString("Confirmation number (8 digits): ");
        String guestName = readString("Guest name: ");
        LoyaltyTier tier = readTier();
        int points = readInt("Reward points: ");
        RoomType roomType = readRoomType();

        VipAllocationRequest request = control.addVipRequest(
                confirmationNumber, guestName, tier, points, roomType);
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

    private LoyaltyTier readTier() {
        System.out.println("Tier: 1 Silver, 2 Gold, 3 Platinum, 4 Diamond, 5 Elite");
        int choice = readInt("Select tier: ");
        switch (choice) {
            case 5:
                return LoyaltyTier.ELITE;
            case 4:
                return LoyaltyTier.DIAMOND;
            case 3:
                return LoyaltyTier.PLATINUM;
            case 2:
                return LoyaltyTier.GOLD;
            default:
                return LoyaltyTier.SILVER;
        }
    }

    private RoomType readRoomType() {
        System.out.println("Room type: 1 Deluxe, 2 Suite, 3 Executive, 4 Presidential");
        int choice = readInt("Select room type: ");
        switch (choice) {
            case 4:
                return RoomType.PRESIDENTIAL;
            case 3:
                return RoomType.EXECUTIVE;
            case 2:
                return RoomType.SUITE;
            default:
                return RoomType.DELUXE;
        }
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

    private void seedVipRequests() {
        control.addVipRequest("10000001", "Tan Mei Ling", LoyaltyTier.PLATINUM, 4300, RoomType.SUITE);
        control.addVipRequest("10000002", "Jason Lim", LoyaltyTier.ELITE, 3900, RoomType.SUITE);
        control.addVipRequest("10000003", "Nur Aisyah", LoyaltyTier.DIAMOND, 7800, RoomType.DELUXE);
    }
}
