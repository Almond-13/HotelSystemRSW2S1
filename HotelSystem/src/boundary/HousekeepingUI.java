package boundary;

import control.HousekeepingManager;
import Entity.Room;
import java.util.Scanner;

public class HousekeepingUI {

    private HousekeepingManager manager;
    private Scanner sc;

    public HousekeepingUI() {
        manager = new HousekeepingManager();
        sc = new Scanner(System.in);
        seedSampleData();
    }

    private void seedSampleData() {
        manager.registerRoom(new Room("101", "Standard"));
        manager.registerRoom(new Room("102", "Standard"));
        manager.registerRoom(new Room("201", "Deluxe"));
        manager.registerRoom(new Room("301", "Suite"));
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    public void run() {
        int choice;
        do {
            clearScreen(); // only clears when we're back at the main menu
            printMenu();
            choice = readMenuChoice(0, 3);
            switch (choice) {
                case 1:
                    viewAllFlow();
                    break;
                case 2:
                    System.out.print("\nEnter status to filter: ");
                    String filter = sc.nextLine().trim();
                    System.out.println();
                    manager.generateRoomStatusReport(filter);
                    pauseForUser();
                    break;
                case 3:
                    System.out.println();
                    manager.generateRollbackFrequencyReport();
                    pauseForUser();
                    break;
                case 0:
                    System.out.println("[i] Exiting Housekeeping module.");
                    break;
            }
        } while (choice != 0);
    }

    private void pauseForUser() {
        System.out.print("\nPress ENTER to continue...");
        sc.nextLine();
    }

    private void printMenu() {
        System.out.println("==================================================");
        System.out.println("           HOUSEKEEPING & TASK LOG");
        System.out.println("==================================================");
        System.out.println(" 1. View All Rooms");
        System.out.println(" 2. Report: Room Status Summary (Filtered)");
        System.out.println(" 3. Report: Rollback Frequency");
        System.out.println(" 0. Exit");
        System.out.println("--------------------------------------------------");
    }

    // ---------- View All + nested actions ----------

    private void viewAllFlow() {
    int subChoice;
    do {
        System.out.println();
        manager.generateRoomStatusReport("ALL");
        System.out.println();
        System.out.printf("%-30s %s%n", "[1] Update Room Status", "[2] Rollback Room Status");
        System.out.printf("%-30s %s%n", "[3] View Room Status History", "[4] Back To Main Menu");
        subChoice = readMenuChoice(1, 4);
        System.out.println();

        switch (subChoice) {
            case 1:
                updateStatusFlow();
                pauseForUser();
                break;
            case 2:
                rollbackFlow();
                pauseForUser();
                break;
            case 3:
                historyFlow();
                pauseForUser();
                break;
            case 4:
                break;
        }
    } while (subChoice != 4);
}

    // ---------- flows ----------

    private void updateStatusFlow() {
        System.out.println("=== UPDATE ROOM STATUS ===\n");
        String roomNo = readExistingRoomNo("Enter room number (e.g. 101): ");

        while (true) {
            String currentStatus = manager.getCurrentStatus(roomNo);
            String nextStatus = manager.getNextStatus(roomNo);

            System.out.println();
            if (nextStatus == null) {
                System.out.println("Room " + roomNo + " is at '" + currentStatus + "' - the final stage.");
                System.out.println();
                System.out.println(" 1. Start new cleaning cycle (guest checked out)");
                System.out.println(" 0. Cancel");
                int choice = readMenuChoice(0, 1);
                if (choice == 0) {
                    return;
                }
                String staffId = readNonEmptyString("\nEnter staff ID (e.g. S001): ");
                boolean started = manager.startNewCycle(roomNo, staffId);
                System.out.println();
                if (started) {
                    System.out.println("[OK] Room " + roomNo + " is now 'Dirty' - new cycle started.");
                } else {
                    System.out.println("[!] " + manager.getLastError());
                }
                return;
            }

            System.out.println("Room:            " + roomNo);
            System.out.println("Current status:  " + currentStatus);
            System.out.println("Next status:     " + nextStatus);
            System.out.println();
            System.out.println(" 1. Confirm update");
            System.out.println(" 0. Cancel");
            int confirm = readMenuChoice(0, 1);
            if (confirm == 0) {
                return;
            }

            String staffId = readNonEmptyString("\nEnter staff ID (e.g. S001): ");
            boolean success = manager.updateStatus(roomNo, nextStatus, staffId);

            System.out.println();
            if (success) {
                System.out.println("[OK] Room " + roomNo + " updated to '" + nextStatus + "'.");
                return;
            } else {
                System.out.println("[!] " + manager.getLastError());
                if (!promptRetry()) {
                    return;
                }
            }
        }
    }

    private void rollbackFlow() {
        System.out.println("=== ROLLBACK ROOM STATUS ===\n");
        while (true) {
            String roomNo = readExistingRoomNo("Enter room number to rollback (e.g. 101): ");

            boolean success = manager.rollbackStatus(roomNo);
            System.out.println();
            if (success) {
                System.out.println("[OK] Room " + roomNo + " rolled back successfully.");
                return;
            } else {
                System.out.println("[!] " + manager.getLastError());
                if (!promptRetry()) {
                    return;
                }
            }
        }
    }

    private void historyFlow() {
        System.out.println("=== ROOM STATUS HISTORY ===\n");
        String roomNo = readExistingRoomNo("Enter room number (e.g. 101): ");
        System.out.println();
        boolean success = manager.printHistory(roomNo);
        if (!success) {
            System.out.println("[!] " + manager.getLastError());
        }
    }

    private boolean promptRetry() {
        System.out.println("\n 1. Try again");
        System.out.println(" 0. Back to main menu");
        int choice = readMenuChoice(0, 1);
        return choice == 1;
    }

    // ---------- input helpers ----------

    private int readMenuChoice(int min, int max) {
        while (true) {
            System.out.print("Enter choice: ");
            String line = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("[!] Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input - please enter a number.");
            }
        }
    }

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("[!] Input cannot be empty, please try again.");
                continue;
            }
            return input;
        }
    }

    private String readExistingRoomNo(String prompt) {
        while (true) {
            String roomNo = readNonEmptyString(prompt);
            if (!manager.roomExists(roomNo)) {
                System.out.println("[!] Room '" + roomNo + "' does not exist. Please try again.");
                continue;
            }
            return roomNo;
        }
    }

    public static void main(String[] args) {
        new HousekeepingUI().run();
    }
}