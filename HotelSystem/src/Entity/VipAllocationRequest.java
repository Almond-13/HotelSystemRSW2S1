// Author: Lim Jia Zheng
package entity;

public class VipAllocationRequest {
    private final GuestProfile guestProfile;
    private final RoomType preferredRoomType;
    private final long arrivalSequence;
    private Room allocatedRoom;

    public VipAllocationRequest(GuestProfile guestProfile, RoomType preferredRoomType, long arrivalSequence) {
        this.guestProfile = guestProfile;
        this.preferredRoomType = preferredRoomType;
        this.arrivalSequence = arrivalSequence;
    }

    public GuestProfile getGuestProfile() {
        return guestProfile;
    }

    public RoomType getPreferredRoomType() {
        return preferredRoomType;
    }

    public long getArrivalSequence() {
        return arrivalSequence;
    }

    public Room getAllocatedRoom() {
        return allocatedRoom;
    }

    public void setAllocatedRoom(Room allocatedRoom) {
        this.allocatedRoom = allocatedRoom;
    }

    public boolean hasHigherPriorityThan(VipAllocationRequest other) {
        int thisTier = guestProfile.getLoyaltyTier().getPriority();
        int otherTier = other.guestProfile.getLoyaltyTier().getPriority();
        if (thisTier != otherTier) {
            return thisTier > otherTier;
        }
        if (guestProfile.getRewardPoints() != other.guestProfile.getRewardPoints()) {
            return guestProfile.getRewardPoints() > other.guestProfile.getRewardPoints();
        }
        return arrivalSequence < other.arrivalSequence;
    }

    @Override
    public String toString() {
        String roomText = allocatedRoom == null ? "Waiting" : "Room " + allocatedRoom.getRoomNumber();
        return guestProfile + " | wants " + preferredRoomType + " | " + roomText;
    }
}
