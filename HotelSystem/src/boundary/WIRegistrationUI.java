package boundary;

import java.util.Scanner;
import control.WIRegistrationControl;
import control.HousekeepingManager;
import dao.RoomDAO;

public class WIRegistrationUI {

    private Scanner input;
    private WIRegistrationControl control;

    public WIRegistrationUI(Scanner input, RoomDAO roomDAO, HousekeepingManager hkManager) {
        this.input = input;
        control = new WIRegistrationControl(input, roomDAO, hkManager);
    }

    public WIRegistrationUI(RoomDAO roomDAO, HousekeepingManager hkManager) {
        this(new Scanner(System.in), roomDAO, hkManager);
    }

    public void ShowMenu() {
        int choice;

        do {
            System.out.println("\n========== Walk-In Registration ==========");
            System.out.println("1. Register Customer");
            System.out.println("2. Update Customer Information");
            System.out.println("3. Delete Customer");
            System.out.println("4. Assign Room");
            System.out.println("5. Check In");
            System.out.println("6. Check Out");
            System.out.println("7. Booking Report");
            System.out.println("8. Room Occupied Report");
            System.out.println("0. Back to Main Menu");

            while (true) {
                System.out.print("\nEnter your choice: ");
                String choiceInput = input.nextLine().trim();
                try {
                    choice = Integer.parseInt(choiceInput);
                    if (choice >= 0 && choice <= 8) {
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
                    control.CancelGuest();
                    break;

                case 4:
                    control.AssignRoom();
                    break;

                case 5:
                    control.CheckIn();
                    break;

                case 6:
                    control.CheckOut();
                    break;

                case 7:
                    control.SReport();
                    break;

                case 8:
                    control.RoomReport();
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