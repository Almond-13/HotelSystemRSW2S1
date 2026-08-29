package boundary;

import java.time.LocalDate;
import java.util.Scanner;
import adt.ArrayList;
import control.LoyaltyRewardsControl;
import entity.LoyaltyAccount;
import entity.TierLevel;

public class LoyaltyRewardsUI {
    private Scanner input;
    private LoyaltyRewardsControl control;

    public LoyaltyRewardsUI(Scanner input) {
        this.input = input;
        control = new LoyaltyRewardsControl();
    }

    public void showMenu() {
        int choice;

        do {
            System.out.println("\n===== Loyalty & Rewards Menu =====");
            System.out.println("1. Register Member");
            System.out.println("2. Add Points");
            System.out.println("3. Redeem Points");
            System.out.println("4. View Members");
            System.out.println("5. Tier Report");
            System.out.println("6. Expiring Points Report");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");

            String rawChoice = input.nextLine();
            try {
                choice = Integer.parseInt(rawChoice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1;
            }

            switch (choice) {
                case 1:
                    registerMember();
                    break;
                case 2:
                    addPoints();
                    break;
                case 3:
                    redeemPoints();
                    break;
                case 4:
                    viewAllMembers();
                    break;
                case 5:
                    tierReport();
                    break;
                case 6:
                    expiringPointsReport();
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    if (choice != -1) {
                        System.out.println("Invalid menu option.");
                    }
                    break;
            }
        } while (choice != 0);
    }

    private void registerMember() {
        System.out.println("\n===== Register New Member =====");

        System.out.print("Guest ID: ");
        String guestId = input.nextLine().trim();

        System.out.print("Member Name: ");
        String memberName = input.nextLine().trim();

        System.out.print("Phone Number: ");
        String phoneNumber = input.nextLine().trim();

        if (guestId.isEmpty() || memberName.isEmpty() || phoneNumber.isEmpty()) {
            System.out.println("Error: All fields are required. Please try again.");
            return;
        }

        LoyaltyAccount account = new LoyaltyAccount(control.generateMemberId(), guestId, memberName, phoneNumber);
        boolean success = control.addAccount(account);

        if (success) {
            System.out.println("Member registered successfully.");
            System.out.println("Member ID: " + account.getMemberId());
            System.out.println("Current tier: " + account.getTierLevel());
        } else {
            System.out.println("Error: " + control.getLastError());
        }
    }

    private void addPoints() {
        System.out.println("\n===== Add Points =====");
        System.out.print("Member ID: ");
        String memberId = input.nextLine().trim();
        System.out.print("Points to add: ");

        try {
            int points = Integer.parseInt(input.nextLine().trim());
            boolean success = control.addPoints(memberId, points);

            if (success) {
                LoyaltyAccount account = control.findAccountByMemberId(memberId);
                System.out.println("Points added successfully.");
                System.out.println("Lifetime points: " + account.getLifetimePoints());
                System.out.println("Redeemable points: " + account.getRedeemablePoints());
                System.out.println("Updated tier: " + account.getTierLevel());
            } else {
                System.out.println("Error: " + control.getLastError());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Points must be a valid integer.");
        }
    }

    private void redeemPoints() {
        System.out.println("\n===== Redeem Points =====");
        System.out.print("Member ID: ");
        String memberId = input.nextLine().trim();
        System.out.print("Points to redeem: ");

        try {
            int points = Integer.parseInt(input.nextLine().trim());
            boolean success = control.redeemPoints(memberId, points);

            if (success) {
                LoyaltyAccount account = control.findAccountByMemberId(memberId);
                System.out.println("Redemption successful.");
                System.out.println("Lifetime points: " + account.getLifetimePoints());
                System.out.println("Redeemable points: " + account.getRedeemablePoints());
                System.out.println("Updated tier: " + account.getTierLevel());
            } else {
                System.out.println("Error: " + control.getLastError());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Points must be a valid integer.");
        }
    }

    private void viewAllMembers() {
        System.out.println("\n===== All Members =====");
        ArrayList<LoyaltyAccount> members = control.getAllAccounts();

        if (members.isEmpty()) {
            System.out.println("No members found.");
            return;
        }

        for (int i = 0; i < members.size(); i++) {
            LoyaltyAccount account = members.get(i);
            System.out.println("----------------------------------------");
            System.out.println("Member ID: " + account.getMemberId());
            System.out.println("Name: " + account.getMemberName());
            System.out.println("Guest ID: " + account.getGuestId());
            System.out.println("Tier: " + account.getTierLevel());
            System.out.println("Lifetime Points: " + account.getLifetimePoints());
            System.out.println("Redeemable Points: " + account.getRedeemablePoints());
            System.out.println("Phone: " + account.getPhoneNumber());
        }
    }

    private void tierReport() {
        System.out.println("\n===== Tier Report =====");
        System.out.println("Available tiers: BRONZE, SILVER, GOLD, PLATINUM");
        System.out.print("Enter tier: ");
        String tierInput = input.nextLine().trim();

        try {
            TierLevel selectedTier = TierLevel.valueOf(tierInput.toUpperCase());
            System.out.print("Optional search text (press Enter to skip): ");
            String searchText = input.nextLine().trim();

            ArrayList<LoyaltyAccount> report = control.buildTierReport(selectedTier, searchText);
            displayReport(report, "Tier report for " + selectedTier);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid tier entered.");
        }
    }

    private void expiringPointsReport() {
        System.out.println("\n===== Expiring Points Report =====");
        System.out.print("Days threshold: ");

        try {
            int daysThreshold = Integer.parseInt(input.nextLine().trim());
            System.out.print("Filter by tier (or press Enter to skip): ");
            String tierInput = input.nextLine().trim();

            TierLevel selectedTier = null;
            if (!tierInput.isEmpty()) {
                try {
                    selectedTier = TierLevel.valueOf(tierInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid tier, report will ignore tier filter.");
                }
            }

            ArrayList<LoyaltyAccount> report = control.buildExpiringPointsReport(LocalDate.now(), daysThreshold,
                    selectedTier);
            displayReport(report, "Expiring points report");
        } catch (NumberFormatException e) {
            System.out.println("Days threshold must be a valid integer.");
        }
    }

    private void displayReport(ArrayList<LoyaltyAccount> report, String title) {
        System.out.println("\n" + title);

        if (report.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

        for (int i = 0; i < report.size(); i++) {
            LoyaltyAccount account = report.get(i);
            System.out.println("----------------------------------------");
            System.out.println("Rank " + (i + 1));
            System.out.println("Member ID: " + account.getMemberId());
            System.out.println("Name: " + account.getMemberName());
            System.out.println("Tier: " + account.getTierLevel());
            System.out.println("Lifetime Points: " + account.getLifetimePoints());
            System.out.println("Redeemable Points: " + account.getRedeemablePoints());

            // Display expiry date if available (for expiring points report)
            String notificationMessage = account.getNotificationMessage();
            if (notificationMessage != null && !notificationMessage.isEmpty()) {
                System.out.println("Expiry Date: " + account.getPointsExpiryDate());
            }
        }
    }
}
