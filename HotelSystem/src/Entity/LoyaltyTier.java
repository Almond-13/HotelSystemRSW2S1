// Author: Lim Jia Zheng
package entity;

public enum LoyaltyTier {
    BRONZE(1, 0, RoomType.STANDARD),
    SILVER(2, 1000, RoomType.DELUXE),
    GOLD(3, 2500, RoomType.SUITE),
    PLATINUM(4, 5000, RoomType.PRESIDENTIAL);

    private final int priority;
    private final int minimumPoints;
    private final RoomType highestEligibleRoomType;

    LoyaltyTier(int priority, int minimumPoints, RoomType highestEligibleRoomType) {
        this.priority = priority;
        this.minimumPoints = minimumPoints;
        this.highestEligibleRoomType = highestEligibleRoomType;
    }

    public int getPriority() {
        return priority;
    }

    public int getMinimumPoints() {
        return minimumPoints;
    }

    public RoomType getHighestEligibleRoomType() {
        return highestEligibleRoomType;
    }

    public static LoyaltyTier fromRewardPoints(int rewardPoints) {
        if (rewardPoints >= PLATINUM.minimumPoints) {
            return PLATINUM;
        }
        if (rewardPoints >= GOLD.minimumPoints) {
            return GOLD;
        }
        if (rewardPoints >= SILVER.minimumPoints) {
            return SILVER;
        }
        return BRONZE;
    }
}
