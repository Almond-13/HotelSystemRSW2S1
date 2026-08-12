package dao;

import adt.ArrayList;
import entity.Guest;
import java.io.*;

public class GuestDAO {

    private final String GUEST_FILE = "guests.txt";

    public void saveGuests(ArrayList<Guest> guests) {
        try (PrintWriter w = new PrintWriter(new FileWriter(GUEST_FILE))) {
            for (int i = 0; i < guests.size(); i++) {
                Guest g = guests.get(i);
                w.println(g.getGuestID() + "|" + g.getName() + "|"
                         + g.getICPassportNo() + "|" + g.getPhoneNumber());
            }
        } catch (IOException e) {}
    }

    public void loadGuests(ArrayList<Guest> guests) {
        File f = new File(GUEST_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 4) {
                    guests.add(new Guest(p[0], p[1], p[2], p[3]));
                }
            }
        } catch (Exception e) {}
    }
}