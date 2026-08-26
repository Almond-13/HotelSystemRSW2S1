package dao;

import entity.Room;
import entity.RoomType;
import adt.ArrayList;

public class RoomDAO {

    private ArrayList<Room> rooms;

    public RoomDAO(){

        rooms = new ArrayList<>();

        RoomData();
    }


    private void RoomData(){

        rooms.add(new Room("101", RoomType.STANDARD));
        rooms.add(new Room("102", RoomType.STANDARD));
        rooms.add(new Room("201", RoomType.DELUXE));
        rooms.add(new Room("301", RoomType.SUITE));
        rooms.add(new Room("302", RoomType.SUITE));
        rooms.add(new Room("303", RoomType.SUITE));
        rooms.add(new Room("304", RoomType.SUITE));
        
    }


    public ArrayList<Room> getRooms(){

        return rooms;
    }
}