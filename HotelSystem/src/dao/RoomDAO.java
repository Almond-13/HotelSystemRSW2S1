package dao;

import entity.Room;
import adt.ArrayList;

public class RoomDAO {

    private ArrayList<Room> rooms;

    public RoomDAO(){

        rooms = new ArrayList<>();

        RoomData();
    }


    private void RoomData(){

        rooms.add(new Room("101", "Standard"));
        rooms.add(new Room("102", "Standard"));
        rooms.add(new Room("201", "Deluxe"));
        rooms.add(new Room("301", "Suite"));
        rooms.add(new Room("302", "Suite"));
        rooms.add(new Room("303", "Suite"));
        rooms.add(new Room("304", "Suite"));
        
    }


    public ArrayList<Room> getRooms(){

        return rooms;
    }
}