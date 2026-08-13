package boundary;

import java.util.Scanner;
import control.WIRegistrationControl;
import control.HousekeepingManager;
import dao.RoomDAO;

public class WIRegistrationUI {

    private Scanner input;
    private WIRegistrationControl control;

    public WIRegistrationUI(RoomDAO roomDAO, HousekeepingManager hkManager) {
    input = new Scanner(System.in);
    control = new WIRegistrationControl(roomDAO, hkManager);
    }

    public void ShowMenu() {
        int choice;

        do {
            System.out.println("\n========== Walk-In Registration ==========");
            System.out.println("1. Register Customer");
            System.out.println("2. Update Customer Information");
            System.out.println("3. Check In");
            System.out.println("4. Check Out");
            System.out.println("5. Summary Report");
            System.out.println("0. Back to Main Menu");

            while (true) {
                System.out.print("\nEnter your choice: ");
                String choiceInput = input.nextLine().trim();
                try {
                    choice = Integer.parseInt(choiceInput);
                    if (choice >= 0 && choice <= 5) {
                        break;
                    }
                    System.out.println("Invalid choice. Please enter 0, 1, 2, 3, 4 or 5.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter numbers only.");
                }
            }

            switch (choice) {

                case 1:
                    control.RGuest();
                    break;

                case 2:
                    control.UpdateGuestInfo();
                    break;

                case 3:
                    control.CheckIn();
                    break;

                case 4:
                    control.CheckOut();
                    break;

                case 5:
                    control.SReport();
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
}