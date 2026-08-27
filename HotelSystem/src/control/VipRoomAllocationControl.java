// Author: Lim Jia Zheng
package control;

import adt.ArrayList;
import adt.VipPriorityQueue;
import dao.RoomDAO;
import entity.GuestProfile;
import entity.LoyaltyTier;
import entity.Room;
import entity.RoomType;
import entity.VipAllocationRequest;

public class VipRoomAllocationControl {
    private static final int MAX_ALLOCATED = 50;

    private final VipPriorityQueue waitingQueue;
    private final RoomDAO roomDAO;
    private final VipAllocationRequest[] allocatedRequests;
    private int allocatedCount;
    private long nextArrivalSequence;

    public VipRoomAllocationControl() {
        this(new RoomDAO());
    }

    public VipRoomAllocationControl(RoomDAO roomDAO) {
        waitingQueue = new VipPriorityQueue();
        this.roomDAO = roomDAO;
        allocatedRequests = new VipAllocationRequest[MAX_ALLOCATED];
        allocatedCount = 0;
        nextArrivalSequence = 1;
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
        if (allocatedCount == allocatedRequests.length) {
            return null;
        }

        request = waitingQueue.removeMax();
        availableRoom.setOccupancyStatus("Occupied");
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

    public VipAllocationRequest searchWaitingRequest(String confirmationNumber) {
        VipAllocationRequest[] waitingReport = waitingQueue.toPriorityOrderArray();
        for (int i = 0; i < waitingReport.length; i++) {
            if (waitingReport[i].getGuestProfile().getConfirmationNumber().equals(confirmationNumber)) {
                return waitingReport[i];
            }
        }
        return null;
    }

    public VipAllocationRequest searchAllocatedRequest(String confirmationNumber) {
        for (int i = 0; i < allocatedCount; i++) {
            if (allocatedRequests[i].getGuestProfile().getConfirmationNumber().equals(confirmationNumber)) {
                return allocatedRequests[i];
            }
        }
        return null;
    }

    public boolean isConfirmationNumberUsed(String confirmationNumber) {
        return searchWaitingRequest(confirmationNumber) != null
                || searchAllocatedRequest(confirmationNumber) != null;
    }

    public boolean cancelWaitingRequest(String confirmationNumber) {
        VipAllocationRequest[] waitingReport = waitingQueue.toPriorityOrderArray();
        boolean found = false;
        waitingQueue.clear();

        for (int i = 0; i < waitingReport.length; i++) {
            if (waitingReport[i].getGuestProfile().getConfirmationNumber().equals(confirmationNumber)) {
                found = true;
            } else {
                waitingQueue.add(waitingReport[i]);
            }
        }
        return found;
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
        ArrayList<Room> rooms = roomDAO.getRooms();
        int readyCount = 0;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).isBookable()) {
                readyCount++;
            }
        }

        Room[] readyRooms = new Room[readyCount];
        int index = 0;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).isBookable()) {
                readyRooms[index++] = rooms.get(i);
            }
        }
        sortRoomsByTypeThenNumber(readyRooms);
        return readyRooms;
    }

    public int getWaitingCount() {
        return waitingQueue.getNumberOfEntries();
    }

    public int getAllocatedCount() {
        return allocatedCount;
    }

    public int countWaitingByTier(LoyaltyTier tier) {
        int count = 0;
        VipAllocationRequest[] waitingReport = waitingQueue.toPriorityOrderArray();
        for (int i = 0; i < waitingReport.length; i++) {
            if (waitingReport[i].getGuestProfile().getLoyaltyTier() == tier) {
                count++;
            }
        }
        return count;
    }

    private Room findReadyRoom(RoomType preferredRoomType) {
        ArrayList<Room> rooms = roomDAO.getRooms();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.isBookable() && room.getRoomType() == preferredRoomType) {
                return room;
            }
        }
        return null;
    }

    private Room findAnyReadyRoom() {
        ArrayList<Room> rooms = roomDAO.getRooms();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.isBookable()) {
                return room;
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
        return existing.getAllocatedRoom().getRoomNo().compareTo(current.getAllocatedRoom().getRoomNo()) > 0;
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
        return first.getRoomNo().compareTo(second.getRoomNo());
    }
}
