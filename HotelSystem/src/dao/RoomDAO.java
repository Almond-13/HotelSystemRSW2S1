package dao;

import entity.Room;
import adt.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RoomDAO {

    private ArrayList<Room> rooms;

    private static final String roomData = "roomData.txt";

    public RoomDAO() {

        rooms = new ArrayList<>();

        loadRooms();
    }

    private void loadRooms() {

        try (BufferedReader br = new BufferedReader(new FileReader(roomData))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|");

                String roomNo = data[0].trim();
                String roomType = data[1].trim();

                rooms.add(new Room(roomNo, roomType));
            }

        } catch (IOException e) {

            System.out.println("Error reading Room.txt: " + e.getMessage());
        }
    }

    public ArrayList<Room> getRooms() {

        return rooms;
    }
}