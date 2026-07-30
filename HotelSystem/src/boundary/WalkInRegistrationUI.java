package boundary;

import control.WIRegistrationControl;
import entity.Booking;
import entity.Guest;
import adt.ArrayQueue;
import adt.QueueInterface;
import java.util.Scanner;

public class WalkInRegistrationUI {

    private Scanner scanner = new Scanner(System.in);
    private WIRegistrationControl control = new WIRegistrationControl();

    public void displayMenu() {

        int choice;

        do {

            System.out.println("\n====================================");
            System.out.println("   WALK-IN REGISTRATION MODULE");
            System.out.println("====================================");
            System.out.println("1. Register Walk-In Guest");
            System.out.println("2. Process Next Guest");
            System.out.println("3. View Next Guest");
            System.out.println("4. Display Waiting Queue");
            System.out.println("5. Exit");
            System.out.print("Enter your choice : ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    registerGuest();
                    break;

                case 2:
                    //processGuest();
                    break;

                case 3:
                    //viewNextGuest();
                    break;

                case 4:
                    //displayQueue();
                    break;

                case 5:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 5);

    }

    private void registerGuest() {

        System.out.println("\n----- Walk-In Registration -----");

        System.out.print("Guest ID : ");
        String GuestID = scanner.nextLine();

        System.out.print("Guest Name : ");
        String Name = scanner.nextLine();

        System.out.print("Phone Number : ");
        String PhoneNumber = scanner.nextLine();

        System.out.print("Room Type : ");
        String roomType = scanner.nextLine();

        System.out.print("Number of Guests : ");
        int pax = scanner.nextInt();
        scanner.nextLine();


    }

}