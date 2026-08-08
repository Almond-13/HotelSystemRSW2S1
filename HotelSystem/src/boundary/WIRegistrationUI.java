package boundary;

import java.util.Scanner;
import control.WIRegistrationControl;
import dao.RoomDAO;

public class WIRegistrationUI {

    private Scanner input;
    private WIRegistrationControl control;

    public WIRegistrationUI(RoomDAO roomDAO) {
        input = new Scanner(System.in);
        control = new WIRegistrationControl(roomDAO);
}

    public void ShowMenu() {

        int choice;

        do {

            System.out.println("\n========== Walk-In Registration ==========");
            System.out.println("1. Register Customer");
            System.out.println("2. Check In");
            System.out.println("3. Check Out");
            System.out.println("0. Back to Main Menu");

            System.out.print("\nEnter your choice: ");
            choice = input.nextInt();

            switch (choice) {

                case 1:
                    control.RGuest();
                    break;

                case 2:
                    control.CheckIn();
                    break;

                case 3:
                    control.CheckOut();
                    break;

                case 4:
                    //control.View Summary();
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