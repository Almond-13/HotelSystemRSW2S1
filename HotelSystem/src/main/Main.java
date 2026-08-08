package main;
import java.util.Scanner;

import boundary.HousekeepingUI;
import boundary.WIRegistrationUI;
import dao.RoomDAO;

public class Main {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        RoomDAO roomDAO = new RoomDAO();
        WIRegistrationUI WIRUI = new WIRegistrationUI(roomDAO);
        HousekeepingUI HKUI = new HousekeepingUI(input, roomDAO);

        int choice;
    do {
        System.out.println("\n============ Mimi Hotel ============");
        System.out.println("1. Walk-In Registration");
        System.out.println("2. VIP Room Allocation");
        System.out.println("3. Housekeeping");
        System.out.println("4. Loyalty and Rewards");
        System.out.println("0. Exit");

        System.out.print("\n Enter Your Choice (0 for Exit): ");
        choice = Integer.parseInt(input.nextLine().trim());


        switch(choice)
        {
            case 1:
                WIRUI.ShowMenu();
                break;
            case 2:
                //VIP
            case 3:
                HKUI.run();
                break;
            case 4:
                //VIP
            case 0:
                System.out.println("Exit System...");
                break;
            default:
                System.out.println("Invalid choice, please try again.");
                break;
        }

    } while(choice != 0);
    input.close();
    }
}




