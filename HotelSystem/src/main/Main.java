package main;

import java.util.Scanner;

import boundary.HousekeepingUI;
import boundary.LoyaltyRewardsUI;
import boundary.VIPUI;
import boundary.WIRegistrationUI;
import control.HousekeepingManager;
import dao.RoomDAO;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        RoomDAO roomDAO = new RoomDAO();
        HousekeepingManager hkManager = new HousekeepingManager(roomDAO.getRooms());
        WIRegistrationUI WIRUI = new WIRegistrationUI(roomDAO, hkManager);
        VIPUI VIPUI = new VIPUI(input, roomDAO);
        HousekeepingUI HKUI = new HousekeepingUI(input, hkManager);

        int choice;
        do {
            System.out.println("\n============ Mimi Hotel ============");
            System.out.println("1. Walk-In Registration");
            System.out.println("2. VIP Room Allocation");
            System.out.println("3. Housekeeping");
            System.out.println("4. Loyalty and Rewards");
            System.out.println("0. Exit");

            // Validation of Main Menu Choice
            while (true) {
                System.out.print("\nEnter Your Choice (0 for Exit): ");
                String inputChoice = input.nextLine().trim();

                try {
                    choice = Integer.parseInt(inputChoice);
                    if (choice >= 0 && choice <= 4) {
                        break;
                    }
                    System.out.println("Invalid choice. Please enter a number from 0 to 4.");

                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter numbers only.");
                }
            }

            switch (choice) {
                case 1:
                    WIRUI.ShowMenu();
                    break;
                case 2:
                    VIPUI.run();   
                    break;
                case 3:
                    HKUI.run();
                    break;
                case 4:
                    LoyaltyRewardsUI loyaltyUI = new LoyaltyRewardsUI(input);
                    loyaltyUI.showMenu();
                    break;
                case 0:
                    System.out.println("Exit System...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }

        } while (choice != 0);
        input.close();
    }
}
