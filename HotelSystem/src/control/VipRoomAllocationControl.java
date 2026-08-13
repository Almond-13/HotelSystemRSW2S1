// Author: Lim Jia Zheng
package control;

import adt.VipPriorityQueue;
import entity.GuestProfile;
import entity.LoyaltyTier;
import entity.Room;
import entity.RoomStatus;
import entity.RoomType;
import entity.VipAllocationRequest;

public class VipRoomAllocationControl {
    private static final int MAX_ROOMS = 30;
    private static final int MAX_ALLOCATED = 50;

    private final VipPriorityQueue waitingQueue;
    private final Room[] rooms;
    private final VipAllocationRequest[] allocatedRequests;
    private int roomCount;
    private int allocatedCount;
    private long nextArrivalSequence;

    public VipRoomAllocationControl() {
        waitingQueue = new VipPriorityQueue();
        rooms = new Room[MAX_ROOMS];
        allocatedRequests = new VipAllocationRequest[MAX_ALLOCATED];
        roomCount = 0;
        allocatedCount = 0;
        nextArrivalSequence = 1;
        seedRooms();
    }

    public VipAllocationRequest addVipRequest(String confirmationNumber, String guestName,
            LoyaltyTier tier, int rewardPoints, RoomType preferredRoomType) {
        GuestProfile guestProfile = new GuestProfile(confirmationNumber, guestName, tier, rewardPoints);
        VipAllocationRequest request = new VipAllocationRequest(guestProfile, preferredRoomType, nextArrivalSequence++);
        waitingQueue.add(request);
        return request;
    }

    public VipAllocationRequest allocateNextVipRoom() {
        VipAllocationRequest request = waitingQueue.peekMax();
        if (request == null) {
            return null;
        }

        Room availableRoom = findReadyRoom(request.getPreferredRoomType());
        if (availableRoom == null) {
            availableRoom = findAnyReadyRoom();
        }
        if (availableRoom == null) {
            return null;
        }

        request = waitingQueue.removeMax();
        availableRoom.setRoomStatus(RoomStatus.OCCUPIED);
        request.setAllocatedRoom(availableRoom);
        allocatedRequests[allocatedCount++] = request;
        return request;
    }

    public VipAllocationRequest peekNextVip() {
        return waitingQueue.peekMax();
    }

    public VipAllocationRequest[] getWaitingReport() {
        return waitingQueue.toPriorityOrderArray();
    }

    public VipAllocationRequest[] getAllocatedReport() {
        VipAllocationRequest[] report = new VipAllocationRequest[allocatedCount];
        for (int i = 0; i < allocatedCount; i++) {
            report[i] = allocatedRequests[i];
        }
        sortAllocatedByTierThenRoom(report);
        return report;
    }

    public Room[] getReadyRoomsReport() {
        int readyCount = 0;
        for (int i = 0; i < roomCount; i++) {
            if (rooms[i].getRoomStatus() == RoomStatus.READY) {
                readyCount++;
            }
        }

        Room[] readyRooms = new Room[readyCount];
        int index = 0;
        for (int i = 0; i < roomCount; i++) {
            if (rooms[i].getRoomStatus() == RoomStatus.READY) {
                readyRooms[index++] = rooms[i];
            }
        }
        sortRoomsByTypeThenNumber(readyRooms);
        return readyRooms;
    }

    public int getWaitingCount() {
        return waitingQueue.getNumberOfEntries();
    }

    private void seedRooms() {
        addRoom(new Room("V101", RoomType.DELUXE, RoomStatus.READY));
        addRoom(new Room("V102", RoomType.DELUXE, RoomStatus.READY));
        addRoom(new Room("V201", RoomType.SUITE, RoomStatus.READY));
        addRoom(new Room("V202", RoomType.SUITE, RoomStatus.CLEANING));
        addRoom(new Room("V301", RoomType.EXECUTIVE, RoomStatus.READY));
        addRoom(new Room("V401", RoomType.PRESIDENTIAL, RoomStatus.READY));
    }

    private void addRoom(Room room) {
        rooms[roomCount++] = room;
    }

    private Room findReadyRoom(RoomType preferredRoomType) {
        for (int i = 0; i < roomCount; i++) {
            if (rooms[i].getRoomStatus() == RoomStatus.READY && rooms[i].getRoomType() == preferredRoomType) {
                return rooms[i];
            }
        }
        return null;
    }

    private Room findAnyReadyRoom() {
        for (int i = 0; i < roomCount; i++) {
            if (rooms[i].getRoomStatus() == RoomStatus.READY) {
                return rooms[i];
            }
        }
        return null;
    }

    private void sortAllocatedByTierThenRoom(VipAllocationRequest[] report) {
        for (int i = 1; i < report.length; i++) {
            VipAllocationRequest current = report[i];
            int j = i - 1;
            while (j >= 0 && shouldMoveAllocatedRight(report[j], current)) {
                report[j + 1] = report[j];
                j--;
            }
            report[j + 1] = current;
        }
    }

    private boolean shouldMoveAllocatedRight(VipAllocationRequest existing, VipAllocationRequest current) {
        int existingTier = existing.getGuestProfile().getLoyaltyTier().getPriority();
        int currentTier = current.getGuestProfile().getLoyaltyTier().getPriority();
        if (existingTier != currentTier) {
            return existingTier < currentTier;
        }
        return existing.getAllocatedRoom().getRoomNumber().compareTo(current.getAllocatedRoom().getRoomNumber()) > 0;
    }

    private void sortRoomsByTypeThenNumber(Room[] report) {
        for (int i = 1; i < report.length; i++) {
            Room current = report[i];
            int j = i - 1;
            while (j >= 0 && compareRoom(report[j], current) > 0) {
                report[j + 1] = report[j];
                j--;
            }
            report[j + 1] = current;
        }
    }

    private int compareRoom(Room first, Room second) {
        int typeCompare = first.getRoomType().compareTo(second.getRoomType());
        if (typeCompare != 0) {
            return typeCompare;
        }
        return first.getRoomNumber().compareTo(second.getRoomNumber());
    }
}
