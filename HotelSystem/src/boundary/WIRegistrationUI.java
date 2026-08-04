package boundary;

import adt.CircularArrayQueue;
import control.WIRegistrationControl;
import java.util.Scanner;
import entity.*;

public class WIRegistrationUI {

    private CircularArrayQueue<Guest> GQueue;
    private Scanner input;


    public WIRegistrationControl() {

        GQueue = new CircularArrayQueue<>();
        input = new Scanner(System.in);

    }


    public void start() {

        int choice;

        do {

            System.out.println("\n===== Walk-In Registration =====");
            System.out.println("1. Register New Customer");
            System.out.println("2. Room Available");
            System.out.println("3. Check In & Check Out");
            System.out.println("0. Back to Main Menu");

            System.out.print("Enter your choice: ");
            choice = input.nextInt();


            switch(choice)
            {
                case 1:
                    WIRegistrationControl WIRControl = new WIRegistrationControl();
                    break;


                case 2:
                    //System.out.println("Process Customer");
                    break;


                case 3:
                    System.out.println("View Queue");
                    break;


                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;


                default:
                    System.out.println("Invalid choice");
                    break;
            }


        } while(choice != 0);

    }
}