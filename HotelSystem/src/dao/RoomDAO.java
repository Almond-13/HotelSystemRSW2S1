package dao;

import entity.Room;
import entity.RoomType;
import adt.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RoomDAO {

    private ArrayList<Room> rooms;

    private static final String[] ROOM_DATA_PATHS = {
            "roomData.txt",
            "HotelSystem" + File.separator + "roomData.txt"
    };

    public RoomDAO() {

        rooms = new ArrayList<>();

        loadRooms();
    }

    private void loadRooms() {

        File dataFile = findRoomDataFile();
        if (dataFile == null) {
            System.out.println("Error reading roomData.txt: file not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|");

                String roomNo = data[0].trim();
                RoomType roomType = RoomType.fromDisplayName(data[1].trim());

                rooms.add(new Room(roomNo, roomType));
            }

        } catch (IOException | IllegalArgumentException e) {

            System.out.println("Error reading roomData.txt: " + e.getMessage());
        }
    }

    private File findRoomDataFile() {
        for (int i = 0; i < ROOM_DATA_PATHS.length; i++) {
            File file = new File(ROOM_DATA_PATHS[i]);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    public ArrayList<Room> getRooms() {

        return rooms;
    }
}
